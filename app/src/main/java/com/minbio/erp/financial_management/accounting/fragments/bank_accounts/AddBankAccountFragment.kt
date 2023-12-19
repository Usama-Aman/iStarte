package com.minbio.erp.financial_management.accounting.fragments.bank_accounts

//import android.app.Activity
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.gson.Gson
//import com.minbio.erp.R
//import com.minbio.erp.financial_management.FinancialManagementFragment
//import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaData
//import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaDataModel
//import com.minbio.erp.maps.AddressMapsActivity
//import com.minbio.erp.network.Api
//import com.minbio.erp.network.ResponseCallBack
//import com.minbio.erp.network.RetrofitClient
//import com.minbio.erp.utils.AppUtils
//import com.minbio.erp.utils.Constants
//import com.minbio.erp.utils.CustomSearchableSpinner
//import kotlinx.android.synthetic.main.fragment_financial_accounting_add_bank_account.*
//import org.json.JSONObject
//import java.util.*
//
//class AddBankAccountFragment : Fragment(), ResponseCallBack {
//
//    private lateinit var v: View
//    private lateinit var back: ImageView
//    private lateinit var etRef: EditText
//    private lateinit var etBankCash: EditText
//    private lateinit var etState: EditText
//    private lateinit var etWeb: EditText
//    private lateinit var etComment: EditText
//    private lateinit var etInitialBalance: EditText
//    private lateinit var etMinAllowedBalance: EditText
//    private lateinit var etMaxDesiredBalance: EditText
//    private lateinit var etBankName: EditText
//    private lateinit var etBankCode: EditText
//    private lateinit var etAccountNumber: EditText
//    private lateinit var etIBAN: EditText
//    private lateinit var etBicCode: EditText
//    private lateinit var etAccountOwnerName: EditText
//    private lateinit var tvDate: TextView
//    private lateinit var tvBankAddress: TextView
//    private lateinit var tvAccountOwnerAddress: TextView
//    private lateinit var btnCreateAccount: LinearLayout
//
//    private lateinit var spinnerAccountType: CustomSearchableSpinner
//    private lateinit var spinnerCurrency: CustomSearchableSpinner
//    private lateinit var spinnerStatus: CustomSearchableSpinner
//    private lateinit var spinnerAccountCountry: CustomSearchableSpinner
//    private lateinit var spinnerAccountingAccount: CustomSearchableSpinner
//    private lateinit var spinnerAccountingCodeJournal: CustomSearchableSpinner
//
//    private var currencyId = 0
//    private var accountCountryId = 0
//    private var statusId = ""
//    private var accountTypeId = 0
//
//
//    private var dateId = ""
//    private var bankLatLng = LatLng(0.0, 0.0)
//    private var accountOwnerLatLng = LatLng(0.0, 0.0)
//
//    private lateinit var bankCashMetaData: BankCashMetaData
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        v = inflater.inflate(
//            R.layout.fragment_financial_accounting_add_bank_account,
//            container,
//            false
//        )
//
//        initViews()
//        getFinancialMetaData()
//
//        return v
//    }
//
//    private fun initViews() {
//        back = v.findViewById(R.id.back)
//        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }
//        etRef = v.findViewById(R.id.etRef)
//        etBankCash = v.findViewById(R.id.etBankCash)
//        etState = v.findViewById(R.id.etState)
//        etWeb = v.findViewById(R.id.etWeb)
//        etComment = v.findViewById(R.id.etComment)
//        etInitialBalance = v.findViewById(R.id.etInitialBalance)
//        etMinAllowedBalance = v.findViewById(R.id.etMinAllowedBalance)
//        etMaxDesiredBalance = v.findViewById(R.id.etMaxDesiredBalance)
//        etBankName = v.findViewById(R.id.etBankName)
//        etBankCode = v.findViewById(R.id.etBankCode)
//        etAccountNumber = v.findViewById(R.id.etAccountNumber)
//        etIBAN = v.findViewById(R.id.etIBAN)
//        etBicCode = v.findViewById(R.id.etBicCode)
//        etAccountOwnerName = v.findViewById(R.id.etAccountOwnerName)
//        spinnerAccountingAccount = v.findViewById(R.id.spinnerAccountingAccount)
//        spinnerAccountingCodeJournal = v.findViewById(R.id.spinnerAccountingCodeJournal)
//
//        tvDate = v.findViewById(R.id.tvDate)
//        tvDate.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val datePicker = DatePickerDialog(
//                activity!!,
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    tvDate.text =
//                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
//
//                    dateId = "$day-${monthOfYear + 1}-$year"
//
//                },
//                year,
//                month,
//                day
//            )
//            datePicker.show()
//        }
//        tvBankAddress = v.findViewById(R.id.tvBankAddress)
//        tvBankAddress.setOnClickListener {
//            getAddress(
//                Constants.BANK_ADDRESS_CODE,
//                bankLatLng,
//                tvBankAddress.text.toString()
//            )
//        }
//        tvAccountOwnerAddress = v.findViewById(R.id.tvAccountOwnerAddress)
//        tvAccountOwnerAddress.setOnClickListener {
//            getAddress(
//                Constants.ACCOUNT_OWNER_ADDRESS_CODE,
//                accountOwnerLatLng,
//                tvAccountOwnerAddress.text.toString()
//            )
//        }
//
//        btnCreateAccount = v.findViewById(R.id.btnCreateAccount)
//
//        spinnerAccountType = v.findViewById(R.id.spinnerAccountType)
//        spinnerCurrency = v.findViewById(R.id.spinnerCurrency)
//        spinnerStatus = v.findViewById(R.id.spinnerStatus)
//        spinnerAccountCountry = v.findViewById(R.id.spinnerAccountCountry)
//
//        btnCreateAccount.setOnClickListener { validate() }
//
//
//    }
//
//    private fun getFinancialMetaData() {
//        AppUtils.showDialog(context!!)
//        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
//        val call = api.getBankCashMetaData()
//        RetrofitClient.apiCall(call, this, "GetMetaData")
//    }
//
//    private fun getAddress(code: Int, latLng: LatLng, address: String) {
//        val intent = Intent(context, AddressMapsActivity::class.java)
//        val bundle = Bundle()
//        bundle.putDouble("lat", latLng.latitude)
//        bundle.putDouble("lng", latLng.longitude)
//        bundle.putString("address", address)
//        intent.putExtras(bundle)
//        startActivityForResult(intent, code)
//    }
//
//    private fun validate() {
//        if (etRef.text.toString().isBlank()) {
//            error_ref.visibility = View.VISIBLE
//            error_ref.setText(R.string.fabaErrorRef);
//            AppUtils.setBackground(etRef, R.drawable.input_border_bottom_red)
//            etRef.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etRef, R.drawable.input_border_bottom)
//            error_ref.visibility = View.INVISIBLE
//        }
//
//        if (etBankCash.text.toString().isBlank()) {
//            error_bank_cash.visibility = View.VISIBLE
//            error_bank_cash.setText(R.string.fabaErrorBankOrCash);
//            AppUtils.setBackground(etBankCash, R.drawable.input_border_bottom_red)
//            etBankCash.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etBankCash, R.drawable.input_border_bottom)
//            error_bank_cash.visibility = View.INVISIBLE
//        }
//
//        if (etState.text.toString().isBlank()) {
//            error_state.visibility = View.VISIBLE
//            error_state.setText(R.string.fabaErrorState);
//            AppUtils.setBackground(etState, R.drawable.input_border_bottom_red)
//            etState.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etState, R.drawable.input_border_bottom)
//            error_state.visibility = View.INVISIBLE
//        }
//
//        if (etWeb.text.toString().isBlank()) {
//            error_web.visibility = View.VISIBLE
//            error_web.setText(R.string.fabaErrorWeb);
//            AppUtils.setBackground(etWeb, R.drawable.input_border_bottom_red)
//            etWeb.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etWeb, R.drawable.input_border_bottom)
//            error_web.visibility = View.INVISIBLE
//        }
//
//        if (etComment.text.toString().isBlank()) {
//            error_comment.visibility = View.VISIBLE
//            error_comment.setText(R.string.fabaErrorComment);
//            AppUtils.setBackground(etComment, R.drawable.round_box_stroke_red)
//            etComment.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etComment, R.drawable.round_box_light_stroke)
//            error_comment.visibility = View.INVISIBLE
//        }
//
//        if (etInitialBalance.text.toString().isBlank()) {
//            error_initial_balance.visibility = View.VISIBLE
//            error_initial_balance.setText(R.string.fabaErrorInitialBalance);
//            AppUtils.setBackground(etInitialBalance, R.drawable.input_border_bottom_red)
//            etInitialBalance.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etInitialBalance, R.drawable.input_border_bottom)
//            error_initial_balance.visibility = View.INVISIBLE
//        }
//
//        if (tvDate.text.toString().isBlank()) {
//            error_date.visibility = View.VISIBLE
//            error_date.setText(R.string.fabaErrorDate);
//            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom_red)
//            tvDate.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom)
//            error_date.visibility = View.INVISIBLE
//        }
//
//        if (etMinAllowedBalance.text.toString().isBlank()) {
//            error_min_allowed.visibility = View.VISIBLE
//            error_min_allowed.setText(R.string.fabaErrorMinAllowedBalance);
//            AppUtils.setBackground(etMinAllowedBalance, R.drawable.input_border_bottom_red)
//            etMinAllowedBalance.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etMinAllowedBalance, R.drawable.input_border_bottom)
//            error_min_allowed.visibility = View.INVISIBLE
//        }
//
//        if (etMaxDesiredBalance.text.toString().isBlank()) {
//            error_max_desired.visibility = View.VISIBLE
//            error_max_desired.setText(R.string.fabaErrorMaxDesiredBalance);
//            AppUtils.setBackground(etMaxDesiredBalance, R.drawable.input_border_bottom_red)
//            etMaxDesiredBalance.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etMaxDesiredBalance, R.drawable.input_border_bottom)
//            error_max_desired.visibility = View.INVISIBLE
//        }
//
//        if (etBankName.text.toString().isBlank()) {
//            error_bank_name.visibility = View.VISIBLE
//            error_bank_name.setText(R.string.fabaErrorBankName);
//            AppUtils.setBackground(etBankName, R.drawable.input_border_bottom_red)
//            etBankName.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etBankName, R.drawable.input_border_bottom)
//            error_bank_name.visibility = View.INVISIBLE
//        }
//
//        if (etBankCode.text.toString().isBlank()) {
//            error_bank_code.visibility = View.VISIBLE
//            error_bank_code.setText(R.string.fabaErrorBankCode);
//            AppUtils.setBackground(etBankCode, R.drawable.input_border_bottom_red)
//            etBankCode.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etBankCode, R.drawable.input_border_bottom)
//            error_bank_code.visibility = View.INVISIBLE
//        }
//
//        if (etAccountNumber.text.toString().isBlank()) {
//            error_account_number.visibility = View.VISIBLE
//            error_account_number.setText(R.string.fabaErrorAccountNumber);
//            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom_red)
//            etAccountNumber.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom)
//            error_account_number.visibility = View.INVISIBLE
//        }
//
//        if (etIBAN.text.toString().isBlank()) {
//            error_iban.visibility = View.VISIBLE
//            error_iban.setText(R.string.fabaErrorIBANAccountNumber);
//            AppUtils.setBackground(etIBAN, R.drawable.input_border_bottom_red)
//            etIBAN.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etIBAN, R.drawable.input_border_bottom)
//            error_iban.visibility = View.INVISIBLE
//        }
//
//        if (etBicCode.text.toString().isBlank()) {
//            error_bic_code.visibility = View.VISIBLE
//            error_bic_code.setText(R.string.fabaErrorBicSwiftCide);
//            AppUtils.setBackground(etBicCode, R.drawable.input_border_bottom_red)
//            etBicCode.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etBicCode, R.drawable.input_border_bottom)
//            error_bic_code.visibility = View.INVISIBLE
//        }
//
//        if (tvBankAddress.text.toString().isBlank()) {
//            error_bank_address.visibility = View.VISIBLE
//            error_bank_address.setText(R.string.fabaErrorBankAddress);
//            AppUtils.setBackground(tvBankAddress, R.drawable.input_border_bottom_red)
//            tvBankAddress.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(tvBankAddress, R.drawable.input_border_bottom)
//            error_bank_address.visibility = View.INVISIBLE
//        }
//
//        if (etAccountOwnerName.text.toString().isBlank()) {
//            error_account_owner_name.visibility = View.VISIBLE
//            error_account_owner_name.setText(R.string.fabaErrorAccountOwnerName);
//            AppUtils.setBackground(etAccountOwnerName, R.drawable.input_border_bottom_red)
//            etAccountOwnerName.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(etAccountOwnerName, R.drawable.input_border_bottom)
//            error_account_owner_name.visibility = View.INVISIBLE
//        }
//
//        if (tvAccountOwnerAddress.text.toString().isBlank()) {
//            error_account_owner_address.visibility = View.VISIBLE
//            error_account_owner_address.setText(R.string.fabaErrorAccountOwnerAddress);
//            AppUtils.setBackground(tvAccountOwnerAddress, R.drawable.input_border_bottom_red)
//            tvAccountOwnerAddress.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(tvAccountOwnerAddress, R.drawable.input_border_bottom)
//            error_account_owner_address.visibility = View.INVISIBLE
//        }
//
////        AppUtils.showDialog(context!!)
////        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
////        val call = api.postFinancialAccount(
////            etRef.text.toString(),
////            etBankCash.text.toString(),
////            accountTypeId,
////            currencyId,
////            statusId,
////            accountCountryId,
////            etState.text.toString(),
////            etWeb.text.toString(),
////            etComment.text.toString(),
////            etInitialBalance.text.toString(),
////            dateId,
////            etMinAllowedBalance.text.toString(),
////            etMaxDesiredBalance.text.toString(),
////            etBankName.text.toString(),
////            etBankCode.text.toString(),
////            etAccountNumber.text.toString(),
////            etIBAN.text.toString(),
////            etBicCode.text.toString(),
////            tvBankAddress.text.toString(),
////            etAccountOwnerName.text.toString(),
////            tvAccountOwnerAddress.text.toString(),
////            etAccountingAccount.text.toString()
////        )
////        RetrofitClient.apiCall(call, this, "PostFinancialAccount")
//
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                Constants.BANK_ADDRESS_CODE -> {
//                    bankLatLng =
//                        LatLng(data!!.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))
//
//                    tvBankAddress.text = data.getStringExtra("address")!!
//                }
//                Constants.ACCOUNT_OWNER_ADDRESS_CODE -> {
//                    accountOwnerLatLng =
//                        LatLng(data!!.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))
//
//                    tvAccountOwnerAddress.text = data.getStringExtra("address")!!
//                }
//            }
//        }
//    }
//
//    override fun onSuccess(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        when (tag) {
//            "GetMetaData" -> {
//                handleMetaDataResponse(jsonObject)
//            }
//            "PostFinancialAccount" -> {
//                AppUtils.showToast(activity, jsonObject.getString("message"), true)
//                (parentFragment as FinancialManagementFragment).bankCashTab.callOnClick()
//            }
//        }
//    }
//
//    override fun onError(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, jsonObject.getString("message"), false)
//    }
//
//    override fun onException(message: String?, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, message!!, false)
//    }
//
//
//    private fun handleMetaDataResponse(jsonObject: JSONObject) {
//        val gson = Gson()
//        val model = gson.fromJson(jsonObject.toString(), BankCashMetaDataModel::class.java)
//
//        bankCashMetaData = model.data
//
//        /*Account country spinner*/
//        val countryStrings = ArrayList<String>()
//        for (i in bankCashMetaData.countries.indices) countryStrings.add(
//            bankCashMetaData.countries.get(
//                i
//            ).name
//        )
//
//        spinnerAccountCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
//
//        spinnerAccountCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
//
//        val countryAdapter = ArrayAdapter(
//            context!!,
//            R.layout.dropdown_item, countryStrings
//        )
//        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerAccountCountry.adapter = countryAdapter
//        spinnerAccountCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
//                accountCountryId = bankCashMetaData.countries[adapterView.selectedItemPosition].id
//                AppUtils.hideKeyboard(activity!!)
//            }
//
//            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//        }
//
//        /*Account Type SPinner*/
//        val accountTypeStrings = ArrayList<String>()
//        for (i in bankCashMetaData.account_types.indices) accountTypeStrings.add(bankCashMetaData.account_types[i].type)
//
//        spinnerAccountType.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
//
//        spinnerAccountType.setTitle(resources.getString(R.string.accountTypeSpinnerTitle))
//
//        val accountTypeAdapter = ArrayAdapter(
//            context!!,
//            R.layout.dropdown_item, accountTypeStrings
//        )
//        accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerAccountType.adapter = accountTypeAdapter
//        spinnerAccountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
//                accountTypeId = bankCashMetaData.account_types[adapterView.selectedItemPosition].id
//                AppUtils.hideKeyboard(activity!!)
//            }
//
//            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//        }
//
//        /*Currency Spinner*/
//        val currencyStrings = ArrayList<String>()
//        for (i in bankCashMetaData.currencies.indices) currencyStrings.add(
//            bankCashMetaData.currencies.get(
//                i
//            ).name
//        )
//
//        spinnerCurrency.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
//
//        spinnerCurrency.setTitle(resources.getString(R.string.currencySpinnerTitle))
//
//        val currencyAdapter = ArrayAdapter(
//            context!!,
//            R.layout.dropdown_item, currencyStrings
//        )
//        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerCurrency.adapter = currencyAdapter
//        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
//                currencyId = bankCashMetaData.currencies[adapterView.selectedItemPosition].id
//                AppUtils.hideKeyboard(activity!!)
//            }
//
//            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//        }
//
//        /*Status Spinner*/
//        val statusStrings = ArrayList<String>()
//        for (i in bankCashMetaData.status.indices) statusStrings.add(bankCashMetaData.status[i])
//
//        spinnerStatus.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
//
//        spinnerStatus.setTitle(resources.getString(R.string.statusSpinnerTitle))
//
//        val adapter = ArrayAdapter(
//            context!!,
//            R.layout.dropdown_item, statusStrings
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerStatus.adapter = adapter
//        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
//                statusId = bankCashMetaData.status[adapterView.selectedItemPosition]
//                AppUtils.hideKeyboard(activity!!)
//            }
//
//            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//        }
//
//    }
//
//
//}