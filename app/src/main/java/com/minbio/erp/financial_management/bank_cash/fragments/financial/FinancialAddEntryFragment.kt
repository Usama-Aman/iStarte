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
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_add_entry.*
import org.json.JSONObject
import java.util.*

class FinancialAddEntryFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var tvPayDate: TextView
    private lateinit var tvValueDate: TextView
    private lateinit var etPayLabel: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCheckNumber: EditText
    private lateinit var etSubledgerAccount: EditText
    private lateinit var spinnerParentAccount: CustomSearchableSpinner
    private lateinit var spinnerSens: CustomSearchableSpinner
    private lateinit var spinnerBankAccount: CustomSearchableSpinner
    private lateinit var spinnerPayType: CustomSearchableSpinner
    private lateinit var btnSave: LinearLayout
    private lateinit var back: ImageView

    private var payDateId = ""
    private var valueDateId = ""
    private var sensId = ""
    private var bankAccountId = 0
    private var payTypeId = 0

    private lateinit var bankCashMetaData: BankCashMetaData

    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var parentAccountId: Int = 0
    private lateinit var api: Api

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_add_entry, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        getPareAccountList()

        return v
    }

    private fun initViews() {
        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }
        etPayLabel = v.findViewById(R.id.etPayLabel)
        etAmount = v.findViewById(R.id.etAmount)
        etCheckNumber = v.findViewById(R.id.etCheckNumber)
        spinnerParentAccount = v.findViewById(R.id.spinnerParentAccount)
        etSubledgerAccount = v.findViewById(R.id.etSubledgerAccount)
        spinnerSens = v.findViewById(R.id.spinnerSens)
        spinnerBankAccount = v.findViewById(R.id.spinnerBankAccount)
        spinnerPayType = v.findViewById(R.id.spinnerPayType)

        tvPayDate = v.findViewById(R.id.tvPayDate)
        tvPayDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvPayDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    payDateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }
        tvValueDate = v.findViewById(R.id.tvValueDate)
        tvValueDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvValueDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    valueDateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        btnSave = v.findViewById(R.id.btnSave)
        btnSave.setOnClickListener { validate() }
    }

    private fun validate() {
        if (tvPayDate.text.toString().isBlank()) {
            error_pay_date.visibility = View.VISIBLE
            error_pay_date.setText(R.string.fadeErrorDateOfPay);
            AppUtils.setBackground(tvPayDate, R.drawable.input_border_bottom_red)
            tvPayDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvPayDate, R.drawable.input_border_bottom)
            error_pay_date.visibility = View.INVISIBLE
        }

        if (tvValueDate.text.toString().isBlank()) {
            error_value_date.visibility = View.VISIBLE
            error_value_date.setText(R.string.fadeErrorValueDate);
            AppUtils.setBackground(tvValueDate, R.drawable.input_border_bottom_red)
            tvValueDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvValueDate, R.drawable.input_border_bottom)
            error_value_date.visibility = View.INVISIBLE
        }

        if (etPayLabel.text.toString().isBlank()) {
            error_pay_label.visibility = View.VISIBLE
            error_pay_label.setText(R.string.fadeErrorLabel);
            AppUtils.setBackground(etPayLabel, R.drawable.input_border_bottom_red)
            etPayLabel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etPayLabel, R.drawable.input_border_bottom)
            error_pay_label.visibility = View.INVISIBLE
        }

        if (etAmount.text.toString().isBlank()) {
            error_amount.visibility = View.VISIBLE
            error_amount.setText(R.string.fadeErrorAmount);
            AppUtils.setBackground(etAmount, R.drawable.input_border_bottom_red)
            etAmount.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAmount, R.drawable.input_border_bottom)
            error_amount.visibility = View.INVISIBLE
        }

//        if (etCheckNumber.text.toString().isBlank()) {
//            error_check_number.visibility = View.VISIBLE
//            error_check_number.setText(R.string.fadeErrorCheckNumber);
//            AppUtils.setBackground(etCheckNumber, R.drawable.input_border_bottom_red)
//            etCheckNumber.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etCheckNumber, R.drawable.input_border_bottom)
//            error_check_number.visibility = View.INVISIBLE
//        }


        if (etSubledgerAccount.text.toString().isBlank()) {
            error_sub_account.visibility = View.VISIBLE
            error_sub_account.setText(R.string.fadeErrorSubledgerAccount);
            AppUtils.setBackground(etSubledgerAccount, R.drawable.input_border_bottom_red)
            etSubledgerAccount.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSubledgerAccount, R.drawable.input_border_bottom)
            error_sub_account.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = api.postFinancialEntry(
            payDateId, valueDateId, etPayLabel.text.toString(), sensId,
            etAmount.text.toString(), bankAccountId, payTypeId, etCheckNumber.text.toString(),
            parentAccountId, etSubledgerAccount.text.toString()
        )
        RetrofitClient.apiCall(call, this, "PostAddEntry")
    }

    private fun getMetaData() {
        val call = api.getBankCashMetaData()
        RetrofitClient.apiCall(call, this, "GetMetaData")
    }

    private fun getPareAccountList() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetMetaData" -> {
                AppUtils.dismissDialog()
                handleMetaDataResponse(jsonObject)
            }
            "PostAddEntry" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                parentAccountList.add(
                    ParentAccountsData(
                        "",
                        0,
                        ""
                    )
                )
                parentAccountList.addAll(model.data)
                setUpParentAccountSpinner()
                getMetaData()
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

    private fun setUpParentAccountSpinner() {
        val strings = ArrayList<String>()
        for (i in parentAccountList.indices) {
            if (i > 0)
                strings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                strings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerParentAccount.adapter = positionAdapter
        spinnerParentAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerParentAccount.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinnerParentAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parentAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

    }


    private fun handleMetaDataResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model = gson.fromJson(jsonObject.toString(), BankCashMetaDataModel::class.java)

        bankCashMetaData = model.data

        /*Bank Accounts spinner*/
        val bankAccount = ArrayList<String>()
        for (i in bankCashMetaData.accounts.indices) bankAccount.add(
            bankCashMetaData.accounts[i].label
        )

        spinnerBankAccount.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerBankAccount.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val countryAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, bankAccount
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBankAccount.adapter = countryAdapter
        spinnerBankAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                bankAccountId = bankCashMetaData.accounts[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Sens SPinner*/
        val sensStrings = ArrayList<String>()
        for (i in bankCashMetaData.types.indices) sensStrings.add(bankCashMetaData.types[i])

        spinnerSens.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerSens.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val sensAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, sensStrings
        )
        sensAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSens.adapter = sensAdapter
        spinnerSens.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                sensId = bankCashMetaData.types[adapterView.selectedItemPosition]
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
