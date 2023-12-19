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
import androidx.constraintlayout.widget.ConstraintLayout
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import java.util.*
import kotlin.collections.ArrayList

class VendorInvoiceToBindSearchDialog(
    context: Context,
    _countriesList: List<CountriesData>,
    _invoiceToBindFragment: VendorInvoiceToBindFragment
) : Dialog(context) {

    private var countriesList: MutableList<CountriesData> = ArrayList()
    private var invoiceToBindFragment = _invoiceToBindFragment

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

    init {
        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(_countriesList)
    }


    private var monthsList = arrayOf(
        "", "January", "February", "March", "April", "May", "June", "July"
        , "August", "September", "October", "November", "December"
    )
    private var yearList: MutableList<String> = java.util.ArrayList()


    private lateinit var spinnerMonth: CustomSearchableSpinner
    private lateinit var spinnerYear: CustomSearchableSpinner
    private lateinit var spinnerCountry: CustomSearchableSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_financial_accounting_vendor_invoice_bind_search)

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

        setUpSpinners()

        btnSearch = findViewById(R.id.btnSearch)
        btnSearch.setOnClickListener {
            hideKeyboard()
            dismiss()

            invoiceToBindFragment.idLineToSearch = etIdLine.text.toString()
            invoiceToBindFragment.invoiceToSearch = etInvoice.text.toString()
            invoiceToBindFragment.invoiceLabelToSearch = etInvoiceLabel.text.toString()
            invoiceToBindFragment.productRefToSearch = etProductRef.text.toString()
            invoiceToBindFragment.productDescToSearch = etDescription.text.toString()
            invoiceToBindFragment.amountToSearch = etAmount.text.toString()
            invoiceToBindFragment.taxToSearch = etTax.text.toString()
            invoiceToBindFragment.thirdPartyToSearch = etThirdParty.text.toString()
            invoiceToBindFragment.vatIdToSearch = etVatId.text.toString()

            invoiceToBindFragment.vendorInvoiceToBindData.clear()
            invoiceToBindFragment.CURRENT_PAGE = 1
            invoiceToBindFragment.LAST_PAGE = 1

            AppUtils.showDialog(ownerActivity!!)
            invoiceToBindFragment.getVendorInvoiceToBind()
        }

        etIdLine.setText(invoiceToBindFragment.idLineToSearch)
        etInvoice.setText(invoiceToBindFragment.invoiceToSearch)
        etInvoiceLabel.setText(invoiceToBindFragment.invoiceLabelToSearch)
        etProductRef.setText(invoiceToBindFragment.productRefToSearch)
        etDescription.setText(invoiceToBindFragment.productDescToSearch)
        etAmount.setText(invoiceToBindFragment.amountToSearch)
        etTax.setText(invoiceToBindFragment.taxToSearch)
        etThirdParty.setText(invoiceToBindFragment.thirdPartyToSearch)
        etVatId.setText(invoiceToBindFragment.vatIdToSearch)

        for (i in countriesList.indices)
            if (invoiceToBindFragment.countryIdToSearch == countriesList[i].id)
                spinnerCountry.setSelection(i)

        for (i in monthsList.indices)
            if (invoiceToBindFragment.monthToSearch == monthsList[i])
                spinnerMonth.setSelection(i)

        for (i in yearList.indices)
            if (invoiceToBindFragment.yearToSearch == yearList[i])
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
                invoiceToBindFragment.monthToSearch = monthsList[adapterView.selectedItemPosition]
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
                invoiceToBindFragment.yearToSearch = yearList[adapterView.selectedItemPosition]
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
            context,
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
                invoiceToBindFragment.countryIdToSearch =
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