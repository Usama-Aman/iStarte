package com.minbio.erp.financial_management.accounting.fragments.chart_accounts

import android.os.Bundle
import android.view.LayoutInflater
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
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModel
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModelData
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models.ChartAccountData
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models.ChartAccountModel
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ChartAccountsFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false
    private lateinit var api: Api

    private lateinit var v: View
    private lateinit var addAccountingJournal: ImageView
    private lateinit var accountSearch: ImageView
    private lateinit var search: ImageView
    private lateinit var pageTitle: TextView
    private lateinit var chartAccountsAdapter: ChartAccountsAdapter

    private lateinit var facaRecyclerView: RecyclerView
    private var chartModelData: MutableList<AccountChartModelData?> = ArrayList()
    var chartAccounts: MutableList<ChartAccountData?> = ArrayList()

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private lateinit var spinnerChartModelCountry: CustomSearchableSpinner

    var accountModelId: Int = 0
    var parentAccountId: Int = 0
    var accountGroupId: String = ""
    var shortLabel: String = ""
    var label: String = ""
    var accountNumber: String = ""

    var chartAccountModel: String = ""
    private var itemPosition: Int = -1

    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_chart_accounts, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()

        chartModelData.clear()
        chartAccounts.clear()
        parentAccountList.clear()

        getAccountChartModel()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            accountModelId = 0
            parentAccountId = 0
            accountGroupId = ""
            shortLabel = ""
            label = ""
            accountNumber = ""
            chartAccounts.clear()
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            getCHartAccounts()
        }
        search = v.findViewById(R.id.search)
        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            chartAccounts.clear()
            label = ""
            shortLabel = ""
            accountNumber = ""
            parentAccountId = 0
            accountGroupId = ""

            AppUtils.showDialog(context!!)
            getCHartAccounts()
        }
        spinnerChartModelCountry = v.findViewById(R.id.spinnerChartModelCountry)
        pageTitle = v.findViewById(R.id.article_code_number)
        pageTitle.text = context!!.resources.getString(R.string.facaPageTitle)

        facaRecyclerView = v.findViewById(R.id.facaRecyclerView)


        addAccountingJournal = v.findViewById(R.id.addAccountingJournal)
        accountSearch = v.findViewById(R.id.accountSearch)
        addAccountingJournal.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("chartAccountModel", chartAccountModel)
            bundle.putInt("chartAccountModelId", accountModelId)
            val fragment = AddChartAccountFragment()
            fragment.arguments = bundle
            (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
        }

        accountSearch.setOnClickListener {
            val searchChartDialog = ChartAccountSearchDialog(context!!, this, parentAccountList)
            searchChartDialog.setOwnerActivity(activity!!)
            searchChartDialog.show()
        }

    }


    private fun setUpAdapter() {
        chartAccountsAdapter = ChartAccountsAdapter(chartAccounts, this)
        facaRecyclerView.layoutManager = LinearLayoutManager(context!!)
        facaRecyclerView.adapter = chartAccountsAdapter
    }

    private fun initScrollListener() {
        facaRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == chartAccounts.size - 1) {
                        recyclerView.post {
                            if (CURRENT_PAGE != LAST_PAGE)
                                loadMore()
                        }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            if (CURRENT_PAGE < LAST_PAGE) {
                chartAccounts.add(null)
                chartAccountsAdapter.notifyItemInserted(chartAccounts.size - 1)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getCHartAccounts()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAccountChartModel() {
        AppUtils.showDialog(context!!)
        val call = api.getAllAccountingChartModels()
        RetrofitClient.apiCall(call, this, "GetChartModels")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetChartModels" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), AccountChartModel::class.java)
                chartModelData.addAll(model.data)

                setUpModelSpinner()
            }
            "GetAccounts" -> {
                AppUtils.dismissDialog()

                val model =
                    Gson().fromJson(jsonObject.toString(), ChartAccountModel::class.java)

                if (chartAccounts.size > 0) {
                    chartAccounts.removeAt(chartAccounts.size - 1)
                    chartAccountsAdapter.notifyItemRemoved(chartAccounts.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                chartAccounts.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (chartAccounts.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    facaRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    facaRecyclerView.visibility = View.VISIBLE
                }

                chartAccountsAdapter.notifyDataSetChanged()
                isLoading = false
            }
            "DeleteChartAccount" -> {
                AppUtils.dismissDialog()

                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                chartAccounts.removeAt(itemPosition)
                chartAccountsAdapter.notifyDataSetChanged()
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

                getCHartAccounts()
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

    private fun setUpModelSpinner() {

        val strings = ArrayList<String>()
        for (i in chartModelData) {
            strings.add(i?.chart_accounts_model!!)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerChartModelCountry.adapter = positionAdapter
        spinnerChartModelCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerChartModelCountry.setTitle(resources.getString(R.string.accountModelSpinnerTitle))
        spinnerChartModelCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    accountModelId = chartModelData[position]?.id!!
                    chartAccountModel = chartModelData[position]?.chart_accounts_model!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

        getParentAccounts()
    }

    private fun getParentAccounts() {
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    fun getCHartAccounts() {

        val call = api.getChartAccounts(
            CURRENT_PAGE,
            accountModelId,
            label,
            shortLabel,
            accountNumber,
            parentAccountId,
            accountGroupId
        )
        RetrofitClient.apiCall(call, this, "GetAccounts")
    }


    fun launchEditFragment(fragment: Fragment) {


        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    fun deleteChartAccount(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteAccountChart(id)
        RetrofitClient.apiCall(call, this, "DeleteChartAccount")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position

        val call = api.updateChartAccountStatus(id, chartAccounts[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
    }

}

