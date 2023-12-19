package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models.TOPInvoiceThirdPartyData

class PurchaseTurnOverInvoicedThirdPartyAdapter(_topInvoiceThirdPartyData: MutableList<TOPInvoiceThirdPartyData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var topInvoiceThirdPartyData = _topInvoiceThirdPartyData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_invoiced_third_parties, parent, false)
        )
    }

    override fun getItemCount(): Int = topInvoiceThirdPartyData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var company: TextView = itemView.findViewById(R.id.company)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var amountExcl: TextView = itemView.findViewById(R.id.amountExcl)
        private var amountIncl: TextView = itemView.findViewById(R.id.amountIncl)
        private var percentage: TextView = itemView.findViewById(R.id.percentage)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            company.text = topInvoiceThirdPartyData!![position].supplier_company_name
            country.text = topInvoiceThirdPartyData!![position].country_name
            amountExcl.text = topInvoiceThirdPartyData!![position].amount_exc_tax
            amountIncl.text = topInvoiceThirdPartyData!![position].amount_inc_tax
            percentage.text = "${topInvoiceThirdPartyData!![position].percentage}%"
        }
    }

}