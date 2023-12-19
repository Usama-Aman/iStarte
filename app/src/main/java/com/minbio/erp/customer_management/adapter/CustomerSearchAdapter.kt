package com.minbio.erp.customer_management.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.room.AppDatabase
import com.minbio.erp.room.SalesModel
import com.minbio.erp.room.SalesOrders
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.customer_management.CustomerManagementFragment
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class CustomerSearchAdapter(
    cma: CustomerManagementFragment,
    _customersList: MutableList<CustomersData?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var customerManagementFragment = cma
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


            customerMainLayout.setOnClickListener {


                if (loginModel.data.permissions.sales_management.contains(PermissionKeys.create_orders)) {
                    val db = AppDatabase.getAppDataBase(context = context.applicationContext)

                    val salesList: MutableList<SalesModel> = ArrayList()
                    db!!.salesDAO()
                        .getSalesOfLoggedInUser(loggedInUserId = loginModel.data.id.toString())
                        .forEach {
                            salesList.add(SalesModel(it.id, it.loggedInUserId))
                        }

                    if (salesList.isEmpty())
                        db.salesDAO().insertSales(SalesModel(0, loginModel.data.id.toString()))

                    val salesOrder = db.salesDAO().getSalesOrdersOfASpecificCustomer(
                        loginModel.data.id.toString(),
                        filteredData[position]?.id.toString()
                    )

                    if (salesOrder.isEmpty()) {
                        db.salesDAO().insertSalesOrders(
                            SalesOrders(
                                0,
                                loginModel.data.id.toString(),
                                filteredData[position]?.id.toString(),
                                filteredData[position]?.name.toString(),
                                filteredData[position]?.siret_no.toString(),
                                filteredData[position]?.vat_id.toString(),
                                filteredData[position]?.balance.toString(),
                                filteredData[position]?.pending_overdraft.toString(),
                                filteredData[position]?.payment_status.toString(),
                                filteredData[position]?.image_path!!
                            )
                        )
                        AppUtils.showToast(
                            context as AppCompatActivity,
                            context.resources.getString(R.string.customer_data_added_to_cart),
                            true
                        )


                    } else
                        AppUtils.showToast(
                            context as AppCompatActivity,
                            context.resources.getString(R.string.customer_already_added_in_cart),
                            false
                        )


                    customerManagementFragment.addCustomerToSales(filteredData[position]?.id)

                }
            }

            btnEditCustomer.setOnClickListener {
                customerManagementFragment.userClick(position, filteredData[position]!!)
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