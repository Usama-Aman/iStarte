package com.minbio.erp.order_for_preparation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.order_for_preparation.fragments.OrderPrepOrderPending
import com.minbio.erp.order_for_preparation.models.PendingOrderData

class OrderPrepOrderPendingAdapter(
    _orderPrepOrderPending: OrderPrepOrderPending,
    _pendingOrderList: MutableList<PendingOrderData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var orderPrepOrderPending = _orderPrepOrderPending
    private var filteredPendingOrderList = _pendingOrderList
    private var unfilteredPendingOrderList = _pendingOrderList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_op_order_pending_tab, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int {
        return filteredPendingOrderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (filteredPendingOrderList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var time: TextView = itemView.findViewById(R.id.time)
        private var status: TextView = itemView.findViewById(R.id.status)
        private var pendingMainLayout: RelativeLayout =
            itemView.findViewById(R.id.pendingMainLayout)

        fun bind(position: Int) {

            orderNo.text = filteredPendingOrderList[position]?.order_no
            date.text = filteredPendingOrderList[position]?.date
            time.text = filteredPendingOrderList[position]?.time
            status.text = filteredPendingOrderList[position]?.status

            pendingMainLayout.setOnClickListener {
                orderPrepOrderPending.itemClick(
                    filteredPendingOrderList[absoluteAdapterPosition]?.id,
                    filteredPendingOrderList[absoluteAdapterPosition]?.order_no,
                    filteredPendingOrderList[absoluteAdapterPosition]?.details
                )
            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<PendingOrderData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredPendingOrderList)
                } else {
                    for (row in unfilteredPendingOrderList) {
                        if (row?.order_no?.startsWith(charString,true)!!) {
                            filteredList.add(row)
                        }
                    }
                }
                try {
                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                if (filterResults != null) {
                    filteredPendingOrderList =
                        filterResults.values as ArrayList<PendingOrderData?>
                    notifyDataSetChanged()
                }
            }
        }
    }

}