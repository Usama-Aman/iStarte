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
import com.minbio.erp.accounting.adapter.AccountingBalanceInventoryAdapter
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.product_management.model.InventoryListData
import com.minbio.erp.product_management.model.InventoryModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class AccountingBalanceInventory : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var accountingBalanceInventoryAdapter: AccountingBalanceInventoryAdapter
    private lateinit var balanceRecyclerView: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var aibtvBuyingCost: TextView
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var selectedId = 0


    var inventoryList: MutableList<InventoryListData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_balance_inventory, container, false)

        initViews()
        setupAdapter()
        initScrollListener()

        return v
    }

    private fun initViews() {
        selectedId = arguments?.getInt("selectedId")!!

        balanceRecyclerView = v.findViewById(R.id.acb_recycler_view)
        tvNoData = v.findViewById(R.id.tvNoData)
        aibtvBuyingCost = v.findViewById(R.id.aibtvBuyingCost)
        aibtvBuyingCost.text = context!!.resources.getString(
            R.string.aibLabelBuyingCost,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        if (selectedId != 0)
            getInventoryList(0)
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )

    }

    private fun setupAdapter() {
        accountingBalanceInventoryAdapter = AccountingBalanceInventoryAdapter(inventoryList)
        balanceRecyclerView.layoutManager = LinearLayoutManager(context)
        balanceRecyclerView.adapter = accountingBalanceInventoryAdapter
    }


    private fun initScrollListener() {
        balanceRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == inventoryList.size - 1) {
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
                inventoryList.add(null)
                accountingBalanceInventoryAdapter.notifyItemInserted(inventoryList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getInventoryList(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInventoryList(c: Int) {
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getInventoryList(selectedId, c)
        RetrofitClient.apiCall(call, this, "GetInventory")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetInventory") {
            handleGetInventoryResponse(jsonObject)
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

    private fun handleGetInventoryResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val inventoryData =
            gson.fromJson(jsonObject.toString(), InventoryModel::class.java)

        if (inventoryList.size > 0) {
            inventoryList.removeAt(inventoryList.size - 1)
            accountingBalanceInventoryAdapter.notifyItemRemoved(inventoryList.size)
        }


        CURRENT_PAGE = inventoryData.meta.current_page
        LAST_PAGE = inventoryData.meta.last_page

        inventoryList.addAll(inventoryData.data)

        if (inventoryList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            balanceRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            balanceRecyclerView.visibility = View.VISIBLE
        }

        accountingBalanceInventoryAdapter.notifyDataSetChanged()
        isLoading = false
    }



}