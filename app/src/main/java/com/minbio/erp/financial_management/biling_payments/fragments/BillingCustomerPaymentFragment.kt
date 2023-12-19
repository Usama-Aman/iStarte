package com.minbio.erp.financial_management.biling_payments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.adapters.BillingCustomerPaymentsAdapter
import com.minbio.erp.financial_management.biling_payments.models.BillingCustomerPaymentData
import com.minbio.erp.financial_management.biling_payments.models.BillingCustomerPaymentModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class BillingCustomerPaymentFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var addEntry: ImageView
    private lateinit var bcpRecyclerView: RecyclerView
    private lateinit var billingCustomerPaymentsAdapter: BillingCustomerPaymentsAdapter
    private lateinit var bcpltvPaidAmount: TextView

    private var paymentData: MutableList<BillingCustomerPaymentData?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_customer_payments, container, false)

        initViews()
        setAdapter()
        initScrollListener()
        getCustomerPayments(0)

        return v
    }

    private fun initViews() {
        bcpltvPaidAmount = v.findViewById(R.id.bcpltvPaidAmount)
        bcpltvPaidAmount.text = context!!.resources.getString(
            R.string.bcpLabelPayAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

    }

    private fun setAdapter() {
        bcpRecyclerView = v.findViewById(R.id.bcpRecyclerView)
        billingCustomerPaymentsAdapter = BillingCustomerPaymentsAdapter(paymentData)
        bcpRecyclerView.layoutManager = LinearLayoutManager(context)
        bcpRecyclerView.adapter = billingCustomerPaymentsAdapter

    }


    private fun initScrollListener() {
        bcpRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == paymentData.size - 1) {
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
                paymentData.add(null)
                billingCustomerPaymentsAdapter.notifyItemInserted(paymentData.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCustomerPayments(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCustomerPayments(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomerPayments(c)
        RetrofitClient.apiCall(call, this, "GetpaymentData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetpaymentData") {
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
            gson.fromJson(jsonObject.toString(), BillingCustomerPaymentModel::class.java)

        if (paymentData.size > 0) {
            paymentData.removeAt(paymentData.size - 1)
            billingCustomerPaymentsAdapter.notifyItemRemoved(paymentData.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        paymentData.addAll(model.data)

        billingCustomerPaymentsAdapter.notifyDataSetChanged()
        isLoading = false
    }


}