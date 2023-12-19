package com.minbio.erp.auth

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.json.JSONObject

class ResetPassword : BaseActivity(), ResponseCallBack {

    private lateinit var btnResetPassword: LinearLayout

    private var forgotPasswordEmail: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initViews()

    }

    private fun initViews() {
        forgotPasswordEmail = intent.getStringExtra("email")

        btnResetPassword = findViewById(R.id.btn_reset_password)
        btnResetPassword.setOnClickListener {
            AppUtils.preventTwoClick(btnResetPassword)
            validate() }

    }

    private fun validate() {

        if (et_otp_reset.text.toString().isBlank()) {
            error_otp_reset.visibility = View.VISIBLE
            error_otp_reset.setText(R.string.resetErrorOTP)
            AppUtils.setBackground(et_otp_reset, R.drawable.round_box_stroke_red)
            et_otp_reset.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_otp_reset, R.drawable.round_box_stroke)
            error_otp_reset.visibility = View.INVISIBLE
        }
        if (et_password_reset.text.toString().isBlank()) {
            error_password_reset.visibility = View.VISIBLE
            error_password_reset.setText(R.string.resetErrorPassword)
            AppUtils.setBackground(et_password_reset, R.drawable.round_box_stroke_red)
            et_password_reset.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_password_reset, R.drawable.round_box_stroke)
            error_password_reset.visibility = View.INVISIBLE
        }
        if (et_Cpassword_reset.text.toString().isBlank()) {
            error_Cpassword_reset.visibility = View.VISIBLE
            error_Cpassword_reset.setText(R.string.resetErrorCPassword)
            AppUtils.setBackground(et_Cpassword_reset, R.drawable.round_box_stroke_red)
            et_Cpassword_reset.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_Cpassword_reset, R.drawable.round_box_stroke)
            error_Cpassword_reset.visibility = View.INVISIBLE
        }

        callResetApi()

    }

    private fun callResetApi() {
        AppUtils.showDialog(this)
        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.resetPassword(
            forgotPasswordEmail,
            et_otp_reset.text.toString(),
            et_password_reset.text.toString(),
            et_Cpassword_reset.text.toString()

        )
        RetrofitClient.apiCall(call, this, "ResetPassword")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(this, jsonObject.getString("message"), true)
        Handler().postDelayed({
            finish()
        }, 1500)
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(this, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(this, message!!, false)
    }

}