package com.minbio.erp.financial_management.accounting.fragments.account_balance

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
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
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AccountBalanceFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private lateinit var v: View
    private lateinit var accountBalanceAdapter: AccountBalanceAdapter
    private lateinit var faabRecyclerView: RecyclerView
    private lateinit var spinnerFromAccount: CustomSearchableSpinner
    private lateinit var spinnerToAccount: CustomSearchableSpinner
    private lateinit var search: ImageView
    private lateinit var totalAccountBalanceLayout: LinearLayout
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvAccountDebit: TextView
    private lateinit var tvAccountCredit: TextView
    private lateinit var tvAccountBalance: TextView
    private lateinit var tvNoData: TextView
    private lateinit var btnExportCSV: TextView
    private lateinit var api: Api

    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var fromAccountId = 0
    private var toAccountId = 0
    private var fromDateToSearch = ""
    private var toDateToSearch = ""

    private var accountBalanceData: MutableList<AccountBalanceData?> = ArrayList()
    private lateinit var faabtvDebit : TextView
    private lateinit var faabtvCredit : TextView
    private lateinit var faabtvBalance : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_account_balance,
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
        faabRecyclerView = v.findViewById(R.id.faabRecyclerView)
        spinnerFromAccount = v.findViewById(R.id.spinnerFromAccount)
        spinnerToAccount = v.findViewById(R.id.spinnerToAccount)
        search = v.findViewById(R.id.search)
        totalAccountBalanceLayout = v.findViewById(R.id.totalAccountBalanceLayout)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvStartDate = v.findViewById(R.id.tvStartDate)
        tvEndDate = v.findViewById(R.id.tvEndDate)
        tvAccountDebit = v.findViewById(R.id.tvAccountDebit)
        tvAccountCredit = v.findViewById(R.id.tvAccountCredit)
        tvAccountBalance = v.findViewById(R.id.tvAccountBalance)
        tvNoData = v.findViewById(R.id.tvNoData)
        btnExportCSV = v.findViewById(R.id.btnExportCSV)

        faabtvDebit = v.findViewById(R.id.faabtvDebit)
        faabtvCredit = v.findViewById(R.id.faabtvCredit)
        faabtvBalance = v.findViewById(R.id.faabtvBalance)

        faabtvDebit.text = context!!.resources.getString(
            R.string.faabLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faabtvCredit.text = context!!.resources.getString(
            R.string.faabLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faabtvBalance.text = context!!.resources.getString(
            R.string.faabLabelBalance,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        tvStartDate.setOnClickListener { showDatePicker(tvStartDate, true) }
        tvEndDate.setOnClickListener { showDatePicker(tvEndDate, false) }

        pullToRefresh.setOnRefreshListener {
            accountBalanceData.clear()
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            getAccountBalance()
        }

        btnExportCSV.setOnClickListener {
            AppUtils.showDialog(context!!)
            val call = api.getAccountBalanceExportCSV(
                fromDateToSearch, toDateToSearch, fromAccountId, toAccountId
            )
            RetrofitClient.apiCall(call, this, "ExportCSV")
        }

        search.setOnClickListener {
            AppUtils.showDialog(context!!)
            getAccountBalance()
        }

        setDate()

    }

    private fun setDate() {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, -30)
        val y = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)

        tvStartDate.text = AppUtils.getDateFormat(context!!, 1, month + 1, y)
        tvEndDate.text = AppUtils.getDateFormat(context!!, 30, month + 1, y)

        fromDateToSearch = "1-${month + 1}-$y"
        toDateToSearch = "30-${month + 1}-$y"
    }

    private fun showDatePicker(textview: TextView, isFrom: Boolean) {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            activity!!,
            { _, year, monthOfYear, dayOfMonth ->
                textview.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                if (isFrom) {
                    fromDateToSearch = "$day-${monthOfYear + 1}-$year"
                } else {
                    toDateToSearch = "$day-${monthOfYear + 1}-$year"
                }
            },
            y,
            month,
            day
        )
        datePicker.show()
    }


    private fun setUpAdapter() {
        accountBalanceAdapter = AccountBalanceAdapter(accountBalanceData)
        faabRecyclerView.layoutManager = LinearLayoutManager(context!!)
        faabRecyclerView.adapter = accountBalanceAdapter
    }

    private fun initScrollListener() {
        faabRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == accountBalanceData.size - 1) {
                    recyclerView.post {
                        try {
                            if (CURRENT_PAGE < LAST_PAGE) {
                                accountBalanceData.add(null)
                                accountBalanceAdapter.notifyItemInserted(accountBalanceData.size - 1)
                                Handler().postDelayed({
                                    CURRENT_PAGE++
                                    getAccountBalance()
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

    private fun getAccountBalance() {
        val call = api.getAccountBalance(
            CURRENT_PAGE, fromDateToSearch, toDateToSearch, fromAccountId, toAccountId
        )
        RetrofitClient.apiCall(call, this, "AccountBalance")
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
                setUpAccountSpinners()
                getAccountBalance()
            }
            "AccountBalance" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), AccountBalanceModel::class.java)

                if (accountBalanceData.size > 0) {
                    accountBalanceData.removeAt(accountBalanceData.size - 1)
                    accountBalanceAdapter.notifyItemRemoved(accountBalanceData.size)
                }

                CURRENT_PAGE = model.meta?.current_page!!
                LAST_PAGE = model.meta?.last_page!!

                accountBalanceData.addAll(model.data!!)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (accountBalanceData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    faabRecyclerView.visibility = View.GONE
                    totalAccountBalanceLayout.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    faabRecyclerView.visibility = View.VISIBLE
                    totalAccountBalanceLayout.visibility = View.VISIBLE

                    tvAccountDebit.text = model.total_debit
                    tvAccountCredit.text = model.total_credit
                    tvAccountBalance.text = model.total_balance
                }

                if (CURRENT_PAGE < LAST_PAGE)
                    accountBalanceData.add(null)

                accountBalanceAdapter.notifyDataSetChanged()
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

    private fun setUpAccountSpinners() {
        val string: MutableList<String> = ArrayList()
        for (i in parentAccountList.indices) {
            if (i > 0)
                string.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                string.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }
        spinnerFromAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))

        spinnerFromAccount.setTitle(context!!.resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val fromAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, string
        )
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFromAccount.adapter = fromAdapter
        spinnerFromAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                fromAccountId = parentAccountList[adapterView.selectedItemPosition]?.id!!
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        spinnerToAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))

        spinnerToAccount.setTitle(context!!.resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val toAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, string
        )
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerToAccount.adapter = toAdapter
        spinnerToAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                toAccountId = parentAccountList[adapterView.selectedItemPosition]?.id!!
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


}