package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.NewTransactionData
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.NewTransactionModel
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.NewTransactionMovements
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.financial_management.model.JournalCodeAccountModel
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.SharedPreference
import kotlinx.android.synthetic.main.fragment_financial_accounting_ledger_transaction.*
import kotlinx.android.synthetic.main.item_financial_personalized_group.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class NewLedgerTransactionFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var tvDate: TextView
    private lateinit var etAccountingDoc: EditText
    private lateinit var spinnerJournal: CustomSearchableSpinner
    private lateinit var btnCreate: LinearLayout
    private lateinit var back: ImageView
    private lateinit var addMovement: ImageView
    private lateinit var fanltRecyclerView: RecyclerView
    private lateinit var listOfMovementAdapter: ListOfMovementAdapter
    private lateinit var newAssignedSpinner: CustomSearchableSpinner
    private lateinit var falnttvDebit: TextView
    private lateinit var falnttvCredit: TextView

    private var dateId = ""
    private var journalId = 0

    private lateinit var api: Api
    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var journalCodeAccountData: MutableList<JournalCodeAccountData?> = java.util.ArrayList()
    private var parentAccountListStrings: MutableList<String> = ArrayList()
    private var movements: MutableList<NewTransactionMovements> = ArrayList()
    private var newTransactionData: NewTransactionData? = null

    private var ledgerId: Int? = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_ledger_transaction,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        getJournalCodeAccounts()

        return v
    }

    private fun initViews() {
        ledgerId = arguments?.getInt("id", -1)
        tvDate = v.findViewById(R.id.tvDate)
        addMovement = v.findViewById(R.id.addMovement)
        etAccountingDoc = v.findViewById(R.id.etAccountingDoc)
        spinnerJournal = v.findViewById(R.id.spinnerJournal)
        btnCreate = v.findViewById(R.id.btnCreate)
        newAssignedSpinner = v.findViewById(R.id.newAssignedSpinner)
        back = v.findViewById(R.id.back)
        falnttvDebit = v.findViewById(R.id.falnttvDebit)
        falnttvCredit = v.findViewById(R.id.falnttvCredit)

        falnttvDebit.text = context!!.resources.getString(
            R.string.falntLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        falnttvCredit.text = context!!.resources.getString(
            R.string.falntLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }

        tvDate.setOnClickListener {
            showDatePicker()
        }

        addMovement.setOnClickListener {
            if (parentAccountList.isNotEmpty())
                movements.add(
                    NewTransactionMovements(
                        0,
                        parentAccountList[0]?.account_number!!,
                        parentAccountList[0]?.label!!,
                        parentAccountList[0]?.id!!,
                        "",
                        "",
                        "0.00",
                        "0.00",
                        false
                    )
                )
            listOfMovementAdapter.notifyDataSetChanged()
            fanltRecyclerView.smoothScrollToPosition(movements.size - 1)
        }

        btnCreate.setOnClickListener {
            AppUtils.preventTwoClick(btnCreate)
            validate()
        }
    }

    private fun validate() {


        if (tvDate.text.toString().isBlank()) {
            errorDate.visibility = View.VISIBLE
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom_red)
            return
        } else {
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom)
            errorDate.visibility = View.INVISIBLE
        }
        if (etAccountingDoc.text.toString().isBlank()) {
            errorAccountingDoc.visibility = View.VISIBLE
            AppUtils.setBackground(etAccountingDoc, R.drawable.input_border_bottom_red)
            etAccountingDoc.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAccountingDoc, R.drawable.input_border_bottom)
            errorAccountingDoc.visibility = View.INVISIBLE
        }

        if (movements.size == 0) {
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.falntErrorAddListMovements),
                false
            )
            return
        }

        var debitValue = 0.0
        var creditValue = 0.0
        var isCreditDebitSame = false

        for (i in movements.indices) {
            creditValue += movements[i].credit.toDouble()
            debitValue += movements[i].debit.toDouble()
            if ((movements[i].debit.isNotEmpty() && movements[i].credit.isNotEmpty()) && (movements[i].debit != "0.00" && movements[i].credit != "0.00")) {
                movements[i].isDebitCreditBothEdit = true
                isCreditDebitSame = true
            } else {
                movements[i].isDebitCreditBothEdit = false
            }
        }

        listOfMovementAdapter.notifyDataSetChanged()
        if (isCreditDebitSame) {
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.falntErrorDebitCreditSame),
                false
            )
            return
        }

        if (creditValue != debitValue) {
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.falntErrorLedgerNotMaintained),
                false
            )
            return
        }

        val jsonArray = JSONArray()
        try {
            for (i in movements.indices) {
                val jsonObject = JSONObject()
                jsonObject.put("chart_account_id", movements[i].chart_account_id)
                jsonObject.put("sub_ledger_account", movements[i].sub_ledger_account)
                jsonObject.put("label", movements[i].label)
                jsonObject.put("credit", movements[i].credit)
                jsonObject.put("debit", movements[i].debit)
                if (ledgerId != -1 && ledgerId != null)
                    jsonObject.put("id", movements[i].id)

                jsonArray.put(jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppUtils.showToast(
                activity!!,
                context!!.resources.getString(R.string.errorSomethingIsNotRight)
                , false
            )
            return
        }

        AppUtils.showDialog(context!!)
        val call = if (ledgerId == -1 || ledgerId == null)
            api.createLedgerTransaction(
                dateId, etAccountingDoc.text.toString(),
                journalId, jsonArray
            )
        else
            api.updateLedgerTransaction(
                ledgerId!!, dateId, etAccountingDoc.text.toString(),
                journalId, jsonArray
            )

        RetrofitClient.apiCall(call, this, "CreateLedgerTransaction")

    }

    private fun setUpAdapter() {
        if (ledgerId == -1) {
            movements.clear()
            addMovement.callOnClick()
        }
        fanltRecyclerView = v.findViewById(R.id.fanltRecyclerView)
        listOfMovementAdapter = ListOfMovementAdapter(movements, this)
        fanltRecyclerView.layoutManager = LinearLayoutManager(context!!)
        fanltRecyclerView.adapter = listOfMovementAdapter
    }

    private fun setUpJournalSpinner() {
        val journalStrings: MutableList<String> = ArrayList()
        for (i in journalCodeAccountData.indices) {
            journalStrings.add(journalCodeAccountData[i]?.code!! + " - " + journalCodeAccountData[i]?.label)
        }

        spinnerJournal.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerJournal.setTitle(context!!.resources.getString(R.string.journalSpinnerTitle))

        val journalAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, journalStrings
        )
        journalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJournal.adapter = journalAdapter
        spinnerJournal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                journalId = journalCodeAccountData[adapterView.selectedItemPosition]?.id!!
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    private fun showDatePicker() {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                tvDate.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                dateId = "$day-${monthOfYear + 1}-$year"
            },
            y,
            month,
            day
        )
        datePicker.show()
    }

    private fun getJournalCodeAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getJournalAccountForMenu()
        RetrofitClient.apiCall(call, this, "JournalCodeAccounts")
    }

    private fun getParentAccounts() {
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "JournalCodeAccounts" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), JournalCodeAccountModel::class.java)
                journalCodeAccountData.addAll(model.data)

                setUpJournalSpinner()
                getParentAccounts()
            }
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                parentAccountList.addAll(model.data)
                parentAccountListStrings.clear()
                for (i in parentAccountList.indices) {
                    parentAccountListStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
                }

                if (ledgerId != -1 && ledgerId != null) {
                    getTransactionData()
                } else {
                    AppUtils.dismissDialog()
                    setUpAdapter()
                }
            }
            "CreateLedgerTransaction" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
            "GetTransactionData" -> {
                AppUtils.dismissDialog()
                val model = Gson().fromJson(jsonObject.toString(), NewTransactionModel::class.java)

                newTransactionData = model.data
                tvDate.text = newTransactionData?.date
                dateId = newTransactionData?.date!!
                etAccountingDoc.setText(newTransactionData?.accounting_doc)

                for (i in journalCodeAccountData.indices)
                    if (newTransactionData?.journal == journalCodeAccountData[i]?.id)
                        spinnerJournal.setSelection(i)

                movements.addAll(newTransactionData?.movements!!)

                setUpAdapter()
            }
        }
    }

    private fun getTransactionData() {
        val call = api.getTransactionData(ledgerId!!)
        RetrofitClient.apiCall(call, this, "GetTransactionData")
    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    fun showSpinner(position: Int) {
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

                    movements[position].chart_account_number =
                        parentAccountList[adapterView.selectedItemPosition]?.account_number!!

                    movements[position].chart_account_label =
                        parentAccountList[adapterView.selectedItemPosition]?.label!!

                    movements[position].chart_account_id =
                        parentAccountList[adapterView.selectedItemPosition]?.id!!

                    listOfMovementAdapter.notifyItemChanged(position)
                    AppUtils.hideKeyboard(activity!!)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        for (i in parentAccountList.indices)
            if (movements[position].chart_account_id == parentAccountList[i]?.id)
                newAssignedSpinner.setSelection(i)

        val motionEvent: MotionEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0
        )

        newAssignedSpinner.dispatchTouchEvent(motionEvent)
    }


}