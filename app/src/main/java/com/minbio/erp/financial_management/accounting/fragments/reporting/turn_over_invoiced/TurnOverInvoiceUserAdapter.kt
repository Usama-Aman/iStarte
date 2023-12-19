package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedUserData

class TurnOverInvoiceUserAdapter(_toInvoicedUserData: MutableList<TOInvoicedUserData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toInvoicedUserData = _toInvoicedUserData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_invoiced_user, parent, false)
        )
    }

    override fun getItemCount(): Int = toInvoicedUserData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var user: TextView = itemView.findViewById(R.id.user)
        private var amountExcl: TextView = itemView.findViewById(R.id.amountExcl)
        private var amountIncl: TextView = itemView.findViewById(R.id.amountIncl)
        private var percentage: TextView = itemView.findViewById(R.id.percentage)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            user.text = toInvoicedUserData[position].user_name
            amountExcl.text = toInvoicedUserData[position].amount_exc_tax
            amountIncl.text = toInvoicedUserData[position].amount_inc_tax
            percentage.text = "${toInvoicedUserData[position].percentage}%"
        }
    }

}