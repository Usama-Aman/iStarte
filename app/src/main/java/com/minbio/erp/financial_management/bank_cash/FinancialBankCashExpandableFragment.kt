package com.minbio.erp.financial_management.bank_cash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ExpandableModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.FinancialManagementAdapter
import com.minbio.erp.financial_management.interfaces.FragmentCallBack
import com.minbio.erp.financial_management.model.ExpandableItems

class FinancialBankCashExpandableFragment : Fragment(), FragmentCallBack {

    private lateinit var v: View
    private lateinit var expandableListView: ExpandableListView
    private lateinit var financialBCAdapter: FinancialManagementAdapter

    private var expandableList: ArrayList<ExpandableModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_expandable, container, false)

        createList()
        setAdapter()

        return v
    }

    private fun createList() {
        val arrayList = ArrayList<ExpandableItems>()
        arrayList.add(
            ExpandableItems(
                "financialNewAccount",

                context!!.resources.getString(R.string.nfaListChildTitleNewFinancialAccount)
            )
        )
        arrayList.add(
            ExpandableItems(
                "financialAccountList",
                context!!.resources.getString(R.string.nfaListChildTitleFinancialAccountList)
            )
        )
        arrayList.add(
            ExpandableItems(
                "financialEntriesList",
                context!!.resources.getString(R.string.nfaListChildTitleListEntries)
            )
        )
//        arrayList.add(
//            ExpandableItems(
//                "financialEntriesCategoryList",
//                context!!.resources.getString(R.string.nfaListChildTitleListEntriesCategory)
//            )
//        )
        arrayList.add(
            ExpandableItems(
                "financialInternalTransfer",
                context!!.resources.getString(R.string.nfaListChildTitleInternalTransfer)
            )
        )

        val arrayList1 = ArrayList<ExpandableItems>()
        arrayList1.add(
            ExpandableItems(
                "financialNewDirectDebit",
                context!!.resources.getString(R.string.nfaListChildTitleNewDirectDebitOrders)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialDirectDebitOrders",
                context!!.resources.getString(R.string.nfaListGroupTitleDirectDebitOrders)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialDirectDebitOrderLines",
                context!!.resources.getString(R.string.nfaListChildTitleDirectDebitOrderLines)
            )
        )
        arrayList1.add(
            ExpandableItems(
                "financialDebitRejects",
                context!!.resources.getString(R.string.nfaListChildTitleRejects)
            )
        )

        val arrayList2 = ArrayList<ExpandableItems>()
        arrayList2.add(
            ExpandableItems(
                "financialNewDeposits",
                context!!.resources.getString(R.string.nfaListChildTitleNewDeposits)
            )
        )
        arrayList2.add(
            ExpandableItems(
                "financialDepositList",
                context!!.resources.getString(R.string.nfaListChildTitleDepositList)
            )
        )

        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.nfaListGroupTitleBankCash), false, arrayList
            )
        )
//        expandableList.add(
//            ExpandableModel(
//                context!!.resources.getString(R.string.nfaListGroupTitleDirectDebitOrders),
//                false,
//                arrayList1
//            )
//        )
//        expandableList.add(
//            ExpandableModel(
//                context!!.resources.getString(R.string.nfaListGroupTitleCheckDeposits),
//                false,
//                arrayList2
//            )
//        )
    }

    private fun setAdapter() {
        expandableListView = v.findViewById(R.id.expandableList)
        financialBCAdapter =
            FinancialManagementAdapter(
                context!!,
                expandableList,
                this,
                null
            )
        expandableListView.setAdapter(financialBCAdapter)

        for (i in expandableList.indices)
            expandableListView.expandGroup(i)
    }

    override fun launchFragment(fragment: Fragment) {
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

}