package com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.models.ExpenseReportAccountsData
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_accounting_add_expense_report_account.*
import org.json.JSONObject

class AddExpenseReportAccountFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var back: ImageView
    private lateinit var btnAdd: LinearLayout
    private lateinit var api: Api

    private lateinit var etCode: EditText
    private lateinit var etLabel: EditText
    private lateinit var btnText: TextView
    private lateinit var spinnerAccountingCode: CustomSearchableSpinner

    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var accountingCodeId = 0
    private var expenseReportAccountsData: ExpenseReportAccountsData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_add_expense_report_account,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        getPareAccountList()

        return v
    }

    private fun initViews() {
        expenseReportAccountsData = arguments?.getParcelable("data")

        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }

        btnText = v.findViewById(R.id.btnText)
        etCode = v.findViewById(R.id.etCode)
        etLabel = v.findViewById(R.id.etLabel)
        spinnerAccountingCode = v.findViewById(R.id.spinnerAccountingCode)
        btnAdd = v.findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener { validate() }


    }

    private fun getPareAccountList() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
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
        val call = if (expenseReportAccountsData == null)
            api.postExpenseReportAccount(
                etCode.text.toString(), etLabel.text.toString(), accountingCodeId
            )
        else api.updateExpenseReportAccount(
            etCode.text.toString(), etLabel.text.toString(),
            accountingCodeId, expenseReportAccountsData?.id!!
        )

        RetrofitClient.apiCall(call, this, "PostExpenseReportAccount")


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
            "PostExpenseReportAccount" -> {

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
        spinnerAccountingCode.adapter = positionAdapter
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


        if (expenseReportAccountsData != null) {

            etCode.setText(expenseReportAccountsData?.code)
            etLabel.setText(expenseReportAccountsData?.label)

            for (i in parentAccountList.indices)
                if (parentAccountList[i]?.id == expenseReportAccountsData?.chart_account_id)
                    spinnerAccountingCode.setSelection(i)

            btnText.text = context!!.resources.getString(R.string.faerBtnModify)
        } else
            btnText.text = context!!.resources.getString(R.string.faerBtnAdd)


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