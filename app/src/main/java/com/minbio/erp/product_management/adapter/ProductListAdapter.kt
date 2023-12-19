package com.minbio.erp.product_management.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minbio.erp.R
import com.minbio.erp.product_management.ProManagementFragment
import com.minbio.erp.product_management.model.ProductListData
import com.minbio.erp.utils.AppUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class ProductListAdapter(
    _proManagementFragment: ProManagementFragment,
    _productList: MutableList<ProductListData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {


    private var proManagementFragment: ProManagementFragment = _proManagementFragment
     var filteredProductList = _productList
     var unfilteredProductList = _productList
    private lateinit var context: Context
    var selectedID = -1
    var expandedID = -1

    private var detail_map: HashMap<Int, Int> = hashMapOf<Int, Int>()

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

    override fun getItemCount(): Int = filteredProductList.size

    override fun getItemViewType(position: Int): Int {
        return if (filteredProductList[position] == null) 1 else 0
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
        var product_name: TextView = itemView.findViewById(R.id.product_name)
        var productMainLayout: RelativeLayout = itemView.findViewById(R.id.productMainLayout)

        fun bind(position: Int) {

            btnEditCustomer.visibility = View.GONE


            Glide.with(context).load(filteredProductList[position]?.image_path)
                .placeholder(R.drawable.ic_plc).into(image)

            product_name.text = filteredProductList[position]?.name.toString()

            if (selectedID == filteredProductList[position]?.id!!) {
                bottomLine.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBBlue))
                productMainLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.lighter_gray
                    )
                )
                btnEditCustomer.background = context.resources.getDrawable(
                    R.drawable.ic_edit_sel,
                    null
                )
            } else {
                productMainLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
                bottomLine.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
                btnEditCustomer.background = context.resources.getDrawable(
                    R.drawable.ic_edit,
                    null
                )
            }

            if (expandedID != filteredProductList[position]?.id!!) {
                detail_layout.removeAllViews()
                btnExpand.text = "+";
                detail_layout.visibility = View.GONE;
            } else {
                btnExpand.text = "-";
                detail_layout.visibility = View.VISIBLE;
                proManagementFragment.prodLotDetail(
                    detail_layout,
                    filteredProductList[position]?.varieties!!,
                    filteredProductList[position]!!
                )
            }

            btnExpand.setOnClickListener {

                expandedID =
                    if (expandedID == filteredProductList[position]?.id!!) -1 else filteredProductList[position]?.id!!
                notifyDataSetChanged()
            }

            btnEditCustomer.setOnClickListener {
                AppUtils.preventTwoClick(btnEditCustomer)
                proManagementFragment.editProduct(filteredProductList[position]!!)
                selectedID = filteredProductList[position]?.id!!
                notifyDataSetChanged()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<ProductListData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredProductList)
                } else {
                    for (row in unfilteredProductList) {
                        if (row?.name?.startsWith(charString,true)!!
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
                    filteredProductList = filterResults.values as ArrayList<ProductListData?>
                    notifyDataSetChanged()
                }
            }
        }
    }

}