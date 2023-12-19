package com.minbio.erp.auth

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.adapters.FingerprintHandler
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.showToast
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class LoginActivity : BaseActivity(), ResponseCallBack {

    private lateinit var btnLogin: LinearLayout
    private lateinit var btnFingerPrint: ImageView
    private var rememberMeCheck = false

    private lateinit var alertDialog: AlertDialog

    private var fingerprintManager: FingerprintManager? = null

    //    private var keyguardManager: KeyguardManager? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var cipher: Cipher? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null
    private lateinit var fingerprintHandler: FingerprintHandler
    private val FINGERPRINT_KEY = "iStarte"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
//        fingerPrint()

    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            AppUtils.preventTwoClick(btnLogin)
            validateRequest()
        }

        btnFingerPrint = findViewById(R.id.btnFingerPrint)
        btnFingerPrint.setOnClickListener {
            AppUtils.preventTwoClick(btnFingerPrint)
            alertDialog = AlertDialog.Builder(this@LoginActivity)
                .setMessage(resources.getString(R.string.fingerPrintDialogueLabel))
                .setPositiveButton(
                    resources.getString(R.string.cancelButton)
                ) { dialog: DialogInterface?, which: Int -> alertDialog.dismiss() }.show()

        }

        btn_register.setOnClickListener {
            AppUtils.preventTwoClick(btn_register)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forget_password.setOnClickListener {
            AppUtils.preventTwoClick(forget_password)
            startActivity(
                Intent(
                    this,
                    ForgotPassword::class.java
                )
            )
        }

        rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            rememberMeCheck = isChecked
        }

    }

    private fun validateBusiness(): Boolean {
        error_business.visibility = View.INVISIBLE
        AppUtils.setBackground(txtbusiness, R.drawable.round_box_stroke)
        var siren_busi_text = txtbusiness.text.toString();
        return if (!siren_busi_text.isEmpty()) {
            true
        } else {
            error_business.visibility = View.VISIBLE
            error_business.setText(R.string.login_empty_siren);
            AppUtils.setBackground(txtbusiness, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateEmail(): Boolean {
        error_email.visibility = View.INVISIBLE
        return if (AppUtils.validateEmail(txtEmail.text.toString())) {
            AppUtils.setBackground(txtEmail, R.drawable.round_box_stroke)
            true
        } else {
            error_email.visibility = View.VISIBLE
            error_email.setText(R.string.login_invalid_email);
            AppUtils.setBackground(txtEmail, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validatePassword(): Boolean {
        error_password.visibility = View.INVISIBLE
        AppUtils.setBackground(txtPassword, R.drawable.round_box_stroke)
        val password_text = txtPassword.text.toString();
        return if (!password_text.isEmpty()) {
            true
        } else {
            error_password.visibility = View.VISIBLE
            error_password?.setText(R.string.login_empty_password);
            AppUtils.setBackground(txtPassword, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateRequest() {
        if (!validateBusiness())
            return
        if (!validateEmail())
            return
        if (!validatePassword())
            return

        login()
    }

    private fun login() {
        AppUtils.showDialog(this)

        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.login(
            txtbusiness.text.toString(),
            txtEmail.text.toString(),
            txtPassword.text.toString()
        )
        RetrofitClient.apiCall(call, this, "Login")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        if (tag == "Login") {
            handleLoginResponse(jsonObject, false)
        }
    }

    fun handleLoginResponse(jsonObject: JSONObject, fromFingerPrint: Boolean) {
        AppUtils.dismissDialog()
        val data = jsonObject.getJSONObject("data")

        if (data.has("phone_verified") || data.has("email_verified") || data.has("is_active")) {
            when {
                data.getInt("phone_verified") == 0 -> {
                    showToast(this, jsonObject.getString("message"), false)

                    Handler().postDelayed({
                        val intent = Intent(this@LoginActivity, ActivateOPTActivity::class.java)
                        intent.putExtra("number", data.getString("phone"))
                        startActivity(intent)
                    }, 1800)
                }
                data.getInt("email_verified") == 0 -> {
                    showToast(this, jsonObject.getString("message"), false)
                }
                data.getInt("is_active") == 0 -> {
                    showToast(this, jsonObject.getString("message"), false)
                }
                else -> {

                    val gson = Gson()
                    val loginModel =
                        gson.fromJson(jsonObject.toString(), LoginModel::class.java)
                    val userDataString: String = gson.toJson(loginModel)

                    SharedPreference.saveSimpleString(
                        this,
                        Constants.accessToken,
                        loginModel.access_token
                    )
                    SharedPreference.saveSimpleString(
                        this,
                        Constants.userData,
                        userDataString
                    )
                    SharedPreference.saveBoolean(
                        this,
                        Constants.isFirstLogin,
                        true
                    )
                    if (!fromFingerPrint) {
                        SharedPreference.saveSimpleString(
                            this,
                            Constants.fingerPrintUserSiren,
                            txtbusiness.text.toString()
                        )
                        SharedPreference.saveSimpleString(
                            this,
                            Constants.fingerPrintUserEmail,
                            txtEmail.text.toString()
                        )
                        SharedPreference.saveSimpleString(
                            this,
                            Constants.fingerPrintUserPassword,
                            txtPassword.text.toString()
                        )
                    }

                    if (rememberMeCheck) {
                        SharedPreference.saveBoolean(this, Constants.isLoggedIn, true)
                    } else {
                        SharedPreference.saveBoolean(this, Constants.isLoggedIn, false)
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        showToast(this, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        showToast(this, message!!, false)
    }

    private fun fingerPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHandler = FingerprintHandler(this@LoginActivity, this)
            fingerprintManager =
                getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager?
//            keyguardManager =
//                getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager?
            if (fingerprintManager != null) {
                if (fingerprintManager!!.hasEnrolledFingerprints()) {
                    generateFingerprintKeyStore()
                    val mCipher: Cipher? = instantiateCipher()
                    if (mCipher != null) {
                        cryptoObject = FingerprintManager.CryptoObject(mCipher)
                    }
                    fingerprintHandler.completeFingerAuthentication(
                        fingerprintManager,
                        cryptoObject
                    )
                }
            }
        }
    }

    //  ------------------ Finger print handler settings -------------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun generateFingerprintKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
        try {
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
        try {
            keyGenerator!!.init(
                KeyGenParameterSpec.Builder(
                    FINGERPRINT_KEY,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        try {
            keyGenerator!!.generateKey()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun instantiateCipher(): Cipher? {
        try {
            cipher =
                Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyStore!!.load(null)
            val secretKey = keyStore!!.getKey(
                FINGERPRINT_KEY,
                null
            ) as SecretKey
            cipher!!.init(Cipher.ENCRYPT_MODE, secretKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cipher
    }
}