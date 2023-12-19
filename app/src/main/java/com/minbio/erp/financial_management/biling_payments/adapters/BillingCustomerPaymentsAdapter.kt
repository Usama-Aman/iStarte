package com.minbio.erp.financial_management.biling_payments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.models.BillingCustomerPaymentData

class BillingCustomerPaymentsAdapter(_paymentData: MutableList<BillingCustomerPaymentData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var paymentData = _paymentData
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_billing_payments, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = paymentData.size

    override fun getItemViewType(position: Int): Int {
        return if (paymentData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val companyName: TextView = itemView.findViewById(R.id.companyName)
        private val payMethod: TextView = itemView.findViewById(R.id.payMethod)
        private val payAmount: TextView = itemView.findViewById(R.id.payAmount)


        fun bind(position: Int) {
            ref.text = paymentData[position]?.id.toString()
            orderNo.text = paymentData[position]?.order_no
            date.text = paymentData[position]?.date
            companyName.text = paymentData[position]?.customer_name
            payAmount.text = paymentData[position]?.paid_amount
            payMethod.text = paymentData[position]?.payment_method
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}