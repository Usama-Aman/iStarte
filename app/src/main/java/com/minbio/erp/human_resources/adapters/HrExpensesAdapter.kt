package com.minbio.erp.human_resources.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.human_resources.fragments.HrExpensesFragment
import com.minbio.erp.human_resources.models.ExpensesListData
import com.minbio.erp.utils.AppUtils

class HrExpensesAdapter(
    _hrExpensesFragment: HrExpensesFragment,
    _expensesList: MutableList<ExpensesListData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val hrExpensesFragment = _hrExpensesFragment
    private val expensesList = _expensesList

    private lateinit var context: Context
    private lateinit var loginModel: LoginModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        loginModel = AppUtils.getLoginModel(context)
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hr_expenses, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = expensesList.size

    override fun getItemViewType(position: Int): Int {
        return if (expensesList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mainLayout: LinearLayout = itemView.findViewById(R.id.mainHRExpenseLayout)
        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val user: TextView = itemView.findViewById(R.id.user)
        private val startDate: TextView = itemView.findViewById(R.id.startDate)
        private val validationDate: TextView = itemView.findViewById(R.id.validationDate)
        private val approvingDate: TextView = itemView.findViewById(R.id.approvingDate)
        private val amount: TextView = itemView.findViewById(R.id.amount)
        private val status: TextView = itemView.findViewById(R.id.status)


        fun bind(position: Int) {
            ref.text = expensesList[position]?.company_user_id.toString()
            startDate.text = expensesList[position]?.date
            user.text = expensesList[position]?.company_user_name
            validationDate.text = expensesList[position]?.validation_date
            approvingDate.text = expensesList[position]?.approvel_date
            amount.text = expensesList[position]?.amount.toString()
            startDate.text = expensesList[position]?.date
            status.text = expensesList[position]?.status
            when {
                expensesList[position]?.status.equals("Approved", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_green_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                expensesList[position]?.status.equals("Assigned", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.detail_h_color))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_gray_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                expensesList[position]?.status.equals("Pending", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.detail_h_color))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_gray_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                expensesList[position]?.status.equals("Refused", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_red_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
            }

            mainLayout.setOnClickListener {
                hrExpensesFragment.itemClick(expensesList[position])
            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)
}

