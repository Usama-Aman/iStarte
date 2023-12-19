package com.minbio.erp.financial_management.bank_cash.fragments.financial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.bank_cash.adapters.FinancialEntriesAdapter
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashEntriesData
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashEntriesModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class FinancialEntriesFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var addEntry: ImageView
    private lateinit var faeRecyclerView: RecyclerView
    private lateinit var financialEntriesAdapter: FinancialEntriesAdapter
    private lateinit var headerLinear: LinearLayout
    private lateinit var divider: View
    private lateinit var faetvDebit: TextView
    private lateinit var faetvCredit: TextView

    private var bankCashEntriesData: MutableList<BankCashEntriesData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_entries, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.accounting.split(",")

        initViews()
        setAdapter()
        initScrollListener()
        bankCashEntriesData.clear()

        AppUtils.showDialog(context!!)
        getEntries()

        return v
    }

    private fun initViews() {
        addEntry = v.findViewById(R.id.addEntry)
        headerLinear = v.findViewById(R.id.headerLinear)
        divider = v.findViewById(R.id.divider)
        faetvDebit =  v.findViewById(R.id.faetvDebit)
        faetvCredit =  v.findViewById(R.id.faetvCredit)
        faetvDebit.text = context!!.resources.getString(
            R.string.faeLabelDebit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faetvCredit.text = context!!.resources.getString(
            R.string.faeLabelCredit,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        addEntry.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                FinancialAddEntryFragment()
            )
        }

        if (permissionsList.contains(PermissionKeys.miscellaneous_payments)) {
            headerLinear.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
        } else {
            headerLinear.visibility = View.GONE
            divider.visibility = View.GONE
        }
    }


    private fun setAdapter() {
        faeRecyclerView = v.findViewById(R.id.faeRecyclerView)
        financialEntriesAdapter = FinancialEntriesAdapter(bankCashEntriesData)
        faeRecyclerView.layoutManager = LinearLayoutManager(context)
        faeRecyclerView.adapter = financialEntriesAdapter

    }


    private fun initScrollListener() {
        faeRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == bankCashEntriesData.size - 1) {
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
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getEntries()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getEntries() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getFinancialEntries(CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetbankCashEntriesData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetbankCashEntriesData") {
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
            gson.fromJson(jsonObject.toString(), BankCashEntriesModel::class.java)

        if (bankCashEntriesData.size > 0) {
            bankCashEntriesData.removeAt(bankCashEntriesData.size - 1)
            financialEntriesAdapter.notifyItemRemoved(bankCashEntriesData.size)
        }


        CURRENT_PAGE = model.meta.current_page
        LAST_PAGE = model.meta.last_page

        bankCashEntriesData.addAll(model.data)

        if (CURRENT_PAGE < LAST_PAGE)
            bankCashEntriesData.add(null)

        financialEntriesAdapter.notifyDataSetChanged()
        isLoading = false
    }


}