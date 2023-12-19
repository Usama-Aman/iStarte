package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding

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
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.CustomerInvoiceToBindData
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.CustomerInvoiceToBindModel
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

class InvoiceToBindFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1

    private lateinit var v: View
    private lateinit var faclbindRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var search: ImageView
    private lateinit var facltvbindAmount: TextView
    private lateinit var facltvbindTax: TextView

    private lateinit var invoiceBindAdapter: InvoiceToBindAdapter
    private lateinit var api: Api

    var customerInvoiceToBindData: MutableList<CustomerInvoiceToBindData?> = ArrayList()
    private var customerInvoiceToBindModel: CustomerInvoiceToBindModel? = null
    private lateinit var newAssignedSpinner: CustomSearchableSpinner

    var idLineToSearch = ""
    var invoiceToSearch = ""
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
        v = inflater.inflate(R.layout.fragment_financial_accounting_invoice_bind, container, false)
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
        faclbindRecyclerView = v.findViewById(R.id.faclbindRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        facltvbindAmount = v.findViewById(R.id.facltvbindAmount)
        facltvbindTax = v.findViewById(R.id.facltvbindTax)
        facltvbindAmount.text = context!!.resources.getString(
            R.string.faclbindLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        facltvbindTax.text = context!!.resources.getString(
            R.string.faclbindLabelTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog = InvoiceToBindSearchDialog(
                context!!,
                (activity as MainActivity).countriesList().data,
                this
            )
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getCustomerInvoiceToBind()
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

        customerInvoiceToBindData.clear()
        CURRENT_PAGE = 1
        LAST_PAGE = 1
    }

    private fun setUpAdapter() {
        invoiceBindAdapter = InvoiceToBindAdapter(
            customerInvoiceToBindData,
            this,
            customerInvoiceToBindModel?.extra_data
        )
        faclbindRecyclerView.layoutManager = LinearLayoutManager(context)
        faclbindRecyclerView.adapter = invoiceBindAdapter
    }

    private fun initScrollListener() {
        faclbindRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customerInvoiceToBindData.size - 1) {
                    recyclerView.post {
                        try {
                            if (CURRENT_PAGE < LAST_PAGE) {
                                Handler().postDelayed({
                                    CURRENT_PAGE++
                                    getCustomerInvoiceToBind()
                                }, 1000)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    fun getCustomerInvoiceToBind() {
        val call = api.getCustomerInvoiceToBind(
            CURRENT_PAGE, idLineToSearch, invoiceToSearch, monthToSearch, yearToSearch,
            productRefToSearch, productDescToSearch, amountToSearch, taxToSearch,
            thirdPartyToSearch, countryIdToSearch, vatIdToSearch
        )
        RetrofitClient.apiCall(call, this, "GetInvoiceToBind")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetInvoiceToBind" -> {
                AppUtils.dismissDialog()
                customerInvoiceToBindModel =
                    Gson().fromJson(jsonObject.toString(), CustomerInvoiceToBindModel::class.java)

                if (customerInvoiceToBindData.size > 0) {
                    customerInvoiceToBindData.removeAt(customerInvoiceToBindData.size - 1)
                    invoiceBindAdapter.notifyItemRemoved(customerInvoiceToBindData.size)
                }


                CURRENT_PAGE = customerInvoiceToBindModel?.meta?.current_page!!
                LAST_PAGE = customerInvoiceToBindModel?.meta?.last_page!!

                customerInvoiceToBindData.addAll(customerInvoiceToBindModel?.data!!)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (customerInvoiceToBindData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faclbindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    faclbindRecyclerView.visibility = View.VISIBLE
                }

                if (CURRENT_PAGE < LAST_PAGE) {
                    customerInvoiceToBindData.add(null)
                    invoiceBindAdapter.notifyItemInserted(customerInvoiceToBindData.size - 1)
                }

                invoiceBindAdapter.notifyDataSetChanged()
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

                getCustomerInvoiceToBind()
            }
            "PostCustomerInvoiceBinding" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                emptyData()
                getCustomerInvoiceToBind()
            }
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

                    customerInvoiceToBindData[position]?.chart_account_id =
                        parentAccountList[i]?.id!!

                    customerInvoiceToBindData[position]?.bind_account =
                        parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label

                    invoiceBindAdapter.notifyItemChanged(position)
                    AppUtils.hideKeyboard(activity!!)

                    if (byUser)
                        postCustomerInvoiceBinding(customerInvoiceToBindData[position])
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

//        for (x in parentAccountList.indices)
//            if (parentAccountList[x]?.id == customerInvoiceToBindData[position]?.chart_account_id) {
//                newAssignedSpinner.setSelection(x)
        Handler().postDelayed({
            byUser = true
        }, (parentAccountList.size + 100).toLong())
//            }

        val motionEvent: MotionEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP, 0f, 0f, 1
        )

        newAssignedSpinner.onTouch(v, motionEvent)

//        newAssignedSpinner.dispatchTouchEvent(motionEvent)
    }

    private fun postCustomerInvoiceBinding(customerInvoiceToBindData: CustomerInvoiceToBindData?) {
        AppUtils.showDialog(context!!)
        val call = api.postCustomerInvoiceBinding(
            customerInvoiceToBindData?.chart_account_id!!,
            customerInvoiceToBindData.id.toString()
        )
        RetrofitClient.apiCall(call, this, "PostCustomerInvoiceBinding")
    }


}