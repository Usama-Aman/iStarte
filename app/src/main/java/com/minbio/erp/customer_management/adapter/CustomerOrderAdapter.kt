package com.minbio.erp.customer_management.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.models.CDBalanceData

class CustomerOrderAdapter(_cdBalanceList: MutableList<CDBalanceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cdBalanceList = _cdBalanceList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_customer_order, parent, false)
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
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val delivery: TextView = itemView.findViewById(R.id.delivery)
        private val invoice: TextView = itemView.findViewById(R.id.invoice)
        private val overdue: TextView = itemView.findViewById(R.id.overdue)
        private val price: TextView = itemView.findViewById(R.id.price)

        fun bind(position: Int) {
            customerId.visibility = View.GONE
            orderNo.visibility = View.GONE
            time.visibility = View.GONE

            customerId.text = cdBalanceList[position]?.customer_id
            orderNo.text = cdBalanceList[position]?.order_no
            orderDate.text = cdBalanceList[position]?.date
            time.text = cdBalanceList[position]?.time
            status.text = cdBalanceList[position]?.status
            delivery.text = cdBalanceList[position]?.delivery_type
            invoice.text = cdBalanceList[position]?.order_no
            overdue.text = cdBalanceList[position]?.overdue
            price.text = cdBalanceList[position]?.total_amount
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}