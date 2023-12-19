package com.minbio.erp.product_management.fragments

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
import com.minbio.erp.utils.*
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO1_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO1_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO2_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO2_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO3_CROP_REQUEST_CODE
import com.minbio.erp.utils.Constants.PRODUCT_PHOTO3_REQUEST_CODE
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_product_detail.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    private var checkToNotLoadGradients = true

    private var alertDialog: AlertDialog? = null
    private val hashMap: HashMap<Int, PathCheck> = HashMap<Int, PathCheck>()
    private lateinit var v: View

    private var selectedProductOrCategoryId: Int = 0
    private var selectedLotId: Int = 0
    private var fromEdit = false
    private var fromEditCheckForProductData = false

    private lateinit var etVarietyName: TextView
    private lateinit var etSupplierNote: TextView
    private lateinit var tvIncomeDate: TextView
    private lateinit var etLotWeight: TextView
    private lateinit var etSupplierTare: TextView
    private lateinit var etBuyingPrice: TextView
    private lateinit var etSupplierInvoice: TextView
    private lateinit var etIncomeQuantity: TextView
    private lateinit var etPackQuantity: TextView
    private lateinit var etLotNo: TextView
    private lateinit var etOtherCost: TextView
    private lateinit var etShipCost: TextView
    private lateinit var etSellingPrice: TextView
    private lateinit var etSellingTare: TextView
    private lateinit var etComment: TextView
    private lateinit var etInterfelTax: TextView
    private lateinit var etSupplierVat: TextView
    private lateinit var spinnerProductCalibre: CustomSearchableSpinner
    private lateinit var spinnerSupplierName: CustomSearchableSpinner
    private lateinit var spinnerProductName: CustomSearchableSpinner
    private lateinit var spinnerSellingPack: CustomSearchableSpinner
    private lateinit var spinnerSellingUnit: CustomSearchableSpinner
    private lateinit var spinnerIncomeUnit: CustomSearchableSpinner
    private lateinit var spinnerSellingVat: CustomSearchableSpinner
    private lateinit var spinnerProductClasses: CustomSearchableSpinner
    private lateinit var spinnerProductCountry: CustomSearchableSpinner
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
    private lateinit var btnSave: LinearLayout

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

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>
    private var parentAccountList: MutableList<ParentAccountsData?> = java.util.ArrayList()
    private var parentAccountListStrings: MutableList<String> = java.util.ArrayList()

    private var saleAccountId = 0
    private var purchaseAccountId = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_product_detail, container, false)

        val gson = Gson()
        loginModel = gson.fromJson(
            SharedPreference.getSimpleString(context, Constants.userData),
            LoginModel::class.java
        )

        initViews()
        enableDisableViews(false)


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
        etVarietyName.isEnabled = b
        etSupplierNote.isEnabled = b
        tvIncomeDate.isEnabled = b
        etLotWeight.isEnabled = b
        etSupplierTare.isEnabled = b
        etBuyingPrice.isEnabled = b
        etSupplierInvoice.isEnabled = b
        etIncomeQuantity.isEnabled = b
        etPackQuantity.isEnabled = b
        etLotNo.isEnabled = b
        etOtherCost.isEnabled = b
        etShipCost.isEnabled = b
        etSellingPrice.isEnabled = b
        etSellingTare.isEnabled = b
        etComment.isEnabled = b
        spinnerPurchaseAccount.isEnabled = b
        spinnerSaleAccount.isEnabled = b
        etSupplierVat.isEnabled = b
        etInterfelTax.isEnabled = b
        cross1.isEnabled = b
        cross2.isEnabled = b
        cross3.isEnabled = b
        photo1.isEnabled = b
        photo2.isEnabled = b
        photo3.isEnabled = b

        spinnerProductName.isEnabled = b
        spinnerSupplierName.isEnabled = b
        spinnerSellingPack.isEnabled = b
        spinnerProductCalibre.isEnabled = b
        spinnerSellingUnit.isEnabled = b
        spinnerSellingVat.isEnabled = b
        spinnerIncomeUnit.isEnabled = b
        spinnerProductClasses.isEnabled = b
        spinnerProductCountry.isEnabled = b
    }


    private fun initViews() {
        selectedProductOrCategoryId = arguments?.getInt("id")!!
        fromEdit = arguments?.getBoolean("fromEdit", false)!!
        fromEditCheckForProductData = fromEdit
        if (fromEdit)
            selectedLotId = arguments?.getInt("lotId")!!

        btnSave = v.findViewById(R.id.btn_save)
        btnSave.setOnClickListener { validate() }

        spinnerProductName = v.findViewById(R.id.spinnerProductName)
        etVarietyName = v.findViewById(R.id.varietyName)
        spinnerSupplierName = v.findViewById(R.id.spinnerSupplierName)
        etSupplierNote = v.findViewById(R.id.etSupplierNote)
        tvIncomeDate = v.findViewById(R.id.tvIncomeDate)
        tvIncomeDate.setOnClickListener { showDatePicker() }
        etLotWeight = v.findViewById(R.id.etLotWeight)
        etSupplierTare = v.findViewById(R.id.etSupplierTare)
        etBuyingPrice = v.findViewById(R.id.etBuyingPrice)
        etSupplierInvoice = v.findViewById(R.id.etSupplierInvoice)
        etIncomeQuantity = v.findViewById(R.id.etIncomeQuantity)
        etPackQuantity = v.findViewById(R.id.etPackQuantity)
        spinnerProductCalibre = v.findViewById(R.id.spinnerProductCalibre)
        etLotNo = v.findViewById(R.id.etLotNo)
        etOtherCost = v.findViewById(R.id.etOtherCost)
        etShipCost = v.findViewById(R.id.etShipCost)
        etSellingPrice = v.findViewById(R.id.etSellingPrice)
        etSellingTare = v.findViewById(R.id.etSellingTare)
        etComment = v.findViewById(R.id.etComment)
        etSupplierVat = v.findViewById(R.id.etSupplierVat)
        spinnerSellingUnit = v.findViewById(R.id.spinnerSellingUnit)
        spinnerSellingPack = v.findViewById(R.id.spinnerSellingPack)
        spinnerSellingVat = v.findViewById(R.id.spinnerSellingVat)
        spinnerIncomeUnit = v.findViewById(R.id.spinnerIncomeUnit)
        spinnerProductClasses = v.findViewById(R.id.spinnerProductClasses)
        spinnerProductCountry = v.findViewById(R.id.spinnerProductCountry)
        etInterfelTax = v.findViewById(R.id.etInterfelTax)
        spinnerPurchaseAccount = v.findViewById(R.id.spinnerPurchaseAccount)
        spinnerSaleAccount = v.findViewById(R.id.spinnerSaleAccount)

        cross1 = v.findViewById(R.id.cross1)
        cross2 = v.findViewById(R.id.cross2)
        cross3 = v.findViewById(R.id.cross3)
        cross1.setOnClickListener(this)
        cross2.setOnClickListener(this)
        cross3.setOnClickListener(this)

        photo1 = v.findViewById(R.id.photo1)
        upl1 = v.findViewById(R.id.upl1)
        photo2 = v.findViewById(R.id.photo2)
        upl2 = v.findViewById(R.id.upl2)
        photo3 = v.findViewById(R.id.photo3)
        upl3 = v.findViewById(R.id.upl3)

        photo1.setOnClickListener(this)
        photo2.setOnClickListener(this)
        photo3.setOnClickListener(this)

        getParentAccounts()

    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                tvIncomeDate.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                incomeDateId = "$day-${monthOfYear + 1}-$year"
            },
            year,
            month,
            day
        )

        datePicker.show()
    }


    private fun validate() {

        if (etVarietyName.text.toString().isBlank()) {
            error_varitey.visibility = View.VISIBLE
            error_varitey.setText(R.string.pro_detail_error_variety)
            AppUtils.setBackground(etVarietyName, R.drawable.input_border_bottom_red)
            etVarietyName.requestFocus()
            return
        } else {
            AppUtils.setBackground(etVarietyName, R.drawable.input_border_bottom)
            error_varitey.visibility = View.INVISIBLE
        }

        if (etSupplierNote.text.toString().isBlank()) {
            error_supplier_note.visibility = View.VISIBLE
            error_supplier_note.setText(R.string.pro_detail_error_supplier_note_no)
            AppUtils.setBackground(etSupplierNote, R.drawable.input_border_bottom_red)
            etSupplierNote.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSupplierNote, R.drawable.input_border_bottom)
            error_supplier_note.visibility = View.INVISIBLE
        }

        if (tvIncomeDate.text.toString().isBlank()) {
            error_incomedate.visibility = View.VISIBLE
            error_incomedate.setText(R.string.pro_detail_error_incoming_date)
            AppUtils.setBackground(tvIncomeDate, R.drawable.input_border_bottom_red)
            tvIncomeDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvIncomeDate, R.drawable.input_border_bottom)
            error_incomedate.visibility = View.INVISIBLE
        }

        if (etLotWeight.text.toString().isBlank()) {
            error_lot_g_weight.visibility = View.VISIBLE
            error_lot_g_weight.setText(R.string.pro_detail_error_total_weight)
            AppUtils.setBackground(etLotWeight, R.drawable.input_border_bottom_red)
            etLotWeight.requestFocus()
            return
        } else {
            AppUtils.setBackground(etLotWeight, R.drawable.input_border_bottom)
            error_lot_g_weight.visibility = View.INVISIBLE
        }

        if (etSupplierTare.text.toString().isBlank()) {
            error_suplier_tare.visibility = View.VISIBLE
            error_suplier_tare.setText(R.string.pro_detail_error_supplier_tare)
            AppUtils.setBackground(etSupplierTare, R.drawable.input_border_bottom_red)
            etSupplierTare.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSupplierTare, R.drawable.input_border_bottom)
            error_suplier_tare.visibility = View.INVISIBLE
        }

        if (etBuyingPrice.text.toString().isBlank()) {
            error_buying_price.visibility = View.VISIBLE
            error_buying_price.setText(R.string.pro_detail_error_buying_price)
            AppUtils.setBackground(etBuyingPrice, R.drawable.input_border_bottom_red)
            etBuyingPrice.requestFocus()
            return
        } else {
            AppUtils.setBackground(etBuyingPrice, R.drawable.input_border_bottom)
            error_buying_price.visibility = View.INVISIBLE
        }

        if (etSupplierInvoice.text.toString().isBlank()) {
            error_suplier_invoice.visibility = View.VISIBLE
            error_suplier_invoice.setText(R.string.pro_detail_error_supplier_invoice_no)
            AppUtils.setBackground(etSupplierInvoice, R.drawable.input_border_bottom_red)
            etSupplierInvoice.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSupplierInvoice, R.drawable.input_border_bottom)
            error_suplier_invoice.visibility = View.INVISIBLE
        }

        if (etIncomeQuantity.text.toString().isBlank()) {
            error_icom_qty.visibility = View.VISIBLE
            error_icom_qty.setText(R.string.pro_detail_error_incoming_quantity)
            AppUtils.setBackground(etIncomeQuantity, R.drawable.input_border_bottom_red)
            etIncomeQuantity.requestFocus()
            return
        } else {
            AppUtils.setBackground(etIncomeQuantity, R.drawable.input_border_bottom)
            error_icom_qty.visibility = View.INVISIBLE
        }

        if (etPackQuantity.text.toString().isBlank()) {
            error_pack_qty.visibility = View.VISIBLE
            error_pack_qty.setText(R.string.pro_detail_error_quantity_in_packing)
            AppUtils.setBackground(etPackQuantity, R.drawable.input_border_bottom_red)
            etPackQuantity.requestFocus()
            return
        } else {
            AppUtils.setBackground(etPackQuantity, R.drawable.input_border_bottom)
            error_pack_qty.visibility = View.INVISIBLE
        }

        if (etLotNo.text.toString().isBlank()) {
            error_lot_no.visibility = View.VISIBLE
            error_lot_no.setText(R.string.pro_detail_error_lot_no)
            AppUtils.setBackground(etLotNo, R.drawable.input_border_bottom_red)
            etLotNo.requestFocus()
            return
        } else {
            AppUtils.setBackground(etLotNo, R.drawable.input_border_bottom)
            error_lot_no.visibility = View.INVISIBLE
        }

        if (etSupplierVat.text.toString().isBlank()) {
            error_sup_vat.visibility = View.VISIBLE
            error_sup_vat.setText(R.string.pro_detail_error_supplier_vat)
            AppUtils.setBackground(etSupplierVat, R.drawable.input_border_bottom_red)
            etSupplierVat.requestFocus()
            return
        } else {
            AppUtils.setBackground(etSupplierVat, R.drawable.input_border_bottom)
            error_sup_vat.visibility = View.INVISIBLE
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

        if (etShipCost.text.toString().isBlank()) {
            error_ship_cost.visibility = View.VISIBLE
            error_ship_cost.setText(R.string.pro_detail_error_shipping_cost)
            AppUtils.setBackground(etShipCost, R.drawable.input_border_bottom_red)
            etShipCost.requestFocus()
            return
        } else {
            AppUtils.setBackground(etShipCost, R.drawable.input_border_bottom)
            error_ship_cost.visibility = View.INVISIBLE
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

        if (etComment.text.toString().isBlank()) {
            error_comment.visibility = View.VISIBLE
            error_comment.setText(R.string.pro_detail_error_comment)
            AppUtils.setBackground(etComment, R.drawable.round_box_stroke_red)
            etComment.requestFocus()
            return
        } else {
            AppUtils.setBackground(etComment, R.drawable.round_box_light_stroke)
            error_comment.visibility = View.INVISIBLE
        }

        if (hashMap.size == 0) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.pro_detail_error_product_image),
                false
            )
            return
        }

