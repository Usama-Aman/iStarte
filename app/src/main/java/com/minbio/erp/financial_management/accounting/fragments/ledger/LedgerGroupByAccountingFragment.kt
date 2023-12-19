package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerGroupByAccountsData
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerGroupByAccountsModel
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.financial_management.model.JournalCodeAccountModel
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject

class LedgerGroupByAccountingFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1

    private lateinit var v: View
    private lateinit var btnViewFlatList: TextView
    private lateinit var btnNewTransaction: TextView
    private lateinit var search: TextView
    private lateinit var tvNoData: TextView
    private lateinit var faledgerRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var btnDelete: LinearLayout
    private lateinit var totalLayout: LinearLayout
    lateinit var debitTotal: TextView
    lateinit var creditTotal: TextView
    private lateinit var ledgerGroupByAccountingAdapter: LedgerGroupByAccountingAdapter

    var numTransactionToSearch = ""
    var accountingDocToSearch = ""
    var labelToSearch = ""
    var debitToSearch = ""
    var creditToSearch = ""
    var journalToSearch = ""
    var fromDateToSearch = ""
    var toDateToSearch = ""
    var fromAccountToSearch = 0
    var toAccountToSearch = 0
    var fromDateToSave = ""
    var toDateToSave = ""

    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var journalCodeAccountData: MutableList<JournalCodeAccountData?> = ArrayList()
    private lateinit var api: Api
    var ledgerData: MutableList<LedgerGroupByAccountsData?> = ArrayList()
    private lateinit var fatvledgerDebit: TextView
    private lateinit var fatvledgerCredit: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_ledger_groupby_accounts,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setupAdapter()
        initScrollListener()

        CURRENT_PAGE = 1
        LAST_PAGE = 1
        ledgerData.clear()
        parentAccountList.clear()
        getParentAccounts()

        return v
    }

    private fun initViews() {
        search = v.findViewById(R.id.search)
        tvNoData = v.findViewById(R.id.tvNoData)
        faledgerRecyclerView = v.findViewById(R.id.faledgerRecyclerView)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnViewFlatList = v.findViewById(R.id.viewFlatList)
        btnNewTransaction = v.findViewById(R.id.newTransaction)
        totalLayout = v.findViewById(R.id.totalLayout)
        debitTotal = v.findViewById(R.id.debitTotal)
        creditTotal = v.findViewById(R.id.creditTotal)
        fatvledgerDebit = v.findViewById(R.id.fatvledgerDebit)
        fatvledgerCredit = v.findViewById(R.id.fatvledgerCredit)

        fatvledgerDebit.text = context!!.resources.getString(
            R.string.faledgerLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        fatvledgerCredit.text = context!!.resources.getString(
            R.string.faledgerLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog =
                LedgerGroupByAccountingSearchDialog(context!!, parentAccountList, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getLedgerData()
        }


        btnNewTransaction.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                NewLedgerTransactionFragment()
            )
        }

        btnViewFlatList.setOnClickListener {
            (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
        }

        btnDelete.setOnClickListener {
            AppUtils.preventTwoClick(btnDelete)
            AppUtils.showDialog(context!!)
            val call = api.getJournalCodeAccounts()
            RetrofitClient.apiCall(call, this, "JournalCodeAccounts")
        }
    }

    private fun emptyData() {
        creditTotal.text ="0.00"
        debitTotal.text ="0.00"

        CURRENT_PAGE = 1
        LAST_PAGE = 1
        ledgerData.clear()

        numTransactionToSearch = ""
        accountingDocToSearch = ""
        labelToSearch = ""
        debitToSearch = ""
        creditToSearch = ""
        journalToSearch = ""
        fromDateToSearch = ""
        toDateToSearch = ""
        fromAccountToSearch = 0
        toAccountToSearch = 0
        fromDateToSave = ""
        toDateToSave = ""
    }

    private fun setupAdapter() {
        ledgerGroupByAccountingAdapter = LedgerGroupByAccountingAdapter(ledgerData, this)
        faledgerRecyclerView.layoutManager = LinearLayoutManager(context)
        faledgerRecyclerView.adapter = ledgerGroupByAccountingAdapter
    }

    private fun initScrollListener() {
        faledgerRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ledgerData.size - 1) {
                    recyclerView.post {
                        try {
                            if (CURRENT_PAGE < LAST_PAGE) {
                                Handler().postDelayed({
                                    CURRENT_PAGE++
                                    getLedgerData()
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

    fun getLedgerData() {
        val call = api.getLedgerDataGroupByAccounts(
            CURRENT_PAGE, numTransactionToSearch, accountingDocToSearch, labelToSearch,
            debitToSearch, creditToSearch, journalToSearch, fromDateToSearch, toDateToSearch,
            fromAccountToSearch, toAccountToSearch
        )
        RetrofitClient.apiCall(call, this, "GetLedgerData")
    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
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
                getLedgerData()

            }
            "JournalCodeAccounts" -> {
                AppUtils.dismissDialog()
                journalCodeAccountData.clear()
                val model =
                    Gson().fromJson(jsonObject.toString(), JournalCodeAccountModel::class.java)
                journalCodeAccountData.addAll(model.data)

                val deleteDialog =
                    DeleteLedgerLinesGroupByDialog(context!!, journalCodeAccountData, this)
                deleteDialog.setOwnerActivity(activity!!)
                deleteDialog.show()
            }
            "DeleteLedgerLines" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)

                Handler().postDelayed({
                    emptyData()
                    AppUtils.showDialog(context!!)
                    getLedgerData()
                }, 500)

            }
            "GetLedgerData" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), LedgerGroupByAccountsModel::class.java)

                if (ledgerData.size > 0) {
                    ledgerData.removeAt(ledgerData.size - 1)
                    ledgerGroupByAccountingAdapter.notifyItemRemoved(ledgerData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                ledgerData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (ledgerData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faledgerRecyclerView.visibility = View.GONE
                    totalLayout.visibility = View.GONE
                } else {

                    creditTotal.text =
                        (creditTotal.text.toString()
                            .toDouble() + ledgerData[ledgerData.size - 1]?.end_total_credit.toString()
                            .toDouble()).toString()

                    debitTotal.text =
                        (debitTotal.text.toString()
                            .toDouble() + ledgerData[ledgerData.size - 1]?.end_total_debit!!.toDouble()).toString()

//                    creditTotal.text = ledgerData[ledgerData.size - 1]?.end_total_credit.toString()
//                    debitTotal.text = ledgerData[ledgerData.size - 1]?.end_total_debit.toString()


                    tvNoData.visibility = View.GONE
                    faledgerRecyclerView.visibility = View.VISIBLE
                    totalLayout.visibility = View.VISIBLE
                }

                if (CURRENT_PAGE < LAST_PAGE) {
                    ledgerData.add(null)
                    ledgerGroupByAccountingAdapter.notifyItemInserted(ledgerData.size - 1)
                } else
                    ledgerGroupByAccountingAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        pullToRefresh.isRefreshing = false
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        pullToRefresh.isRefreshing = false
    }

    fun editLedger(entryPosition: Int, ledgerItemPosition: Int) {
        val fragment = NewLedgerTransactionFragment()
        val bundle = Bundle()
        ledgerData[ledgerItemPosition]?.ledger_entries?.get(entryPosition)?.id?.let {
            bundle.putInt(
                "id",
                it
            )
        }
        fragment.arguments = bundle
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    fun deleteLedgerLines(ledgerEntryPosition: Int, ledgerPosition: Int) {
        AppUtils.showDialog(context!!)
        val call = api.deleteLedgerLines(
            "single",
            ledgerData[ledgerPosition]?.ledger_entries!![ledgerEntryPosition].id,
            "",
            "",
            -1
        )
        RetrofitClient.apiCall(call, this, "DeleteLedgerLines")
    }

    fun deleteLedgerLines(year: String, month: String, journalId: Int) {
        AppUtils.showDialog(context!!)
        val call = api.deleteLedgerLines(
            "multiple", -1, year, month, journalId
        )
        RetrofitClient.apiCall(call, this, "DeleteLedgerLines")
    }


}