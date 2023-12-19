package com.minbio.erp.financial_management.bank_cash.fragments.financial

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import java.util.*

class FinancialAccountSearchDialog(
    _context: Context,
    _financialAccountListFragment: FinancialAccountListFragment
) : Dialog(_context) {

    private var statusStrings = arrayOf("", "Open", "Closed")
    private var financialAccountListFragment = _financialAccountListFragment

    private lateinit var etBankAccounts: EditText
    private lateinit var etLabel: EditText
    private lateinit var etNumber: EditText
    private lateinit var spinnerStatus: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_bank_account_search)

        initViews()

    }

    private fun initViews() {
        etBankAccounts = findViewById(R.id.etBankAccounts)
        etLabel = findViewById(R.id.etLabel)
        etNumber = findViewById(R.id.etNumber)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSearch = findViewById(R.id.btnSearch)


        setUpStatusSpinner()

        etBankAccounts.setText(financialAccountListFragment.bankAccountToSearch)
        etLabel.setText(financialAccountListFragment.labelToSearch)
        etNumber.setText(financialAccountListFragment.numberToSearch)

        for (i in statusStrings.indices)
            if (statusStrings[i].equals(financialAccountListFragment.statusToSearch, true))
                spinnerStatus.setSelection(i)

        btnSearch.setOnClickListener {
            AppUtils.showDialog(ownerActivity!!)
            hideKeyboard()
            dismiss()

            financialAccountListFragment.bankAccountToSearch = etBankAccounts.text.toString()
            financialAccountListFragment.labelToSearch = etLabel.text.toString()
            financialAccountListFragment.numberToSearch = etNumber.text.toString()

            financialAccountListFragment.financialAccountList.clear()
            financialAccountListFragment.CURRENT_PAGE = 1
            financialAccountListFragment.LAST_PAGE = 1
            financialAccountListFragment.getFinancialAccountList()
        }

    }

    private fun setUpStatusSpinner() {
        /*Status Spinner*/
        spinnerStatus.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerStatus.setTitle(context.resources.getString(R.string.statusSpinnerTitle))

        val adapter = ArrayAdapter(
            context,
            R.layout.dropdown_item, statusStrings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                financialAccountListFragment.statusToSearch =
                    statusStrings[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(ownerActivity!!)
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
}