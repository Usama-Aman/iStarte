package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceToBindData
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceToBindModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import kotlin.collections.ArrayList

class VendorInvoiceToBindFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var favlbindRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var search: ImageView
    private lateinit var favltvbindAmount: TextView
    private lateinit var favltvbindTax: TextView
    private lateinit var invoiceBindAdapter: VendorInvoiceToBindAdapter
    private lateinit var api: Api

    var vendorInvoiceToBindData: MutableList<VendorInvoiceToBindData?> = ArrayList()
    private var vendorInvoiceToBindModel: VendorInvoiceToBindModel? = null
    private lateinit var newAssignedSpinner: CustomSearchableSpinner

    var idLineToSearch = ""
    var invoiceToSearch = ""
    var invoiceLabelToSearch = ""
    var yearToSearch = ""
    var monthToSearch = ""
    var productRefToSearch = ""
    var productDescToSearch = ""
    var amountToSearch = ""
    var taxToSearch = ""
    var thirdPartyToSearch = ""
    var countryIdToSearch = 0
    var vatIdToSearch = ""

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var parentAccountListStrings: MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_vendor_invoice_bind,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        setUpAdapter()
        initScrollListener()

        getParentAccounts()

        return v
    }

    private fun initViews() {
        newAssignedSpinner = v.findViewById(R.id.newAssignedSpinner)
        search = v.findViewById(R.id.search)
        favlbindRecyclerView = v.findViewById(R.id.favlbindRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        favltvbindAmount = v.findViewById(R.id.favltvbindAmount)
        favltvbindTax = v.findViewById(R.id.favltvbindTax)
        favltvbindAmount.text = context!!.resources.getString(
            R.string.favlbindLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        favltvbindTax.text = context!!.resources.getString(
            R.string.favlbindLabelTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            val searchDialog = VendorInvoiceToBindSearchDialog(
                context!!,
                (activity as MainActivity).countriesList().data,
                this
            )
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getVendorInvoiceToBind()
        }
    }

    private fun emptyData() {
        idLineToSearch = ""
        invoiceToSearch = ""
        invoiceLabelToSearch = ""
        yearToSearch = ""
        monthToSearch = ""
        productRefToSearch = ""
        productDescToSearch = ""
        amountToSearch = ""
        taxToSearch = ""
        thirdPartyToSearch = ""
        countryIdToSearch = 0
        vatIdToSearch = ""

        vendorInvoiceToBindData.clear()
        CURRENT_PAGE = 1
        LAST_PAGE = 1
    }

    private fun setUpAdapter() {
        invoiceBindAdapter = VendorInvoiceToBindAdapter(
            vendorInvoiceToBindData,
            this,
            vendorInvoiceToBindModel?.extra_data
        )
        favlbindRecyclerView.layoutManager = LinearLayoutManager(context)
        favlbindRecyclerView.adapter = invoiceBindAdapter
    }

    private fun initScrollListener() {
        favlbindRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == vendorInvoiceToBindData.size - 1) {
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
                vendorInvoiceToBindData.add(null)
                invoiceBindAdapter.notifyItemInserted(vendorInvoiceToBindData.size - 1)

                Handler().postDelayed({
                    CURRENT_PAGE++
                    getVendorInvoiceToBind()
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    fun getVendorInvoiceToBind() {
        val call = api.getVendorInvoiceToBind(
            CURRENT_PAGE,
            idLineToSearch,
            invoiceToSearch,
            invoiceLabelToSearch,
            monthToSearch,
            yearToSearch,
            productRefToSearch,
            productDescToSearch,
            amountToSearch,
            taxToSearch,
            thirdPartyToSearch,
            countryIdToSearch,
            vatIdToSearch
        )
        RetrofitClient.apiCall(call, this, "GetInvoiceToBind")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetInvoiceToBind" -> {
                AppUtils.dismissDialog()
                vendorInvoiceToBindModel =
                    Gson().fromJson(jsonObject.toString(), VendorInvoiceToBindModel::class.java)

                if (vendorInvoiceToBindData.size > 0) {
                    vendorInvoiceToBindData.removeAt(vendorInvoiceToBindData.size - 1)
                    invoiceBindAdapter.notifyItemRemoved(vendorInvoiceToBindData.size)
                }


                CURRENT_PAGE = vendorInvoiceToBindModel?.meta?.current_page!!
                LAST_PAGE = vendorInvoiceToBindModel?.meta?.last_page!!

                vendorInvoiceToBindData.addAll(vendorInvoiceToBindModel?.data!!)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (vendorInvoiceToBindData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    favlbindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    favlbindRecyclerView.visibility = View.VISIBLE
                }

                invoiceBindAdapter.notifyDataSetChanged()
                isLoading = false
            }
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                parentAccountList.add(
                    ParentAccountsData(
                        "",
                        0,
                        ""
                    )
                )
                parentAccountList.addAll(model.data)

                for (i in parentAccountList.indices) {
                    if (i > 0)
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
                    else
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
                }

                getVendorInvoiceToBind()
            }
            "PostVendorInvoiceBinding" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                emptyData()
                getVendorInvoiceToBind()
            }
        }
    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, message!!, false)
    }

    fun showAccountSpinner(position: Int) {
        var byUser = false

        newAssignedSpinner.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        newAssignedSpinner.setTitle(context!!.resources.getString(R.string.newAssignedAccountSpinnerTitle))

        val quantityAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, parentAccountListStrings
        )
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        newAssignedSpinner.adapter = quantityAdapter
        newAssignedSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {

                    vendorInvoiceToBindData[position]?.bind_account_id =
                        parentAccountList[i]?.id!!

                    vendorInvoiceToBindData[position]?.bind_account =
                        parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label

                    invoiceBindAdapter.notifyItemChanged(position)
                    AppUtils.hideKeyboard(activity!!)

                    if (byUser)
                        postVendorInvoiceBinding(vendorInvoiceToBindData[position])

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

//        for (x in parentAccountList.indices)
//            if (parentAccountList[x]?.id == vendorInvoiceToBindData[position]?.bind_account_id) {
//                newAssignedSpinner.setSelection(x)
        Handler().postDelayed({
            byUser = true
        }, (parentAccountList.size + 100).toLong())
//            }

        val motionEvent: MotionEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0
        )

        newAssignedSpinner.dispatchTouchEvent(motionEvent)
    }

    private fun postVendorInvoiceBinding(vendorInvoiceToBindData: VendorInvoiceToBindData?) {
        AppUtils.showDialog(context!!)
        val call = api.postVendorInvoiceBinding(
            vendorInvoiceToBindData?.bind_account_id!!,
            vendorInvoiceToBindData.id.toString()
        )
        RetrofitClient.apiCall(call, this, "PostVendorInvoiceBinding")
    }
}

