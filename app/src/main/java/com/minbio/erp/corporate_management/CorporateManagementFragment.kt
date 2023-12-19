package com.minbio.erp.corporate_management

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.adapter.CorporateSearchAdapter
import com.minbio.erp.corporate_management.fragments.CorporateBankDetail
import com.minbio.erp.corporate_management.fragments.CorporateCorporate
import com.minbio.erp.corporate_management.fragments.CorporateProfile
import com.minbio.erp.corporate_management.models.CorporateUsers
import com.minbio.erp.corporate_management.models.CorporateUsersData
import com.minbio.erp.corporate_management.models.DesignationData
import com.minbio.erp.corporate_management.models.DesignationsModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.getLoginModel
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class CorporateManagementFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var designationsModel: DesignationsModel
    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private lateinit var corporateSearchRecyclerView: RecyclerView
    private lateinit var corporateSearchAdapter: CorporateSearchAdapter
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var btnAdd: TextView
    var corporateUserList: MutableList<CorporateUsersData?> = ArrayList()
    private lateinit var et_customer_search: EditText
    private lateinit var cust_profile: RadioButton
    private lateinit var cust_bank_detail: RadioButton
    private lateinit var cust_corporate: RadioButton
    private lateinit var corporateProfileHeaderLayout: LinearLayout
    private lateinit var corporateHeaderProfileImage: CircleImageView
    private lateinit var corporateHeaderCompanyImage: CircleImageView
    private lateinit var corporateHeaderProfileName: TextView
    private lateinit var corporateHeaderProfileDesignation: TextView
    private lateinit var corporateHeaderCompanyName: TextView
    private lateinit var tvNoData: TextView

    private var selectedCorporateUsersData: CorporateUsersData? = null
    private var fromEdit = false

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_corporate_mangement, container, false)

        (activity as MainActivity).setToolbarTitle(resources.getString(R.string.corporatePageTitle))

        loginModel = getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.corporate_management.split(",")

        initViews()
        setUpCustomerSearchRecycler()
        initScrollListener()

        setUpPermissions()

        return v;
    }


    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            AppUtils.showDialog(context!!)
            getDesignations()
            getUsers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_users)) {
                corporateSearchRecyclerView.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                AppUtils.showDialog(context!!)
                getUsers(0)
            } else {
                corporateSearchRecyclerView.visibility = View.GONE
                pullToRefresh.isEnabled = false
            }

            if (permissionsList.contains(PermissionKeys.create_company_users)) {
                btnAdd.visibility = View.VISIBLE
                cust_profile.isEnabled = true
                cust_profile.isClickable = true
            } else {
                btnAdd.visibility = View.GONE
                cust_profile.isEnabled = false
                cust_profile.isClickable = false
            }


            if (permissionsList.contains(PermissionKeys.view_company_profile)) {
                cust_corporate.isEnabled = true
                cust_corporate.isClickable = true
            } else {
                cust_corporate.isEnabled = false
                cust_corporate.isClickable = false
            }

            if (permissionsList.contains(PermissionKeys.view_company_bankdetail)) {
                cust_bank_detail.isEnabled = true
                cust_bank_detail.isClickable = true
            } else {
                cust_bank_detail.isEnabled = false
                cust_bank_detail.isClickable = false
            }
        }
    }

    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.pull_to_refresh_corporate)
        pullToRefresh.setOnRefreshListener {
            getUsers(0)
        }

        et_customer_search = v.findViewById(R.id.et_customer_search)
        et_customer_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                corporateSearchAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        btnAdd = v.findViewById(R.id.btn_add_corporate_user)
        btnAdd.setOnClickListener {
            selectedCorporateUsersData = null
            corporateSearchAdapter.selectedID = -1
            corporateProfileHeaderLayout.visibility = View.INVISIBLE
            corporateSearchAdapter.notifyDataSetChanged()
            fromEdit = false
            cust_profile.callOnClick()
        }

        cust_profile = v.findViewById(R.id.cust_profile)
        cust_corporate = v.findViewById(R.id.cust_corporate)
        cust_bank_detail = v.findViewById(R.id.cust_bank_detail)

        corporateProfileHeaderLayout = v.findViewById(R.id.corporateProfileHeaderLayout)
        corporateHeaderProfileName = v.findViewById(R.id.corporateHeaderProfileName)
        corporateHeaderProfileDesignation = v.findViewById(R.id.corporateHeaderProfileDesignation)
        corporateHeaderProfileImage = v.findViewById(R.id.corporateHeaderProfileImage)
        corporateHeaderCompanyImage = v.findViewById(R.id.corporateHeaderCompanyImage)
        corporateHeaderCompanyName = v.findViewById(R.id.corporateHeaderCompanyName)
        tvNoData = v.findViewById(R.id.tvNoData)

        corporateHeaderCompanyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(corporateHeaderCompanyImage)

        cust_profile.setOnClickListener {
            updateView(cust_profile)
        }
        cust_corporate.setOnClickListener {
            updateView(cust_corporate)
        }
        cust_bank_detail.setOnClickListener {
            updateView(cust_bank_detail)
        }
    }

    private fun setUpCustomerSearchRecycler() {
        corporateSearchRecyclerView = v.findViewById(R.id.corporate_search_recycler_view)
        corporateSearchAdapter = CorporateSearchAdapter(this, corporateUserList)
        corporateSearchRecyclerView.layoutManager =
            LinearLayoutManager(context)
        corporateSearchRecyclerView.adapter = corporateSearchAdapter
    }

    private fun initScrollListener() {
        corporateSearchRecyclerView.addOnScrollListener(object :
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
                corporateSearchAdapter.notifyItemInserted(corporateUserList.size - 1)

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

    private fun updateView(view: RadioButton) {

        cust_profile.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        cust_corporate.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        cust_bank_detail.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cust_profile.setCompoundDrawables(profile_drawable, null, null, null);

        var corporate_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_corporate)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cust_corporate.setCompoundDrawables(corporate_drawable, null, null, null);

        var bank_detail_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_bank_detail)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cust_bank_detail.setCompoundDrawables(bank_detail_drawable, null, null, null);

        when (view.id) {
            R.id.cust_profile -> {
                cust_profile.isChecked = true

                cust_profile.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cust_profile.setCompoundDrawables(profile_drawable, null, null, null);

                launchFragment(CorporateProfile())

            }
            R.id.cust_corporate -> {
                cust_corporate.isChecked = true

                cust_corporate.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                corporate_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_corporate_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cust_corporate.setCompoundDrawables(corporate_drawable, null, null, null);

                launchFragment(CorporateCorporate())
            }
            R.id.cust_bank_detail -> {
                cust_bank_detail.isChecked = true

                cust_bank_detail.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                bank_detail_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_bank_detail_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cust_bank_detail.setCompoundDrawables(bank_detail_drawable, null, null, null);

                launchFragment(CorporateBankDetail())
            }
        }
    }

    private fun launchFragment(fragment: Fragment) {

        if (fragment is CorporateProfile) {
            val bundle = Bundle()
            bundle.putParcelable("CorporateUserData", selectedCorporateUsersData)
            bundle.putBoolean("fromEdit", fromEdit)
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.corporate_management_fragment, fragment)
                .commit()

        } else {
            childFragmentManager.beginTransaction()
                .replace(R.id.corporate_management_fragment, fragment)
                .commit()
        }
    }

    fun userClick(
        position: Int,
        cud: CorporateUsersData?
    ) {
        fromEdit = true
        selectedCorporateUsersData = cud
        cust_profile.callOnClick()

        corporateProfileHeaderLayout.visibility = View.VISIBLE
        corporateHeaderProfileName.text = cud?.first_name
        corporateHeaderProfileDesignation.text = cud?.designation

        Glide
            .with(context!!)
            .load(cud?.image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(corporateHeaderProfileImage)
    }

    fun getUsers(currentPage: Int) {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCorporateUsers(currentPage)
        RetrofitClient.apiCall(call, this, "CorporateUsers")
    }

    private fun getDesignations() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getDesignations()
        RetrofitClient.apiCall(call, this, "Designations")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        if (tag == "CorporateUsers") {
            AppUtils.dismissDialog()
            handleCorporateResponse(jsonObject)
        } else if (tag == "Designations") {
            val gson = Gson()
            designationsModel =
                gson.fromJson(jsonObject.toString(), DesignationsModel::class.java)
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
            corporateSearchAdapter.notifyItemRemoved(corporateUserList.size)
        }


        CURRENT_PAGE = corporateUsersModel.meta.current_page
        LAST_PAGE = corporateUsersModel.meta.last_page

        corporateUserList.addAll(corporateUsersModel.data)

        if (corporateUserList.isNullOrEmpty()) {
            corporateSearchRecyclerView.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            corporateSearchRecyclerView.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        corporateSearchAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun getDesignationsFromCorporateActivity(): List<DesignationData> = designationsModel.data


}