package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBoundData

class VendorInvoiceBoundAdapter(
    _vendorInvoiceBoundData: MutableList<VendorInvoiceBoundData?>,
    _vendorInvoiceBoundFragment: VendorInvoiceBoundFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var vendorInvoiceBoundData = _vendorInvoiceBoundData
    private var vendorInvoiceBoundFragment = _vendorInvoiceBoundFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_vendor_invoice_bound, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = vendorInvoiceBoundData.size

    override fun getItemViewType(position: Int): Int {
        return if (vendorInvoiceBoundData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var idLine: TextView = itemView.findViewById(R.id.idLine)
        private var invoice: TextView = itemView.findViewById(R.id.invoice)
        private var invoiceLabel: TextView = itemView.findViewById(R.id.invoiceLabel)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var productRef: TextView = itemView.findViewById(R.id.productRef)
        private var description: TextView = itemView.findViewById(R.id.description)
        private var amount: TextView = itemView.findViewById(R.id.amount)
        private var tax: TextView = itemView.findViewById(R.id.tax)
        private var thirdParty: TextView = itemView.findViewById(R.id.thirdParty)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var vatId: TextView = itemView.findViewById(R.id.vatId)
        private var account: TextView = itemView.findViewById(R.id.suggestedAccount)
        private var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(position: Int) {

            checkBox.setOnClickListener {
                vendorInvoiceBoundData[position]?.isChecked =
                    !vendorInvoiceBoundData[position]?.isChecked!!
            }

            idLine.text = vendorInvoiceBoundData[position]?.id.toString()
            invoice.text = vendorInvoiceBoundData[position]?.invoice_no
            invoiceLabel.text = vendorInvoiceBoundData[position]?.supplier_note
            date.text = vendorInvoiceBoundData[position]?.incoming_date
            productRef.text = vendorInvoiceBoundData[position]?.product_variety
            description.text = vendorInvoiceBoundData[position]?.comment
            amount.text = vendorInvoiceBoundData[position]?.amount.toString()
            tax.text = vendorInvoiceBoundData[position]?.supplier_vat.toString()
            thirdParty.text = vendorInvoiceBoundData[position]?.supplier_name
            country.text = vendorInvoiceBoundData[position]?.supplier_country_name

            checkBox.isChecked = vendorInvoiceBoundData[position]?.isChecked!!

//            vatId .text = vendorInvoiceBoundData[position]?.va
            account .text = vendorInvoiceBoundData[position]?.chart_account_number + " - " + vendorInvoiceBoundData[position]?.chart_account_label

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}