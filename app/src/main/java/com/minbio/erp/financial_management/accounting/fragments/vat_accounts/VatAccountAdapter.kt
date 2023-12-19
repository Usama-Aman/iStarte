package com.minbio.erp.financial_management.accounting.fragments.vat_accounts

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
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.models.VatAccountsData

class VatAccountAdapter(
    _vatAccountsFragment: VatAccountsFragment,
    _vatAccountData: MutableList<VatAccountsData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var alertDialog: AlertDialog
    private lateinit var context: Context
    private var vatAccountsFragment = _vatAccountsFragment
    private var vatAccountData = _vatAccountData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_accounting_vat_accounts, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = vatAccountData.size

    override fun getItemViewType(position: Int): Int {
        return if (vatAccountData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var country: TextView = itemView.findViewById(R.id.country)
        private var code: TextView = itemView.findViewById(R.id.code)
        private var rate: TextView = itemView.findViewById(R.id.rate)
        private var includeTax2: TextView = itemView.findViewById(R.id.includeTax2)
        private var rate2: TextView = itemView.findViewById(R.id.rate2)
        private var includeTax3: TextView = itemView.findViewById(R.id.includeTax3)
        private var rate3: TextView = itemView.findViewById(R.id.rate3)
        private var npr: TextView = itemView.findViewById(R.id.npr)
        private var saleAccountCode: TextView = itemView.findViewById(R.id.saleAccountCode)
        private var purchaseAmountCode: TextView = itemView.findViewById(R.id.purchaseAmountCode)
        private var note: TextView = itemView.findViewById(R.id.note)
        private var status: Switch = itemView.findViewById(R.id.status)
        private var edit: ImageView = itemView.findViewById(R.id.edit)
        private var delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {
            country.text = vatAccountData[position]?.country
            code.text = vatAccountData[position]?.code
            rate.text = vatAccountData[position]?.rate
            includeTax2.text = if (vatAccountData[position]?.include_tax_2 == 0)
                context.resources.getString(R.string.no)
            else
                context.resources.getString(R.string.yes)
            rate2.text = vatAccountData[position]?.rate_2
            includeTax3.text = if (vatAccountData[position]?.include_tax_3 == 0)
                context.resources.getString(R.string.no)
            else
                context.resources.getString(R.string.yes)
            rate3.text = vatAccountData[position]?.rate_3
            npr.text = if (vatAccountData[position]?.npr == 0)
                context.resources.getString(R.string.no)
            else
                context.resources.getString(R.string.yes)

            saleAccountCode.text = vatAccountData[position]?.sale_chart_account_id.toString()
            purchaseAmountCode.text = vatAccountData[position]?.purchase_chart_account_id.toString()
            note.text = vatAccountData[position]?.note

            status.isChecked = vatAccountData[position]?.status == 1

            status.setOnClickListener {
                if (vatAccountData[position]?.status == 0)
                    vatAccountData[position]?.status = 1
                else
                    vatAccountData[position]?.status = 0

                vatAccountsFragment.updateStatus(
                    vatAccountData[absoluteAdapterPosition]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", vatAccountData[position])
                val fragment = AddVatAccountFragment()
                fragment.arguments = bundle
                vatAccountsFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        vatAccountsFragment.deleteVatAccount(
                            vatAccountData[absoluteAdapterPosition]?.id!!,
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