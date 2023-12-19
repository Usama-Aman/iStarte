package com.minbio.erp.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.btn_login
import kotlinx.android.synthetic.main.activity_register.error_business
import kotlinx.android.synthetic.main.activity_register.error_email
import kotlinx.android.synthetic.main.activity_register.txtEmail
import kotlinx.android.synthetic.main.activity_register.txtbusiness
import org.json.JSONObject

class RegisterActivity : BaseActivity(), ResponseCallBack {

    private var countryId: Int = 0
    private lateinit var countriesSpinner: CustomSearchableSpinner
    private lateinit var countriesModel: CountriesModel

    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var accountAddress: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        getCountriesList()

    }


    private fun initViews() {

        countriesSpinner = findViewById(R.id.countriesSpinnerRegister)

        btn_login.setOnClickListener {
            AppUtils.preventTwoClick(btn_login)
            validateRequest()
        }

        back_to_login.setOnClickListener {
            AppUtils.preventTwoClick(back_to_login)
            finish()
        }

        txtcorporateadd.setOnClickListener {
            AppUtils.preventTwoClick(txtcorporateadd)
            val intent = Intent(this@RegisterActivity, AddressMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putDouble("lat", latLng.latitude)
            bundle.putDouble("lng", latLng.longitude)
            bundle.putString("address", accountAddress)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE_KEY)
        }

    }

    private fun validateSiren(): Boolean {
        error_siren.visibility = View.INVISIBLE
        AppUtils.setBackground(txtsiren, R.drawable.round_box_stroke)
        val siren_text = txtsiren.text.toString();
        return if (!siren_text.isEmpty()) {
            true
        } else {
            error_siren.visibility = View.VISIBLE
            error_siren.setText(R.string.reg_siren_empty);
            AppUtils.setBackground(txtsiren, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateCorpName(): Boolean {
        error_business.visibility = View.INVISIBLE
        AppUtils.setBackground(txtbusiness, R.drawable.round_box_stroke)
        var siren_busi_text = txtbusiness.text.toString();
        return if (!siren_busi_text.isEmpty()) {
            true
        } else {
            error_business.visibility = View.VISIBLE
            error_business.setText(R.string.reg_corp_name_empty);
            AppUtils.setBackground(txtbusiness, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateCorpAdd(): Boolean {
        error_corporateadd.visibility = View.INVISIBLE
        AppUtils.setBackground(txtcorporateadd, R.drawable.round_box_stroke)
        var corp_add_text = txtcorporateadd.text.toString();
        return if (!corp_add_text.isEmpty()) {
            true
        } else {
            error_corporateadd.visibility = View.VISIBLE
            error_corporateadd.setText(R.string.reg_corp_add_emtpy);
            AppUtils.setBackground(txtcorporateadd, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateZipCode(): Boolean {
        error_zipcode.visibility = View.INVISIBLE
        AppUtils.setBackground(txtzipcode, R.drawable.round_box_stroke)
        var zipcode_text = txtzipcode.text.toString();
        return if (!zipcode_text.isEmpty()) {
            true
        } else {
            error_zipcode.visibility = View.VISIBLE
            error_zipcode.setText(R.string.reg_zipcode_empty);
            AppUtils.setBackground(txtzipcode, R.drawable.round_box_stroke_red)
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
            error_email.setText(R.string.reg_invalid_email);
            AppUtils.setBackground(txtEmail, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateContactFname(): Boolean {
        error_contactfanme.visibility = View.INVISIBLE
        AppUtils.setBackground(txtcontactfanme, R.drawable.round_box_stroke)
        var con_fname_text = txtcontactfanme.text.toString();
        return if (!con_fname_text.isEmpty()) {
            true
        } else {
            error_contactfanme.visibility = View.VISIBLE
            error_contactfanme.setText(R.string.reg_confname_empty);
            AppUtils.setBackground(txtcontactfanme, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateContactLname(): Boolean {
        error_contactlname.visibility = View.INVISIBLE
        AppUtils.setBackground(txtcontactlname, R.drawable.round_box_stroke)
        var con_lname_text = txtcontactlname.text.toString();
        return if (!con_lname_text.isEmpty()) {
            true
        } else {
            error_contactlname.visibility = View.VISIBLE
            error_contactlname.setText(R.string.reg_conlname_empty);
            AppUtils.setBackground(txtcontactlname, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateContactTitle(): Boolean {
        error_contacttitle.visibility = View.INVISIBLE
        AppUtils.setBackground(txtcontacttitle, R.drawable.round_box_stroke)
        var con_title_text = txtcontacttitle.text.toString();
        return if (!con_title_text.isEmpty()) {
            true
        } else {
            error_contacttitle.visibility = View.VISIBLE
            error_contacttitle.setText(R.string.reg_conltitle_empty);
            AppUtils.setBackground(txtcontacttitle, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validatePhone(): Boolean {
        error_mobile.visibility = View.INVISIBLE
        AppUtils.setBackground(phoneNumber_layout, R.drawable.round_box_stroke)
        return if (AppUtils.validatePhone(txtmobile.text.toString())) {
            true
        } else {
            error_mobile.visibility = View.VISIBLE
            error_mobile.setText(R.string.reg_invalid_phone);
            AppUtils.setBackground(phoneNumber_layout, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validatePassword(): Boolean {
        error_register_password.visibility = View.INVISIBLE
        AppUtils.setBackground(et_register_password, R.drawable.round_box_stroke)
        return if (!et_register_password.text.toString().isBlank()) {
            true
        } else {
            error_register_password.visibility = View.VISIBLE
            error_register_password.setText(R.string.reg_invalid_password);
            AppUtils.setBackground(et_register_password, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateCPassword(): Boolean {
        error_register_Cpassword.visibility = View.INVISIBLE
        AppUtils.setBackground(et_register_Cpassword, R.drawable.round_box_stroke)
        return if (!et_register_Cpassword.text.toString().isBlank()) {
            true
        } else {
            error_register_Cpassword.visibility = View.VISIBLE
            error_register_Cpassword.setText(R.string.reg_invalid_Cpassword);
            AppUtils.setBackground(et_register_Cpassword, R.drawable.round_box_stroke_red)
            false
        }
    }

    private fun validateRequest() {

        if (!validateSiren()) return

        if (!validateCorpName()) return

        if (!validateCorpAdd()) return

        if (!validateZipCode()) return

        if (!validateEmail()) return

        if (!validateContactFname()) return

        if (!validateContactLname()) return

        if (!validateContactTitle()) return

        if (!validatePhone()) return

        if (!validatePassword()) return

        if (!validateCPassword()) return

        register()

    }

    private fun register() {

        AppUtils.showDialog(this)

        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.register(
            txtbusiness.text.toString(),
            txtsiren.text.toString(),
            txtcorporateadd.text.toString(),
            txtzipcode.text.toString(),
            countryId,
            txtEmail.text.toString(),
            txtcontactfanme.text.toString(),
            txtcontactlname.text.toString(),
            txtcontacttitle.text.toString(),
            txtmobile.text.toString(),
            mobileCodePicker.selectedCountryCodeWithPlus,
            et_register_password.text.toString(),
            et_register_Cpassword.text.toString(),
            mobileCodePicker.selectedCountryNameCode,
            latLng.latitude.toString(),
            latLng.longitude.toString()
        )
        RetrofitClient.apiCall(call, this, "Register")

    }

    private fun getCountriesList() {
        AppUtils.showDialog(this)
        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.getCountiesList()
        RetrofitClient.apiCall(call, this, "CountriesList")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "CountriesList") {
            val gson: Gson = Gson()
            countriesModel = gson.fromJson(jsonObject.toString(), CountriesModel::class.java)

            setUpCountriesSpinner()
        } else if (tag == "Register") {
            AppUtils.showToast(this, jsonObject.getString("message"), true)
            val data = jsonObject.getJSONObject("data")
            Handler().postDelayed({
                val intent = Intent(this@RegisterActivity, ActivateOPTActivity::class.java)
                intent.putExtra("number", data.getString("phone"))
                startActivity(intent)
                finish()
            }, 1800)
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

    private fun setUpCountriesSpinner() {
        val strings = ArrayList<String>()

        for (i in countriesModel.data.indices) strings.add(countriesModel.data.get(i).name)

        countriesSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        countriesSpinner.setTitle(resources.getString(R.string.countriesSpinnerTitle))

        val adapter = ArrayAdapter(
            this@RegisterActivity,
            android.R.layout.simple_spinner_item, strings
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countriesSpinner.adapter = adapter
        countriesSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                i: Int,
                l: Long
            ) {
                countryId = countriesModel.data[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(this@RegisterActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADDRESS_RESULT_CODE_KEY) {
                accountAddress = data?.getStringExtra("address")!!
                latLng =
                    LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                txtcorporateadd.text = accountAddress

                val countryName = data.getStringExtra("countryName")

                for (i in countriesModel.data.indices)
                    if (countryName == countriesModel.data[i].name)
                        countriesSpinner.setSelection(i)


            }

        }
    }


}
