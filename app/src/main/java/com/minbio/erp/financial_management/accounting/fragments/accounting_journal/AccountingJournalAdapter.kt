package com.minbio.erp.financial_management.accounting.fragments.accounting_journal

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
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models.AccountingJournalData

class AccountingJournalAdapter(
    _journalDataList: MutableList<AccountingJournalData?>,
    _accountingJournalFragment: AccountingJournalFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var alertDialog: AlertDialog
    private var journalDataList = _journalDataList
    private var accountingJournalFragment = _accountingJournalFragment
    private lateinit var context: Context

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

    override fun getItemCount(): Int = journalDataList.size

    override fun getItemViewType(position: Int): Int {
        return if (journalDataList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val label: TextView = itemView.findViewById(R.id.Label)
        private val code: TextView = itemView.findViewById(R.id.code)
        private val natureOfJournal: TextView = itemView.findViewById(R.id.natureOfJournal)
        val status: Switch = itemView.findViewById(R.id.status)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {

            label.text = journalDataList[position]?.label
            code.text = journalDataList[position]?.code
            natureOfJournal.text = journalDataList[position]?.journal_nature

            status.setOnCheckedChangeListener(null)
            status.isChecked = journalDataList[position]?.status == 1
            status.setOnCheckedChangeListener(null)

            status.setOnClickListener {
                if (journalDataList[position]?.status == 0)
                    journalDataList[position]?.status = 1
                else
                    journalDataList[position]?.status = 0

                accountingJournalFragment.updateJournalStatus(
                    journalDataList[absoluteAdapterPosition]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", journalDataList[position])
                val fragment = AddAccountingJournalFragment()
                fragment.arguments = bundle
                accountingJournalFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {

                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        accountingJournalFragment.deleteJournal(
                            journalDataList[absoluteAdapterPosition]?.id!!,
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