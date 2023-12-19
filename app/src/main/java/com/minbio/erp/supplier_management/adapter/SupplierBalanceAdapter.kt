package com.minbio.erp.supplier_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.supplier_management.fragments.SupplierBalance
import com.minbio.erp.supplier_management.models.SupplierBalanceData

class SupplierBalanceAdapter(
    _supplierBalance: SupplierBalance,
    _supplierBalanceList: MutableList<SupplierBalanceData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val supplierBalanceFragment = _supplierBalance
    private val supplierBalanceList = _supplierBalanceList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_supplier_balance, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = supplierBalanceList.size

    override fun getItemViewType(position: Int): Int {
        return if (supplierBalanceList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val supplierId: TextView = itemView.findViewById(R.id.supplierId)
        private val invoiceNo: TextView = itemView.findViewById(R.id.invoiceNo)
        private val invoiceDate: TextView = itemView.findViewById(R.id.invoiceDate)
        private val deliveryNoteNo: TextView = itemView.findViewById(R.id.deliveryNoteNo)
        private val payMethod: TextView = itemView.findViewById(R.id.payMethod)
        private val debit: TextView = itemView.findViewById(R.id.debit)
        private val credit: TextView = itemView.findViewById(R.id.credit)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val overdue: TextView = itemView.findViewById(R.id.overdue)
        private val total: TextView = itemView.findViewById(R.id.total)


        fun bind(position: Int) {
            supplierId.text = supplierBalanceList[position]?.supplier_id
            invoiceNo.text = supplierBalanceList[position]?.invoice_no
            invoiceDate.text = supplierBalanceList[position]?.date
            deliveryNoteNo.text = supplierBalanceList[position]?.supplier_note
            payMethod.text = supplierBalanceList[position]?.payment_method
            debit.text = supplierBalanceList[position]?.debit
            credit.text = supplierBalanceList[position]?.credit
            status.text = supplierBalanceList[position]?.status
            overdue.text = supplierBalanceList[position]?.overdue
            total.text = supplierBalanceList[position]?.total_amount
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}