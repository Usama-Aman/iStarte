package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

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
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportToBindData
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportToBindModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class ExpenseToBindFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false


    private lateinit var v: View
    private lateinit var faelbindRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var search: ImageView
    private lateinit var faeltvbindAmount: TextView
    private lateinit var faeltvbindTax: TextView
    private lateinit var expenseBindAdapter: ExpenseToBindAdapter

    var expenseReportToBindData: MutableList<ExpenseReportToBindData?> = ArrayList()
    private lateinit var newAssignedSpinner: CustomSearchableSpinner

    var idLineToSearch = ""
    var monthToSearch = ""
    var yearToSearch = ""
    var expenseReportToSearch = ""
    var descriptionToSearch = ""
    var amountToSearch = ""
    var taxToSearch = ""

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var parentAccountListStrings: MutableList<String> = ArrayList()
    private lateinit var api: Api

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_expense_bind, container, false)
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
        faelbindRecyclerView = v.findViewById(R.id.faelbindRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        faeltvbindAmount = v.findViewById(R.id.faeltvbindAmount)
        faeltvbindTax = v.findViewById(R.id.faeltvbindTax)
        faeltvbindAmount.text = context!!.resources.getString(
            R.string.faelbindLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faeltvbindTax.text = context!!.resources.getString(
            R.string.faelbindLabelTaxRate,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog = ExpenseToBindSearchDialog(context!!, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getExpenseReportToBind()
        }
    }

    private fun emptyData() {
        idLineToSearch = ""
        monthToSearch = ""
        yearToSearch = ""
        expenseReportToSearch = ""
        descriptionToSearch = ""
        amountToSearch = ""
        taxToSearch = ""

        CURRENT_PAGE = 1
        LAST_PAGE = 1
        expenseReportToBindData.clear()
    }

    private fun setUpAdapter() {
        expenseBindAdapter = ExpenseToBindAdapter(expenseReportToBindData, this)
        faelbindRecyclerView.layoutManager = LinearLayoutManager(context)
        faelbindRecyclerView.adapter = expenseBindAdapter
    }

    private fun initScrollListener() {
        faelbindRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == expenseReportToBindData.size - 1) {
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
                expenseReportToBindData.add(null)
                expenseBindAdapter.notifyItemInserted(expenseReportToBindData.size - 1)
                Handler().postDelayed({
                    CURRENT_PAGE++
                    getExpenseReportToBind()
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

    fun getExpenseReportToBind() {
        val call = api.getExpenseReportToBind(
            CURRENT_PAGE, idLineToSearch, monthToSearch, yearToSearch,
            expenseReportToSearch, descriptionToSearch, amountToSearch, taxToSearch
        )
        RetrofitClient.apiCall(call, this, "GetExpenseReportToBind")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetExpenseReportToBind" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), ExpenseReportToBindModel::class.java)

                if (expenseReportToBindData.size > 0) {
                    expenseReportToBindData.removeAt(expenseReportToBindData.size - 1)
                    expenseBindAdapter.notifyItemRemoved(expenseReportToBindData.size)
                }


                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                expenseReportToBindData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (expenseReportToBindData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faelbindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    faelbindRecyclerView.visibility = View.VISIBLE
                }

                expenseBindAdapter.notifyDataSetChanged()
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

                getExpenseReportToBind()
            }
            "PostExpenseReportBinding" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                emptyData()
                getExpenseReportToBind()
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

                    expenseReportToBindData[position]?.chart_account_id =
                        parentAccountList[i]?.id!!

                    expenseReportToBindData[position]?.bind_account =
                        parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label!!

                    expenseBindAdapter.notifyItemChanged(position)
                    AppUtils.hideKeyboard(activity!!)

                    if (byUser)
                        postExpenseReportBinding(expenseReportToBindData[position])

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

//        for (x in parentAccountList.indices)
//            if (parentAccountList[x]?.id == expenseReportToBindData[position]?.chart_account_id) {
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

    private fun postExpenseReportBinding(expenseReportToBindData: ExpenseReportToBindData?) {
        AppUtils.showDialog(context!!)
        val call = api.postExpenseReportBinding(
            expenseReportToBindData?.chart_account_id!!,
            expenseReportToBindData.id.toString()
        )
        RetrofitClient.apiCall(call, this, "PostExpenseReportBinding")
    }
}