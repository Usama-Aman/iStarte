package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.customer_management.models.CustomersModel
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.AllCustomersData
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.AllCustomersModel
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedProductServiceData
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedProductServiceModel
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

class TurnOverInvoicedProductServiceFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btnRefresh: TextView
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var tvNoData: TextView
    private lateinit var tvTotalAmountExcl: TextView
    private lateinit var tvTotalAmountIncl: TextView
    private lateinit var totalLayout: LinearLayout
    private lateinit var fatoipsRecyclerView: RecyclerView
    private lateinit var spinnerThirdParty: CustomSearchableSpinner
    private lateinit var turnOverInvoicedProductServiceAdapter: TurnOverInvoicedProductServiceAdapter

    private var fromDateToSearch = ""
    private var toDateToSearch = ""
    private var customerId: Int = 0

    private var customersList: MutableList<AllCustomersData?> = ArrayList()
    private var toInvoicedProductServiceData: MutableList<TOInvoicedProductServiceData?> =
        ArrayList()
    private lateinit var fatoipstvAmountExcl: TextView
    private lateinit var fatoipstvAmountIncl: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_fa_turnover_invoiced_product_service,
            container,
            false
        )
        initViews()
        setUpAdapter()

        getCustomersData()

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
        fatoipsRecyclerView = v.findViewById(R.id.fatoipsRecyclerView)
        totalLayout = v.findViewById(R.id.totalLayout)
        spinnerThirdParty = v.findViewById(R.id.spinnerThirdParty)
        fatoipstvAmountExcl = v.findViewById(R.id.fatoipstvAmountExcl)
        fatoipstvAmountIncl = v.findViewById(R.id.fatoipstvAmountIncl)

        fatoipstvAmountExcl.text = context!!.resources.getString(
            R.string.fatoipsLabelAmountExcl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        fatoipstvAmountIncl.text = context!!.resources.getString(
            R.string.fatoipsLabelAmountIncl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        fromDate.setOnClickListener { showDatePicker(fromDate, true) }
        toDate.setOnClickListener { showDatePicker(toDate, false) }

        btnRefresh.setOnClickListener {
            AppUtils.showDialog(context!!)
            getInvoicedProductService()
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
        turnOverInvoicedProductServiceAdapter =
            TurnOverInvoicedProductServiceAdapter(toInvoicedProductServiceData)
        fatoipsRecyclerView.layoutManager = LinearLayoutManager(context)
        fatoipsRecyclerView.adapter = turnOverInvoicedProductServiceAdapter
    }

    private fun setUpCustomerSpinner() {

        val strings = ArrayList<String>()
        for (i in customersList) {
            strings.add(i?.full_name!!)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerThirdParty.adapter = positionAdapter
        spinnerThirdParty.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerThirdParty.setTitle(context!!.resources.getString(R.string.thirdPartySpinnerTitle))
        spinnerThirdParty.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    customerId = customersList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

    }

    private fun getCustomersData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getAllCustomersList()
        RetrofitClient.apiCall(call, this, "GetCustomers")
    }

    private fun getInvoicedProductService() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call =
            api.getTurnOverInvoicedByProductService(fromDateToSearch, toDateToSearch, customerId)
        RetrofitClient.apiCall(call, this, "InvoicedByProductService")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetCustomers" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), AllCustomersModel::class.java)

                customersList.clear()
                customersList.add(
                    AllCustomersData("", 0, "")
                )
                model.data?.let { customersList.addAll(it) }

                setUpCustomerSpinner()
                getInvoicedProductService()
            }
            "InvoicedByProductService" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(
                        jsonObject.toString(),
                        TOInvoicedProductServiceModel::class.java
                    )

                toInvoicedProductServiceData.clear()
                toInvoicedProductServiceData.addAll(model?.data!!)

                if (toInvoicedProductServiceData.isNullOrEmpty()) {
                    fatoipsRecyclerView.visibility = View.GONE
                    totalLayout.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                } else {
                    fatoipsRecyclerView.visibility = View.VISIBLE
                    totalLayout.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE
                    tvTotalAmountExcl.text = model.total_amount_exc_tax
                    tvTotalAmountIncl.text = model.total_amount_inc_tax
                }

                turnOverInvoicedProductServiceAdapter.notifyDataSetChanged()
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