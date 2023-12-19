package com.minbio.erp.corporate_management.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.models.BankDetailModel
import com.minbio.erp.utils.*
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_corporate_bank_detail.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class CorporateBankDetail : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var bankImageConstraintLayout: ConstraintLayout

    private lateinit var bankDetail: BankDetailModel

    private lateinit var et_bank_detail_IBAN: EditText
    private lateinit var et_bank_detail_bic_code: EditText
    private lateinit var bankFile: ImageView

    private lateinit var corporate_bank_company_image: CircleImageView

    private var imagePath = ""
    private var imageExt = ""

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_corporate_bank_detail, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.corporate_management.split(",")

        initViews()
        getBankDetails()

        setUpPermission()

        return v
    }

    private fun setUpPermission() {
        if (loginModel.data.designation_id != 0) {
            if (permissionsList.contains(PermissionKeys.update_company_bankdetail)) {
                btnSave.visibility = View.VISIBLE
                btnSave.isClickable = true
                btnSave.isEnabled = true
                enableDisableViews(true)
            } else {
                btnSave.visibility = View.GONE
                btnSave.isClickable = false
                btnSave.isEnabled = false
                enableDisableViews(false)
            }
        }
    }


    private fun enableDisableViews(boolean: Boolean) {
        et_bank_detail_IBAN.isEnabled = boolean
        et_bank_detail_bic_code.isEnabled = boolean
        bankFile.isClickable = boolean
    }

    private fun initViews() {

        corporate_bank_company_image = v.findViewById(R.id.corporate_bank_company_image)

        et_bank_detail_bic_code = v.findViewById(R.id.et_bank_detail_bic_code)
        et_bank_detail_IBAN = v.findViewById(R.id.et_bank_detail_IBAN)

        bankFile = v.findViewById(R.id.bank_detail_image)
        bankImageConstraintLayout = v.findViewById(R.id.bank_detail_image_constraint)
        bankImageConstraintLayout.setOnClickListener {
            val intent = Intent(context, FilePickerActivity::class.java)
            intent.putExtra(
                FilePickerActivity.CONFIGS, Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(true)
                    .setShowAudios(false)
                    .setShowVideos(false)
                    .setSuffixes("png", "jpg", "jpeg", "pdf")
                    .enableImageCapture(true)
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true)
                    .setSkipZeroSizeFiles(true)
                    .setShowFiles(true)
                    .build()
            )
            startActivityForResult(intent, Constants.CORPORATE_BANK_FILE_REQUEST_CODE)
        }

        btnSave = v.findViewById(R.id.btn_bank_detail_save)
        btnSave.setOnClickListener { validate() }

        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(corporate_bank_company_image)
    }

    private fun loadBankData() {
        et_bank_detail_bic_code.setText(bankDetail.data.bic_code)
        et_bank_detail_IBAN.setText(bankDetail.data.iban)

        imagePath = bankDetail.data.bank_detail_photo_path
        if (imagePath != null) {
            imageExt = imagePath.substring(imagePath.lastIndexOf("."))
            if (imageExt == ".pdf")
                Glide
                    .with(context!!)
                    .load(R.drawable.ic_pdf)
                    .centerCrop()
                    .placeholder(R.drawable.ic_corporate_back_detail)
                    .into(bankFile)
            else
                Glide
                    .with(context!!)
                    .load(imagePath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_corporate_back_detail)
                    .into(bankFile)
        }
    }


    private fun validate() {

        if (et_bank_detail_IBAN.text.toString().isBlank()) {
            error_bank_detail_IBAN.visibility = View.VISIBLE
            error_bank_detail_IBAN.setText(R.string.bank_detail_IBAN_error)
            AppUtils.setBackground(et_bank_detail_IBAN, R.drawable.input_border_bottom_red)
            et_bank_detail_IBAN.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_bank_detail_IBAN, R.drawable.input_border_bottom)
            error_bank_detail_IBAN.visibility = View.INVISIBLE
        }

        if (et_bank_detail_bic_code.text.toString().isBlank()) {
            error_bank_detail_bic_code.visibility = View.VISIBLE
            error_bank_detail_bic_code.setText(R.string.bank_detail_BIC_code_error)
            AppUtils.setBackground(et_bank_detail_bic_code, R.drawable.input_border_bottom_red)
            et_bank_detail_bic_code.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_bank_detail_bic_code, R.drawable.input_border_bottom)
            error_bank_detail_bic_code.visibility = View.INVISIBLE
        }

        if (imagePath == "") {
            AppUtils.showToast(
                activity,
                context?.resources!!.getString(R.string.bank_detail_RID_file_error),
                false
            )
            return
        }

        postBankDetails()


    }

    private fun postBankDetails() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val multiPart: MultipartBody.Part?
        multiPart = if (imagePath == bankDetail.data.bank_detail_photo_path)
            null
        else {
            val file = File(imagePath)
            val imageRequestBody = if (imageExt == ".pdf")
                file.asRequestBody("application/pdf".toMediaTypeOrNull())
            else
                file.asRequestBody("image/*".toMediaTypeOrNull())

            MultipartBody.Part.createFormData("bank_detail_photo", file.name, imageRequestBody)
        }

        val call = api.postBankDetails(
            AppUtils.createPlainRequestBody(et_bank_detail_bic_code.text.toString()),
            AppUtils.createPlainRequestBody(et_bank_detail_IBAN.text.toString()),
            multiPart
        )
        RetrofitClient.apiCall(call, this, "PostBankDetails")
    }


    private fun getBankDetails() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getBankDetails()
        RetrofitClient.apiCall(call, this, "GetBankDetails")
    }

    private fun startCrop(sourceUri: Uri, requestCode: Int) {
        val file = File(
            context!!.cacheDir,
            "IMG_" + System.currentTimeMillis() + ".jpg"
        )
        val destinationUriCropper = Uri.fromFile(file)

        startActivityForResult(
            UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(context!!), requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CORPORATE_BANK_FILE_REQUEST_CODE) {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri

                    imagePath = files[0].path
                    imageExt = imagePath.substring(imagePath.lastIndexOf("."))

                    if (imageExt.equals(".png", ignoreCase = true) || imageExt.equals(
                            ".jpg",
                            ignoreCase = true
                        ) || imageExt.equals(".jpeg", ignoreCase = true)
                    ) {
                        startCrop(fileUri, Constants.CORPORATE_BANK_FILE_CROP_REQUEST_CODE)
                    } else {
                        Glide
                            .with(context!!)
                            .load(R.drawable.ic_pdf)
                            .centerCrop()
                            .placeholder(R.drawable.ic_plc)
                            .into(bankFile)
                    }
                }
            } else if (requestCode == Constants.CORPORATE_BANK_FILE_CROP_REQUEST_CODE) {
                val cropperUri = UCrop.getOutput(data!!)
                bankFile.setImageURI(cropperUri)
                imagePath = FilePath.getPath(context, cropperUri)
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        if (tag == "GetBankDetails") {
            AppUtils.dismissDialog()

            val gson = Gson()
            bankDetail = gson.fromJson(jsonObject.toString(), BankDetailModel::class.java)

            if (bankDetail.data.bic_code != null)
                loadBankData()

        } else if (tag == "PostBankDetails") {
            AppUtils.dismissDialog()
            AppUtils.showToast(activity, jsonObject.getString("message"), true)
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

}