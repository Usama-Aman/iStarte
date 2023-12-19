package com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts

import android.os.Bundle
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
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.models.ExpenseReportAccountsData
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.models.ExpenseReportAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ExpenseReportAccountsFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var addExpenseReport: ImageView
    private lateinit var pageTitle: TextView

    private lateinit var expenseReportAccountsAdapter: ExpenseReportAccountsAdapter
    private lateinit var faerRecyclerView: RecyclerView
    private lateinit var codeSearchValue: EditText
    private lateinit var search: ImageView
    private var countryId: Int = 0
    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var api: Api
    private var expenseReportData: MutableList<ExpenseReportAccountsData?> = ArrayList()
    private var itemPosition = -1

    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_expense_report,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        setUpAdapter()
        initScrollListener()

        expenseReportData.clear()
        AppUtils.showDialog(context!!)
        getExpenseReportAccountData()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            expenseReportData.clear()
            getExpenseReportAccountData()
        }
        pageTitle = v.findViewById(R.id.article_code_number)
        pageTitle.text = context!!.resources.getString(R.string.faerPageTitle)

        codeSearchValue = v.findViewById(R.id.codeSearchValue)
        search = v.findViewById(R.id.search)
        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            expenseReportData.clear()
            AppUtils.showDialog(context!!)
            getExpenseReportAccountData()
        }

        faerRecyclerView = v.findViewById(R.id.faerRecyclerView)
        addExpenseReport = v.findViewById(R.id.addExpenseReport)
        addExpenseReport.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddExpenseReportAccountFragment()
            )
        }
    }

    private fun setUpAdapter() {
        expenseReportAccountsAdapter = ExpenseReportAccountsAdapter(this, expenseReportData)
        faerRecyclerView.layoutManager = LinearLayoutManager(context!!)
        faerRecyclerView.adapter = expenseReportAccountsAdapter
    }

    private fun initScrollListener() {
        faerRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == expenseReportData.size - 1) {
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
                expenseReportData.add(null)
                expenseReportAccountsAdapter.notifyItemInserted(expenseReportData.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getExpenseReportAccountData()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getExpenseReportAccountData() {
        val call = api.getExpenseReportData(codeSearchValue.text.toString(), CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetExpenseReport")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetExpenseReport" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), ExpenseReportAccountsModel::class.java)

                if (expenseReportData.size > 0) {
                    expenseReportData.removeAt(expenseReportData.size - 1)
                    expenseReportAccountsAdapter.notifyItemRemoved(expenseReportData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                expenseReportData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (expenseReportData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faerRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    faerRecyclerView.visibility = View.VISIBLE
                }

                expenseReportAccountsAdapter.notifyDataSetChanged()
                isLoading = false
            }
            "DeleteExpenseReport" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                expenseReportData.removeAt(itemPosition)
                expenseReportAccountsAdapter.notifyDataSetChanged()
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


    fun launchEditFragment(fragment: Fragment) {
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    fun deleteExpenseReport(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteExpenseReportAccount(id)
        RetrofitClient.apiCall(call, this, "DeleteExpenseReport")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position

        val call = api.updateExpenseReportStatus(id, expenseReportData[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
    }


}