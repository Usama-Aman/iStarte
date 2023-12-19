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
import com.minbio.erp.supplier_management.adapter.SupplierBalanceAdapter
import com.minbio.erp.supplier_management.models.SupplierBalanceData
import com.minbio.erp.supplier_management.models.SupplierBalanceModel
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SupplierBalance : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var supplierBalanceAdapter: SupplierBalanceAdapter
    private lateinit var sbRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var sctvTotalAmount: TextView
    private lateinit var sctvDebit: TextView
    private lateinit var sctvCredit: TextView
    private lateinit var sctvOverdraft: TextView

    private var supplierBalanceList: MutableList<SupplierBalanceData?> = ArrayList()

    private var supplierData: SuppliersData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_supplier_balance, container, false)

        initViews()
        setUpAdapter()
        initScrollListener()

        return v
    }

    private fun initViews() {
        supplierData = arguments!!.getParcelable("SupplierData")
        sbRecyclerView = v.findViewById(R.id.sb_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        sctvDebit = v.findViewById(R.id.sctvDebit)
        sctvCredit = v.findViewById(R.id.sctvCredit)
        sctvOverdraft = v.findViewById(R.id.sctvOverdraft)
        sctvTotalAmount = v.findViewById(R.id.sctvTotalAmount)

        sctvDebit.text = context!!.resources.getString(
            R.string.sbLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sctvCredit.text = context!!.resources.getString(
            R.string.sbLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sctvOverdraft.text = context!!.resources.getString(
            R.string.sbLabelOverdue,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sctvTotalAmount.text = context!!.resources.getString(
            R.string.sbLabelTotalAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        if (supplierData != null)
            getSupplierBalance(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }

    private fun setUpAdapter() {
        supplierBalanceAdapter = SupplierBalanceAdapter(this, supplierBalanceList)
        sbRecyclerView.layoutManager = LinearLayoutManager(context)
        sbRecyclerView.adapter = supplierBalanceAdapter
    }

    private fun initScrollListener() {
        sbRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == supplierBalanceList.size - 1) {
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
                supplierBalanceList.add(null)
                supplierBalanceAdapter.notifyItemInserted(supplierBalanceList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getSupplierBalance(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSupplierBalance(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getSupplierBalance(supplierData?.id!!, c)
        RetrofitClient.apiCall(call, this, "GetSupplierBalance")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetSupplierBalance") {
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
            gson.fromJson(jsonObject.toString(), SupplierBalanceModel::class.java)

        if (supplierBalanceList.size > 0) {
            supplierBalanceList.removeAt(supplierBalanceList.size - 1)
            supplierBalanceAdapter.notifyItemRemoved(supplierBalanceList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        supplierBalanceList.addAll(model.data)

        if (supplierBalanceList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            sbRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            sbRecyclerView.visibility = View.VISIBLE
        }

        supplierBalanceAdapter.notifyDataSetChanged()
        isLoading = false
    }


}