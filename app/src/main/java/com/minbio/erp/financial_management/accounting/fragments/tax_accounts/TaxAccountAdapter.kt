package com.minbio.erp.financial_management.accounting.fragments.tax_accounts

import android.annotation.SuppressLint
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
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.models.TaxAccountsData
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.AddVatAccountFragment

class TaxAccountAdapter(
    _taxAccountsData: MutableList<TaxAccountsData?>,
    _taxAccountFragment: TaxAccountFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var taxAccountsData = _taxAccountsData
    private var taxAccountFragment = _taxAccountFragment
    private lateinit var alertDialog: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_tax_accounts, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = taxAccountsData.size

    override fun getItemViewType(position: Int): Int {
        return if (taxAccountsData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var code: TextView = itemView.findViewById(R.id.code)
        private var Label: TextView = itemView.findViewById(R.id.Label)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var accountingCode: TextView = itemView.findViewById(R.id.accountingCode)
        private var deductible: TextView = itemView.findViewById(R.id.deductible)
        private var status: Switch = itemView.findViewById(R.id.status)
        private var edit: ImageView = itemView.findViewById(R.id.edit)
        private var delete: ImageView = itemView.findViewById(R.id.delete)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            code.text = taxAccountsData[position]?.code
            Label.text = taxAccountsData[position]?.label
            country.text = taxAccountsData[position]?.country
            accountingCode.text =
                "${taxAccountsData[position]?.chart_account_number} : ${taxAccountsData[position]?.chart_account_label} "

            deductible.text =
                if (taxAccountsData[position]?.deductible == 0) context.resources.getString(R.string.no)
                else
                    context.resources.getString(R.string.yes)

            status.isChecked = taxAccountsData[position]?.status!! == 1

            status.setOnClickListener {
                taxAccountsData[position]?.status =
                    if (taxAccountsData[position]?.status == 0) 1 else 0

                taxAccountFragment.updateStatus(
                    taxAccountsData[position]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", taxAccountsData[position])
                val fragment = AddTaxAccountFragment()
                fragment.arguments = bundle
                taxAccountFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        taxAccountFragment.deleteTaxAccount(
                            taxAccountsData[absoluteAdapterPosition]?.id!!,
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