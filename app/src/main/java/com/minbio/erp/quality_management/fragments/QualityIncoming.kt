package com.minbio.erp.quality_management.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.product_management.ProManagementFragment
import com.minbio.erp.product_management.model.*
import com.minbio.erp.product_management.model.Unit
import com.minbio.erp.quality_management.QualityManagementFragment
import com.minbio.erp.utils.*
import com.minbio.erp.utils.AppUtils.Companion.createFileMultiPart
import com.minbio.erp.utils.AppUtils.Companion.createPlainRequestBody
import com.minbio.erp.utils.Constants.PRODUCT_DELIVERY_NOTE_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_DELIVERY_NOTE_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_LABEL_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_LABEL_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO1_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO1_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO2_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO2_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO3_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO3_REQUEST_CODE
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_quality_incoming.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File
import java.util.*

class QualityIncoming : Fragment(), ResponseCallBack, View.OnClickListener {

    private lateinit var v: View

    private var checkToNotLoadGradients = true

    private var alertDialog: AlertDialog? = null
    private val hashMap: HashMap<Int, PathCheck> = HashMap<Int, PathCheck>()
    private var labelImagePath = ""
    private var deliveryNoteFilePath = ""
    private var deliveryNoteFileExt = ""

    private var selectedProductOrCategoryId: Int = 0
    private var selectedLotId: Int = 0
    private var fromEdit = false
    private var fromEditCheckForProductData = false

    private lateinit var et_incoming_variety: EditText
    private lateinit var et_incoming_sup_delivery_note: EditText
    private lateinit var tv_incoming_date: TextView
    private lateinit var et_incoming_gross_total_weight: EditText
    private lateinit var et_incoming_sup_tare: EditText
    private lateinit var et_quantity_income_unit: EditText
    private lateinit var et_quantity_income_packing: EditText
    private lateinit var et_incoming_lot_no: EditText
    private lateinit var et_incoming_buying_price: EditText
    private lateinit var et_incoming_shipping_cost: EditText

    //    private lateinit var et_incoming_sup_other_tax: EditText
    private lateinit var et_incoming_sup_vat: EditText
    private lateinit var et_incoming_sup_invoice: EditText
    private lateinit var et_incoming_comment: EditText
    private lateinit var etOtherCost: EditText
    private lateinit var etSellingTare: EditText
    private lateinit var etSellingPrice: EditText
    private lateinit var etInterfelTax: EditText

    private lateinit var productSpinner: CustomSearchableSpinner
    private lateinit var originSpinner: CustomSearchableSpinner
    private lateinit var categorySpinner: CustomSearchableSpinner
    private lateinit var supplierSpinner: CustomSearchableSpinner
    private lateinit var spinner_incoming_unit: CustomSearchableSpinner
    private lateinit var spinner_incoming_calibre: CustomSearchableSpinner
    private lateinit var spinnerSellingPack: CustomSearchableSpinner
    private lateinit var spinnerSellingUnit: CustomSearchableSpinner
    private lateinit var spinnerSellingVat: CustomSearchableSpinner
    private lateinit var spinnerPurchaseAccount: CustomSearchableSpinner
    private lateinit var spinnerSaleAccount: CustomSearchableSpinner

    private lateinit var photo1: ImageView
    private lateinit var photo2: ImageView
    private lateinit var photo3: ImageView
    private lateinit var upl1: ImageView
    private lateinit var upl2: ImageView
    private lateinit var upl3: ImageView
    private lateinit var cross1: ImageView
    private lateinit var cross2: ImageView
    private lateinit var cross3: ImageView

    private lateinit var deliveryUploadImage: ImageView
    private lateinit var labelUploadImage: ImageView
    private lateinit var qualityIncomingImage: CircleImageView
    private lateinit var btnSetCurrentLot: TextView

    private var countriesList: MutableList<Country> = ArrayList()
    private var unitsList: MutableList<Unit> = ArrayList()
    private var vatsList: MutableList<Vat> = ArrayList()
    private var classesList: MutableList<Classes> = ArrayList()
    private var categoriesList: MutableList<Category> = ArrayList()
    private var sizeList: MutableList<Size> = ArrayList()

    private var productNameId: Int = 0
    private var countryId: Int = 0
    private var sellingPackId: Int = 0
    private var sellingUnitId: Int = 0
    private var incomeUnitId: Int = 0
    private var sellingVatId: Int = 0
    private var supplierVatId: Int = 0
    private var classesId: Int = 0
    private var supplierId: Int = 0
    private var sizeId: Int = 0
    private var incomeDateId = ""

    private var productSupplierModel: ProductSupplierModel? = null
    private lateinit var productDetailModel: ProductDetailModel

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>
    private var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    private var parentAccountListStrings: MutableList<String> = ArrayList()

    private var saleAccountId = 0
    private var purchaseAccountId = 0

    private lateinit var btnSave: LinearLayout

    private var selectedVarietyId = 0
    private var selectedVarietyName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_quality_incoming, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.quality_management.split(",")

