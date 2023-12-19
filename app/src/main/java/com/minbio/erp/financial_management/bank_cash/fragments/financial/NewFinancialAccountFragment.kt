package com.minbio.erp.financial_management.bank_cash.fragments.financial

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaData
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashMetaDataModel
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListData
import com.minbio.erp.financial_management.model.JournalCodeAccountData
import com.minbio.erp.financial_management.model.JournalCodeAccountModel
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_add_personalized_group.*
import kotlinx.android.synthetic.main.fragment_new_financial_account.*
import org.json.JSONObject
import java.util.*

class NewFinancialAccountFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var etRef: EditText
    private lateinit var etBankCash: EditText
    private lateinit var etState: EditText
    private lateinit var etWeb: EditText
    private lateinit var etComment: EditText
    private lateinit var etInitialBalance: EditText
    private lateinit var etMinAllowedBalance: EditText
    private lateinit var etMaxDesiredBalance: EditText
    private lateinit var etBankName: EditText
    private lateinit var etBankCode: EditText
    private lateinit var etAccountNumber: EditText
    private lateinit var etIBAN: EditText
    private lateinit var etBicCode: EditText
    private lateinit var etAccountOwnerName: EditText
    private lateinit var tvDate: TextView
    private lateinit var tvBankAddress: TextView
    private lateinit var tvAccountOwnerAddress: TextView
    private lateinit var back: ImageView
    private lateinit var btnCreateAccount: LinearLayout
    private lateinit var headerLayout: LinearLayout
    private lateinit var whenComingFromMain: LinearLayout
    private lateinit var btnDelete: LinearLayout
    private lateinit var btnAddModify: LinearLayout
    private lateinit var btnAddText: TextView

    private lateinit var spinnerAccountType: CustomSearchableSpinner
    private lateinit var spinnerCurrency: CustomSearchableSpinner
    private lateinit var spinnerStatus: CustomSearchableSpinner
    private lateinit var spinnerAccountCountry: CustomSearchableSpinner
    private lateinit var spinnerAccountingAccount: CustomSearchableSpinner
    private lateinit var spinnerAccountingCodeJournal: CustomSearchableSpinner

    private var currencyId = 0
    private var accountCountryId = 0
    private var statusId = ""
    private var accountTypeId = 0
    private var accountingAccountId = 0
    private var journalCodeAccountId = 0


    private var dateId = ""
    private var bankLatLng = LatLng(0.0, 0.0)
    private var accountOwnerLatLng = LatLng(0.0, 0.0)

    private lateinit var bankCashMetaData: BankCashMetaData
    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var journalCodeAccountData: MutableList<JournalCodeAccountData?> = ArrayList()
    private lateinit var api: Api

    private var editAccountData: FinancialAccountListData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_new_financial_account, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        getPareAccountList()

        return v
    }

    private fun initViews() {

        headerLayout = v.findViewById(R.id.headerLayout)
        whenComingFromMain = v.findViewById(R.id.whenComingFromMain)
        etRef = v.findViewById(R.id.etRef)
        etBankCash = v.findViewById(R.id.etBankCash)
        etState = v.findViewById(R.id.etState)
        etWeb = v.findViewById(R.id.etWeb)
        etComment = v.findViewById(R.id.etComment)
        etInitialBalance = v.findViewById(R.id.etInitialBalance)
        etMinAllowedBalance = v.findViewById(R.id.etMinAllowedBalance)
        etMaxDesiredBalance = v.findViewById(R.id.etMaxDesiredBalance)
        etBankName = v.findViewById(R.id.etBankName)
        etBankCode = v.findViewById(R.id.etBankCode)
        etAccountNumber = v.findViewById(R.id.etAccountNumber)
        etIBAN = v.findViewById(R.id.etIBAN)
        etBicCode = v.findViewById(R.id.etBicCode)
        etAccountOwnerName = v.findViewById(R.id.etAccountOwnerName)
        spinnerAccountingAccount = v.findViewById(R.id.spinnerAccountingAccount)
        spinnerAccountingCodeJournal = v.findViewById(R.id.spinnerAccountingCodeJournal)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnAddModify = v.findViewById(R.id.btnAddModify)
        btnAddText = v.findViewById(R.id.btnAddText)

        back = v.findViewById(R.id.back)
        back.setOnClickListener {
            (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
        }


        tvDate = v.findViewById(R.id.tvDate)
        tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val myear = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    tvDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    dateId = "$day-${monthOfYear + 1}-$year"

                },
                myear,
                month,
                day
            )
            datePicker.show()
        }
        tvBankAddress = v.findViewById(R.id.tvBankAddress)
        tvBankAddress.setOnClickListener {
            getAddress(
                Constants.BANK_ADDRESS_CODE,
                bankLatLng,
                tvBankAddress.text.toString()
            )
        }
        tvAccountOwnerAddress = v.findViewById(R.id.tvAccountOwnerAddress)
        tvAccountOwnerAddress.setOnClickListener {
            getAddress(
                Constants.ACCOUNT_OWNER_ADDRESS_CODE,
                accountOwnerLatLng,
                tvAccountOwnerAddress.text.toString()
            )
        }

        spinnerAccountType = v.findViewById(R.id.spinnerAccountType)
        spinnerCurrency = v.findViewById(R.id.spinnerCurrency)
        spinnerStatus = v.findViewById(R.id.spinnerStatus)
        spinnerAccountCountry = v.findViewById(R.id.spinnerAccountCountry)

        btnAddModify.setOnClickListener { validate() }

        btnDelete.setOnClickListener {
            /*TODO implement Delete financatial account api */
        }

        val fromMain = arguments?.getBoolean("fromMain")
        if (fromMain == false) {
            headerLayout.visibility = View.VISIBLE
            whenComingFromMain.visibility = View.GONE

            editAccountData = arguments?.getParcelable("data")

            if (editAccountData == null) {
                btnAddText.text = context!!.resources.getString(R.string.nfaBtnCreateAccount)
                btnDelete.visibility = View.GONE
                btnDelete.isEnabled = false

            } else {
                btnAddText.text = context!!.resources.getString(R.string.nfaBtnModifyAccount)
                btnDelete.visibility = View.GONE
                btnDelete.isEnabled = false
            }

        } else if (fromMain == true) {
            headerLayout.visibility = View.GONE
            whenComingFromMain.visibility = View.VISIBLE
        }

    }

    private fun getPareAccountList() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    private fun getJournalCodeAccounts() {
        val call = api.getJournalCodeAccounts()
        RetrofitClient.apiCall(call, this, "JournalCodeAccounts")
    }


    private fun getFinancialMetaData() {
        val call = api.getBankCashMetaData()
        RetrofitClient.apiCall(call, this, "GetMetaData")
    }

    private fun getAddress(code: Int, latLng: LatLng, address: String) {
        val intent = Intent(context, AddressMapsActivity::class.java)
        val bundle = Bundle()
        bundle.putDouble("lat", latLng.latitude)
        bundle.putDouble("lng", latLng.longitude)
        bundle.putString("address", address)
        intent.putExtras(bundle)
        startActivityForResult(intent, code)
    }

    private fun validate() {
        if (etRef.text.toString().isBlank()) {
            error_ref.visibility = View.VISIBLE
            error_ref.setText(R.string.nfaErrorRef)
            AppUtils.setBackground(etRef, R.drawable.input_border_bottom_red)
            etRef.requestFocus()
            return
        } else {
            AppUtils.setBackground(etRef, R.drawable.input_border_bottom)
            error_ref.visibility = View.INVISIBLE
        }

        if (etBankCash.text.toString().isBlank()) {
            error_bank_cash.visibility = View.VISIBLE
            error_bank_cash.setText(R.string.nfaErrorBankOrCash)
            AppUtils.setBackground(etBankCash, R.drawable.input_border_bottom_red)
            etBankCash.requestFocus()
            return
        } else {
            AppUtils.setBackground(etBankCash, R.drawable.input_border_bottom)
            error_bank_cash.visibility = View.INVISIBLE
        }

        if (etState.text.toString().isBlank()) {
            error_state.visibility = View.VISIBLE
            error_state.setText(R.string.nfaErrorState)
            AppUtils.setBackground(etState, R.drawable.input_border_bottom_red)
            etState.requestFocus()
            return
        } else {
            AppUtils.setBackground(etState, R.drawable.input_border_bottom)
            error_state.visibility = View.INVISIBLE
        }

        if (etWeb.text.toString().isBlank()) {
            error_web.visibility = View.VISIBLE
            error_web.setText(R.string.nfaErrorWeb)
            AppUtils.setBackground(etWeb, R.drawable.input_border_bottom_red)
            etWeb.requestFocus()
            return
        } else {
            AppUtils.setBackground(etWeb, R.drawable.input_border_bottom)
            error_web.visibility = View.INVISIBLE
        }

        if (etComment.text.toString().isBlank()) {
            error_comment.visibility = View.VISIBLE
            error_comment.setText(R.string.nfaErrorComment)
            AppUtils.setBackground(etComment, R.drawable.round_box_stroke_red)
            etComment.requestFocus()
            return
        } else {
            AppUtils.setBackground(etComment, R.drawable.round_box_light_stroke)
            error_comment.visibility = View.INVISIBLE
        }

        if (etInitialBalance.text.toString().isBlank()) {
            error_initial_balance.visibility = View.VISIBLE
            error_initial_balance.setText(R.string.nfaErrorInitialBalance)
            AppUtils.setBackground(etInitialBalance, R.drawable.input_border_bottom_red)
            etInitialBalance.requestFocus()
            return
        } else {
            AppUtils.setBackground(etInitialBalance, R.drawable.input_border_bottom)
            error_initial_balance.visibility = View.INVISIBLE
        }

        if (tvDate.text.toString().isBlank()) {
            error_date.visibility = View.VISIBLE
            error_date.setText(R.string.nfaErrorDate)
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom_red)
            tvDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom)
            error_date.visibility = View.INVISIBLE
        }

        if (etMinAllowedBalance.text.toString().isBlank()) {
            error_min_allowed.visibility = View.VISIBLE
            error_min_allowed.setText(R.string.nfaErrorMinAllowedBalance)
            AppUtils.setBackground(etMinAllowedBalance, R.drawable.input_border_bottom_red)
            etMinAllowedBalance.requestFocus()
            return
        } else {
            AppUtils.setBackground(etMinAllowedBalance, R.drawable.input_border_bottom)
            error_min_allowed.visibility = View.INVISIBLE
        }

        if (etMaxDesiredBalance.text.toString().isBlank()) {
            error_max_desired.visibility = View.VISIBLE
            error_max_desired.setText(R.string.nfaErrorMaxDesiredBalance)
            AppUtils.setBackground(etMaxDesiredBalance, R.drawable.input_border_bottom_red)
            etMaxDesiredBalance.requestFocus()
            return
        } else {
            AppUtils.setBackground(etMaxDesiredBalance, R.drawable.input_border_bottom)
            error_max_desired.visibility = View.INVISIBLE
        }

        if (etBankName.text.toString().isBlank()) {
            error_bank_name.visibility = View.VISIBLE
            error_bank_name.setText(R.string.nfaErrorBankName)
            AppUtils.setBackground(etBankName, R.drawable.input_border_bottom_red)
            etBankName.requestFocus()
            return
        } else {
            AppUtils.setBackground(etBankName, R.drawable.input_border_bottom)
            error_bank_name.visibility = View.INVISIBLE
        }

        if (etBankCode.text.toString().isBlank()) {
            error_bank_code.visibility = View.VISIBLE
            error_bank_code.setText(R.string.nfaErrorBankCode)
            AppUtils.setBackground(etBankCode, R.drawable.input_border_bottom_red)
            etBankCode.requestFocus()
            return
        } else {
            AppUtils.setBackground(etBankCode, R.drawable.input_border_bottom)
            error_bank_code.visibility = View.INVISIBLE
        }

        if (etAccountNumber.text.toString().isBlank()) {
            error_account_number.visibility = View.VISIBLE
            error_account_number.setText(R.string.nfaErrorAccountNumber)
            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom_red)
            etAccountNumber.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom)
            error_account_number.visibility = View.INVISIBLE
        }

        if (etIBAN.text.toString().isBlank()) {
            error_iban.visibility = View.VISIBLE
            error_iban.setText(R.string.nfaErrorIBANAccountNumber)
            AppUtils.setBackground(etIBAN, R.drawable.input_border_bottom_red)
            etIBAN.requestFocus()
            return
        } else {
            AppUtils.setBackground(etIBAN, R.drawable.input_border_bottom)
            error_iban.visibility = View.INVISIBLE
        }

        if (etBicCode.text.toString().isBlank()) {
            error_bic_code.visibility = View.VISIBLE
            error_bic_code.setText(R.string.nfaErrorBicSwiftCide)
            AppUtils.setBackground(etBicCode, R.drawable.input_border_bottom_red)
            etBicCode.requestFocus()
            return
        } else {
            AppUtils.setBackground(etBicCode, R.drawable.input_border_bottom)
            error_bic_code.visibility = View.INVISIBLE
        }

        if (tvBankAddress.text.toString().isBlank()) {
            error_bank_address.visibility = View.VISIBLE
            error_bank_address.setText(R.string.nfaErrorBankAddress)
            AppUtils.setBackground(tvBankAddress, R.drawable.input_border_bottom_red)
            tvBankAddress.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvBankAddress, R.drawable.input_border_bottom)
            error_bank_address.visibility = View.INVISIBLE
        }

        if (etAccountOwnerName.text.toString().isBlank()) {
            error_account_owner_name.visibility = View.VISIBLE
            error_account_owner_name.setText(R.string.nfaErrorAccountOwnerName)
            AppUtils.setBackground(etAccountOwnerName, R.drawable.input_border_bottom_red)
            etAccountOwnerName.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAccountOwnerName, R.drawable.input_border_bottom)
            error_account_owner_name.visibility = View.INVISIBLE
        }

        if (tvAccountOwnerAddress.text.toString().isBlank()) {
            error_account_owner_address.visibility = View.VISIBLE
            error_account_owner_address.setText(R.string.nfaErrorAccountOwnerAddress)
            AppUtils.setBackground(tvAccountOwnerAddress, R.drawable.input_border_bottom_red)
            tvAccountOwnerAddress.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvAccountOwnerAddress, R.drawable.input_border_bottom)
            error_account_owner_address.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = if (editAccountData == null) api.postFinancialAccount(
            etRef.text.toString(),
            etBankCash.text.toString(),
            accountTypeId,
            currencyId,
            statusId,
            accountCountryId,
            etState.text.toString(),
            etWeb.text.toString(),
            etComment.text.toString(),
            etInitialBalance.text.toString(),
            dateId,
            etMinAllowedBalance.text.toString(),
            etMaxDesiredBalance.text.toString(),
            etBankName.text.toString(),
            etBankCode.text.toString(),
            etAccountNumber.text.toString(),
            etIBAN.text.toString(),
            etBicCode.text.toString(),
            tvBankAddress.text.toString(),
            etAccountOwnerName.text.toString(),
            tvAccountOwnerAddress.text.toString(),
            accountingAccountId, journalCodeAccountId, bankLatLng.latitude, bankLatLng.longitude,
            accountOwnerLatLng.latitude, accountOwnerLatLng.longitude
        ) else api.updateFinancialAccount(
            editAccountData?.id!!,
            etRef.text.toString(),
            etBankCash.text.toString(),
            accountTypeId,
            currencyId,
            statusId,
            accountCountryId,
            etState.text.toString(),
            etWeb.text.toString(),
            etComment.text.toString(),
            etInitialBalance.text.toString(),
            dateId,
            etMinAllowedBalance.text.toString(),
            etMaxDesiredBalance.text.toString(),
            etBankName.text.toString(),
            etBankCode.text.toString(),
            etAccountNumber.text.toString(),
            etIBAN.text.toString(),
            etBicCode.text.toString(),
            tvBankAddress.text.toString(),
            etAccountOwnerName.text.toString(),
            tvAccountOwnerAddress.text.toString(),
            accountingAccountId, journalCodeAccountId, bankLatLng.latitude, bankLatLng.longitude,
            accountOwnerLatLng.latitude, accountOwnerLatLng.longitude
        )

        RetrofitClient.apiCall(call, this, "PostFinancialAccount")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.BANK_ADDRESS_CODE -> {
                    bankLatLng =
                        LatLng(data!!.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                    tvBankAddress.text = data.getStringExtra("address")!!
                }
                Constants.ACCOUNT_OWNER_ADDRESS_CODE -> {
                    accountOwnerLatLng =
                        LatLng(data!!.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                    tvAccountOwnerAddress.text = data.getStringExtra("address")!!
                }
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetMetaData" -> {
                AppUtils.dismissDialog()
                handleMetaDataResponse(jsonObject)
            }
            "PostFinancialAccount" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
//                parentAccountList.add(ParentAccountsData("", 0, ""))
                parentAccountList.addAll(model.data)

                setUpParentAccountSpinner()
                getJournalCodeAccounts()
            }
            "JournalCodeAccounts" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), JournalCodeAccountModel::class.java)
                journalCodeAccountData.add(JournalCodeAccountData("", 0, "", 0))
                journalCodeAccountData.addAll(model.data)

                setUpJournalCodeAccountSpinner()
                getFinancialMetaData()
            }
        }
    }


    private fun setUpParentAccountSpinner() {
        val strings = ArrayList<String>()
        for (i in parentAccountList.indices)
            strings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerAccountingAccount.adapter = positionAdapter
        spinnerAccountingAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerAccountingAccount.setTitle(context!!.resources.getString(R.string.accountingCodeAccountSpinnerTitle))
        spinnerAccountingAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    accountingAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpJournalCodeAccountSpinner() {

        val journalCodeStrings = ArrayList<String>()
        for (i in journalCodeAccountData.indices) {
            if (i > 0)
                journalCodeStrings.add(journalCodeAccountData[i]?.code!! + " - " + journalCodeAccountData[i]?.label)
            else
                journalCodeStrings.add(journalCodeAccountData[i]?.code!! + journalCodeAccountData[i]?.label)
        }

        val journalCodeAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, journalCodeStrings)
        spinnerAccountingCodeJournal.adapter = journalCodeAdapter
        spinnerAccountingCodeJournal.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerAccountingCodeJournal.setTitle(context!!.resources.getString(R.string.journalCodeAccountSpinnerTitle))
        spinnerAccountingCodeJournal.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    journalCodeAccountId = journalCodeAccountData[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
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

        /*Account country spinner*/
        val countryStrings = ArrayList<String>()
        for (i in bankCashMetaData.countries.indices)
            countryStrings.add(bankCashMetaData.countries[i].name)

        spinnerAccountCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerAccountCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))

        val countryAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, countryStrings
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountCountry.adapter = countryAdapter
        spinnerAccountCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                accountCountryId = bankCashMetaData.countries[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Account Type Spinner*/
        val accountTypeStrings = ArrayList<String>()
        for (i in bankCashMetaData.account_types.indices) accountTypeStrings.add(bankCashMetaData.account_types[i].type)

        spinnerAccountType.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerAccountType.setTitle(resources.getString(R.string.accountTypeSpinnerTitle))

        val accountTypeAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, accountTypeStrings
        )
        accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = accountTypeAdapter
        spinnerAccountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                accountTypeId = bankCashMetaData.account_types[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Currency Spinner*/
        val currencyStrings = ArrayList<String>()
        for (i in bankCashMetaData.currencies.indices) currencyStrings.add(
            bankCashMetaData.currencies.get(
                i
            ).name
        )

        spinnerCurrency.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerCurrency.setTitle(resources.getString(R.string.currencySpinnerTitle))

        val currencyAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, currencyStrings
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = currencyAdapter
        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                currencyId = bankCashMetaData.currencies[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Status Spinner*/
        val statusStrings = ArrayList<String>()
        for (i in bankCashMetaData.status.indices) statusStrings.add(bankCashMetaData.status[i])

        spinnerStatus.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerStatus.setTitle(resources.getString(R.string.statusSpinnerTitle))

        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, statusStrings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                statusId = bankCashMetaData.status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        if (editAccountData != null)
            setupData()


    }

    private fun setupData() {
        etRef.setText(editAccountData?.reference)
        etBankCash.setText(editAccountData?.label)
        etState.setText(editAccountData?.state)
        etWeb.setText(editAccountData?.web)
        etComment.setText(editAccountData?.comment)
        etInitialBalance.setText(editAccountData?.initial_balance)
        etMinAllowedBalance.setText(editAccountData?.minimum_allowed_balance)
        etMaxDesiredBalance.setText(editAccountData?.minimum_desired_balance)
        etBankName.setText(editAccountData?.bank_name)
        etBankCode.setText(editAccountData?.bank_code)
        etAccountNumber.setText(editAccountData?.account_no)
        etIBAN.setText(editAccountData?.iban)
        etBicCode.setText(editAccountData?.bic_swift_code)
        etAccountOwnerName.setText(editAccountData?.account_holder)
        tvBankAddress.text = editAccountData?.bank_address
        tvDate.text = editAccountData?.date
        tvAccountOwnerAddress.text = editAccountData?.account_holder_address
        dateId = editAccountData?.date!!

        bankLatLng =
            LatLng(editAccountData?.bank_lat?.toDouble()!!, editAccountData?.bank_lng?.toDouble()!!)

        accountOwnerLatLng =
            LatLng(editAccountData?.owner_lat?.toDouble()!!, editAccountData?.owner_lng?.toDouble()!!)

        for (i in parentAccountList.indices)
            if (parentAccountList[i]?.id == editAccountData?.chart_account_id)
                spinnerAccountingAccount.setSelection(i)

        for (i in journalCodeAccountData.indices)
            if (journalCodeAccountData[i]?.id == editAccountData?.accounting_journal_id)
                spinnerAccountingCodeJournal.setSelection(i)

        for (i in bankCashMetaData.status.indices)
            if (bankCashMetaData.status[i] == editAccountData?.status)
                spinnerStatus.setSelection(i)

        for (i in bankCashMetaData.currencies.indices)
            if (bankCashMetaData.currencies[i].id == editAccountData?.currency_id)
                spinnerCurrency.setSelection(i)

        for (i in bankCashMetaData.countries.indices)
            if (bankCashMetaData.countries[i].id == editAccountData?.country_id)
                spinnerAccountCountry.setSelection(i)

    }


}