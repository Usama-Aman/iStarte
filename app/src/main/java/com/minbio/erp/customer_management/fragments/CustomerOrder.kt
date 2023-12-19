package com.minbio.erp.customer_management.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.models.CDBalanceData
import com.minbio.erp.cashier_desk.models.CDCustomerBalanceModel
import com.minbio.erp.customer_management.adapter.CustomerOrderAdapter
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class CustomerOrder : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var cdBalanceList: MutableList<CDBalanceData?> = ArrayList()
    private var customerData: CustomersData? = null


    private lateinit var v: View
    private lateinit var customerOrderAdapter: CustomerOrderAdapter
    private lateinit var coRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var cmOrderOverdraft: TextView
    private lateinit var cmOrderPrice: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_customer_order, container, false)

        initViews()
        setAdapter()
        initScrollListener()

        return v;
    }

    private fun initViews() {
        coRecyclerView = v.findViewById(R.id.cmOrder_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        cmOrderOverdraft = v.findViewById(R.id.cmOrderOverdraft)
        cmOrderPrice = v.findViewById(R.id.cmOrderPrice)
        cmOrderOverdraft.text = context!!.resources.getString(
            R.string.cmOrderLabelOverdue,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        cmOrderPrice.text = context!!.resources.getString(
            R.string.cmOrderLabelPrice,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        customerData = arguments!!.getParcelable("CustomerData")

        if (customerData != null)
            getCustomerBalance(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }

    private fun setAdapter() {
        customerOrderAdapter = CustomerOrderAdapter(cdBalanceList)
        coRecyclerView.layoutManager = LinearLayoutManager(context)
        coRecyclerView.adapter = customerOrderAdapter

    }

    private fun initScrollListener() {
        coRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == cdBalanceList.size - 1) {
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
                cdBalanceList.add(null)
                customerOrderAdapter.notifyItemInserted(cdBalanceList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCustomerBalance(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCustomerBalance(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomerBalance(customerData?.id!!, c)
        RetrofitClient.apiCall(call, this, "GetCustomerBalance")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomerBalance") {
            handleOrderResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }


    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }


    private fun handleOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model =
            gson.fromJson(jsonObject.toString(), CDCustomerBalanceModel::class.java)

        if (cdBalanceList.size > 0) {
            cdBalanceList.removeAt(cdBalanceList.size - 1)
            customerOrderAdapter.notifyItemRemoved(cdBalanceList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        cdBalanceList.addAll(model.data)

        if (cdBalanceList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            coRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            coRecyclerView.visibility = View.VISIBLE
        }

        customerOrderAdapter.notifyDataSetChanged()
        isLoading = false
    }


}