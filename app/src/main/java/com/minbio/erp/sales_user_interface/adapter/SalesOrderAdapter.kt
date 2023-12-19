package com.minbio.erp.sales_user_interface.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.room.SalesOrderItems
import com.minbio.erp.sales_user_interface.SalesFragment
import com.minbio.erp.utils.CustomSearchableSpinner

class SalesOrderAdapter(
    _salesFragment: SalesFragment,
    _selectedOrderItems: MutableList<SalesOrderItems>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val salesFragment = _salesFragment
    private val selectedOrderItems = _selectedOrderItems
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sales_order, parent, false)
        )
    }

    override fun getItemCount(): Int = selectedOrderItems.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val lotNoCheck: CheckBox = itemView.findViewById(R.id.lotNoCheck)

        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val varietyName: TextView = itemView.findViewById(R.id.varietyName)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val size: TextView = itemView.findViewById(R.id.size)
        private val origin: TextView = itemView.findViewById(R.id.origin)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val tare: TextView = itemView.findViewById(R.id.tare)
        private val quantity: TextView = itemView.findViewById(R.id.quantity)
        private val packaging: TextView = itemView.findViewById(R.id.packaging)
        private val vat: TextView = itemView.findViewById(R.id.vat)
        private val total: TextView = itemView.findViewById(R.id.total)

        private val quantitySpinner: CustomSearchableSpinner =
            itemView.findViewById(R.id.quantitySpinner)

        private val orderMainLayout: ConstraintLayout = itemView.findViewById(R.id.orderMainLayout)

        fun bind(position: Int) {

            lotNoCheck.isChecked =
                salesFragment.saleItemsToDeleteIDs.contains(selectedOrderItems[position].id)

            lotNoCheck.text = selectedOrderItems[position].lotNo
            productName.text = selectedOrderItems[position].productName
            varietyName.text = selectedOrderItems[position].varietyName
            categoryName.text = selectedOrderItems[position].categoryName
            size.text = selectedOrderItems[position].calibreSize
            origin.text = selectedOrderItems[position].origin
            price.text = selectedOrderItems[position].price
            tare.text = selectedOrderItems[position].tare
            quantity.text = selectedOrderItems[position].quantity
            packaging.text = selectedOrderItems[position].packaging
            vat.text =
                (((selectedOrderItems[position].quantity.toDouble() * selectedOrderItems[position].price.toDouble()) / 100)
                        * selectedOrderItems[position].vat.toDouble()).toString()

            total.text = selectedOrderItems[position].total

            lotNoCheck.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->

                if (isChecked) {
                    if (!salesFragment.saleItemsToDeleteIDs.contains(selectedOrderItems[absoluteAdapterPosition].lotId.toInt()))
                        salesFragment.saleItemsToDeleteIDs.add(selectedOrderItems[absoluteAdapterPosition].lotId.toInt())
                } else
                    if (salesFragment.saleItemsToDeleteIDs.contains(selectedOrderItems[absoluteAdapterPosition].lotId.toInt()))
                        salesFragment.saleItemsToDeleteIDs.remove(selectedOrderItems[absoluteAdapterPosition].lotId.toInt())
            }

            orderMainLayout.setOnClickListener {
                salesFragment.showQuantitySpinner(
                    selectedOrderItems[absoluteAdapterPosition],
                    absoluteAdapterPosition,
                    quantitySpinner
                )
            }

        }
    }


}