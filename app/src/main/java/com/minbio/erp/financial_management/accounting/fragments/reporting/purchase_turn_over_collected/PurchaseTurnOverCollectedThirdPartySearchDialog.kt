package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*

class PurchaseTurnOverCollectedThirdPartySearchDialog(
    context: Context,
    purchaseTurnOverCollectedThirdPartyFragment: PurchaseTurnOverCollectedThirdPartyFragment,
    _countriesModel: CountriesModel
) : Dialog(context) {

    private val fragment = purchaseTurnOverCollectedThirdPartyFragment

    private lateinit var etCompany: EditText
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var btnSearch: LinearLayout
    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private var countriesModel: CountriesModel

    init {

        countriesList.clear()
        countriesModel = _countriesModel

        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(countriesModel.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_turn_over_invoice_third_party_search)

        initViews()

    }

    private fun initViews() {
        etCompany = findViewById(R.id.etCompany)
        spinnerCountry = findViewById(R.id.spinnerCountry)
        btnSearch = findViewById(R.id.btnSearch)
        setUpCountrySpinner()


        etCompany.setText(fragment.supplierNameToSearch)
        for (i in countriesList.indices)
            if (fragment.countryIdToSearch == countriesList[i]?.id)
                spinnerCountry.setSelection(i)

        btnSearch.setOnClickListener {
            hideKeyboard()
            dismiss()
            fragment.supplierNameToSearch = etCompany.text.toString()
            fragment.getThirdPartyData()
        }
    }

    private fun setUpCountrySpinner() {
        val strings = ArrayList<String>()
        for (i in countriesList) {
            strings.add(i!!.name)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerCountry.adapter = positionAdapter
        spinnerCountry.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))
        spinnerCountry.setTitle(context.resources.getString(R.string.countriesSpinnerTitle))
        spinnerCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    fragment.countryIdToSearch = countriesList[position]?.id!!
                    AppUtils.hideKeyboard(ownerActivity!!)
                }
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