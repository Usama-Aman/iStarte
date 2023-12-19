package com.minbio.erp.financial_management.accounting.fragments.personalized_group

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.customer_management.adapter.MultiSelectSearchSpinner
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedAccountData
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedAccountModel
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedGroupData
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_add_personalized_group.*
import org.json.JSONObject

class AddPersonalizedGroupFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var back: ImageView
    private lateinit var etCode: EditText
    private lateinit var etLabel: EditText
    private lateinit var etComment: EditText
    private lateinit var etFormula: EditText
    private lateinit var etPosition: EditText
    private lateinit var spinnerCalculated: CustomSearchableSpinner
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var btnAdd: LinearLayout
    private lateinit var btnText: TextView
    private lateinit var multiSpinnerAccountText: TextView
    private lateinit var accountConstraint: ConstraintLayout

    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private lateinit var countriesModel: CountriesModel
    private var countryId: Int = 0
    private var calculatedId: Int = 0

    private var personalizedGroupData: PersonalizedGroupData? = null
    var parentAccountList: java.util.ArrayList<PersonalizedAccountData> = ArrayList()
    private var personalizedAccountsId = ""
    private val itemsStr = ArrayList<String?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_add_personalized_group, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        getParentAccounts()

        return v
    }

    private fun initViews() {
        personalizedGroupData = arguments?.getParcelable("data")

        back = v.findViewById(R.id.back)
        etCode = v.findViewById(R.id.etCode)
        etLabel = v.findViewById(R.id.etLabel)
        etComment = v.findViewById(R.id.etComment)
        etFormula = v.findViewById(R.id.etFormula)
        etPosition = v.findViewById(R.id.etPosition)
        spinnerCalculated = v.findViewById(R.id.spinnerCalculated)
        spinnerCountry = v.findViewById(R.id.spinnerCountry)
        btnText = v.findViewById(R.id.btnText)
        btnAdd = v.findViewById(R.id.btnAdd)
        accountConstraint = v.findViewById(R.id.accountConstraint)
        multiSpinnerAccountText = v.findViewById(R.id.multiSpinnerAccountText)

        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }
        btnAdd.setOnClickListener { validate() }
    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    private fun setUpSpinners() {
        countriesList.clear()
        countriesModel = (activity as MainActivity).countriesList()
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
                    countryId = countriesList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }


        //Status Spinner
        val statusItems = arrayOf("Yes", "No")
        val spinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        spinnerCalculated.adapter = spinnerAdapter
        spinnerCalculated.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerCalculated.setTitle(resources.getString(R.string.statusSpinnerTitle))
        spinnerCalculated.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    calculatedId = 1
                else if (position == 1)
                    calculatedId = 0
            }
        }

        accountConstraint.setOnClickListener {

            val multiSearchableSpinner = MultiAccountSpinner(context!!, parentAccountList)
            multiSearchableSpinner.setOnworkflowlistclicklistener { results ->
                personalizedAccountsId = TextUtils.join(",", results)

                itemsStr.clear()
                for (i in parentAccountList.indices) {
                    for (j in results.indices) {
                        if (results[j] == parentAccountList[i].id) {
                            itemsStr.add(parentAccountList[i].label)
                        }
                    }
                }
                multiSpinnerAccountText.text = TextUtils.join(",", itemsStr)

            }
            multiSearchableSpinner.show()
        }

        if (personalizedGroupData != null) {

            etCode.setText(personalizedGroupData?.code)
            etComment.setText(personalizedGroupData?.comment)
            etLabel.setText(personalizedGroupData?.label)
            etFormula.setText(personalizedGroupData?.formula)
            etPosition.setText(personalizedGroupData?.position.toString())

            if (personalizedGroupData?.calculated!! == 0)
                spinnerCalculated.setSelection(1)
            else
                spinnerCalculated.setSelection(0)


            for (i in countriesList.indices)
                if (personalizedGroupData?.country_id == countriesList[i]?.id)
                    spinnerCountry.setSelection(i)

            for (i in personalizedGroupData?.chart_account?.indices!!)
                inner@ for (j in parentAccountList.indices)
                    if (personalizedGroupData?.chart_account!![i].id == parentAccountList[j].id) {
                        parentAccountList[j].isChecked = true
                        itemsStr.add(parentAccountList[j].label)
                        break@inner
                    }

            multiSpinnerAccountText.text = TextUtils.join(",", itemsStr)

            btnText.text = context!!.resources.getString(R.string.fapgBtnModify)


        } else
            btnText.text = context!!.resources.getString(R.string.fapgBtnAdd)

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

        if (etComment.text.toString().isBlank()) {
            errorComment.visibility = View.VISIBLE
            AppUtils.setBackground(etComment, R.drawable.input_border_bottom_red)
            etComment.requestFocus()
            return
        } else {
            AppUtils.setBackground(etComment, R.drawable.input_border_bottom)
            errorComment.visibility = View.INVISIBLE
        }

        if (etFormula.text.toString().isBlank()) {
            errorFormula.visibility = View.VISIBLE
            AppUtils.setBackground(etFormula, R.drawable.input_border_bottom_red)
            etFormula.requestFocus()
            return
        } else {
            AppUtils.setBackground(etFormula, R.drawable.input_border_bottom)
            errorFormula.visibility = View.INVISIBLE
        }

        if (etPosition.text.toString().isBlank()) {
            errorPosition.visibility = View.VISIBLE
            AppUtils.setBackground(etPosition, R.drawable.input_border_bottom_red)
            etPosition.requestFocus()
            return
        } else {
            AppUtils.setBackground(etPosition, R.drawable.input_border_bottom)
            errorPosition.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = if (personalizedGroupData == null)
            api.postPersonalizedGroup(
                etCode.text.toString(),
                etLabel.text.toString(),
                calculatedId,
                etPosition.text.toString(),
                etFormula.text.toString(),
                etComment.text.toString(),
                countryId,
                personalizedAccountsId
            )
        else
            api.updatePersonalizedGroup(
                etCode.text.toString(),
                etLabel.text.toString(),
                calculatedId,
                etPosition.text.toString(),
                etFormula.text.toString(),
                etComment.text.toString(),
                countryId,
                personalizedAccountsId,
                personalizedGroupData?.id!!
            )

        RetrofitClient.apiCall(call, this, "PostPersonalizedGroup")


    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "PostPersonalizedGroup" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack()
            }
            "ParentAccountList" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), PersonalizedAccountModel::class.java)
                parentAccountList.addAll(model.data)

                for (i in parentAccountList.indices) {
                    parentAccountList[i]?.label =
                        (parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
                }

                setUpSpinners()
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


}