package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBindingData

class ExpenseReportBindingAdapter(
    _expenseReportBindingData: ExpenseReportBindingData?,
    _isBound: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var expenseReportBindingData = _expenseReportBindingData
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
            expenseReportBindingData == null -> 0
            isBound -> expenseReportBindingData?.bound?.size!!
            expenseReportBindingData?.tobind != null -> 1
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
                account.text = expenseReportBindingData?.bound!![position].account_number
                label.text = expenseReportBindingData?.bound!![position].label
                jan.text = expenseReportBindingData?.bound!![position].report.Jan
                feb.text = expenseReportBindingData?.bound!![position].report.Feb
                mar.text = expenseReportBindingData?.bound!![position].report.Mar
                apr.text = expenseReportBindingData?.bound!![position].report.Apr
                may.text = expenseReportBindingData?.bound!![position].report.May
                jun.text = expenseReportBindingData?.bound!![position].report.Jun
                jul.text = expenseReportBindingData?.bound!![position].report.Jul
                aug.text = expenseReportBindingData?.bound!![position].report.Aug
                sep.text = expenseReportBindingData?.bound!![position].report.Sep
                oct.text = expenseReportBindingData?.bound!![position].report.Oct
                nov.text = expenseReportBindingData?.bound!![position].report.Nov
                dec.text = expenseReportBindingData?.bound!![position].report.Dec
                total.text = expenseReportBindingData?.bound!![position].total_amount
            } else {
                account.text = expenseReportBindingData?.tobind?.account_number
                label.text = expenseReportBindingData?.tobind?.label
                jan.text = expenseReportBindingData?.tobind?.report?.Jan
                feb.text = expenseReportBindingData?.tobind?.report?.Feb
                mar.text = expenseReportBindingData?.tobind?.report?.Mar
                apr.text = expenseReportBindingData?.tobind?.report?.Apr
                may.text = expenseReportBindingData?.tobind?.report?.May
                jun.text = expenseReportBindingData?.tobind?.report?.Jun
                jul.text = expenseReportBindingData?.tobind?.report?.Jul
                aug.text = expenseReportBindingData?.tobind?.report?.Aug
                sep.text = expenseReportBindingData?.tobind?.report?.Sep
                oct.text = expenseReportBindingData?.tobind?.report?.Oct
                nov.text = expenseReportBindingData?.tobind?.report?.Nov
                dec.text = expenseReportBindingData?.tobind?.report?.Dec
                total.text = expenseReportBindingData?.tobind?.total_amount
            }
        }
    }

}