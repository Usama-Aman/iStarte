package com.minbio.erp.accounting.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import com.minbio.erp.accounting.adapter.AccountingSupplierListAdapter
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.supplier_management.models.SuppliersModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*

class AccountingSupplierList : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var accountingSupplierListAdapter: AccountingSupplierListAdapter
    private lateinit var aclRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var tvNoData: TextView

    var suppliersList: MutableList<SuppliersData?> = ArrayList()
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_supplier_list, container, false)


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
            getSuppliers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_suppliers)) {
                aclRecyclerView.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                getSuppliers(0)
            } else {
                aclRecyclerView.visibility = View.GONE
                pullToRefresh.isEnabled = false
            }
        }
    }


    private fun initViews() {
        aclRecyclerView = v.findViewById(R.id.acc_supplier_list_recycler_view)
        pullToRefresh = v.findViewById(R.id.acc_supplier_list_swipe)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener { getSuppliers(0) }


        etSearch = v.findViewById(R.id.et_acc_supplier_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                accountingSupplierListAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun setupAdapter() {
        accountingSupplierListAdapter = AccountingSupplierListAdapter(this, suppliersList)
        aclRecyclerView.layoutManager = LinearLayoutManager(context)
        aclRecyclerView.adapter = accountingSupplierListAdapter
    }


    private fun initScrollListener() {
        aclRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == suppliersList.size - 1) {
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
                suppliersList.add(null)
                accountingSupplierListAdapter.notifyItemInserted(suppliersList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getSuppliers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getSuppliers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getSuppliers(c)
        RetrofitClient.apiCall(call, this, "GetSuppliers")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetSuppliers") {
            handleSupplierResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetCustomers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        if (tag == "GetCustomers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }

    private fun handleSupplierResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val supplierModel =
            gson.fromJson(jsonObject.toString(), SuppliersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            suppliersList.clear()
        }

        if (suppliersList.size > 0) {
            suppliersList.removeAt(suppliersList.size - 1)
            accountingSupplierListAdapter.notifyItemRemoved(suppliersList.size)
        }


        CURRENT_PAGE = supplierModel.meta.current_page
        LAST_PAGE = supplierModel.meta.last_page

        suppliersList.addAll(supplierModel.data)

        if (suppliersList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            aclRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            aclRecyclerView.visibility = View.VISIBLE
        }

        accountingSupplierListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun itemEdit(
        id: Int
    ) {
        (parentFragment as AccountingFragment).leftItemClick(id)
    }


}