package com.minbio.erp.accounting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.accounting.models.FileTransferModel
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject

class AccountingFileTransfer : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var aft_third_party_download_upload: CheckBox
    private lateinit var aft_order_transfer: CheckBox
    private lateinit var aft_inventory_transfer: CheckBox
    private lateinit var aft_customerList_transfer: CheckBox
    private lateinit var aft_product_list_transfer: CheckBox
    private lateinit var aft_supplier_list_transfer: CheckBox
    private lateinit var aft_invoice_list_transfer: CheckBox

    private lateinit var btnSave: LinearLayout

    private var thirdParty = 0
    private var orderTransfer = 0
    private var customerListTransfer = 0
    private var productListTransfer = 0
    private var supplierListTransfer = 0
    private var invoiceListTransfer = 0
    private var inventoryTransfer = 0

    private var selectedId = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_file_transfer, container, false)

        initViews()


        return v
    }

    private fun initViews() {

        selectedId = arguments?.getInt("selectedId")!!

        if (selectedId != 0)
            getFileTransferData()
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )

        btnSave = v.findViewById(R.id.btn_aft_save)
        btnSave.setOnClickListener { postFileTransferData() }

        aft_third_party_download_upload = v.findViewById(R.id.aft_third_party_download_upload)
        aft_order_transfer = v.findViewById(R.id.aft_order_transfer)
        aft_customerList_transfer = v.findViewById(R.id.aft_customerList_transfer)
        aft_inventory_transfer = v.findViewById(R.id.aft_inventory_transfer)
        aft_product_list_transfer = v.findViewById(R.id.aft_product_list_transfer)
        aft_supplier_list_transfer = v.findViewById(R.id.aft_supplier_list_transfer)
        aft_invoice_list_transfer = v.findViewById(R.id.aft_invoice_list_transfer)

        aft_third_party_download_upload.setOnCheckedChangeListener { buttonView, isChecked ->
            thirdParty = if (isChecked) 1 else 0
        }
        aft_order_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            orderTransfer = if (isChecked) 1 else 0
        }
        aft_customerList_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            customerListTransfer = if (isChecked) 1 else 0
        }
        aft_product_list_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            productListTransfer = if (isChecked) 1 else 0
        }
        aft_supplier_list_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            supplierListTransfer = if (isChecked) 1 else 0
        }
        aft_invoice_list_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            invoiceListTransfer = if (isChecked) 1 else 0
        }
        aft_inventory_transfer.setOnCheckedChangeListener { buttonView, isChecked ->
            inventoryTransfer = if (isChecked) 1 else 0
        }
    }

    private fun getFileTransferData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getFileTransferSettings(selectedId)
        RetrofitClient.apiCall(call, this, "GetFileTransfer")
    }

    private fun postFileTransferData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postFileTransferSettings(
            selectedId,
            thirdParty.toString(),
            orderTransfer.toString(),
            customerListTransfer.toString(),
            inventoryTransfer.toString(),
            productListTransfer.toString(),
            supplierListTransfer.toString(),
            invoiceListTransfer.toString()
        )
        RetrofitClient.apiCall(call, this, "PostFileTransfer")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetFileTransfer") {
            handleResponse(jsonObject)
        } else if (tag == "PostFileTransfer")
            AppUtils.showToast(activity, jsonObject.getString("message"), true)
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }


    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    private fun handleResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model =
            gson.fromJson(jsonObject.toString(), FileTransferModel::class.java)

        aft_third_party_download_upload.isChecked = model.data.third_party_download_upload == "1"
        aft_order_transfer.isChecked = model.data.order_transfer == "1"
        aft_customerList_transfer.isChecked = model.data.customer_list_transfer == "1"
        aft_inventory_transfer.isChecked = model.data.inventory_transfer == "1"
        aft_product_list_transfer.isChecked = model.data.product_list_transfer == "1"
        aft_supplier_list_transfer.isChecked = model.data.supplier_list_transfer == "1"
        aft_invoice_list_transfer.isChecked = model.data.invoice_list_transfer == "1"

    }

}