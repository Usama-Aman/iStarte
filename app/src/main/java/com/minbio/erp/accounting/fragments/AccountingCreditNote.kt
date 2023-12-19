package com.minbio.erp.accounting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.accounting.models.AccountingCreditNoteData
import com.minbio.erp.accounting.models.AccountingCreditNoteModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_accounting_credit_note.*
import org.json.JSONObject

class AccountingCreditNote : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btnSend: LinearLayout
    private lateinit var statusSpinner: CustomSearchableSpinner
    private lateinit var supplierInvoiceSpinner: CustomSearchableSpinner

    private lateinit var article_code_number: TextView
    private lateinit var tv_acn_supplier_delivery_note_no: TextView
    private lateinit var tv_acn_credit_note_closed: TextView
    private lateinit var tv_acn_comlaint_no: TextView
    private lateinit var tv_acn_email: TextView
    private lateinit var tv_acn_amount: TextView

    private lateinit var accountingCreditNoteModel: AccountingCreditNoteModel
    private var accountingCreditNoteData: MutableList<AccountingCreditNoteData> = ArrayList()

    private var selectedId = 0
    private var invoiceId = 0
    private var statusId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_credit_note, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        selectedId = arguments?.getInt("selectedId")!!

        btnSend = v.findViewById(R.id.btn_acn_send)
        btnSend.setOnClickListener { validate() }

        supplierInvoiceSpinner = v.findViewById(R.id.spinner_acn_supplier_invoice_no)
        statusSpinner = v.findViewById(R.id.spinner_acn_status)

        tv_acn_supplier_delivery_note_no = v.findViewById(R.id.tv_acn_supplier_delivery_note_no)
        tv_acn_credit_note_closed = v.findViewById(R.id.tv_acn_credit_note_closed)
        tv_acn_comlaint_no = v.findViewById(R.id.tv_acn_comlaint_no)
        tv_acn_email = v.findViewById(R.id.tv_acn_email)
        tv_acn_amount = v.findViewById(R.id.tv_acn_amount)

        article_code_number = v.findViewById(R.id.article_code_number)

        if (selectedId != 0) {
            article_code_number.text = context!!.resources.getString(
                R.string.acnLabelSupplierIDNumber,
                selectedId.toString()
            )
            getCreditNote()
        } else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }

    private fun validate() {
        if (et_acn_comment.text.toString().isBlank()) {
            error_acn_comment.visibility = View.VISIBLE
            error_acn_comment.setText(R.string.acnErrorComment)
            AppUtils.setBackground(et_acn_comment, R.drawable.round_box_stroke_red)
            et_acn_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_acn_comment, R.drawable.round_box_light_stroke)
            error_acn_comment.visibility = View.INVISIBLE
        }

        postCreditNoteData()
    }

    private fun postCreditNoteData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postAccountingCreditNote(error_acn_comment.text.toString(), statusId, invoiceId)
        RetrofitClient.apiCall(call, this, "PostCreditNote")
    }


    private fun getCreditNote() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getAccountingCreditNote(selectedId)
        RetrofitClient.apiCall(call, this, "GetCreditNote")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetCreditNote" -> {
                val gson = Gson()
                accountingCreditNoteModel =
                    gson.fromJson(jsonObject.toString(), AccountingCreditNoteModel::class.java)
                accountingCreditNoteData.addAll(accountingCreditNoteModel.data)

                setUpSpinners()
            }
            "PostCreditNote" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                et_acn_comment.setText("")
            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    private fun setUpSpinners() {
        val strings = ArrayList<String>()

        for (i in accountingCreditNoteData.indices) strings.add(accountingCreditNoteData[i].invoice_no)

        supplierInvoiceSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        supplierInvoiceSpinner.setTitle(resources.getString(R.string.orderNoSpinnerTitle))

        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, strings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        supplierInvoiceSpinner.adapter = adapter
        supplierInvoiceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    invoiceId = accountingCreditNoteData[adapterView.selectedItemPosition].id
                    AppUtils.hideKeyboard(activity!!)

                    for (j in accountingCreditNoteData.indices)
                        if (invoiceId == accountingCreditNoteData[j].id)
                            updateViews(accountingCreditNoteData[j])

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }


        val statusStrings = ArrayList<String>()

        for (i in accountingCreditNoteModel.complaint_status.indices) statusStrings.add(
            accountingCreditNoteModel.complaint_status[i]
        )

        statusSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        statusSpinner.setTitle(resources.getString(R.string.statusSpinnerTitle))

        val statusAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, statusStrings
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                statusId =
                    accountingCreditNoteModel.complaint_status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    private fun updateViews(accountingCreditNoteData: AccountingCreditNoteData) {
        tv_acn_supplier_delivery_note_no.text = accountingCreditNoteData.delivery_note
        tv_acn_credit_note_closed.text = accountingCreditNoteData.date
        tv_acn_comlaint_no.text = accountingCreditNoteData.complaint_no
        tv_acn_email.text = accountingCreditNoteData.supplier_email
        tv_acn_amount.text = accountingCreditNoteData.amount
    }

}