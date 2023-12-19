package com.minbio.erp.financial_management.accounting.fragments.product_accounts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData

class ProductAccountsAdapter(
    _productAccountsFragment: ProductAccountsFragment,
    _productAccountsData: MutableList<ProductAccountsData?>,
    _parentAccountList: MutableList<ParentAccountsData?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var productAccountsFragment = _productAccountsFragment
    private var productAccountsData = _productAccountsData
    private var parentAccountList = _parentAccountList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_product_account, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = productAccountsData.size

    override fun getItemViewType(position: Int): Int {
        return if (productAccountsData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val label: TextView = itemView.findViewById(R.id.label)
        private val currentDedicatedAccount: TextView =
            itemView.findViewById(R.id.currentDedicatedAccount)
        private val newAssignedAccount: TextView = itemView.findViewById(R.id.newAssignedAccount)
        private val mainProductAccountLayout: RelativeLayout = itemView.findViewById(R.id.mainProductAccountLayout)

        fun bind(position: Int) {

            ref.text = productAccountsData[position]?.id.toString()
            label.text = productAccountsData[position]?.product_variety
            currentDedicatedAccount.text = productAccountsData[position]?.account_number
            newAssignedAccount.text = productAccountsData[position]?.chart_account_label


            mainProductAccountLayout.setOnClickListener {
                productAccountsFragment.showAccountSpinner(position)

            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}