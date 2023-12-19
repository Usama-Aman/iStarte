package com.minbio.erp.financial_management.accounting.fragments.tax_accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.models.TaxAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_accounting_add_tax_accounts.*
import org.json.JSONObject
import java.util.ArrayList

class AddTaxAccountFragment : Fragment(), ResponseCallBack {

    private var deductibleStrings = arrayOf("Yes", "No")

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var back: ImageView
    private lateinit var btnAdd: LinearLayout
    private lateinit var btnText: TextView

    private lateinit var etCode: EditText
    private lateinit var etLabel: EditText
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var spinnerAccountingCode: CustomSearchableSpinner
    private lateinit var spinnerDeductible: CustomSearchableSpinner
    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private lateinit var countriesModel: CountriesModel

    private var taxAccountsData: TaxAccountsData? = null
    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()

    private var countryId = 0
    private var deductibleId = 0
    private var accountingCodeId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_add_tax_accounts,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        setUpCountrySpinner()

        getParentAccounts()

        return v
    }

    private fun initViews() {
        taxAccountsData = arguments?.getParcelable("data")
        etCode = v.findViewById(R.id.etCode)
        etLabel = v.findViewById(R.id.etLabel)
        spinnerCountry = v.findViewById(R.id.spinnerCountry)
        spinnerAccountingCode = v.findViewById(R.id.spinnerAccountingCode)
        spinnerDeductible = v.findViewById(R.id.spinnerDeductible)
        btnAdd = v.findViewById(R.id.btnAdd)
        back = v.findViewById(R.id.back)
        btnText = v.findViewById(R.id.btnText)

        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }
        btnAdd.setOnClickListener { validate() }


        if (taxAccountsData != null)
            btnText.text = context!!.resources.getString(R.string.fataBtnModify)
        else
            btnText.text = context!!.resources.getString(R.string.fataBtnAdd)

    }

    private fun setUpCountrySpinner() {
        countriesList.clear()
        countriesModel = (activity as MainActivity).countriesList()

        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(countriesModel.data)

        val strings = ArrayList<String>()
        for (i in countriesList) {
            strings.add(i!!.name)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerCountry.adapter = positionAdapter
        spinnerCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
        spinnerCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countryId = countriesList[parent?.selectedItemPosition!!]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

        val deductibleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, deductibleStrings)
        spinnerDeductible.adapter = deductibleAdapter
        spinnerDeductible.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerDeductible.setTitle(resources.getString(R.string.statusSpinnerTitle))
        spinnerDeductible.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    deductibleId = if (parent?.selectedItemPosition!! == 0) 1 else 0
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpParentAccountSpinner() {

        val parentAccountStrings = ArrayList<String>()
        for (i in parentAccountList.indices) {
            if (i > 0)
                parentAccountStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                parentAccountStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }

        val saleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, parentAccountStrings)
        spinnerAccountingCode.adapter = saleAdapter
        spinnerAccountingCode.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerAccountingCode.setTitle(context!!.resources.getString(R.string.accountingCodeAccountSpinnerTitle))
        spinnerAccountingCode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    accountingCodeId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }


    private fun validate() {

        if (etCode.text.toString().isBlank()) {
            errorCode.visibility = View.VISIBLE
            AppUtils.setBackground(etCode, R.drawable.input_border_bottom_red)
            etCode.requestFocus()
            return
        } else {
            AppUtils.setBackground(etCode, R.drawable.input_border_bottom)
            errorCode.visibility = View.INVISIBLE
        }

        if (etLabel.text.toString().isBlank()) {
            errorLabel.visibility = View.VISIBLE
            AppUtils.setBackground(etLabel, R.drawable.input_border_bottom_red)
            etLabel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etLabel, R.drawable.input_border_bottom)
            errorLabel.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = if (taxAccountsData == null)
            api.postTaxAccount(
                etCode.text.toString(), etLabel.text.toString(), countryId,
                accountingCodeId, deductibleId
            )
        else
            api.updateTaxAccount(
                taxAccountsData?.id!!, etCode.text.toString(), etLabel.text.toString(), countryId,
                accountingCodeId, deductibleId
            )

        RetrofitClient.apiCall(call, this, "PostTaxAccount")

    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "ParentAccountList" -> {
                parentAccountList.clear()
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

                if (taxAccountsData != null) {
                    updateViews()
                }
            }
            "PostTaxAccount" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
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

    private fun updateViews() {
        etCode.setText(taxAccountsData?.code)
        etLabel.setText(taxAccountsData?.label)

        for (i in countriesList.indices)
            if (countriesList[i]?.id == taxAccountsData?.country_id)
                spinnerCountry.setSelection(i)

        for (i in parentAccountList.indices)
            if (parentAccountList[i]?.id == taxAccountsData?.chart_account_id)
                spinnerAccountingCode.setSelection(i)

        if (taxAccountsData?.deductible == 0)
            spinnerDeductible.setSelection(1)
        else
            spinnerDeductible.setSelection(0)

    }

}