package com.minbio.erp.financial_management.biling_payments.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.models.BillingReportingModel
import com.minbio.erp.financial_management.biling_payments.models.MonthYearModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.DownloadFile
import org.json.JSONObject
import java.util.*


class BillingReportingFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var spinnerMonth: CustomSearchableSpinner
    private lateinit var spinnerYear: CustomSearchableSpinner
    private lateinit var btnDownload: LinearLayout

    private var monthsList: MutableList<MonthYearModel> = ArrayList()
    private var yearList: MutableList<String> = ArrayList()

    private var monthId = 0
    private var yearId = ""
    private var fromWhere = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_reporting, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        fromWhere = arguments?.getInt("fromWhere", -1)!!

        spinnerMonth = v.findViewById(R.id.spinnerMonth)
        spinnerYear = v.findViewById(R.id.spinnerYear)

        btnDownload = v.findViewById(R.id.btnDownload)
        btnDownload.setOnClickListener {
            AppUtils.preventTwoClick(btnDownload)
            getReportFile()
        }


        setUpLists()
        setUpSpinners()
    }

    private fun setUpLists() {
        monthsList.clear()
        yearList.clear()

//        monthsList.add(MonthYearModel(0, ""))
        monthsList.add(MonthYearModel(1, "Jan"))
        monthsList.add(MonthYearModel(2, "Feb"))
        monthsList.add(MonthYearModel(3, "Mar"))
        monthsList.add(MonthYearModel(4, "Apr"))
        monthsList.add(MonthYearModel(5, "May"))
        monthsList.add(MonthYearModel(6, "Jun"))
        monthsList.add(MonthYearModel(7, "Jul"))
        monthsList.add(MonthYearModel(8, "Aug"))
        monthsList.add(MonthYearModel(9, "Sep"))
        monthsList.add(MonthYearModel(10, "Oct"))
        monthsList.add(MonthYearModel(11, "Nov"))
        monthsList.add(MonthYearModel(12, "Dec"))

//        yearList.add("")
//        yearList.add("2025")
//        yearList.add("2024")
//        yearList.add("2023")
//        yearList.add("2022")
//        yearList.add("2021")
        yearList.add("2020")
        yearList.add("2019")
        yearList.add("2018")
        yearList.add("2017")
        yearList.add("2016")
        yearList.add("2015")
        yearList.add("2014")
        yearList.add("2013")
        yearList.add("2012")
        yearList.add("2012")
        yearList.add("2011")
        yearList.add("2010")


    }

    private fun setUpSpinners() {
        /*Month Spinner*/
        val monthStrings = ArrayList<String>()
        for (i in monthsList.indices) monthStrings.add(monthsList[i].name)

        spinnerMonth.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerMonth.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val monthAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, monthStrings
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                monthId = monthsList[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Year Spinner*/
        val yearStrings = ArrayList<String>()
        for (i in yearList.indices) yearStrings.add(yearList[i])

        spinnerYear.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerYear.setTitle(resources.getString(R.string.bankAccountTypeSpinnerTitle))

        val sensAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, yearStrings
        )
        sensAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = sensAdapter
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                yearId = yearList[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        if (tag == "GetBillingReports") {
            val model = Gson().fromJson(jsonObject.toString(), BillingReportingModel::class.java)
            val url = model.data.pdf_url
            var fileName = url.substring(url.lastIndexOf('/') + 1)
            val fileExt = fileName.substring(fileName.lastIndexOf('.') + 1)
            fileName = fileName.substringBefore('.', fileName)

            DownloadFile(context!!, fileName, fileExt).execute(url)
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

    private fun getReportFile() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            AppUtils.showDialog(context!!)
            val api = RetrofitClient.getClient(context!!).create(Api::class.java)
            val call =
                if (fromWhere == 1) api.getBillingVendorReport(monthId, yearId)
                else api.getBillingCustomerReport(monthId, yearId)

            RetrofitClient.apiCall(call, this, "GetBillingReports")
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Constants.STORAGE_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getReportFile()
            } else {
                AppUtils.showToast(
                    activity!!,
                    resources.getString(R.string.locationPermissionDenied),
                    false
                )
            }
        }
    }

}