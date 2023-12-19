package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.minbio.erp.R
import com.minbio.erp.utils.CustomSearchableSpinner

class TurnOverInvoicedProductServiceSearchDialog (context: Context) : Dialog(context) {

    private lateinit var spinnerType : CustomSearchableSpinner
    private lateinit var spinnerThirdParty : CustomSearchableSpinner
    private lateinit var cbWithoutCategories : CheckBox
    private lateinit var cbSubCategories : CheckBox
    private lateinit var btnSearch : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_fa_turnover_invoiced_product_service_search)

        initViews()

    }

    private fun initViews() {
        cbWithoutCategories = findViewById(R.id.cbWithoutCategories)
        cbSubCategories = findViewById(R.id.cbSubCategories)
        spinnerType = findViewById(R.id.spinnerType)
        spinnerThirdParty = findViewById(R.id.spinnerThirdParty)
        btnSearch = findViewById(R.id.btnSearch)
    }

}