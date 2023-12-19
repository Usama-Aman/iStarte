package com.minbio.erp.auth

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_activate_opt.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


@Suppress("DEPRECATION")
class ActivateOPTActivity : BaseActivity(), ResponseCallBack {

    private var phoneNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activate_opt)
        initViews()
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra("number")

        btn_send.setOnClickListener {
            AppUtils.preventTwoClick(btn_send)
            validateRequest()
        }

        go_to_login.setOnClickListener {
            AppUtils.preventTwoClick(go_to_login)
            finish()
        }

    }

    private fun validateCode(): Boolean {
        val pin_text = pinView.text.toString();
        pin_view_error.visibility = View.GONE;
        pinView.setLineColor(resources.getColor(R.color.colorInputborder));
        return if (pin_text.length < 4) {
            pin_view_error.visibility = View.VISIBLE
            pin_view_error.setText(R.string.actOTP_empty_text);
            pinView.setLineColor(resources.getColor(R.color.colorRed));
            false
        } else {
            return true
        }
    }


    private fun validateRequest() {

        if (!validateCode())
            return

        checkOtp()
    }

    private fun checkOtp() {
        AppUtils.showDialog(this)

        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.confirmOtp(phoneNumber, pinView.text.toString())
        RetrofitClient.apiCall(call, this, "OTP")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()

        if (tag == "OTP") {
            AppUtils.showToast(this, jsonObject.getString("message"), true)
            Handler().postDelayed({ finish() }, 1800)
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


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}