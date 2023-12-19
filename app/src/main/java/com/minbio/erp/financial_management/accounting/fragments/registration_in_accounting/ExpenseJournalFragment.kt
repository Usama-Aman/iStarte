package com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting

//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.os.Handler
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.gson.Gson
//import com.minbio.erp.R
//import com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting.models.JournalsData
//import com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting.models.JournalsModel
//import com.minbio.erp.network.Api
//import com.minbio.erp.network.ResponseCallBack
//import com.minbio.erp.network.RetrofitClient
//import com.minbio.erp.utils.AppUtils
//import com.minbio.erp.utils.CustomSearchableSpinner
//import org.json.JSONObject
//import java.util.*
//
//class ExpenseJournalFragment : Fragment(), ResponseCallBack {
//
//    private val statusString =
//        arrayOf("Not yet journalized in ledgers", "Already journalized in ledgers")
//
//    private lateinit var v: View
//    private lateinit var fromDate: TextView
//    private lateinit var toDate: TextView
//    private lateinit var btnRefresh: TextView
//    private lateinit var tvNoData: TextView
//    private lateinit var btnRegisterTransaction: TextView
//    private lateinit var spinnerJournalizationStatus: CustomSearchableSpinner
//    private lateinit var faerjRecyclerView: RecyclerView
//    private lateinit var journalsAdapter: JournalsAdapter
//
//    private lateinit var api: Api
//
//    private var fromDateId = ""
//    private var toDateId = ""
//    private var journalizationStatusId = ""
//
//    private var journalsData: MutableList<JournalsData?> = ArrayList()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        v = inflater.inflate(
//            R.layout.fragment_financial_accounting_expense_report_journal,
//            container,
//            false
//        )
//        api = RetrofitClient.getClient(context!!).create(Api::class.java)
//
//        initViews()
//        setUpAdapter()
//
//        journalsData.clear()
//        AppUtils.showDialog(context!!)
//        getJournals()
//
//        return v
//    }
//
//    private fun initViews() {
//        fromDate = v.findViewById(R.id.fromDate)
//        toDate = v.findViewById(R.id.toDate)
//        tvNoData = v.findViewById(R.id.tvNoData)
//        btnRefresh = v.findViewById(R.id.btnRefresh)
//        btnRegisterTransaction = v.findViewById(R.id.btnRegisterTransaction)
//        spinnerJournalizationStatus = v.findViewById(R.id.spinnerJournalizationStatus)
//        faerjRecyclerView = v.findViewById(R.id.faerjRecyclerView)
//
//        setDate()
//
//        fromDate.setOnClickListener { showDatePicker(fromDate, isFrom = true) }
//        toDate.setOnClickListener { showDatePicker(toDate, isFrom = false) }
//
//        setUpJournalStatusSpinner()
//
//        btnRefresh.setOnClickListener {
//            AppUtils.preventTwoClick(btnRefresh)
//            journalsData.clear()
//            AppUtils.showDialog(context!!)
//            getJournals()
//        }
//
//        btnRegisterTransaction.setOnClickListener {
//            AppUtils.preventTwoClick(btnRegisterTransaction)
//            AppUtils.showDialog(context!!)
//            val call = api.registerInLedger(fromDateId, toDateId, "expenses")
//            RetrofitClient.apiCall(call, this, "RegisterInLedger")
//        }
//    }
//
//    private fun setUpAdapter() {
//        journalsAdapter = JournalsAdapter(journalsData, false)
//        faerjRecyclerView.layoutManager = LinearLayoutManager(context)
//        faerjRecyclerView.adapter = journalsAdapter
//    }
//
//    private fun setDate() {
//        val c = Calendar.getInstance()
//        c.add(Calendar.DATE, -30)
//        val y = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//
//        fromDate.text = AppUtils.getDateFormat(context!!, 1, month + 1, y)
//        toDate.text = AppUtils.getDateFormat(context!!, 30, month + 1, y)
//
//        fromDateId = "1-${month + 1}-$y"
//        toDateId = "30-${month + 1}-$y"
//    }
//
//    private fun showDatePicker(textview: TextView, isFrom: Boolean) {
//        val c = Calendar.getInstance()
//        c.add(Calendar.DATE, -30)
//        val y = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = if (isFrom) 1 else 30
//
//        val datePicker = DatePickerDialog(
//            activity!!,
//            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//                textview.text =
//                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
//                if (isFrom)
//                    fromDateId = "$day-${monthOfYear + 1}-$year"
//                else
//                    toDateId = "$day-${monthOfYear + 1}-$year"
//            },
//            y,
//            month,
//            day
//        )
//        datePicker.show()
//    }
//
//    private fun setUpJournalStatusSpinner() {
//        spinnerJournalizationStatus.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
//
//        spinnerJournalizationStatus.setTitle(context!!.resources.getString(R.string.journalizationSpinnerTitle))
//
//        val monthsAdapter = ArrayAdapter(
//            context!!,
//            R.layout.dropdown_item, statusString
//        )
//        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerJournalizationStatus.adapter = monthsAdapter
//        spinnerJournalizationStatus.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    adapterView: AdapterView<*>,
//                    view: View?,
//                    i: Int,
//                    l: Long
//                ) {
//                    journalizationStatusId =
//                        if (adapterView.selectedItemPosition == 0) "not_in_ledgers" else "already_in_ledgers"
//                    AppUtils.hideKeyboard(activity!!)
//                }
//
//                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//            }
//    }
//
//    private fun getJournals() {
//        val call = api.getExpenseReportJournals(fromDateId, toDateId, journalizationStatusId)
//        RetrofitClient.apiCall(call, this, "GetExpenseReportJournals")
//    }
//
//    override fun onSuccess(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        when (tag) {
//            "GetExpenseReportJournals" -> {
//                val model =
//                    Gson().fromJson(jsonObject.toString(), JournalsModel::class.java)
//
//                if (journalsData.size > 0) {
//                    journalsData.removeAt(journalsData.size - 1)
//                    journalsAdapter.notifyItemRemoved(journalsData.size)
//                }
//
//                journalsData.addAll(model.data)
//
//                if (journalsData.isNullOrEmpty()) {
//                    tvNoData.visibility = View.VISIBLE
//                    faerjRecyclerView.visibility = View.GONE
//                    btnRegisterTransaction.isEnabled = false
//                    btnRegisterTransaction.setBackgroundColor(
//                        ContextCompat.getColor(
//                            context!!,
//                            R.color.light_gray
//                        )
//                    )
//                } else {
//                    tvNoData.visibility = View.GONE
//                    faerjRecyclerView.visibility = View.VISIBLE
//                    btnRegisterTransaction.isEnabled = true
//                    btnRegisterTransaction.setBackgroundColor(
//                        ContextCompat.getColor(
//                            context!!,
//                            R.color.colorDarkBlue
//                        )
//                    )
//                }
//
//                journalsAdapter.notifyDataSetChanged()
//            }
//            "RegisterInLedger" -> {
//                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
//
//                Handler().postDelayed({
//                    journalsData.clear()
//                    AppUtils.showDialog(context!!)
//                    getJournals()
//                }, 500)
//            }
//        }
//    }
//
//
//    override fun onError(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, jsonObject.getString("message"), false)
//    }
//
//    override fun onException(message: String?, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, message!!, false)
//    }
//
//}