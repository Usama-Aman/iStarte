package com.minbio.erp.financial_management

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.financial_management.accounting.fragments.account_balance.AccountBalanceFragment
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.ChartAccountsFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.ChartModelFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.AccountingJournalFragment
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.CustomerInvoiceBindingFragment
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.InvoiceBoundFragment
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.InvoiceToBindFragment
import com.minbio.erp.financial_management.accounting.fragments.default_accounts.DefaultAccountsFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.ExpenseReportAccountsFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.ExpenseBoundFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.ExpenseReportBindingFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.ExpenseToBindFragment
import com.minbio.erp.financial_management.accounting.fragments.export_accounting_documents.ExportAccountingDocumentFragment
import com.minbio.erp.financial_management.accounting.fragments.export_options.ExportOptionsFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.LedgerFragment
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.VatAccountsFragment
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.PersonalizedGroupsFragment
import com.minbio.erp.financial_management.accounting.fragments.product_accounts.ProductAccountsFragment
import com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting.FinanceJournalFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.ExpenseIncomeReportingFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.PersonalizedGroupReportingFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.PreDefinedGroupReportingFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected.PurchaseTurnOverCollectedFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected.PurchaseTurnOverCollectedThirdPartyFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.PurchaseTurnOverInvoicedFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.PurchaseTurnOverInvoicedProductServiceFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.PurchaseTurnOverInvoicedThirdPartyFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.TurnOverCollectedFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.TurnOverCollectedThirdPartyFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.TurnOverCollectedUserFragment
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.*
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.TaxAccountFragment
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.VendorInvoiceBindingFragment
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.VendorInvoiceBoundFragment
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.VendorInvoiceToBindFragment
import com.minbio.erp.financial_management.model.ExpandableModel
import com.minbio.erp.financial_management.bank_cash.fragments.financial.FinancialAccountListFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.FinancialEntriesFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.FinancialInternalTransferFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.NewFinancialAccountFragment
import com.minbio.erp.financial_management.biling_payments.fragments.*
import com.minbio.erp.financial_management.interfaces.FragmentCallBack
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys

class FinancialManagementAdapter(
    _context: Context,
    _expandableList: ArrayList<ExpandableModel>,
    _fragmentCallBack: FragmentCallBack,
    _journalCodeAccountData: MutableList<JournalCodeAccountData?>?
) :
    BaseExpandableListAdapter() {

    private var context = _context
    private var expandableList = _expandableList
    private var fragmentCallBack = _fragmentCallBack
    private var journalCodeAccountData = _journalCodeAccountData
    private var clickedTitleKey = "-1"
    private var loginModel: LoginModel
    private var permissionsList: List<String>

    init {
        loginModel = AppUtils.getLoginModel(context)
        permissionsList = loginModel.data.permissions.accounting.split(",")
    }

    override fun getGroup(groupPosition: Int): Any = expandableList[groupPosition]

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.item_financial_group, null)
        }

        val title: TextView = view!!.findViewById(R.id.groupName)
        val expandableTitleConstraint: ConstraintLayout =
            view.findViewById(R.id.expandableTitleConstraint)
        title.text = expandableList[groupPosition].title

