package com.minbio.erp.accounting.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.accounting.AccountingFragment
import com.minbio.erp.accounting.adapter.AccountingInventoryAdapter
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.product_management.model.ProductListData
import com.minbio.erp.product_management.model.ProductListModel
import com.minbio.erp.product_management.model.Variety
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*

class AccountingInventory : Fragment(), ResponseCallBack {

    var productList: MutableList<ProductListData?> = ArrayList()
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false
    private var selectedVarietyId: Int = 0

    private lateinit var v: View
    private lateinit var accountingInventoryAdapter: AccountingInventoryAdapter
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var tvNoData: TextView

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_inventory, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.accounting.split(",")

        initViews()
        setupAdapter()
        initScrollListener()
        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getProducts(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_products)) {
                inventoryRecyclerView.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                getProducts(0)
            } else {
                inventoryRecyclerView.visibility = View.GONE
                pullToRefresh.isEnabled = false
            }
        }
    }

    private fun initViews() {
        inventoryRecyclerView = v.findViewById(R.id.acc_inventory_recycler_view)
        pullToRefresh = v.findViewById(R.id.acc_inventory_swipe)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener { getProducts(0) }


        etSearch = v.findViewById(R.id.et_acc_inventory_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                accountingInventoryAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun setupAdapter() {
        accountingInventoryAdapter = AccountingInventoryAdapter(this, productList)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(context)
        inventoryRecyclerView.adapter = accountingInventoryAdapter
    }

    private fun initScrollListener() {
        inventoryRecyclerView.addOnScrollListener(object :
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
            if (CURRENT_PAGE < LAST_PAGE) {
                productList.add(null)
                accountingInventoryAdapter.notifyItemInserted(productList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getProducts(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getProducts(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductList(0, c)
        RetrofitClient.apiCall(call, this, "GetProductsList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetProductsList") {
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
            accountingInventoryAdapter.notifyItemRemoved(productList.size)
        }


        CURRENT_PAGE = proLisModel.meta.current_page
        LAST_PAGE = proLisModel.meta.last_page

        productList.addAll(proLisModel.data)


        if (productList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            inventoryRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            inventoryRecyclerView.visibility = View.VISIBLE
        }


        accountingInventoryAdapter.notifyDataSetChanged()
        isLoading = false
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

            view.findViewById<Button>(R.id.btnspace).visibility = View.GONE
            btnVarietyEdit.visibility = View.VISIBLE

            btnVarietyEdit.setOnClickListener {
                selectedVarietyId = btnVarietyEdit.id
                (parentFragment as AccountingFragment).leftItemClick(selectedVarietyId)
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
                btnLotEdit.visibility = View.GONE

                line.addView(view_2)
            }
            layout.addView(view)
        }

    }

    fun editProduct(pld: ProductListData) {
    }

}