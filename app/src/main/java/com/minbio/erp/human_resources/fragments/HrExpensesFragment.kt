package com.minbio.erp.human_resources.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.human_resources.HumanResourceFragment
import com.minbio.erp.human_resources.adapters.HrExpensesAdapter
import com.minbio.erp.human_resources.models.ExpensesListData
import com.minbio.erp.human_resources.models.ExpensesListModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class HrExpensesFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var hrExpensesRecycler: RecyclerView
    private lateinit var hrExpensesAdapter: HrExpensesAdapter
    private lateinit var addExpense: ImageView
    private lateinit var hrELTvAmount: TextView
    private lateinit var tvNoData: TextView

    private var expensesList: MutableList<ExpensesListData?> = ArrayList()
    private lateinit var expensesModel: ExpensesListModel

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>
    private var isHR = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_expenses, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        initViews()
        setUpAdapter()
        initScrollListener()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            if (permissionsList.contains(PermissionKeys.create_expenses))
                addExpense.visibility = View.VISIBLE
            else
                addExpense.visibility = View.GONE
        }
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        hrExpensesRecycler = v.findViewById(R.id.hrExpensesRecycler)
        addExpense = v.findViewById(R.id.addExpense)
        hrELTvAmount = v.findViewById(R.id.hrELTvAmount)
        hrELTvAmount.text = context!!.resources.getString(
            R.string.hrELLabelAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        addExpense.setOnClickListener {
            (parentFragment as HumanResourceFragment).addExpense(HrAddExpenseFragment())
        }
        isHR = loginModel.data.designation_key == "hr_management"

        getExpenses(0)
    }

    private fun setUpAdapter() {
        hrExpensesAdapter = HrExpensesAdapter(this, expensesList)
        hrExpensesRecycler.layoutManager = LinearLayoutManager(context)
        hrExpensesRecycler.adapter = hrExpensesAdapter
    }

    private fun initScrollListener() {
        hrExpensesRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == expensesList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            expensesList.add(null)
            hrExpensesAdapter.notifyItemInserted(expensesList.size - 1)
            if (CURRENT_PAGE < LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getExpenses(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                hrExpensesRecycler.post {
                    expensesList.removeAt(expensesList.size - 1)
                    hrExpensesAdapter.notifyItemRemoved(expensesList.size)
                }
            }
            hrExpensesAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getExpenses(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = if (isHR)
            api.getExpenses(0, c)
        else
            api.getExpenses(loginModel.data.id, c)
        RetrofitClient.apiCall(call, this, "GetExpenses")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetExpenses") {
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
        expensesModel =
            gson.fromJson(jsonObject.toString(), ExpensesListModel::class.java)

        if (expensesList.size > 0) {
            expensesList.removeAt(expensesList.size - 1)
            hrExpensesAdapter.notifyItemRemoved(expensesList.size)
        }


        CURRENT_PAGE = expensesModel.meta.current_page
        LAST_PAGE = expensesModel.meta.last_page

        expensesList.addAll(expensesModel.data)

        if (expensesList.isNullOrEmpty()) {
            hrExpensesRecycler.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            hrExpensesRecycler.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        hrExpensesAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun itemClick(expensesListData: ExpensesListData?) {
        if (loginModel.data.designation_key == "hr_management") {
            when (expensesListData?.status) {
                "Pending" -> {

                    if (permissionsList.contains(PermissionKeys.assign_expenses))
                        (parentFragment as HumanResourceFragment).expenseItemClick(
                            expensesListData,
                            expensesModel.company_users
                        )
                }
                else -> {
                    (parentFragment as HumanResourceFragment).expenseDetail(
                        expensesListData,
                        expensesModel.company_users
                    )
                }
            }
        } else {
            (parentFragment as HumanResourceFragment).expenseDetail(
                expensesListData,
                expensesModel.company_users
            )
        }
    }

}