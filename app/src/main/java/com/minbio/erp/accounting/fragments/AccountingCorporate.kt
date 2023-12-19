package com.minbio.erp.accounting.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.accounting.AccountingFragment
import com.minbio.erp.accounting.adapter.AccountingCorporateAdapter
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.models.CorporateUsers
import com.minbio.erp.corporate_management.models.CorporateUsersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class AccountingCorporate : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var accountingCorporateAdapter: AccountingCorporateAdapter
    private lateinit var corporateRecyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var tvNoData: TextView

    var corporateUserList: MutableList<CorporateUsersData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_accounting_corporate, container, false)


        val gson = Gson()
        loginModel = gson.fromJson(
            SharedPreference.getSimpleString(context, Constants.userData),
            LoginModel::class.java
        )

//        permissionsList = loginModel.data.permissions.split(",")


        initViews()
        setupAdapter()
        initScrollListener()
//        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            AppUtils.showDialog(context!!)
            getUsers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_users)) {
                corporateRecyclerView.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                AppUtils.showDialog(context!!)
                getUsers(0)
            } else {
                corporateRecyclerView.visibility = View.GONE
                pullToRefresh.isEnabled = false
            }
        }
    }

    private fun initViews() {
        corporateRecyclerView = v.findViewById(R.id.acc_corporate__recycler_view)
        pullToRefresh = v.findViewById(R.id.acc_corporate_swipe)
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh.setOnRefreshListener { getUsers(0) }

        etSearch = v.findViewById(R.id.acc_corporate_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                accountingCorporateAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun setupAdapter() {
        accountingCorporateAdapter = AccountingCorporateAdapter(this, corporateUserList)
        corporateRecyclerView.layoutManager = LinearLayoutManager(context)
        corporateRecyclerView.adapter = accountingCorporateAdapter
    }

    private fun initScrollListener() {
        corporateRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == corporateUserList.size - 1) {
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
                corporateUserList.add(null)
                accountingCorporateAdapter.notifyItemInserted(corporateUserList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getUsers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUsers(currentPage: Int) {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCorporateUsers(currentPage)
        RetrofitClient.apiCall(call, this, "CorporateUsers")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        if (tag == "CorporateUsers") {
            AppUtils.dismissDialog()
            handleCorporateResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "CorporateUsers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }

    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    private fun handleCorporateResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val corporateUsersModel =
            gson.fromJson(jsonObject.toString(), CorporateUsers::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            corporateUserList.clear()
        }

        if (corporateUserList.size > 0) {
            corporateUserList.removeAt(corporateUserList.size - 1)
            accountingCorporateAdapter.notifyItemRemoved(corporateUserList.size)
        }


        CURRENT_PAGE = corporateUsersModel.meta.current_page
        LAST_PAGE = corporateUsersModel.meta.last_page

        corporateUserList.addAll(corporateUsersModel.data)

        if (corporateUserList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            corporateRecyclerView.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            corporateRecyclerView.visibility = View.VISIBLE
        }

        accountingCorporateAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun userClick(
        id: Int
    ) {
        (parentFragment as AccountingFragment).leftItemClick(id)
    }


}