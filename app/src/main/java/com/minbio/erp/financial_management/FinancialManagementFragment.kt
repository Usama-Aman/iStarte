package com.minbio.erp.financial_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.financial_management.accounting.FinancialAccountingExpandableFragment
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.AddChartAccountFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.AddChartModelFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.AddAccountingJournalFragment
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.AddExpenseReportAccountFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.LedgerGroupByAccountingFragment
import com.minbio.erp.financial_management.accounting.fragments.ledger.NewLedgerTransactionFragment
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.AddVatAccountFragment
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.AddPersonalizedGroupFragment
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.AddTaxAccountFragment
import com.minbio.erp.financial_management.bank_cash.FinancialBankCashExpandableFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.FinancialAddEntryFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.NewFinancialAccountFragment
import com.minbio.erp.financial_management.biling_payments.FinancialBillingExpandableFragment
import com.minbio.erp.main.MainActivity
import com.minbio.erp.utils.AppUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_financial_management.*

class FinancialManagementFragment : Fragment() {

    private lateinit var v: View

    lateinit var financialTab: RadioButton

/*
    lateinit var thirdPartyTab: RadioButton
    lateinit var productTab: RadioButton
    lateinit var agendaTab: RadioButton
    lateinit var commereceTab: RadioButton
    lateinit var documentTab: RadioButton
*/

    lateinit var billingTab: RadioButton
    lateinit var bankCashTab: RadioButton
    lateinit var acountingTab: RadioButton
    lateinit var companyImage: CircleImageView
    lateinit var companyName: TextView

    private lateinit var loginModel: LoginModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_management, container, false)

        (activity as MainActivity).setToolbarTitle(resources.getString(R.string.financialPageTitle))
        loginModel = AppUtils.getLoginModel(context!!)

        initViews()

        return v
    }

    private fun initViews() {
        financialTab = v.findViewById(R.id.financialTab)
//        thirdPartyTab = v.findViewById(R.id.financialTabThirdParties)
//        productTab = v.findViewById(R.id.financialTabProducts)
//        agendaTab = v.findViewById(R.id.financialTabAgenda)
//        commereceTab = v.findViewById(R.id.financialTabCommerce)
//        documentTab = v.findViewById(R.id.financialTabDocuments)
        acountingTab = v.findViewById(R.id.financialTabAccounting)
        bankCashTab = v.findViewById(R.id.financialTabBankCash)
        billingTab = v.findViewById(R.id.financialTabBilling)
        companyName = v.findViewById(R.id.companyName)
        companyImage = v.findViewById(R.id.companyImage)

        companyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(companyImage)

/*        thirdPartyTab.setOnClickListener { updateRightTabs(thirdPartyTab) }
        productTab.setOnClickListener { updateRightTabs(productTab) }
        agendaTab.setOnClickListener { updateRightTabs(agendaTab) }
        commereceTab.setOnClickListener { updateRightTabs(commereceTab) }
        documentTab.setOnClickListener { updateRightTabs(documentTab) }*/
        billingTab.setOnClickListener { updateRightTabs(billingTab) }
        bankCashTab.setOnClickListener { updateRightTabs(bankCashTab) }
        acountingTab.setOnClickListener { updateRightTabs(acountingTab) }

        billingTab.callOnClick()
    }

    private fun updateRightTabs(view: RadioButton) {
//        financialRightFragmentContainer.removeAllViews()

/*        thirdPartyTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        productTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        agendaTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        commereceTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        documentTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))*/
        billingTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        bankCashTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        acountingTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

/*        thirdPartyTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_third_party
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )
        productTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_financial_product
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )
        agendaTab.setCompoundDrawables(
            ContextCompat.getDrawable(context!!, R.drawable.ic_agenda)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )
        commereceTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_commerce
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )
        documentTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_documents
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        ) */
        billingTab.setCompoundDrawables(
            ContextCompat.getDrawable(context!!, R.drawable.ic_billing)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )

        bankCashTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_bank_cash
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )
        acountingTab.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_financial_accounting
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null, null, null
        )

        view.isChecked = true

        when (view.id) {
            R.id.financialTabThirdParties -> {
                checkRightTab(view, R.drawable.ic_third_party_sel, null)
            }
            R.id.financialTabProducts -> {
                checkRightTab(view, R.drawable.ic_financial_product_sel, null)
            }
            R.id.financialTabCommerce -> {
                checkRightTab(view, R.drawable.ic_commerece_sel, null)
            }
            R.id.financialTabBilling -> {
                checkRightTab(view, R.drawable.ic_billing_sel, null)
                launchLeftFragment(FinancialBillingExpandableFragment())
            }
            R.id.financialTabBankCash -> {
                checkRightTab(view, R.drawable.ic_bank_cash_sel, null)
                launchLeftFragment(FinancialBankCashExpandableFragment())
            }
            R.id.financialTabDocuments -> {
                checkRightTab(view, R.drawable.ic_document_sel, null)
            }
            R.id.financialTabAccounting -> {
                checkRightTab(view, R.drawable.ic_financial_accounting_sel, null)
                launchLeftFragment(FinancialAccountingExpandableFragment())
            }
            R.id.financialTabAgenda -> {
                checkRightTab(view, R.drawable.ic_agenda_sel, null)
            }
        }


    }

    private fun checkRightTab(radiobutton: RadioButton, drawableId: Int, fragment: Fragment?) {
        radiobutton.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorDarkBlue
            )
        )
        radiobutton.setCompoundDrawables(
            ContextCompat.getDrawable(
                context!!,
                drawableId
            )
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) },
            null,
            null,
            null
        )
    }

    private fun launchLeftFragment(fragment: Fragment?) {
        childFragmentManager.beginTransaction()
            .replace(R.id.financialLeftFragmentContainer, fragment!!)
            .commit()
    }

    fun launchRightFragment(fragment: Fragment?) {
        when (fragment) {
            is FinancialAddEntryFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddAccountingJournalFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddChartModelFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddChartAccountFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddPersonalizedGroupFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddVatAccountFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddTaxAccountFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is AddExpenseReportAccountFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is NewLedgerTransactionFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is NewFinancialAccountFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            is LedgerGroupByAccountingFragment -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment)
                .addToBackStack("")
                .commit()
            else -> childFragmentManager.beginTransaction()
                .replace(R.id.financialRightFragmentContainer, fragment!!)
                .commit()
        }
    }
}