package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedThirdPartyData
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedThirdPartyModel
import com.minbio.erp.main.MainActivity
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

class TurnOverInvoiceThirdPartyFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btnRefresh: TextView
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var tvNoData: TextView
    private lateinit var tvTotalAmountExcl: TextView
    private lateinit var tvTotalAmountIncl: TextView
    private lateinit var searchItem: ImageView
    private lateinit var totalLayout: LinearLayout
    private lateinit var fatoitpRecyclerView: RecyclerView
    private lateinit var turnOverInvoicedThirdPartyAdapter: TurnOverInvoicedThirdPartyAdapter


    private var fromDateToSearch = ""
    private var toDateToSearch = ""
    var customerNameToSearch = ""
    var countryIdToSearch = 0

    private var toInvoicedThirdPartyData: MutableList<TOInvoicedThirdPartyData> = ArrayList()
    private lateinit var fatoitptvAmountExcl: TextView
    private lateinit var fatoitptvAmountIncl: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_invoiced_third_parties,
            container,
            false
        )
        initViews()
        setUpAdapter()

        getThirdPartyData()

        return v
    }

    private fun initViews() {
        btnRefresh = v.findViewById(R.id.btnRefresh)
        fromDate = v.findViewById(R.id.fromDate)
        toDate = v.findViewById(R.id.toDate)
        currentDateTime = v.findViewById(R.id.currentDateTime)
        tvNoData = v.findViewById(R.id.tvNoData)
        tvTotalAmountExcl = v.findViewById(R.id.tvTotalAmountExcl)
        tvTotalAmountIncl = v.findViewById(R.id.tvTotalAmountIncl)
        fatoitpRecyclerView = v.findViewById(R.id.fatoitpRecyclerView)
        searchItem = v.findViewById(R.id.searchItem)
        totalLayout = v.findViewById(R.id.totalLayout)
        fatoitptvAmountExcl = v.findViewById(R.id.fatoitptvAmountExcl)
        fatoitptvAmountIncl = v.findViewById(R.id.fatoitptvAmountIncl)

        fatoitptvAmountExcl.text = context!!.resources.getString(
            R.string.fatoitpLabelAmountExcl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        fatoitptvAmountIncl.text = context!!.resources.getString(
            R.string.fatoitpLabelAmountIncl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        fromDate.setOnClickListener { showDatePicker(fromDate, true) }
        toDate.setOnClickListener { showDatePicker(toDate, false) }

        searchItem.setOnClickListener {
            val dialog = TurnOverInvoicedThirdPartyDialogSearch(
                this,
                context!!,
                (activity as MainActivity).countriesModel
            )
            dialog.setOwnerActivity(activity!!)
            dialog.show()
        }

        btnRefresh.setOnClickListener {
            getThirdPartyData()
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
        turnOverInvoicedThirdPartyAdapter =
            TurnOverInvoicedThirdPartyAdapter(toInvoicedThirdPartyData)
        fatoitpRecyclerView.layoutManager = LinearLayoutManager(context)
        fatoitpRecyclerView.adapter = turnOverInvoicedThirdPartyAdapter
    }

    fun getThirdPartyData() {
        AppUtils.showDialog(context!!)
        val call =
            RetrofitClient.getClient(context!!).create(Api::class.java)
                .getTurnOverInvoicedByThirdParties(
                    fromDateToSearch, toDateToSearch, customerNameToSearch, countryIdToSearch
                )
        RetrofitClient.apiCall(call, this, " InvoicedByThirdParties")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        val model =
            Gson().fromJson(jsonObject.toString(), TOInvoicedThirdPartyModel::class.java)

        toInvoicedThirdPartyData.clear()
        toInvoicedThirdPartyData.addAll(model.data!!)

        if (toInvoicedThirdPartyData.isNullOrEmpty()) {
            fatoitpRecyclerView.visibility = View.GONE
            totalLayout.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            fatoitpRecyclerView.visibility = View.VISIBLE
            totalLayout.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            tvTotalAmountExcl.text = model?.total_amount_exc_tax
            tvTotalAmountIncl.text = model?.total_amount_inc_tax
        }

        turnOverInvoicedThirdPartyAdapter.notifyDataSetChanged()
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