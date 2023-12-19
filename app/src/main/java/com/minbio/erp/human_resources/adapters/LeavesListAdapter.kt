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
import com.minbio.erp.human_resources.fragments.HrLeaveFragment
import com.minbio.erp.human_resources.models.LeavesListData
import com.minbio.erp.utils.AppUtils

class LeavesListAdapter(
    _hrLeaveFragment: HrLeaveFragment,
    _leavesList: MutableList<LeavesListData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val hrLeaveFragment = _hrLeaveFragment
    private val leavesList = _leavesList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hr_leaves, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = leavesList.size

    override fun getItemViewType(position: Int): Int {
        return if (leavesList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mainLayout: LinearLayout = itemView.findViewById(R.id.mainHRLeavesLayout)
        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val creationDate: TextView = itemView.findViewById(R.id.creationDate)
        private val employee: TextView = itemView.findViewById(R.id.employee)
        private val approbator: TextView = itemView.findViewById(R.id.approbator)
        private val type: TextView = itemView.findViewById(R.id.type)
        private val days: TextView = itemView.findViewById(R.id.days)
        private val startDate: TextView = itemView.findViewById(R.id.startDate)
        private val endDate: TextView = itemView.findViewById(R.id.endDate)
        private val status: TextView = itemView.findViewById(R.id.status)

        fun bind(position: Int) {
            ref.text = leavesList[position]?.company_user_id.toString()
            creationDate.text = leavesList[position]?.date
            employee.text = leavesList[position]?.company_user_name
            approbator.text = leavesList[position]?.aproved_by
            type.text = leavesList[position]?.leave_type
            days.text = leavesList[position]?.days.toString()
            startDate.text =
                "${leavesList[position]?.start_date}\n(${leavesList[position]?.start_half})"
            endDate.text = "${leavesList[position]?.end_date}\n(${leavesList[position]?.end_half})"
            status.text = leavesList[position]?.status
            when {
                leavesList[position]?.status.equals("Approved", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_green_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                leavesList[position]?.status.equals("Assigned", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.detail_h_color))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_gray_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                leavesList[position]?.status.equals("Pending", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.detail_h_color))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_gray_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
                leavesList[position]?.status.equals("Refused", false) -> {
                    status.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                    status.setCompoundDrawables(
                        null, null,
                        ContextCompat.getDrawable(context, R.drawable.ic_red_status_circle)
                            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }, null
                    )
                }
            }

            mainLayout.setOnClickListener {
                hrLeaveFragment.itemClick(leavesList[position])
            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)
}

