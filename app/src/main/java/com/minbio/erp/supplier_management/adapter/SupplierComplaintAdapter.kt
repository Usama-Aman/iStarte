package com.minbio.erp.supplier_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.supplier_management.models.SupplierComplaintData

class SupplierComplaintAdapter(_supplierComplaintList: MutableList<SupplierComplaintData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var supplierComplaintList = _supplierComplaintList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_supplier_complaint, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = supplierComplaintList.size

    override fun getItemViewType(position: Int): Int {
        return if (supplierComplaintList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val supplierId: TextView = itemView.findViewById(R.id.supplierId)
        private val complaintNo: TextView = itemView.findViewById(R.id.complaintNo)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private val supplierDeliveryNote: TextView =
            itemView.findViewById(R.id.supplierDeliveryNote)
        private val status: TextView = itemView.findViewById(R.id.status)


        fun bind(position: Int) {
            supplierId.text = supplierComplaintList[position]?.supplier_id
            complaintNo.text = supplierComplaintList[position]?.complaint_no
            date.text = supplierComplaintList[position]?.date
            orderNo.text = supplierComplaintList[position]?.order_no
            supplierDeliveryNote.text = supplierComplaintList[position]?.supplier_note
            status.text = supplierComplaintList[position]?.status
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

}