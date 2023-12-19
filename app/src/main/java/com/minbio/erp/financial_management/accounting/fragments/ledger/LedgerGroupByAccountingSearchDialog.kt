package com.minbio.erp.financial_management.accounting.fragments.ledger

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
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*
import kotlin.collections.ArrayList

class LedgerGroupByAccountingSearchDialog(
    context: Context,
    _parentAccountList: MutableList<ParentAccountsData?>,
    _ledgerGroupByFragment: LedgerGroupByAccountingFragment
) : Dialog(context) {

    private lateinit var etNumTransaction: EditText
    private lateinit var etAccountingDoc: EditText
    private lateinit var etLabel: EditText
    private lateinit var etDebit: EditText
    private lateinit var etCredit: EditText
    private lateinit var etJournal: EditText
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var spinnerFromAccount: CustomSearchableSpinner
    private lateinit var spinnerToAccount: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout

    private var parentAccountList = _parentAccountList
    private var ledgerGroupByFragment = _ledgerGroupByFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_ledger_group_by_search)

        initViews()

    }

    private fun initViews() {
        etNumTransaction = findViewById(R.id.etNumTransaction)
        etAccountingDoc = findViewById(R.id.etAccountingDoc)
        etLabel = findViewById(R.id.etLabel)
        etDebit = findViewById(R.id.etDebit)
        etCredit = findViewById(R.id.etCredit)
        etJournal = findViewById(R.id.etJournal)
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        spinnerFromAccount = findViewById(R.id.spinnerFromAccount)
        spinnerToAccount = findViewById(R.id.spinnerToAccount)
        btnSearch = findViewById(R.id.btnSearch)

        setUpAccountSpinners()

        etNumTransaction.setText(ledgerGroupByFragment.numTransactionToSearch)
        etAccountingDoc.setText(ledgerGroupByFragment.accountingDocToSearch)
        etLabel.setText(ledgerGroupByFragment.labelToSearch)
        etDebit.setText(ledgerGroupByFragment.debitToSearch)
        etCredit.setText(ledgerGroupByFragment.creditToSearch)
        etJournal.setText(ledgerGroupByFragment.journalToSearch)
        tvFromDate.text = ledgerGroupByFragment.fromDateToSave
        tvToDate.text = ledgerGroupByFragment.toDateToSave

        for (i in parentAccountList.indices)
            if (i == ledgerGroupByFragment.fromAccountToSearch)
                spinnerFromAccount.setSelection(i)

        for (i in parentAccountList.indices)
            if (i == ledgerGroupByFragment.toAccountToSearch)
                spinnerToAccount.setSelection(i)

        btnSearch.setOnClickListener {
            dismiss()
            AppUtils.hideKeyboard(ownerActivity!!)
            AppUtils.showDialog(ownerActivity!!)

            ledgerGroupByFragment.numTransactionToSearch = etNumTransaction.text.toString()
            ledgerGroupByFragment.accountingDocToSearch = etAccountingDoc.text.toString()
            ledgerGroupByFragment.labelToSearch = etLabel.text.toString()
            ledgerGroupByFragment.debitToSearch = etDebit.text.toString()
            ledgerGroupByFragment.creditToSearch = etCredit.text.toString()
            ledgerGroupByFragment.journalToSearch = etJournal.text.toString()
            ledgerGroupByFragment.fromDateToSearch = tvFromDate.text.toString()
            ledgerGroupByFragment.toDateToSearch = tvToDate.text.toString()

            ledgerGroupByFragment.CURRENT_PAGE = 1
            ledgerGroupByFragment.LAST_PAGE = 1
            ledgerGroupByFragment.creditTotal.text ="0.00"
            ledgerGroupByFragment.debitTotal.text ="0.00"
            ledgerGroupByFragment.ledgerData.clear()

            ledgerGroupByFragment.getLedgerData()
        }

        tvFromDate.setOnClickListener { showDatePicker(tvFromDate, isFrom = true) }
        tvToDate.setOnClickListener { showDatePicker(tvToDate, isFrom = false) }


    }

    private fun showDatePicker(textview: TextView, isFrom: Boolean) {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            ownerActivity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                textview.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                if (isFrom) {
                    ledgerGroupByFragment.fromDateToSearch = "$day-${monthOfYear + 1}-$year"
                    ledgerGroupByFragment.fromDateToSave =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                } else {
                    ledgerGroupByFragment.toDateToSearch = "$day-${monthOfYear + 1}-$year"
                    ledgerGroupByFragment.toDateToSave =
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
                ledgerGroupByFragment.fromAccountToSearch =
                    parentAccountList[adapterView.selectedItemPosition]?.id!!
                AppUtils.hideKeyboard(ownerActivity!!)
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
                ledgerGroupByFragment.toAccountToSearch =
                    parentAccountList[adapterView.selectedItemPosition]?.id!!
                AppUtils.hideKeyboard(ownerActivity!!)
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