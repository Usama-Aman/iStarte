package com.minbio.erp.supplier_management.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.supplier_management.adapter.SupplierCreditNoteAdapter
import com.minbio.erp.supplier_management.models.SupplierCreditNoteData
import com.minbio.erp.supplier_management.models.SupplierCreditNoteModel
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SupplierCreditNote : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var supplierCreditNoteAdapter: SupplierCreditNoteAdapter
    private lateinit var scnRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var scntvTotalAmount: TextView

    private var creditNoteList: MutableList<SupplierCreditNoteData?> = ArrayList()
    private var supplierData: SuppliersData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_supplier_credit_note, container, false)

        initViews()
        setAdapter()
        initScrollListener()


        return v
    }

    private fun initViews() {
        supplierData = arguments!!.getParcelable("SupplierData")

        scnRecyclerView = v.findViewById(R.id.scn_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        scntvTotalAmount = v.findViewById(R.id.scntvTotalAmount)
        scntvTotalAmount.text = context!!.resources.getString(
            R.string.scnLabelTotalAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        if (supplierData != null)
            getSupplierComplaints(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }

    private fun setAdapter() {
        supplierCreditNoteAdapter = SupplierCreditNoteAdapter(creditNoteList)
        scnRecyclerView.layoutManager = LinearLayoutManager(context)
        scnRecyclerView.adapter = supplierCreditNoteAdapter

    }

    private fun initScrollListener() {
        scnRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == creditNoteList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            if (CURRENT_PAGE < LAST_PAGE) {
                creditNoteList.add(null)
                supplierCreditNoteAdapter.notifyItemInserted(creditNoteList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getSupplierComplaints(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSupplierComplaints(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getSupplierCreditNotes(supplierData?.id!!, c)
        RetrofitClient.apiCall(call, this, "GetSupplierComplaints")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetSupplierComplaints") {
            handleResponse(jsonObject)
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


    private fun handleResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model =
            gson.fromJson(jsonObject.toString(), SupplierCreditNoteModel::class.java)

        if (creditNoteList.size > 0) {
            creditNoteList.removeAt(creditNoteList.size - 1)
            supplierCreditNoteAdapter.notifyItemRemoved(creditNoteList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        creditNoteList.addAll(model.data)


        if (creditNoteList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            scnRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            scnRecyclerView.visibility = View.VISIBLE
        }

        supplierCreditNoteAdapter.notifyDataSetChanged()
        isLoading = false
    }


}