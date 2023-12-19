package com.minbio.erp.financial_management.accounting.fragments.chart_accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models.*
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_add_chart_accounts.*
import org.json.JSONObject

class AddChartAccountFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var api: Api
    private lateinit var tvChartOfAccounts: TextView
    private lateinit var btnText: TextView

    private lateinit var etAccountNumber: EditText
    private lateinit var etLabel: EditText
    private lateinit var etShortLabel: EditText
    private lateinit var etGroupOfAccount: EditText
    private lateinit var spinnerParentAccount: CustomSearchableSpinner
    private lateinit var spinnerPersonalizedGroup: CustomSearchableSpinner
    private lateinit var back: ImageView
    private lateinit var btnSave: LinearLayout /*text Modify if edit*/
    private lateinit var btnDelete: LinearLayout
    private var chartAccountData: ChartAccountData? = null

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var personalizedGroupList: MutableList<PersonalizedGroupData?> = ArrayList()

    private var parentAccountId: Int = 0
    private var personalizedGroupId: Int = 0
    private var chartAccountModelId: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_add_chart_accounts, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        getPareAccountList()

        return v
    }

    private fun initViews() {


        tvChartOfAccounts = v.findViewById(R.id.tvChartOfAccounts)
        etAccountNumber = v.findViewById(R.id.etAccountNumber)
        etLabel = v.findViewById(R.id.etLabel)
        etShortLabel = v.findViewById(R.id.etShortLabel)
        etGroupOfAccount = v.findViewById(R.id.etGroupOfAccount)
        spinnerParentAccount = v.findViewById(R.id.spinnerParentAccount)
        spinnerPersonalizedGroup = v.findViewById(R.id.spinnerPersonalizedGroup)
        back = v.findViewById(R.id.back)
        btnSave = v.findViewById(R.id.btnSave)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnText = v.findViewById(R.id.btnText)

        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }
        btnSave.setOnClickListener {
            validate()
        }

        chartAccountData = arguments?.getParcelable("data")
        tvChartOfAccounts.text = arguments?.getString("chartAccountModel")
        chartAccountModelId = arguments?.getInt("chartAccountModelId")
    }

    private fun getPareAccountList() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    private fun getPersonalizedGroupAccounts() {
        val call = api.getPersonalizedGroupList()
        RetrofitClient.apiCall(call, this, "PersonalizedGroupList")
    }

    private fun validate() {
        if (etAccountNumber.text.toString().isBlank()) {
            errorAccountNumber.visibility = View.VISIBLE
            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom_red)
            etAccountNumber.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAccountNumber, R.drawable.input_border_bottom)
            errorAccountNumber.visibility = View.INVISIBLE
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

        if (etShortLabel.text.toString().isBlank()) {
            errorShortLabel.visibility = View.VISIBLE
            AppUtils.setBackground(etShortLabel, R.drawable.input_border_bottom_red)
            etShortLabel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etShortLabel, R.drawable.input_border_bottom)
            errorShortLabel.visibility = View.INVISIBLE
        }

        if (etGroupOfAccount.text.toString().isBlank()) {
            errorGroupOfAccount.visibility = View.VISIBLE
            AppUtils.setBackground(etGroupOfAccount, R.drawable.input_border_bottom_red)
            etGroupOfAccount.requestFocus()
            return
        } else {
            AppUtils.setBackground(etGroupOfAccount, R.drawable.input_border_bottom)
            errorGroupOfAccount.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = if (chartAccountData == null)
            api.postChartAccount(
                chartAccountModelId!!,
                etAccountNumber.text.toString(),
                etLabel.text.toString(),
                etShortLabel.text.toString(),
                parentAccountId, personalizedGroupId, etGroupOfAccount.text.toString()
            )
        else api.updateChartAccount(

            chartAccountModelId!!,
            etAccountNumber.text.toString(),
            etLabel.text.toString(),
            etShortLabel.text.toString(),
            parentAccountId, personalizedGroupId, etGroupOfAccount.text.toString(),
            chartAccountData?.id!!
        )


        RetrofitClient.apiCall(call, this, "PostChartAccount")

    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
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
            "PersonalizedGroupList" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), PersonalizedGroupModel::class.java)
                personalizedGroupList.add(PersonalizedGroupData("", 0, ""))
                personalizedGroupList.addAll(model.data)

                setUpPersonalizedGroupsData()
            }
            "PostChartAccount" -> {
                AppUtils.dismissDialog()

                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
        }
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

        getPersonalizedGroupAccounts()

    }

    private fun setUpPersonalizedGroupsData() {
        val strings = ArrayList<String>()
        for (i in personalizedGroupList.indices) {
            if (i > 0)
                strings.add(personalizedGroupList[i]?.code + " - " + personalizedGroupList[i]?.label!!)
            else
                strings.add(personalizedGroupList[i]?.code + personalizedGroupList[i]?.label!!)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerPersonalizedGroup.adapter = positionAdapter
        spinnerPersonalizedGroup.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerPersonalizedGroup.setTitle(context!!.resources.getString(R.string.personalizedGroupAccountSpinnerTitle))
        spinnerPersonalizedGroup.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    personalizedGroupId = personalizedGroupList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }


        if (chartAccountData != null) {

            etAccountNumber.setText(chartAccountData?.account_number)
            etLabel.setText(chartAccountData?.label)
            etShortLabel.setText(chartAccountData?.short_label)
            etGroupOfAccount.setText(chartAccountData?.account_group)

            for (i in parentAccountList.indices)
                if (parentAccountList[i]?.id == chartAccountData?.parent_id)
                    spinnerParentAccount.setSelection(i)

            for (i in personalizedGroupList.indices)
                if (personalizedGroupList[i]?.id == chartAccountData?.personalized_group_id)
                    spinnerPersonalizedGroup.setSelection(i)

            btnText.text = context!!.resources.getString(R.string.facaBtnModify)
        } else
            btnText.text = context!!.resources.getString(R.string.facaBtnADD)

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