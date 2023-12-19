package com.minbio.erp.accounting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.accounting.fragments.AccountingCustomerList
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import de.hdodenhof.circleimageview.CircleImageView

class AccountingCustomerListAdapter(
    _accountingCustomerList: AccountingCustomerList,
    _customersList: MutableList<CustomersData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val accountingCustomerList = _accountingCustomerList
    private var unfilteredData = _customersList
    private var filteredData = _customersList
    private lateinit var context: Context
    var selectedID = -1
    private lateinit var loginModel: LoginModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        val gson = Gson()
        loginModel = gson.fromJson(
            SharedPreference.getSimpleString(context, Constants.userData),
            LoginModel::class.java
        )

        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_customer_search_list, parent, false)
        ) else
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
        private var customerMainLayout: RelativeLayout =
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
                "${filteredData[position]?.name} ${filteredData[position]?.last_name}"

            if (selectedID == filteredData[position]?.id!!) {
                bottomLine.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBBlue))
                customerMainLayout.setBackgroundColor(
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
                customerMainLayout.setBackgroundColor(
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
                accountingCustomerList.userClick(filteredData[position]!!.id)
                selectedID = filteredData[position]?.id!!
                notifyDataSetChanged()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<CustomersData?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(unfilteredData)
                } else {
                    for (row in unfilteredData) {
                        if (row?.name?.startsWith(charString,true)!!) {
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
                    filteredData = filterResults.values as ArrayList<CustomersData?>
                    notifyDataSetChanged()
                }
            }
        }
    }
}