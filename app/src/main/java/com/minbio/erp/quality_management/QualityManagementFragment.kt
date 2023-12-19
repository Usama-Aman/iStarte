package com.minbio.erp.quality_management

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.adapter.QualityProductListAdapter
import com.minbio.erp.product_management.model.ProductCategoriesModel
import com.minbio.erp.product_management.model.ProductListData
import com.minbio.erp.product_management.model.ProductListModel
import com.minbio.erp.product_management.model.Variety
import com.minbio.erp.quality_management.fragments.QualityComplaint
import com.minbio.erp.quality_management.fragments.QualityIncoming
import com.minbio.erp.quality_management.fragments.QualityTrashed
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import kotlinx.android.synthetic.main.fragment_financial_internal_transfer.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class QualityManagementFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    lateinit var btn_add: TextView

    lateinit var pullToRefresh: SwipeRefreshLayout

    private lateinit var qualityIncoming: RadioButton
    private lateinit var qualityComplaintsToSupplier: RadioButton
    private lateinit var qualityLostTrashed: RadioButton

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    var productList: MutableList<ProductListData?> = ArrayList()
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var fromEdit = false
    private var selectedProductData: ProductListData? = null

    private lateinit var productListRecycler: RecyclerView
    lateinit var productListAdapter: QualityProductListAdapter
    lateinit var et_search: EditText

    private var categoryId: Int = 0
    private var selectedVarietyId: Int = 0
    private var selectedLotId: Int = 0
    private var selectedVarietyName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_quality_management, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.quality_management.split(",")


        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.qualityPageTitle))

        initViews()
        setAdapter()
        initScrollListener()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getCategories()
        } else {
            if (permissionsList.contains(PermissionKeys.view_categories)) {
                getCategories()
                btn_add.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                qualityIncoming.isEnabled = true
            } else {
                pullToRefresh.isEnabled = false
                qualityIncoming.isEnabled = false
                btn_add.visibility = View.GONE
                btn_add.isEnabled = false
            }

            if (permissionsList.contains(PermissionKeys.create_products)) {
                btn_add.visibility = View.VISIBLE
                qualityIncoming.isEnabled = true
            } else {
                btn_add.visibility = View.GONE
                qualityIncoming.isEnabled = false
            }

            qualityComplaintsToSupplier.isEnabled =
                permissionsList.contains(PermissionKeys.create_complaint)
            qualityLostTrashed.isEnabled =
                permissionsList.contains(PermissionKeys.lost_trashed)

        }
    }


    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.pull_to_refresh_quality)
        btn_add = v.findViewById(R.id.btn_add)
        btn_add.setOnClickListener {
            fromEdit = false
            selectedVarietyId = 0
            selectedLotId = 0
            selectedVarietyName = ""
            selectedProductData = null
            qualityIncoming.callOnClick()
        }
        pullToRefresh.setOnRefreshListener { getProducts(0) }
        qualityIncoming = v.findViewById(R.id.qualityIncoming)
        qualityComplaintsToSupplier = v.findViewById(R.id.qualityComplaintsToSupplier)
        qualityLostTrashed = v.findViewById(R.id.qualityLostTrashed)

        qualityIncoming.setOnClickListener { updateRightView(qualityIncoming) }
        qualityComplaintsToSupplier.setOnClickListener { updateRightView(qualityComplaintsToSupplier) }
        qualityLostTrashed.setOnClickListener { updateRightView(qualityLostTrashed) }


        et_search = v.findViewById(R.id.et_quality_search)
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productListAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun setAdapter() {
        productListRecycler = v.findViewById(R.id.qualityRecycler)
        productListAdapter = QualityProductListAdapter(this, productList)
        productListRecycler.layoutManager =
            LinearLayoutManager(context!!)
        productListRecycler.adapter = productListAdapter
    }

    private fun initScrollListener() {
        productListRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            productList.add(null)
            productListAdapter.notifyItemInserted(productList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getProducts(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                productListRecycler.post {
                    productList.removeAt(productList.size - 1)
                    productListAdapter.notifyItemRemoved(productList.size)
                }
            }
            productListAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateRightView(view: RadioButton) {

        qualityIncoming.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        qualityComplaintsToSupplier.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        qualityLostTrashed.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var incoming_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_incoming)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        qualityIncoming.setCompoundDrawables(incoming_drawable, null, null, null)

        var complaint_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_complain)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        qualityComplaintsToSupplier.setCompoundDrawables(complaint_drawable, null, null, null)

        var trashed_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_lost)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        qualityLostTrashed.setCompoundDrawables(trashed_drawable, null, null, null)


        when (view.id) {
            R.id.qualityIncoming -> {
                qualityIncoming.isChecked = true

                qualityIncoming.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                incoming_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_incoming_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                qualityIncoming.setCompoundDrawables(incoming_drawable, null, null, null);

                launchFragment(QualityIncoming())
            }
            R.id.qualityComplaintsToSupplier -> {
                qualityComplaintsToSupplier.isChecked = true

                qualityComplaintsToSupplier.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                complaint_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_complain_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                qualityComplaintsToSupplier.setCompoundDrawables(
                    complaint_drawable,
                    null,
                    null,
                    null
                );

                launchFragment(QualityComplaint())
            }
            R.id.qualityLostTrashed -> {
                qualityLostTrashed.isChecked = true

                qualityLostTrashed.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                trashed_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_lost_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                qualityLostTrashed.setCompoundDrawables(trashed_drawable, null, null, null);

                launchFragment(QualityTrashed())
            }
        }


    }

    private fun getCategories() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductCategories()
        RetrofitClient.apiCall(call, this, "GetProductCategories")
    }

    private fun handleCategoriesResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val categoriesModel =
            gson.fromJson(jsonObject.toString(), ProductCategoriesModel::class.java)

        val radioGroup: RadioGroup = v.findViewById(R.id.categoriesRadioGroup)
        val inflater: LayoutInflater = LayoutInflater.from(context)

        for (i in categoriesModel.data.indices) {
            val radioButton: RadioButton =
                inflater.inflate(R.layout.left_tab_radio_button, null) as RadioButton

            when (i) {
                0 -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_left_button_selector)
                categoriesModel.data.size - 1 -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_right_button_selector)
                else -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_mid_button_selector)
            }

            radioButton.id = categoriesModel.data[i].id
            radioButton.text = categoriesModel.data[i].name
            radioButton.buttonDrawable = null
            radioButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            radioButton.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
            radioGroup.addView(radioButton)
            radioButton.setOnClickListener {
                for (a in 0 until radioGroup.childCount) {
                    val rb: RadioButton = radioGroup.getChildAt(a) as RadioButton
                    rb.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorWhite
                        )
                    )
                }

                radioButton
                    .isChecked = true

                radioButton.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                categoryId = radioButton.id
                productList.clear()

                if (loginModel.data.designation_id == 0) {
                    getProducts(0)
                } else {
                    if (permissionsList.contains(PermissionKeys.view_products)) {
                        productListRecycler.visibility = View.VISIBLE
                        pullToRefresh.isEnabled = true
                        getProducts(0)
                    } else {
                        productListRecycler.visibility = View.GONE
                        pullToRefresh.isEnabled = false
                    }
                }
            }
        }

        radioGroup.getChildAt(0).callOnClick() // To get the products at first time


    }


    fun prodLotDetail(
        layout: LinearLayout,
        varieties: List<Variety>,
        productListData: ProductListData
    ) {
        layout.removeAllViews()
        val inflater: LayoutInflater = LayoutInflater.from(context)
        for (i in varieties.indices) {
            val view = inflater.inflate(R.layout.pro_category_list_detail, null, false)
            view.findViewById<TextView>(R.id.variatey_title).text = varieties[i].product_variety
            val line = view.findViewById<LinearLayout>(R.id.variatey_detail)
            val btnVarietyEdit = view.findViewById<Button>(R.id.btn_edit_variety)
            val btnAddVarietyLot = view.findViewById<Button>(R.id.btn_add_variety_lot)
            btnVarietyEdit.id = varieties[i].id
            btnAddVarietyLot.id = varieties[i].id
            btnAddVarietyLot.tag = varieties[i].product_variety

            btnAddVarietyLot.visibility = View.VISIBLE
            btnVarietyEdit.visibility = View.VISIBLE

            btnAddVarietyLot.setOnClickListener {
                fromEdit = false
                selectedVarietyId = it.id
                selectedVarietyName = it.tag.toString()
                selectedProductData = productListData
                if (permissionsList.contains(PermissionKeys.update_products))
                    qualityIncoming.callOnClick()
            }

            btnVarietyEdit.setOnClickListener {
                selectedVarietyId = btnVarietyEdit.id

                fromEdit = false
                selectedLotId = 0
                selectedVarietyName = ""
                selectedProductData = null

                if (permissionsList.contains(PermissionKeys.create_complaint))
                    qualityComplaintsToSupplier.callOnClick()
            }

            val vari_lot = varieties[i].lots
            for (element in vari_lot) {
                val lot_ob = element;
                val view_2 = inflater.inflate(R.layout.pro_category_list_detail_item, null, false)
                view_2.findViewById<TextView>(R.id.lot_no).text = lot_ob.lot_no
                view_2.findViewById<TextView>(R.id.lot_quantity).text = lot_ob.stock
                view_2.findViewById<TextView>(R.id.lot_origin).text = lot_ob.origin
                view_2.findViewById<TextView>(R.id.lot_sell).text =
                    "${lot_ob.selling_price} / ${lot_ob.selling_unit}"
                view_2.findViewById<TextView>(R.id.lot_incom).text = lot_ob.incoming_date


                val btnLotEdit = view_2.findViewById<Button>(R.id.btn_edit)
                btnLotEdit.id = lot_ob.id

                btnLotEdit.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - Constants.mLastClickTime < 1000)
                        return@setOnClickListener
                    Constants.mLastClickTime = SystemClock.elapsedRealtime()

                    fromEdit = true
                    selectedLotId = btnLotEdit.id
                    selectedProductData = productListData
                    if (permissionsList.contains(PermissionKeys.update_products))
                        qualityIncoming.callOnClick()
                }


                if (loginModel.data.designation_id != 0) {
                    if (permissionsList.contains(PermissionKeys.update_products)) {
                        btnLotEdit.visibility = View.VISIBLE
                        btnLotEdit.isEnabled = true
                    } else {
                        btnLotEdit.visibility = View.INVISIBLE
                        btnLotEdit.isEnabled = false
                    }
                }

                line.addView(view_2)
            }
            layout.addView(view)
        }

    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetProductCategories") {
            handleCategoriesResponse(jsonObject)
        } else if (tag == "GetProductsList") {
            handleListResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
        }
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
        }
        AppUtils.showToast(activity, message!!, false)
    }


    fun getProducts(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductList(categoryId, c)
        RetrofitClient.apiCall(call, this, "GetProductsList")
    }

    private fun handleListResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val proLisModel =
            gson.fromJson(jsonObject.toString(), ProductListModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            productList.clear()
        }

        if (productList.size > 0) {
            productList.removeAt(productList.size - 1)
            productListAdapter.notifyItemRemoved(productList.size)
        }


        CURRENT_PAGE = proLisModel.meta.current_page
        LAST_PAGE = proLisModel.meta.last_page

        productList.addAll(proLisModel.data)

        productListAdapter.filteredProductList = productList
        productListAdapter.unfilteredProductList = productList
        productListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    private fun launchFragment(fragment: Fragment) {
        val bundle = Bundle()

        if (fragment is QualityIncoming) {
            bundle.putInt("lotId", selectedLotId)
            bundle.putInt("id", categoryId)
            bundle.putBoolean("fromEdit", fromEdit)
            bundle.putInt("selectedVarietyId", selectedVarietyId)
            bundle.putString("selectedVarietyName", selectedVarietyName)
            if (selectedProductData != null) {
                bundle.putInt("selectedProductId", selectedProductData?.id!!)
            }
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.quality_management_fragment, fragment)
                .commit()
        } else {
            if (selectedVarietyId != 0) {
                bundle.putInt("VarietyId", selectedVarietyId)
                bundle.putInt("id", categoryId)
                fragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.quality_management_fragment, fragment)
                    .commit()

            } else {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorQualityVarietySelect),
                    false
                )
            }
        }
    }

}
