package com.minbio.erp.customer_management.fragments

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
import com.google.android.gms.maps.model.LatLng
import com.hbb20.CountryCodePicker
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.customer_management.CustomerManagementFragment
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.utils.*
import com.minbio.erp.utils.AppUtils.Companion.createFileMultiPart
import com.minbio.erp.utils.AppUtils.Companion.createPlainRequestBody
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_customer_profile.*
import org.json.JSONObject
import java.io.File

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CustomerProfile : Fragment(), ResponseCallBack {

    private lateinit var countriesModel: CountriesModel
    private var customerData: CustomersData? = null
    private var fromEdit: Boolean = false
    private var countryId: Int = 0
    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var accountAddress: String = ""

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var countrySpinner: CustomSearchableSpinner

    private lateinit var tv_customer_id: TextView
    private lateinit var et_cmp_siren: TextView
    private lateinit var et_cmp_overdraft: EditText
    private lateinit var et_cmp_company_name: EditText
    private lateinit var et_cmp_address: TextView
    private lateinit var et_cmp_contact_full_name: EditText
    private lateinit var et_cmp_mobile: EditText
    private lateinit var et_cmp_email: EditText
    private lateinit var mobileCodePicker: CountryCodePicker
    private lateinit var idImageView: ImageView
    private lateinit var kbisImageView: ImageView

    private lateinit var customer_image: CircleImageView

    private lateinit var iv_cmp_upload_kbis: ConstraintLayout
    private lateinit var iv_cmp_upload_id: LinearLayout

    private var customerImagePath = ""
    private var kbisImagePath = ""
    private var kbisImageExt = ""
    private var idImagePath = ""
    private var idImageExt = ""

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_customer_profile, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.customer_management.split(",")

        initViews()
//        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            if (fromEdit)
                if (permissionsList.contains(PermissionKeys.update_company_customers)) {
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
        customer_image.isClickable = boolean
        et_cmp_overdraft.isEnabled = boolean
        et_cmp_company_name.isEnabled = boolean
        et_cmp_address.isEnabled = boolean
        et_cmp_siren.isEnabled = boolean
        et_cmp_contact_full_name.isEnabled = boolean
        et_cmp_mobile.isEnabled = boolean
        et_cmp_email.isEnabled = boolean
        mobileCodePicker.setCcpClickable(boolean)
        iv_cmp_upload_kbis.isClickable = boolean
        iv_cmp_upload_id.isClickable = boolean
        countrySpinner.isEnabled = boolean
    }

    private fun initViews() {

        fromEdit = arguments!!.getBoolean("fromEdit", false)
        customerData = arguments?.getParcelable("CustomerData")

        btnSave = v.findViewById(R.id.btn_cmp_save)
        btnSave.setOnClickListener { validate() }

        customer_image = v.findViewById(R.id.customer_image)
        tv_customer_id = v.findViewById(R.id.tv_customer_id)
        et_cmp_siren = v.findViewById(R.id.et_cmp_siren)
        et_cmp_overdraft = v.findViewById(R.id.et_cmp_overdraft)
        et_cmp_company_name = v.findViewById(R.id.et_cmp_company_name)
        et_cmp_address = v.findViewById(R.id.et_cmp_address)
        et_cmp_contact_full_name = v.findViewById(R.id.et_cmp_contact_full_name)
        et_cmp_mobile = v.findViewById(R.id.et_cmp_mobile)
        et_cmp_email = v.findViewById(R.id.et_cmp_email)
        mobileCodePicker = v.findViewById(R.id.mobileCodePicker)
        kbisImageView = v.findViewById(R.id.kbisImage)
        idImageView = v.findViewById(R.id.idImage)

        countrySpinner = v.findViewById(R.id.spinner_cmp_country)

        iv_cmp_upload_kbis = v.findViewById(R.id.iv_cmp_upload_kbis)
        iv_cmp_upload_id = v.findViewById(R.id.iv_cmp_upload_id)

        et_cmp_address.setOnClickListener {
            val intent = Intent(context, AddressMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putDouble("lat", latLng.latitude)
            bundle.putDouble("lng", latLng.longitude)
            bundle.putString("address", accountAddress)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE_KEY)
        }

        customer_image.setOnClickListener {
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
            startActivityForResult(intent, Constants.CUSTOMER_IMAGE_REQUEST_CODE)
        }

        iv_cmp_upload_kbis.setOnClickListener {
            getFileFromIntent(Constants.CUSTOMER_KBIS_REQUEST_CODE)
        }

        iv_cmp_upload_id.setOnClickListener {
            getFileFromIntent(Constants.CUSTOMER_ID_REQUEST_CODE)
        }

        setUpCountriesSpinner()
        if (customerData != null) {
            updateViews()
        }
    }

    private fun updateViews() {
        tv_customer_id.text =
            context!!.resources.getString(R.string.cmpLabelCustomerId, customerData?.id.toString())
        et_cmp_siren.text = customerData?.siret_no.toString()
        et_cmp_overdraft.setText(customerData?.overdraft_amount.toString())
        et_cmp_company_name.setText(customerData?.company_name)
        et_cmp_contact_full_name.setText(customerData?.name)
        et_cmp_address.text = customerData?.address
        et_cmp_mobile.setText(customerData?.phone)
        et_cmp_email.setText(customerData?.email)
        mobileCodePicker.setCountryForPhoneCode(customerData?.country_code!!.toInt())

        for (i in countriesModel.data.indices)
            if (countriesModel.data[i].id == customerData?.country_id)
                countrySpinner.setSelection(i)

        kbisImagePath = customerData?.kbis_file_path!!
        idImagePath = customerData?.id_file_path!!

        kbisImageExt = customerData?.kbis_file_ext!!
        idImageExt = customerData?.id_file_ext!!

        customerImagePath = customerData?.image_path!!

        Glide
            .with(context!!)
            .load(customerImagePath)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(customer_image)

        if (kbisImageExt == "pdf") {
            Glide
                .with(context!!)
                .load(R.drawable.ic_pdf)
                .centerCrop()
                .placeholder(R.drawable.ic_kbis_upload)
                .into(kbisImageView)
        } else {
            Glide
                .with(context!!)
                .load(kbisImagePath)
                .centerCrop()
                .placeholder(R.drawable.ic_kbis_upload)
                .into(kbisImageView)
        }

        if (idImageExt == "pdf") {
            Glide
                .with(context!!)
                .load(R.drawable.ic_pdf)
                .centerCrop()
                .placeholder(R.drawable.ic_id_upload)
                .into(idImageView)
        } else {
            Glide
                .with(context!!)
                .load(idImagePath)
                .centerCrop()
                .placeholder(R.drawable.ic_id_upload)
                .into(idImageView)
        }
    }

    private fun setUpCountriesSpinner() {
        val strings = ArrayList<String>()
        countriesModel = (activity as MainActivity).countriesList()

        for (i in countriesModel.data.indices) strings.add(countriesModel.data.get(i).name)

        countrySpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        countrySpinner.setTitle(resources.getString(R.string.countriesSpinnerTitle))

        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, strings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                countryId = countriesModel.data[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun getFileFromIntent(code: Int) {
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
        startActivityForResult(intent, code)
    }

    private fun validate() {
        if (customerImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.cmpErrorCustomerImage),
                false
            )
            return
        }

        if (et_cmp_overdraft.text.toString().isBlank()) {
            error_cmp_overdraft.visibility = View.VISIBLE
            error_cmp_overdraft.setText(R.string.cmpErrorOverdraft);
            AppUtils.setBackground(et_cmp_overdraft, R.drawable.input_border_bottom_red)
            et_cmp_overdraft.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_overdraft, R.drawable.input_border_bottom)
            error_cmp_overdraft.visibility = View.INVISIBLE
        }

        if (et_cmp_company_name.text.toString().isBlank()) {
            error_cmp_company_name.visibility = View.VISIBLE
            error_cmp_company_name.setText(R.string.cmpErrorCompanyName);
            AppUtils.setBackground(et_cmp_company_name, R.drawable.input_border_bottom_red)
            et_cmp_company_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_company_name, R.drawable.input_border_bottom)
            error_cmp_company_name.visibility = View.INVISIBLE
        }

        if (et_cmp_address.text.toString().isBlank()) {
            error_cmp_address.visibility = View.VISIBLE
            error_cmp_address.setText(R.string.cmpErrorAddress);
            AppUtils.setBackground(et_cmp_address, R.drawable.input_border_bottom_red)
            et_cmp_address.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_address, R.drawable.input_border_bottom)
            error_cmp_address.visibility = View.INVISIBLE
        }

        if (et_cmp_contact_full_name.text.toString().isBlank()) {
            error_cmp_contact_full_name.visibility = View.VISIBLE
            error_cmp_contact_full_name.setText(R.string.cmpErrorContactFullName);
            AppUtils.setBackground(et_cmp_contact_full_name, R.drawable.input_border_bottom_red)
            et_cmp_contact_full_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_contact_full_name, R.drawable.input_border_bottom)
            error_cmp_contact_full_name.visibility = View.INVISIBLE
        }

        if (et_cmp_mobile.text.toString().isBlank()) {
            error_cmp_mobile.visibility = View.VISIBLE
            error_cmp_mobile.setText(R.string.cmpErrorMobile);
            AppUtils.setBackground(et_cmp_mobile, R.drawable.input_border_bottom_red)
            et_cmp_mobile.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_mobile, R.drawable.input_border_bottom)
            error_cmp_mobile.visibility = View.INVISIBLE
        }

        if (et_cmp_email.text.toString().isBlank()) {
            error_cmp_email.visibility = View.VISIBLE
            error_cmp_email.setText(R.string.cmpErrorEmail);
            AppUtils.setBackground(et_cmp_email, R.drawable.input_border_bottom_red)
            et_cmp_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_email, R.drawable.input_border_bottom)
            error_cmp_email.visibility = View.INVISIBLE
        }

        if (kbisImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.cmpErrorKbisImage),
                false
            )
            return
        }

        if (idImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.cmpErrorIDImage),
                false
            )
            return
        }

        if (et_cmp_siren.text.toString().isBlank()) {
            error_cmp_siren.visibility = View.VISIBLE
            error_cmp_siren.setText(R.string.cmpErrorSiren);
            AppUtils.setBackground(et_cmp_siren, R.drawable.input_border_bottom_red)
            et_cmp_siren.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmp_siren, R.drawable.input_border_bottom)
            error_cmp_siren.visibility = View.INVISIBLE
        }

        if (fromEdit)
            updateCustomerData()
        else
            postCustomerData()

    }

    private fun updateCustomerData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val customerMultiPart =
            if (customerImagePath != customerData?.image_path) createFileMultiPart(
                customerImagePath,
                "file",
                ""
            ) else
                null

        val kbisMultiPart = if (kbisImagePath != customerData?.kbis_file_path) createFileMultiPart(
            kbisImagePath,
            "kbis_file",
            kbisImageExt
        ) else
            null

        val idMultiPart = if (idImagePath != customerData?.id_file_path) createFileMultiPart(
            idImagePath,
            "id_file",
            idImageExt
        ) else
            null

        val call = api.updateCustomerDate(
            customerData?.id!!,
            createPlainRequestBody(et_cmp_contact_full_name.text.toString()),
            createPlainRequestBody(et_cmp_company_name.text.toString()),
            createPlainRequestBody(et_cmp_siren.text.toString()),
            createPlainRequestBody(et_cmp_email.text.toString()),
            createPlainRequestBody(et_cmp_mobile.text.toString()),
            createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus),
            createPlainRequestBody(mobileCodePicker.selectedCountryNameCode),
            createPlainRequestBody(countryId.toString()),
            createPlainRequestBody(latLng.latitude.toString()),
            createPlainRequestBody(latLng.longitude.toString()),
            createPlainRequestBody(et_cmp_address.text.toString()),
            createPlainRequestBody(et_cmp_overdraft.text.toString()),
            customerMultiPart,
            kbisMultiPart,
            idMultiPart
        )
        RetrofitClient.apiCall(call, this, "UpdateCustomerData")
    }

    private fun postCustomerData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val customerImageMultiPart = createFileMultiPart(customerImagePath, "file", "")
        val kbisMultiPart = createFileMultiPart(kbisImagePath, "kbis_file", kbisImageExt)
        val idMultiPart = createFileMultiPart(idImagePath, "id_file", idImageExt)

        val call = api.postCustomerDate(

            createPlainRequestBody(et_cmp_contact_full_name.text.toString()),
            createPlainRequestBody(et_cmp_company_name.text.toString()),
            createPlainRequestBody(et_cmp_siren.text.toString()),
            createPlainRequestBody(et_cmp_email.text.toString()),
            createPlainRequestBody(et_cmp_mobile.text.toString()),
            createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus),
            createPlainRequestBody(mobileCodePicker.selectedCountryNameCode),
            createPlainRequestBody(countryId.toString()),
            createPlainRequestBody(latLng.latitude.toString()),
            createPlainRequestBody(latLng.longitude.toString()),
            createPlainRequestBody(et_cmp_address.text.toString()),
            createPlainRequestBody(et_cmp_overdraft.text.toString()),
            customerImageMultiPart,
            kbisMultiPart,
            idMultiPart
        )
        RetrofitClient.apiCall(call, this, "PostCustomerData")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ADDRESS_RESULT_CODE_KEY -> {
                    accountAddress = data?.getStringExtra("address")!!
                    latLng =
                        LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                    et_cmp_address.text = accountAddress

                    val countryName = data.getStringExtra("countryName")

                    for (i in countriesModel.data.indices)
                        if (countryName == countriesModel.data[i].name)
                            countrySpinner.setSelection(i)

                }
                Constants.CUSTOMER_IMAGE_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                    if (files.size > 0) {
                        val fileUri = files[0].uri
                        customerImagePath = files[0].path
                        startCrop(fileUri, Constants.CUSTOMER_IMAGE_CROP_REQUEST_CODE)
                    }
                }
                Constants.CUSTOMER_IMAGE_CROP_REQUEST_CODE -> {
                    val cropperUri = UCrop.getOutput(data!!)
                    customer_image.setImageURI(cropperUri)
                    customerImagePath = FilePath.getPath(context, cropperUri)

                }
                Constants.CUSTOMER_KBIS_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                    if (files.size > 0) {
                        val fileUri = files[0].uri

                        kbisImagePath = files[0].path
                        kbisImageExt = kbisImagePath.substring(kbisImagePath.lastIndexOf("."))

                        if (kbisImageExt.equals(".png", ignoreCase = true) || kbisImageExt.equals(
                                ".jpg",
                                ignoreCase = true
                            ) || kbisImageExt.equals(".jpeg", ignoreCase = true)
                        ) {
                            startCrop(fileUri, Constants.CUSTOMER_KBIS_CROP_REQUEST_CODE)
                        } else {
                            Glide
                                .with(context!!)
                                .load(R.drawable.ic_pdf)
                                .centerCrop()
                                .placeholder(R.drawable.ic_plc)
                                .into(kbisImageView)
                        }
                    }
                }
                Constants.CUSTOMER_KBIS_CROP_REQUEST_CODE -> {
                    val cropperUri = UCrop.getOutput(data!!)
                    kbisImageView.setImageURI(cropperUri)
                    kbisImagePath = FilePath.getPath(context, cropperUri)

                }
                Constants.CUSTOMER_ID_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                    if (files.size > 0) {
                        val fileUri = files[0].uri

                        idImagePath = files[0].path
                        idImageExt = idImagePath.substring(idImagePath.lastIndexOf("."))

                        if (idImageExt.equals(".png", ignoreCase = true) || idImageExt.equals(
                                ".jpg",
                                ignoreCase = true
                            ) || idImageExt.equals(".jpeg", ignoreCase = true)
                        ) {
                            startCrop(fileUri, Constants.CUSTOMER_ID_CROP_REQUEST_CODE)
                        } else {
                            Glide
                                .with(context!!)
                                .load(R.drawable.ic_pdf)
                                .centerCrop()
                                .placeholder(R.drawable.ic_plc)
                                .into(idImageView)
                        }
                    }
                }
                Constants.CUSTOMER_ID_CROP_REQUEST_CODE -> {
                    val cropperUri = UCrop.getOutput(data!!)
                    idImageView.setImageURI(cropperUri)
                    idImagePath = FilePath.getPath(context, cropperUri)

                }
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
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        (parentFragment as CustomerManagementFragment).customersList.clear()
        (parentFragment as CustomerManagementFragment).pullToRefresh.isRefreshing = true
        (parentFragment as CustomerManagementFragment).getCustomers(0)

        if (customerData == null)
            customerProfileLayout.visibility = View.GONE

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