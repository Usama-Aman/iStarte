package com.minbio.erp.supplier_management.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.supplier_management.SupplierManagementFragment
import com.minbio.erp.supplier_management.models.SuppliersData
import com.minbio.erp.utils.*
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_supplier_profile.*
import org.json.JSONObject
import java.io.File

class SupplierProfile : Fragment(), ResponseCallBack {

    private lateinit var countriesModel: CountriesModel
    private var supplierData: SuppliersData? = null
    private var fromEdit: Boolean = false
    private var countryId: Int = 0
    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var accountAddress: String = ""

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var countrySpinner: CustomSearchableSpinner

    private lateinit var tv_customer_id: TextView
    private lateinit var et_sp_siren: TextView
    private lateinit var et_sp_overdraft: EditText
    private lateinit var et_sp_company_name: EditText
    private lateinit var et_sp_address: TextView
    private lateinit var et_sp_contact_full_name: EditText
    private lateinit var et_sp_mobile: EditText
    private lateinit var et_sp_email: EditText
    private lateinit var mobileCodePicker: CountryCodePicker
    private lateinit var idImageView: ImageView
    private lateinit var kbisImageView: ImageView

    private lateinit var supplier_image: CircleImageView

    private lateinit var iv_sp_upload_kbis: LinearLayout
    private lateinit var iv_sp_upload_id: LinearLayout

    private var supplierImagePath = ""
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
        v = inflater.inflate(R.layout.fragment_supplier_profile, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.supplier_management.split(",")


        initViews()
        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            if (fromEdit)
                if (permissionsList.contains(PermissionKeys.update_company_suppliers)) {
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
        supplier_image.isClickable = boolean
        et_sp_overdraft.isEnabled = boolean
        et_sp_company_name.isEnabled = boolean
        et_sp_address.isEnabled = boolean
        et_sp_contact_full_name.isEnabled = boolean
        et_sp_mobile.isEnabled = boolean
        et_sp_email.isEnabled = boolean
        et_sp_siren.isEnabled = boolean
        mobileCodePicker.setCcpClickable(boolean)
        iv_sp_upload_kbis.isClickable = boolean
        iv_sp_upload_id.isClickable = boolean
        countrySpinner.isEnabled = boolean
    }

    private fun initViews() {

        fromEdit = arguments!!.getBoolean("fromEdit", false)
        supplierData = arguments?.getParcelable("SupplierData")

        btnSave = v.findViewById(R.id.btn_sp_save)
        btnSave.setOnClickListener { validate() }

        supplier_image = v.findViewById(R.id.supplier_image)
        tv_customer_id = v.findViewById(R.id.supplier_id)
        et_sp_siren = v.findViewById(R.id.et_sp_siren)
        et_sp_siren = v.findViewById(R.id.et_sp_siren)
        et_sp_overdraft = v.findViewById(R.id.et_sp_overdraft)
        et_sp_company_name = v.findViewById(R.id.et_sp_company_name)
        et_sp_address = v.findViewById(R.id.et_sp_address)
        et_sp_contact_full_name = v.findViewById(R.id.et_sp_contact_full_name)
        et_sp_mobile = v.findViewById(R.id.et_sp_mobile)
        et_sp_email = v.findViewById(R.id.et_sp_email)
        mobileCodePicker = v.findViewById(R.id.mobileCodePicker)
        kbisImageView = v.findViewById(R.id.kbisImage)
        idImageView = v.findViewById(R.id.idImage)
        countrySpinner = v.findViewById(R.id.spinner_sp_country)

        iv_sp_upload_kbis = v.findViewById(R.id.iv_sp_upload_kbis)
        iv_sp_upload_id = v.findViewById(R.id.iv_sp_upload_id)

        et_sp_address.setOnClickListener {
            val intent = Intent(context, AddressMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putDouble("lat", latLng.latitude)
            bundle.putDouble("lng", latLng.longitude)
            bundle.putString("address", accountAddress)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE_KEY)
        }

        supplier_image.setOnClickListener {
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
            startActivityForResult(intent, Constants.SUPPLIER_IMAGE_REQUEST_CODE)
        }

        iv_sp_upload_kbis.setOnClickListener {
            getFileFromIntent(Constants.SUPPLIER_KBIS_REQUEST_CODE)
        }

        iv_sp_upload_id.setOnClickListener {
            getFileFromIntent(Constants.SUPPLIER_ID_REQUEST_CODE)
        }

        setUpCountriesSpinner()

//        getCountriesList()
    }

    private fun updateViews() {
        tv_customer_id.text =
            context!!.resources.getString(R.string.spLabelCustomerId, supplierData?.id.toString())
        et_sp_siren.text = supplierData?.siret_no.toString()
        et_sp_overdraft.setText(supplierData?.allowed_overdraft.toString())
        et_sp_company_name.setText(supplierData?.company_name)
        et_sp_contact_full_name.setText(supplierData?.contact_full_name)
        et_sp_address.text = supplierData?.address
        et_sp_mobile.setText(supplierData?.phone)
        et_sp_email.setText(supplierData?.email)
        mobileCodePicker.setCountryForPhoneCode(supplierData?.country_code!!.toInt())

        for (i in countriesModel.data.indices)
            if (countriesModel.data[i].id == supplierData?.country_id)
                countrySpinner.setSelection(i)

        kbisImagePath = supplierData?.kbis_file_path!!
        idImagePath = supplierData?.id_file_path!!

        kbisImageExt = supplierData?.kbis_file_ext!!
        idImageExt = supplierData?.id_file_ext!!

        supplierImagePath = supplierData?.image_path!!

        Glide
            .with(context!!)
            .load(supplierImagePath)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(supplier_image)

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

    private fun getCountriesList() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClientNoToken(context!!).create(Api::class.java)
        val call = api.getCountiesList()
        RetrofitClient.apiCall(call, this, "CountriesList")
    }

    private fun setUpCountriesSpinner() {
        countriesModel = (activity as MainActivity).countriesList()
        val strings = ArrayList<String>()
        for (i in countriesModel.data.indices) strings.add(countriesModel.data[i].name)
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

        if (supplierData != null) {
            updateViews()
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
        if (supplierImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.spErrorSupplierImage),
                false
            )
            return
        }

        if (et_sp_overdraft.text.toString().isBlank()) {
            error_sp_overdraft.visibility = View.VISIBLE
            error_sp_overdraft.setText(R.string.spErrorOverdraft);
            AppUtils.setBackground(et_sp_overdraft, R.drawable.input_border_bottom_red)
            et_sp_overdraft.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_overdraft, R.drawable.input_border_bottom)
            error_sp_overdraft.visibility = View.INVISIBLE
        }

