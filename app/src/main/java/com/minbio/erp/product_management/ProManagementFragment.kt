package com.minbio.erp.product_management

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
import com.minbio.erp.product_management.adapter.ProductListAdapter
import com.minbio.erp.product_management.fragments.ProCustomerComplaintFragment
import com.minbio.erp.product_management.fragments.ProSupplierComplaintFragment
import com.minbio.erp.product_management.fragments.ProductDetailFragment
import com.minbio.erp.product_management.fragments.ProductInventoryFragment
import com.minbio.erp.product_management.model.*
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ProManagementFragment : Fragment(), ResponseCallBack {

    private lateinit var et_search: EditText
    private var categoryId: Int = 0
    private var selectedLotId: Int = 0
    private var selectedVarietyId: Int = 0

    private lateinit var v: View
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var fromEdit = false
    private var selectedProductData: ProductListData? = null

    private lateinit var productListRecycler: RecyclerView
    lateinit var productListAdapter: ProductListAdapter
    lateinit var pullToRefresh: SwipeRefreshLayout
    lateinit var btn_add: TextView

    var productList: MutableList<ProductListData?> = ArrayList()

    private lateinit var detail: RadioButton
    private lateinit var inventory: RadioButton
    private lateinit var customer_comp: RadioButton
    private lateinit var supplier_comp: RadioButton

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_pro_mangement, container, false)


        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.product_management.split(",")


        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.proPageTitle))

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
                pullToRefresh.isEnabled = true
                detail.isEnabled = true
                detail.isClickable = true
            } else {
                pullToRefresh.isEnabled = false
                detail.isEnabled = false
                detail.isClickable = false
            }

            customer_comp.isEnabled = permissionsList.contains(PermissionKeys.view_complaint)
            supplier_comp.isEnabled = permissionsList.contains(PermissionKeys.view_complaint)
            inventory.isEnabled = permissionsList.contains(PermissionKeys.view_products)

        }
    }

    private fun initViews() {

        btn_add = v.findViewById(R.id.btn_add)
        btn_add.setOnClickListener {
            fromEdit = false
            onRightTabClick(detail)
        }
        pullToRefresh = v.findViewById(R.id.productListPullToRefresh)
        pullToRefresh.setOnRefreshListener { getProducts(0) }
        detail = v.findViewById(R.id.product_detail)
        inventory = v.findViewById(R.id.inventory)
        customer_comp = v.findViewById(R.id.customer_comp)
        supplier_comp = v.findViewById(R.id.supplier_comp)

        detail.setOnClickListener {
            onRightTabClick(detail)
        }
        inventory.setOnClickListener {
            onRightTabClick(inventory)
        }
        customer_comp.setOnClickListener {
            onRightTabClick(customer_comp)
        }
        supplier_comp.setOnClickListener {
            onRightTabClick(supplier_comp)
        }


        et_search = v.findViewById(R.id.et_pro_search)
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
        productListRecycler = v.findViewById(R.id.product_list_recycler)
        productListAdapter = ProductListAdapter(this, productList)
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

    private fun onRightTabClick(v: View) {
        detail.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        inventory.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        customer_comp.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        supplier_comp.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var detail_d = ContextCompat.getDrawable(context!!, R.drawable.ic_detail)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        detail.setCompoundDrawables(detail_d, null, null, null);
        var inventory_d = ContextCompat.getDrawable(context!!, R.drawable.ic_inventory)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        inventory.setCompoundDrawables(inventory_d, null, null, null);

        var comp_d = ContextCompat.getDrawable(context!!, R.drawable.ic_complain)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        customer_comp.setCompoundDrawables(comp_d, null, null, null);
        supplier_comp.setCompoundDrawables(comp_d, null, null, null);

        when (v.id) {
            R.id.product_detail -> {
                detail.isChecked = true
                detail.setTextColor(ContextCompat.getColor(context!!, R.color.colorDarkBlue))
                detail_d = ContextCompat.getDrawable(context!!, R.drawable.ic_detail_sel)?.apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
                detail.setCompoundDrawables(detail_d, null, null, null);

                launchFragment(ProductDetailFragment())

            }
            R.id.inventory -> {
                inventory.isChecked = true;
                inventory.setTextColor(ContextCompat.getColor(context!!, R.color.colorDarkBlue))
                inventory_d =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_inventory_sel)?.apply {
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                    }
                inventory.setCompoundDrawables(inventory_d, null, null, null)

                launchFragment(ProductInventoryFragment())

            }
            R.id.customer_comp -> {
                customer_comp.isChecked = true
                customer_comp.setTextColor(ContextCompat.getColor(context!!, R.color.colorDarkBlue))
                comp_d = ContextCompat.getDrawable(context!!, R.drawable.ic_complain_sel)?.apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
                customer_comp.setCompoundDrawables(comp_d, null, null, null)

                launchFragment(ProCustomerComplaintFragment())

            }
            R.id.supplier_comp -> {
                supplier_comp.setTextColor(ContextCompat.getColor(context!!, R.color.colorDarkBlue))
                comp_d = ContextCompat.getDrawable(context!!, R.drawable.ic_complain_sel)?.apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
                supplier_comp.setCompoundDrawables(comp_d, null, null, null)

                launchFragment(ProSupplierComplaintFragment())
            }
        }
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
            btnVarietyEdit.id = varieties[i].id
            btnVarietyEdit.visibility = View.VISIBLE
            btnVarietyEdit.setOnClickListener {
                selectedVarietyId = btnVarietyEdit.id
                if (permissionsList.contains(PermissionKeys.view_complaint))
                    customer_comp.callOnClick()
            }
            val btnspace = view.findViewById<Button>(R.id.btnspace)
            btnspace.visibility = View.GONE

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
                btnLotEdit.visibility = View.GONE
                val productLotLayout = view_2.findViewById<RelativeLayout>(R.id.productLotLayout)
                productLotLayout.id = lot_ob.id

                productLotLayout.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - Constants.mLastClickTime < 1000)
                        return@setOnClickListener
                    Constants.mLastClickTime = SystemClock.elapsedRealtime()

                    fromEdit = true
                    selectedLotId = productLotLayout.id
                    selectedProductData = productListData
                    onRightTabClick(detail)
                }

                line.addView(view_2)
            }
            layout.addView(view)
        }

    }

    private fun getCategories() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductCategories()
        RetrofitClient.apiCall(call, this, "GetProductCategories")
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
                        inventory.isEnabled = true
                    } else {
                        productListRecycler.visibility = View.GONE
                        pullToRefresh.isEnabled = false
                        inventory.isEnabled = false
                    }
                }
            }
        }

        radioGroup.getChildAt(0).callOnClick() // To get the products at first time


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

    fun editProduct(pld: ProductListData) {
        fromEdit = true
        selectedProductData = pld
        detail.callOnClick()
    }

    private fun launchFragment(fragment: Fragment) {
        if (fragment is ProductDetailFragment) {
            if (selectedLotId != 0) {
                val bundle = Bundle()
                if (fromEdit) {
                    bundle.putInt("id", categoryId)
                    bundle.putInt("lotId", selectedLotId)
                } else
                    bundle.putInt("id", categoryId)

                bundle.putBoolean("fromEdit", fromEdit)
                fragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.product_fragment, fragment)
                    .commit()
            } else {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorPleaseSelectLot),
                    false
                )
            }

        } else if (fragment is ProCustomerComplaintFragment || fragment is ProSupplierComplaintFragment) {
            if (selectedVarietyId != 0) {
                val bundle = Bundle()
                bundle.putInt("varietyId", selectedVarietyId)
                fragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.product_fragment, fragment)
                    .commit()
            } else {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorPleaseSelectVarity),
                    false
                )
            }

        } else {
            childFragmentManager.beginTransaction()
                .replace(R.id.product_fragment, fragment)
                .commit()
        }
    }


}
