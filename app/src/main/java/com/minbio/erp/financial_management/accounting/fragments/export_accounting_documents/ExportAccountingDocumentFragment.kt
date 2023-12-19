package com.minbio.erp.financial_management.accounting.fragments.export_accounting_documents

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.DownloadFile
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ExportAccountingDocumentFragment : Fragment(), CompoundButton.OnCheckedChangeListener,
    ResponseCallBack {

    private lateinit var v: View
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var btnSearch: TextView
    private lateinit var btnDownload: TextView
    private lateinit var tvTotalIncomeExcl: TextView
    private lateinit var tvTotalIncomeIncl: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenseExcl: TextView
    private lateinit var tvTotalExpenseIncl: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTotalExcl: TextView
    private lateinit var tvTotalIncl: TextView
    private lateinit var tvTotalTotal: TextView
    private lateinit var tvNoData: TextView
    private lateinit var cbInvoices: CheckBox
    private lateinit var cbVendorInvoices: CheckBox
    private lateinit var cbExpenseReport: CheckBox
    private lateinit var cbMiscellaneousPayments: CheckBox
    private lateinit var faeadRecyclerView: RecyclerView
    private lateinit var totalLayout: LinearLayout
    private lateinit var totalExpenseLayout: LinearLayout
    private lateinit var totalIncomeLayout: LinearLayout
    private lateinit var exportAccountingDocumentAdapter: ExportAccountingDocumentAdapter

    private var fromDateToSearch = ""
    private var toDateToSearch = ""
    private var isInvoices = 1
    private var isVendorInvoices = 1
    private var isExpenseReports = 1
    private var isMiscellaneousPayment = 1

    private var downloadFileId = ""
    private var downloadFileType = ""

    private var exportAccountingDocumentData: MutableList<ExportAccountingDocumentData> =
        ArrayList()
    private lateinit var faeadtvPaid : TextView
    private lateinit var faeadtvTaxExcl : TextView
    private lateinit var faeadtvTaxIncl : TextView
    private lateinit var faeadtvTotalTax : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_export_accounting_document,
            container,
            false
        )

        initViews()
        setUpAdapter()

        getExportAccounting()

        return v
    }

    private fun initViews() {
        fromDate = v.findViewById(R.id.fromDate)
        toDate = v.findViewById(R.id.toDate)
        btnSearch = v.findViewById(R.id.btnSearch)
        tvTotalIncomeExcl = v.findViewById(R.id.tvTotalIncomeExcl)
        tvTotalIncomeIncl = v.findViewById(R.id.tvTotalIncomeIncl)
        tvTotalIncome = v.findViewById(R.id.tvTotalIncome)
        tvTotalExpenseExcl = v.findViewById(R.id.tvTotalExpenseExcl)
        tvTotalExpenseIncl = v.findViewById(R.id.tvTotalExpenseIncl)
        tvTotalExpense = v.findViewById(R.id.tvTotalExpense)
        tvTotalExcl = v.findViewById(R.id.tvTotalExcl)
        tvTotalIncl = v.findViewById(R.id.tvTotalIncl)
        tvTotalTotal = v.findViewById(R.id.tvTotalTotal)
        cbInvoices = v.findViewById(R.id.cbInvoices)
        cbVendorInvoices = v.findViewById(R.id.cbVendorInvoices)
        cbExpenseReport = v.findViewById(R.id.cbExpenseReport)
        cbMiscellaneousPayments = v.findViewById(R.id.cbMiscellaneousPayments)
        faeadRecyclerView = v.findViewById(R.id.faeadRecyclerView)
        totalLayout = v.findViewById(R.id.totalLayout)
        totalExpenseLayout = v.findViewById(R.id.totalExpenseLayout)
        totalIncomeLayout = v.findViewById(R.id.totalIncomeLayout)
        tvNoData = v.findViewById(R.id.tvNoData)
        btnDownload = v.findViewById(R.id.btnDownload)

        faeadtvPaid = v.findViewById(R.id.faeadtvPaid)
        faeadtvTaxExcl = v.findViewById(R.id.faeadtvTaxExcl)
        faeadtvTaxIncl = v.findViewById(R.id.faeadtvTaxIncl)
        faeadtvTotalTax = v.findViewById(R.id.faeadtvTotalTax)

        faeadtvPaid.text = context!!.resources.getString(
            R.string.faeadLabelPaid,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faeadtvTaxExcl.text = context!!.resources.getString(
            R.string.faeadLabelTotalExcl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faeadtvTaxIncl.text = context!!.resources.getString(
            R.string.faeadLabelTotalIncl,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faeadtvTotalTax.text = context!!.resources.getString(
            R.string.faeadLabelTotalTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        fromDate.setOnClickListener { showDatePicker(fromDate, true) }
        toDate.setOnClickListener { showDatePicker(toDate, false) }

        cbInvoices.setOnCheckedChangeListener(this)
        cbVendorInvoices.setOnCheckedChangeListener(this)
        cbExpenseReport.setOnCheckedChangeListener(this)
        cbMiscellaneousPayments.setOnCheckedChangeListener(this)

        btnSearch.setOnClickListener {
            getExportAccounting()
        }


        setDate()

    }

    private fun setDate() {
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
        exportAccountingDocumentAdapter =
            ExportAccountingDocumentAdapter(exportAccountingDocumentData, this)
        faeadRecyclerView.layoutManager = LinearLayoutManager(context)
        faeadRecyclerView.adapter = exportAccountingDocumentAdapter
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.cbInvoices -> {
                isInvoices = if (isChecked) 1 else 0
            }
            R.id.cbVendorInvoices -> {
                isVendorInvoices = if (isChecked) 1 else 0
            }
            R.id.cbExpenseReport -> {
                isExpenseReports = if (isChecked) 1 else 0
            }
            R.id.cbMiscellaneousPayments -> {
                isMiscellaneousPayment = if (isChecked) 1 else 0
            }
        }
    }

    private fun getExportAccounting() {
        AppUtils.showDialog(context!!)
        val call = RetrofitClient.getClient(context!!).create(Api::class.java)
            .getExportAccoutningDocument(
                fromDateToSearch, toDateToSearch, isInvoices,
                isVendorInvoices, isMiscellaneousPayment, isExpenseReports
            )
        RetrofitClient.apiCall(call, this, "ExportAccountingDocument")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()

        when (tag) {
            "ExportAccountingDocument" -> {
                val model =
                    Gson().fromJson(
                        jsonObject.toString(),
                        ExportAccountingDocumentModel::class.java
                    )
                exportAccountingDocumentData.clear()
                exportAccountingDocumentData.addAll(model.data!!)

                if (exportAccountingDocumentData.isNullOrEmpty()) {
                    faeadRecyclerView.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                    totalLayout.visibility = View.GONE
                    totalExpenseLayout.visibility = View.GONE
                    totalIncomeLayout.visibility = View.GONE
                    btnDownload.visibility = View.GONE
                } else {
                    faeadRecyclerView.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE
                    totalLayout.visibility = View.VISIBLE
                    totalExpenseLayout.visibility = View.VISIBLE
                    totalIncomeLayout.visibility = View.VISIBLE
                    btnDownload.visibility = View.GONE

                    tvTotalIncomeExcl.text = model.income_exc_tax
                    tvTotalIncomeIncl.text = model.income_inc_tax
                    tvTotalIncome.text = model.income_tax

                    tvTotalExpenseExcl.text = model.expense_exc_tax
                    tvTotalExpenseIncl.text = model.expense_inc_tax
                    tvTotalExpense.text = model.expense_tax

                    tvTotalExcl.text = model.total_exc_tax
                    tvTotalIncl.text = model.total_inc_tax
                    tvTotalTotal.text = model.total_tax
                }

                exportAccountingDocumentAdapter.notifyDataSetChanged()
            }
            "DownloadExportAccountingDocument" -> {
                val data: JSONObject = jsonObject.getJSONObject("data")
                val url = data.getString("url")
                val fileName = data.getString("file_name")
                val fileExt = data.getString("ext")
                DownloadFile(context!!, fileName, fileExt).execute(url)
            }
        }


    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, message!!, false)
    }

    fun downloadExportAccountingDocument(id: String?, type: String?) {
        downloadFileId = id!!
        downloadFileType = type!!
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            AppUtils.showDialog(context!!)
            val call = RetrofitClient.getClient(context!!).create(Api::class.java)
                .downloadExportAccountingDocument(downloadFileId.toInt(), downloadFileType)
            RetrofitClient.apiCall(call, this, "DownloadExportAccountingDocument")
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
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                downloadExportAccountingDocument(downloadFileId, downloadFileType)
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