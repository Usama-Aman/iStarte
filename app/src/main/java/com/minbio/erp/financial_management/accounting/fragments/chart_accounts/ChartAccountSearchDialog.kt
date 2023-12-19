package com.minbio.erp.financial_management.accounting.fragments.chart_accounts

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*
import kotlin.collections.ArrayList

class ChartAccountSearchDialog(
    _context: Context,
    _chartAccountsFragment: ChartAccountsFragment,
    _parentAccountList: MutableList<ParentAccountsData?>
) :
    Dialog(_context) {

    private val chartAccountsFragment = _chartAccountsFragment
    private val parentAccountList = _parentAccountList

    private lateinit var etAccountNumber: EditText
    private lateinit var etLabel: EditText
    private lateinit var etShortLabel: EditText
    private lateinit var etGroupOfAccount: EditText
    private lateinit var spinnerParentAccount: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_financial_accounting_account_chart_search)

            initViews()

        }

    private fun initViews() {
        etAccountNumber = findViewById(R.id.etAccountNumber)
        etLabel = findViewById(R.id.etLabel)
        etShortLabel = findViewById(R.id.etShortLabel)
        etGroupOfAccount = findViewById(R.id.etGroupOfAccount)
        spinnerParentAccount = findViewById(R.id.spinnerParentAccount)
        btnSearch = findViewById(R.id.btnSearch)

        setUpParentAccountSpinner()

        etAccountNumber.setText(chartAccountsFragment.accountNumber)
        etLabel.setText(chartAccountsFragment.label)
        etShortLabel.setText(chartAccountsFragment.shortLabel)
        etGroupOfAccount.setText(chartAccountsFragment.accountGroupId)

        for (i in parentAccountList.indices)
            if (parentAccountList[i]?.id == chartAccountsFragment.parentAccountId)
                spinnerParentAccount.setSelection(i)

        btnSearch.setOnClickListener {

            chartAccountsFragment.label = etLabel.text.toString()
            chartAccountsFragment.shortLabel = etShortLabel.text.toString()
            chartAccountsFragment.accountNumber = etAccountNumber.text.toString()
            chartAccountsFragment.accountGroupId = etGroupOfAccount.text.toString()

            val inputManager: InputMethodManager = Objects.requireNonNull(ownerActivity)
                ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            dismiss()
            chartAccountsFragment.chartAccounts.clear()
            chartAccountsFragment.CURRENT_PAGE = 1
            chartAccountsFragment.LAST_PAGE = 1

            chartAccountsFragment.getCHartAccounts()
        }
    }

    private fun setUpParentAccountSpinner() {
        val strings = ArrayList<String>()
        for (i in parentAccountList) {
            strings.add(i?.account_number!!)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerParentAccount.adapter = positionAdapter
        spinnerParentAccount.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))
        spinnerParentAccount.setTitle(context.resources.getString(R.string.parentAccountSpinnerTitle))
        spinnerParentAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    chartAccountsFragment.parentAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(ownerActivity!!)
                }
            }

    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        val inputManager: InputMethodManager = Objects.requireNonNull(ownerActivity)
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }


}