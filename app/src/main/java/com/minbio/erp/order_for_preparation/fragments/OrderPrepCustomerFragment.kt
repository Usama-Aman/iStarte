package com.minbio.erp.order_for_preparation.fragments

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
import com.minbio.erp.order_for_preparation.OrderPreparationFragment
import com.minbio.erp.order_for_preparation.adapter.OrderPrepCustomerListAdapter
import com.minbio.erp.order_for_preparation.models.CustomerOrderData
import com.minbio.erp.order_for_preparation.models.CustomerOrdersModel
import com.minbio.erp.order_for_preparation.models.PendingOrder
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class OrderPrepCustomerFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var orderPrepCustomerListAdapter: OrderPrepCustomerListAdapter
    private lateinit var opCustomerRecyclerView: RecyclerView
    private lateinit var customerOrderPullToRefresh: SwipeRefreshLayout
    private lateinit var etOPCustomerSearch: EditText
    private lateinit var tvNoData: TextView

    private var customerOrderList: MutableList<CustomerOrderData?> = ArrayList()
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_order_prep_customer_list, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.order_preparation.split(",")

        initViews()
        setUpAdapter()
        initScrollListener()

        setUpPermissions()


        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getCustomerOrders(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_customers)) {
                getCustomerOrders(0)
                customerOrderPullToRefresh.isEnabled = true
                opCustomerRecyclerView.visibility = View.VISIBLE
            } else {
                customerOrderPullToRefresh.isEnabled = false
                opCustomerRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        etOPCustomerSearch = v.findViewById(R.id.etOPCustomerSearch)
        opCustomerRecyclerView = v.findViewById(R.id.opCustomerListRecycler)
        customerOrderPullToRefresh = v.findViewById(R.id.customerOrderPullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        customerOrderPullToRefresh.setOnRefreshListener { getCustomerOrders(0) }

        etOPCustomerSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                orderPrepCustomerListAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun setUpAdapter() {
        orderPrepCustomerListAdapter = OrderPrepCustomerListAdapter(this, customerOrderList)
        opCustomerRecyclerView.layoutManager = LinearLayoutManager(context)
        opCustomerRecyclerView.adapter = orderPrepCustomerListAdapter
    }

    private fun initScrollListener() {
        opCustomerRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customerOrderList.size - 1) {
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
                customerOrderList.add(null)
                orderPrepCustomerListAdapter.notifyItemInserted(customerOrderList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCustomerOrders(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCustomerOrders(c: Int) {
        if (!customerOrderPullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomerOrders(c)
        RetrofitClient.apiCall(call, this, "GetCustomerOrders")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomerOrders") {
            handleCustomerOrderResponse(jsonObject)
        }

    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetCustomerOrders") {
            if (customerOrderPullToRefresh.isRefreshing)
                customerOrderPullToRefresh.isRefreshing = false
        }
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        if (tag == "GetCustomerOrders") {
            if (customerOrderPullToRefresh.isRefreshing)
                customerOrderPullToRefresh.isRefreshing = false
        }
    }

    private fun handleCustomerOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val customerOrderModel =
            gson.fromJson(jsonObject.toString(), CustomerOrdersModel::class.java)

        if (customerOrderPullToRefresh.isRefreshing) {
            customerOrderPullToRefresh.isRefreshing = false
            customerOrderList.clear()
        }

        if (customerOrderList.size > 0) {
            customerOrderList.removeAt(customerOrderList.size - 1)
            orderPrepCustomerListAdapter.notifyItemRemoved(customerOrderList.size)
        }


        CURRENT_PAGE = customerOrderModel.meta.current_page
        LAST_PAGE = customerOrderModel.meta.last_page

        customerOrderList.addAll(customerOrderModel.data)

        if (customerOrderList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            opCustomerRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            opCustomerRecyclerView.visibility = View.VISIBLE
        }


        orderPrepCustomerListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun viewExpandDetails(
        detailLayout: LinearLayout,
        pendingOrders: List<PendingOrder?>
    ) {

        detailLayout.removeAllViews()
        if (pendingOrders.isNotEmpty()) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val mainLayout = inflater.inflate(R.layout.item_customer_orders_expand1, null, false)
            val item = mainLayout.findViewById<LinearLayout>(R.id.expand_details)

            mainLayout.findViewById<TextView>(R.id.textView7).text =context!!.resources.getString(
                R.string.pro_lot_de_lot_sell,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )

            for (i in pendingOrders.indices) {

                val itemLayout =
                    inflater.inflate(R.layout.item_customer_orders_expand2, null, false)
                itemLayout.findViewById<TextView>(R.id.t1).text = pendingOrders[i]?.order_no
                itemLayout.findViewById<TextView>(R.id.t2).text = pendingOrders[i]?.date
                itemLayout.findViewById<TextView>(R.id.t3).text = pendingOrders[i]?.time
                itemLayout.findViewById<TextView>(R.id.t4).text = pendingOrders[i]?.status

                val itemMainLayout = itemLayout.findViewById<RelativeLayout>(R.id.itemMainLayout)
                itemMainLayout.id = i
                itemMainLayout.setOnClickListener {
                    (parentFragment as OrderPreparationFragment).generateRightTabs(
                        pendingOrders[itemLayout.id]?.id,
                        pendingOrders[itemLayout.id]?.order_no!!,
                        pendingOrders[itemLayout.id]?.details!!
                    )
                }

                item.addView(itemLayout)
            }
            detailLayout.addView(mainLayout)
        }

    }

}