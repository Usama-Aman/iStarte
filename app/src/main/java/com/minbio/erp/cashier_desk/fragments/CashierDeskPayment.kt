package com.minbio.erp.cashier_desk.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.cashier_desk.CashierDeskFragment
import com.minbio.erp.cashier_desk.models.CDOrderDetail
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.room.AppDatabase
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import org.json.JSONObject

class CashierDeskPayment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var article_code_number: TextView
    private lateinit var tvCoverageAllowed: TextView

    private lateinit var cashPaymentConstraint: ConstraintLayout
    private lateinit var creditCardConstraint: ConstraintLayout
    private lateinit var chequePaymentConstraint: ConstraintLayout
    private lateinit var cashierOrderMainLayout: ConstraintLayout
    private lateinit var switchTakeCoverage: Switch

    private var orderDetails: CDOrderDetail? = null
    private var orderNo: String = ""
    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    var isCoverageAllowed = 0
    var overdraftLogs: String = "0"
    var overdraftAmount: String = "0"

    private var stripeToken = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_cashier_desk_payment, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.cashier_desk.split(",")


        initViews()
        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            creditCardConstraint.isEnabled = true
        } else {
            creditCardConstraint.isEnabled =
                permissionsList.contains(PermissionKeys.pay_orders_payment)
        }
    }

    private fun initViews() {
        article_code_number = v.findViewById(R.id.article_code_number)
        cashierOrderMainLayout = v.findViewById(R.id.cashierOrderMainLayout)
        cashPaymentConstraint = v.findViewById(R.id.cashPaymentConstraint)
        creditCardConstraint = v.findViewById(R.id.creditCardConstraint)
        creditCardConstraint.setOnClickListener {
            AppUtils.preventTwoClick(creditCardConstraint)
            showDialog()
        }
        chequePaymentConstraint = v.findViewById(R.id.chequePaymentConstraint)
        switchTakeCoverage = v.findViewById(R.id.switchTakeCoverage)
        tvCoverageAllowed = v.findViewById(R.id.tvCoverageAllowed)

        orderDetails = arguments?.getParcelable("details")
        orderNo = arguments?.getString("orderNo")!!

        if (orderDetails != null) {
            cashierOrderMainLayout.visibility = View.VISIBLE
            article_code_number.text = orderNo
        } else
            cashierOrderMainLayout.visibility = View.GONE

        switchTakeCoverage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                callCheckForCoverageApi()
            else {
                isCoverageAllowed = 0
                overdraftLogs = "0"
                overdraftAmount = "0"
                tvCoverageAllowed.visibility = View.GONE
            }
        }

    }

    private fun callCheckForCoverageApi() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.checkForCoverage(orderDetails?.id!!)
        RetrofitClient.apiCall(call, this, "CheckForCoverage")
    }

    fun showDialog() {
        if (orderDetails?.grandtotal!! > overdraftAmount) {
            val paymentDialog = PaymentDialog(
                context!!,
                orderDetails!!,
                this,
                null,
                "cashierDeskFragment"
            )
            paymentDialog.setOwnerActivity(activity!!)
            paymentDialog.show()
        } else {
            AppUtils.showDialog(context!!)
            postPayment()
        }
    }

    private fun reloadCashierOrders() {
        (parentFragment as CashierDeskFragment).reloadLeftTabs()
        cashierOrderMainLayout.visibility = View.GONE
        article_code_number.text = ""
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        when (tag) {
            "CheckForCoverage" -> {
                val data = jsonObject.getJSONObject("data")
                overdraftLogs = data.getString("overdraft_logs")
                overdraftAmount = data.getString("total_amount")
                isCoverageAllowed = 1
                tvCoverageAllowed.visibility = View.VISIBLE

                if (orderDetails?.grandtotal!! > overdraftAmount) {
                    val diff = orderDetails?.grandtotal!!.toDouble() - overdraftAmount.toDouble()
                    tvCoverageAllowed.text = context!!.resources.getString(
                        R.string.amountGreatorThanCoverage,
                        AppUtils.appendCurrency(overdraftAmount, context!!),
                        AppUtils.appendCurrency(AppUtils.round(diff, 2).toString(), context!!)
                    )
                } else {
                    tvCoverageAllowed.text = context!!.resources.getString(
                        R.string.amountLessThanCoverage,
                        AppUtils.appendCurrency(overdraftAmount, context!!)
                    )
                }
            }
            "PostPayment" -> {
                reloadCashierOrders()
                (parentFragment as CashierDeskFragment).customerLayout.visibility = View.INVISIBLE
                (parentFragment as CashierDeskFragment).headerPayLayout.visibility = View.INVISIBLE

                if (isCoverageAllowed == 1) {

                    val db = AppDatabase.getAppDataBase(context = context!!)
                    val gson = Gson()
                    val loginModel = gson.fromJson(
                        SharedPreference.getSimpleString(context, Constants.userData),
                        LoginModel::class.java
                    )

                    db?.salesDAO()?.updateSalesCustomerPendingOverdraft(
                        loginModel.data.id.toString(),
                        orderDetails?.customer_id.toString(),
                        overdraftAmount
                    )

                }

            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
        when (tag) {
            "CheckForCoverage" -> {
                switchTakeCoverage.isChecked = false
                isCoverageAllowed = 0
                tvCoverageAllowed.visibility = View.GONE
            }
        }
    }


    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
        when (tag) {
            "CheckForCoverage" -> {
                switchTakeCoverage.isChecked = false
                isCoverageAllowed = 0
                tvCoverageAllowed.visibility = View.GONE
            }
        }
    }

    fun initializedStripeToken(card: Card?) {
        AppUtils.showDialog(context!!)

        var stripe: Stripe? = null
        val cardToSave: Card? = card

        if (cardToSave == null) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.invalid_card),
                false
            )
            return
        }

        stripe = Stripe(context!!, context!!.resources.getString(R.string.test_stripe__key))

        stripe.createToken(
            cardToSave,
            object : ApiResultCallback<Token?> {
                override fun onSuccess(token: Token) {
                    val tokenIntent = Intent("TokenFromStripe")
                    tokenIntent.putExtra("tokenKey", token.id)
                    stripeToken = token.id
                    postPayment()
                }

                override fun onError(error: Exception) {
                    AppUtils.dismissDialog()
                    Log.d(
                        "Stripe Activity",
                        "onError: " + error.localizedMessage
                    )
                    AppUtils.showToast(activity, error.localizedMessage!!, false)
                }
            })
    }

    private fun postPayment() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postOrderPayment(
            orderDetails?.id!!,
            stripeToken,
            overdraftLogs,
            isCoverageAllowed
        )
        RetrofitClient.apiCall(call, this, "PostPayment")
    }


}