//        expandableTitleConstraint.setOnClickListener {
//            /*item title click*/
//        }

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int =
        expandableList[groupPosition].subTitles.size

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        expandableList[groupPosition].subTitles[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.item_financial_child, null)
        }

        val title: TextView = view!!.findViewById(R.id.childName)
        title.text = expandableList[groupPosition].subTitles[childPosition].name

        if (clickedTitleKey == expandableList[groupPosition].subTitles[childPosition].key) {
            title.setTextColor(ContextCompat.getColor(context, R.color.colorDarkBlue))

        } else {
            title.setTextColor(ContextCompat.getColor(context, R.color.colorInputLbl))
        }


        title.setOnClickListener {
            AppUtils.preventTwoClick(title)
            clickedTitleKey = expandableList[groupPosition].subTitles[childPosition].key

            if (clickedTitleKey != "financialAccountingRegistrationAccounting" && clickedTitleKey != "noJournalDefined" && clickedTitleKey != "financialAccountingFinancialReporting") {
                checkForFragment(clickedTitleKey)
                notifyDataSetChanged()
            }
        }

        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = expandableList.size

    private fun checkForFragment(clickedTitleKey: String) {
        when {
            clickedTitleKey == "financialNewAccount" -> {
                if (permissionsList.contains(PermissionKeys.create_financial_accounts)) {
                    val fragment =
                        NewFinancialAccountFragment()
                    val bundle = Bundle()
                    bundle.putBoolean("fromMain", true)
                    fragment.arguments = bundle
                    fragmentCallBack.launchFragment(fragment)
                } else
                    showPermissionToast()
            }
            clickedTitleKey == "financialAccountList" -> {
                if (permissionsList.contains(PermissionKeys.view_financial_accounts))
                    fragmentCallBack.launchFragment(FinancialAccountListFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "financialEntriesList" -> {
                if (permissionsList.contains(PermissionKeys.transactions_entries_list))
                    fragmentCallBack.launchFragment(FinancialEntriesFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "financialInternalTransfer" -> {
                if (permissionsList.contains(PermissionKeys.internal_transfers))
                    fragmentCallBack.launchFragment(FinancialInternalTransferFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "billingCustomerInvoiceList" -> {
                if (permissionsList.contains(PermissionKeys.view_orders))
                    fragmentCallBack.launchFragment(BillingCustomerInvoiceListFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "billingVendorInvoiceList" -> {
                if (permissionsList.contains(PermissionKeys.view_orders))
                    fragmentCallBack.launchFragment(BillingVendorInvoiceListFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "billingCustomerPayments" -> {
                if (permissionsList.contains(PermissionKeys.view_customers_payments))
                    fragmentCallBack.launchFragment(BillingCustomerPaymentFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "billingVendorPayments" -> {
                if (permissionsList.contains(PermissionKeys.view_vendors_payments))
                    fragmentCallBack.launchFragment(BillingVendorPaymentFragment())
                else
                    showPermissionToast()
            }
            clickedTitleKey == "billingCustomerStats" -> {
                if (permissionsList.contains(PermissionKeys.view_customers_stats)) {
                    val bundle = Bundle()
                    bundle.putInt("fromWhere", 0)    /*O for from Customer 1 for vendor*/
                    val fragment = BillingStatisticsFragment()
                    fragment.arguments = bundle
                    fragmentCallBack.launchFragment(fragment)
                } else
                    showPermissionToast()
            }
            clickedTitleKey == "billingVendorStats" -> {
                if (permissionsList.contains(PermissionKeys.view_vendors_stats)) {
                    val bundle = Bundle()
                    bundle.putInt("fromWhere", 1)    /*O for from Customer 1 for vendor*/
                    val fragment = BillingStatisticsFragment()
                    fragment.arguments = bundle
                    fragmentCallBack.launchFragment(fragment)
                } else
                    showPermissionToast()

            }
            clickedTitleKey == "billingCustomerReporting" -> {
                if (permissionsList.contains(PermissionKeys.view_customers_report)) {
                    val bundle = Bundle()
                    bundle.putInt("fromWhere", 0)    /*O for from Customer 1 for vendor*/
                    val fragment = BillingReportingFragment()
                    fragment.arguments = bundle
                    fragmentCallBack.launchFragment(fragment)
                } else
                    showPermissionToast()
            }
            clickedTitleKey == "billingVendorReporting" -> {
                if (permissionsList.contains(PermissionKeys.view_customers_report)) {
                    val bundle = Bundle()
                    bundle.putInt("fromWhere", 1)    /*O for from Customer 1 for vendor*/
                    val fragment = BillingReportingFragment()
                    fragment.arguments = bundle
                    fragmentCallBack.launchFragment(fragment)
                } else
                    showPermissionToast()
            }
            clickedTitleKey == "financialAccountingAccountsJournal" -> {
                fragmentCallBack.launchFragment(AccountingJournalFragment())
            }
            clickedTitleKey == "financialAccountingChartAccountingModel" -> {
                fragmentCallBack.launchFragment(ChartModelFragment())
            }
            clickedTitleKey == "financialAccountingAccountsChart" -> {
                fragmentCallBack.launchFragment(ChartAccountsFragment())
            }
            clickedTitleKey == "financialAccountingPersonalizedGroups" -> {
                fragmentCallBack.launchFragment(PersonalizedGroupsFragment())
            }
            clickedTitleKey == "financialAccountingDefaultAccounts" -> {
                fragmentCallBack.launchFragment(DefaultAccountsFragment())
            }
            clickedTitleKey == "financialAccountingBankAccounts" -> {
                fragmentCallBack.launchFragment(FinancialAccountListFragment())
            }
            clickedTitleKey == "financialAccountingVATAccounts" -> {
                fragmentCallBack.launchFragment(VatAccountsFragment())
            }
            clickedTitleKey == "financialAccountingTAXAccounts" -> {
                fragmentCallBack.launchFragment(TaxAccountFragment())
            }
            clickedTitleKey == "financialAccountingExpenseReportAccounts" -> {
                fragmentCallBack.launchFragment(ExpenseReportAccountsFragment())
            }
            clickedTitleKey == "financialAccountingExportOptions" -> {
                fragmentCallBack.launchFragment(ExportOptionsFragment())
            }
            clickedTitleKey == "financialAccountingProductAccounts" -> {
                fragmentCallBack.launchFragment(ProductAccountsFragment())
            }
            clickedTitleKey == "financialAccountingTransferCustomerInvoice" -> {
                fragmentCallBack.launchFragment(CustomerInvoiceBindingFragment())
            }
            clickedTitleKey == "financialAccountingTransferExpenseReportBinding" -> {
                fragmentCallBack.launchFragment(ExpenseReportBindingFragment())
            }
            clickedTitleKey == "financialAccountingTransferInvoiceLineBind" -> {
                fragmentCallBack.launchFragment(InvoiceToBindFragment())
            }
            clickedTitleKey == "financialAccountingTransferInvoiceLineBound" -> {
                fragmentCallBack.launchFragment(InvoiceBoundFragment())
            }
            clickedTitleKey == "financialAccountingTransferExpenseLineBind" -> {
                fragmentCallBack.launchFragment(ExpenseToBindFragment())
            }
            clickedTitleKey == "financialAccountingTransferExpenseLineBound" -> {
                fragmentCallBack.launchFragment(ExpenseBoundFragment())
            }
            clickedTitleKey == "financialAccountingTransferVendorInvoiceBinding" -> {
                fragmentCallBack.launchFragment(VendorInvoiceBindingFragment())
            }
            clickedTitleKey == "financialAccountingTransferVendorLineBind" -> {
                fragmentCallBack.launchFragment(VendorInvoiceToBindFragment())
            }
            clickedTitleKey == "financialAccountingTransferVendorLineBound" -> {
                fragmentCallBack.launchFragment(VendorInvoiceBoundFragment())
            }
            clickedTitleKey == "financialAccountingAccountingLedger" -> {
                fragmentCallBack.launchFragment(LedgerFragment())
            }
            clickedTitleKey == "financialAccountingAccountingAccountBalance" -> {
                fragmentCallBack.launchFragment(AccountBalanceFragment())
            }
            clickedTitleKey == "financialAccountingAccountingExportDocuments" -> {
                fragmentCallBack.launchFragment(ExportAccountingDocumentFragment())
            }
            clickedTitleKey == "financialAccountingFinancialIncomeExpense" -> {
                fragmentCallBack.launchFragment(ExpenseIncomeReportingFragment())
            }
            clickedTitleKey == "financialAccountingFinancialIncomeExpensePredefined" -> {
                fragmentCallBack.launchFragment(PreDefinedGroupReportingFragment())
            }
            clickedTitleKey == "financialAccountingFinancialIncomeExpensePersonalized" -> {
                fragmentCallBack.launchFragment(PersonalizedGroupReportingFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverInvoiced" -> {
                fragmentCallBack.launchFragment(TurnOverInvoiceFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverThirdParties" -> {
                fragmentCallBack.launchFragment(TurnOverInvoiceThirdPartyFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverUser" -> {
                fragmentCallBack.launchFragment(TurnOverInvoiceUserFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverProductService" -> {
                fragmentCallBack.launchFragment(TurnOverInvoicedProductServiceFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverSalesTAXRate" -> {
                fragmentCallBack.launchFragment(TurnOverInvoicedSaleTaxFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverCollected" -> {
                fragmentCallBack.launchFragment(TurnOverCollectedFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverCollectedThirdParties" -> {
                fragmentCallBack.launchFragment(TurnOverCollectedThirdPartyFragment())
            }
            clickedTitleKey == "financialAccountingFinancialTurnOverCollectedUser" -> {
                fragmentCallBack.launchFragment(TurnOverCollectedUserFragment())
            }
            clickedTitleKey == "financialAccountingFinancialPurchaseTurnOverInvoiced" -> {
                fragmentCallBack.launchFragment(PurchaseTurnOverInvoicedFragment())
            }
            clickedTitleKey == "financialAccountingFinancialPurchaseTurnOverInvoicedThirdParty" -> {
                fragmentCallBack.launchFragment(PurchaseTurnOverInvoicedThirdPartyFragment())
            }
            clickedTitleKey == "financialAccountingFinancialPurchaseTurnOverInvoicedProductService" -> {
                fragmentCallBack.launchFragment(PurchaseTurnOverInvoicedProductServiceFragment())
            }
            clickedTitleKey == "financialAccountingFinancialPurchaseTurnOverCollected" -> {
                fragmentCallBack.launchFragment(PurchaseTurnOverCollectedFragment())
            }
            clickedTitleKey == "financialAccountingFinancialPurchaseTurnOverCollectedThirdParties" -> {
                fragmentCallBack.launchFragment(PurchaseTurnOverCollectedThirdPartyFragment())
            }
            journalCodeAccountData != null -> {
                if (journalCodeAccountData!!.isNotEmpty())
                    outer@ for (i in journalCodeAccountData!!.indices)
                        if (clickedTitleKey == journalCodeAccountData!![i]?.code) {
                            val fragment = FinanceJournalFragment()
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                "journalList", ArrayList<Parcelable>(
                                    journalCodeAccountData!!
                                )
                            )
                            bundle.putString("journalCode", journalCodeAccountData!![i]?.code)
                            bundle.putInt("journalId", journalCodeAccountData!![i]?.id!!)
                            bundle.putString("journalLabel", journalCodeAccountData!![i]?.label)
                            journalCodeAccountData!![i]?.journal_nature_id?.let {
                                bundle.putInt(
                                    "journalNature",
                                    it!!
                                )
                            }
                            fragment.arguments = bundle

                            fragmentCallBack.launchFragment(fragment)
                            break@outer
                        }
            }
        }
    }

    private fun showPermissionToast() {
        AppUtils.showToast(
            context as AppCompatActivity,
            context.resources.getString(R.string.dontHaveModulePermission),
            false
        )
    }
}