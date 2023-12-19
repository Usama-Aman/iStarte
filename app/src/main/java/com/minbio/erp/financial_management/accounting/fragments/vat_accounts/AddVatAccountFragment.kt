package com.minbio.erp.financial_management.accounting.fragments.vat_accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.models.VatAccountsData
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_accounting_add_vat_account.*
import org.json.JSONObject

class AddVatAccountFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var back: ImageView
    private lateinit var btnAdd: LinearLayout
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var spinnerTax2: CustomSearchableSpinner
    private lateinit var spinnerTax3: CustomSearchableSpinner
    private lateinit var spinnerNPR: CustomSearchableSpinner
    private lateinit var spinnerSalesAccount: CustomSearchableSpinner
    private lateinit var spinnerPurchaseAccount: CustomSearchableSpinner
    private lateinit var etCode: EditText
    private lateinit var etRate: EditText
    private lateinit var etRate2: EditText
    private lateinit var etRate3: EditText
    private lateinit var etNote: EditText
    private lateinit var btnText: TextView

    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private var countryId: Int = 0
    private var tax2Id: Int = 0
    private var tax3Id: Int = 0
    private var nprId: Int = 0
    private var saleAccountId: Int = 0
    private var purchaseAccountId: Int = 0

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var vatAccountData: VatAccountsData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_add_vat_account,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        getAllAccounts()

        return v
    }

    private fun initViews() {
        vatAccountData = arguments?.getParcelable("data")

        btnText = v.findViewById(R.id.btnText)
        back = v.findViewById(R.id.back)
        back.setOnClickListener {
            (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
        }

        spinnerCountry = v.findViewById(R.id.spinnerCountry)
        spinnerTax2 = v.findViewById(R.id.spinnerTax2)
        spinnerTax3 = v.findViewById(R.id.spinnerTax3)
        spinnerNPR = v.findViewById(R.id.spinnerNPR)
        spinnerSalesAccount = v.findViewById(R.id.spinnerSaleAccount)
        spinnerPurchaseAccount = v.findViewById(R.id.spinnerPurchaseAccount)
        etCode = v.findViewById(R.id.etCode)
        etRate = v.findViewById(R.id.etRate)
        etRate2 = v.findViewById(R.id.etRate2)
        etRate3 = v.findViewById(R.id.etRate3)
        etNote = v.findViewById(R.id.etNote)



        btnAdd = v.findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            validate()
        }
    }

    private fun getAllAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    private fun setUpSpinners() {
        countriesList.clear()
        countriesList.addAll((activity as MainActivity).countriesList().data)

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
                    countryId = countriesList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }


        //Status Spinner
        val statusItems = arrayOf("Yes", "No")
        val tax2Adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        spinnerTax2.adapter = tax2Adapter
        spinnerTax2.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerTax2.setTitle(resources.getString(R.string.taxSpinnerTitle))
        spinnerTax2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    tax2Id = 1
                else if (position == 1)
                    tax2Id = 0
            }
        }

        val tax3Adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        spinnerTax3.adapter = tax3Adapter
        spinnerTax3.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerTax3.setTitle(resources.getString(R.string.taxSpinnerTitle))
        spinnerTax3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    tax3Id = 1
                else if (position == 1)
                    tax3Id = 0
            }
        }

        val nprAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        spinnerNPR.adapter = nprAdapter
        spinnerNPR.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerNPR.setTitle(resources.getString(R.string.statusSpinnerTitle))
        spinnerNPR.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    nprId = 1
                else if (position == 1)
                    nprId = 0
            }
        }

        if (vatAccountData != null) {

            etCode.setText(vatAccountData?.code)
            etRate.setText(vatAccountData?.rate)
            etRate2.setText(vatAccountData?.rate_2)
            etRate3.setText(vatAccountData?.rate_3)
            etNote.setText(vatAccountData?.note)

            for (i in countriesList.indices)
                if (countriesList[i]?.id == vatAccountData?.country_id)
                    spinnerCountry.setSelection(i)

            for (i in parentAccountList.indices)
                if (parentAccountList[i]?.id == vatAccountData?.sale_chart_account_id)
                    spinnerSalesAccount.setSelection(i)

            for (i in parentAccountList.indices)
                if (parentAccountList[i]?.id == vatAccountData?.purchase_chart_account_id)
                    spinnerPurchaseAccount.setSelection(i)

            if (vatAccountData?.include_tax_2!! == 1)
                spinnerTax2.setSelection(0)
            else
                spinnerTax2.setSelection(1)

            if (vatAccountData?.include_tax_3!! == 1)
                spinnerTax3.setSelection(0)
            else
                spinnerTax3.setSelection(1)

            if (vatAccountData?.npr!! == 1)
                spinnerNPR.setSelection(0)
            else
                spinnerNPR.setSelection(1)

            btnText.text = context!!.resources.getString(R.string.favaBtnModify)
        } else
            btnText.text = context!!.resources.getString(R.string.favaBtnAdd)


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

        if (etRate.text.toString().isBlank()) {
            errorRate.visibility = View.VISIBLE
            AppUtils.setBackground(etRate, R.drawable.input_border_bottom_red)
            etRate.requestFocus()
            return
        } else {
            AppUtils.setBackground(etRate, R.drawable.input_border_bottom)
            errorRate.visibility = View.INVISIBLE
        }

        if (etRate2.text.toString().isBlank()) {
            errorRate2.visibility = View.VISIBLE
            AppUtils.setBackground(etRate2, R.drawable.input_border_bottom_red)
            etRate2.requestFocus()
            return
        } else {
            AppUtils.setBackground(etRate2, R.drawable.input_border_bottom)
            errorRate2.visibility = View.INVISIBLE
        }

        if (etRate3.text.toString().isBlank()) {
            errorRate3.visibility = View.VISIBLE
            AppUtils.setBackground(etRate3, R.drawable.input_border_bottom_red)
            etRate3.requestFocus()
            return
        } else {
            AppUtils.setBackground(etRate3, R.drawable.input_border_bottom)
            errorRate3.visibility = View.INVISIBLE
        }

        if (etNote.text.toString().isBlank()) {
            errorNote.visibility = View.VISIBLE
            AppUtils.setBackground(etNote, R.drawable.round_box_stroke_red)
            etNote.requestFocus()
            return
        } else {
            AppUtils.setBackground(etNote, R.drawable.round_box_light_stroke)
            errorNote.visibility = View.INVISIBLE
        }


            AppUtils.showDialog(context!!)
            val call = if (vatAccountData == null)
                api.postVatAccount(
                    countryId, etCode.text.toString(), etRate.text.toString(), tax2Id,
                    etRate2.text.toString(), tax3Id, etRate3.text.toString(), nprId, saleAccountId,
                    purchaseAccountId, etNote.text.toString()
                )
            else
                api.updateVatAccount(
                    countryId, etCode.text.toString(), etRate.text.toString(), tax2Id,
                    etRate2.text.toString(), tax3Id, etRate3.text.toString(), nprId, saleAccountId,
                    purchaseAccountId, etNote.text.toString(), vatAccountData?.id!!
                )

            RetrofitClient.apiCall(call, this, "PostVatAccount")

    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
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
            }
            "PostVatAccount" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
        }
    }

    private fun setUpParentAccountSpinner() {

        val saleStrings = ArrayList<String>()
        for (i in parentAccountList.indices) {
            if (i > 0)
                saleStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                saleStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }

        val saleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, saleStrings)
        spinnerSalesAccount.adapter = saleAdapter
        spinnerSalesAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerSalesAccount.setTitle(context!!.resources.getString(R.string.saleAccountSpinnerTitle))
        spinnerSalesAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    saleAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }


        val purchaseStrings = ArrayList<String>()
        for (i in parentAccountList.indices) {
            if (i > 0)
                purchaseStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
            else
                purchaseStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
        }

        val purchaseAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, purchaseStrings)
        spinnerPurchaseAccount.adapter = purchaseAdapter
        spinnerPurchaseAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerPurchaseAccount.setTitle(context!!.resources.getString(R.string.purchaseAccountSpinnerTitle))
        spinnerPurchaseAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    purchaseAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

        setUpSpinners()

    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }


}