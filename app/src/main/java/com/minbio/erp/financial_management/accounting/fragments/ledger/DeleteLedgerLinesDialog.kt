package com.minbio.erp.financial_management.accounting.fragments.ledger

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
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.hideKeyboard
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*
import kotlin.collections.ArrayList

class DeleteLedgerLinesDialog(
    context: Context,
    _journalAccountsData: MutableList<JournalCodeAccountData?>,
    _ledgerFragment: LedgerFragment
) : Dialog(context) {

    private var monthsList = arrayOf(
        "", "January", "February", "March", "April", "May", "June", "July"
        , "August", "September", "October", "November", "December"
    )
    private var yearList: MutableList<String> = ArrayList()
    private lateinit var spinnerMonth: CustomSearchableSpinner
    private lateinit var spinnerYear: CustomSearchableSpinner
    private lateinit var spinnerJournal: CustomSearchableSpinner
    private lateinit var btnYes: LinearLayout
    private lateinit var btnNo: LinearLayout

    private var ledgerFragment = _ledgerFragment
    private var journalAccountsData = _journalAccountsData

    private var yearId = ""
    private var monthsId = ""
    private var journalId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_delete_ledger_lines)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        initViews()

    }

    private fun initViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        spinnerJournal = findViewById(R.id.spinnerJournal)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)

        setupSpinners()

        btnYes.setOnClickListener {
            dismiss()
            AppUtils.hideKeyboard(ownerActivity!!)
            ledgerFragment.deleteLedgerLines(yearId, monthsId, journalId)
        }

        btnNo.setOnClickListener {
            hideKeyboard(ownerActivity!!)
            dismiss()
        }

    }

    private fun setupSpinners() {
        spinnerMonth.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerMonth.setTitle(context.resources.getString(R.string.monthsSpinnerTitle))

        val monthsAdapter = ArrayAdapter(
            context,
            R.layout.dropdown_item, monthsList
        )
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthsAdapter
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                monthsId = if (adapterView.selectedItemPosition == 0)
                    "0"
                else
                    monthsList[adapterView.selectedItemPosition]
                hideKeyboard(ownerActivity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        yearList.add("")
        for (i in 0 until 10)
            yearList.add((currentYear - i).toString())

        spinnerYear.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerYear.setTitle(context.resources.getString(R.string.yearSpinnerTitle))

        val yearAdapter = ArrayAdapter(
            context,
            R.layout.dropdown_item, yearList
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                yearId = if (adapterView.selectedItemPosition == 0)
                    "0"
                else yearList[adapterView.selectedItemPosition]
                hideKeyboard(ownerActivity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        val journalStrings: MutableList<String> = ArrayList()
        for (i in journalAccountsData.indices) {
            journalStrings.add(journalAccountsData[i]?.code!! + " - " + journalAccountsData[i]?.label)
        }

        spinnerJournal.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))
        spinnerJournal.setTitle(context.resources.getString(R.string.journalSpinnerTitle))

        val journalAdapter = ArrayAdapter(
            context,
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
                journalId = journalAccountsData[adapterView.selectedItemPosition]?.id!!
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