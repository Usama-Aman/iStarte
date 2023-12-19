package com.minbio.erp.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.minbio.erp.BuildConfig
import com.minbio.erp.R
import com.minbio.erp.auth.LoginActivity
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class SplashActivity : BaseActivity() {

    private val splashTime: Long = 3000
    private lateinit var tvVersionNumber: TextView

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvVersionNumber = findViewById(R.id.tvVersionNumber)
        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName: String = BuildConfig.VERSION_NAME
        tvVersionNumber.text = resources.getString(R.string.version) + " " + versionName + "." + versionCode.toString()

        callNextActivity()
    }

    private fun callNextActivity() {

        if (SharedPreference.getBoolean(this, Constants.isLoggedIn))
            Handler().postDelayed(
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, splashTime
            )
        else
            Handler().postDelayed(
                {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }, splashTime
            )
    }
}
