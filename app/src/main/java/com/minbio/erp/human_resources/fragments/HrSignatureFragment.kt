package com.minbio.erp.human_resources.fragments

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chaos.view.PinView
import com.kyanogen.signatureview.SignatureView
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.models.CorporateUsersData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.createPlainRequestBody
import kotlinx.android.synthetic.main.fragment_hr_signature.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class HrSignatureFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var signatureView: SignatureView
    private lateinit var tvDate: TextView
    private lateinit var pinView: PinView
    private lateinit var sendOtp: LinearLayout
    private lateinit var btnSave: LinearLayout

    private var corporateUsersData: CorporateUsersData? = null

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_signature, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        initViews()

        return v
    }


    private fun initViews() {
        corporateUsersData = arguments?.getParcelable("CorporateUserData")

        signatureView = v.findViewById(R.id.signatureView)
        tvDate = v.findViewById(R.id.tvDate)
        pinView = v.findViewById(R.id.pinView)

        tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth + 1, monthOfYear, year)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        sendOtp = v.findViewById(R.id.btnSendOtp)
        btnSave = v.findViewById(R.id.btnSave)
        sendOtp.setOnClickListener {
            AppUtils.showDialog(context!!)
            val api = RetrofitClient.getClient(context!!).create(Api::class.java)
            val call = api.sendOTP(corporateUsersData?.id!!)
            RetrofitClient.apiCall(call, this, "SendOTP")
        }
        btnSave.setOnClickListener { validate() }


    }

    private fun validate() {

        if (signatureView.isBitmapEmpty) {
            errorSignature.visibility = View.VISIBLE
            errorSignature.setText(R.string.hrSigErrorSignature)
            signatureView.requestFocus()
            return
        } else {
            errorSignature.visibility = View.INVISIBLE
        }

        if (tvDate.text.toString().isBlank()) {
            errorDate.visibility = View.VISIBLE
            errorDate.setText(R.string.hrSigErrorDate)
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom_red)
            tvDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvDate, R.drawable.input_border_bottom)
            errorDate.visibility = View.INVISIBLE
        }

        if (pinView.text.toString().isBlank()) {
            errorOTP.visibility = View.VISIBLE
            pinView.setLineColor(ContextCompat.getColor(context!!, R.color.colorRed));
            errorOTP.setText(R.string.hrSigErrorOTP)
            pinView.requestFocus()
            return
        } else {
            pinView.setLineColor(ContextCompat.getColor(context!!, R.color.light_gray));
            errorOTP.visibility = View.INVISIBLE
        }

        verifyUser(saveBitmap(signatureView.signatureBitmap))
    }

    private fun saveBitmap(b: Bitmap): File? {
        val filesDir: File = context!!.getFilesDir()
        val imageFile = File(filesDir, "Temp" + ".png")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            b.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile
    }

    private fun verifyUser(sigFile: File?) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val imageRequestBody = sigFile?.asRequestBody("image/*".toMediaTypeOrNull())
        val multiPart =
            MultipartBody.Part.createFormData("signature", sigFile?.name, imageRequestBody!!)

        val call = api.verifyUser(
            createPlainRequestBody(corporateUsersData?.id!!.toString()),
            createPlainRequestBody(pinView.text.toString()),
            createPlainRequestBody(tvDate.text.toString()),
            multiPart
        )
        RetrofitClient.apiCall(call, this, "VerifyUser")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        when (tag) {
            "SendOTP" -> {
                sendOtp.visibility = View.GONE
                pinView.visibility = View.VISIBLE
            }
            "VerifyUser" -> {
                tvDate.text = ""
                signatureView.clearCanvas()
                sendOtp.visibility - View.VISIBLE
                pinView.setText("")
                pinView.visibility = View.GONE
            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, true)
    }


}