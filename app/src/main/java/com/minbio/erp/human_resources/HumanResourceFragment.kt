package com.minbio.erp.human_resources

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.minbio.erp.corporate_management.fragments.CorporateProfile
import com.minbio.erp.corporate_management.models.CorporateUsers
import com.minbio.erp.corporate_management.models.CorporateUsersData
import com.minbio.erp.human_resources.adapters.HRUserAdapter
import com.minbio.erp.human_resources.fragments.*
import com.minbio.erp.human_resources.models.ExpensesCompanyUser
import com.minbio.erp.human_resources.models.ExpensesListData
import com.minbio.erp.human_resources.models.LeaveCompanyUser
import com.minbio.erp.human_resources.models.LeavesListData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class HumanResourceFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    lateinit var userTab: RadioButton
    lateinit var signatureTab: RadioButton
    lateinit var profileTab: RadioButton
    lateinit var leaveTab: RadioButton
    lateinit var expenseTab: RadioButton
    lateinit var payTab: RadioButton

    private lateinit var userRecycler: RecyclerView
    private lateinit var hrUserAdapter: HRUserAdapter
    lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var btnAdd: TextView
    private lateinit var hrCompanyName: TextView
    private lateinit var hrCompanyImage: CircleImageView
    private lateinit var etSearch: EditText

    private var fromEdit: Boolean = false
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var selectedCorporateUsersData: CorporateUsersData? = null
    var corporateUserList: MutableList<CorporateUsersData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_human_resource, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        (activity as MainActivity).setToolbarTitle(resources.getString(R.string.hrPageTitle))

        initViews()
        setAdapter()
        initScrollListener()
        setUpPermissions()


        return v
    }

    private fun setUpPermissions() {

        if (permissionsList.contains(PermissionKeys.view_company_users)) {
            profileTab.isEnabled = true
            pullToRefresh.isEnabled = true
            getUsers(0)
        } else {
            profileTab.isEnabled = false
            pullToRefresh.isEnabled = false
        }

        leaveTab.isEnabled = permissionsList.contains(PermissionKeys.view_leaves)
        expenseTab.isEnabled = permissionsList.contains(PermissionKeys.view_expenses)
        signatureTab.isEnabled = permissionsList.contains(PermissionKeys.verify_company_users)


        if (loginModel.data.designation_key == "hr_management") {
            profileTab.visibility = View.VISIBLE
            signatureTab.visibility = View.VISIBLE
        } else {
            profileTab.visibility = View.GONE
            signatureTab.visibility = View.GONE
            leaveTab.background =
                ContextCompat.getDrawable(context!!, R.drawable.tab_left_button_selector)
        }

    }

    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.hrUserSwipe)
        pullToRefresh.setOnRefreshListener {
            getUsers(0)
        }

        userTab = v.findViewById(R.id.hrTabUsers)
        profileTab = v.findViewById(R.id.hrTabProfile)
        signatureTab = v.findViewById(R.id.hrTabSignature)
        leaveTab = v.findViewById(R.id.hrTabLeave)
        expenseTab = v.findViewById(R.id.hrTabExpense)
        payTab = v.findViewById(R.id.hrTabPaySlip)

        profileTab.setOnClickListener { updateView(profileTab) }
        signatureTab.setOnClickListener { updateView(signatureTab) }
        leaveTab.setOnClickListener { updateView(leaveTab) }
        expenseTab.setOnClickListener { updateView(expenseTab) }
        payTab.setOnClickListener { updateView(payTab) }

        etSearch = v.findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                hrUserAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        hrCompanyName = v.findViewById(R.id.hrCompanyName)
        hrCompanyImage = v.findViewById(R.id.hrCompanyImage)

        hrCompanyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(hrCompanyImage)
    }

    private fun updateView(view: RadioButton?) {

        profileTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        signatureTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        leaveTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        expenseTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        payTab.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        profileTab.setCompoundDrawables(drawable, null, null, null);

        drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_tab_signature)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        signatureTab.setCompoundDrawables(drawable, null, null, null);

        drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_leave_tab)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        leaveTab.setCompoundDrawables(drawable, null, null, null);

        drawable =
            ContextCompat.getDrawable(context!!, R.drawable.ic_expense_tab)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        expenseTab.setCompoundDrawables(drawable, null, null, null);

        drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_payment_tab)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        payTab.setCompoundDrawables(drawable, null, null, null);

        when (view?.id) {
            R.id.hrTabProfile -> {
                profileTab.isChecked = true

                profileTab.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                profileTab.setCompoundDrawables(drawable, null, null, null);

                launchFragment(CorporateProfile())
            }
            R.id.hrTabSignature -> {
                signatureTab.isChecked = true

                signatureTab.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_tab_signature_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                signatureTab.setCompoundDrawables(drawable, null, null, null);

                launchFragment(HrSignatureFragment())
            }
            R.id.hrTabLeave -> {
                leaveTab.isChecked = true

                leaveTab.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_leave_tab_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                leaveTab.setCompoundDrawables(drawable, null, null, null);

                launchFragment(HrLeaveFragment())
            }
            R.id.hrTabExpense -> {
                expenseTab.isChecked = true

                expenseTab.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_expense_tab_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                expenseTab.setCompoundDrawables(
                    drawable, null, null, null
                )

                launchFragment(HrExpensesFragment())
            }
            R.id.hrTabPaySlip -> {
                payTab.isChecked = true

                payTab.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_pay_slip_tab_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                payTab.setCompoundDrawables(drawable, null, null, null)

//                launchFragment(CustomerCreditNote())
            }
        }

    }


    private fun setAdapter() {
        userRecycler = v.findViewById(R.id.hrUserRecycler)
        hrUserAdapter = HRUserAdapter(this, corporateUserList)
        userRecycler.layoutManager =
            LinearLayoutManager(context)
        userRecycler.adapter = hrUserAdapter
    }

    private fun initScrollListener() {
        userRecycler.addOnScrollListener(object :
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
                hrUserAdapter.notifyItemInserted(corporateUserList.size - 1)

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

    fun getUsers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCorporateUsers(c)
        RetrofitClient.apiCall(call, this, "getUsers")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "getUsers") {
            handleCorporateResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "getUsers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        if (tag == "getUsers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
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
            hrUserAdapter.notifyItemRemoved(corporateUserList.size)
        }


        CURRENT_PAGE = corporateUsersModel.meta.current_page
        LAST_PAGE = corporateUsersModel.meta.last_page

        corporateUserList.addAll(corporateUsersModel.data)


        hrUserAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun userClick(cud: CorporateUsersData?) {
        fromEdit = true
        selectedCorporateUsersData = cud
        if (loginModel.data.designation_key == "hr_management")
            if (permissionsList.contains(PermissionKeys.update_company_users))
                profileTab.callOnClick()
    }


    private fun launchFragment(fragment: Fragment) {
        val bundle = Bundle()
        when (fragment) {
            is CorporateProfile -> {
                if (selectedCorporateUsersData != null) {
                    bundle.putParcelable("CorporateUserData", selectedCorporateUsersData)
                    bundle.putBoolean("fromEdit", fromEdit)
                    fragment.arguments = bundle
                    childFragmentManager.beginTransaction()
                        .replace(R.id.hr_right_tab_fragment, fragment)
                        .commit()
                } else {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.hrErrorSelectUser),
                        false
                    )
                }
            }
            is HrSignatureFragment -> {
                if (selectedCorporateUsersData != null) {
                    bundle.putParcelable("CorporateUserData", selectedCorporateUsersData)
                    fragment.arguments = bundle
                    childFragmentManager.beginTransaction()
                        .replace(R.id.hr_right_tab_fragment, fragment)
                        .commit()

                } else {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.hrErrorSelectUser),
                        false
                    )
                }
            }
            else -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.hr_right_tab_fragment, fragment)
                    .commit()
            }
        }
    }

    fun leaveItemClick(
        leavesListData: LeavesListData?,
        leaveCompanyUser: ArrayList<LeaveCompanyUser>
    ) {
        val fragment = HrAddLeaveFragment()
        val bundle = Bundle()
        bundle.putBoolean("isHR", true)
        bundle.putParcelable("leaveData", leavesListData)
        bundle.putParcelableArrayList("leaveCompanyUsers", leaveCompanyUser)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }

    fun expenseItemClick(
        expenseData: ExpensesListData?,
        companyUser: ArrayList<ExpensesCompanyUser>
    ) {
        val fragment = HrAddExpenseFragment()
        val bundle = Bundle()
        bundle.putBoolean("isHR", true)
        bundle.putParcelable("expenseData", expenseData)
        bundle.putParcelableArrayList("expenseCompanyUser", companyUser)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }

    fun leaveDetail(
        leavesListData: LeavesListData?,
        leaveCompanyUser: ArrayList<LeaveCompanyUser>
    ) {
        val fragment = HrLeaveDetailFragment()
        val bundle = Bundle()
        bundle.putBoolean("isHR", true)
        bundle.putParcelable("leaveData", leavesListData)
        bundle.putParcelableArrayList("leaveCompanyUsers", leaveCompanyUser)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }

    fun expenseDetail(
        expenseListData: ExpensesListData?,
        expenseCompanyUser: ArrayList<ExpensesCompanyUser>
    ) {
        val fragment = HrExpenseDetailFragment()
        val bundle = Bundle()
        bundle.putBoolean("isHR", true)
        bundle.putParcelable("expenseData", expenseListData)
        bundle.putParcelableArrayList("expenseCompanyUser", expenseCompanyUser)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }

    fun addLeave(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putBoolean("isHR", false)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }

    fun addExpense(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putBoolean("isHR", false)
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.hr_right_tab_fragment, fragment)
            .commit()
    }


}