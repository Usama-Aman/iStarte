package com.minbio.erp.financial_management.accounting.fragments.export_accounting_documents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.utils.AppUtils

class ExportAccountingDocumentAdapter(
    _exportAccountingDocumentData: MutableList<ExportAccountingDocumentData>,
    _exportAccountingDocumentFragment: ExportAccountingDocumentFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var exportAccountingDocumentData = _exportAccountingDocumentData
    private var exportAccountingDocumentFragment = _exportAccountingDocumentFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return /*if (viewType == 0)*/ Items(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_financial_accounting_export_accounting_document,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = exportAccountingDocumentData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var type: TextView = itemView.findViewById(R.id.type)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var dueDate: TextView = itemView.findViewById(R.id.dueDate)
        private var ref: TextView = itemView.findViewById(R.id.ref)
        private var document: TextView = itemView.findViewById(R.id.document)
        private var paid: TextView = itemView.findViewById(R.id.paid)
        private var totalExcl: TextView = itemView.findViewById(R.id.totalExcl)
        private var totalIncl: TextView = itemView.findViewById(R.id.totalIncl)
        private var totalTax: TextView = itemView.findViewById(R.id.totalTax)
        private var thirdParty: TextView = itemView.findViewById(R.id.thirdParty)
        private var code: TextView = itemView.findViewById(R.id.code)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var vatId: TextView = itemView.findViewById(R.id.vatId)


        fun bind(position: Int) {
            type.text = exportAccountingDocumentData[position].type
            date.text = exportAccountingDocumentData[position].date
            dueDate.text = exportAccountingDocumentData[position].due_date
            ref.text = exportAccountingDocumentData[position].ref
            paid.text = exportAccountingDocumentData[position].status
            totalExcl.text = exportAccountingDocumentData[position].total_exc_tax
            totalIncl.text = exportAccountingDocumentData[position].total_inc_tax
            totalTax.text = exportAccountingDocumentData[position].tax
            thirdParty.text = exportAccountingDocumentData[position].third_party
            code.text = exportAccountingDocumentData[position].code
            country.text = exportAccountingDocumentData[position].country_code
            vatId.text = exportAccountingDocumentData[position].vat_id

            if (exportAccountingDocumentData[position].type == "Expense Report" || exportAccountingDocumentData[position].type == "Invoice") {
                document.text = context.resources.getString(R.string.faeadFileDownload)
                document.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_download_blue,0)
                document.setOnClickListener {
                    AppUtils.preventTwoClick(document)
                    exportAccountingDocumentFragment.downloadExportAccountingDocument(
                        exportAccountingDocumentData[absoluteAdapterPosition].id,
                        exportAccountingDocumentData[absoluteAdapterPosition].type
                    )
                }

            } else {
                document.text = ""
                document.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}