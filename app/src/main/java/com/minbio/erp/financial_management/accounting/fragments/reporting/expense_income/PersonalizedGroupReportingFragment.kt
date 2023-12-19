package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PersonalizedGroupReportingData
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PersonalizedGroupReportingModel
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

class PersonalizedGroupReportingFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    private var detailByAccountStrings = arrayOf("No", "All accounts with non-zero values", "All")

    private lateinit var v: View
    private lateinit var btnRefresh: TextView
    private lateinit var tvNoData: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var farpgRecyclerView: RecyclerView
    private lateinit var spinnerDetailByAccount: CustomSearchableSpinner
    private lateinit var personalizedGroupAdapter: PersonalizedGroupReportingAdapter
    private var mYear: Int = 0
    private lateinit var ivArrowPrev: ImageView
    private lateinit var ivArrowNext: ImageView
    private lateinit var tvYear: TextView

    private var detailbyAccountId = "no"

    private var personalizedGroupReportingData: MutableList<PersonalizedGroupReportingData> =
        ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_personalized_groups_reporting,
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
        tvNoData = v.findViewById(R.id.tvNoData)
        currentDateTime = v.findViewById(R.id.currentDateTime)
        farpgRecyclerView = v.findViewById(R.id.farpgRecyclerView)
        spinnerDetailByAccount = v.findViewById(R.id.spinnerDetailByAccount)

        mYear = Calendar.getInstance().get(Calendar.YEAR)
        ivArrowPrev = v.findViewById(R.id.ivArrowPrev)
        ivArrowNext = v.findViewById(R.id.ivArrowNext)
        tvYear = v.findViewById(R.id.tvYear)
        tvYear.text = mYear.toString()
        ivArrowPrev.setOnClickListener(this)
        ivArrowNext.setOnClickListener(this)

        btnRefresh.setOnClickListener {
            getPersonalizedGroupData()
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivArrowNext -> {
                mYear++
                tvYear.text = mYear.toString()
                getPersonalizedGroupData()
            }
            R.id.ivArrowPrev -> {
                mYear--
                tvYear.text = mYear.toString()
                getPersonalizedGroupData()
            }
        }
    }

    private fun setUpAdapter() {
        personalizedGroupAdapter = PersonalizedGroupReportingAdapter(personalizedGroupReportingData)
        farpgRecyclerView.layoutManager = LinearLayoutManager(context)
        farpgRecyclerView.adapter = personalizedGroupAdapter
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

        getPersonalizedGroupData()
    }

    private fun getPersonalizedGroupData() {
        AppUtils.showDialog(context!!)
        val call = RetrofitClient.getClient(context!!).create(Api::class.java)
            .getPersonalizedGroupReporting(mYear, detailbyAccountId)
        RetrofitClient.apiCall(call, this, "PersonalizedGroupReporting")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        val model =
            Gson().fromJson(jsonObject.toString(), PersonalizedGroupReportingModel::class.java)

        personalizedGroupReportingData.clear()
        personalizedGroupReportingData.addAll(model.data!!)

        if (personalizedGroupReportingData.isNullOrEmpty()) {
            farpgRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            farpgRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        personalizedGroupAdapter.notifyDataSetChanged()
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
