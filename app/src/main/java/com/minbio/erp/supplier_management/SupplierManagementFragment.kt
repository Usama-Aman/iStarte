package com.minbio.erp.supplier_management

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
import com.google.gson.Gson
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.supplier_management.adapter.SupplierSearchAdapter
import com.minbio.erp.supplier_management.fragments.*
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.supplier_management.models.SuppliersModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SupplierManagementFragment : Fragment(), ResponseCallBack {

    private var fromEdit: Boolean = false
    private lateinit var v: View
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private var selectedSupplierData: SuppliersData? = null

    private lateinit var supplierSearchRecycler: RecyclerView
    private lateinit var supplierSearchAdapter: SupplierSearchAdapter
    lateinit var pullToRefresh: SwipeRefreshLayout

    private lateinit var supplierTabProfile: RadioButton
    private lateinit var supplierTabBalance: RadioButton
    private lateinit var supplierTabOverdraft: RadioButton
    private lateinit var supplierTabSupplierComplaint: RadioButton
    private lateinit var supplierTabCreditNote: RadioButton

    private lateinit var btnAdd: TextView
    private lateinit var tvNoData: TextView
    private lateinit var et_supplier_search: EditText

    var suppliersList: MutableList<SuppliersData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_supplier_management, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.supplier_management.split(",")

        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.supplierPageTitle))

        initViews()
        setUpSupplierSearchRecycler()
        initScrollListener()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getSuppliers(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_company_suppliers)) {
                supplierSearchRecycler.visibility = View.VISIBLE
                pullToRefresh.isEnabled = true
                getSuppliers(0)
            } else {
                supplierSearchRecycler.visibility = View.GONE
                pullToRefresh.isEnabled = false
            }

            if (permissionsList.contains(PermissionKeys.create_company_suppliers)) {
                btnAdd.visibility = View.VISIBLE
                supplierTabProfile.isEnabled = true
                supplierTabProfile.isClickable = true
            } else {
                btnAdd.visibility = View.GONE
                supplierTabProfile.isEnabled = false
                supplierTabProfile.isClickable = false
            }

            supplierTabBalance.isEnabled =
                permissionsList.contains(PermissionKeys.view_customer_balance)
            supplierTabSupplierComplaint.isEnabled =
                permissionsList.contains(PermissionKeys.view_complaint)
            supplierTabCreditNote.isEnabled =
                permissionsList.contains(PermissionKeys.view_creditnote)
        }
    }

    private fun initViews() {
        pullToRefresh = v.findViewById(R.id.pull_to_refresh_supplier)
        pullToRefresh.setOnRefreshListener { getSuppliers(0) }

        supplierTabProfile = v.findViewById(R.id.supplierTabProfile)
        supplierTabBalance = v.findViewById(R.id.supplierTabBalance)
        supplierTabOverdraft = v.findViewById(R.id.supplierTabOverdraft)
        supplierTabSupplierComplaint = v.findViewById(R.id.supplierTabSupplierComplaint)
        supplierTabCreditNote = v.findViewById(R.id.supplierTabCreditNote)
        tvNoData = v.findViewById(R.id.tvNoData)

        supplierTabProfile.setOnClickListener { updateView(supplierTabProfile) }
        supplierTabBalance.setOnClickListener { updateView(supplierTabBalance) }
        supplierTabOverdraft.setOnClickListener { updateView(supplierTabOverdraft) }
        supplierTabSupplierComplaint.setOnClickListener { updateView(supplierTabSupplierComplaint) }
        supplierTabCreditNote.setOnClickListener { updateView(supplierTabCreditNote) }

        btnAdd = v.findViewById(R.id.supplier_btn_add)
        btnAdd.setOnClickListener {
            selectedSupplierData = null
            supplierSearchAdapter.selectedID = -1
            supplierSearchAdapter.notifyDataSetChanged()
            fromEdit = false
            supplierTabProfile.callOnClick()
        }

        et_supplier_search = v.findViewById(R.id.et_supplier_search)
        et_supplier_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                supplierSearchAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun updateView(view: RadioButton?) {

        supplierTabProfile.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        supplierTabBalance.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        supplierTabOverdraft.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        supplierTabSupplierComplaint.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        supplierTabCreditNote.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        var profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        supplierTabProfile.setCompoundDrawables(profile_drawable, null, null, null);

        var balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        supplierTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

        var overdraft_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_overdraft)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        supplierTabOverdraft.setCompoundDrawables(overdraft_drawable, null, null, null);


        var complaint_drawable =
            ContextCompat.getDrawable(context!!, R.drawable.ic_customer_complaint)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        supplierTabSupplierComplaint.setCompoundDrawables(complaint_drawable, null, null, null);

        var credit_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        supplierTabCreditNote.setCompoundDrawables(credit_drawable, null, null, null);

        when (view?.id) {
            R.id.supplierTabProfile -> {
                supplierTabProfile.isChecked = true

                supplierTabProfile.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                supplierTabProfile.setCompoundDrawables(profile_drawable, null, null, null);

                launchFragment(SupplierProfile())

            }
            R.id.supplierTabBalance -> {
                supplierTabBalance.isChecked = true

                supplierTabBalance.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                supplierTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

                launchFragment(SupplierBalance())
            }
            R.id.supplierTabOverdraft -> {
                supplierTabOverdraft.isChecked = true

                supplierTabOverdraft.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                overdraft_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_overdraft_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                supplierTabOverdraft.setCompoundDrawables(overdraft_drawable, null, null, null);

                launchFragment(SupplierOverdraft())
            }
            R.id.supplierTabSupplierComplaint -> {
                supplierTabSupplierComplaint.isChecked = true

                supplierTabSupplierComplaint.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                complaint_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_customer_complaint_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                supplierTabSupplierComplaint.setCompoundDrawables(
                    complaint_drawable, null, null, null
                )

                launchFragment(SupplierComplaint())
            }
            R.id.supplierTabCreditNote -> {
                supplierTabCreditNote.isChecked = true

                supplierTabCreditNote.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                credit_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                supplierTabCreditNote.setCompoundDrawables(credit_drawable, null, null, null)

                launchFragment(SupplierCreditNote())
            }
        }

    }

    private fun setUpSupplierSearchRecycler() {
        supplierSearchRecycler = v.findViewById(R.id.corporate_search_recycler_view)
        supplierSearchAdapter = SupplierSearchAdapter(this, suppliersList)
        supplierSearchRecycler.layoutManager =
            LinearLayoutManager(context!!)
        supplierSearchRecycler.adapter = supplierSearchAdapter
    }


    private fun initScrollListener() {
        supplierSearchRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == suppliersList.size - 1) {
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
                suppliersList.add(null)
                supplierSearchAdapter.notifyItemInserted(suppliersList.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getSuppliers(CURRENT_PAGE + 1)
                    }
                }, 1000)
            }
            supplierSearchAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun launchFragment(fragment: Fragment) {
        val bundle = Bundle()
        if (fragment is SupplierProfile) {
            bundle.putParcelable("SupplierData", selectedSupplierData)
            bundle.putBoolean("fromEdit", fromEdit)
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.supplier_management_fragment, fragment)
                .commit()

        } else {
            if (selectedSupplierData != null) {
                bundle.putParcelable("SupplierData", selectedSupplierData)
                bundle.putBoolean("fromEdit", fromEdit)
                fragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.supplier_management_fragment, fragment)
                    .commit()
            } else {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorSupplierSelect),
                    false
                )
            }

        }
    }

    fun getSuppliers(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getSuppliers(c)
        RetrofitClient.apiCall(call, this, "GetSuppliers")
    }

    fun userClick(
        sd: SuppliersData
    ) {
        fromEdit = true
        selectedSupplierData = sd
        supplierTabProfile.callOnClick()
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetSuppliers") {
            handleSupplierResponse(jsonObject)
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

    private fun handleSupplierResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val supplierModel =
            gson.fromJson(jsonObject.toString(), SuppliersModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            suppliersList.clear()
        }

        if (suppliersList.size > 0) {
            suppliersList.removeAt(suppliersList.size - 1)
            supplierSearchAdapter.notifyItemRemoved(suppliersList.size)
        }


        CURRENT_PAGE = supplierModel.meta.current_page
        LAST_PAGE = supplierModel.meta.last_page

        suppliersList.addAll(supplierModel.data)

        if (suppliersList.isNullOrEmpty()) {
            tvNoData.visibility = View.VISIBLE
            supplierSearchRecycler.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            supplierSearchRecycler.visibility = View.VISIBLE
        }

        supplierSearchAdapter.notifyDataSetChanged()
        isLoading = false
    }

}