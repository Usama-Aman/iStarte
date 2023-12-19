package com.minbio.erp.customer_management

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
import com.minbio.erp.customer_management.adapter.CustomerSearchAdapter
import com.minbio.erp.customer_management.fragments.*
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.customer_management.models.CustomersModel
import com.minbio.erp.sales_user_interface.SalesFragment
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_customer_management.*
import org.json.JSONObject
import java.util.*

class CustomerManagementFragment : Fragment(), ResponseCallBack {

    private lateinit var btnAdd: TextView
    private var fromEdit: Boolean = false
    private var selectedCustomerData: CustomersData? = null
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View

    private lateinit var customerSearchRecycler: RecyclerView
    private lateinit var customerSearchAdapter: CustomerSearchAdapter
    lateinit var pullToRefresh: SwipeRefreshLayout

    private lateinit var customerCompanyImage: CircleImageView
    private lateinit var customerCompanyName: TextView
    private lateinit var tvNoData: TextView

    private lateinit var customerTabProfile: RadioButton
    private lateinit var customerTabBalance: RadioButton
    private lateinit var customerTabOverdraft: RadioButton
    private lateinit var customerTabOrder: RadioButton
    lateinit var customerTabCustomerComplaint: RadioButton
    private lateinit var customerTabCreditNote: RadioButton
    private lateinit var etCustomerSearch: EditText

    var customersList: MutableList<CustomersData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_customer_management, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.customer_management.split(",")

        (activity as MainActivity).setToolbarTitle(resources.getString(R.string.customerPageTitle))