        initViews()
        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id != 0) {
            if (fromEdit)
                if (permissionsList.contains(PermissionKeys.update_products)) {
                    btnSave.visibility = View.VISIBLE
                    btnSave.isEnabled = true
                    enableDisableViews(true)
                } else {
                    btnSave.visibility = View.GONE
                    btnSave.isEnabled = false
                    enableDisableViews(false)
                }
        }
    }

    private fun enableDisableViews(b: Boolean) {
        et_incoming_variety.isEnabled = b
        et_incoming_sup_delivery_note.isEnabled = b
        tv_incoming_date.isEnabled = b
        et_incoming_gross_total_weight.isEnabled = b
        et_incoming_sup_tare.isEnabled = b
        et_quantity_income_unit.isEnabled = b
        et_quantity_income_packing.isEnabled = b
        et_incoming_lot_no.isEnabled = b
        et_incoming_buying_price.isEnabled = b
        et_incoming_shipping_cost.isEnabled = b
//        et_incoming_sup_other_tax.isEnabled = b
        et_incoming_sup_vat.isEnabled = b
        et_incoming_sup_invoice.isEnabled = b
        et_incoming_comment.isEnabled = b
        etOtherCost.isEnabled = b
        etSellingTare.isEnabled = b
        etSellingPrice.isEnabled = b
        etInterfelTax.isEnabled = b
        spinner_incoming_unit.isEnabled = b
        spinner_incoming_calibre.isEnabled = b
        spinnerPurchaseAccount.isEnabled = b
        spinnerSaleAccount.isEnabled = b
        productSpinner.isEnabled = b
        categorySpinner.isEnabled = b
        originSpinner.isEnabled = b
        supplierSpinner.isEnabled = b
        spinnerSellingUnit.isEnabled = b
        spinnerSellingPack.isEnabled = b
        spinnerSellingVat.isEnabled = b
        deliveryUploadImage.isEnabled = b
        labelUploadImage.isEnabled = b
        btnSave.isEnabled = b
        cross1.isEnabled = b
        cross2.isEnabled = b
        cross3.isEnabled = b
        photo1.isEnabled = b
        photo2.isEnabled = b
        photo3.isEnabled = b
        upl1.isEnabled = b
        upl2.isEnabled = b
        upl3.isEnabled = b
    }


    private fun initViews() {
        selectedProductOrCategoryId = arguments?.getInt("id")!!
        fromEdit = arguments?.getBoolean("fromEdit", false)!!
        selectedVarietyId = arguments?.getInt("selectedVarietyId")!!
        selectedVarietyName = arguments?.getString("selectedVarietyName")!!

        qualityIncomingImage = v.findViewById(R.id.quality_incoming_image)
        btnSetCurrentLot = v.findViewById(R.id.btnSetCurrentLot)
        fromEditCheckForProductData = fromEdit
        if (fromEdit) {
            selectedLotId = arguments?.getInt("lotId")!!
            qualityIncomingImage.visibility = View.VISIBLE
            btnSetCurrentLot.visibility = View.VISIBLE
        } else {
            qualityIncomingImage.visibility = View.GONE
            btnSetCurrentLot.visibility = View.GONE
        }

        et_incoming_variety = v.findViewById(R.id.et_incoming_variety)
        et_incoming_sup_delivery_note = v.findViewById(R.id.et_incoming_sup_delivery_note)
        tv_incoming_date = v.findViewById(R.id.tv_incoming_date)
        tv_incoming_date.setOnClickListener { showDatePicker() }
        et_incoming_gross_total_weight = v.findViewById(R.id.et_incoming_gross_total_weight)
        et_incoming_sup_tare = v.findViewById(R.id.et_incoming_sup_tare)
        et_quantity_income_unit = v.findViewById(R.id.et_quantity_income_unit)
        et_quantity_income_packing = v.findViewById(R.id.et_quantity_income_packing)
        et_incoming_lot_no = v.findViewById(R.id.et_incoming_lot_no)
        et_incoming_buying_price = v.findViewById(R.id.et_incoming_buying_price)
        et_incoming_shipping_cost = v.findViewById(R.id.et_incoming_shipping_cost)
//        et_incoming_sup_other_tax = v.findViewById(R.id.et_incoming_sup_other_tax)
        et_incoming_sup_vat = v.findViewById(R.id.et_incoming_sup_vat)
        et_incoming_sup_invoice = v.findViewById(R.id.et_incoming_sup_invoice)
        et_incoming_comment = v.findViewById(R.id.et_incoming_comment)
        etOtherCost = v.findViewById(R.id.etOtherCost)
        etSellingTare = v.findViewById(R.id.etSellingTare)
        etSellingPrice = v.findViewById(R.id.etSellingPrice)
        etInterfelTax = v.findViewById(R.id.etInterfelTax)
        spinnerPurchaseAccount = v.findViewById(R.id.spinnerPurchaseAccount)
        spinnerSaleAccount = v.findViewById(R.id.spinnerSaleAccount)

        spinner_incoming_unit = v.findViewById(R.id.spinner_incoming_unit)
        spinner_incoming_calibre = v.findViewById(R.id.spinner_incoming_calibre)
        productSpinner = v.findViewById(R.id.spinner_product_incoming)
        categorySpinner = v.findViewById(R.id.spinner_category_incoming)
        originSpinner = v.findViewById(R.id.spinner_origin_incoming)
        supplierSpinner = v.findViewById(R.id.spinner_supplier_incoming)
        spinnerSellingUnit = v.findViewById(R.id.spinnerSellingUnit)
        spinnerSellingPack = v.findViewById(R.id.spinnerSellingPack)
        spinnerSellingVat = v.findViewById(R.id.spinnerSellingVat)

        deliveryUploadImage = v.findViewById(R.id.iv_incoming_upload_delivery_note)
        labelUploadImage = v.findViewById(R.id.iv_incoming_label_photos)
        btnSave = v.findViewById(R.id.btn_incoming_save)

        cross1 = v.findViewById(R.id.cross1)
        cross2 = v.findViewById(R.id.cross2)
        cross3 = v.findViewById(R.id.cross3)

        photo1 = v.findViewById(R.id.photo1)
        photo2 = v.findViewById(R.id.photo2)
        photo3 = v.findViewById(R.id.photo3)

        upl1 = v.findViewById(R.id.upl1)
        upl2 = v.findViewById(R.id.upl2)
        upl3 = v.findViewById(R.id.upl3)

        cross1.setOnClickListener(this)
        cross2.setOnClickListener(this)
        cross3.setOnClickListener(this)

        photo1.setOnClickListener(this)
        photo2.setOnClickListener(this)
        photo3.setOnClickListener(this)

        btnSetCurrentLot.setOnClickListener(this)

        deliveryUploadImage.setOnClickListener {
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
            startActivityForResult(intent, PRODUCT_DELIVERY_NOTE_REQUEST_CODE)
        }
        labelUploadImage.setOnClickListener { getImage(PRODUCT_LABEL_REQUEST_CODE) }

        btnSave.setOnClickListener { validate() }

        getParentAccounts()
    }

    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call =
            RetrofitClient.getClient(context!!).create(Api::class.java).getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }


    private fun validate() {

        if (et_incoming_variety.text.toString().isBlank()) {
            error_incoming_variety.visibility = View.VISIBLE
            error_incoming_variety.setText(R.string.incomingErrorVariety);
            AppUtils.setBackground(et_incoming_variety, R.drawable.input_border_bottom_red)
            et_incoming_variety.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_variety, R.drawable.input_border_bottom)
            error_incoming_variety.visibility = View.INVISIBLE
        }

        if (et_incoming_sup_delivery_note.text.toString().isBlank()) {
            error_incoming_sup_delivery_note.visibility = View.VISIBLE
            error_incoming_sup_delivery_note.setText(R.string.incomingErrorSupplierDeliveryNote);
            AppUtils.setBackground(
                et_incoming_sup_delivery_note,
                R.drawable.input_border_bottom_red
            )
            et_incoming_sup_delivery_note.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_sup_delivery_note, R.drawable.input_border_bottom)
            error_incoming_sup_delivery_note.visibility = View.INVISIBLE
        }

        if (tv_incoming_date.text.toString().isBlank()) {
            error_incoming_date.visibility = View.VISIBLE
            error_incoming_date.setText(R.string.incomingErrorIncomingDate);
            AppUtils.setBackground(tv_incoming_date, R.drawable.input_border_bottom_red)
            tv_incoming_date.requestFocus()
            return
        } else {
            AppUtils.setBackground(tv_incoming_date, R.drawable.input_border_bottom)
            error_incoming_date.visibility = View.INVISIBLE
        }

        if (et_incoming_gross_total_weight.text.toString().isBlank()) {
            error_incoming_gross_total_weight.visibility = View.VISIBLE
            error_incoming_gross_total_weight.setText(R.string.incomingErrorLotGrossWeight);
            AppUtils.setBackground(
                et_incoming_gross_total_weight,
                R.drawable.input_border_bottom_red
            )
            et_incoming_gross_total_weight.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_gross_total_weight, R.drawable.input_border_bottom)
            error_incoming_gross_total_weight.visibility = View.INVISIBLE
        }

        if (et_incoming_sup_tare.text.toString().isBlank()) {
            error_incoming_sup_tare.visibility = View.VISIBLE
            error_incoming_sup_tare.setText(R.string.incomingErrorSupplierTare);
            AppUtils.setBackground(et_incoming_sup_tare, R.drawable.input_border_bottom_red)
            et_incoming_sup_tare.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_sup_tare, R.drawable.input_border_bottom)
            error_incoming_sup_tare.visibility = View.INVISIBLE
        }

        if (et_quantity_income_unit.text.toString().isBlank()) {
            error_quantity_income_unit.visibility = View.VISIBLE
            error_quantity_income_unit.setText(R.string.incomingErrorQuantityIncomeUnite);
            AppUtils.setBackground(et_quantity_income_unit, R.drawable.input_border_bottom_red)
            et_quantity_income_unit.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_quantity_income_unit, R.drawable.input_border_bottom)
            error_quantity_income_unit.visibility = View.INVISIBLE
        }

        if (et_quantity_income_packing.text.toString().isBlank()) {
            error_quantity_income_packing.visibility = View.VISIBLE
            error_quantity_income_packing.setText(R.string.incomingErrorQuantityIncomePacking);
            AppUtils.setBackground(et_quantity_income_packing, R.drawable.input_border_bottom_red)
            et_quantity_income_packing.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_quantity_income_packing, R.drawable.input_border_bottom)
            error_quantity_income_packing.visibility = View.INVISIBLE
        }

        if (et_incoming_lot_no.text.toString().isBlank()) {
            error_incoming_lot_no.visibility = View.VISIBLE
            error_incoming_lot_no.setText(R.string.incomingErrorLotNo);
            AppUtils.setBackground(et_incoming_lot_no, R.drawable.input_border_bottom_red)
            et_incoming_lot_no.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_lot_no, R.drawable.input_border_bottom)
            error_incoming_lot_no.visibility = View.INVISIBLE
        }

        if (et_incoming_buying_price.text.toString().isBlank()) {
            error_incoming_buying_price.visibility = View.VISIBLE
            error_incoming_buying_price.setText(R.string.incomingErrorBuyingPrice);
            AppUtils.setBackground(et_incoming_buying_price, R.drawable.input_border_bottom_red)
            et_incoming_buying_price.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_buying_price, R.drawable.input_border_bottom)
            error_incoming_buying_price.visibility = View.INVISIBLE
        }

        if (et_incoming_shipping_cost.text.toString().isBlank()) {
            error_incoming_shipping_cost.visibility = View.VISIBLE
            error_incoming_shipping_cost.setText(R.string.incomingErrorShippingCost);
            AppUtils.setBackground(et_incoming_shipping_cost, R.drawable.input_border_bottom_red)
            et_incoming_shipping_cost.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_shipping_cost, R.drawable.input_border_bottom)
            error_incoming_shipping_cost.visibility = View.INVISIBLE
        }

