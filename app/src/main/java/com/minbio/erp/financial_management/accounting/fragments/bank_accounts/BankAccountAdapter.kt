package com.minbio.erp.financial_management.accounting.fragments.bank_accounts

//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.minbio.erp.R
//
//class BankAccountAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//
//    private lateinit var context: Context
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        context = parent.context
//        return /*if (viewType == 0)*/ Items(
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_accounting_bank_accounts, parent, false)
//        )
////        else
////            Progress(
////                LayoutInflater.from(parent.context)
////                    .inflate(R.layout.item_pagination_progress, parent, false)
////            )
//    }
//
//    override fun getItemCount(): Int = 20
//
////    override fun getItemViewType(position: Int): Int {
////        return if (financialAccountList[position] == null) 1 else 0
////    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is Items)
//            holder.bind(position)
//    }
//
//    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//
//        fun bind(position: Int) {
//
//        }
//    }
//
//    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//
//}