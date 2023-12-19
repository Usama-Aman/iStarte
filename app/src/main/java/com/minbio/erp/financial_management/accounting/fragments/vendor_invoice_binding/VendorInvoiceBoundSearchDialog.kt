package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.app.Dialog
import android.content.Context
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
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*

class VendorInvoiceBoundSearchDialog(
    context: Context,
    _countriesList: List<CountriesData>,
    _invoiceBoundFragment: VendorInvoiceBoundFragment
) : Dialog(context) {

    private var countriesList: MutableList<CountriesData> = ArrayList()
    private var invoiceBoundFragment = _invoiceBoundFragment

    init {
        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(_countriesList)
    }

    private lateinit var btnSearch: LinearLayout
    private lateinit var etIdLine: EditText
    private lateinit var etInvoice: EditText
    private lateinit var etInvoiceLabel: EditText
    private lateinit var etProductRef: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etTax: EditText
    private lateinit var etThirdParty: EditText
    private lateinit var etVatId: EditText
    private lateinit var etAccount: EditText


    private var monthsList = arrayOf(
        "", "January", "February", "March", "April", "May", "June", "July"
        , "August", "September", "October", "November", "December"
    )
    private var yearList: MutableList<String> = ArrayList()


    private lateinit var spinnerMonth: CustomSearchableSpinner
    private lateinit var spinnerYear: CustomSearchableSpinner
    private lateinit var spinnerCountry: CustomSearchableSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_vendor_bound_search)

        initViews()

    }

    private fun initViews() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        yearList.add("")
        for (i in 0 until 10)
            yearList.add((currentYear - i).toString())

        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        spinnerCountry = findViewById(R.id.spinnerCountry)

        etIdLine = findViewById(R.id.etIdLine)
        etInvoice = findViewById(R.id.etInvoice)
        etInvoiceLabel = findViewById(R.id.etInvoiceLabel)
        etProductRef = findViewById(R.id.etProductRef)
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etTax = findViewById(R.id.etTax)
        etThirdParty = findViewById(R.id.etThirdParty)
        etVatId = findViewById(R.id.etVatId)
        etAccount = findViewById(R.id.etAccount)

        setUpSpinners()

        btnSearch = findViewById(R.id.btnSearch)
        btnSearch.setOnClickListener {
            hideKeyboard()
            dismiss()

            invoiceBoundFragment.idLineToSearch = etIdLine.text.toString()
            invoiceBoundFragment.invoiceToSearch = etInvoice.text.toString()
            invoiceBoundFragment.invoiceLabelToSearch = etInvoiceLabel.text.toString()
            invoiceBoundFragment.productRefToSearch = etProductRef.text.toString()
            invoiceBoundFragment.productDescToSearch = etDescription.text.toString()
            invoiceBoundFragment.amountToSearch = etAmount.text.toString()
            invoiceBoundFragment.taxToSearch = etTax.text.toString()
            invoiceBoundFragment.thirdPartyToSearch = etThirdParty.text.toString()
            invoiceBoundFragment.vatIdToSearch = etVatId.text.toString()
            invoiceBoundFragment.accountNumberToSearch = etAccount.text.toString()

            invoiceBoundFragment.vendorInvoiceBoundData.clear()
            invoiceBoundFragment.CURRENT_PAGE = 1
            invoiceBoundFragment.LAST_PAGE = 1


            AppUtils.showDialog(ownerActivity!!)
            invoiceBoundFragment.getVendorInvoiceBound()
        }

        etIdLine.setText(invoiceBoundFragment.idLineToSearch)
        etInvoice.setText(invoiceBoundFragment.invoiceToSearch)
        etInvoiceLabel.setText(invoiceBoundFragment.invoiceLabelToSearch)
        etProductRef.setText(invoiceBoundFragment.productRefToSearch)
        etDescription.setText(invoiceBoundFragment.productDescToSearch)
        etAmount.setText(invoiceBoundFragment.amountToSearch)
        etTax.setText(invoiceBoundFragment.taxToSearch)
        etThirdParty.setText(invoiceBoundFragment.thirdPartyToSearch)
        etVatId.setText(invoiceBoundFragment.vatIdToSearch)
        etAccount.setText(invoiceBoundFragment.accountNumberToSearch)

        for (i in countriesList.indices)
            if (invoiceBoundFragment.countryIdToSearch == countriesList[i].id)
                spinnerCountry.setSelection(i)

        for (i in monthsList.indices)
            if (invoiceBoundFragment.monthToSearch == monthsList[i])
                spinnerMonth.setSelection(i)

        for (i in yearList.indices)
            if (invoiceBoundFragment.yearToSearch == yearList[i])
                spinnerYear.setSelection(i)

    }

    private fun setUpSpinners() {

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
                invoiceBoundFragment.monthToSearch = monthsList[adapterView.selectedItemPosition]
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
                invoiceBoundFragment.yearToSearch = yearList[adapterView.selectedItemPosition]
                hideKeyboard()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        val countriesStrings = ArrayList<String>()
        for (i in countriesList.indices) {
            countriesStrings.add(countriesList[i].name)
        }
        spinnerCountry.setPositiveButton(context.resources.getString(R.string.spinnerBtnClose))

        spinnerCountry.setTitle(context.resources.getString(R.string.countriesSpinnerTitle))

        val countriesAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, countriesStrings
        )
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = countriesAdapter
        spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                invoiceBoundFragment.countryIdToSearch =
                    countriesList[adapterView.selectedItemPosition].id
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