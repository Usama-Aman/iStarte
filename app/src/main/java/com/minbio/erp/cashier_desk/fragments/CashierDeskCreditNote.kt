package com.minbio.erp.cashier_desk.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.CashierDeskFragment
import com.minbio.erp.cashier_desk.models.CDCreditNoteModel
import com.minbio.erp.cashier_desk.models.CNOrdersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_cashier_desk_credit_note.*
import org.json.JSONObject

class CashierDeskCreditNote : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var orderSpinner: CustomSearchableSpinner
    private lateinit var statusSpinner: CustomSearchableSpinner
    private lateinit var btnSend: LinearLayout
    private lateinit var article_code_number: TextView

    private var customerId = 0
    private var complaintId = 0
    private var statusId = ""

    private lateinit var cdCreditNoteModel: CDCreditNoteModel
    private var creditNoteOrders: MutableList<CNOrdersData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_cashier_desk_credit_note, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        customerId = arguments!!.getInt("customerId")
        if (customerId != 0)
            getOrdersData()

        article_code_number = v.findViewById(R.id.article_code_number)
        article_code_number.text =
            context!!.resources.getString(R.string.cdcnLabelCustomerID, customerId.toString())
        btnSend = v.findViewById(R.id.btn_cdcn_send)
        btnSend.setOnClickListener { validate() }

        statusSpinner = v.findViewById(R.id.spinner_cdcn_status)
        orderSpinner = v.findViewById(R.id.spinner_cdcn_order_no)
    }

    private fun getOrdersData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCreditNoteOrders(customerId)
        RetrofitClient.apiCall(call, this, "GetCreditNoteOrders")
    }

    private fun validate() {

        if (et_cdcn_comment.text.toString().isBlank()) {
            error_cdcn_comment.visibility = View.VISIBLE
            error_cdcn_comment.setText(R.string.cdcnErrorComment);
            AppUtils.setBackground(et_cdcn_comment, R.drawable.round_box_stroke_red)
            et_cdcn_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cdcn_comment, R.drawable.round_box_light_stroke)
            error_cdcn_comment.visibility = View.INVISIBLE
        }

        postCreditNoteData()
    }

    private fun postCreditNoteData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postCreditNoteData(et_cdcn_comment.text.toString(), statusId, complaintId)
        RetrofitClient.apiCall(call, this, "PostCreditNote")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetCreditNoteOrders" -> {
                val gson = Gson()
                cdCreditNoteModel =
                    gson.fromJson(jsonObject.toString(), CDCreditNoteModel::class.java)
                creditNoteOrders.addAll(cdCreditNoteModel.data)

                if (creditNoteOrders.size == 0) {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.cdcnErrorNoComplaint),
                        false
                    )
                    Handler().postDelayed({
                        (parentFragment as CashierDeskFragment).cashierTabPayment.callOnClick()
                    }, 1000)
                } else {
                    setUpSpinners()
                }
            }
            "PostCreditNote" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                et_cdcn_comment.setText("")
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

        for (i in creditNoteOrders.indices) strings.add(creditNoteOrders[i].order_no)

        orderSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        orderSpinner.setTitle(resources.getString(R.string.orderNoSpinnerTitle))

        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, strings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        orderSpinner.adapter = adapter
        orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                complaintId = creditNoteOrders[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)

                for (j in creditNoteOrders.indices)
                    if (complaintId == creditNoteOrders[j].id)
                        updateViews(creditNoteOrders[j])

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        val statusStrings = ArrayList<String>()

        for (i in cdCreditNoteModel.complaint_status.indices) statusStrings.add(cdCreditNoteModel.complaint_status[i])

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
                statusId = cdCreditNoteModel.complaint_status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


    }

    private fun updateViews(cnOrdersData: CNOrdersData) {
        tv_cdcn_amount.text = cnOrdersData.amount
        tv_cdcn_complaint_no.text = cnOrdersData.complaint_no
    }


}