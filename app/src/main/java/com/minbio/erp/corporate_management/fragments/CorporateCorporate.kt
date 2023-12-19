package com.minbio.erp.corporate_management.fragments

import android.app.Activity
import android.content.Intent
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
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.corporate_management.models.CorporateCompanyDetailsModel
import com.minbio.erp.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_corporate_corporate.*
import org.json.JSONObject
import kotlin.collections.ArrayList

class CorporateCorporate : Fragment(), ResponseCallBack {

    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var accountAddress: String = ""
    private var countryId: Int = 0
    private lateinit var countriesModel: CountriesModel
    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var countrySpinner: CustomSearchableSpinner
    private lateinit var corporateCompanyDetailsModel: CorporateCompanyDetailsModel

    private lateinit var et_corporate_designation: EditText
    private lateinit var tv_corporate_address: TextView
    private lateinit var et_corporate_zip_code: EditText
    private lateinit var et_corporate_contact_title: EditText
    private lateinit var et_corporate_contact_first_name: EditText
    private lateinit var et_corporate_contact_last_name: EditText
    private lateinit var et_corporate_mobile: EditText
    private lateinit var et_corporate_email: EditText
    private lateinit var et_corporate_user_number: EditText
    private lateinit var mobileCodePicker: CountryCodePicker
    private lateinit var tv_corporate_id_number: TextView
    private lateinit var corporate_company_image: CircleImageView

    private lateinit var tv_siren: TextView
    private lateinit var tv_euVat: TextView
    private lateinit var tv_nafcode: TextView

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_corporate_corporate, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.corporate_management.split(",")

        initViews()
        getCountriesList()

        setUpPermission()

