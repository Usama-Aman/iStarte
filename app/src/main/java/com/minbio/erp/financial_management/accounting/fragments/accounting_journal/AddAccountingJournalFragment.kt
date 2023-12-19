package com.minbio.erp.financial_management.accounting.fragments.accounting_journal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models.AccountingJournalData
import com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models.JournalNatureModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject

class AddAccountingJournalFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var back: ImageView

    private lateinit var etJournalCode: EditText
    private lateinit var etJournalLabel: EditText
    private lateinit var spinnerJournalNature: CustomSearchableSpinner
    private lateinit var errorJournalCode: TextView
    private lateinit var errorJournalLabel: TextView
    private lateinit var btnText: TextView
    private lateinit var btnAddModify: LinearLayout

    private var journalData: AccountingJournalData? = null
    private var journalNatureId = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_add_journal, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        getJournalNatures()
        return v
    }

    private fun initViews() {
        journalData = arguments?.getParcelable("data")

        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }

        btnText = v.findViewById(R.id.btnText)
        etJournalCode = v.findViewById(R.id.etJournalCode)
        etJournalLabel = v.findViewById(R.id.etJournalLabel)
        spinnerJournalNature = v.findViewById(R.id.spinnerJournalNature)
        errorJournalCode = v.findViewById(R.id.errorJournalCode)
        errorJournalLabel = v.findViewById(R.id.errorJournalLabel)

        btnAddModify = v.findViewById(R.id.btnAdd)
        btnAddModify.setOnClickListener {
            validate()
        }


    }

    private fun validate() {

        if (etJournalCode.text.toString().isBlank()) {
            errorJournalCode.visibility = View.VISIBLE
            AppUtils.setBackground(etJournalCode, R.drawable.input_border_bottom_red)
            etJournalCode.requestFocus()
            return
        } else {
            AppUtils.setBackground(etJournalCode, R.drawable.input_border_bottom)
            errorJournalCode.visibility = View.INVISIBLE
        }

        if (etJournalLabel.text.toString().isBlank()) {
            errorJournalLabel.visibility = View.VISIBLE
            AppUtils.setBackground(etJournalLabel, R.drawable.input_border_bottom_red)
            etJournalLabel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etJournalLabel, R.drawable.input_border_bottom)
            errorJournalLabel.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call =
            if (journalData == null)
                api.postAccountingJournal(
                    etJournalCode.text.toString(),
                    etJournalLabel.text.toString(),
                    journalNatureId
                )
            else
                api.updateAccountingJournal(
                    etJournalCode.text.toString(),
                    etJournalLabel.text.toString(),
                    journalNatureId,
                    journalData?.id!!
                )

        RetrofitClient.apiCall(call, this, "PostJournal")
    }

    private fun getJournalNatures() {
        AppUtils.showDialog(context!!)
        val call = api.getJournalNatures()
        RetrofitClient.apiCall(call, this, "GetJournalNatures")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetJournalNatures" -> {
                setUpSpinner(Gson().fromJson(jsonObject.toString(), JournalNatureModel::class.java))
            }
            "PostJournal" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()

                val intent = Intent("accountingListRefresh")
                context!!.sendBroadcast(intent)
            }
        }
    }

    private fun setUpSpinner(journalNatureModel: JournalNatureModel) {
        val strings = ArrayList<String>()
        for (i in journalNatureModel.data) {
            strings.add(i.nature)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerJournalNature.adapter = positionAdapter
        spinnerJournalNature.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerJournalNature.setTitle(resources.getString(R.string.natureSpinnerTitle))
        spinnerJournalNature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                journalNatureId = journalNatureModel.data[position].id
                AppUtils.hideKeyboard(activity!!)
            }
        }

        if (journalData != null) {
            etJournalCode.setText(journalData?.code)
            etJournalLabel.setText(journalData?.label)

            for (i in journalNatureModel.data.indices)
                if (journalData?.journal_nature_id == journalNatureModel.data[i].id)
                    spinnerJournalNature.setSelection(i)

            btnText.text = context!!.resources.getString(R.string.fajBtnModify)
        } else
            btnText.text = context!!.resources.getString(R.string.fajBtnAdd)

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