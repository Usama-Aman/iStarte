package com.minbio.erp.financial_management.bank_cash.fragments.financial

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaData
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaDataModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_internal_transfer.*
import org.json.JSONObject
import java.util.*

class FinancialInternalTransferFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var spinnerFrom: CustomSearchableSpinner
    private lateinit var spinnerTo: CustomSearchableSpinner
    private lateinit var spinnerPayType: CustomSearchableSpinner
    private lateinit var tvDate: TextView
    private lateinit var etTransferAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnAdd: LinearLayout

    private var dateId = ""
    private var fromId = 0
    private var toId = 0
    private var payTypeId = 0
    private lateinit var bankCashMetaData: BankCashMetaData


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_internal_transfer, container, false)

        initViews()
        getMetaData()

        return v
    }

    private fun initViews() {
        spinnerFrom = v.findViewById(R.id.spinnerFrom)
        spinnerTo = v.findViewById(R.id.spinnerTo)
        spinnerPayType = v.findViewById(R.id.spinnerPayType)
        tvDate = v.findViewById(R.id.tvDate)
        tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    dateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        etTransferAmount = v.findViewById(R.id.etTransferAmount)
        etDescription = v.findViewById(R.id.etDescription)

        btnAdd = v.findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener { validate() }
    }

    private fun validate() {
        if (tvDate.text.toString().isBlank()) {
            error_date.visibility = View.VISIBLE
            error_date.setText(R.string.fitErrorDate);
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom_red)
            tvDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom)
            error_date.visibility = View.INVISIBLE
        }

        if (etTransferAmount.text.toString().isBlank()) {
            error_transfer_amount.visibility = View.VISIBLE
            error_transfer_amount.setText(R.string.fitErrorAmount);
            AppUtils.setBackground(etTransferAmount, R.drawable.input_border_bottom_red)
            etTransferAmount.requestFocus()
            return
        } else {
            AppUtils.setBackground(etTransferAmount, R.drawable.input_border_bottom)
            error_transfer_amount.visibility = View.INVISIBLE
        }

        if (etDescription.text.toString().isBlank()) {
            error_description.visibility = View.VISIBLE
            error_description.setText(R.string.fitErrorDescription)
            AppUtils.setBackground(etDescription, R.drawable.input_border_bottom_red)
            etDescription.requestFocus()
            return
        } else {
            AppUtils.setBackground(etDescription, R.drawable.input_border_bottom)
            error_description.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postFinancialInternalTransfer(
            fromId,
            toId,
            etDescription.text.toString(),
            etTransferAmount.text.toString(),
            dateId,
            payTypeId
        )
        RetrofitClient.apiCall(call, this, "PostInternalTransfer")

    }

    private fun getMetaData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getBankCashMetaData()
        RetrofitClient.apiCall(call, this, "GetMetaData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetMetaData" -> {
                handleMetaDataResponse(jsonObject)
            }
            "PostInternalTransfer" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).bankCashTab.callOnClick()

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

    private fun handleMetaDataResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model = gson.fromJson(jsonObject.toString(), BankCashMetaDataModel::class.java)

        bankCashMetaData = model.data

        /*From spinner*/
        val fromStrings = ArrayList<String>()
        for (i in bankCashMetaData.accounts.indices)
            fromStrings.add(bankCashMetaData.accounts[i].label
        )

        spinnerFrom.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerFrom.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val countryAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, fromStrings
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = countryAdapter
        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                fromId = bankCashMetaData.accounts[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*To SPinner*/
        val toStrings = ArrayList<String>()
        for (i in bankCashMetaData.accounts.indices) toStrings.add(bankCashMetaData.accounts[i].label)

        spinnerTo.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerTo.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val toAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, toStrings
        )
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTo.adapter = toAdapter
        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                toId = bankCashMetaData.accounts[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Payment Type Spinner*/
        val payStrings = ArrayList<String>()
        for (i in bankCashMetaData.payment_types.indices) payStrings.add(
            bankCashMetaData.payment_types[i].type
        )

        spinnerPayType.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerPayType.setTitle(resources.getString(R.string.payTypeSpinnerTitle))

        val currencyAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, payStrings
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPayType.adapter = currencyAdapter
        spinnerPayType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                payTypeId = bankCashMetaData.payment_types[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }


}