package com.minbio.erp.financial_management.accounting.fragments.accounting_journal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models.AccountingJournalData
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models.AccountingJournalModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AccountingJournalFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var addAccountingJournal: ImageView
    private lateinit var pageTitle: TextView

    private lateinit var journalAdapter: AccountingJournalAdapter
    private lateinit var fajRecyclerView: RecyclerView

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private var itemPosition = -1

    private var journalDataList: MutableList<AccountingJournalData?> = ArrayList()
    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_journal, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()
        journalDataList.clear()

        AppUtils.showDialog(context!!)
        getAccountingJournals()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            journalDataList.clear()
            getAccountingJournals()
        }
        pageTitle = v.findViewById(R.id.article_code_number)
        pageTitle.text = context!!.resources.getString(R.string.fajPageTitle)

        fajRecyclerView = v.findViewById(R.id.fajRecyclerView)

        addAccountingJournal = v.findViewById(R.id.addAccountingJournal)
        addAccountingJournal.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddAccountingJournalFragment()
            )
        }

    }

    private fun setUpAdapter() {
        journalAdapter = AccountingJournalAdapter(journalDataList, this)
        fajRecyclerView.layoutManager = LinearLayoutManager(context!!)
        fajRecyclerView.adapter = journalAdapter
    }

    private fun initScrollListener() {
        fajRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == journalDataList.size - 1) {
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
                journalDataList.add(null)
                journalAdapter.notifyItemInserted(journalDataList.size - 1)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getAccountingJournals()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAccountingJournals() {
        val call = api.getAccountingJournals(CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetJournals")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetJournals" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), AccountingJournalModel::class.java)

                if (journalDataList.size > 0) {
                    journalDataList.removeAt(journalDataList.size - 1)
                    journalAdapter.notifyItemRemoved(journalDataList.size)
                }


                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                journalDataList.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (journalDataList.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    fajRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    fajRecyclerView.visibility = View.VISIBLE
                }

                journalAdapter.notifyDataSetChanged()
                isLoading = false


            }
            "DeleteJournals" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                journalDataList.removeAt(itemPosition)
                journalAdapter.notifyDataSetChanged()
//                (parentFragment as FinancialManagementFragment).acountingTab.callOnClick()
                val intent = Intent("accountingListRefresh")
                context!!.sendBroadcast(intent)
            }
        }
    }

    fun deleteJournal(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteAccountingJournals(id)
        RetrofitClient.apiCall(call, this, "DeleteJournals")
    }

    fun updateJournalStatus(id: Int, position: Int) {
        itemPosition = position

        val call = api.updateJournalStatus(id, journalDataList[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateJournalStatus")
//        val intent = Intent("accountingListRefresh")
//        context!!.sendBroadcast(intent)
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


}