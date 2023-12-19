package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PredefinedGroupsReportingData
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PredefinedGroupsReportingModel
import com.minbio.erp.main.models.SettingsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PreDefinedGroupReportingFragment : Fragment(), ResponseCallBack {

    private var detailByAccountStrings = arrayOf("No", "All accounts with non-zero values", "All")

    private lateinit var v: View
    private lateinit var btnRefresh: TextView
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvNoData: TextView
    private lateinit var fapdgtvAmount: TextView
    private lateinit var profitLayout: LinearLayout
    private lateinit var spinnerDetailByAccount: CustomSearchableSpinner
    private lateinit var fapdgrRecyclerView: RecyclerView
    private lateinit var preDefinedGroupReportingAdapter: PreDefinedGroupReportingAdapter

    private var fromDateToSearch = ""
    private var toDateToSearch = ""
    private var detailbyAccountId = "no"

    private var predefinedGroupsReportingData: MutableList<PredefinedGroupsReportingData> =
        ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_predefined_group_reporting,
            container,
            false
        )

        initViews()
        setUpAdapter()
        setUpDetailSpinner()


        return v
    }

    private fun initViews() {
        btnRefresh = v.findViewById(R.id.btnRefresh)
        fromDate = v.findViewById(R.id.fromDate)
        toDate = v.findViewById(R.id.toDate)
        currentDateTime = v.findViewById(R.id.currentDateTime)
        tvTotalAmount = v.findViewById(R.id.tvTotalAmount)
        fapdgrRecyclerView = v.findViewById(R.id.fapdgrRecyclerView)
        spinnerDetailByAccount = v.findViewById(R.id.spinnerDetailByAccount)
        profitLayout = v.findViewById(R.id.profitLayout)
        tvNoData = v.findViewById(R.id.tvNoData)
        fapdgtvAmount = v.findViewById(R.id.fapdgtvAmount)
        fapdgtvAmount.text = context!!.resources.getString(
            R.string.fapdgLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        fromDate.setOnClickListener { showDatePicker(fromDate, true) }
        toDate.setOnClickListener { showDatePicker(toDate, false) }

        btnRefresh.setOnClickListener {
            getPredefinedGroupData()
        }

        setDate()
    }

    private fun setDate() {
        val gson = Gson()
        val settingModel = gson.fromJson(
            SharedPreference.getSimpleString(context, Constants.settingsData),
            SettingsModel::class.java
        )
        var format: String = ""
        for (i in settingModel.data.date_formats.indices)
            if (settingModel.data.date_formats[i].value == settingModel.data.settings.date_format.toInt()) {
                when (settingModel.data.date_formats[i].format) {
                    "YYYY-MM-DD" -> {
                        format = "yyyy-MM-dd"
                    }
                    "DD-MM-YYYY" -> {
                        format = "dd-MM-yyyy"
                    }
                    "YYYY/MM/DD" -> {
                        format = "yyyy/MM/dd"
                    }
                    "DD/MM/YYYY" -> {
                        format = "dd-MM-yyyy"
                    }
                    else -> {
                        format = "dd-MM-yyyy"
                    }
                }

                val sdf = SimpleDateFormat(
                    "$format H:mm a",
                    Locale.getDefault()
                )
                val cdt: String = sdf.format(Date())
                currentDateTime.text = cdt
                break
            }

        val c = Calendar.getInstance()
        c.add(Calendar.DATE, -30)
        val y = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)

        fromDate.text = AppUtils.getDateFormat(context!!, 1, month + 1, y)
        toDate.text = AppUtils.getDateFormat(context!!, 30, month + 1, y)

        fromDateToSearch = "1-${month + 1}-$y"
        toDateToSearch = "30-${month + 1}-$y"
    }

    private fun showDatePicker(textview: TextView, isFrom: Boolean) {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            activity!!,
            { _, year, monthOfYear, dayOfMonth ->
                textview.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                if (isFrom) {
                    fromDateToSearch = "$day-${monthOfYear + 1}-$year"
                } else {
                    toDateToSearch = "$day-${monthOfYear + 1}-$year"
                }
            },
            y,
            month,
            day
        )
        datePicker.show()
    }

    private fun setUpDetailSpinner() {
        spinnerDetailByAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerDetailByAccount.setTitle(context!!.resources.getString(R.string.optionTypeSpinnerTitle))

        val fromAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, detailByAccountStrings
        )
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDetailByAccount.adapter = fromAdapter
        spinnerDetailByAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    detailbyAccountId = when (adapterView.selectedItemPosition) {
                        0 -> "no"
                        1 -> "with_non_zeros_values"
                        2 -> "all"
                        else -> ""
                    }
                    AppUtils.hideKeyboard(activity!!)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        getPredefinedGroupData()

    }

    private fun setUpAdapter() {
        preDefinedGroupReportingAdapter =
            PreDefinedGroupReportingAdapter(predefinedGroupsReportingData)
        fapdgrRecyclerView.layoutManager = LinearLayoutManager(context)
        fapdgrRecyclerView.adapter = preDefinedGroupReportingAdapter
    }

    private fun getPredefinedGroupData() {
        AppUtils.showDialog(context!!)
        val call = RetrofitClient.getClient(context!!).create(Api::class.java)
            .getPredefinedGroupReporting(fromDateToSearch, toDateToSearch, detailbyAccountId)
        RetrofitClient.apiCall(call, this, "Predefined Groups")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        val model =
            Gson().fromJson(jsonObject.toString(), PredefinedGroupsReportingModel::class.java)

        predefinedGroupsReportingData.clear()
        predefinedGroupsReportingData.addAll(model.data!!)

        if (predefinedGroupsReportingData.isNullOrEmpty()) {
            profitLayout.visibility = View.GONE
            fapdgrRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            tvTotalAmount.text = model?.profit
            profitLayout.visibility = View.VISIBLE
            fapdgrRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        preDefinedGroupReportingAdapter.notifyDataSetChanged()
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, message!!, false)
    }
}
