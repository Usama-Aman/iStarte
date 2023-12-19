package com.minbio.erp.financial_management.accounting.fragments.product_accounts

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
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*

class ProductAccountSearchDialog(
    context: Context,
    _productAccountFragment: ProductAccountsFragment
) : Dialog(context) {

    private val productAccountFragment = _productAccountFragment
    private val dedicatedAccountStatus =
        arrayOf("", "Without valid dedicated account", "With valid dedicated account")

    private lateinit var etRef: EditText
    private lateinit var etLabel: EditText
    private lateinit var etCurrentDedicationNumber: EditText
    private lateinit var spinnerDedicatedAccount: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_product_account_search)

        initViews()

    }

    private fun initViews() {
        btnSearch = findViewById(R.id.btnSearch)
        etRef = findViewById(R.id.etRef)
        etLabel = findViewById(R.id.etLabel)
        etCurrentDedicationNumber = findViewById(R.id.etCurrentDedicationNumber)
        spinnerDedicatedAccount = findViewById(R.id.spinnerDedicatedAccount)
        setUpParentAccountSpinner()


        etRef.setText(productAccountFragment.refToSearch)
        etLabel.setText(productAccountFragment.labelToSearch)
        etCurrentDedicationNumber.setText(productAccountFragment.dedicatedAccountNumberToSearch)

        when (productAccountFragment.dedicatedAccountIdToSearch) {
            "" -> spinnerDedicatedAccount.setSelection(0)
            "without_account" -> spinnerDedicatedAccount.setSelection(1)
            "with_account" -> spinnerDedicatedAccount.setSelection(2)
        }


        btnSearch.setOnClickListener {
            AppUtils.showDialog(ownerActivity!!)

            productAccountFragment.refToSearch = etRef.text.toString()
            productAccountFragment.labelToSearch = etLabel.text.toString()
            productAccountFragment.dedicatedAccountNumberToSearch =
                etCurrentDedicationNumber.text.toString()

            hideKeyboard()
            dismiss()
            productAccountFragment.productAccountsData.clear()
            productAccountFragment.CURRENT_PAGE = 1
            productAccountFragment.LAST_PAGE = 1

            productAccountFragment.getProductAccounts()
        }

    }

    private fun setUpParentAccountSpinner() {
        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter(this.context, R.layout.dropdown_item, dedicatedAccountStatus)
        spinnerDedicatedAccount.adapter = positionAdapter
        spinnerDedicatedAccount.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))
        spinnerDedicatedAccount.setTitle(context.resources.getString(R.string.parentAccountSpinnerTitle))
        spinnerDedicatedAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    when (position) {
                        0 -> productAccountFragment.dedicatedAccountIdToSearch = ""
                        1 -> productAccountFragment.dedicatedAccountIdToSearch = "without_account"
                        else -> productAccountFragment.dedicatedAccountIdToSearch = "with_account"
                    }



                    hideKeyboard()
                }
            }

    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        hideKeyboard()
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