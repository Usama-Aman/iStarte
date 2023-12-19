package com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModelData


class ChartModelAdapter(
    _chartModelData: MutableList<AccountChartModelData?>,
    _chartModelFragment: ChartModelFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chartModelData = _chartModelData
    private var chartModelFragment = _chartModelFragment
    private lateinit var context: Context
    private lateinit var alertDialog: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_journals, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = chartModelData.size

    override fun getItemViewType(position: Int): Int {
        return if (chartModelData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is Items)
            holder.status.setOnCheckedChangeListener(null)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val label: TextView = itemView.findViewById(R.id.Label)
        private val code: TextView = itemView.findViewById(R.id.code)
        private val natureOfJournal: TextView = itemView.findViewById(R.id.natureOfJournal)
        val status: Switch = itemView.findViewById(R.id.status)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {

            code.text = chartModelData[position]?.chart_accounts_model
            label.text = chartModelData[position]?.label
            natureOfJournal.text = chartModelData[position]?.country

            status.setOnCheckedChangeListener(null)
            status.isChecked = chartModelData[position]?.status == 1
            status.setOnCheckedChangeListener(null)

            status.setOnClickListener {

                if (chartModelData[position]?.status == 0)
                    chartModelData[position]?.status = 1
                else
                    chartModelData[position]?.status = 0

                chartModelFragment.updateStatus(
                    chartModelData[absoluteAdapterPosition]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", chartModelData[position])
                val fragment = AddChartModelFragment()
                fragment.arguments = bundle
                chartModelFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {

                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        chartModelFragment.deleteChartModel(
                            chartModelData[absoluteAdapterPosition]?.id!!,
                            absoluteAdapterPosition
                        )
                    }
                    .setNegativeButton(context.resources.getString(R.string.no)) { _, _ ->
                        alertDialog.dismiss()
                    }
                    .show()
            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}