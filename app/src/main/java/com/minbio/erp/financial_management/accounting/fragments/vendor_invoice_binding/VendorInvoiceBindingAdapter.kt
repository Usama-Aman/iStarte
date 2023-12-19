package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBindingData

class VendorInvoiceBindingAdapter(
    _vendorInvoiceBindingData: VendorInvoiceBindingData?,
    _isBound: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var vendorInvoiceBindingData = _vendorInvoiceBindingData
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
            vendorInvoiceBindingData == null -> 0
            isBound -> vendorInvoiceBindingData?.bound?.size!!
            vendorInvoiceBindingData?.tobind != null -> 1
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
                account.text = vendorInvoiceBindingData?.bound!![position].account_number
                label.text = vendorInvoiceBindingData?.bound!![position].label
                jan.text = vendorInvoiceBindingData?.bound!![position].report.Jan
                feb.text = vendorInvoiceBindingData?.bound!![position].report.Feb
                mar.text = vendorInvoiceBindingData?.bound!![position].report.Mar
                apr.text = vendorInvoiceBindingData?.bound!![position].report.Apr
                may.text = vendorInvoiceBindingData?.bound!![position].report.May
                jun.text = vendorInvoiceBindingData?.bound!![position].report.Jun
                jul.text = vendorInvoiceBindingData?.bound!![position].report.Jul
                aug.text = vendorInvoiceBindingData?.bound!![position].report.Aug
                sep.text = vendorInvoiceBindingData?.bound!![position].report.Sep
                oct.text = vendorInvoiceBindingData?.bound!![position].report.Oct
                nov.text = vendorInvoiceBindingData?.bound!![position].report.Nov
                dec.text = vendorInvoiceBindingData?.bound!![position].report.Dec
                total.text = vendorInvoiceBindingData?.bound!![position].total_amount
            } else {
                account.text = vendorInvoiceBindingData?.tobind?.account_number
                label.text = vendorInvoiceBindingData?.tobind?.label
                jan.text = vendorInvoiceBindingData?.tobind?.report?.Jan
                feb.text = vendorInvoiceBindingData?.tobind?.report?.Feb
                mar.text = vendorInvoiceBindingData?.tobind?.report?.Mar
                apr.text = vendorInvoiceBindingData?.tobind?.report?.Apr
                may.text = vendorInvoiceBindingData?.tobind?.report?.May
                jun.text = vendorInvoiceBindingData?.tobind?.report?.Jun
                jul.text = vendorInvoiceBindingData?.tobind?.report?.Jul
                aug.text = vendorInvoiceBindingData?.tobind?.report?.Aug
                sep.text = vendorInvoiceBindingData?.tobind?.report?.Sep
                oct.text = vendorInvoiceBindingData?.tobind?.report?.Oct
                nov.text = vendorInvoiceBindingData?.tobind?.report?.Nov
                dec.text = vendorInvoiceBindingData?.tobind?.report?.Dec
                total.text = vendorInvoiceBindingData?.tobind?.total_amount
            }
        }
    }

}