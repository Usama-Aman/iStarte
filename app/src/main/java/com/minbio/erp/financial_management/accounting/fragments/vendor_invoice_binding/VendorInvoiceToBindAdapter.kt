package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.ExtraData
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceToBindData

class VendorInvoiceToBindAdapter(
    _vendorInvoiceToBindData: MutableList<VendorInvoiceToBindData?>,
    _vendorInvoiceToBindFragment: VendorInvoiceToBindFragment,
    _extraData: ExtraData?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var vendorInvoiceToBindData = _vendorInvoiceToBindData
    private var vendorInvoiceToBindFragment = _vendorInvoiceToBindFragment
    private var extraData = _extraData


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_vendor_invoice_bind, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = vendorInvoiceToBindData.size

    override fun getItemViewType(position: Int): Int {
        return if (vendorInvoiceToBindData[position] == null) 1 else 0
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
        private var suggestedAccount: TextView = itemView.findViewById(R.id.suggestedAccount)
        private var bindLineAccount: TextView = itemView.findViewById(R.id.bindLineAccount)
        private var mainInvoiceLayout: RelativeLayout =
            itemView.findViewById(R.id.mainInvoiceLayout)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            idLine.text = vendorInvoiceToBindData[position]?.id.toString()
            invoice.text = vendorInvoiceToBindData[position]?.invoice_no
            invoiceLabel.text = vendorInvoiceToBindData[position]?.supplier_note
            date.text = vendorInvoiceToBindData[position]?.incoming_date
            productRef.text = vendorInvoiceToBindData[position]?.product_variety
            description.text = vendorInvoiceToBindData[position]?.comment
            amount.text = vendorInvoiceToBindData[position]?.amount.toString()
            tax.text = vendorInvoiceToBindData[position]?.supplier_vat.toString()
            thirdParty.text = vendorInvoiceToBindData[position]?.supplier_name
            country.text = vendorInvoiceToBindData[position]?.supplier_country_name
//            vatId .text = vendorInvoiceToBindData[position]?.

            val defaultAccount =
                if (extraData?.account_number_for_sold_products == null || extraData?.account_number_for_sold_products == "")
                    context.resources.getString(R.string.favlbindLabelNotDefined)
                else
                    extraData?.account_number_for_sold_products

            val thisProductAccount =
                if (vendorInvoiceToBindData[position]?.default_sale_accounting_account_number == null || vendorInvoiceToBindData[position]?.default_sale_accounting_account_number == "")
                    context.resources.getString(R.string.favlbindLabelNotDefined)
                else
                    vendorInvoiceToBindData[position]?.default_sale_accounting_account_number

            suggestedAccount.text =
                context.resources.getString(R.string.favlbindLabelAccountDefaultForProduct) + defaultAccount + "\n\n" +
                        context.resources.getString(R.string.favlbindLabelAccountDefaultForProduct) + thisProductAccount

            bindLineAccount.text = vendorInvoiceToBindData[position]?.bind_account

            mainInvoiceLayout.setOnClickListener {
                vendorInvoiceToBindFragment.showAccountSpinner(position)

            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}