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
import com.minbio.erp.financial_management.biling_payments.adapters.BillingCustomerInvoiceAdapter
import com.minbio.erp.financial_management.biling_payments.models.BillingCustomerInvoiceData
import com.minbio.erp.financial_management.biling_payments.models.BillingCustomerInvoiceModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class BillingCustomerInvoiceListFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var addEntry: ImageView
    private lateinit var bcilRecycler: RecyclerView
    private lateinit var billingCustomerInvoiceAdapter: BillingCustomerInvoiceAdapter
    private lateinit var bciltvDebit: TextView
    private lateinit var bciltvCredit: TextView
    private lateinit var bciltvOverdraft: TextView
    private lateinit var bciltvTotalPayAmount: TextView

    private var invoiceListData: MutableList<BillingCustomerInvoiceData?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_customer_invoice_list, container, false)

        initViews()
        setAdapter()
        initScrollListener()
        getInvoiceData(0)

        return v
    }

    private fun initViews() {
        bciltvDebit = v.findViewById(R.id.bciltvDebit)
        bciltvCredit = v.findViewById(R.id.bciltvCredit)
        bciltvOverdraft = v.findViewById(R.id.bciltvOverdraft)
        bciltvTotalPayAmount = v.findViewById(R.id.bciltvTotalPayAmount)

        bciltvDebit.text = context!!.resources.getString(
            R.string.bcilLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bciltvCredit.text = context!!.resources.getString(
            R.string.bcilLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bciltvOverdraft.text = context!!.resources.getString(
            R.string.bcilLabelOverdue,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bciltvTotalPayAmount.text = context!!.resources.getString(
            R.string.bcilLabelTotalPayableAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


    }

    private fun setAdapter() {
        bcilRecycler = v.findViewById(R.id.bcilRecyclerView)
        billingCustomerInvoiceAdapter = BillingCustomerInvoiceAdapter(invoiceListData)
        bcilRecycler.layoutManager = LinearLayoutManager(context)
        bcilRecycler.adapter = billingCustomerInvoiceAdapter

    }


    private fun initScrollListener() {
        bcilRecycler.addOnScrollListener(object :
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
                billingCustomerInvoiceAdapter.notifyItemInserted(invoiceListData.size - 1)
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
        val call = api.getCustomerInvoiceList("all", c)
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
            gson.fromJson(jsonObject.toString(), BillingCustomerInvoiceModel::class.java)

        if (invoiceListData.size > 0) {
            invoiceListData.removeAt(invoiceListData.size - 1)
            billingCustomerInvoiceAdapter.notifyItemRemoved(invoiceListData.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        invoiceListData.addAll(model.data)

        billingCustomerInvoiceAdapter.notifyDataSetChanged()
        isLoading = false
    }


}