//        if (fromEdit) {
//            updateLot()
//        } else {
//            postProductData()
//        }

    }


    private fun getParentAccounts() {
        AppUtils.showDialog(context!!)
        val call =
            RetrofitClient.getClient(context!!).create(Api::class.java).getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
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

//    private fun postProductData() {
//        AppUtils.showDialog(context!!)
//        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
//
//        val multipartBodiesList: MutableList<MultipartBody.Part>? = ArrayList()
//
//        for (i in 0 until hashMap.size)
//            if (hashMap.containsKey(i))
//                if (!hashMap[i]!!.isURL)
//                    multipartBodiesList?.add(createFileMultiPart(hashMap[i]?.path!!, "files[]", ""))
//
//        val multipartBodiesArray: Array<MultipartBody.Part>? = multipartBodiesList?.toTypedArray()
//
//
//        val call = api.postProductData(
//            createPlainRequestBody(etVarietyName.text.toString()),
//            createPlainRequestBody(productNameId.toString()),
//            createPlainRequestBody(supplierId.toString()),
//            createPlainRequestBody(etSupplierNote.text.toString()),
//            createPlainRequestBody(etLotWeight.text.toString()),
//            createPlainRequestBody(etSupplierTare.text.toString()),
//            createPlainRequestBody(etIncomeQuantity.text.toString()),
//            createPlainRequestBody(etBuyingPrice.text.toString()),
//            createPlainRequestBody(etPackQuantity.text.toString()),
//            createPlainRequestBody(sizeId.toString()),
//            createPlainRequestBody(countryId.toString()),
//            createPlainRequestBody(etSupplierVat.text.toString()),
//            createPlainRequestBody(etOtherCost.text.toString()),
//            createPlainRequestBody(etShipCost.text.toString()),
//            createPlainRequestBody(incomeUnitId.toString()),
//            createPlainRequestBody(sellingUnitId.toString()),
//            createPlainRequestBody(sellingPackId.toString()),
//            createPlainRequestBody(etSellingPrice.text.toString()),
//            createPlainRequestBody(etSellingTare.text.toString()),
//            createPlainRequestBody(sellingVatId.toString()),
//            createPlainRequestBody(etInterfelTax.text.toString()),
//            createPlainRequestBody(etComment.text.toString()),
//            createPlainRequestBody("0"),
//            createPlainRequestBody("0"),
//            createPlainRequestBody(classesId.toString()),
//            createPlainRequestBody(tvIncomeDate.text.toString()),
//            multipartBodiesArray
//        )
//        RetrofitClient.apiCall(call, this, "PostProductData")
//
//    }
//
//    private fun updateLot() {
//        AppUtils.showDialog(context!!)
//        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
//
//        val multipartBodiesList: MutableList<MultipartBody.Part>? = ArrayList()
//
//        for (i in 0 until hashMap.size)
//            if (hashMap.containsKey(i))
//                if (!hashMap[i]!!.isURL)
//                    multipartBodiesList?.add(createFileMultiPart(hashMap[i]?.path!!, "files[]", ""))
//
//        val multipartBodiesArray: Array<MultipartBody.Part>? = multipartBodiesList?.toTypedArray()
//
//
//        val call = api.updateProductData(
//            selectedLotId,
//            createPlainRequestBody(etVarietyName.text.toString()),
//            createPlainRequestBody(productNameId.toString()),
//            createPlainRequestBody(supplierId.toString()),
//            createPlainRequestBody(etSupplierNote.text.toString()),
//            createPlainRequestBody(etLotWeight.text.toString()),
//            createPlainRequestBody(etSupplierTare.text.toString()),
//            createPlainRequestBody(etIncomeQuantity.text.toString()),
//            createPlainRequestBody(etBuyingPrice.text.toString()),
//            createPlainRequestBody(etPackQuantity.text.toString()),
//            createPlainRequestBody(sizeId.toString()),
//            createPlainRequestBody(countryId.toString()),
//            createPlainRequestBody(etSupplierVat.text.toString()),
//            createPlainRequestBody(etOtherCost.text.toString()),
//            createPlainRequestBody(etShipCost.text.toString()),
//            createPlainRequestBody(incomeUnitId.toString()),
//            createPlainRequestBody(sellingUnitId.toString()),
//            createPlainRequestBody(sellingPackId.toString()),
//            createPlainRequestBody(etSellingPrice.text.toString()),
//            createPlainRequestBody(etSellingTare.text.toString()),
//            createPlainRequestBody(sellingVatId.toString()),
//            createPlainRequestBody(etInterfelTax.text.toString()),
//            createPlainRequestBody(etComment.text.toString()),
//            createPlainRequestBody("0"),
//            createPlainRequestBody("0"),
//            createPlainRequestBody(classesId.toString()),
//            createPlainRequestBody(tvIncomeDate.text.toString()),
//            multipartBodiesArray
//        )
//        RetrofitClient.apiCall(call, this, "UpdateLot")
//
//    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.photo1 -> getImage(PRODUCT_PHOTO1_REQUEST_CODE)
            R.id.photo2 -> getImage(PRODUCT_PHOTO2_REQUEST_CODE)
            R.id.photo3 -> getImage(PRODUCT_PHOTO3_REQUEST_CODE)
            R.id.cross1 -> deleteImage(cross1, upl1, photo1, 0)
            R.id.cross2 -> deleteImage(cross2, upl2, photo2, 1)
            R.id.cross3 -> deleteImage(cross3, upl3, photo3, 2)
        }
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
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
                val productDetailModel =
                    gson.fromJson(jsonObject.toString(), ProductDetailModel::class.java)

                updateViews(productDetailModel)
            }
            "PostProductData" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as ProManagementFragment).productList.clear()
                (parentFragment as ProManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as ProManagementFragment).getProducts(0)
            }
            "UpdateLot" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as ProManagementFragment).productList.clear()
                (parentFragment as ProManagementFragment).pullToRefresh.isRefreshing = true
                (parentFragment as ProManagementFragment).getProducts(0)
            }
            "DeleteFiles" -> {
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

        spinnerProductName.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerProductName.setTitle(resources.getString(R.string.productSpinnerTitle))

        val productSpinnerAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, productStrings
        )
        productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductName.adapter = productSpinnerAdapter
        spinnerProductName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        spinnerSupplierName.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerSupplierName.setTitle(resources.getString(R.string.supplierSpinnerTitle))

        val supplierSpinnerAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, supplierStrings
        )
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSupplierName.adapter = supplierSpinnerAdapter
        spinnerSupplierName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    }

    private fun setUpSpinners() {

        /*Origin Spinner*/
        val countriesStrings = ArrayList<String>()

        for (i in countriesList.indices) {
            countriesStrings.add(countriesList[i].name)
        }
        spinnerProductCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerProductCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))

        val countriesAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, countriesStrings
        )
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCountry.adapter = countriesAdapter
        spinnerProductCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        /*spinnerIncomeUnit*/
        val incomeUnitStrings = ArrayList<String>()

        for (i in unitsList.indices) {
            incomeUnitStrings.add(unitsList[i].name)
        }
        spinnerIncomeUnit.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerIncomeUnit.setTitle(resources.getString(R.string.incomeUnitSpinnerTitle))

        val incomeUnitAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, incomeUnitStrings
        )
        incomeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIncomeUnit.adapter = incomeUnitAdapter
        spinnerIncomeUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        /*spinnerProductClasses*/
        val classesStrings = ArrayList<String>()

        for (i in classesList.indices) {
            classesStrings.add(classesList[i].name)
        }

        spinnerProductClasses.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerProductClasses.setTitle(resources.getString(R.string.productClassesSpinnerTitle))

        val categoriesAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, classesStrings
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductClasses.adapter = categoriesAdapter
        spinnerProductClasses.onItemSelectedListener =
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

        spinnerProductCalibre.setPositiveButton(resources.getString(R.string.spinnerBtnClose))

        spinnerProductCalibre.setTitle(resources.getString(R.string.sizeSpinnerTitle))

        val calibreAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, calibreStrings
        )
        calibreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCalibre.adapter = calibreAdapter
        spinnerProductCalibre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
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
        } else
            AppUtils.dismissDialog()
    }

    private fun updateViews(productDetailModel: ProductDetailModel?) {

        Log.d("", "Product data response")
        checkToNotLoadGradients = false
        etVarietyName.text = (productDetailModel!!.data.product_variety)
        etSupplierNote.text = productDetailModel.data.supplier_note
        tvIncomeDate.text = productDetailModel.data.incoming_date
        etLotWeight.text = productDetailModel.data.gross_weight
        etSupplierTare.text = productDetailModel.data.supplier_tare
        etBuyingPrice.text = productDetailModel.data.buying_price
        etSupplierInvoice.text = (productDetailModel.data.invoice_no)
        etLotNo.text = (productDetailModel.data.lot_no)
        etSupplierVat.text = (productDetailModel.data.supplier_vat)
        etOtherCost.text = (productDetailModel.data.other_cost)
        etShipCost.text = (productDetailModel.data.shipping_cost)
        etIncomeQuantity.text = (productDetailModel.data.stock)
        etPackQuantity.text = (productDetailModel.data.income_packing_quantity)
        etSellingPrice.text = (productDetailModel.data.selling_price)
        etSellingTare.text = (productDetailModel.data.selling_tare)
        etInterfelTax.text = (productDetailModel.data.interfel_tax)
        etComment.text = (productDetailModel.data.comment)

        for (i in productSupplierModel!!.data.products.indices) {
            if (productDetailModel.data.product_id == productSupplierModel!!.data.products[i].id) {
                spinnerProductName.setSelection(i)
            }
        }

        for (i in productSupplierModel!!.data.suppliers.indices) {
            if (productDetailModel.data.supplier_id == productSupplierModel!!.data.suppliers[i].id)
                spinnerSupplierName.setSelection(i)
        }

        for (i in unitsList.indices) {
            if (productDetailModel.data.income_unit_id == unitsList[i].id)
                spinnerIncomeUnit.setSelection(i)
        }

        for (i in countriesList.indices) {
            if (productDetailModel.data.country_id == countriesList[i].id)
                spinnerProductCountry.setSelection(i)
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
                spinnerProductCalibre.setSelection(i)
        }

        for (i in classesList.indices) {
            if (productDetailModel.data.class_id == classesList[i].id)
                spinnerProductClasses.setSelection(i)
        }

        for (i in parentAccountList.indices) {
            if (productDetailModel.data.sale_account_id == parentAccountList[i]?.id)
                spinnerSaleAccount.setSelection(i)
        }

        for (i in parentAccountList.indices) {
            if (productDetailModel.data.purchase_account_id == parentAccountList[i]?.id)
                spinnerPurchaseAccount.setSelection(i)
        }

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
            .into(detail_pro_image)

        AppUtils.dismissDialog()
    }


}