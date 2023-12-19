package com.minbio.erp.financial_management.accounting.fragments.chart_accounts

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models.ChartAccountData

class ChartAccountsAdapter(
    _chartAccounts: MutableList<ChartAccountData?>,
    _chartAccountsFragment: ChartAccountsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var alertDialog: AlertDialog
    private lateinit var context: Context
    private var chartAccounts = _chartAccounts
    private var chartAccountsFragment = _chartAccountsFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chart_accounts, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = chartAccounts.size

    override fun getItemViewType(position: Int): Int {
        return if (chartAccounts[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val accountNumber: TextView = itemView.findViewById(R.id.accountNumber)
        private val Label: TextView = itemView.findViewById(R.id.Label)
        private val shortLabel: TextView = itemView.findViewById(R.id.shortLabel)
        private val parentAccount: TextView = itemView.findViewById(R.id.parentAccount)
        private val groupOfAccount: TextView = itemView.findViewById(R.id.groupOfAccount)
        private val status: Switch = itemView.findViewById(R.id.status)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {

            accountNumber.text = chartAccounts[position]?.account_number
            Label.text = chartAccounts[position]?.label
            shortLabel.text = chartAccounts[position]?.short_label
            parentAccount.text = chartAccounts[position]?.parent_account_number
            groupOfAccount.text = chartAccounts[position]?.account_group

            status.isChecked = chartAccounts[position]?.status == 1

            status.setOnClickListener {
                if (chartAccounts[position]?.status == 0)
                    chartAccounts[position]?.status = 1
                else
                    chartAccounts[position]?.status = 0

                chartAccountsFragment.updateStatus(
                    chartAccounts[absoluteAdapterPosition]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", chartAccounts[position])
                bundle.putString("chartAccountModel", chartAccountsFragment.chartAccountModel)
                bundle.putInt("chartAccountModelId", chartAccountsFragment.accountModelId)
                val fragment = AddChartAccountFragment()
                fragment.arguments = bundle
                chartAccountsFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        chartAccountsFragment.deleteChartAccount(
                            chartAccounts[absoluteAdapterPosition]?.id!!,
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