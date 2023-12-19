package com.minbio.erp.corporate_management.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.CorporateManagementFragment
import com.minbio.erp.corporate_management.models.CorporateUsersData
import com.minbio.erp.corporate_management.models.DesignationData
import com.minbio.erp.corporate_management.models.DesignationsModel
import com.minbio.erp.utils.*
import com.minbio.erp.utils.Constants.CORPORATE_PROFILE_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.CORPORATE_PROFILE_REQUEST_CODE
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_corporate_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File

class CorporateProfile : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var btn_save: LinearLayout
    private lateinit var positionSpinner: CustomSearchableSpinner
    private lateinit var statusSpinner: CustomSearchableSpinner
    private lateinit var customerImage: CircleImageView
    private lateinit var corporateProfileLayout: ConstraintLayout

    private lateinit var etCustomerFirstName: EditText
    private lateinit var etCustomerLastName: EditText
    private lateinit var etCustomerEmail: EditText
    private lateinit var etCustomerMobile: EditText
    private lateinit var tvCorporateCustomerId: TextView
    private lateinit var mobileCodePicker: CountryCodePicker

    private var corporateUsersData: CorporateUsersData? = null
    private var designationData: ArrayList<DesignationData> = ArrayList()
    private var imagePath: String? = ""
    private var statusId: Int = 0
    private var positionId: Int = 0

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    private var fromEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_corporate_profile, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.corporate_management.split(",")

        initViews()
        setUpPermission()

        return v
    }

    private fun setUpPermission() {

        if (fromEdit) {
            if (permissionsList.contains(PermissionKeys.update_company_users)) {
                btn_save.visibility = View.VISIBLE
                btn_save.isClickable = true
                btn_save.isEnabled = true
                enableDisableViews(true)
            } else {
                btn_save.visibility = View.GONE
                btn_save.isClickable = false
                btn_save.isEnabled = false
                enableDisableViews(false)
            }
        }
        getDesignations()
    }

    private fun enableDisableViews(boolean: Boolean) {
        etCustomerFirstName.isEnabled = boolean
        etCustomerLastName.isEnabled = boolean
        etCustomerEmail.isEnabled = boolean
        etCustomerMobile.isEnabled = boolean
        positionSpinner.isEnabled = boolean
        statusSpinner.isEnabled = boolean
        customerImage.isClickable = boolean
        mobileCodePicker.setCcpClickable(boolean)
    }

    private fun initViews() {

        corporateUsersData = arguments?.getParcelable("CorporateUserData")
        fromEdit = arguments?.getBoolean("fromEdit", false)!!

        corporateProfileLayout = v.findViewById(R.id.corporateProfileLayout)
        etCustomerFirstName = v.findViewById(R.id.et_customer_first_name)
        etCustomerLastName = v.findViewById(R.id.et_customer_last_name)
        etCustomerEmail = v.findViewById(R.id.et_customer_email)
        etCustomerMobile = v.findViewById(R.id.et_customer_mobile)
        positionSpinner = v.findViewById(R.id.spinner_customer_position)
        statusSpinner = v.findViewById(R.id.spinner_customer_status)
        tvCorporateCustomerId = v.findViewById(R.id.corporate_customer_id)
        mobileCodePicker = v.findViewById(R.id.mobileCodePicker)

        btn_save = v.findViewById(R.id.btn_customer_save)
        btn_save.setOnClickListener { validate() }


        customerImage = v.findViewById(R.id.customer_image)
        customerImage.setOnClickListener {

            val intent = Intent(context, FilePickerActivity::class.java)
            intent.putExtra(
                FilePickerActivity.CONFIGS, Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(true)
                    .setShowAudios(false)
                    .setShowVideos(false)
                    .setSuffixes("png", "jpg", "jpeg")
                    .enableImageCapture(true)
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true)
                    .setSkipZeroSizeFiles(true)
                    .setShowFiles(false)
                    .build()
            )
            startActivityForResult(intent, CORPORATE_PROFILE_REQUEST_CODE)
        }

    }

    private fun setProfileData() {
        etCustomerFirstName.setText(corporateUsersData?.first_name)
        etCustomerLastName.setText(corporateUsersData?.last_name)
        etCustomerEmail.setText(corporateUsersData?.email)
        etCustomerMobile.setText(corporateUsersData?.phone)

        tvCorporateCustomerId.text = context?.resources!!.getString(
            R.string.corporate_label_profile_ID_number,
            corporateUsersData?.id.toString()
        )

        imagePath = corporateUsersData?.image_path

        Glide
            .with(context!!)
            .load(imagePath)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(customerImage)

        mobileCodePicker.setCountryForPhoneCode(corporateUsersData?.country_code!!.toInt())

        statusId = corporateUsersData?.is_active!!

        if (statusId == 0)
            statusSpinner.setSelection(1)
        else if (statusId == 1)
            statusSpinner.setSelection(0)

        for (i in designationData.indices)
            if (designationData[i].id == corporateUsersData?.designation_id)
                positionSpinner.setSelection(i)

    }

    private fun getDesignations() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getDesignations()
        RetrofitClient.apiCall(call, this, "Designations")
    }

    private fun setUpSpinners() {
        //Position Spinner
        val positionItems = ArrayList<String>()
        for (i in designationData) {
            positionItems.add(i.designation)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, positionItems)
        positionSpinner.adapter = positionAdapter
        positionSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        positionSpinner.setTitle(resources.getString(R.string.positionSpinnerTitle))
        positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                positionId = designationData.get(position).id
            }

        }


        //Status Spinner
        val statusItems = arrayOf("Active", "In-Active")
        val spinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        statusSpinner.adapter = spinnerAdapter
        statusSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        statusSpinner.setTitle(resources.getString(R.string.statusSpinnerTitle))
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    statusId = 1
                else if (position == 1)
                    statusId = 0
            }
        }


    }

    private fun validate() {

        if (et_customer_first_name.text.toString().isBlank()) {
            error_customer_first_name.visibility = View.VISIBLE
            error_customer_first_name.setText(R.string.corporate_first_name_error);
            AppUtils.setBackground(et_customer_first_name, R.drawable.input_border_bottom_red)
            et_customer_first_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_customer_first_name, R.drawable.input_border_bottom)
            error_customer_first_name.visibility = View.INVISIBLE
        }

        if (et_customer_last_name.text.toString().isBlank()) {
            error_customer_last_name.visibility = View.VISIBLE
            error_customer_last_name.setText(R.string.corporate_last_name_error);
            AppUtils.setBackground(et_customer_last_name, R.drawable.input_border_bottom_red)
            et_customer_last_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_customer_last_name, R.drawable.input_border_bottom)
            error_customer_last_name.visibility = View.INVISIBLE
        }

        if (!AppUtils.validateEmail(et_customer_email.text.toString())) {
            error_customer_email.visibility = View.VISIBLE
            error_customer_email.setText(R.string.corporate_email_profile_error);
            AppUtils.setBackground(et_customer_email, R.drawable.input_border_bottom_red)
            et_customer_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_customer_email, R.drawable.input_border_bottom)
            error_customer_email.visibility = View.INVISIBLE
        }

        if (et_customer_mobile.text.toString().isBlank()) {
            error_customer_mobile.visibility = View.VISIBLE
            error_customer_mobile.setText(R.string.corporate_mobile_profile_error);
            AppUtils.setBackground(et_customer_mobile, R.drawable.input_border_bottom_red)
            et_customer_mobile.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_customer_mobile, R.drawable.input_border_bottom)
            error_customer_mobile.visibility = View.INVISIBLE
        }

        if (imagePath == "") {
            AppUtils.showToast(
                activity,
                context?.resources!!.getString(R.string.corporate_profile_image_error)
                , false
            )
            return
        }

        postCorporateData()


    }

    private fun postCorporateData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val call: Call<ResponseBody>
        if (corporateUsersData == null) {
            val file = File(imagePath!!)
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val multiPart = MultipartBody.Part.createFormData("file", file.name, imageRequestBody)

            call = api.postCorporateUser(
                createPlainRequestBody(etCustomerFirstName.text.toString()),
                createPlainRequestBody(etCustomerLastName.text.toString()),
                createPlainRequestBody(etCustomerEmail.text.toString()),
                createPlainRequestBody(etCustomerMobile.text.toString()),
                createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus.toString()),
                createPlainRequestBody(mobileCodePicker.selectedCountryNameCode.toString()),
                createPlainRequestBody(positionId.toString()),
                createPlainRequestBody(statusId.toString()),
                multiPart
            )
            RetrofitClient.apiCall(call, this, "AddCorporateUser")
        } else {
            val multiPart: MultipartBody.Part?
            multiPart = if (imagePath == corporateUsersData?.image_path)
                null
            else {
                val file = File(imagePath)
                val imageRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, imageRequestBody)
            }


            call = api.updateCorporateUser(
                corporateUsersData?.id!!,
                createPlainRequestBody(etCustomerFirstName.text.toString()),
                createPlainRequestBody(etCustomerLastName.text.toString()),
                createPlainRequestBody(etCustomerEmail.text.toString()),
                createPlainRequestBody(etCustomerMobile.text.toString()),
                createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus.toString()),
                createPlainRequestBody(mobileCodePicker.selectedCountryNameCode.toString()),
                createPlainRequestBody(positionId.toString()),
                createPlainRequestBody(statusId.toString()),
                multiPart
            )
            RetrofitClient.apiCall(call, this, "UpdateCorporateUser")
        }
    }

    private fun createPlainRequestBody(string: String): RequestBody {
        return RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            string
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CORPORATE_PROFILE_REQUEST_CODE) {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri
                    customer_image.setImageURI(fileUri)

                    imagePath = files[0].path
                    startCrop(fileUri, CORPORATE_PROFILE_CROP_REQUEST_CODE)
                }
            } else if (requestCode == CORPORATE_PROFILE_CROP_REQUEST_CODE) {
                val cropperUri = UCrop.getOutput(data!!)
                imagePath = FilePath.getPath(context, cropperUri)
            }
        }
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

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "Designations") {
            val gson = Gson()
            val designationsModel =
                gson.fromJson(jsonObject.toString(), DesignationsModel::class.java)

            designationData.addAll(designationsModel.data)
            setUpSpinners()

            if (corporateUsersData != null) {
                setProfileData()
            }

        } else {
            AppUtils.showToast(activity, jsonObject.getString("message"), true)
            (parentFragment as CorporateManagementFragment).corporateUserList.clear()
            (parentFragment as CorporateManagementFragment).getUsers(0)

            if (corporateUsersData == null)
                corporateProfileLayout.visibility = View.GONE
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