//        if (et_incoming_sup_other_tax.text.toString().isBlank()) {
//            error_incoming_sup_other_tax.visibility = View.VISIBLE
//            error_incoming_sup_other_tax.setText(R.string.incomingErrorSupplierOtherTax);
//            AppUtils.setBackground(et_incoming_sup_other_tax, R.drawable.input_border_bottom_red)
//            et_incoming_sup_other_tax.requestFocus()
//            return
//        } else {
//            AppUtils.setBackground(et_incoming_sup_other_tax, R.drawable.input_border_bottom)
//            error_incoming_sup_other_tax.visibility = View.INVISIBLE
//        }

        if (et_incoming_sup_vat.text.toString().isBlank()) {
            error_incoming_sup_vat.visibility = View.VISIBLE
            error_incoming_sup_vat.setText(R.string.incomingErrorSupplierVat);
            AppUtils.setBackground(et_incoming_sup_vat, R.drawable.input_border_bottom_red)
            et_incoming_sup_vat.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_sup_vat, R.drawable.input_border_bottom)
            error_incoming_sup_vat.visibility = View.INVISIBLE
        }

        if (et_incoming_sup_invoice.text.toString().isBlank()) {
            error_incoming_sup_invoice.visibility = View.VISIBLE
            error_incoming_sup_invoice.setText(R.string.incomingErrorSupplierInvoiceNo);
            AppUtils.setBackground(et_incoming_sup_invoice, R.drawable.input_border_bottom_red)
            et_incoming_sup_invoice.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_sup_invoice, R.drawable.input_border_bottom)
            error_incoming_sup_invoice.visibility = View.INVISIBLE
        }

        if (etOtherCost.text.toString().isBlank()) {
            error_other_cost.visibility = View.VISIBLE
            error_other_cost.setText(R.string.pro_detail_error_other_cost)
            AppUtils.setBackground(etOtherCost, R.drawable.input_border_bottom_red)
            etOtherCost.requestFocus()
            return
        } else {
            AppUtils.setBackground(etOtherCost, R.drawable.input_border_bottom)
            error_other_cost.visibility = View.INVISIBLE
        }
        if (etSellingTare.text.toString().isBlank()) {
            error_sell_tare.visibility = View.VISIBLE
            error_sell_tare.setText(R.string.pro_detail_error_selling_tare)
            AppUtils.setBackground(etSellingTare, R.drawable.input_border_bottom_red)
            etSellingTare.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSellingTare, R.drawable.input_border_bottom)
            error_sell_tare.visibility = View.INVISIBLE
        }

        if (etSellingPrice.text.toString().isBlank()) {
            error_sell_price.visibility = View.VISIBLE
            error_sell_price.setText(R.string.pro_detail_error_selling_price)
            AppUtils.setBackground(etSellingPrice, R.drawable.input_border_bottom_red)
            etSellingPrice.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSellingPrice, R.drawable.input_border_bottom)
            error_sell_price.visibility = View.INVISIBLE
        }


        if (etInterfelTax.text.toString().isBlank()) {
            error_interfel_tex.visibility = View.VISIBLE
            error_interfel_tex.setText(R.string.pro_detail_error_incoming_unit)
            AppUtils.setBackground(etInterfelTax, R.drawable.input_border_bottom_red)
            etInterfelTax.requestFocus()
            return
        } else {
            AppUtils.setBackground(etInterfelTax, R.drawable.input_border_bottom)
            error_interfel_tex.visibility = View.INVISIBLE
        }

        if (et_incoming_comment.text.toString().isBlank()) {
            error_incoming_comment.visibility = View.VISIBLE
            error_incoming_comment.setText(R.string.incomingErrorSupplierInvoiceNo);
            AppUtils.setBackground(et_incoming_comment, R.drawable.round_box_stroke_red)
            et_incoming_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_incoming_comment, R.drawable.round_box_light_stroke)
            error_incoming_comment.visibility = View.INVISIBLE
        }

        if (deliveryNoteFilePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.pro_detail_error_delivery_file),
                false
            )
            return
        }

        if (labelImagePath == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.pro_detail_error_label_photo),
                false
            )
            return
        }

        if (hashMap.size == 0) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.pro_detail_error_product_image),
                false
            )
            return
        }

        if (et_quantity_income_unit.text.toString().toDouble() %
            etSellingTare.text.toString().toDouble()
            != 0.0
        ) {
            error_quantity_income_unit.visibility = View.VISIBLE
            error_quantity_income_unit.setText(R.string.pro_detail_sale_case_match);
            AppUtils.setBackground(et_quantity_income_unit, R.drawable.input_border_bottom_red)

            error_sell_tare.visibility = View.VISIBLE
            error_sell_tare.setText(R.string.pro_detail_sale_case_match);
            AppUtils.setBackground(etSellingTare, R.drawable.input_border_bottom_red)

            et_quantity_income_unit.requestFocus()

            return
        } else {
            AppUtils.setBackground(etSellingTare, R.drawable.input_border_bottom)
            error_sell_tare.visibility = View.INVISIBLE
            AppUtils.setBackground(et_quantity_income_unit, R.drawable.input_border_bottom)
            error_quantity_income_unit.visibility = View.INVISIBLE
        }

        /*    if (Integer.parseInt(salesQuantity.getText().toString()) % Integer.parseInt(salesCase.getText().toString()) != 0) {
        showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.SalesFormErrorMessageQuantityShouldMatchIwthBox), false);
        return;
    }*/
        /*    if (AppUtils.convertStringToFloat(salesQuantity.getText().toString()) % AppUtils.convertStringToFloat(salesCase.getText().toString()) != 0) {
                showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.SalesFormErrorMessageQuantityShouldMatchIwthBox), false);
                return;
            }*/

        if (fromEdit) {
            updateLot()
        } else {
            postProductData()
        }

    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                tv_incoming_date.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                incomeDateId = "$day-${monthOfYear + 1}-$year"
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun getProductSupplierList() {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductListForForm(selectedProductOrCategoryId)
        RetrofitClient.apiCall(call, this, "GetProductList")
    }

    private fun getGradients(id: Int) {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getGradients(id)
        RetrofitClient.apiCall(call, this, "Gradients")
    }

    private fun getProductData(id: Int) {
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductData(id)
        RetrofitClient.apiCall(call, this, "GetProductData")
    }


    private fun postProductData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val deliverNoteMultiPart: MultipartBody.Part? =
            createFileMultiPart(deliveryNoteFilePath, "delivery_note_file", deliveryNoteFileExt)
        val labelMultiPart: MultipartBody.Part? =
            createFileMultiPart(labelImagePath, "label_file", "")

        val multipartBodiesList: MutableList<MultipartBody.Part>? = ArrayList()
        for (i in 0 until hashMap.size)
            if (hashMap.containsKey(i))
                if (!hashMap[i]!!.isURL)
                    multipartBodiesList?.add(
                        AppUtils.createFileMultiPart(
                            hashMap[i]?.path!!,
                            "files[]",
                            ""
                        )
                    )

        val multipartBodiesArray: Array<MultipartBody.Part>? = multipartBodiesList?.toTypedArray()


        val call = api.postProductData(
            createPlainRequestBody(selectedVarietyId.toString()),
            createPlainRequestBody(et_incoming_variety.text.toString()),
            createPlainRequestBody(productNameId.toString()),
            createPlainRequestBody(supplierId.toString()),
            createPlainRequestBody(et_incoming_sup_delivery_note.text.toString()),
            createPlainRequestBody(et_incoming_gross_total_weight.text.toString()),
            createPlainRequestBody(et_incoming_sup_tare.text.toString()),
            createPlainRequestBody(et_quantity_income_unit.text.toString()),
            createPlainRequestBody(et_incoming_buying_price.text.toString()),
            createPlainRequestBody(et_quantity_income_packing.text.toString()),
            createPlainRequestBody(sizeId.toString()),
            createPlainRequestBody(countryId.toString()),
            createPlainRequestBody(et_incoming_sup_vat.text.toString()),
            createPlainRequestBody(etOtherCost.text.toString()),
            createPlainRequestBody(et_incoming_shipping_cost.text.toString()),
            createPlainRequestBody(incomeUnitId.toString()),
            createPlainRequestBody(sellingUnitId.toString()),
            createPlainRequestBody(sellingPackId.toString()),
            createPlainRequestBody(etSellingPrice.text.toString()),
            createPlainRequestBody(etSellingTare.text.toString()),
            createPlainRequestBody(sellingVatId.toString()),
            createPlainRequestBody(etInterfelTax.text.toString()),
            createPlainRequestBody(et_incoming_comment.text.toString()),
            createPlainRequestBody("0"),
            createPlainRequestBody("0"),
            createPlainRequestBody(classesId.toString()),
            createPlainRequestBody(incomeDateId),
            createPlainRequestBody(saleAccountId.toString()),
            createPlainRequestBody(purchaseAccountId.toString()),
            deliverNoteMultiPart,
            labelMultiPart,
            multipartBodiesArray
        )
        RetrofitClient.apiCall(call, this, "PostProductData")
    }

    private fun updateLot() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)

        val deliverNoteMultiPart: MultipartBody.Part? =
            if (deliveryNoteFilePath == productDetailModel.data.delivery_note_file_path)
                null
            else
                createFileMultiPart(deliveryNoteFilePath, "delivery_note_file", deliveryNoteFileExt)

        val labelMultiPart: MultipartBody.Part? =
            if (labelImagePath == productDetailModel.data.label_file_path)
                null
            else
                createFileMultiPart(labelImagePath, "label_file", "")

        val multipartBodiesList: MutableList<MultipartBody.Part>? = ArrayList()
        for (i in 0 until hashMap.size)
            if (hashMap.containsKey(i))
                if (!hashMap[i]!!.isURL)
                    multipartBodiesList?.add(
                        AppUtils.createFileMultiPart(
                            hashMap[i]?.path!!,
                            "files[]",
                            ""
                        )
                    )

        val multipartBodiesArray: Array<MultipartBody.Part>? = multipartBodiesList?.toTypedArray()


        val call = api.updateProductData(
            selectedLotId,
            createPlainRequestBody(et_incoming_variety.text.toString()),
            createPlainRequestBody(productNameId.toString()),
            createPlainRequestBody(supplierId.toString()),
            createPlainRequestBody(et_incoming_sup_delivery_note.text.toString()),
            createPlainRequestBody(et_incoming_gross_total_weight.text.toString()),
            createPlainRequestBody(et_incoming_sup_tare.text.toString()),
            createPlainRequestBody(et_quantity_income_unit.text.toString()),
            createPlainRequestBody(et_incoming_buying_price.text.toString()),
            createPlainRequestBody(et_quantity_income_packing.text.toString()),
            createPlainRequestBody(sizeId.toString()),
            createPlainRequestBody(countryId.toString()),
            createPlainRequestBody(et_incoming_sup_vat.text.toString()),
            createPlainRequestBody(etOtherCost.text.toString()),
            createPlainRequestBody(et_incoming_shipping_cost.text.toString()),
            createPlainRequestBody(incomeUnitId.toString()),
            createPlainRequestBody(sellingUnitId.toString()),
            createPlainRequestBody(sellingPackId.toString()),
            createPlainRequestBody(etSellingPrice.text.toString()),
            createPlainRequestBody(etSellingTare.text.toString()),
            createPlainRequestBody(sellingVatId.toString()),
            createPlainRequestBody(etInterfelTax.text.toString()),
            createPlainRequestBody(et_incoming_comment.text.toString()),
            createPlainRequestBody("0"),
            createPlainRequestBody("0"),
            createPlainRequestBody(classesId.toString()),
            createPlainRequestBody(incomeDateId),
            createPlainRequestBody(saleAccountId.toString()),
            createPlainRequestBody(purchaseAccountId.toString()),
            deliverNoteMultiPart,
            labelMultiPart,
            multipartBodiesArray
        )
        RetrofitClient.apiCall(call, this, "UpdateLot")

    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.photo1 -> getImage(PRODUCT_PHOTO1_REQUEST_CODE)
            R.id.photo2 -> getImage(PRODUCT_PHOTO2_REQUEST_CODE)
            R.id.photo3 -> getImage(PRODUCT_PHOTO3_REQUEST_CODE)
            R.id.cross1 -> deleteImage(cross1, upl1, photo1, 0)
            R.id.cross2 -> deleteImage(cross2, upl2, photo2, 1)
            R.id.cross3 -> deleteImage(cross3, upl3, photo3, 2)
            R.id.btnSetCurrentLot -> showCurrentLotDialog()
        }
    }

    private fun showCurrentLotDialog() {
        alertDialog = AlertDialog.Builder(context!!)
            .setMessage(context!!.resources.getString(R.string.setCurrentLot))
            .setPositiveButton(
                context!!.resources.getString(R.string.yes)
            ) { _: DialogInterface?, i: Int ->

                AppUtils.showDialog(context!!)
                val api = RetrofitClient.getClient(context!!).create(Api::class.java)
                val call = api.setDefaultLot(selectedLotId)
                RetrofitClient.apiCall(call, this, "SetDefaultLot")

            }
            .setNegativeButton(
                context!!.resources.getString(R.string.no)
            ) { _: DialogInterface?, i: Int -> alertDialog?.dismiss() }
            .show()
    }

    private fun deleteImage(
        cross: ImageView,
        upl: ImageView,
        photo: ImageView,
        index: Int
    ) {
        alertDialog = AlertDialog.Builder(context!!)
            .setMessage(context!!.resources.getString(R.string.delete_picture))
            .setPositiveButton(
                context!!.resources.getString(R.string.yes)
            ) { _: DialogInterface?, i: Int ->
                if (hashMap.containsKey(index))
                    if (hashMap[index]!!.isURL) {
                        AppUtils.showDialog(context!!)
                        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
                        val call = api.deleteFiles(hashMap[index]?.id!!)
                        RetrofitClient.apiCall(call, this, "DeleteFiles")
                    }

                hashMap.remove(index)
                cross.visibility = View.GONE
                upl.visibility = View.VISIBLE
                photo.setImageDrawable(null)
                photo.isEnabled = true
            }
            .setNegativeButton(
                context!!.resources.getString(R.string.no)
            ) { _: DialogInterface?, i: Int -> alertDialog?.dismiss() }
            .show()
    }

    private fun getImage(code: Int) {
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
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            PRODUCT_PHOTO1_REQUEST_CODE -> {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri
                    startCrop(fileUri, PRODUCT_PHOTO1_CROP_REQUEST_CODE)
                }
            }
            PRODUCT_PHOTO2_REQUEST_CODE -> {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri
                    startCrop(fileUri, PRODUCT_PHOTO2_CROP_REQUEST_CODE)
                }
            }
            PRODUCT_PHOTO3_REQUEST_CODE -> {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri
                    startCrop(fileUri, PRODUCT_PHOTO3_CROP_REQUEST_CODE)
                }
            }
            PRODUCT_LABEL_REQUEST_CODE -> {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri
                    startCrop(fileUri, PRODUCT_LABEL_CROP_REQUEST_CODE)
                }
            }
            PRODUCT_DELIVERY_NOTE_REQUEST_CODE -> {
                val files: ArrayList<MediaFile> =
                    data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)

                if (files.size > 0) {
                    val fileUri = files[0].uri

                    deliveryNoteFilePath = files[0].path
                    deliveryNoteFileExt =
                        deliveryNoteFilePath.substring(deliveryNoteFilePath.lastIndexOf("."))

                    if (deliveryNoteFileExt.equals(".pdf", ignoreCase = true)) {
                        deliveryUploadImage.setPadding(3, 3, 3, 3)
                        Glide
                            .with(context!!)
                            .load(R.drawable.ic_pdf)
                            .centerCrop()
                            .placeholder(R.drawable.ic_plc)
                            .into(deliveryUploadImage)
                    } else {
                        startCrop(fileUri, PRODUCT_DELIVERY_NOTE_CROP_REQUEST_CODE)
                    }
                }
            }
            PRODUCT_PHOTO1_CROP_REQUEST_CODE -> {

                val cropperUri = UCrop.getOutput(data!!)

                hashMap[0] = PathCheck(0, cropperUri?.path!!, false)
                Glide.with(context!!).load(cropperUri).into(photo1)

                upl1.visibility = View.GONE
                cross1.visibility = View.VISIBLE
                photo1.isEnabled = false

            }
            PRODUCT_PHOTO2_CROP_REQUEST_CODE -> {

                val cropperUri = UCrop.getOutput(data!!)

                hashMap[1] = PathCheck(0, cropperUri?.path!!, false)
                Glide.with(context!!).load(cropperUri).into(photo2)

                upl2.visibility = View.GONE
                cross2.visibility = View.VISIBLE
                photo2.isEnabled = false

            }
            PRODUCT_PHOTO3_CROP_REQUEST_CODE -> {

                val cropperUri = UCrop.getOutput(data!!)

                hashMap[2] = PathCheck(0, cropperUri?.path!!, false)
                Glide.with(context!!).load(cropperUri).into(photo3)

                upl3.visibility = View.GONE
                cross3.visibility = View.VISIBLE
                photo3.isEnabled = false

            }
            PRODUCT_LABEL_CROP_REQUEST_CODE -> {

                val cropperUri = UCrop.getOutput(data!!)
                Glide.with(context!!).load(cropperUri).into(labelUploadImage)
                labelUploadImage.setPadding(3, 3, 3, 3)
                labelImagePath = cropperUri?.path!!
            }
            PRODUCT_DELIVERY_NOTE_CROP_REQUEST_CODE -> {

                val cropperUri = UCrop.getOutput(data!!)
                Glide.with(context!!).load(cropperUri).into(deliveryUploadImage)
                deliveryUploadImage.setPadding(3, 3, 3, 3)
                deliveryNoteFilePath = cropperUri?.path!!
            }
        }

    }

    private fun startCrop(sourceUri: Uri, requestCode: Int) {
        val file = File(
            context!!.cacheDir,
            "IMG_" + System.currentTimeMillis() + ".jpg"
        )
        val destinationUriCropper = Uri.fromFile(file)

        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        startActivityForResult(
            UCrop.of(sourceUri, destinationUriCropper)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .getIntent(context!!), requestCode
        )
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                parentAccountList.add(
                    ParentAccountsData(
                        "",
                        0,
                        ""
                    )
                )
                parentAccountList.addAll(model.data)

                for (i in parentAccountList.indices) {
                    if (i > 0)
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
                    else
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
                }

                setUpParentAccountSpinner()
                getProductSupplierList()

            }
            "GetProductList" -> {
                val gson = Gson()
                productSupplierModel =
                    gson.fromJson(jsonObject.toString(), ProductSupplierModel::class.java)
                setUpProductSpinner()

            }
            "Gradients" -> {
                val gson = Gson()
                val gradientModel = gson.fromJson(jsonObject.toString(), GradientsModel::class.java)

                countriesList.clear()
                unitsList.clear()
                categoriesList.clear()
                sizeList.clear()
                classesList.clear()
                vatsList.clear()

                countriesList.addAll(gradientModel.data.countries)
                unitsList.addAll(gradientModel.data.units)
                categoriesList.addAll(gradientModel.data.categories)
                sizeList.addAll(gradientModel.data.sizes)
                classesList.addAll(gradientModel.data.classes)
                vatsList.addAll(gradientModel.data.vats)

                Log.d("", "Gradient is caliing again n again")

                setUpSpinners()
            }
            "GetProductData" -> {

                val gson = Gson()
                productDetailModel =
                    gson.fromJson(jsonObject.toString(), ProductDetailModel::class.java)

                updateViews(productDetailModel)
            }
            "PostProductData" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as QualityManagementFragment).productList.clear()
                (parentFragment as QualityManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as QualityManagementFragment).getProducts(0)
