package com.minbio.erp.order_for_preparation.fragments

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
import com.minbio.erp.order_for_preparation.OrderPreparationFragment
import com.minbio.erp.order_for_preparation.adapter.OrderPrepOrderPendingAdapter
import com.minbio.erp.order_for_preparation.models.OrderDetails
import com.minbio.erp.order_for_preparation.models.PendingOrderData
import com.minbio.erp.order_for_preparation.models.PendingOrderModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class OrderPrepOrderPending : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var orderPrepOrderPendingAdapter: OrderPrepOrderPendingAdapter
    private lateinit var orderPendingRecyclerView: RecyclerView
    private lateinit var pendingOrderPullToRefresh: SwipeRefreshLayout
    private lateinit var etOPPendingOrderSearch:EditText
    private lateinit var tvNoData:TextView

    private var pendingOrderList: MutableList<PendingOrderData?> = ArrayList()
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_order_prep_pending_order, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.order_preparation.split(",")

        initViews()
        setupAdapter()
        initScrollListener()

        setUpPermissions()

        return v
    }


    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getPendingOrders(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_orders)) {
                getPendingOrders(0)
                pendingOrderPullToRefresh.isEnabled = true
                orderPendingRecyclerView.visibility = View.VISIBLE
            } else {
                pendingOrderPullToRefresh.isEnabled = false
                orderPendingRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        etOPPendingOrderSearch = v.findViewById(R.id.etOPPendingOrderSearch)
        orderPendingRecyclerView = v.findViewById(R.id.op_order_pending_recycler_view)
        pendingOrderPullToRefresh = v.findViewById(R.id.pendingOrderPullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        pendingOrderPullToRefresh.setOnRefreshListener {
            getPendingOrders(0)
        }

        etOPPendingOrderSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                orderPrepOrderPendingAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun setupAdapter() {
        orderPrepOrderPendingAdapter = OrderPrepOrderPendingAdapter(this, pendingOrderList)
        orderPendingRecyclerView.layoutManager = LinearLayoutManager(context)
        orderPendingRecyclerView.adapter = orderPrepOrderPendingAdapter
    }

    private fun initScrollListener() {
        orderPendingRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == pendingOrderList.size - 1) {
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
                pendingOrderList.add(null)
                orderPrepOrderPendingAdapter.notifyItemInserted(pendingOrderList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getPendingOrders(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPendingOrders(c: Int) {
        if (!pendingOrderPullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getPendingOrders(c)
        RetrofitClient.apiCall(call, this, "GetPendingOrders")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetPendingOrders") {
            handleCustomerOrderResponse(jsonObject)

        }

    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetPendingOrders") {
            if (pendingOrderPullToRefresh.isRefreshing)
                pendingOrderPullToRefresh.isRefreshing = false
        }
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        if (tag == "GetPendingOrders") {
            if (pendingOrderPullToRefresh.isRefreshing)
                pendingOrderPullToRefresh.isRefreshing = false
        }
    }

    private fun handleCustomerOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val pendingOrderModel =
            gson.fromJson(jsonObject.toString(), PendingOrderModel::class.java)

        if (pendingOrderPullToRefresh.isRefreshing) {
            pendingOrderPullToRefresh.isRefreshing = false
            pendingOrderList.clear()
        }

        if (pendingOrderList.size > 0) {
            pendingOrderList.removeAt(pendingOrderList.size - 1)
            orderPrepOrderPendingAdapter.notifyItemRemoved(pendingOrderList.size)
        }


        CURRENT_PAGE = pendingOrderModel.meta.current_page
        LAST_PAGE = pendingOrderModel.meta.last_page

        pendingOrderList.addAll(pendingOrderModel.data)

        if (pendingOrderList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            orderPendingRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            orderPendingRecyclerView.visibility = View.VISIBLE
        }


        orderPrepOrderPendingAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun itemClick(
        id: Int?,
        orderNo: String?,
        details: List<OrderDetails>?
    ) {

        (parentFragment as OrderPreparationFragment).generateRightTabs(
            id, orderNo!!, details!!
        )
    }

}