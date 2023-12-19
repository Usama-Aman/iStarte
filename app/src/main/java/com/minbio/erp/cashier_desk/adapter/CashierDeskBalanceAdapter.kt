package com.minbio.erp.cashier_desk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.models.CDBalanceData

class CashierDeskBalanceAdapter(_cdBalanceList: MutableList<CDBalanceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cdBalanceList = _cdBalanceList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cashier_desk_balance, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = cdBalanceList.size

    override fun getItemViewType(position: Int): Int {
        return if (cdBalanceList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val customerId: TextView = itemView.findViewById(R.id.customerId)
        private val invoiceNo: TextView = itemView.findViewById(R.id.invoiceNo)
        private val invoiceDate: TextView = itemView.findViewById(R.id.invoiceDate)
        private val payMethod: TextView = itemView.findViewById(R.id.payMethod)
        private val debit: TextView = itemView.findViewById(R.id.debit)
        private val credit: TextView = itemView.findViewById(R.id.credit)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val overdue: TextView = itemView.findViewById(R.id.overdue)
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)

        fun bind(position: Int) {

            customerId.text = cdBalanceList[position]?.customer_id
            invoiceNo.text = cdBalanceList[position]?.order_no.toString()
            invoiceDate.text = cdBalanceList[position]?.date
            payMethod.text = cdBalanceList[position]?.payment_method
            debit.text = cdBalanceList[position]?.debit
            credit.text = cdBalanceList[position]?.credit
            status.text = cdBalanceList[position]?.status
            overdue.text = cdBalanceList[position]?.overdue
            orderNo.text = cdBalanceList[position]?.order_no
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}