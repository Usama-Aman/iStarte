package com.minbio.erp.cashier_desk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minbio.erp.main.MainActivity
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.cashier_desk.fragments.*
import com.minbio.erp.cashier_desk.models.CDOrderDetail
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView

class CashierDeskFragment : Fragment() {

    private lateinit var v: View

    lateinit var cashierTabOrder: RadioButton
    lateinit var cashierTabCustomer: RadioButton
    lateinit var cashierTabPayment: RadioButton
    lateinit var cashierTabBalance: RadioButton
    lateinit var cashierTabCreditNote: RadioButton

    private lateinit var cashierCompanyImage: CircleImageView
    private lateinit var cashierCompanyName: TextView
    private lateinit var customerProileImage: CircleImageView
    lateinit var customerLayout: LinearLayout
    lateinit var headerPayLayout: ConstraintLayout
    private lateinit var cashierPaymentStatus: TextView
    private lateinit var cashierBalanceAmount: TextView
    private lateinit var cashierOverdraftAmount: TextView
    private lateinit var customerProfileName: TextView
    private lateinit var customerSirenNo: TextView

    private var selectedDetails: CDOrderDetail? = null
    private var selectedCustomerId: Int = 0
    private var selectedOrderNo: String = ""

    private var whichTab = 0    //0 for Order Tab, 1 for Customer List Tab

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_cashier_desk, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.cashier_desk.split(",")

        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.cashierPageTitle))

        initViews()
        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            cashierTabBalance.isEnabled =
                permissionsList.contains(PermissionKeys.view_customer_balance)
            cashierTabCreditNote.isEnabled =
                permissionsList.contains(PermissionKeys.create_creditnote)
        }
    }

    private fun disableTabs(b: Boolean) {
        cashierTabPayment.isEnabled = b
        cashierTabBalance.isEnabled = b
        cashierTabCreditNote.isEnabled = b
    }

    private fun initViews() {
        cashierTabOrder = v.findViewById(R.id.cashierTabOrder)
        cashierTabCustomer = v.findViewById(R.id.cashierTabCustomer)
        cashierTabPayment = v.findViewById(R.id.cashierTabPayment)
        cashierTabBalance = v.findViewById(R.id.cashierTabBalance)
        cashierTabCreditNote = v.findViewById(R.id.cashierTabCreditNote)

        cashierTabOrder.setOnClickListener { updateLeftTabs(cashierTabOrder) }
        cashierTabCustomer.setOnClickListener { updateLeftTabs(cashierTabCustomer) }

        cashierTabPayment.setOnClickListener { updateRightTabs(cashierTabPayment) }
        cashierTabBalance.setOnClickListener { updateRightTabs(cashierTabBalance) }
        cashierTabCreditNote.setOnClickListener { updateRightTabs(cashierTabCreditNote) }

        cashierPaymentStatus = v.findViewById(R.id.cashierPaymentStatus)
        cashierBalanceAmount = v.findViewById(R.id.cashierBalanceAmount)
        cashierOverdraftAmount = v.findViewById(R.id.cashierOverdraftAmount)
        customerLayout = v.findViewById(R.id.customerLayout)
        headerPayLayout = v.findViewById(R.id.headerPayLayout)
        cashierCompanyImage = v.findViewById(R.id.cashierCompanyImage)
        customerProileImage = v.findViewById(R.id.cusProfile)
        cashierCompanyName = v.findViewById(R.id.cashierCompanyName)
        customerProfileName = v.findViewById(R.id.customerProfileName)
        customerSirenNo = v.findViewById(R.id.customerSirenNo)

        cashierCompanyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(cashierCompanyImage)

        cashierTabOrder.callOnClick()
    }

    private fun updateLeftTabs(view: RadioButton) {
        cashierTabOrder.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        cashierTabCustomer.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cashierTabOrder.setCompoundDrawables(order_drawable, null, null, null);

        var customer_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cashierTabCustomer.setCompoundDrawables(customer_drawable, null, null, null);

        if (view.id == R.id.cashierTabOrder) {
            cashierTabOrder.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorDarkBlue
                )
            )
            order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order_sel)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
            cashierTabOrder.setCompoundDrawables(order_drawable, null, null, null)

            whichTab = 0
            launchLeftTabsFragment(CashierDeskOrder())

        } else if (view.id == R.id.cashierTabCustomer) {
            cashierTabCustomer.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorDarkBlue
                )
            )
            customer_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
            cashierTabCustomer.setCompoundDrawables(customer_drawable, null, null, null);

            whichTab = 1
            launchLeftTabsFragment(CashierDeskCustomer())
        }

    }

    private fun updateRightTabs(view: RadioButton?) {

        cashierTabPayment.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        cashierTabBalance.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        cashierTabCreditNote.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var pay_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_payment_tab)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cashierTabPayment.setCompoundDrawables(pay_drawable, null, null, null);

        var balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cashierTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

        var credit_note_drawable =
            ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        cashierTabCreditNote.setCompoundDrawables(credit_note_drawable, null, null, null);



        when (view?.id) {
            R.id.cashierTabPayment -> {
                cashierTabPayment.isChecked = true

                cashierTabPayment.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                pay_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_payment_tab_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cashierTabPayment.setCompoundDrawables(pay_drawable, null, null, null);

                launchRightTabsFragment(CashierDeskPayment())

            }
            R.id.cashierTabBalance -> {
                cashierTabBalance.isChecked = true

                cashierTabBalance.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                balance_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_eblance_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cashierTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

                launchRightTabsFragment(CashierDeskBalance())
            }
            R.id.cashierTabCreditNote -> {
                cashierTabCreditNote.isChecked = true

                cashierTabCreditNote.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                credit_note_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                cashierTabCreditNote.setCompoundDrawables(
                    credit_note_drawable,
                    null,
                    null,
                    null
                );

                launchRightTabsFragment(CashierDeskCreditNote())
            }
        }

    }

    private fun launchLeftTabsFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.cashier_left_tab_fragment, fragment)
            .commit()
    }

    private fun launchRightTabsFragment(fragment: Fragment) {
        if (selectedCustomerId != 0) {
            val bundle = Bundle()
            if (fragment is CashierDeskPayment) {
                bundle.putParcelable("details", selectedDetails)
                bundle.putString("orderNo", selectedOrderNo)
                fragment.arguments = bundle
            } else if (fragment is CashierDeskBalance) {
                bundle.putInt("customerId", selectedCustomerId)
                fragment.arguments = bundle
            } else if (fragment is CashierDeskCreditNote) {
                bundle.putInt("customerId", selectedCustomerId)
                fragment.arguments = bundle
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.cashier_right_tab_fragment, fragment)
                .commit()
        } else {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSelectCashierOrderFirst),
                false
            )
        }
    }

    fun onListItemClicked(
        orderNo: String?,
        details: CDOrderDetail?
    ) {
        selectedOrderNo = orderNo!!
        selectedCustomerId = details!!.customer_id.toInt()
        selectedDetails = details

        customerLayout.visibility = View.VISIBLE
        headerPayLayout.visibility = View.VISIBLE

        Glide
            .with(context!!)
            .load(details.customer_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(customerProileImage)

        cashierPaymentStatus.text = details.payment_status
        cashierBalanceAmount.text = AppUtils.appendCurrency(details.customer_balance, context!!)
        customerSirenNo.text = details.customer_siret_no
        customerProfileName.text = details.customer_name
        cashierOverdraftAmount.text =
            AppUtils.appendCurrency(details.customer_pending_overdraft, context!!)

        cashierTabPayment.callOnClick()
    }

    fun reloadLeftTabs() {
        if (whichTab == 0)
            updateLeftTabs(cashierTabOrder)
        else if (whichTab == 1)
            updateLeftTabs(cashierTabCustomer)

    }


}
