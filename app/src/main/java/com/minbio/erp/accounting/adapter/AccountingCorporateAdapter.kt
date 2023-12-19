package com.minbio.erp.accounting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minbio.erp.R
import com.minbio.erp.accounting.fragments.AccountingCorporate
import com.minbio.erp.corporate_management.CorporateManagementFragment
import com.minbio.erp.corporate_management.models.CorporateUsersData
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class AccountingCorporateAdapter(
    _accountingCorporate: AccountingCorporate,
    _data: MutableList<CorporateUsersData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var accountingCorporate: AccountingCorporate = _accountingCorporate
    private var filteredData: MutableList<CorporateUsersData?> = _data
    private var unfilteredData: MutableList<CorporateUsersData?> = _data

    var selectedID = -1
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0)
            Items(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_customer_search_list, parent, false)
            )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = filteredData.size

    override fun getItemViewType(position: Int): Int {
        return if (filteredData[position] == null) 1 else 0
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items) holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var btnEditCustomer: Button = itemView.findViewById(R.id.btn_customer_edit)
        private var bottomLine: View = itemView.findViewById(R.id.bottomLine)
        private var corporateMainLayout: RelativeLayout =
            itemView.findViewById(R.id.customer_main_layout)
        private var corporateUserName: TextView = itemView.findViewById(R.id.corporate_user_name)
        private var corporateUserImage: CircleImageView =
            itemView.findViewById(R.id.corporate_user_image)

        fun bind(position: Int) {


            Glide
                .with(context)
                .load(filteredData[position]?.image_path)
                .centerCrop()
                .placeholder(R.drawable.ic_plc)
                .into(corporateUserImage)


            corporateUserName.text =
                "${filteredData[position]?.first_name} ${filteredData[position]?.last_name}"

            if (selectedID == filteredData[position]?.id!!) {
                bottomLine.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBBlue))
                corporateMainLayout.setBackgroundColor(
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
                corporateMainLayout.setBackgroundColor(
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

            btnEditCustomer.setOnClickListener {
                accountingCorporate.userClick(filteredData[position]?.id!!)
                selectedID = filteredData[position]?.id!!
                notifyDataSetChanged()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<CorporateUsersData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredData)
                } else {
                    for (row in unfilteredData) {
                        if (row?.first_name
                                ?.startsWith(charString,true)!!
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
                filteredData = filterResults.values as ArrayList<CorporateUsersData?>
                notifyDataSetChanged()
            }
        }
    }
}