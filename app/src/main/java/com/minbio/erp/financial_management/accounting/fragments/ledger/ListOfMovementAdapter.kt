package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.NewTransactionMovements

class ListOfMovementAdapter(
    _movements: MutableList<NewTransactionMovements>,
    _newLedgerTransactionFragment: NewLedgerTransactionFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var alertDialog: AlertDialog
    private lateinit var context: Context
    private var movements = _movements
    private var newLedgerTransactionFragment = _newLedgerTransactionFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_list_of_movements, parent, false)
        )
    }

    override fun getItemCount(): Int = movements.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val account: TextView = itemView.findViewById(R.id.account)
        private val etSubledgerAccount: EditText = itemView.findViewById(R.id.etSubledgerAccount)
        private val etLabelOperation: EditText = itemView.findViewById(R.id.etLabelOperation)
        private val etDebit: EditText = itemView.findViewById(R.id.etDebit)
        private val etCredit: EditText = itemView.findViewById(R.id.etCredit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            etSubledgerAccount.tag = position
            etLabelOperation.tag = position
            etDebit.tag = position
            etCredit.tag = position
            delete.tag = position

            account.text =
                movements[position].chart_account_number + " - " + movements[position].chart_account_label
            etSubledgerAccount.setText(movements[position].sub_ledger_account)
            etLabelOperation.setText(movements[position].label)
            etDebit.setText(movements[position].debit)
            etCredit.setText(movements[position].credit)

            etSubledgerAccount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    movements[absoluteAdapterPosition].sub_ledger_account = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            etLabelOperation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    movements[absoluteAdapterPosition].label = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            etDebit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    movements[absoluteAdapterPosition].debit = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            etCredit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    movements[absoluteAdapterPosition].credit = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            account.setOnClickListener {
                newLedgerTransactionFragment.showSpinner(absoluteAdapterPosition)
            }

            delete.setOnClickListener {
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        movements.removeAt(absoluteAdapterPosition)
                        notifyDataSetChanged()
                    }
                    .setNegativeButton(context.resources.getString(R.string.no)) { _, _ ->
                        alertDialog.dismiss()
                    }
                    .show()


            }

            if (movements[position].isDebitCreditBothEdit) {
                if (etCredit.tag == position && etDebit.tag == position) {
                    etCredit.background =
                        ContextCompat.getDrawable(context, R.drawable.input_border_bottom_red)
                    etDebit.background =
                        ContextCompat.getDrawable(context, R.drawable.input_border_bottom_red)
                }
            } else {
                if (etCredit.tag == position && etDebit.tag == position) {
                    etCredit.background =
                        ContextCompat.getDrawable(context, R.drawable.input_border_bottom)
                    etDebit.background =
                        ContextCompat.getDrawable(context, R.drawable.input_border_bottom)
                }
            }

        }
    }

}