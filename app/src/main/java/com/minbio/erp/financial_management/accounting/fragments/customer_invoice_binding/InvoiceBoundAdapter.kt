package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.CustomerInvoiceBoundData

class InvoiceBoundAdapter(
    _customerInvoiceBoundData: MutableList<CustomerInvoiceBoundData?>,
    _invoiceBoundFragment: InvoiceBoundFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var customerInvoiceBoundData = _customerInvoiceBoundData
    private var invoiceBoundFragment = _invoiceBoundFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_invoice_bound, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = customerInvoiceBoundData.size

    override fun getItemViewType(position: Int): Int {
        return if (customerInvoiceBoundData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var idLine: TextView = itemView.findViewById(R.id.idLine)
        private var invoice: TextView = itemView.findViewById(R.id.invoice)
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
                customerInvoiceBoundData[position]?.isChecked =
                    !customerInvoiceBoundData[position]?.isChecked!!
            }

            idLine.text = customerInvoiceBoundData[position]?.id.toString()
            invoice.text = customerInvoiceBoundData[position]?.invoice
            date.text = customerInvoiceBoundData[position]?.date
            productRef.text = customerInvoiceBoundData[position]?.product_variety
            description.text = customerInvoiceBoundData[position]?.desc +
                    "\n(" + context.resources.getString(R.string.faclboundLabelOriginCountry) + ":"+ customerInvoiceBoundData[position]?.product_country_name + ")"
            amount.text = customerInvoiceBoundData[position]?.amount.toString()
            tax.text = customerInvoiceBoundData[position]?.vat.toString()
            thirdParty.text = customerInvoiceBoundData[position]?.customer_name
            country.text = customerInvoiceBoundData[position]?.customer_country_name

            checkBox.isChecked = customerInvoiceBoundData[position]?.isChecked!!

//            vatId .text = customerInvoiceBoundData[position]?.va
            account.text =
                customerInvoiceBoundData[position]?.chart_account_number + " - " + customerInvoiceBoundData[position]?.chart_account_label
//            bindLineAccount .text = customerInvoiceBoundData[position]?.

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}