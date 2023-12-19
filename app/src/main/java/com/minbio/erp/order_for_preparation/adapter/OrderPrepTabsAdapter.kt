package com.minbio.erp.order_for_preparation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.order_for_preparation.OrderPreparationFragment
import com.minbio.erp.order_for_preparation.models.OPRightTabs

class OrderPrepTabsAdapter(
    _orderPreparationFragment: OrderPreparationFragment,
    _opRightTabs: MutableList<OPRightTabs>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var orderPreparationFragment = _orderPreparationFragment
    private var opRightTabs = _opRightTabs
    private lateinit var context: Context

    var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.right_tab_radio_button, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return opRightTabs.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var rightRadio: RadioButton = itemView.findViewById(R.id.rightRadio)

        fun bind(position: Int) {
            rightRadio.text = opRightTabs[position].order_no

            if (opRightTabs.size == 1)
                rightRadio.background =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.tab_left_right_button_selector
                    )
            else
                when (position) {
                    0 -> {
                        rightRadio.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.tab_left_button_selector
                            )
                    }
                    opRightTabs.size - 1 -> {
                        rightRadio.background =
                            ContextCompat.getDrawable(context, R.drawable.tab_right_button_selector)
                    }
                    else -> {
                        rightRadio.background =
                            ContextCompat.getDrawable(context, R.drawable.tab_mid_button_selector)
                    }
                }

            if (position == selected) {
                rightRadio.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorDarkBlue
                    )
                )
                orderPreparationFragment.showOrderDetail(
                    opRightTabs[absoluteAdapterPosition],
                    position
                )
                rightRadio.isChecked = true

            } else {
                rightRadio.isChecked = false
                rightRadio.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
            }


            rightRadio.setOnClickListener {
                selected = position
                notifyDataSetChanged()
            }

        }
    }
}