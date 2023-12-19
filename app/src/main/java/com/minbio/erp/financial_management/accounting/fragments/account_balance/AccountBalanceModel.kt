package com.minbio.erp.financial_management.accounting.fragments.account_balance

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class AccountBalanceModel(
    var `data`: List<AccountBalanceData>?,
    var links: Links?,
    var message: String?,
    var meta: Meta?,
    var status: Boolean?,
    var total_balance: String?,
    var total_credit: String?,
    var total_debit: String?
)

data class AccountBalanceData(
    var account_number: String?,
    var balance: String?,
    var chart_account_label: String?,
    var credit: String?,
    var debit: String?,
    var id: Int?,
    var opening_balance: String?,
    var subtotal_balance: String?,
    var subtotal_credit: String?,
    var subtotal_debit: String?
)