package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerData
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerModel
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.financial_management.model.JournalCodeAccountModel
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.DownloadFile
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class LedgerFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1

    private lateinit var v: View
    private lateinit var btnNewTransaction: TextView
    private lateinit var btnGroupByAccounting: TextView
    private lateinit var search: TextView
    private lateinit var tvNoData: TextView
    lateinit var tvDebitTotal: TextView
    lateinit var tvCreditTotal: TextView
    private lateinit var includeDocExported: TextView
    private lateinit var exportedSwitch: Switch
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var faledgerRecyclerView: RecyclerView
    private lateinit var btnDelete: LinearLayout
    private lateinit var totalLayout: LinearLayout
    private lateinit var fatvledgerDebit: TextView
    private lateinit var fatvledgerCredit: TextView

    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()

    var numTransactionToSearch = ""
    var accountingDocToSearch = ""
    var fromSubledgerToSearch = ""
    var toSubledgerToSearch = ""
    var labelToSearch = ""
    var debitToSearch = ""
    var creditToSearch = ""
    var journalToSearch = ""
    var fromDateToSearch = ""
    var toDateToSearch = ""
    var exportFromDateToSearch = ""
    var exportToDateToSearch = ""
    var fromAccountToSearch = 0
    var toAccountToSearch = 0
    var withExported = 0

    var fromDateToSave = ""
    var toDateToSave = ""
    var exportFromDateToSave = ""
    var exportToDateToSave = ""

    lateinit var ledgerAdapter: LedgerAdapter
    private var journalCodeAccountData: MutableList<JournalCodeAccountData?> = ArrayList()
    private lateinit var api: Api
    var ledgerData: MutableList<LedgerData?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_ledger, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()


        CURRENT_PAGE = 1
        LAST_PAGE = 1
        ledgerData.clear()
        parentAccountList.clear()
        getParentAccounts()

        return v
    }

    private fun initViews() {
        btnNewTransaction = v.findViewById(R.id.newTransaction)
        btnGroupByAccounting = v.findViewById(R.id.groupByAccounting)
        search = v.findViewById(R.id.search)
        exportedSwitch = v.findViewById(R.id.exportedSwitch)
        includeDocExported = v.findViewById(R.id.includeDocExported)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        faledgerRecyclerView = v.findViewById(R.id.faledgerRecyclerView)
        btnDelete = v.findViewById(R.id.btnDelete)
        tvNoData = v.findViewById(R.id.tvNoData)
        totalLayout = v.findViewById(R.id.totalLayout)
        tvDebitTotal = v.findViewById(R.id.tvDebitTotal)
        tvCreditTotal = v.findViewById(R.id.tvCreditTotal)
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


        exportedSwitch.setOnClickListener {
            withExported = if (withExported == 0) 1 else 0

            CURRENT_PAGE = 1
            LAST_PAGE = 1
            ledgerData.clear()
            AppUtils.showDialog(context!!)
            getLedgerData()

        }

        pullToRefresh.setOnRefreshListener {
            emptyData()
            getLedgerData()
        }

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog =
                LedgerSearchDialog(context!!, parentAccountList, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        btnGroupByAccounting.setOnClickListener {
            AppUtils.preventTwoClick(btnGroupByAccounting)
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                LedgerGroupByAccountingFragment()
            )
        }

        btnNewTransaction.setOnClickListener {
            AppUtils.preventTwoClick(btnNewTransaction)
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                NewLedgerTransactionFragment()
            )
        }

        btnDelete.setOnClickListener {
            AppUtils.preventTwoClick(btnDelete)

            AppUtils.showDialog(context!!)
            val call = api.getJournalAccountForMenu()
            RetrofitClient.apiCall(call, this, "JournalCodeAccounts")
        }

        includeDocExported.setOnClickListener {
            AppUtils.preventTwoClick(btnDelete)
            getExportCSV()
        }
    }

    private fun getExportCSV() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            AppUtils.showDialog(context!!)
            val call = api.getLedgerExportCsv()
            RetrofitClient.apiCall(call, this, "ExportCSV")
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Constants.STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun emptyData() {
        tvCreditTotal.text = "0.00"
        tvDebitTotal.text = "0.00"

        CURRENT_PAGE = 1
        LAST_PAGE = 1
        ledgerData.clear()

        numTransactionToSearch = ""
        accountingDocToSearch = ""
        fromSubledgerToSearch = ""
        toSubledgerToSearch = ""
        labelToSearch = ""
        debitToSearch = ""
        creditToSearch = ""
        journalToSearch = ""
        fromDateToSearch = ""
        toDateToSearch = ""
        exportFromDateToSearch = ""
        exportToDateToSearch = ""
        fromAccountToSearch = 0
        toAccountToSearch = 0
        fromDateToSave = ""
        toDateToSave = ""
        exportFromDateToSave = ""
        exportToDateToSave = ""
    }

    private fun setUpAdapter() {
        ledgerAdapter = LedgerAdapter(ledgerData, this)
        faledgerRecyclerView.layoutManager = LinearLayoutManager(context!!)
        faledgerRecyclerView.adapter = ledgerAdapter

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
        val call = api.getLedgerData(
            CURRENT_PAGE,
            numTransactionToSearch, accountingDocToSearch, fromDateToSearch, toDateToSearch,
            fromAccountToSearch, toAccountToSearch, fromSubledgerToSearch, toSubledgerToSearch,
            labelToSearch, debitToSearch, creditToSearch, journalToSearch, exportFromDateToSearch,
            exportToDateToSearch, withExported
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
                    DeleteLedgerLinesDialog(context!!, journalCodeAccountData, this)
                deleteDialog.setOwnerActivity(activity!!)
                deleteDialog.show()
            }
            "GetLedgerData" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), LedgerModel::class.java)

                if (ledgerData.size > 0) {
                    ledgerData.removeAt(ledgerData.size - 1)
                    ledgerAdapter.notifyItemRemoved(ledgerData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                ledgerData.addAll(model?.data!!)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (ledgerData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faledgerRecyclerView.visibility = View.GONE
                    totalLayout.visibility = View.GONE
                } else {
                    tvCreditTotal.text =
                        (tvCreditTotal.text.toString()
                            .toDouble() + model.total_credit.toDouble()).toString()

                    tvDebitTotal.text =
                        (tvDebitTotal.text.toString()
                            .toDouble() + model.total_debit.toDouble()).toString()


                    tvNoData.visibility = View.GONE
                    faledgerRecyclerView.visibility = View.VISIBLE
                    totalLayout.visibility = View.VISIBLE
                }

                if (CURRENT_PAGE < LAST_PAGE) {
                    ledgerData.add(null)
                    ledgerAdapter.notifyItemInserted(ledgerData.size - 1)
                } else
                    ledgerAdapter.notifyDataSetChanged()
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
            "ExportCSV" -> {
                val data: JSONObject = jsonObject.getJSONObject("data")
                val url = data.getString("url")
                val fileName = data.getString("file_name")
                val fileExt = data.getString("file_ext")
                DownloadFile(context!!, fileName, fileExt).execute(url)
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

    fun editLedger(position: Int) {
        val fragment = NewLedgerTransactionFragment()
        val bundle = Bundle()
        bundle.putInt("id", ledgerData[position]?.id!!)
        fragment.arguments = bundle
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    fun deleteLedgerLines(position: Int) {
        AppUtils.showDialog(context!!)
        val call = api.deleteLedgerLines(
            "single", ledgerData[position]?.id!!, "", "", -1
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getExportCSV()
            } else {
                AppUtils.showToast(
                    activity!!,
                    resources.getString(R.string.locationPermissionDenied),
                    false
                )
            }
        }
    }


}