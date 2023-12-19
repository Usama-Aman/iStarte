package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

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
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBoundData
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBoundModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import kotlin.collections.ArrayList

class ExpenseBoundFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var faclboundRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var btnChangeBinding: TextView
    private lateinit var search: ImageView
    private lateinit var mainCheckbox: CheckBox
    private lateinit var faeltvboundAmount: TextView
    private lateinit var faeltvboundTax: TextView

    var expenseReportBoundData: MutableList<ExpenseReportBoundData?> = ArrayList()
    private lateinit var spinnerParentAccount: CustomSearchableSpinner

    private lateinit var expenseBoundAdapter: ExpenseBoundAdapter
    private lateinit var api: Api

    var idLineToSearch = ""
    var monthToSearch = ""
    var yearToSearch = ""
    var expenseReportToSearch = ""
    var descriptionToSearch = ""
    var amountToSearch = ""
    var taxToSearch = ""
    var accountToSearch = ""

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var parentAccountListStrings: MutableList<String> = ArrayList()
    private var parentAccountId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_expense_bound, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        setUpAdapter()
        initScrollListener()

        getParentAccounts()

        return v
    }

    private fun initViews() {
        btnChangeBinding = v.findViewById(R.id.btnChangeBinding)
        search = v.findViewById(R.id.search)
        spinnerParentAccount = v.findViewById(R.id.spinnerParentAccount)
        faclboundRecyclerView = v.findViewById(R.id.faclboundRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        mainCheckbox = v.findViewById(R.id.mainCheckbox)
        faeltvboundAmount = v.findViewById(R.id.faeltvboundAmount)
        faeltvboundTax = v.findViewById(R.id.faeltvboundTax)
        faeltvboundAmount.text = context!!.resources.getString(
            R.string.faelboundLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faeltvboundTax.text = context!!.resources.getString(
            R.string.faelboundLabelTaxRate,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog = ExpenseBoundSearchDialog(context!!, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getExpenseReportBound()
        }

        btnChangeBinding.setOnClickListener {
            changeBinding()
        }

        mainCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                for (i in expenseReportBoundData.indices)
                    expenseReportBoundData[i]?.isChecked = true
            else
                for (i in expenseReportBoundData.indices)
                    expenseReportBoundData[i]?.isChecked = false

            expenseBoundAdapter.notifyDataSetChanged()
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
        accountToSearch = ""
        mainCheckbox.isChecked = false

        expenseReportBoundData.clear()
        CURRENT_PAGE = 1
        LAST_PAGE = 1
    }

    private fun setUpAdapter() {
        expenseBoundAdapter = ExpenseBoundAdapter(expenseReportBoundData, this)
        faclboundRecyclerView.layoutManager = LinearLayoutManager(context)
        faclboundRecyclerView.adapter = expenseBoundAdapter
    }

    private fun initScrollListener() {
        faclboundRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == expenseReportBoundData.size - 1) {
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
                expenseReportBoundData.add(null)
                expenseBoundAdapter.notifyItemInserted(expenseReportBoundData.size - 1)
                Handler().postDelayed({
                    CURRENT_PAGE++
                    getExpenseReportBound()
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

    fun getExpenseReportBound() {
        val call = api.getExpenseReportBound(
            CURRENT_PAGE, idLineToSearch, monthToSearch, yearToSearch,
            expenseReportToSearch, descriptionToSearch, amountToSearch, taxToSearch, accountToSearch
        )
        RetrofitClient.apiCall(call, this, "GetExpenseBound")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetExpenseBound" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), ExpenseReportBoundModel::class.java)

                if (expenseReportBoundData.size > 0) {
                    expenseReportBoundData.removeAt(expenseReportBoundData.size - 1)
                    expenseBoundAdapter.notifyItemRemoved(expenseReportBoundData.size)
                }


                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                expenseReportBoundData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (expenseReportBoundData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faclboundRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    faclboundRecyclerView.visibility = View.VISIBLE
                }

                expenseBoundAdapter.notifyDataSetChanged()
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
                getExpenseReportBound()
            }
            "PostExpenseReportBinding" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                mainCheckbox.isChecked = false

                emptyData()
                getExpenseReportBound()
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
        for (i in expenseReportBoundData.indices)
            if (expenseReportBoundData[i]?.isChecked!!)
                checkedAccounts.add(expenseReportBoundData[i]?.id.toString())

        if (checkedAccounts.size > 0) {
            AppUtils.showDialog(context!!)
            val call = api.postExpenseReportBinding(
                parentAccountId, TextUtils.join(",", checkedAccounts)
            )
            RetrofitClient.apiCall(call, this, "PostExpenseReportBinding")
        } else {
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.faelboundErrorSelectExpenseToChangeBinding),
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