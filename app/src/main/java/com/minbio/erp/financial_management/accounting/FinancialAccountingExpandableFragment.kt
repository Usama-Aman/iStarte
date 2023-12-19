package com.minbio.erp.financial_management.accounting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementAdapter
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.interfaces.FragmentCallBack
import com.minbio.erp.financial_management.model.ExpandableItems
import com.minbio.erp.financial_management.model.ExpandableModel
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.financial_management.model.JournalCodeAccountModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject

class FinancialAccountingExpandableFragment : Fragment(), FragmentCallBack, ResponseCallBack {

    private lateinit var v: View
    private lateinit var expandableListView: ExpandableListView
    private lateinit var financialBCAdapter: FinancialManagementAdapter

    private var expandableList: ArrayList<ExpandableModel> = ArrayList()
    private var journalCodeAccountData: MutableList<JournalCodeAccountData?> = ArrayList()
    private lateinit var api: Api

    var journalBroadCastReceiver = (object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null)
                if (intent.action == "accountingListRefresh") {
                    journalCodeAccountData.clear()
                    expandableList.clear()

                    AppUtils.showDialog(activity!!)
                    getAccountingJournals()
                }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_expandable, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(journalBroadCastReceiver, IntentFilter("accountingListRefresh"))

        AppUtils.showDialog(context!!)
        getAccountingJournals()

        return v
    }

    fun getAccountingJournals() {
        val call = api.getJournalAccountForMenu()
        RetrofitClient.apiCall(call, this, "JournalCodeAccounts")
    }

    private fun createList() {
        val arrayList1 = ArrayList<ExpandableItems>()
//        arrayList1.add(
//            ExpandableItems(
//                "financialAccountingSetupGeneral",
//                context!!.resources.getString(R.string.faListAccountingSetupGeneral)
//            )
//        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingAccountsJournal",
                context!!.resources.getString(R.string.faListAccountingJournals)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingChartAccountingModel",
                context!!.resources.getString(R.string.faListChartAccountingModels)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingAccountsChart",
                context!!.resources.getString(R.string.faListAccountsChart)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingPersonalizedGroups",
                context!!.resources.getString(R.string.faListPersonalizedGroups)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingDefaultAccounts",
                context!!.resources.getString(R.string.faListDefaultAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingBankAccounts",
                context!!.resources.getString(R.string.faListBankAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingVATAccounts",
                context!!.resources.getString(R.string.faListVATAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingTAXAccounts",
                context!!.resources.getString(R.string.faListTAXAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingExpenseReportAccounts",
                context!!.resources.getString(R.string.faListExpenseReportAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingProductAccounts",
                context!!.resources.getString(R.string.faListProductAccounts)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialAccountingExportOptions",
                context!!.resources.getString(R.string.faListExportOptions)
            )
        )

        val arrayList2 = ArrayList<ExpandableItems>()
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferCustomerInvoice",
                context!!.resources.getString(R.string.faListCustomerInvoiceBinding)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferInvoiceLineBind",
                context!!.resources.getString(R.string.faListCustomerInvoiceLinesBinding)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferInvoiceLineBound",
                context!!.resources.getString(R.string.faListCustomerInvoiceLinesBound)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferVendorInvoiceBinding",
                context!!.resources.getString(R.string.faListVendorInvoiceBinding)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferVendorLineBind",
                context!!.resources.getString(R.string.faListVendorInvoiceLinesToBind)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferVendorLineBound",
                context!!.resources.getString(R.string.faListVendorInvoiceLinesToBound)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferExpenseReportBinding",
                context!!.resources.getString(R.string.faListExpenseReportBinding)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferExpenseLineBind",
                context!!.resources.getString(R.string.faListExpenseReportLinesToBind)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingTransferExpenseLineBound",
                context!!.resources.getString(R.string.faListExpenseReportLinesToBound)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialAccountingRegistrationAccounting",
                context!!.resources.getString(R.string.faListRegistrationAccounting)
            )
        )

        if (journalCodeAccountData.isNullOrEmpty())
            arrayList2.add(
                ExpandableItems(
                    "noJournalDefined",
                    "      No journal defined"
                )
            )
        else {
            for (i in journalCodeAccountData.indices)
                arrayList2.add(
                    ExpandableItems(
                        journalCodeAccountData[i]?.code!!,
                        "      ${journalCodeAccountData[i]?.label!!}"
                    )
                )
        }


        val arrayList3 = ArrayList<ExpandableItems>()
        arrayList3.add(
            ExpandableItems(
                "financialAccountingAccountingLedger",
                context!!.resources.getString(R.string.faListAccountingLedger)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingAccountingAccountBalance",
                context!!.resources.getString(R.string.faListFinancialAccountingAccountBalance)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingAccountingExportDocuments",
                context!!.resources.getString(R.string.faListExportAccountingDocuments)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialReporting",
                context!!.resources.getString(R.string.faListFinancialReporting)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialIncomeExpense",
                context!!.resources.getString(R.string.faListFinancialIncomeExpense)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialIncomeExpensePredefined",
                context!!.resources.getString(R.string.faListIncomeExpensePredefinedGroups)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialIncomeExpensePersonalized",
                context!!.resources.getString(R.string.faListIncomeExpensePersonalizedGroups)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverInvoiced",
                context!!.resources.getString(R.string.faListTurnOverInvoiced)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverThirdParties",
                context!!.resources.getString(R.string.faListInvoicedThirdParties)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverUser",
                context!!.resources.getString(R.string.faListInvoicedUser)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverProductService",
                context!!.resources.getString(R.string.faListInvoicedProductServices)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverSalesTAXRate",
                context!!.resources.getString(R.string.faListInvoicedSaleTaxRate)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverCollected",
                context!!.resources.getString(R.string.faListTurnOverCollected)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverCollectedThirdParties",
                context!!.resources.getString(R.string.faListCollectedThirdParties)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialTurnOverCollectedUser",
                context!!.resources.getString(R.string.faListCollectedUser)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialPurchaseTurnOverInvoiced",
                context!!.resources.getString(R.string.faListPurchaseTurnOverInvoiced)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialPurchaseTurnOverInvoicedThirdParty",
                context!!.resources.getString(R.string.faListPurchaseInvoicedThirdParties)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialPurchaseTurnOverInvoicedProductService",
                context!!.resources.getString(R.string.faListPurchaseInvoicedProductService)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialPurchaseTurnOverCollected",
                context!!.resources.getString(R.string.faListPurchaseTurnOverCollected)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "financialAccountingFinancialPurchaseTurnOverCollectedThirdParties",
                context!!.resources.getString(R.string.faListPurchaseCollectedThirdParties)
            )
        )

        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.faListFinancialAccountingSetup),
                true,
                arrayList1
            )
        )
        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.faListTransferAccounting),
                true,
                arrayList2
            )
        )
        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.faListFinancialAccounting),
                true,
                arrayList3
            )
        )

    }

    private fun setAdapter() {
        expandableListView = v.findViewById(R.id.expandableList)
        financialBCAdapter =
            FinancialManagementAdapter(
                context!!,
                expandableList,
                this,
                journalCodeAccountData
            )
        expandableListView.setAdapter(financialBCAdapter)

        for (i in expandableList.indices)
            expandableListView.expandGroup(i)
    }

    override fun launchFragment(fragment: Fragment) {
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "JournalCodeAccounts" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), JournalCodeAccountModel::class.java)
                journalCodeAccountData.addAll(model.data)

                createList()
                setAdapter()
            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(journalBroadCastReceiver)
    }


}