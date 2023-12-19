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
import com.minbio.erp.human_resources.adapters.LeavesListAdapter
import com.minbio.erp.human_resources.models.LeavesListData
import com.minbio.erp.human_resources.models.LeavesListModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class HrLeaveFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View
    private lateinit var hrLeavesRecycler: RecyclerView
    private lateinit var addLeave: ImageView
    private lateinit var tvNoData: TextView
    private lateinit var leavesListAdapter: LeavesListAdapter

    private var isHR = false

    private var leavesList: MutableList<LeavesListData?> = ArrayList()
    private lateinit var leaveModel: LeavesListModel

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_leaves, container, false)

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
            if (permissionsList.contains(PermissionKeys.create_leaves))
                addLeave.visibility = View.VISIBLE
            else
                addLeave.visibility = View.GONE
        }
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        hrLeavesRecycler = v.findViewById(R.id.hrLeavesRecycler)
        addLeave = v.findViewById(R.id.addLeave)
        addLeave.setOnClickListener {
            (parentFragment as HumanResourceFragment).addLeave(HrAddLeaveFragment())
        }
        isHR = loginModel.data.designation_key == "hr_management"

        getLeaves(0)
    }

    private fun setUpAdapter() {
        leavesListAdapter = LeavesListAdapter(this, leavesList)
        hrLeavesRecycler.layoutManager = LinearLayoutManager(context)
        hrLeavesRecycler.adapter = leavesListAdapter
    }

    private fun initScrollListener() {
        hrLeavesRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == leavesList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            leavesList.add(null)
            leavesListAdapter.notifyItemInserted(leavesList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getLeaves(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                hrLeavesRecycler.post {
                    leavesList.removeAt(leavesList.size - 1)
                    leavesListAdapter.notifyItemRemoved(leavesList.size)
                }
            }
            leavesListAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLeaves(currentPage: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = if (isHR)
            api.getLeaves(0, currentPage)
        else
            api.getLeaves(loginModel.data.id, currentPage)
        RetrofitClient.apiCall(call, this, "GetLeaves")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetLeaves") {
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
        leaveModel =
            gson.fromJson(jsonObject.toString(), LeavesListModel::class.java)

        if (leavesList.size > 0) {
            leavesList.removeAt(leavesList.size - 1)
            leavesListAdapter.notifyItemRemoved(leavesList.size)
        }


        CURRENT_PAGE = leaveModel.meta.current_page
        LAST_PAGE = leaveModel.meta.last_page

        leavesList.addAll(leaveModel.data)

        if (leavesList.isNullOrEmpty()) {
            hrLeavesRecycler.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            hrLeavesRecycler.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        leavesListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun itemClick(leavesListData: LeavesListData?) {
        if (loginModel.data.designation_key == "hr_management") {
            when (leavesListData?.status) {
                "Pending" -> {
                    if (permissionsList.contains(PermissionKeys.assign_leaves))
                        (parentFragment as HumanResourceFragment).leaveItemClick(
                            leavesListData,
                            leaveModel.company_users
                        )
                }
                else -> {
                    (parentFragment as HumanResourceFragment).leaveDetail(
                        leavesListData,
                        leaveModel.company_users
                    )
                }
            }
        } else {
            (parentFragment as HumanResourceFragment).leaveDetail(
                leavesListData,
                leaveModel.company_users
            )
        }
    }


}