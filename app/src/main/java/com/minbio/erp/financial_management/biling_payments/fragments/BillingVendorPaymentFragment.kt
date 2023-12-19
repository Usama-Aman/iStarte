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
import com.minbio.erp.financial_management.biling_payments.adapters.BillingVendorPaymentAdapter
import com.minbio.erp.financial_management.biling_payments.models.BillingVendorPaymentData
import com.minbio.erp.financial_management.biling_payments.models.BillingVendorPaymentModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class BillingVendorPaymentFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var addEntry: ImageView
    private lateinit var bvpRecyclerView: RecyclerView
    private lateinit var billingVendorPaymentAdapter: BillingVendorPaymentAdapter
    private lateinit var bvptvPayAmount: TextView

    private var paymentData: MutableList<BillingVendorPaymentData?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_vendor_payments, container, false)

        initViews()
        setAdapter()
        initScrollListener()
        getVendorPayments(0)

        return v
    }

    private fun initViews() {
        bvptvPayAmount = v.findViewById(R.id.bvptvPayAmount)
        bvptvPayAmount.text = context!!.resources.getString(
            R.string.bvpLabelPayAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

    }

    private fun setAdapter() {
        bvpRecyclerView = v.findViewById(R.id.bvpRecyclerView)
        billingVendorPaymentAdapter = BillingVendorPaymentAdapter(paymentData)
        bvpRecyclerView.layoutManager = LinearLayoutManager(context)
        bvpRecyclerView.adapter = billingVendorPaymentAdapter

    }


    private fun initScrollListener() {
        bvpRecyclerView.addOnScrollListener(object :
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
                billingVendorPaymentAdapter.notifyItemInserted(paymentData.size - 1)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getVendorPayments(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getVendorPayments(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getVendorPayments(c)
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
            gson.fromJson(jsonObject.toString(), BillingVendorPaymentModel::class.java)

        if (paymentData.size > 0) {
            paymentData.removeAt(paymentData.size - 1)
            billingVendorPaymentAdapter.notifyItemRemoved(paymentData.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        paymentData.addAll(model.data)

        billingVendorPaymentAdapter.notifyDataSetChanged()
        isLoading = false
    }


}