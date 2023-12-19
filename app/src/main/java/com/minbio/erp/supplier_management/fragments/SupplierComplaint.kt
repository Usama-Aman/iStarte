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
import com.minbio.erp.supplier_management.adapter.SupplierComplaintAdapter
import com.minbio.erp.supplier_management.models.SupplierComplaintData
import com.minbio.erp.supplier_management.models.SupplierComplaintModel
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SupplierComplaint : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var supplierComplaintAdapter: SupplierComplaintAdapter
    private lateinit var scRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView


    private var supplierComplaintList: MutableList<SupplierComplaintData?> = ArrayList()

    private var supplierData: SuppliersData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_supplier_complaint, container, false)

        initViews()
        setUpAdapter()
        initScrollListener()

        return v
    }

    private fun initViews() {
        scRecyclerView = v.findViewById(R.id.sc_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        supplierData = arguments!!.getParcelable("SupplierData")

        if (supplierData != null)
            getSupplierComplaints(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }


    private fun setUpAdapter() {
        supplierComplaintAdapter = SupplierComplaintAdapter(supplierComplaintList)
        scRecyclerView.layoutManager = LinearLayoutManager(context)
        scRecyclerView.adapter = supplierComplaintAdapter
    }

    private fun initScrollListener() {
        scRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == supplierComplaintList.size - 1) {
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
                supplierComplaintList.add(null)
                supplierComplaintAdapter.notifyItemInserted(supplierComplaintList.size - 1)
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
        val call = api.getSupplierComplaints(supplierData?.id!!, c)
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
            gson.fromJson(jsonObject.toString(), SupplierComplaintModel::class.java)

        if (supplierComplaintList.size > 0) {
            supplierComplaintList.removeAt(supplierComplaintList.size - 1)
            supplierComplaintAdapter.notifyItemRemoved(supplierComplaintList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        supplierComplaintList.addAll(model.data)

        if (supplierComplaintList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            scRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            scRecyclerView.visibility = View.VISIBLE
        }

        supplierComplaintAdapter.notifyDataSetChanged()
        isLoading = false
    }


}