package com.minbio.erp.financial_management.accounting.fragments.bank_accounts

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.gson.Gson
//import com.minbio.erp.R
//import com.minbio.erp.financial_management.FinancialManagementFragment
//import com.minbio.erp.financial_management.bank_cash.adapters.FinancialAccountListAdapter
//import com.minbio.erp.financial_management.bank_cash.fragments.financial.NewFinancialAccountFragment
//import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListData
//import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListModel
//import com.minbio.erp.network.Api
//import com.minbio.erp.network.ResponseCallBack
//import com.minbio.erp.network.RetrofitClient
//import com.minbio.erp.utils.AppUtils
//import org.json.JSONObject
//import java.util.*
//import kotlin.collections.ArrayList
//
//class BankAccountsFragment : Fragment(), ResponseCallBack {
//
//    /*Financial Account and bank accounts were same so using the same UI and classes*/
//
//    private var LAST_PAGE: Int = 1
//    private var CURRENT_PAGE: Int = 1
//    private var isLoading: Boolean = false
//
//    private lateinit var v: View
//    private lateinit var falRecyclerView: RecyclerView
//    private lateinit var financialAccountListAdapter: FinancialAccountListAdapter
//
//    private var financialAccountList: MutableList<FinancialAccountListData?> = ArrayList()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        v = inflater.inflate(R.layout.fragment_financial_account_list, container, false)
//
//        setAdapter()
//        initScrollListener()
//        getFinancialAccountList(1)
//
//        return v
//    }
//
//    private fun setAdapter() {
//        falRecyclerView = v.findViewById(R.id.falRecyclerView)
//        financialAccountListAdapter = FinancialAccountListAdapter(financialAccountList)
//        falRecyclerView.layoutManager = LinearLayoutManager(context)
//        falRecyclerView.adapter = financialAccountListAdapter
//
//    }
//
//
//    private fun initScrollListener() {
//        falRecyclerView.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val linearLayoutManager =
//                    recyclerView.layoutManager as LinearLayoutManager?
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == financialAccountList.size - 1) {
//                        recyclerView.post { loadMore() }
//                        isLoading = true
//                    }
//                }
//            }
//        })
//    }
//
//    private fun loadMore() {
//        try {
//            if (CURRENT_PAGE < LAST_PAGE) {
//                financialAccountList.add(null)
//                financialAccountListAdapter.notifyItemInserted(financialAccountList.size - 1)
//
//                Timer().schedule(object : TimerTask() {
//                    override fun run() {
//                        getFinancialAccountList(CURRENT_PAGE + 1)
//                    }
//                }, 1000)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun getFinancialAccountList(c: Int) {
//        AppUtils.showDialog(context!!)
//        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
//        val call = api.getFinancialAccountList(c)
//        RetrofitClient.apiCall(call, this, "GetFinancialAccountList")
//    }
//
//    override fun onSuccess(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        if (tag == "GetFinancialAccountList") {
//            handleOrderResponse(jsonObject)
//        }
//    }
//
//    override fun onError(jsonObject: JSONObject, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, jsonObject.getString("message"), false)
//    }
//
//
//    override fun onException(message: String?, tag: String) {
//        AppUtils.dismissDialog()
//        AppUtils.showToast(activity, message!!, false)
//    }
//
//
//    private fun handleOrderResponse(jsonObject: JSONObject) {
//        val gson = Gson()
//        val model =
//            gson.fromJson(jsonObject.toString(), FinancialAccountListModel::class.java)
//
//        if (financialAccountList.size > 0) {
//            financialAccountList.removeAt(financialAccountList.size - 1)
//            financialAccountListAdapter.notifyItemRemoved(financialAccountList.size)
//        }
//
//
//        CURRENT_PAGE = model.meta.current_page
//        LAST_PAGE = model.meta.last_page
//
//        financialAccountList.addAll(model.data)
//
//        financialAccountListAdapter.notifyDataSetChanged()
//        isLoading = false
//    }
//
//
//}