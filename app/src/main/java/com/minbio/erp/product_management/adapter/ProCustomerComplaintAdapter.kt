package com.minbio.erp.product_management.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minbio.erp.R
import com.minbio.erp.product_management.model.ProCustomerComplaintData

class ProCustomerComplaintAdapter(_complaintList: MutableList<ProCustomerComplaintData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var complaintList = _complaintList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_product_cust_complaint_row, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = complaintList.size

    override fun getItemViewType(position: Int): Int {
        return if (complaintList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txt_lot_no: TextView = itemView.findViewById(R.id.txt_lot_no)
        private val txt_comp_date: TextView = itemView.findViewById(R.id.txt_comp_date)
        private val txt_supplier: TextView = itemView.findViewById(R.id.txt_supplier)
        private val txt_comp_no: TextView = itemView.findViewById(R.id.txt_comp_no)
        private val txt_comment: TextView = itemView.findViewById(R.id.txt_comment)
        private val com_photo: ImageView = itemView.findViewById(R.id.com_photo)
        private val txt_status: TextView = itemView.findViewById(R.id.txt_status)

        fun bind(position: Int) {
            txt_lot_no.text = complaintList[position]?.lot_no
            txt_comp_date.text = complaintList[position]?.date
            txt_supplier.text = complaintList[position]?.supplier_name
            txt_comp_no.text = complaintList[position]?.complaint_no
            txt_comment.text = complaintList[position]?.comment
            txt_status.text = complaintList[position]?.status

            if (complaintList[position]?.files?.isNotEmpty()!!)
                Glide.with(context).load(complaintList[position]?.files!![0].image_path)
                    .placeholder(R.drawable.ic_plc).into(com_photo)

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

}