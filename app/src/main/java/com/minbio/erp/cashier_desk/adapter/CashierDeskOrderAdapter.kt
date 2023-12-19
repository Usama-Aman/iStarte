package com.minbio.erp.cashier_desk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.fragments.CashierDeskOrder
import com.minbio.erp.cashier_desk.models.CDOrderData

class CashierDeskOrderAdapter(
    _cashierDeskOrder: CashierDeskOrder,
    _cdOrderList: MutableList<CDOrderData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val cashierDeskOrder = _cashierDeskOrder
    private var filteredCdOrderList = _cdOrderList
    private var unfilteredCdOrderList = _cdOrderList
    private lateinit var context: Context

    var selectedID = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cashier_desk_order, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = filteredCdOrderList.size

    override fun getItemViewType(position: Int): Int {
        return if (filteredCdOrderList[position] == null) 1 else 0
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var orderNo: TextView = itemView.findViewById(R.id.orderNo)
        private var date: TextView = itemView.findViewById(R.id.orderDate)
        private var status: TextView = itemView.findViewById(R.id.status)
        private var bottomLine: View = itemView.findViewById(R.id.bottomLine)

        private var cdoMainLayout: RelativeLayout = itemView.findViewById(R.id.cdoMainLayout)

        fun bind(position: Int) {

            orderNo.text = filteredCdOrderList[position]?.order_no
            date.text = filteredCdOrderList[position]?.date
            status.text = filteredCdOrderList[position]?.status

            if (position == selectedID) {
                cashierDeskOrder.itemClick(
                    filteredCdOrderList[absoluteAdapterPosition]?.order_no,
                    filteredCdOrderList[absoluteAdapterPosition]?.details
                )
                orderNo.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                date.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                status.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))

                cdoMainLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorDarkBlue
                    )
                )
                bottomLine.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorDarkBlue
                    )
                )
            } else {
                orderNo.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                date.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                status.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

                cdoMainLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
                bottomLine.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_gray
                    )
                )
            }


            cdoMainLayout.setOnClickListener {
                selectedID = absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<CDOrderData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredCdOrderList)
                } else {
                    for (row in unfilteredCdOrderList) {
                        if (row?.order_no?.startsWith(charString)!!
                        ) {
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
                    filteredCdOrderList = filterResults.values as ArrayList<CDOrderData?>
                    notifyDataSetChanged()
                }
            }
        }
    }

}