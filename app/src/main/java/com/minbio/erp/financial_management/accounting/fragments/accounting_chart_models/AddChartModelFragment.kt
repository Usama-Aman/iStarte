package com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models.AccountChartModelData
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject

class AddChartModelFragment : Fragment(), ResponseCallBack {

    private var countriesId: Int = -1
    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var back: ImageView

    private lateinit var etChartModel: EditText
    private lateinit var etChartModelLabel: EditText
    private lateinit var spinnerChartModelCountry: CustomSearchableSpinner
    private lateinit var errorChartModel: TextView
    private lateinit var errorChartModelLabel: TextView
    private lateinit var btnText: TextView

    private lateinit var btnAddModify: LinearLayout
    private var chartModelData: AccountChartModelData? = null
    private lateinit var countriesModel: CountriesModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_add_chart_model,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        return v
    }

    private fun initViews() {
        chartModelData = arguments?.getParcelable("data")

        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as FinancialManagementFragment).childFragmentManager.popBackStack() }

        btnText = v.findViewById(R.id.btnText)

        etChartModel = v.findViewById(R.id.etChartModel)
        etChartModelLabel = v.findViewById(R.id.etChartModelLabel)
        spinnerChartModelCountry = v.findViewById(R.id.spinnerChartModelCountry)
        errorChartModel = v.findViewById(R.id.errorChartModel)
        errorChartModelLabel = v.findViewById(R.id.errorChartModelLabel)

        btnAddModify = v.findViewById(R.id.btnAdd)
        btnAddModify.setOnClickListener {
            validate()
        }

        setUpCountriesSpinner()

    }

    private fun validate() {

        if (etChartModel.text.toString().isBlank()) {
            errorChartModel.visibility = View.VISIBLE
            AppUtils.setBackground(etChartModel, R.drawable.input_border_bottom_red)
            etChartModel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etChartModel, R.drawable.input_border_bottom)
            errorChartModel.visibility = View.INVISIBLE
        }

        if (etChartModelLabel.text.toString().isBlank()) {
            errorChartModelLabel.visibility = View.VISIBLE
            AppUtils.setBackground(etChartModelLabel, R.drawable.input_border_bottom_red)
            etChartModelLabel.requestFocus()
            return
        } else {
            AppUtils.setBackground(etChartModelLabel, R.drawable.input_border_bottom)
            errorChartModelLabel.visibility = View.INVISIBLE
        }

        postChartModel()
    }

    private fun setUpCountriesSpinner() {
        countriesModel = (activity as MainActivity).countriesList()

        val strings = ArrayList<String>()
        for (i in countriesModel.data) {
            strings.add(i.name)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerChartModelCountry.adapter = positionAdapter
        spinnerChartModelCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerChartModelCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
        spinnerChartModelCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countriesId = countriesModel.data[position].id
                    AppUtils.hideKeyboard(activity!!)
                }
            }

        if (chartModelData != null) {
            etChartModel.setText(chartModelData?.chart_accounts_model)
            etChartModelLabel.setText(chartModelData?.label)

            for (i in countriesModel.data.indices)
                if (chartModelData?.country_id == countriesModel.data[i].id)
                    spinnerChartModelCountry.setSelection(i)

            btnText.text = context!!.resources.getString(R.string.facamBtnModify)
        } else
            btnText.text = context!!.resources.getString(R.string.facamBtnAdd)


    }

    private fun postChartModel() {
        AppUtils.showDialog(context!!)
        val call = if (chartModelData == null)
            api.postChartModel(
                etChartModel.text.toString(),
                etChartModelLabel.text.toString(),
                countriesId
            )
        else
            api.updateChartModel(
                etChartModel.text.toString(),
                etChartModelLabel.text.toString(),
                countriesId, chartModelData?.id!!
            )

        RetrofitClient.apiCall(call, this, "PostChartModel")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "PostChartModel" -> {
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


}