        return v
    }

    private fun setUpPermission() {
        if (loginModel.data.designation_id != 0) {
            if (permissionsList.contains(PermissionKeys.update_company_profile)) {
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
        countrySpinner.isEnabled = boolean
        et_corporate_designation.isEnabled = boolean
        tv_corporate_address.isEnabled = boolean
        et_corporate_zip_code.isEnabled = boolean
        et_corporate_contact_title.isEnabled = boolean
        et_corporate_contact_first_name.isEnabled = boolean
        et_corporate_contact_last_name.isEnabled = boolean
        et_corporate_mobile.isEnabled = boolean
        et_corporate_email.isEnabled = boolean
        et_corporate_user_number.isEnabled = boolean
        mobileCodePicker.setCcpClickable(boolean)
    }

    private fun initViews() {
        tv_corporate_id_number = v.findViewById(R.id.tv_corporate_id_number)
        countrySpinner = v.findViewById(R.id.spinner_corporate_country)
        et_corporate_designation = v.findViewById(R.id.et_corporate_designation)
        tv_corporate_address = v.findViewById(R.id.tv_corporate_address)
        et_corporate_zip_code = v.findViewById(R.id.et_corporate_zip_code)
        et_corporate_contact_title = v.findViewById(R.id.et_corporate_contact_title)
        et_corporate_contact_first_name = v.findViewById(R.id.et_corporate_contact_first_name)
        et_corporate_contact_last_name = v.findViewById(R.id.et_corporate_contact_last_name)
        et_corporate_mobile = v.findViewById(R.id.et_corporate_mobile)
        et_corporate_email = v.findViewById(R.id.et_corporate_email)
        et_corporate_user_number = v.findViewById(R.id.et_corporate_user_number)
        mobileCodePicker = v.findViewById(R.id.mobileCodePicker)
        corporate_company_image = v.findViewById(R.id.corporate_company_image)

        tv_siren = v.findViewById(R.id.er_siren)
        tv_euVat = v.findViewById(R.id.tv_euVat)
        tv_nafcode = v.findViewById(R.id.tv_nafcode)

        tv_corporate_address.setOnClickListener {
            val intent = Intent(context, AddressMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putDouble("lat", latLng.latitude)
            bundle.putDouble("lng", latLng.longitude)
            bundle.putString("address", accountAddress)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE_KEY)
        }


        btnSave = v.findViewById(R.id.btn_corporate_save)
        btnSave.setOnClickListener { validate() }

    }

    private fun setUpCountriesSpinner() {
        val strings = ArrayList<String>()

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
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                countryId = countriesModel.data.get(adapterView.selectedItemPosition).id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpDetails() {
        et_corporate_designation.setText(corporateCompanyDetailsModel.data.company_name)
        tv_corporate_address.setText(corporateCompanyDetailsModel.data.company_address)
        et_corporate_zip_code.setText(corporateCompanyDetailsModel.data.zip_code)
        et_corporate_contact_title.setText(corporateCompanyDetailsModel.data.contact_title)
        et_corporate_contact_first_name.setText(corporateCompanyDetailsModel.data.contact_first_name)
        et_corporate_contact_last_name.setText(corporateCompanyDetailsModel.data.contact_last_name)
        et_corporate_mobile.setText(corporateCompanyDetailsModel.data.phone)
        et_corporate_email.setText(corporateCompanyDetailsModel.data.email)
        et_corporate_user_number.setText(corporateCompanyDetailsModel.data.id.toString())

        tv_siren.text = corporateCompanyDetailsModel.data.siret_no

        tv_corporate_id_number.text = context?.resources!!.getString(
            R.string.corporate_label_profile_ID_number,
            corporateCompanyDetailsModel.data.id.toString()
        )

        mobileCodePicker.setCountryForPhoneCode(corporateCompanyDetailsModel.data.country_code.toInt())
        countryId = corporateCompanyDetailsModel.data.country_id

        for (i in countriesModel.data.indices)
            if (countriesModel.data[i].id == corporateCompanyDetailsModel.data.country_id)
                countrySpinner.setSelection(i)

        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(corporate_company_image)

    }

    private fun getCountriesList() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClientNoToken(context!!).create(Api::class.java)
        val call = api.getCountiesList()
        RetrofitClient.apiCall(call, this, "CountriesList")
    }

    private fun validate() {

        if (et_corporate_designation.text.toString().isBlank()) {
            error_designation.visibility = View.VISIBLE
            error_designation.setText(R.string.corporate_designation_error)
            AppUtils.setBackground(et_corporate_designation, R.drawable.input_border_bottom_red)
            et_corporate_designation.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_designation, R.drawable.input_border_bottom)
            error_designation.visibility = View.INVISIBLE
        }

        if (tv_corporate_address.text.toString().isBlank()) {
            error_corporate_address.visibility = View.VISIBLE
            error_corporate_address.setText(R.string.corporate_address_error)
            AppUtils.setBackground(tv_corporate_address, R.drawable.input_border_bottom_red)
            tv_corporate_address.requestFocus()
            return
        } else {
            AppUtils.setBackground(tv_corporate_address, R.drawable.input_border_bottom)
            error_corporate_address.visibility = View.INVISIBLE
        }

        if (et_corporate_zip_code.text.toString().isBlank()) {
            error_corporate_zip_code.visibility = View.VISIBLE
            error_corporate_zip_code.setText(R.string.corporate_zip_code_error)
            AppUtils.setBackground(et_corporate_zip_code, R.drawable.input_border_bottom_red)
            et_corporate_zip_code.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_zip_code, R.drawable.input_border_bottom)
            error_corporate_zip_code.visibility = View.INVISIBLE
        }


        if (et_corporate_contact_title.text.toString().isBlank()) {
            error_corporate_contact_title.visibility = View.VISIBLE
            error_corporate_contact_title.setText(R.string.corporate_contact_title_error)
            AppUtils.setBackground(et_corporate_contact_title, R.drawable.input_border_bottom_red)
            et_corporate_contact_title.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_contact_title, R.drawable.input_border_bottom)
            error_corporate_contact_title.visibility = View.INVISIBLE
        }

        if (et_corporate_contact_first_name.text.toString().isBlank()) {
            error_corporate_contact_first_name.visibility = View.VISIBLE
            error_corporate_contact_first_name.setText(R.string.corporate_contact_first_name_error)
            AppUtils.setBackground(
                et_corporate_contact_first_name,
                R.drawable.input_border_bottom_red
            )
            et_corporate_contact_first_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_contact_first_name, R.drawable.input_border_bottom)
            error_corporate_contact_first_name.visibility = View.INVISIBLE
        }

        if (et_corporate_contact_last_name.text.toString().isBlank()) {
            error_corporate_contact_last_name.visibility = View.VISIBLE
            error_corporate_contact_last_name.setText(R.string.corporate_contact_last_name_error)
            AppUtils.setBackground(
                et_corporate_contact_last_name,
                R.drawable.input_border_bottom_red
            )
            et_corporate_contact_last_name.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_contact_last_name, R.drawable.input_border_bottom)
            error_corporate_contact_last_name.visibility = View.INVISIBLE
        }

        if (et_corporate_mobile.text.toString().isBlank()) {
            error_corporate_mobile.visibility = View.VISIBLE
            error_corporate_mobile.setText(R.string.corporate_mobile_error)
            AppUtils.setBackground(et_corporate_mobile, R.drawable.input_border_bottom_red)
            et_corporate_mobile.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_mobile, R.drawable.input_border_bottom)
            error_corporate_mobile.visibility = View.INVISIBLE
        }

        if (et_corporate_email.text.toString().isBlank()) {
            error_corporate_email.visibility = View.VISIBLE
            error_corporate_email.setText(R.string.corporate_email_error)
            AppUtils.setBackground(et_corporate_email, R.drawable.input_border_bottom_red)
            et_corporate_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_email, R.drawable.input_border_bottom)
            error_corporate_email.visibility = View.INVISIBLE
        }

        if (et_corporate_user_number.text.toString().isBlank()) {
            error_corporate_user_number.visibility = View.VISIBLE
            error_corporate_user_number.setText(R.string.corporate_user_number_error)
            AppUtils.setBackground(et_corporate_user_number, R.drawable.input_border_bottom_red)
            et_corporate_user_number.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_corporate_user_number, R.drawable.input_border_bottom)
            error_corporate_user_number.visibility = View.INVISIBLE
        }

        postCorporateCompanyDetails()
    }

    private fun postCorporateCompanyDetails() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postCorporateCompanyDetails(
            et_corporate_designation.text.toString(),
            corporateCompanyDetailsModel.data.siret_no,
            tv_corporate_address.text.toString(),
            et_corporate_zip_code.text.toString(),
            countryId,
            et_corporate_email.text.toString(),
            et_corporate_contact_first_name.text.toString(),
            et_corporate_contact_last_name.text.toString(),
            et_corporate_contact_title.text.toString(),
            et_corporate_mobile.text.toString(),
            mobileCodePicker.selectedCountryCodeWithPlus,
            mobileCodePicker.selectedCountryNameCode,
            latLng.latitude.toString(),
            latLng.longitude.toString()
        )
        RetrofitClient.apiCall(call, this, "PostCorporateCompanyDetails")
    }

    private fun getCorporateDetails() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCorporateCompanyDetails()
        RetrofitClient.apiCall(call, this, "CorporateCompanyDetails")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "CorporateCompanyDetails" -> {
                AppUtils.dismissDialog()

                val gson = Gson()
                corporateCompanyDetailsModel =
                    gson.fromJson(jsonObject.toString(), CorporateCompanyDetailsModel::class.java)

                setUpCountriesSpinner()
                setUpDetails()
            }
            "CountriesList" -> {
                val gson: Gson = Gson()
                countriesModel = gson.fromJson(jsonObject.toString(), CountriesModel::class.java)

                getCorporateDetails()
            }
            "PostCorporateCompanyDetails" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADDRESS_RESULT_CODE_KEY) {
                accountAddress = data?.getStringExtra("address")!!
                latLng =
                    LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                tv_corporate_address.text = accountAddress

                val countryName = data.getStringExtra("countryName")

                for (i in countriesModel.data.indices)
                    if (countryName == countriesModel.data[i].name)
                        countrySpinner.setSelection(i)


            }

        }
    }

}