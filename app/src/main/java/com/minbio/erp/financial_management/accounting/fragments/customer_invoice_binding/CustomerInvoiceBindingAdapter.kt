package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.CustomerInvoiceBindingData

class CustomerInvoiceBindingAdapter(
    _customerInvoiceBindingData: CustomerInvoiceBindingData?,
    _isBound: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var customerInvoiceBindingData = _customerInvoiceBindingData
    private var isBound = _isBound

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_customer_invoice_binding, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return when {
            customerInvoiceBindingData == null -> 0
            isBound -> customerInvoiceBindingData?.bound?.size!!
            customerInvoiceBindingData?.tobind != null -> 1
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val account: TextView = itemView.findViewById(R.id.account)
        private val label: TextView = itemView.findViewById(R.id.label)
        private val jan: TextView = itemView.findViewById(R.id.jan)
        private val feb: TextView = itemView.findViewById(R.id.feb)
        private val mar: TextView = itemView.findViewById(R.id.mar)
        private val apr: TextView = itemView.findViewById(R.id.apr)
        private val may: TextView = itemView.findViewById(R.id.may)
        private val jun: TextView = itemView.findViewById(R.id.jun)
        private val jul: TextView = itemView.findViewById(R.id.jul)
        private val aug: TextView = itemView.findViewById(R.id.aug)
        private val sep: TextView = itemView.findViewById(R.id.sep)
        private val oct: TextView = itemView.findViewById(R.id.oct)
        private val nov: TextView = itemView.findViewById(R.id.nov)
        private val dec: TextView = itemView.findViewById(R.id.dec)
        private val total: TextView = itemView.findViewById(R.id.total)

        fun bind(position: Int) {
            if (isBound) {
                account.text = customerInvoiceBindingData?.bound!![position].account_number
                label.text = customerInvoiceBindingData?.bound!![position].label
                jan.text = customerInvoiceBindingData?.bound!![position].report.Jan
                feb.text = customerInvoiceBindingData?.bound!![position].report.Feb
                mar.text = customerInvoiceBindingData?.bound!![position].report.Mar
                apr.text = customerInvoiceBindingData?.bound!![position].report.Apr
                may.text = customerInvoiceBindingData?.bound!![position].report.May
                jun.text = customerInvoiceBindingData?.bound!![position].report.Jun
                jul.text = customerInvoiceBindingData?.bound!![position].report.Jul
                aug.text = customerInvoiceBindingData?.bound!![position].report.Aug
                sep.text = customerInvoiceBindingData?.bound!![position].report.Sep
                oct.text = customerInvoiceBindingData?.bound!![position].report.Oct
                nov.text = customerInvoiceBindingData?.bound!![position].report.Nov
                dec.text = customerInvoiceBindingData?.bound!![position].report.Dec
                total.text = customerInvoiceBindingData?.bound!![position].total_amount
            } else {
                account.text = customerInvoiceBindingData?.tobind?.account_number
                label.text = customerInvoiceBindingData?.tobind?.label
                jan.text = customerInvoiceBindingData?.tobind?.report?.Jan
                feb.text = customerInvoiceBindingData?.tobind?.report?.Feb
                mar.text = customerInvoiceBindingData?.tobind?.report?.Mar
                apr.text = customerInvoiceBindingData?.tobind?.report?.Apr
                may.text = customerInvoiceBindingData?.tobind?.report?.May
                jun.text = customerInvoiceBindingData?.tobind?.report?.Jun
                jul.text = customerInvoiceBindingData?.tobind?.report?.Jul
                aug.text = customerInvoiceBindingData?.tobind?.report?.Aug
                sep.text = customerInvoiceBindingData?.tobind?.report?.Sep
                oct.text = customerInvoiceBindingData?.tobind?.report?.Oct
                nov.text = customerInvoiceBindingData?.tobind?.report?.Nov
                dec.text = customerInvoiceBindingData?.tobind?.report?.Dec
                total.text = customerInvoiceBindingData?.tobind?.total_amount
            }
        }
    }

}