package com.minbio.erp.financial_management.bank_cash.fragments.financial

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.bank_cash.adapters.FinancialAccountListAdapter
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListData
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class FinancialAccountListFragment : Fragment(), ResponseCallBack {

    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var falRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var tvNoData: TextView
    private lateinit var search: ImageView
    private lateinit var addBankAccount: ImageView
    private lateinit var financialAccountListAdapter: FinancialAccountListAdapter
    private lateinit var faltvBalance: TextView

    var financialAccountList: MutableList<FinancialAccountListData?> = ArrayList()

    var bankAccountToSearch = ""
    var labelToSearch = ""
    var numberToSearch = ""
    var statusToSearch = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_account_list, container, false)

        initViews()
        setAdapter()
        initScrollListener()

        financialAccountList.clear()
        AppUtils.showDialog(context!!)
        getFinancialAccountList()

        return v
    }

    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        tvNoData = v.findViewById(R.id.tvNoData)
        search = v.findViewById(R.id.search)
        addBankAccount = v.findViewById(R.id.addBankAccount)
        faltvBalance = v.findViewById(R.id.faltvBalance)
        faltvBalance.text = context!!.resources.getString(
            R.string.falLabelBalance,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        search.setOnClickListener {
            AppUtils.preventTwoClick(search)
            val searchDialog = FinancialAccountSearchDialog(context!!, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }

        addBankAccount.setOnClickListener {
            AppUtils.preventTwoClick(addBankAccount)
            val fragment =
                NewFinancialAccountFragment()
            val bundle = Bundle()
            bundle.putBoolean("fromMain", false)
            fragment.arguments = bundle
            (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
        }

        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            bankAccountToSearch = ""
            labelToSearch = ""
            numberToSearch = ""
            statusToSearch = ""

            financialAccountList.clear()
            getFinancialAccountList()
        }
    }

    private fun setAdapter() {
        falRecyclerView = v.findViewById(R.id.falRecyclerView)
        financialAccountListAdapter = FinancialAccountListAdapter(financialAccountList, this)
        falRecyclerView.layoutManager = LinearLayoutManager(context)
        falRecyclerView.adapter = financialAccountListAdapter

    }


    private fun initScrollListener() {
        falRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == financialAccountList.size - 1) {
                        recyclerView.post {
                            try {
                                if (CURRENT_PAGE < LAST_PAGE) {
                                    CURRENT_PAGE++
                                    getFinancialAccountList()
                                    Handler().postDelayed({

                                    }, 1000)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
    }

    fun getFinancialAccountList() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getFinancialAccountList(
            CURRENT_PAGE, bankAccountToSearch, labelToSearch, numberToSearch, statusToSearch
        )
        RetrofitClient.apiCall(call, this, "GetFinancialAccountList")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetFinancialAccountList") {
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
        pullToRefresh.isRefreshing = false
        val model = Gson().fromJson(jsonObject.toString(), FinancialAccountListModel::class.java)

        if (financialAccountList.size > 0) {
            financialAccountList.removeAt(financialAccountList.size - 1)
            financialAccountListAdapter.notifyItemRemoved(financialAccountList.size)
        }

        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        financialAccountList.addAll(model.data)

        if (financialAccountList.isNullOrEmpty()) {
            falRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            falRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }


        if (CURRENT_PAGE < LAST_PAGE) {
            financialAccountList.add(null)
            financialAccountListAdapter.notifyItemInserted(financialAccountList.size - 1)
        }

        financialAccountListAdapter.notifyDataSetChanged()
    }

    fun editAccount(position: Int) {
        val fragment =
            NewFinancialAccountFragment()
        val bundle = Bundle()
        bundle.putBoolean("fromMain", false)
        bundle.putParcelable("data", financialAccountList[position])
        fragment.arguments = bundle
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }


}