        initViews()
        setUpCustomerSearchRecycler()
        initScrollListener()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getCustomers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_customers)) {
                customerSearchRecycler.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                getCustomers(0)
                customerTabProfile.isEnabled = true
                customerTabCustomerComplaint.isEnabled = true
                customerTabBalance.isEnabled = true
                customerTabOrder.isEnabled = true
            } else {
                customerSearchRecycler.visibility = View.GONE
                pullToRefresh.isEnabled = false

                customerTabProfile.isEnabled = false
                customerTabCustomerComplaint.isEnabled = false
                customerTabBalance.isEnabled = false
                customerTabOrder.isEnabled = false
            }

            if (permissionsList.contains(PermissionKeys.create_company_customers)) {
                btnAdd.visibility = View.VISIBLE
                customerTabProfile.isEnabled = true
            } else {
                btnAdd.visibility = View.GONE
                customerTabProfile.isEnabled = false
            }

            customerTabBalance.isEnabled =
                permissionsList.contains(PermissionKeys.view_customer_balance)
            customerTabCustomerComplaint.isEnabled =
                permissionsList.contains(PermissionKeys.create_complaint)
            customerTabCreditNote.isEnabled =
                permissionsList.contains(PermissionKeys.view_creditnote)
            customerTabOrder.isEnabled = permissionsList.contains(PermissionKeys.view_orders)

        }
    }

    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.pull_to_refresh_customer)
        pullToRefresh.setOnRefreshListener {
            getCustomers(0)
        }

        customerTabProfile = v.findViewById(R.id.customerTabProfile)
        customerTabBalance = v.findViewById(R.id.customerTabBalance)
        customerTabOverdraft = v.findViewById(R.id.customerTabOverdraft)
        customerTabOrder = v.findViewById(R.id.customerTabOrder)
        customerTabCustomerComplaint = v.findViewById(R.id.customerTabCustomerComplaint)
        customerTabCreditNote = v.findViewById(R.id.customerTabCreditNote)
        customerCompanyName = v.findViewById(R.id.customerCompanyName)
        customerCompanyImage = v.findViewById(R.id.customerCompanyImage)
        tvNoData = v.findViewById(R.id.tvNoData)


        customerTabProfile.setOnClickListener { updateView(customerTabProfile) }
        customerTabBalance.setOnClickListener { updateView(customerTabBalance) }
        customerTabOverdraft.setOnClickListener { updateView(customerTabOverdraft) }
        customerTabOrder.setOnClickListener { updateView(customerTabOrder) }
        customerTabCustomerComplaint.setOnClickListener { updateView(customerTabCustomerComplaint) }
        customerTabCreditNote.setOnClickListener { updateView(customerTabCreditNote) }

        btnAdd = v.findViewById(R.id.customer_add_button)
        btnAdd.setOnClickListener {
            selectedCustomerData = null
            customerSearchAdapter.selectedID = -1
            customerProfileHeaderLayout.visibility = View.INVISIBLE
            customerSearchAdapter.notifyDataSetChanged()
            fromEdit = false
            customerTabProfile.callOnClick()
        }

        customerCompanyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(customerCompanyImage)

        etCustomerSearch = v.findViewById(R.id.et_customer_search)
        etCustomerSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                customerSearchAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun updateView(view: RadioButton?) {

        customerTabProfile.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        customerTabBalance.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        customerTabOverdraft.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        customerTabOrder.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        customerTabCustomerComplaint.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        customerTabCreditNote.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabProfile.setCompoundDrawables(profile_drawable, null, null, null);

        var balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

        var overdraft_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_overdraft)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabOverdraft.setCompoundDrawables(overdraft_drawable, null, null, null);

        var order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabOrder.setCompoundDrawables(order_drawable, null, null, null);

        var complaint_drawable =
            ContextCompat.getDrawable(context!!, R.drawable.ic_customer_complaint)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabCustomerComplaint.setCompoundDrawables(complaint_drawable, null, null, null);

        var credit_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        customerTabCreditNote.setCompoundDrawables(credit_drawable, null, null, null);

        when (view?.id) {
            R.id.customerTabProfile -> {
                customerTabProfile.isChecked = true

                customerTabProfile.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabProfile.setCompoundDrawables(profile_drawable, null, null, null);

                launchFragment(CustomerProfile())

            }
            R.id.customerTabBalance -> {
                customerTabBalance.isChecked = true

                customerTabBalance.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

                launchFragment(CustomerBalance())
            }
            R.id.customerTabOverdraft -> {
                customerTabOverdraft.isChecked = true

                customerTabOverdraft.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                overdraft_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_overdraft_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabOverdraft.setCompoundDrawables(overdraft_drawable, null, null, null);

                launchFragment(CustomerOverdraft())
            }
            R.id.customerTabOrder -> {
                customerTabOrder.isChecked = true

                customerTabOrder.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabOrder.setCompoundDrawables(order_drawable, null, null, null);

                launchFragment(CustomerOrder())
            }
            R.id.customerTabCustomerComplaint -> {
                customerTabCustomerComplaint.isChecked = true

                customerTabCustomerComplaint.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                complaint_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_customer_complaint_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabCustomerComplaint.setCompoundDrawables(
                    complaint_drawable, null, null, null
                )

                launchFragment(CustomerComplaint())
            }
            R.id.customerTabCreditNote -> {
                customerTabCreditNote.isChecked = true

                customerTabCreditNote.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                credit_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                customerTabCreditNote.setCompoundDrawables(credit_drawable, null, null, null)

                launchFragment(CustomerCreditNote())
            }
        }

    }

    private fun setUpCustomerSearchRecycler() {
        customerSearchRecycler = v.findViewById(R.id.corporate_search_recycler_view)
        customerSearchAdapter = CustomerSearchAdapter(this, customersList)
        customerSearchRecycler.layoutManager =
            LinearLayoutManager(context)
        customerSearchRecycler.adapter = customerSearchAdapter
    }

    private fun initScrollListener() {
        customerSearchRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customersList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            customersList.add(null)
            customerSearchAdapter.notifyItemInserted(customersList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getCustomers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                customerSearchRecycler.post {
                    customersList.removeAt(customersList.size - 1)
                    customerSearchAdapter.notifyItemRemoved(customersList.size)
                }
            }
            customerSearchAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCustomers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomers(c)
        RetrofitClient.apiCall(call, this, "GetCustomers")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomers") {
            handleGetCustomerResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        if (tag == "GetCustomers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        if (tag == "GetCustomers") {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
        }
    }

    private fun handleGetCustomerResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val customerModel =
            gson.fromJson(jsonObject.toString(), CustomersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            customersList.clear()
        }

        if (customersList.size > 0) {
            customersList.removeAt(customersList.size - 1)
            customerSearchAdapter.notifyItemRemoved(customersList.size)
        }


        CURRENT_PAGE = customerModel.meta.current_page
        LAST_PAGE = customerModel.meta.last_page

        customersList.addAll(customerModel.data)

        if (customersList.isNullOrEmpty()) {
            customerSearchRecycler.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            customerSearchRecycler.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        }

        customerSearchAdapter.notifyDataSetChanged()
        isLoading = false
    }

    fun userClick(position: Int, cd: CustomersData) {
        fromEdit = true
        selectedCustomerData = cd
        customerTabProfile.callOnClick()

        customerProfileHeaderLayout.visibility = View.VISIBLE
        customerPayLayout.visibility = View.VISIBLE

        customerProfileName.text = cd.name
        customerSirenNo.text =
            context!!.resources.getString(R.string.customerLabelSiren, cd.siret_no.toString())

        customerPaymentStatus.text = selectedCustomerData?.payment_status
        customerBalanceAmount.text =
            AppUtils.appendCurrency(selectedCustomerData?.balance!!, context!!)
        customerOverdraftAmount.text =
            AppUtils.appendCurrency(selectedCustomerData?.pending_overdraft!!, context!!)

        Glide
            .with(context!!)
            .load(cd.image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(customerProfileImage)

    }

    fun addCustomerToSales(customerId: Int?) {
        val fragment = SalesFragment()
        val bundle = Bundle()
        bundle.putInt("customerId", customerId!!)
        fragment.arguments = bundle
        (activity as MainActivity).checkModulePermission(fragment)
    }

    private fun launchFragment(fragment: Fragment) {
        val bundle = Bundle()
        if (fragment is CustomerProfile) {
            bundle.putParcelable("CustomerData", selectedCustomerData)
            bundle.putBoolean("fromEdit", fromEdit)
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.customer_management_fragment, fragment)
                .commit()
        } else if (fragment is CustomerComplaint) {
            if (permissionsList.contains(PermissionKeys.create_complaint)) {
                if (selectedCustomerData != null) {
                    bundle.putParcelable("CustomerData", selectedCustomerData)
                    fragment.arguments = bundle
                    childFragmentManager.beginTransaction()
                        .replace(R.id.customer_management_fragment, fragment)
                        .commit()
                } else {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.errorCustomerSelect),
                        false
                    )
                }
            } else {
                (activity as MainActivity).showPermissionToast()
            }
        } else {
            if (selectedCustomerData != null) {
                bundle.putParcelable("CustomerData", selectedCustomerData)
                fragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.customer_management_fragment, fragment)
                    .commit()
            } else {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorCustomerSelect),
                    false
                )
            }
        }
    }

    fun updateOverdraft() {
        customerOverdraftAmount.text =
            AppUtils.appendCurrency("0.00", context!!)
    }


}
