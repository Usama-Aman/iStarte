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
import com.minbio.erp.financial_management.biling_payments.adapters.BillingVendorInvoiceAdapter
import com.minbio.erp.financial_management.biling_payments.models.BillingVendorInvoiceData
import com.minbio.erp.financial_management.biling_payments.models.BillingVendorInvoiceModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class BillingVendorInvoiceListFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var addEntry: ImageView
    private lateinit var bvilRecyclerView: RecyclerView
    private lateinit var billingVendorInvoiceAdapter: BillingVendorInvoiceAdapter
    private lateinit var bviltvDebit: TextView
    private lateinit var bviltvCredit: TextView
    private lateinit var bviltvOverdraft: TextView
    private lateinit var bviltvTotalPayAmount: TextView

    private var invoiceListData: MutableList<BillingVendorInvoiceData?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_vendor_invoice_list, container, false)

        initViews()
        setAdapter()
        initScrollListener()
        getInvoiceData(0)

        return v
    }

    private fun initViews() {
        bviltvDebit = v.findViewById(R.id.bviltvDebit)
        bviltvCredit = v.findViewById(R.id.bviltvCredit)
        bviltvOverdraft = v.findViewById(R.id.bviltvOverdraft)
        bviltvTotalPayAmount = v.findViewById(R.id.bviltvTotalPayAmount)

        bviltvDebit.text = context!!.resources.getString(
            R.string.bvilLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bviltvCredit.text = context!!.resources.getString(
            R.string.bvilLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bviltvOverdraft.text = context!!.resources.getString(
            R.string.bvilLabelOverdue,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bviltvTotalPayAmount.text = context!!.resources.getString(
            R.string.bvilLabelTotalPayableAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

    }

    private fun setAdapter() {
        bvilRecyclerView = v.findViewById(R.id.bvilRecyclerView)
        billingVendorInvoiceAdapter = BillingVendorInvoiceAdapter(invoiceListData)
        bvilRecyclerView.layoutManager = LinearLayoutManager(context)
        bvilRecyclerView.adapter = billingVendorInvoiceAdapter

    }


    private fun initScrollListener() {
        bvilRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == invoiceListData.size - 1) {
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
                invoiceListData.add(null)
                billingVendorInvoiceAdapter.notifyItemInserted(invoiceListData.size - 1)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getInvoiceData(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInvoiceData(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getVendorInvoiceList("all", c)
        RetrofitClient.apiCall(call, this, "GetInvoiceListData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetInvoiceListData") {
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
            gson.fromJson(jsonObject.toString(), BillingVendorInvoiceModel::class.java)

        if (invoiceListData.size > 0) {
            invoiceListData.removeAt(invoiceListData.size - 1)
            billingVendorInvoiceAdapter.notifyItemRemoved(invoiceListData.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        invoiceListData.addAll(model.data)

        billingVendorInvoiceAdapter.notifyDataSetChanged()
        isLoading = false
    }


}