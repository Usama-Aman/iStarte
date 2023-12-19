package com.minbio.erp.product_management.fragments

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
import com.minbio.erp.product_management.adapter.ProCustomerComplaintAdapter
import com.minbio.erp.product_management.model.ProCustomerComplaintData
import com.minbio.erp.product_management.model.ProCustomerComplaintModel
import com.minbio.erp.utils.AppUtils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProCustomerComplaintFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var complaintRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var complaintAdapter: ProCustomerComplaintAdapter

    private var complaintList: MutableList<ProCustomerComplaintData?> = ArrayList()

    private var varietyId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_product_cust_complaint, container, false)

        initViews()
        setAdapter()
        initScrollListener()

        return v
    }

    private fun initViews() {
        varietyId = arguments!!.getInt("varietyId")
        tvNoData = v.findViewById(R.id.tvNoData)

        if (varietyId != 0)
            getComplaintData(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )

    }

    private fun setAdapter() {
        complaintRecyclerView = v.findViewById(R.id.complaintRecyclerView)
        complaintAdapter = ProCustomerComplaintAdapter(complaintList)
        complaintRecyclerView.layoutManager = LinearLayoutManager(context)
        complaintRecyclerView.adapter = complaintAdapter

    }


    private fun initScrollListener() {
        complaintRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == complaintList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            complaintList.add(null)
            complaintAdapter.notifyItemInserted(complaintList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getComplaintData(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                complaintRecyclerView.post {
                    complaintList.removeAt(complaintList.size - 1)
                    complaintAdapter.notifyItemRemoved(complaintList.size)
                }
            }
            complaintAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getComplaintData(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProCustomerComplaint(varietyId, c)
        RetrofitClient.apiCall(call, this, "GetCustomerComplaint")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomerComplaint") {
            handleOrderResponse(jsonObject)
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


    private fun handleOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model =
            gson.fromJson(jsonObject.toString(), ProCustomerComplaintModel::class.java)

        if (complaintList.size > 0) {
            complaintList.removeAt(complaintList.size - 1)
            complaintAdapter.notifyItemRemoved(complaintList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        complaintList.addAll(model.data)

        if (complaintList.isNullOrEmpty()) {
            complaintRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            complaintRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        complaintAdapter.notifyDataSetChanged()
        isLoading = false
    }

}