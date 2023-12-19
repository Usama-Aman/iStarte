package com.minbio.erp.financial_management.biling_payments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.fragments.BillingStatisticsFragment
import com.minbio.erp.financial_management.biling_payments.models.BillingStatisticsData

class BillingStatisticsAdapter(
    _statData: MutableList<BillingStatisticsData?>,
    _billingStatisticsFragment: BillingStatisticsFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var statData = _statData
    private var billingStatisticsFragment = _billingStatisticsFragment
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_billing_statistics, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = statData.size

    override fun getItemViewType(position: Int): Int {
        return if (statData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val year: TextView = itemView.findViewById(R.id.year)
        private val noOfInvoices: TextView = itemView.findViewById(R.id.noOfInvoices)
        private val totalAmount: TextView = itemView.findViewById(R.id.totalAmount)
        private val avgAmount: TextView = itemView.findViewById(R.id.avgAmount)
        private val item_layout: LinearLayout = itemView.findViewById(R.id.item_layout)

        fun bind(position: Int) {
            year.text = statData[position]?.year.toString()
            noOfInvoices.text = statData[position]?.orders_per_year.toString()
            totalAmount.text = statData[position]?.total_amount_per_year.toString()
            avgAmount.text = statData[position]?.average_amount_per_year.toString()


            item_layout.setOnClickListener {
                billingStatisticsFragment.graphsScroll.visibility = View.VISIBLE

                billingStatisticsFragment.setChartDataInvoices(
                    billingStatisticsFragment.noOfInvoicesChart,
                    statData[absoluteAdapterPosition]?.monthly_stats
                )
                billingStatisticsFragment.setChartDataAmount(
                    billingStatisticsFragment.invoiceAmountChart,
                    statData[absoluteAdapterPosition]?.monthly_stats
                )
                billingStatisticsFragment.setChartDataAvgAmount(
                    billingStatisticsFragment.avgAmountChart,
                    statData[absoluteAdapterPosition]?.monthly_stats
                )
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}