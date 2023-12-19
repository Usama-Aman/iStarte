package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import com.minbio.erp.R
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.item_financial_accounting_expense_bind.*
import java.util.*

class ExpenseToBindSearchDialog(
    context: Context,
    _expenseToBindFragment: ExpenseToBindFragment
) : Dialog(context) {


    private var monthsList = arrayOf(
        "", "January", "February", "March", "April", "May", "June", "July"
        , "August", "September", "October", "November", "December"
    )
    private var yearList: MutableList<String> = ArrayList()
    private var expenseToBindFragment = _expenseToBindFragment

    private lateinit var spinnerMonth: CustomSearchableSpinner
    private lateinit var spinnerYear: CustomSearchableSpinner
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var etIdLine: EditText
    private lateinit var etExpenseReport: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etTax: EditText
    private lateinit var btnSearch: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_expense_bind_search)

        initViews()
    }

    private fun initViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        etIdLine = findViewById(R.id.etIdLine)
        etExpenseReport = findViewById(R.id.etExpenseReport)
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etTax = findViewById(R.id.etTax)
        btnSearch = findViewById(R.id.btnSearch)

        setUpSpinners()

        etIdLine.setText(expenseToBindFragment.idLineToSearch)
        etExpenseReport.setText(expenseToBindFragment.expenseReportToSearch)
        etDescription.setText(expenseToBindFragment.descriptionToSearch)
        etAmount.setText(expenseToBindFragment.amountToSearch)
        etTax.setText(expenseToBindFragment.taxToSearch)

        for (i in monthsList.indices)
            if (expenseToBindFragment.monthToSearch == monthsList[i])
                spinnerMonth.setSelection(i)

        for (i in yearList.indices)
            if (expenseToBindFragment.yearToSearch == yearList[i])
                spinnerYear.setSelection(i)


        btnSearch.setOnClickListener {

            hideKeyboard()
            dismiss()

            expenseToBindFragment.idLineToSearch = etIdLine.text.toString()
            expenseToBindFragment.expenseReportToSearch = etExpenseReport.text.toString()
            expenseToBindFragment.descriptionToSearch = etDescription.text.toString()
            expenseToBindFragment.amountToSearch = etAmount.text.toString()
            expenseToBindFragment.taxToSearch = etTax.text.toString()

            expenseToBindFragment.expenseReportToBindData.clear()
            expenseToBindFragment.CURRENT_PAGE = 1
            expenseToBindFragment.LAST_PAGE = 1

            AppUtils.showDialog(ownerActivity!!)
            expenseToBindFragment.getExpenseReportToBind()

        }

    }

    private fun setUpSpinners() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        yearList.add("")
        for (i in 0 until 10)
            yearList.add((currentYear - i).toString())

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
                expenseToBindFragment.monthToSearch = monthsList[adapterView.selectedItemPosition]
                hideKeyboard()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


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
                expenseToBindFragment.yearToSearch = yearList[adapterView.selectedItemPosition]
                hideKeyboard()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = Objects.requireNonNull(ownerActivity)
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        AppUtils.hideKeyboard(ownerActivity!!)
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