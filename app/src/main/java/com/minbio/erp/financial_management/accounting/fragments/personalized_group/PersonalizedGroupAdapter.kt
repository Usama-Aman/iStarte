package com.minbio.erp.financial_management.accounting.fragments.personalized_group

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedGroupData
import com.minbio.erp.utils.AppUtils

class PersonalizedGroupAdapter(
    _personalizedGroupsFragment: PersonalizedGroupsFragment,
    _personalizedGroupData: MutableList<PersonalizedGroupData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var alertDialog: AlertDialog
    private lateinit var context: Context
    private var personalizedGroupsFragment = _personalizedGroupsFragment
    private var personalizedGroupData = _personalizedGroupData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_personalized_group, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = personalizedGroupData.size

    override fun getItemViewType(position: Int): Int {
        return if (personalizedGroupData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var code: TextView = itemView.findViewById(R.id.code)
        private var label: TextView = itemView.findViewById(R.id.label)
        private var comment: TextView = itemView.findViewById(R.id.comment)
        private var calculated: TextView = itemView.findViewById(R.id.calculated)
        private var formula: TextView = itemView.findViewById(R.id.formula)
        private var tvposition: TextView = itemView.findViewById(R.id.position)
        private var country: TextView = itemView.findViewById(R.id.country)
        var status: Switch = itemView.findViewById(R.id.status)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {

            code.text = personalizedGroupData[position]?.code
            comment.text = personalizedGroupData[position]?.comment
            label.text = personalizedGroupData[position]?.label

            calculated.text = if (personalizedGroupData[position]?.calculated == 1)
                context.resources.getString(R.string.yes)
            else
                context.resources.getString(R.string.no)

            formula.text = personalizedGroupData[position]?.formula
            tvposition.text = personalizedGroupData[position]?.position.toString()
            country.text = personalizedGroupData[position]?.country?.name
            status.isChecked = personalizedGroupData[position]?.status == 1

            status.setOnClickListener {

                if (personalizedGroupData[position]?.status == 0)
                    personalizedGroupData[position]?.status = 1
                else
                    personalizedGroupData[position]?.status = 0

                personalizedGroupsFragment.updateStatus(
                    personalizedGroupData[position]?.id!!,
                    absoluteAdapterPosition
                )
            }


//            status.setOnCheckedChangeListener(null)


            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", personalizedGroupData[position])
                val fragment = AddPersonalizedGroupFragment()
                fragment.arguments = bundle
                personalizedGroupsFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {

                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        personalizedGroupsFragment.deletePersonalizedGroups(
                            personalizedGroupData[absoluteAdapterPosition]?.id!!,
                            absoluteAdapterPosition
                        )
                    }
                    .setNegativeButton(context.resources.getString(R.string.no)) { _, _ ->
                        alertDialog.dismiss()
                    }
                    .show()
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}