//                (parentFragment as QualityManagementFragment).productListAdapter.expandedID = -1
            }
            "UpdateLot" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as QualityManagementFragment).productList.clear()
                (parentFragment as QualityManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as QualityManagementFragment).getProducts(0)
//                (parentFragment as QualityManagementFragment).productListAdapter.expandedID = -1
            }
            "DeleteFiles" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
            }
            "SetDefaultLot" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
            }
        }
    }

    private fun setUpParentAccountSpinner() {
        val saleAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, parentAccountListStrings)
        spinnerSaleAccount.adapter = saleAdapter
        spinnerSaleAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerSaleAccount.setTitle(context!!.resources.getString(R.string.saleAccountSpinnerTitle))
        spinnerSaleAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    saleAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }

        val purchaseAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, parentAccountListStrings)
        spinnerPurchaseAccount.adapter = purchaseAdapter
        spinnerPurchaseAccount.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinnerPurchaseAccount.setTitle(context!!.resources.getString(R.string.purchaseAccountSpinnerTitle))
        spinnerPurchaseAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    purchaseAccountId = parentAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
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

    private fun setUpProductSpinner() {
        /*Products Spinner*/
        val productStrings = ArrayList<String>()

        for (i in productSupplierModel!!.data.products.indices) {
            productStrings.add(
                productSupplierModel!!.data.products[i].name
            )
        }

        productSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        productSpinner.setTitle(resources.getString(R.string.productSpinnerTitle))

        val productSpinnerAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, productStrings
        )
        productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productSpinner.adapter = productSpinnerAdapter
        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                productNameId =
                    productSupplierModel!!.data.products[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)

                if (checkToNotLoadGradients)
                    getGradients(productNameId)
                else
                    checkToNotLoadGradients = true
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Supplier Spinner*/
        val supplierStrings = ArrayList<String>()

        for (i in productSupplierModel!!.data.suppliers.indices) supplierStrings.add(
            productSupplierModel!!.data.suppliers[i].company_name + " - " + productSupplierModel!!.data.suppliers[i].contact_full_name
        )

        supplierSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        supplierSpinner.setTitle(resources.getString(R.string.supplierSpinnerTitle))

        val supplierSpinnerAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, supplierStrings
        )
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        supplierSpinner.adapter = supplierSpinnerAdapter
        supplierSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                supplierId =
                    productSupplierModel!!.data.suppliers[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        println("Is Edit false is printing again and again")
        if (selectedVarietyId != null) {
            if (selectedVarietyId != 0) {
                productSpinner.isEnabled = false
                et_incoming_variety.isEnabled = false
                et_incoming_variety.setText(selectedVarietyName)

                val selectedProductId = arguments?.getInt("selectedProductId")

                for (i in productSupplierModel!!.data.products.indices) {
                    if (selectedProductId == productSupplierModel!!.data.products[i].id) {
                        productSpinner.setSelection(i)
                    }
                }

            }
        }
        if (selectedLotId != null) {
            if (selectedLotId != 0) {
                productSpinner.isEnabled = false
                et_incoming_variety.isEnabled = false
            }
        }
    }

    private fun setUpSpinners() {
        /*Origin Spinner*/
        val countriesStrings = ArrayList<String>()

        for (i in countriesList.indices) {
            countriesStrings.add(countriesList[i].name)
        }
        originSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        originSpinner.setTitle(resources.getString(R.string.countriesSpinnerTitle))

        val countriesAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, countriesStrings
        )
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        originSpinner.adapter = countriesAdapter
        originSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                countryId = countriesList[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Selling Pack Spinner*/
        val sellingPackStrings = ArrayList<String>()

        for (i in unitsList.indices) {
            sellingPackStrings.add(unitsList[i].name)
        }
        spinnerSellingPack.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerSellingPack.setTitle(resources.getString(R.string.sellingPackSpinnerTitle))

        val sellingPackAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, sellingPackStrings
        )
        sellingPackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSellingPack.adapter = sellingPackAdapter
        spinnerSellingPack.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                sellingPackId = unitsList[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*Selling Unit Spinner*/
        val sellingStrings = ArrayList<String>()

        for (i in unitsList.indices) {
            sellingStrings.add(unitsList[i].name)
        }
        spinnerSellingUnit.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerSellingUnit.setTitle(resources.getString(R.string.sellingUnitSpinnerTitle))

        val sellingAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, sellingStrings
        )
        sellingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSellingUnit.adapter = sellingAdapter
        spinnerSellingUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                sellingUnitId = unitsList[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        /*spinner_incoming_unit*/
        val incomeUnitStrings = ArrayList<String>()

        for (i in unitsList.indices) {
            incomeUnitStrings.add(unitsList[i].name)
        }
        spinner_incoming_unit.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinner_incoming_unit.setTitle(resources.getString(R.string.incomeUnitSpinnerTitle))

        val incomeUnitAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, incomeUnitStrings
        )
        incomeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_incoming_unit.adapter = incomeUnitAdapter
        spinner_incoming_unit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                incomeUnitId = unitsList[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        /*spinnerSellingVat*/
        val sellingVatStrings = ArrayList<String>()

        for (i in vatsList.indices) {
            sellingVatStrings.add(vatsList[i].value)
        }
        spinnerSellingVat.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerSellingVat.setTitle(resources.getString(R.string.sellingVatSpinnerTitle))

        val sellingVatAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, sellingVatStrings
        )
        sellingVatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSellingVat.adapter = sellingVatAdapter
        spinnerSellingVat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                sellingVatId = vatsList.get(adapterView.selectedItemPosition).id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        /*categorySpinner*/
        val classesStrings = ArrayList<String>()

        for (i in classesList.indices) {
            classesStrings.add(classesList[i].name)
        }

        categorySpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        categorySpinner.setTitle(resources.getString(R.string.productClassesSpinnerTitle))

        val categoriesAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, classesStrings
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoriesAdapter
        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    classesId = classesList[adapterView.selectedItemPosition].id
                    AppUtils.hideKeyboard(activity!!)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        /*spinnerCalibre*/
        val calibreStrings = ArrayList<String>()

        for (i in sizeList.indices) calibreStrings.add(sizeList.get(i).name)

        spinner_incoming_calibre.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinner_incoming_calibre.setTitle(resources.getString(R.string.sizeSpinnerTitle))

        val calibreAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, calibreStrings
        )
        calibreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_incoming_calibre.adapter = calibreAdapter
        spinner_incoming_calibre.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    sizeId = sizeList.get(adapterView.selectedItemPosition).id
                    AppUtils.hideKeyboard(activity!!)

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        if (fromEditCheckForProductData) {
            getProductData(selectedLotId)
            fromEditCheckForProductData = false
        } else {
            AppUtils.dismissDialog()
        }
    }

    private fun updateViews(productDetailModel: ProductDetailModel?) {
        AppUtils.dismissDialog()

        Log.d("", "Product data response")
        checkToNotLoadGradients = false
        et_incoming_variety.setText(productDetailModel!!.data.product_variety)
        et_incoming_sup_delivery_note.setText(productDetailModel.data.supplier_note)
        tv_incoming_date.text = productDetailModel.data.incoming_date
        et_incoming_gross_total_weight.setText(productDetailModel.data.gross_weight)
        et_incoming_sup_tare.setText(productDetailModel.data.supplier_tare)
        et_incoming_buying_price.setText(productDetailModel.data.buying_price)
        et_incoming_sup_invoice.setText(productDetailModel.data.invoice_no)
        et_incoming_lot_no.setText(productDetailModel.data.lot_no)
        et_incoming_sup_vat.setText(productDetailModel.data.supplier_vat)
        et_incoming_shipping_cost.setText(productDetailModel.data.shipping_cost)
        et_quantity_income_unit.setText(productDetailModel.data.stock)
        et_quantity_income_packing.setText(productDetailModel.data.income_packing_quantity)
        et_incoming_comment.setText(productDetailModel.data.comment)
        etSellingPrice.setText(productDetailModel.data.selling_price)
        etSellingTare.setText(productDetailModel.data.selling_tare)
        etOtherCost.setText(productDetailModel.data.other_cost)
        etInterfelTax.setText(productDetailModel.data.interfel_tax)
        incomeDateId = productDetailModel.data.incoming_date


        for (i in productSupplierModel!!.data.products.indices) {
            if (productDetailModel.data.product_id == productSupplierModel!!.data.products[i].id) {
                productSpinner.setSelection(i)
            }
        }

        for (i in productSupplierModel!!.data.suppliers.indices) {
            if (productDetailModel.data.supplier_id == productSupplierModel!!.data.suppliers[i].id)
                supplierSpinner.setSelection(i)
        }

        for (i in unitsList.indices) {
            if (productDetailModel.data.income_unit_id == unitsList[i].id)
                spinner_incoming_unit.setSelection(i)
        }

        for (i in countriesList.indices) {
            if (productDetailModel.data.country_id == countriesList[i].id)
                originSpinner.setSelection(i)
        }

        for (i in unitsList.indices) {
            if (productDetailModel.data.selling_unit_id == unitsList[i].id)
                spinnerSellingUnit.setSelection(i)
        }

        for (i in unitsList.indices) {
            if (productDetailModel.data.selling_packing_unit_id == unitsList[i].id)
                spinnerSellingPack.setSelection(i)
        }

        for (i in vatsList.indices) {
            if (productDetailModel.data.selling_vat_id == vatsList[i].id)
                spinnerSellingVat.setSelection(i)
        }

        for (i in sizeList.indices) {
            if (productDetailModel.data.size_id == sizeList[i].id)
                spinner_incoming_calibre.setSelection(i)
        }

        for (i in classesList.indices) {
            if (productDetailModel.data.class_id == classesList[i].id)
                categorySpinner.setSelection(i)
        }

        for (i in parentAccountList.indices) {
            if (productDetailModel.data.sale_account_id == parentAccountList[i]?.id)
                spinnerSaleAccount.setSelection(i)
        }

        for (i in parentAccountList.indices) {
            if (productDetailModel.data.purchase_account_id == parentAccountList[i]?.id)
                spinnerPurchaseAccount.setSelection(i)
        }

        deliveryNoteFilePath = productDetailModel.data.delivery_note_file_path
        labelImagePath = productDetailModel.data.label_file_path

        deliveryNoteFileExt =
            deliveryNoteFilePath.substring(deliveryNoteFilePath.lastIndexOf("."))

        if (deliveryNoteFileExt == ".pdf") {
            deliveryUploadImage.setPadding(3, 3, 3, 3)
            Glide
                .with(context!!)
                .load(R.drawable.ic_pdf)
                .centerCrop()
                .placeholder(R.drawable.ic_plc)
                .into(deliveryUploadImage)
        } else {
            deliveryUploadImage.setPadding(3, 3, 3, 3)
            Glide
                .with(context!!)
                .load(deliveryNoteFilePath)
                .centerCrop()
                .placeholder(R.drawable.ic_add_note)
                .into(deliveryUploadImage)
        }

        labelUploadImage.setPadding(3, 3, 3, 3)
        Glide
            .with(context!!)
            .load(labelImagePath)
            .centerCrop()
            .placeholder(R.drawable.ic_label_photos)
            .into(labelUploadImage)

        hashMap.clear()

        for (i in productDetailModel.data.files.indices) {
            hashMap[i] = PathCheck(
                productDetailModel.data.files[i].id,
                productDetailModel.data.files[i].image_path, true
            )
            try {
                when (i) {
                    0 -> {
                        Glide.with(context!!)
                            .load(productDetailModel.data.files[i].image_path)
                            .placeholder(R.drawable.ic_plc)
                            .into(photo1)
                        upl1.visibility = View.GONE
                        cross1.visibility = View.VISIBLE
                        photo1.isEnabled = false
                    }
                    1 -> {
                        Glide.with(context!!)
                            .load(productDetailModel.data.files[i].image_path)
                            .placeholder(R.drawable.ic_plc)
                            .into(photo2)
                        upl2.visibility = View.GONE
                        cross2.visibility = View.VISIBLE
                        photo2.isEnabled = false
                    }
                    2 -> {
                        Glide.with(context!!)
                            .load(productDetailModel.data.files[i].image_path)
                            .placeholder(R.drawable.ic_plc)
                            .into(photo3)
                        upl3.visibility = View.GONE
                        cross3.visibility = View.VISIBLE
                        photo3.isEnabled = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Glide.with(context!!)
            .load(productDetailModel.data.files[0].image_path)
            .placeholder(R.drawable.ic_plc)
            .into(quality_incoming_image)


    }


}