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
import com.minbio.erp.accounting.adapter.AccountingCustomerListAdapter
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.customer_management.models.CustomersModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*

class AccountingCustomerList : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var accountingCustomerListAdapter: AccountingCustomerListAdapter
    private lateinit var aclRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var tvNoData: TextView

    var customersList: MutableList<CustomersData?> = ArrayList()
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_customer_list, container, false)


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
            getCustomers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_customers)) {
                aclRecyclerView.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                getCustomers(0)
            } else {
                aclRecyclerView.visibility = View.GONE
                pullToRefresh.isEnabled = false

            }

        }
    }


    private fun initViews() {
        aclRecyclerView = v.findViewById(R.id.acc_customer_list_recycler_view)
        pullToRefresh = v.findViewById(R.id.acc_swipe)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener {
            getCustomers(0)
        }


        etSearch = v.findViewById(R.id.et_acc_customer_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                accountingCustomerListAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun setupAdapter() {
        accountingCustomerListAdapter = AccountingCustomerListAdapter(this, customersList)
        aclRecyclerView.layoutManager = LinearLayoutManager(context)
        aclRecyclerView.adapter = accountingCustomerListAdapter
    }


    private fun initScrollListener() {
        aclRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customersList.size - 1) {
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
                customersList.add(null)
                accountingCustomerListAdapter.notifyItemInserted(customersList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCustomers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getCustomers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomers(c)
        RetrofitClient.apiCall(call, this, "GetCustomers")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomers") {
            handleGetCustomerResponse(jsonObject)
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

    private fun handleGetCustomerResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val customerModel =
            gson.fromJson(jsonObject.toString(), CustomersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            customersList.clear()
        }

        if (customersList.size > 0) {
            customersList.removeAt(customersList.size - 1)
            accountingCustomerListAdapter.notifyItemRemoved(customersList.size)
        }


        CURRENT_PAGE = customerModel.meta.current_page
        LAST_PAGE = customerModel.meta.last_page

        customersList.addAll(customerModel.data)

        if (customersList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            aclRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            aclRecyclerView.visibility = View.VISIBLE
        }

        accountingCustomerListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun userClick(id: Int) {
        (parentFragment as AccountingFragment).leftItemClick(id)
    }


}