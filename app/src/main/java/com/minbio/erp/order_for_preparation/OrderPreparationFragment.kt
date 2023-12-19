package com.minbio.erp.order_for_preparation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.order_for_preparation.adapter.OrderPrepRightDetailAdapter
import com.minbio.erp.order_for_preparation.adapter.OrderPrepTabsAdapter
import com.minbio.erp.order_for_preparation.fragments.OrderPrepCustomerFragment
import com.minbio.erp.order_for_preparation.fragments.OrderPrepOrderPending
import com.minbio.erp.order_for_preparation.models.OPRightTabs
import com.minbio.erp.order_for_preparation.models.OrderDetails
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject

class OrderPreparationFragment : Fragment(), ResponseCallBack {

    private var opRightTabs: MutableList<OPRightTabs> = ArrayList()
    private var pendingOrderId = 0
    private var pendingListPosition = 0

    private var orderDetails: MutableList<OrderDetails> = ArrayList()

    private lateinit
    var v: View

    private lateinit var orderPrepRightDetailAdapter: OrderPrepRightDetailAdapter
    private lateinit var orderPrepTabsAdapter: OrderPrepTabsAdapter
    private lateinit var orderPrepDetailRecycler: RecyclerView
    private lateinit var orderPrepRightTabRecycler: RecyclerView

    private lateinit var opTabCustomerList: RadioButton
    private lateinit var opTabOrderPending: RadioButton
    private lateinit var optvTotal: TextView
    private var selectedLeftTab = ""

    private lateinit var detailHeaderLayout: LinearLayout
    private lateinit var btnSend: LinearLayout

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_order_for_preparation, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.order_preparation.split(",")

        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.opPageTitle))

        initViews()
        setupAdapter()
        setupRightTabAdapter()


        return v
    }


    private fun initViews() {
        opTabCustomerList = v.findViewById(R.id.opTabCustomerList)
        opTabOrderPending = v.findViewById(R.id.opTabOrderPending)
        optvTotal = v.findViewById(R.id.optvTotal)
        optvTotal.text = context!!.resources.getString(
            R.string.opPoTotal,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        detailHeaderLayout = v.findViewById(R.id.detailHeaderLayout)
        btnSend = v.findViewById(R.id.btn_order_prep_send)
        btnSend.setOnClickListener {
            AppUtils.showDialog(context!!)
            val api = RetrofitClient.getClient(context!!).create(Api::class.java)
            val call = api.changeOrderStatus(pendingOrderId, "Ready")
            RetrofitClient.apiCall(call, this, "ChangeStatus")
        }

        opTabCustomerList.setOnClickListener { updateRightTabs(opTabCustomerList) }
        opTabOrderPending.setOnClickListener { updateRightTabs(opTabOrderPending) }

        updateRightTabs(opTabCustomerList)
    }

    private fun setupAdapter() {
        orderPrepDetailRecycler = v.findViewById(R.id.orderPrepDetailRecycler)
        orderPrepRightDetailAdapter = OrderPrepRightDetailAdapter(orderDetails)
        orderPrepDetailRecycler.layoutManager = LinearLayoutManager(context!!)
        orderPrepDetailRecycler.adapter = orderPrepRightDetailAdapter
    }

    private fun updateRightTabs(view: RadioButton) {

        opTabCustomerList.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        opTabOrderPending.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var cl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        opTabCustomerList.setCompoundDrawables(cl_drawable, null, null, null);

        var op_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order_pending)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        opTabOrderPending.setCompoundDrawables(op_drawable, null, null, null);

        if (view.id == R.id.opTabCustomerList) {
            opTabCustomerList.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorDarkBlue
                )
            )
            cl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
            opTabCustomerList.setCompoundDrawables(cl_drawable, null, null, null)

            selectedLeftTab = "Customer"
            launchLeftTabsFragment(OrderPrepCustomerFragment())

        } else if (view.id == R.id.opTabOrderPending) {
            opTabOrderPending.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorDarkBlue
                )
            )
            op_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order_pending_sel)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
            opTabOrderPending.setCompoundDrawables(op_drawable, null, null, null);

            selectedLeftTab = "Pending"
            launchLeftTabsFragment(OrderPrepOrderPending())
        }

    }

    private fun launchLeftTabsFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.op_left_tabs_fragment, fragment)
            .commit()
    }

    private fun setupRightTabAdapter() {
        orderPrepRightTabRecycler = v.findViewById(R.id.orderPrepRightTabRecycler)
        orderPrepTabsAdapter = OrderPrepTabsAdapter(this, opRightTabs)
        orderPrepRightTabRecycler.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        orderPrepRightTabRecycler.adapter = orderPrepTabsAdapter
    }


    fun generateRightTabs(
        id: Int?,
        orderNo: String,
        details: List<OrderDetails>
    ) {

        if (!opRightTabs.contains(OPRightTabs(id!!, orderNo, details))) {

            if (details.isNotEmpty())
                opRightTabs.add(OPRightTabs(id!!, orderNo, details))
            else
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.opErrorNoOrderItems),
                    false
                )

            if (opRightTabs.size == 1)
                orderPrepTabsAdapter.selected = 0

            orderPrepTabsAdapter.notifyDataSetChanged()


        }
    }

    fun showOrderDetail(opRightTabs: OPRightTabs, position: Int) {
        pendingOrderId = opRightTabs.pendingListId
        pendingListPosition = position

        orderDetails.clear()
        orderDetails.addAll(opRightTabs.details)
        orderPrepRightDetailAdapter.notifyDataSetChanged()

        if (orderDetails.isEmpty()) {
            detailHeaderLayout.visibility = View.GONE
            btnSend.visibility - View.GONE
        } else {
            detailHeaderLayout.visibility = View.VISIBLE
            btnSend.visibility = View.VISIBLE

            if (permissionsList.contains(PermissionKeys.update_orders))
                btnSend.visibility = View.VISIBLE
            else
                btnSend.visibility = View.GONE
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        if (tag == "ChangeStatus") {
            opRightTabs.removeAt(pendingListPosition)
            orderPrepTabsAdapter.notifyDataSetChanged()
            detailHeaderLayout.visibility = View.GONE
            btnSend.visibility = View.GONE

            orderDetails.clear()
            orderPrepRightDetailAdapter.notifyDataSetChanged()


            if (selectedLeftTab == "Customer")
                launchLeftTabsFragment(OrderPrepCustomerFragment())
            else
                launchLeftTabsFragment(OrderPrepOrderPending())
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

}