package com.minbio.erp.cashier_desk.fragments

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
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.cashier_desk.CashierDeskFragment
import com.minbio.erp.cashier_desk.adapter.CashierDeskOrderAdapter
import com.minbio.erp.cashier_desk.models.CDOrderData
import com.minbio.erp.cashier_desk.models.CDOrderDetail
import com.minbio.erp.cashier_desk.models.CDOrdersModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CashierDeskOrder : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var cdOrderList: MutableList<CDOrderData?> = ArrayList()

    private lateinit var v: View
    private lateinit var cashierDeskOrderAdapter: CashierDeskOrderAdapter
    private lateinit var cdoRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etCashierOrderSearch: EditText
    private lateinit var tvNoData: TextView

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_cashier_desk_order, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.cashier_desk.split(",")

        initViews()
        setupAdapter()
        initScrollListener()

        setUpPermissions()

        return v
    }


    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getOrdersList(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_orders)) {
                getOrdersList(0)
                pullToRefresh.isEnabled = true
                cdoRecyclerView.visibility = View.VISIBLE
            } else {
                pullToRefresh.isEnabled = false
                cdoRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        etCashierOrderSearch = v.findViewById(R.id.etCashierOrderSearch)
        cdoRecyclerView = v.findViewById(R.id.cdo_recycler_view)
        pullToRefresh = v.findViewById(R.id.cdoPullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener {
            getOrdersList(0)
        }

        etCashierOrderSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashierDeskOrderAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


    }

    private fun setupAdapter() {
        cashierDeskOrderAdapter = CashierDeskOrderAdapter(this, cdOrderList)
        cdoRecyclerView.layoutManager = LinearLayoutManager(context)
        cdoRecyclerView.adapter = cashierDeskOrderAdapter

    }

    private fun initScrollListener() {
        cdoRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == cdOrderList.size - 1) {
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
                cdOrderList.add(null)
                cashierDeskOrderAdapter.notifyItemInserted(cdOrderList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getOrdersList(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getOrdersList(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCashierOrders(c)
        RetrofitClient.apiCall(call, this, "GetCashierOrders")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCashierOrders") {
            handleOrderResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetCashierOrders") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }


    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }


    private fun handleOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val customerModel =
            gson.fromJson(jsonObject.toString(), CDOrdersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            cdOrderList.clear()
        }

        if (cdOrderList.size > 0) {
            cdOrderList.removeAt(cdOrderList.size - 1)
            cashierDeskOrderAdapter.notifyItemRemoved(cdOrderList.size)
        }


        CURRENT_PAGE = customerModel.meta.current_page
        LAST_PAGE = customerModel.meta.last_page

        cdOrderList.addAll(customerModel.data)

        if (cdOrderList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            cdoRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            cdoRecyclerView.visibility = View.VISIBLE
        }

        cashierDeskOrderAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun itemClick(orderNo: String?, details: CDOrderDetail?) {
        (parentFragment as CashierDeskFragment).onListItemClicked(orderNo, details)
    }

}