        if (et_sp_company_name.text.toString().isBlank()) {
            error_sp_company_name.visibility = View.VISIBLE
            error_sp_company_name.setText(R.string.spErrorCompanyName);
            AppUtils.setBackground(et_sp_company_name, R.drawable.input_border_bottom_red)
            et_sp_company_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_company_name, R.drawable.input_border_bottom)
            error_sp_company_name.visibility = View.INVISIBLE
        }

        if (et_sp_address.text.toString().isBlank()) {
            error_sp_address.visibility = View.VISIBLE
            error_sp_address.setText(R.string.spErrorAddress);
            AppUtils.setBackground(et_sp_address, R.drawable.input_border_bottom_red)
            et_sp_address.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_address, R.drawable.input_border_bottom)
            error_sp_address.visibility = View.INVISIBLE
        }

        if (et_sp_contact_full_name.text.toString().isBlank()) {
            error_sp_contact_full_name.visibility = View.VISIBLE
            error_sp_contact_full_name.setText(R.string.spErrorContactFullName);
            AppUtils.setBackground(et_sp_contact_full_name, R.drawable.input_border_bottom_red)
            et_sp_contact_full_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_contact_full_name, R.drawable.input_border_bottom)
            error_sp_contact_full_name.visibility = View.INVISIBLE
        }

        if (et_sp_mobile.text.toString().isBlank()) {
            error_sp_mobile.visibility = View.VISIBLE
            error_sp_mobile.setText(R.string.spErrorMobile);
            AppUtils.setBackground(et_sp_mobile, R.drawable.input_border_bottom_red)
            et_sp_mobile.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_mobile, R.drawable.input_border_bottom)
            error_sp_mobile.visibility = View.INVISIBLE
        }

        if (et_sp_email.text.toString().isBlank()) {
            error_sp_email.visibility = View.VISIBLE
            error_sp_email.setText(R.string.spErrorEmail);
            AppUtils.setBackground(et_sp_email, R.drawable.input_border_bottom_red)
            et_sp_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_email, R.drawable.input_border_bottom)
            error_sp_email.visibility = View.INVISIBLE
        }


        if (kbisImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.spErrorKbisImage),
                false
            )
            return
        }

        if (idImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.spErrorIDImage),
                false
            )
            return
        }


        if (et_sp_siren.text.toString().isBlank()) {
            error_sp_siren.visibility = View.VISIBLE
            error_sp_siren.setText(R.string.spErrorSiren);
            AppUtils.setBackground(et_sp_siren, R.drawable.input_border_bottom_red)
            et_sp_siren.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sp_siren, R.drawable.input_border_bottom)
            error_sp_siren.visibility = View.INVISIBLE
        }


        if (fromEdit)
            updateSupplierData()
        else
            postSupplierData()
    }


    private fun updateSupplierData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val customerMultiPart =
            if (supplierImagePath != supplierData?.image_path) AppUtils.createFileMultiPart(
                supplierImagePath,
                "file",
                ""
            ) else
                null

        val kbisMultiPart =
            if (kbisImagePath != supplierData?.kbis_file_path) AppUtils.createFileMultiPart(
                kbisImagePath,
                "kbis_file",
                kbisImageExt
            ) else
                null

        val idMultiPart =
            if (idImagePath != supplierData?.id_file_path) AppUtils.createFileMultiPart(
                idImagePath,
                "id_file",
                idImageExt
            ) else
                null

        val call = api.updateSupplierData(
            supplierData?.id!!,
            AppUtils.createPlainRequestBody(et_sp_contact_full_name.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_company_name.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_siren.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_email.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_mobile.text.toString()),
            AppUtils.createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus),
            AppUtils.createPlainRequestBody(mobileCodePicker.selectedCountryNameCode),
            AppUtils.createPlainRequestBody(countryId.toString()),
            AppUtils.createPlainRequestBody(latLng.latitude.toString()),
            AppUtils.createPlainRequestBody(latLng.longitude.toString()),
            AppUtils.createPlainRequestBody(et_sp_address.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_overdraft.text.toString()),
            customerMultiPart,
            kbisMultiPart,
            idMultiPart
        )
        RetrofitClient.apiCall(call, this, "UpdateSupplierData")
    }

    private fun postSupplierData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val customerImageMultiPart = AppUtils.createFileMultiPart(supplierImagePath, "file", "")
        val kbisMultiPart = AppUtils.createFileMultiPart(kbisImagePath, "kbis_file", kbisImageExt)
        val idMultiPart = AppUtils.createFileMultiPart(idImagePath, "id_file", idImageExt)

        val call = api.postSupplierData(

            AppUtils.createPlainRequestBody(et_sp_contact_full_name.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_company_name.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_siren.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_email.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_mobile.text.toString()),
            AppUtils.createPlainRequestBody(mobileCodePicker.selectedCountryCodeWithPlus),
            AppUtils.createPlainRequestBody(mobileCodePicker.selectedCountryNameCode),
            AppUtils.createPlainRequestBody(countryId.toString()),
            AppUtils.createPlainRequestBody(latLng.latitude.toString()),
            AppUtils.createPlainRequestBody(latLng.longitude.toString()),
            AppUtils.createPlainRequestBody(et_sp_address.text.toString()),
            AppUtils.createPlainRequestBody(et_sp_overdraft.text.toString()),
            customerImageMultiPart,
            kbisMultiPart,
            idMultiPart
        )
        RetrofitClient.apiCall(call, this, "PostSupplierData")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ADDRESS_RESULT_CODE_KEY -> {
                    accountAddress = data?.getStringExtra("address")!!
                    latLng =
                        LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                    et_sp_address.text = accountAddress

                    val countryName = data.getStringExtra("countryName")

                    for (i in countriesModel.data.indices)
                        if (countryName == countriesModel.data[i].name)
                            countrySpinner.setSelection(i)

                }
                Constants.SUPPLIER_IMAGE_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                    if (files.size > 0) {
                        val fileUri = files[0].uri
                        supplierImagePath = files[0].path
                        startCrop(fileUri, Constants.SUPPLIER_IMAGE_CROP_REQUEST_CODE)
                    }
                }
                Constants.SUPPLIER_IMAGE_CROP_REQUEST_CODE -> {
                    val cropperUri = UCrop.getOutput(data!!)
                    supplier_image.setImageURI(cropperUri)
                    supplierImagePath = FilePath.getPath(context, cropperUri)

                }
                Constants.SUPPLIER_KBIS_REQUEST_CODE -> {
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
                            startCrop(fileUri, Constants.SUPPLIER_KBIS_CROP_REQUEST_CODE)
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
                Constants.SUPPLIER_KBIS_CROP_REQUEST_CODE -> {
                    val cropperUri = UCrop.getOutput(data!!)
                    kbisImageView.setImageURI(cropperUri)
                    kbisImagePath = FilePath.getPath(context, cropperUri)

                }
                Constants.SUPPLIER_ID_REQUEST_CODE -> {
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
                            startCrop(fileUri, Constants.SUPPLIER_ID_CROP_REQUEST_CODE)
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
                Constants.SUPPLIER_ID_CROP_REQUEST_CODE -> {
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
        when (tag) {
            "CountriesList" -> {
                val gson: Gson = Gson()
                countriesModel = gson.fromJson(jsonObject.toString(), CountriesModel::class.java)
            }
            else -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as SupplierManagementFragment).suppliersList.clear()
                (parentFragment as SupplierManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as SupplierManagementFragment).getSuppliers(0)

                if (supplierData == null)
                    supplierProfileLayout.visibility = View.GONE
            }
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