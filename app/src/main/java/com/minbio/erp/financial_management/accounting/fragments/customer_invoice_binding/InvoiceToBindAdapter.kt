package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.CustomerInvoiceToBindData
import com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models.ExtraData

class InvoiceToBindAdapter(
    _customerInvoiceToBindData: MutableList<CustomerInvoiceToBindData?>,
    _invoiceToBindFragment: InvoiceToBindFragment,
    _extraData: ExtraData?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var customerInvoiceToBindData = _customerInvoiceToBindData
    private var invoiceToBindFragment = _invoiceToBindFragment
    private var extraData = _extraData


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_invoice_bind, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = customerInvoiceToBindData.size

    override fun getItemViewType(position: Int): Int {
        return if (customerInvoiceToBindData[position] == null) 1 else 0
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
        private var suggestedAccount: TextView = itemView.findViewById(R.id.suggestedAccount)
        private var bindLineAccount: TextView = itemView.findViewById(R.id.bindLineAccount)
        private var mainInvoiceLayout: RelativeLayout =
            itemView.findViewById(R.id.mainInvoiceLayout)

        fun bind(position: Int) {

            idLine.text = customerInvoiceToBindData[position]?.id.toString()
            invoice.text = customerInvoiceToBindData[position]?.invoice
            date.text = customerInvoiceToBindData[position]?.date
            productRef.text = customerInvoiceToBindData[position]?.product_variety
            description.text =
                customerInvoiceToBindData[position]?.desc +
                        "\n(" + context.resources.getString(R.string.faclbindLabelOriginCountry) + ":"+ customerInvoiceToBindData[position]?.product_country_name + ")"
            amount.text = customerInvoiceToBindData[position]?.amount.toString()
            tax.text = customerInvoiceToBindData[position]?.vat.toString()
            thirdParty.text = customerInvoiceToBindData[position]?.customer_name
            country.text = customerInvoiceToBindData[position]?.customer_country_name
//            vatId .text = customerInvoiceToBindData[position]?.


            val defaultAccount =
                if (extraData?.account_number_for_sold_products == null || extraData?.account_number_for_sold_products == "")
                    context.resources.getString(R.string.faclbindLabelNotDefined)
                else
                    extraData?.account_number_for_sold_products

            val thisProductAccount =
                if (customerInvoiceToBindData[position]?.product_chart_account_number == null || customerInvoiceToBindData[position]?.product_chart_account_number == "")
                    context.resources.getString(R.string.faclbindLabelNotDefined)
                else
                    customerInvoiceToBindData[position]?.product_chart_account_number

            suggestedAccount.text =
                context.resources.getString(R.string.faclbindLabelAccountDefaultForProduct) + defaultAccount + "\n\n" +
                        context.resources.getString(R.string.faclbindLabelAccountThisProduct) + thisProductAccount

            bindLineAccount.text = customerInvoiceToBindData[position]?.bind_account

            mainInvoiceLayout.setOnClickListener {
                invoiceToBindFragment.showAccountSpinner(position)

            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}