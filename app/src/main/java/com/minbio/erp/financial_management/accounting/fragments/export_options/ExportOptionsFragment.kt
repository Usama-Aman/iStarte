package com.minbio.erp.financial_management.accounting.fragments.export_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.main.models.DateFormat
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.fragment_financial_accounting_export_options.*
import org.json.JSONObject

class ExportOptionsFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var spinnerFileFormat: CustomSearchableSpinner
    private lateinit var spinnerDateFormat: CustomSearchableSpinner
    private lateinit var etPrefixName: EditText
    private lateinit var btnSave: LinearLayout

    private var dateFormatList: MutableList<DateFormat> = ArrayList()

    private var fileFormatValue = ""
    private var dateFormat: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_export_options,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()

        return v
    }

    private fun initViews() {
        spinnerFileFormat = v.findViewById(R.id.spinnerFileFormat)
        spinnerDateFormat = v.findViewById(R.id.spinnerDateFormat)
        etPrefixName = v.findViewById(R.id.etPrefixName)

        btnSave = v.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            validate()
        }

        setUpSpinners()

        getExportOptionData()
    }

    private fun getExportOptionData() {
        AppUtils.showDialog(context!!)
        val call = api.getFileExportOptions()
        RetrofitClient.apiCall(call, this, "GetExportOptions")
    }


    private fun setUpSpinners() {
        val fileFormatItems = arrayOf("pdf", "csv")
        val spinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, fileFormatItems)
        spinnerFileFormat.adapter = spinnerAdapter
        spinnerFileFormat.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerFileFormat.setTitle(resources.getString(R.string.fileTypeSpinnerTitle))
        spinnerFileFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fileFormatValue = fileFormatItems[position]
            }
        }

        dateFormatList.add(DateFormat("YYYY-MM-DD", 1))
        dateFormatList.add(DateFormat("DD-MM-YYYY", 2))
//        dateFormatList.add(DateFormat("YYYY/MM/DD", 3))
//        dateFormatList.add(DateFormat("DD/MM/YYYY", 4))

        val strings = ArrayList<String>()
        for (i in dateFormatList) {
            strings.add(i.format)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerDateFormat.adapter = positionAdapter
        spinnerDateFormat.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerDateFormat.setTitle(resources.getString(R.string.dateFormatSpinnerTitle))
        spinnerDateFormat.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    dateFormat = dateFormatList[position].value
                    AppUtils.hideKeyboard(activity!!)
                }
            }

    }

    private fun validate() {
        if (etPrefixName.text.toString().isBlank()) {
            errorPrefixName.visibility = View.VISIBLE
            AppUtils.setBackground(etPrefixName, R.drawable.input_border_bottom_red)
            etPrefixName.requestFocus()
            return
        } else {
            AppUtils.setBackground(etPrefixName, R.drawable.input_border_bottom)
            errorPrefixName.visibility = View.INVISIBLE
        }

        AppUtils.showDialog(context!!)
        val call = api.postFileExportOptions(
            dateFormat.toString(), etPrefixName.text.toString(), fileFormatValue
        )
        RetrofitClient.apiCall(call, this, "PostExportOptions")

    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetExportOptions" -> {
                val model = Gson().fromJson(jsonObject.toString(), ExportOptionModel::class.java)

                etPrefixName.setText(model.data.file_name_prefix)

                if (model.data.file_format.equals("pdf", true))
                    spinnerFileFormat.setSelection(0)
                else
                    spinnerFileFormat.setSelection(1)

                for (i in dateFormatList.indices)
                    if (model.data.date_format == dateFormatList[i].value.toString())
                        spinnerDateFormat.setSelection(i)

            }
            "PostExportOptions" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
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