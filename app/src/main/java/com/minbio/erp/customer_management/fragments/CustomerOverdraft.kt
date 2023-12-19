package com.minbio.erp.customer_management.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.cashier_desk.fragments.PaymentDialog
import com.minbio.erp.customer_management.CustomerManagementFragment
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.room.AppDatabase
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.fragment_customer_overdraft.*
import org.json.JSONObject

class CustomerOverdraft : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var et_cmo_overdraft_allow: EditText
    private lateinit var error_cmo_overdraft_allow: TextView
    private lateinit var tvPayOverdraft: TextView
    private lateinit var article_code_number: TextView
    private lateinit var switchOverdraftAllowed: Switch
    private lateinit var overdraftSettingLayout: LinearLayout
    private lateinit var overdraftPaymentLayout: ConstraintLayout
    private lateinit var creditCardConstraint: ConstraintLayout

    private var customerData: CustomersData? = null

    var isOverdraftAllowed = 0
    private var stripeToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_customer_overdraft, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        customerData = arguments!!.getParcelable("CustomerData")

        article_code_number = v.findViewById(R.id.article_code_number)
        article_code_number.text =
            context!!.resources.getString(
                R.string.cmoLabelCustomerIDNumber,
                customerData?.id.toString()
            )

        btnSave = v.findViewById(R.id.btn_cmo_save)
        et_cmo_overdraft_allow = v.findViewById(R.id.et_cmo_overdraft_allow)
        error_cmo_overdraft_allow = v.findViewById(R.id.error_cmo_overdraft_allow)
        switchOverdraftAllowed = v.findViewById(R.id.switchOverdraftAllowed)
        overdraftSettingLayout = v.findViewById(R.id.overdraftSettingLayout)
        overdraftPaymentLayout = v.findViewById(R.id.overdraftPaymentLayout)
        tvPayOverdraft = v.findViewById(R.id.tvPayOverdraft)
        creditCardConstraint = v.findViewById(R.id.creditCardConstraint)
        creditCardConstraint.setOnClickListener {
            AppUtils.preventTwoClick(creditCardConstraint)
            showDialog()
        }


        if (customerData?.is_pending_overdraft == 0) {
            btnSave.visibility = View.VISIBLE
            overdraftSettingLayout.visibility = View.VISIBLE
            overdraftPaymentLayout.visibility = View.GONE
        } else {
            tvPayOverdraft.text = context!!.resources.getString(
                R.string.cmoLabelPayOverdraft,
                AppUtils.appendCurrency(customerData?.pending_overdraft!!, context!!)
            )

            btnSave.visibility = View.GONE
            overdraftSettingLayout.visibility = View.GONE
            overdraftPaymentLayout.visibility = View.VISIBLE
        }


        if (customerData?.overdraft_amount != null) {
            et_cmo_overdraft_allow.setText(customerData?.overdraft_amount.toString())
        }
        switchOverdraftAllowed.isChecked = (customerData?.is_allow_overdraft == 1)

        switchOverdraftAllowed.setOnCheckedChangeListener { _, isChecked ->
            isOverdraftAllowed = if (isChecked) 1 else 0
        }

        btnSave.setOnClickListener { validate() }
    }

    private fun validate() {
        if (et_cmo_overdraft_allow.text.toString().isBlank()) {
            error_cmo_overdraft_allow.visibility = View.VISIBLE
            error_cmo_overdraft_allow.setText(R.string.cmoErrorOverdraftAllow);
            AppUtils.setBackground(et_cmo_overdraft_allow, R.drawable.input_border_bottom_red)
            et_cmo_overdraft_allow.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmo_overdraft_allow, R.drawable.input_border_bottom)
            error_cmo_overdraft_allow.visibility = View.INVISIBLE
        }

        callPostOverdraftSettingApi()
    }

    private fun callPostOverdraftSettingApi() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postCustomerOverdraftSetting(
            isOverdraftAllowed,
            et_cmo_overdraft_allow.text.toString(),
            customerData?.id!!
        )
        RetrofitClient.apiCall(call, this, "PostCustomerOverdraftSetting")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        when (tag) {
            "PostCustomerOverdraftSetting" -> {

                (parentFragment as CustomerManagementFragment).customersList.clear()
                (parentFragment as CustomerManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as CustomerManagementFragment).getCustomers(0)
            }
            "PostCustomerOverdraftPayment" -> {
                btnSave.visibility = View.VISIBLE
                overdraftSettingLayout.visibility = View.VISIBLE
                overdraftPaymentLayout.visibility = View.GONE

                (parentFragment as CustomerManagementFragment).customersList.clear()
                (parentFragment as CustomerManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as CustomerManagementFragment).getCustomers(0)

                (parentFragment as CustomerManagementFragment).updateOverdraft()

                val db = AppDatabase.getAppDataBase(context = context!!)
                val gson = Gson()
                val loginModel = gson.fromJson(
                    SharedPreference.getSimpleString(context, Constants.userData),
                    LoginModel::class.java
                )

                db?.salesDAO()?.updateSalesCustomerPendingOverdraft(
                    loginModel.data.id.toString(),
                    customerData?.id.toString(),
                    "0.00"
                )
            }
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

    fun showDialog() {
        val paymentDialog = PaymentDialog(
            context!!,
            null,
            null,
            this,
            "customerOverdraftFragment"
        )
        paymentDialog.setOwnerActivity(activity!!)
        paymentDialog.show()
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
                    postOverdraftPayment()
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

    private fun postOverdraftPayment() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postCustomerOverdraftPayment(
            stripeToken,
            customerData?.id!!
        )
        RetrofitClient.apiCall(call, this, "PostCustomerOverdraftPayment")
    }

}