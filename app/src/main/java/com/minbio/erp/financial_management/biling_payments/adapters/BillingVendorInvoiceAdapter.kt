package com.minbio.erp.financial_management.biling_payments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.models.BillingVendorInvoiceData

class BillingVendorInvoiceAdapter(_invoiceListData: MutableList<BillingVendorInvoiceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var invoiceListData = _invoiceListData
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_billing_customer_invoice, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = invoiceListData.size

    override fun getItemViewType(position: Int): Int {
        return if (invoiceListData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val thirdParty: TextView = itemView.findViewById(R.id.thirdParty)
        private val payMethod: TextView = itemView.findViewById(R.id.payMethod)
        private val debit: TextView = itemView.findViewById(R.id.debit)
        private val credit: TextView = itemView.findViewById(R.id.credit)
        private val overdue: TextView = itemView.findViewById(R.id.overdue)
        private val deliverType: TextView = itemView.findViewById(R.id.deliverType)
        private val amount: TextView = itemView.findViewById(R.id.amount)
        private val status: TextView = itemView.findViewById(R.id.status)


        fun bind(position: Int) {
            time.visibility = View.GONE
            deliverType.visibility = View.GONE

            ref.text = invoiceListData[position]?.supplier_id
            orderNo.text =
                invoiceListData[position]?.invoice_no /*Same View used  order no for invoice no*/
            date.text = invoiceListData[position]?.date
            thirdParty.text = invoiceListData[position]?.supplier_company_name
            payMethod.text = invoiceListData[position]?.payment_method
            debit.text = invoiceListData[position]?.debit
            credit.text = invoiceListData[position]?.credit
            overdue.text = invoiceListData[position]?.overdue
            amount.text = invoiceListData[position]?.total_payable_amount
            status.text = invoiceListData[position]?.status
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}