package com.minbio.erp.accounting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.minbio.erp.main.MainActivity
import com.minbio.erp.R
import com.minbio.erp.accounting.fragments.*
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import kotlinx.android.synthetic.main.activity_accounting.*

class AccountingFragment : Fragment() {

    private var whichTab: Int =
        0  //0 for customerList , 1 for supplier list, 2 for inventory, 3 for corporate Balance

    private lateinit var v: View

    private lateinit var accountingTabCustomerList: RadioButton
    private lateinit var accountingTabSupplierList: RadioButton
    private lateinit var accountingTabInventory: RadioButton
    private lateinit var accountingTabBalance: RadioButton
    private lateinit var accountingTabFileTransfer: RadioButton
    private lateinit var accountingTabCreditNote: RadioButton
    private lateinit var accounting_right_tabs_fragment: LinearLayout

    private var selectedId = 0
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_accounting, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.accounting.split(",")

        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.accountingPageTitle))
        initViews()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            accountingTabBalance.isEnabled =
                permissionsList.contains(PermissionKeys.view_inventory_balance)
            accountingTabCreditNote.isEnabled =
                permissionsList.contains(PermissionKeys.create_creditnote)
            accountingTabFileTransfer.isEnabled =
                permissionsList.contains(PermissionKeys.suppliers_setting)

        }
    }

    private fun initViews() {
        accounting_right_tabs_fragment = v.findViewById(R.id.accounting_right_tabs_fragment)
        accountingTabCustomerList = v.findViewById(R.id.accountingTabCustomerList)
        accountingTabSupplierList = v.findViewById(R.id.accountingTabSupplierList)
        accountingTabInventory = v.findViewById(R.id.accountingTabInventory)
        accountingTabBalance = v.findViewById(R.id.accountingTabBalance)
        accountingTabFileTransfer = v.findViewById(R.id.accountingTabFileTransfer)
        accountingTabCreditNote = v.findViewById(R.id.accountingTabCreditNote)

        accountingTabCustomerList.setOnClickListener { updateLeftViews(accountingTabCustomerList) }
        accountingTabSupplierList.setOnClickListener { updateLeftViews(accountingTabSupplierList) }
        accountingTabInventory.setOnClickListener { updateLeftViews(accountingTabInventory) }

        accountingTabBalance.setOnClickListener { updateRightViews(accountingTabBalance) }
        accountingTabFileTransfer.setOnClickListener { updateRightViews(accountingTabFileTransfer) }
        accountingTabCreditNote.setOnClickListener { updateRightViews(accountingTabCreditNote) }

        updateLeftViews(accountingTabCustomerList)
    }

    private fun updateLeftViews(view: RadioButton) {
        selectedId = 0
        accounting_right_tabs_fragment.removeAllViews()

        accountingTabCustomerList.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        accountingTabSupplierList.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        accountingTabInventory.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var cl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabCustomerList.setCompoundDrawables(cl_drawable, null, null, null)


        var sl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_supplier)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabSupplierList.setCompoundDrawables(sl_drawable, null, null, null)


        var inv_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_inventory)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabInventory.setCompoundDrawables(inv_drawable, null, null, null)

        when (view.id) {
            R.id.accountingTabCustomerList -> {
                accountingTabCustomerList.isChecked = true
                whichTab = 0

                accountingTabCustomerList.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                cl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabCustomerList.setCompoundDrawables(cl_drawable, null, null, null);

                launchLeftFragment(AccountingCustomerList())
                accountingTabBalance.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_left_right_button_selector)
                accountingTabBalance.visibility = View.VISIBLE
                accountingTabCreditNote.visibility = View.GONE
                accountingTabFileTransfer.visibility = View.GONE

            }
            R.id.accountingTabSupplierList -> {
                accountingTabSupplierList.isChecked = true
                whichTab = 1

                accountingTabSupplierList.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                sl_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_supplier_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabSupplierList.setCompoundDrawables(sl_drawable, null, null, null)

                launchLeftFragment(AccountingSupplierList())

                accountingTabBalance.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_left_button_selector)
                accountingTabFileTransfer.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_mid_button_selector)
                accountingTabCreditNote.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_right_button_selector)
                accountingTabBalance.visibility = View.VISIBLE
                accountingTabCreditNote.visibility = View.VISIBLE
                accountingTabFileTransfer.visibility = View.VISIBLE


            }
            R.id.accountingTabInventory -> {
                accountingTabInventory.isChecked = true
                whichTab = 2

                accountingTabInventory.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                inv_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_inventory_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabInventory.setCompoundDrawables(inv_drawable, null, null, null)

                launchLeftFragment(AccountingInventory())
                accountingTabBalance.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_left_right_button_selector)
                accountingTabBalance.visibility = View.VISIBLE
                accountingTabCreditNote.visibility = View.GONE
                accountingTabFileTransfer.visibility = View.GONE

            }
        }
    }

    private fun updateRightViews(view: RadioButton) {
        accountingTabBalance.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        accountingTabFileTransfer.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
        accountingTabCreditNote.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabBalance.setCompoundDrawables(balance_drawable, null, null, null)


        var file_transfer_drawable =
            ContextCompat.getDrawable(context!!, R.drawable.ic_file_transfer)
                ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabFileTransfer.setCompoundDrawables(file_transfer_drawable, null, null, null)


        var credit_note_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        accountingTabCreditNote.setCompoundDrawables(credit_note_drawable, null, null, null)



        when (view.id) {
            R.id.accountingTabBalance -> {
                accountingTabBalance.isChecked = true

                accountingTabBalance.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

                when (whichTab) {
                    0 -> launchRightFragment(AccountingBalanceCL())
                    1 -> launchRightFragment(AccountingBalanceSL())
                    2 -> launchRightFragment(AccountingBalanceInventory())
                }

            }
            R.id.accountingTabFileTransfer -> {
                accountingTabFileTransfer.isChecked = true

                accountingTabFileTransfer.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                file_transfer_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_file_transfer_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabFileTransfer.setCompoundDrawables(
                    file_transfer_drawable,
                    null,
                    null,
                    null
                )

                launchRightFragment(AccountingFileTransfer())
            }
            R.id.accountingTabCreditNote -> {
                accountingTabCreditNote.isChecked = true

                accountingTabCreditNote.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                credit_note_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_credit_note_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                accountingTabCreditNote.setCompoundDrawables(credit_note_drawable, null, null, null)

                launchRightFragment(AccountingCreditNote())
            }
        }
    }

    private fun launchLeftFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.accounting_left_tabs_fragment, fragment)
            .commit()
    }

    private fun launchRightFragment(fragment: Fragment) {
        val bundle = Bundle()
        if (selectedId != 0) {
            bundle.putInt("selectedId", selectedId)
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.accounting_right_tabs_fragment, fragment)
                .commit()
        } else {

            when (whichTab) {
                0 -> AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorAccountingSelectCustomerID),
                    false
                )
                1 -> AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorAccountingSelectSupplierID),
                    false
                )
                2 -> AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorAccountingSelectVerityID),
                    false
                )
            }
        }
    }

    fun leftItemClick(i: Int) {
        selectedId = i
        if (loginModel.data.designation_id == 0)
            accountingTabBalance.callOnClick()
        else {
            if (permissionsList.contains(PermissionKeys.view_inventory_balance))
                accountingTabBalance.callOnClick()
        }
    }

}