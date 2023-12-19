package com.minbio.erp.supplier_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.supplier_management.models.SupplierCreditNoteData

class SupplierCreditNoteAdapter(_creditNoteList: MutableList<SupplierCreditNoteData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var creditNoteList = _creditNoteList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_supplier_credit_note, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = creditNoteList.size

    override fun getItemViewType(position: Int): Int {
        return if (creditNoteList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val creditNoteNo: TextView = itemView.findViewById(R.id.creditNoteNo)
        private val supplierId: TextView = itemView.findViewById(R.id.supplierId)
        private val complaintNo: TextView = itemView.findViewById(R.id.complaintNo)
        private val creditNoteDate: TextView = itemView.findViewById(R.id.creditNoteDate)
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private val supplierInvoiceNo: TextView = itemView.findViewById(R.id.supplierInvoiceNo)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val total: TextView = itemView.findViewById(R.id.total)

        fun bind(position: Int) {
            creditNoteNo.text = creditNoteList[position]?.creditnote_id
            supplierId.text = creditNoteList[position]?.supplier_id
            complaintNo.text = creditNoteList[position]?.complaint_no
            creditNoteDate.text = creditNoteList[position]?.date
            orderNo.text = creditNoteList[position]?.order_no
            supplierInvoiceNo.text = creditNoteList[position]?.invoice_no.toString()
            status.text = creditNoteList[position]?.status
            total.text = creditNoteList[position]?.amount
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}