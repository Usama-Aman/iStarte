package com.minbio.erp.cashier_desk.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.fragments.CashierDeskCustomer
import com.minbio.erp.cashier_desk.models.CDCustomersData
import de.hdodenhof.circleimageview.CircleImageView

class CashierDeskCustomerAdapter(
    _cashierDeskCustomer: CashierDeskCustomer,
    _cdCustomerOrderList: MutableList<CDCustomersData?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() , Filterable {

    private val cashierDeskCustomer =_cashierDeskCustomer

    private var filteredCustomerOrderList = _cdCustomerOrderList
    private var unfilteredCustomerOrderList = _cdCustomerOrderList

    private lateinit var context: Context
    var selectedID = -1
    var expandedID = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pro_category_list_item, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = filteredCustomerOrderList.size

    override fun getItemViewType(position: Int): Int {
        return if (filteredCustomerOrderList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items) holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var btnEditCustomer: Button = itemView.findViewById(R.id.btn_edit)
        private var bottomLine: View = itemView.findViewById(R.id.bottomLine)
        private var btnExpand: Button = itemView.findViewById(R.id.btn_cols)
        var detail_layout: LinearLayout = itemView.findViewById(R.id.detail_layout)
        var image: CircleImageView = itemView.findViewById(R.id.pro_image)
        var customerName: TextView = itemView.findViewById(R.id.product_name)
        var productMainLayout: RelativeLayout = itemView.findViewById(R.id.productMainLayout)

        fun bind(position: Int) {

            btnEditCustomer.visibility = View.GONE

            Glide.with(context).load(filteredCustomerOrderList[position]?.image_path)
                .placeholder(R.drawable.ic_plc).into(image)

            customerName.text = filteredCustomerOrderList[position]?.full_name.toString()


            if (expandedID != filteredCustomerOrderList[position]?.id!!) {
                detail_layout.removeAllViews()
                btnExpand.text = "+";
                detail_layout.visibility = View.GONE;
            } else {
                btnExpand.text = "-";
                detail_layout.visibility = View.VISIBLE;
                cashierDeskCustomer.viewExpandDetails(
                    detail_layout,
                    filteredCustomerOrderList[position]?.pending_orders!!
                )
            }

            btnExpand.setOnClickListener {

                if (expandedID == filteredCustomerOrderList[position]?.id!!)
                    expandedID = -1
                else
                    expandedID = filteredCustomerOrderList[position]?.id!!
                notifyDataSetChanged()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<CDCustomersData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredCustomerOrderList)
                } else {
                    for (row in unfilteredCustomerOrderList) {
                        if (row?.full_name?.startsWith(charString, true)!!) {
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
                    filteredCustomerOrderList =
                        filterResults.values as ArrayList<CDCustomersData?>
                    notifyDataSetChanged()
                }
            }
        }
    }

}