package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBoundData
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBoundModel
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

class VendorInvoiceBoundFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var favlboundRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var btnChangeBinding: TextView
    private lateinit var search: ImageView
    private lateinit var mainCheckbox: CheckBox
    private lateinit var favltvboundAmount: TextView
    private lateinit var favltvboundTax: TextView

    private lateinit var invoiceBoundAdapter: VendorInvoiceBoundAdapter
    private lateinit var api: Api

    var vendorInvoiceBoundData: MutableList<VendorInvoiceBoundData?> = ArrayList()
    private lateinit var spinnerParentAccount: CustomSearchableSpinner

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
    var accountNumberToSearch = ""

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var parentAccountListStrings: MutableList<String> = ArrayList()
    private var parentAccountId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_vendor_invoice_bound,
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
        mainCheckbox = v.findViewById(R.id.mainCheckbox)
        spinnerParentAccount = v.findViewById(R.id.spinnerParentAccount)
        btnChangeBinding = v.findViewById(R.id.btnChangeBinding)
        search = v.findViewById(R.id.search)
        favlboundRecyclerView = v.findViewById(R.id.favlboundRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        favltvboundAmount = v.findViewById(R.id.favltvboundAmount)
        favltvboundTax = v.findViewById(R.id.favltvboundTax)
        favltvboundAmount.text = context!!.resources.getString(
            R.string.favlboundLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        favltvboundTax.text = context!!.resources.getString(
            R.string.favlboundLabelTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog =
                VendorInvoiceBoundSearchDialog(
                    context!!, (activity as MainActivity).countriesList().data, this
                )
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getVendorInvoiceBound()
        }

        btnChangeBinding.setOnClickListener {
            changeBinding()
        }

        mainCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                for (i in vendorInvoiceBoundData.indices)
                    vendorInvoiceBoundData[i]?.isChecked = true
            else
                for (i in vendorInvoiceBoundData.indices)
                    vendorInvoiceBoundData[i]?.isChecked = false

            invoiceBoundAdapter.notifyDataSetChanged()
        }
    }

    private fun emptyData() {
        idLineToSearch = ""
        invoiceToSearch = ""
        yearToSearch = ""
        monthToSearch = ""
        productRefToSearch = ""
        productDescToSearch = ""
        amountToSearch = ""
        taxToSearch = ""
        thirdPartyToSearch = ""
        countryIdToSearch = 0
        vatIdToSearch = ""
        accountNumberToSearch = ""
        mainCheckbox.isChecked = false

        vendorInvoiceBoundData.clear()
        CURRENT_PAGE = 1
        LAST_PAGE=1
    }

    private fun setUpAdapter() {
        invoiceBoundAdapter = VendorInvoiceBoundAdapter(vendorInvoiceBoundData, this)
        favlboundRecyclerView.layoutManager = LinearLayoutManager(context)
        favlboundRecyclerView.adapter = invoiceBoundAdapter
    }

    private fun initScrollListener() {
        favlboundRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == vendorInvoiceBoundData.size - 1) {
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
                vendorInvoiceBoundData.add(null)
                invoiceBoundAdapter.notifyItemInserted(vendorInvoiceBoundData.size - 1)

                Handler().postDelayed({
                    CURRENT_PAGE++
                    getVendorInvoiceBound()
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

    fun getVendorInvoiceBound() {
        val call = api.getVendorInvoiceBound(
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
            vatIdToSearch,
            accountNumberToSearch
        )
        RetrofitClient.apiCall(call, this, "GetInvoiceBound")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetInvoiceBound" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), VendorInvoiceBoundModel::class.java)

                if (vendorInvoiceBoundData.size > 0) {
                    vendorInvoiceBoundData.removeAt(vendorInvoiceBoundData.size - 1)
                    invoiceBoundAdapter.notifyItemRemoved(vendorInvoiceBoundData.size)
                }


                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                vendorInvoiceBoundData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (vendorInvoiceBoundData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    favlboundRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    favlboundRecyclerView.visibility = View.VISIBLE
                }

                invoiceBoundAdapter.notifyDataSetChanged()
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

                setUpParentAccountSpinner()
                getVendorInvoiceBound()
            }
            "PostVendorInvoiceBinding" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                mainCheckbox.isChecked = false

                emptyData()
//                AppUtils.showDialog(context!!)
                getVendorInvoiceBound()

            }
        }
    }

    private fun setUpParentAccountSpinner() {
        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, parentAccountListStrings)
        spinnerParentAccount.adapter = positionAdapter
        spinnerParentAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerParentAccount.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinnerParentAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parentAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

    }

    private fun changeBinding() {

        val checkedAccounts: MutableList<String> = ArrayList()
        for (i in vendorInvoiceBoundData.indices)
            if (vendorInvoiceBoundData[i]?.isChecked!!)
                checkedAccounts.add(vendorInvoiceBoundData[i]?.id.toString())

        if (checkedAccounts.size > 0) {
            AppUtils.showDialog(context!!)
            val call = api.postVendorInvoiceBinding(
                parentAccountId, TextUtils.join(",", checkedAccounts)
            )
            RetrofitClient.apiCall(call, this, "PostVendorInvoiceBinding")
        } else {
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.favlboundErrorSelectInvoiceToChangeBinding),
                false
            )
        }
    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing =false
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing =false
        AppUtils.showToast(activity, message!!, false)
    }


}