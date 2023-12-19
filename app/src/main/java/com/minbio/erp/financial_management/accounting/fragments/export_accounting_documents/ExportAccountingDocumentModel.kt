package com.minbio.erp.financial_management.accounting.fragments.export_accounting_documents

data class ExportAccountingDocumentModel(
    var `data`: List<ExportAccountingDocumentData>?,
    var expense_exc_tax: String?,
    var expense_inc_tax: String?,
    var expense_tax: String?,
    var income_exc_tax: String?,
    var income_inc_tax: String?,
    var income_tax: String?,
    var message: String?,
    var status: Boolean?,
    var total_exc_tax: String?,
    var total_inc_tax: String?,
    var total_tax: String?
)

data class ExportAccountingDocumentData(
    var code: String?,
    var country_code: String?,
    var date: String?,
    var due_date: String?,
    var id: String?,
    var ref: String?,
    var status: String?,
    var tax: String?,
    var third_party: String?,
    var total_exc_tax: String?,
    var total_inc_tax: String?,
    var type: String?,
    var vat_id: String?
)