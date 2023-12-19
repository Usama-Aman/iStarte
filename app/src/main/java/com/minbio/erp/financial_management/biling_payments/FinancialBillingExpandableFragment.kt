package com.minbio.erp.financial_management.biling_payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.FinancialManagementAdapter
import com.minbio.erp.financial_management.interfaces.FragmentCallBack
import com.minbio.erp.financial_management.model.ExpandableItems
import com.minbio.erp.financial_management.model.ExpandableModel

class FinancialBillingExpandableFragment : Fragment(), FragmentCallBack {

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
                "billingCustomerInvoiceList",
                context!!.resources.getString(R.string.fbpListCustomerInvoiceList)
            )
        )
//        arrayList.add(
//            ExpandableItems(
//                "billingCustomerTemplateList",
//                context!!.resources.getString(R.string.fbpListCustomerTemplateList)
//            )
//        )
        arrayList.add(
            ExpandableItems(
                "billingCustomerPayments",
                context!!.resources.getString(R.string.fbpListCustomerPayments)
            )
        )
        arrayList.add(
            ExpandableItems(
                "billingCustomerReporting",
                context!!.resources.getString(R.string.fbpListCustomerReporting)
            )
        )
        arrayList.add(
            ExpandableItems(
                "billingCustomerStats",
                context!!.resources.getString(R.string.fbpListCustomerStatistics)
            )
        )

        val arrayList3 = ArrayList<ExpandableItems>()
        arrayList3.add(
            ExpandableItems(
                "billingVendorInvoiceList",
                context!!.resources.getString(R.string.fbpListVendorInvoiceList)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "billingVendorPayments",
                context!!.resources.getString(R.string.fbpListVendorPayments)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "billingVendorReporting",
                context!!.resources.getString(R.string.fbpListVendorReporting)
            )
        )
        arrayList3.add(
            ExpandableItems(
                "billingVendorStats",
                context!!.resources.getString(R.string.fbpListVendorStatistics)
            )
        )

        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.fbpListCustomerInvoice), false, arrayList
            )
        )
        expandableList.add(
            ExpandableModel(
                context!!.resources.getString(R.string.fbpListVendorInvoices), false, arrayList3
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