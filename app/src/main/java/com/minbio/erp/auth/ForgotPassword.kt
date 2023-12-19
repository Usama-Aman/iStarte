package com.minbio.erp.auth

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class ForgotPassword : BaseActivity(), ResponseCallBack {

    private lateinit var btnSend: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initViews()

    }

    private fun initViews() {
        btnSend = findViewById(R.id.forgot_btn_Send)
        btnSend.setOnClickListener {
            AppUtils.preventTwoClick(btnSend)
            validate()
        }
    }

    private fun validate() {
        if (et_forgot_email.text.toString().isBlank()) {
            error_forgot_email.visibility = View.VISIBLE
            error_forgot_email.setText(R.string.forgotErrorEmail)
            AppUtils.setBackground(et_forgot_email, R.drawable.round_box_stroke_red)
            et_forgot_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_forgot_email, R.drawable.round_box_stroke)
            error_forgot_email.visibility = View.INVISIBLE
        }

        callForgotApi()

    }

    private fun callForgotApi() {
        AppUtils.showDialog(this)

        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.forgotPassword(
            et_forgot_email.text.toString()
        )
        RetrofitClient.apiCall(call, this, "ForgotPassword")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "ForgotPassword") {

            AppUtils.showToast(this, jsonObject.getString("message"), true)

            val data: JSONObject = jsonObject.getJSONObject("data")
            val email = data.getString("email")

            Handler().postDelayed({
                val intent = Intent(this@ForgotPassword, ResetPassword::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            }, 1800)


        }
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