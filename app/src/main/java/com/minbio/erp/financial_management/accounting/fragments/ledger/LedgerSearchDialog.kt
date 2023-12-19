package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.hideKeyboard
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*
import kotlin.collections.ArrayList

class LedgerSearchDialog(
    context: Context,
    _parentAccountList: MutableList<ParentAccountsData?>,
    _ledgerFragment: LedgerFragment
) : Dialog(context) {

    private lateinit var etNumTransaction: EditText
    private lateinit var etAccountingDoc: EditText
    private lateinit var etFromSubledger: EditText
    private lateinit var etToSubledger: EditText
    private lateinit var etLabel: EditText
    private lateinit var etDebit: EditText
    private lateinit var etCredit: EditText
    private lateinit var etJournal: EditText
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var tvExportFromDate: TextView
    private lateinit var tvExportToDate: TextView
    private lateinit var spinnerFromAccount: CustomSearchableSpinner
    private lateinit var spinnerToAccount: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout

    private var parentAccountList = _parentAccountList
    private var ledgerFragment = _ledgerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_ledger_search)

        initViews()

    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        etNumTransaction = findViewById(R.id.etNumTransaction)
        etAccountingDoc = findViewById(R.id.etAccountingDoc)
        etFromSubledger = findViewById(R.id.etFromSubledger)
        etToSubledger = findViewById(R.id.etToSubledger)
        etLabel = findViewById(R.id.etLabel)
        etDebit = findViewById(R.id.etDebit)
        etCredit = findViewById(R.id.etCredit)
        etJournal = findViewById(R.id.etJournal)
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        tvExportFromDate = findViewById(R.id.tvExportFromDate)
        tvExportToDate = findViewById(R.id.tvExportToDate)
        spinnerFromAccount = findViewById(R.id.spinnerFromAccount)
        spinnerToAccount = findViewById(R.id.spinnerToAccount)
        btnSearch = findViewById(R.id.btnSearch)

        setUpAccountSpinners()

        etNumTransaction.setText(ledgerFragment.numTransactionToSearch)
        etAccountingDoc.setText(ledgerFragment.accountingDocToSearch)
        etFromSubledger.setText(ledgerFragment.fromSubledgerToSearch)
        etToSubledger.setText(ledgerFragment.toSubledgerToSearch)
        etLabel.setText(ledgerFragment.labelToSearch)
        etDebit.setText(ledgerFragment.debitToSearch)
        etCredit.setText(ledgerFragment.creditToSearch)
        etJournal.setText(ledgerFragment.journalToSearch)
        tvFromDate.text = ledgerFragment.fromDateToSave
        tvToDate.text = ledgerFragment.toDateToSave
        tvExportFromDate.text = ledgerFragment.exportFromDateToSave
        tvExportToDate.text = ledgerFragment.exportToDateToSave

        for (i in parentAccountList.indices)
            if (parentAccountList[i]?.id == ledgerFragment.fromAccountToSearch)
                spinnerFromAccount.setSelection(i)

        for (i in parentAccountList.indices)
            if (parentAccountList[i]?.id == ledgerFragment.toAccountToSearch)
                spinnerToAccount.setSelection(i)

        btnSearch.setOnClickListener {
            dismiss()
            AppUtils.hideKeyboard(ownerActivity!!)
            AppUtils.showDialog(ownerActivity!!)

            ledgerFragment.numTransactionToSearch = etNumTransaction.text.toString()
            ledgerFragment.accountingDocToSearch = etAccountingDoc.text.toString()
            ledgerFragment.fromSubledgerToSearch = etFromSubledger.text.toString()
            ledgerFragment.toSubledgerToSearch = etToSubledger.text.toString()
            ledgerFragment.labelToSearch = etLabel.text.toString()
            ledgerFragment.debitToSearch = etDebit.text.toString()
            ledgerFragment.creditToSearch = etCredit.text.toString()
            ledgerFragment.journalToSearch = etJournal.text.toString()
            ledgerFragment.fromDateToSearch = tvFromDate.text.toString()
            ledgerFragment.toDateToSearch = tvToDate.text.toString()
            ledgerFragment.exportFromDateToSearch = tvExportFromDate.text.toString()
            ledgerFragment.exportToDateToSearch = tvExportToDate.text.toString()

            ledgerFragment.CURRENT_PAGE = 1
            ledgerFragment.LAST_PAGE = 1
            ledgerFragment.tvCreditTotal.text = "0.00"
            ledgerFragment.tvDebitTotal.text = "0.00"
            ledgerFragment.ledgerData.clear()
            ledgerFragment.ledgerAdapter.notifyDataSetChanged()

            ledgerFragment.getLedgerData()

        }

        tvFromDate.setOnClickListener { showDatePicker(tvFromDate, isFrom = true, isDate = true) }
        tvToDate.setOnClickListener { showDatePicker(tvToDate, isFrom = false, isDate = true) }
        tvExportFromDate.setOnClickListener {
            showDatePicker(tvExportFromDate, isFrom = true, isDate = false)
        }
        tvExportToDate.setOnClickListener {
            showDatePicker(tvExportToDate, isFrom = false, isDate = false)
        }


    }

    private fun showDatePicker(textview: TextView, isFrom: Boolean, isDate: Boolean) {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            ownerActivity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                textview.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                if (isDate)
                    if (isFrom) {
                        ledgerFragment.fromDateToSearch = "$day-${monthOfYear + 1}-$year"
                        ledgerFragment.fromDateToSave =
                            AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                    } else {
                        ledgerFragment.toDateToSearch = "$day-${monthOfYear + 1}-$year"
                        ledgerFragment.toDateToSave =
                            AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                    }
                else
                    if (isFrom) {
                        ledgerFragment.exportFromDateToSearch = "$day-${monthOfYear + 1}-$year"
                        ledgerFragment.exportFromDateToSave =
                            AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                    } else {
                        ledgerFragment.exportToDateToSearch = "$day-${monthOfYear + 1}-$year"
                        ledgerFragment.exportToDateToSave =
                            AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                    }
            },
            y,
            month,
            day
        )
        datePicker.show()
    }


    private fun setUpAccountSpinners() {
        val string: MutableList<String> = ArrayList()
        for (i in parentAccountList.indices) {
            if (i > 0)
                string.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                string.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }
        spinnerFromAccount.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerFromAccount.setTitle(context.resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val fromAdapter = ArrayAdapter(
            context,
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
                ledgerFragment.fromAccountToSearch =
                    parentAccountList[adapterView.selectedItemPosition]?.id!!
                hideKeyboard(ownerActivity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        spinnerToAccount.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerToAccount.setTitle(context.resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val toAdapter = ArrayAdapter(
            context,
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
                ledgerFragment.toAccountToSearch =
                    parentAccountList[adapterView.selectedItemPosition]?.id!!
                hideKeyboard(ownerActivity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        ownerActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}