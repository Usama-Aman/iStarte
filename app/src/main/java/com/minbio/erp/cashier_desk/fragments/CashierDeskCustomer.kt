package com.minbio.erp.cashier_desk.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.minbio.erp.cashier_desk.adapter.CashierDeskCustomerAdapter
import com.minbio.erp.cashier_desk.models.CDCustomerOrdersModel
import com.minbio.erp.cashier_desk.models.CDCustomersData
import com.minbio.erp.cashier_desk.models.CDPendingOrder
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*

class CashierDeskCustomer : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var cashierDeskCustomerAdapter: CashierDeskCustomerAdapter
    private lateinit var cdcRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etCDCustomerSearch: EditText
    private lateinit var tvNoData: TextView

    private var cdCustomerOrderList: MutableList<CDCustomersData?> = ArrayList()
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_cashier_desk_customer, container, false)

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
            getCashierCustomers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_customers)) {
                getCashierCustomers(0)
                pullToRefresh.isEnabled = true
                cdcRecyclerView.visibility = View.VISIBLE
            } else {
                pullToRefresh.isEnabled = false
                cdcRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        etCDCustomerSearch = v.findViewById(R.id.etCDCustomerSearch)
        cdcRecyclerView = v.findViewById(R.id.cdc_recycler_view)
        pullToRefresh = v.findViewById(R.id.cdcPullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener {
            getCashierCustomers(0)
        }

        etCDCustomerSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashierDeskCustomerAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun setupAdapter() {
        cashierDeskCustomerAdapter = CashierDeskCustomerAdapter(this, cdCustomerOrderList)
        cdcRecyclerView.layoutManager = LinearLayoutManager(context)
        cdcRecyclerView.adapter = cashierDeskCustomerAdapter

    }


    private fun initScrollListener() {
        cdcRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == cdCustomerOrderList.size - 1) {
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
                cdCustomerOrderList.add(null)
                cashierDeskCustomerAdapter.notifyItemInserted(cdCustomerOrderList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCashierCustomers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCashierCustomers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCashierCustomerOrders(c)
        RetrofitClient.apiCall(call, this, "GetCashierCustomerOrders")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCashierCustomerOrders") {
            handleOrderResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetCashierCustomerOrders") {
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
        val model =
            gson.fromJson(jsonObject.toString(), CDCustomerOrdersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            cdCustomerOrderList.clear()
        }

        if (cdCustomerOrderList.size > 0) {
            cdCustomerOrderList.removeAt(cdCustomerOrderList.size - 1)
            cashierDeskCustomerAdapter.notifyItemRemoved(cdCustomerOrderList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        cdCustomerOrderList.addAll(model.data)

        if (cdCustomerOrderList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            cdcRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            cdcRecyclerView.visibility = View.VISIBLE
        }


        cashierDeskCustomerAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun viewExpandDetails(detailLayout: LinearLayout, pendingOrders: List<CDPendingOrder>) {
        detailLayout.removeAllViews()
        if (pendingOrders.isNotEmpty()) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val mainLayout = inflater.inflate(R.layout.item_cashier_customer_expand, null, false)
            val item = mainLayout.findViewById<LinearLayout>(R.id.expand_details)

            for (i in pendingOrders.indices) {

                val itemLayout =
                    inflater.inflate(R.layout.item_customer_orders_expand2, null, false)
                itemLayout.findViewById<TextView>(R.id.t1).text = pendingOrders[i].order_no
                itemLayout.findViewById<TextView>(R.id.t2).text = pendingOrders[i].date
                itemLayout.findViewById<TextView>(R.id.t3).text = pendingOrders[i].status
                itemLayout.findViewById<TextView>(R.id.t4).text = pendingOrders[i].due_date

                val itemMainLayout = itemLayout.findViewById<RelativeLayout>(R.id.itemMainLayout)
                itemMainLayout.id = i
                itemMainLayout.setOnClickListener {
                    (parentFragment as CashierDeskFragment).onListItemClicked(
                        pendingOrders[itemMainLayout.id].order_no,
                        pendingOrders[itemMainLayout.id].details
                    )
                }

                item.addView(itemLayout)
            }
            detailLayout.addView(mainLayout)
        }
    }
}