package com.minbio.erp.accounting.fragments

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
import com.minbio.erp.accounting.adapter.AccountingBalanceSLAdapter
import com.minbio.erp.supplier_management.models.SupplierBalanceData
import com.minbio.erp.supplier_management.models.SupplierBalanceModel
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class AccountingBalanceSL : Fragment(), ResponseCallBack {
    
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false
    
    private lateinit var v: View
    private lateinit var accountingBalanceSLAdapter: AccountingBalanceSLAdapter
    private lateinit var balanceRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var sbtvDebit: TextView
    private lateinit var sbtvCredit: TextView
    private lateinit var sbtvOverdraft: TextView
    private lateinit var sbtvTotalAmount: TextView

    private var selectedId = 0

    private var supplierBalanceList: MutableList<SupplierBalanceData?> = ArrayList()
    private var supplierData: SuppliersData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_balance_sl, container, false)

        initViews()
        setupAdapter()
        initScrollListener()


        return v
    }

    private fun initViews() {
        selectedId = arguments?.getInt("selectedId")!!
        balanceRecyclerView = v.findViewById(R.id.acb_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        sbtvDebit = v.findViewById(R.id.sbtvDebit)
        sbtvCredit = v.findViewById(R.id.sbtvCredit)
        sbtvOverdraft = v.findViewById(R.id.sbtvOverdraft)
        sbtvTotalAmount = v.findViewById(R.id.sbtvTotalAmount)

        sbtvDebit.text = context!!.resources.getString(
            R.string.sbLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sbtvCredit.text = context!!.resources.getString(
            R.string.sbLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sbtvOverdraft.text = context!!.resources.getString(
            R.string.sbLabelOverdue,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sbtvTotalAmount.text = context!!.resources.getString(
            R.string.sbLabelTotalAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        if (selectedId != 0)
            getSupplierBalance(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )
    }

    private fun setupAdapter() {
        accountingBalanceSLAdapter = AccountingBalanceSLAdapter(supplierBalanceList)
        balanceRecyclerView.layoutManager = LinearLayoutManager(context)
        balanceRecyclerView.adapter = accountingBalanceSLAdapter
    }

    private fun initScrollListener() {
        balanceRecyclerView.addOnScrollListener(object :
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
                accountingBalanceSLAdapter.notifyItemInserted(supplierBalanceList.size - 1)

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
        val call = api.getSupplierBalance(selectedId, c)
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
            accountingBalanceSLAdapter.notifyItemRemoved(supplierBalanceList.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        supplierBalanceList.addAll(model.data)

        if (supplierBalanceList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            balanceRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            balanceRecyclerView.visibility = View.VISIBLE
        }

        accountingBalanceSLAdapter.notifyDataSetChanged()
        isLoading = false
    }


}