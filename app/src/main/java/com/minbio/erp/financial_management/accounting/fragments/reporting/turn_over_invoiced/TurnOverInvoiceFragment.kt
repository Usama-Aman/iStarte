package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedData
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedModel
import com.minbio.erp.main.models.SettingsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TurnOverInvoiceFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var fatoiRecyclerView: RecyclerView
    private lateinit var turnOverInvoicedAdapter: TurnOverInvoicedAdapter
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var btnRefresh: TextView
    private lateinit var tvNoData: TextView
    private lateinit var currentDateTime: TextView


    private var fromDateToSearch = ""
    private var toDateToSearch = ""

    private var toInvoicedData : MutableList<TOInvoicedData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_turnover_invoiced,
            container,
            false
        )

        initViews()
        setUpAdapter()

            getInvoicedTurnOver()

        return v
    }

    private fun initViews() {
        fatoiRecyclerView = v.findViewById(R.id.fatoiRecyclerView)
        fromDate = v.findViewById(R.id.fromDate)
        toDate = v.findViewById(R.id.toDate)
        btnRefresh = v.findViewById(R.id.btnRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        currentDateTime = v.findViewById(R.id.currentDateTime)


        fromDate.setOnClickListener { showDatePicker(fromDate, true) }
        toDate.setOnClickListener { showDatePicker(toDate, false) }

        btnRefresh.setOnClickListener {
            getInvoicedTurnOver()
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

    private fun setUpAdapter() {
        turnOverInvoicedAdapter = TurnOverInvoicedAdapter(toInvoicedData)
        fatoiRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fatoiRecyclerView.adapter = turnOverInvoicedAdapter
    }

    private fun getInvoicedTurnOver() {
        AppUtils.showDialog(context!!)
        val call = RetrofitClient.getClient(context!!).create(Api::class.java)
            .getTurnOverInvoiced(fromDateToSearch, toDateToSearch)
        RetrofitClient.apiCall(call, this, "TurnOverInvoiced")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        val model = Gson().fromJson(jsonObject.toString(), TOInvoicedModel::class.java)
        toInvoicedData.clear()
        toInvoicedData.addAll(model.data!!)

        if (toInvoicedData.isNullOrEmpty()) {
            fatoiRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            fatoiRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        turnOverInvoicedAdapter.notifyDataSetChanged()

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