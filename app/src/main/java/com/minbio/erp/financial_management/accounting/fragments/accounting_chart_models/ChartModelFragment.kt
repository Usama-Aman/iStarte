package com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models

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
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModel
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModelData
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ChartModelFragment : Fragment(), ResponseCallBack {

    private var countryId: Int = 0
    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var addAccountingJournal: ImageView
    private lateinit var search: ImageView
    private lateinit var pageTitle: TextView
    private lateinit var spinnerChartModelCountry: CustomSearchableSpinner

    private lateinit var chartModelAdapter: ChartModelAdapter
    private lateinit var facamRecyclerView: RecyclerView
    private lateinit var countriesModel: CountriesModel

    private var chartModelData: MutableList<AccountChartModelData?> = ArrayList()
    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private var itemPosition = -1

    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_chart_model, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()
        setUpCountrySpinner()

        chartModelData.clear()
        AppUtils.showDialog(context!!)
        getAccountChartModel()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            chartModelData.clear()
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            getAccountChartModel()
        }
        spinnerChartModelCountry = v.findViewById(R.id.spinnerChartModelCountry)
        pageTitle = v.findViewById(R.id.article_code_number)
        pageTitle.text = context!!.resources.getString(R.string.facamPageTitle)

        facamRecyclerView = v.findViewById(R.id.facamRecyclerView)
        search = v.findViewById(R.id.search)
        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1
            chartModelData.clear()
            AppUtils.showDialog(context!!)
            getAccountChartModel()
        }

        addAccountingJournal = v.findViewById(R.id.addAccountingJournal)
        addAccountingJournal.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddChartModelFragment()
            )
        }

    }

    private fun setUpAdapter() {
        chartModelAdapter = ChartModelAdapter(chartModelData, this)
        facamRecyclerView.layoutManager = LinearLayoutManager(context!!)
        facamRecyclerView.adapter = chartModelAdapter
    }

    private fun initScrollListener() {
        facamRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == chartModelData.size - 1) {
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
                chartModelData.add(null)
                chartModelAdapter.notifyItemInserted(chartModelData.size - 1)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getAccountChartModel()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpCountrySpinner() {
        countriesList.clear()
        countriesModel = (activity as MainActivity).countriesList()

        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(countriesModel.data)

        val strings = ArrayList<String>()
        for (i in countriesList) {
            strings.add(i!!.name)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerChartModelCountry.adapter = positionAdapter
        spinnerChartModelCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerChartModelCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
        spinnerChartModelCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countryId = countriesList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun getAccountChartModel() {
        val call = api.getAccountingChartModels(countryId, CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetChartModels")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetChartModels" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), AccountChartModel::class.java)

                if (chartModelData.size > 0) {
                    chartModelData.removeAt(chartModelData.size - 1)
                    chartModelAdapter.notifyItemRemoved(chartModelData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                chartModelData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (chartModelData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    facamRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    facamRecyclerView.visibility = View.VISIBLE
                }

                chartModelAdapter.notifyDataSetChanged()
                isLoading = false
            }
            "DeleteChartModel" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                chartModelData.removeAt(itemPosition)
                chartModelAdapter.notifyDataSetChanged()
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

    fun deleteChartModel(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteAccountChartModel(id)
        RetrofitClient.apiCall(call, this, "DeleteChartModel")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position
        val call = api.updateChartModelStatus(id, chartModelData[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
    }


}