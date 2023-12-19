package com.minbio.erp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.minbio.erp.R
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_activation.*
import kotlinx.android.synthetic.main.activity_activation.error_email
import kotlinx.android.synthetic.main.activity_activation.txtEmail
import kotlinx.android.synthetic.main.activity_login.*


class ActivationActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation)
        setListeners()
    }

    private fun setListeners() {
        btn_process.setOnClickListener{
            validateRequest();
        }

        txtMobile.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateRequest();
                true
            } else false
        }

        process_.isClickable = false;
        process_.isFocusable = false;
        process_.isEnabled = false;

        go_to_login.setOnClickListener{
            AppUtils.preventTwoClick(go_to_login)
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
            finish()
        }
    }



    private fun validateEmail(): Boolean {
        error_email.visibility = View.INVISIBLE
        return if (AppUtils.validateEmail(txtEmail.text.toString())) {
            AppUtils.setBackground(txtEmail, R.drawable.round_box_stroke)
            true
        } else {
            error_email.visibility = View.VISIBLE
            error_email.setText(R.string.invalid_email);
            AppUtils.setBackground(txtEmail, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validatePhone(): Boolean {
        error_phone.visibility = View.INVISIBLE
        return if (AppUtils.validatePhone(txtMobile.text.toString())) {
            AppUtils.setBackground(txtMobile, R.drawable.round_box_stroke)
            true
        } else {
            error_phone.visibility = View.VISIBLE
            error_phone?.setText(R.string.invalid_phone);
            AppUtils.setBackground(txtMobile, R.drawable.round_box_stroke_red)
            false
        }
    }


    private fun validateRequest() {

        if (!validateEmail())
            return
        if (!validatePhone())
            return

        AppUtils.hideKeyboard(this)
        var intent = Intent(this, ActivateOPTActivity::class.java)
        startActivity(intent)
        finishAffinity()
        finish()


    }

}