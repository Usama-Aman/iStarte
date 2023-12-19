package com.minbio.erp.customer_management.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.minbio.erp.customer_management.CustomerManagementFragment
import com.minbio.erp.customer_management.adapter.MultiSelectSearchSpinner
import com.minbio.erp.customer_management.models.ComplaintData
import com.minbio.erp.customer_management.models.ComplaintItems
import com.minbio.erp.customer_management.models.CustomerComplaintModel
import com.minbio.erp.customer_management.models.CustomersData
import com.minbio.erp.product_management.model.PathCheck
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_customer_complaint.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CustomerComplaint : Fragment(), ResponseCallBack, View.OnClickListener {

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var orderNoSpinner: CustomSearchableSpinner
    private lateinit var complaintTopicSpinner: CustomSearchableSpinner
    private lateinit var statusSpinner: CustomSearchableSpinner
    private lateinit var productItemConstraint: ConstraintLayout
    private lateinit var itemsComplaintText: TextView
    private lateinit var article_code_number: TextView

    private var customerData: CustomersData? = null

    private var complaintData: ComplaintData? = null
    private var complaintItems: ArrayList<ComplaintItems> = ArrayList()

    private lateinit var photo1: ImageView
    private lateinit var photo2: ImageView
    private lateinit var photo3: ImageView
    private lateinit var upl1: ImageView
    private lateinit var upl2: ImageView
    private lateinit var upl3: ImageView
    private lateinit var cross1: ImageView
    private lateinit var cross2: ImageView
    private lateinit var cross3: ImageView

    private lateinit var et_cmcc_contact_name: EditText
    private lateinit var et_cmcc_complaint_email: EditText
    private lateinit var et_cmcc_mobile_number: EditText

    private var alertDialog: AlertDialog? = null
    private val hashMap: HashMap<Int, PathCheck> = HashMap<Int, PathCheck>()

    private var orderId = 0
    private var productItemId = ""
    private var statusId = ""
    private var complaintTypeId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_customer_complaint, container, false)

        initViews()

        return v;
    }

    private fun initViews() {
        customerData = arguments!!.getParcelable("CustomerData")

        article_code_number = v.findViewById(R.id.article_code_number)
        article_code_number.text =
            context!!.resources.getString(R.string.cmpLabelCustomerId, customerData?.id.toString())
        itemsComplaintText = v.findViewById(R.id.itemsComplaintText)
        productItemConstraint = v.findViewById(R.id.productItemConstraint)
        btnSave = v.findViewById(R.id.btn_cmcc_send)
        btnSave.setOnClickListener { validate() }
        et_cmcc_contact_name = v.findViewById(R.id.et_cmcc_contact_name)
        et_cmcc_complaint_email = v.findViewById(R.id.et_cmcc_email)
        et_cmcc_mobile_number = v.findViewById(R.id.et_cmcc_mobile_number)
        et_cmcc_contact_name.isEnabled = false
        et_cmcc_complaint_email.isEnabled = false
        et_cmcc_mobile_number.isEnabled = false

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

        if (customerData != null)
            getCustomerData()
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight),
                false
            )

        productItemConstraint.setOnClickListener {
            val multiSearchableSpinner = MultiSelectSearchSpinner(context!!, complaintItems)
            multiSearchableSpinner.setOnworkflowlistclicklistener { results ->
                productItemId = TextUtils.join(",", results)

                val itemsStr = ArrayList<String?>()
                for (i in complaintItems.indices) {
                    for (j in results.indices) {
                        if (results[j] == complaintItems[i].id) {
                            itemsStr.add(complaintItems[i].value)
                        }
                    }
                }
                itemsComplaintText.text = TextUtils.join(",", itemsStr)

            }
            multiSearchableSpinner.show()
        }
    }

    private fun getCustomerData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getCustomerComplaintData(customerData?.id!!)
        RetrofitClient.apiCall(call, this, "GetCustomerComplaintData")
    }

    private fun setupSpinners() {

        et_cmcc_contact_name.setText("${customerData?.name} ${customerData?.last_name}")
        et_cmcc_complaint_email.setText(customerData?.email)
        et_cmcc_mobile_number.setText("${customerData?.country_code} ${customerData?.phone}")

        //orderNo Spinner
        orderNoSpinner = v.findViewById(R.id.spinner_cmcc_order_no)
        val orderNoItems: MutableList<String> = ArrayList()

        for (i in complaintData!!.orders.indices)
            orderNoItems.add(complaintData!!.orders[i].value)

        val orderNoAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, orderNoItems)

        orderNoSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        orderNoSpinner.setTitle(resources.getString(R.string.orderNoSpinnerTitle))
        orderNoSpinner.adapter = orderNoAdapter
        orderNoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                orderId = complaintData!!.orders[adapterView.selectedItemPosition].id

                itemsComplaintText.text = ""

                for (j in complaintData!!.orders.indices)
                    if (orderId == complaintData!!.orders[j].id) {
                        complaintItems.clear()
                        complaintItems.addAll(complaintData!!.orders[j].items)
                        productItemId = ""
                    }

                for (k in complaintItems.indices)
                    complaintItems[k].isChecked = false

                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //complaintTopic Spinner
        complaintTopicSpinner = v.findViewById(R.id.spinner_cmcc_complaint_topic)
        val complaintTopicItems = ArrayList<String>()
        for (i in complaintData!!.types.indices)
            complaintTopicItems.add(complaintData!!.types[i].value)
        val complaintTopicAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, complaintTopicItems)

        complaintTopicSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        complaintTopicSpinner.setTitle(resources.getString(R.string.complaintTypeSpinnerTitle))
        complaintTopicSpinner.adapter = complaintTopicAdapter
        complaintTopicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                complaintTypeId = complaintData!!.types[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        //status Spinner
        statusSpinner = v.findViewById(R.id.spinner_cmcc_status)
        val statusItems = ArrayList<String>()
        for (i in complaintData!!.status.indices)
            statusItems.add(complaintData!!.status[i])

        val statusAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        statusSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        statusSpinner.setTitle(resources.getString(R.string.statusSpinnerTitle))
        statusSpinner.adapter = statusAdapter
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                statusId = complaintData!!.status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    private fun validate() {

        if (productItemId == "") {
            error_cmcc_product_item.visibility = View.VISIBLE
            error_cmcc_product_item.setText(R.string.cmCustComplaintErrorComplaintItems);
            error_cmcc_product_item.requestFocus()
            return
        } else {
            error_cmcc_product_item.visibility = View.INVISIBLE
        }


        if (et_cmcc_comment.text.toString().isBlank()) {
            error_cmcc_comment.visibility = View.VISIBLE
            error_cmcc_comment.setText(R.string.cmCustComplaintErrorComment);
            AppUtils.setBackground(et_cmcc_comment, R.drawable.round_box_stroke_red)
            et_cmcc_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_cmcc_comment, R.drawable.round_box_light_stroke)
            error_cmcc_comment.visibility = View.INVISIBLE
        }

        if (hashMap.size == 0) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.cmCustComplaintErrorComplaintPhoto),
                false
            )
            return
        }

        postComplaint()

    }

    private fun postComplaint() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
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

        val call = api.postComplaint(
            AppUtils.createPlainRequestBody(orderId.toString()),
            AppUtils.createPlainRequestBody(productItemId),
            AppUtils.createPlainRequestBody(complaintTypeId.toString()),
            AppUtils.createPlainRequestBody(et_cmcc_comment.text.toString()),
            AppUtils.createPlainRequestBody(customerData?.id.toString()),
            AppUtils.createPlainRequestBody(""),
            AppUtils.createPlainRequestBody(statusId),
            multipartBodiesArray
        )
        RetrofitClient.apiCall(call, this, "PostComplaintData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetCustomerComplaintData") {
            val gson = Gson()
            val model = gson.fromJson(jsonObject.toString(), CustomerComplaintModel::class.java)
            complaintData = model.data
            setupSpinners()
        } else if (tag == "PostComplaintData") {
            AppUtils.showToast(activity, jsonObject.getString("message"), true)
            (parentFragment as CustomerManagementFragment).customerTabCustomerComplaint.callOnClick()
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
            when (requestCode) {
                Constants.PRODUCT_PHOTO1_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!

                    if (files.size > 0) {
                        val fileUri = files[0].uri
                        startCrop(fileUri, Constants.PRODUCT_PHOTO1_CROP_REQUEST_CODE)
                    }
                }
                Constants.PRODUCT_PHOTO2_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!

                    if (files.size > 0) {
                        val fileUri = files[0].uri
                        startCrop(fileUri, Constants.PRODUCT_PHOTO2_CROP_REQUEST_CODE)
                    }
                }
                Constants.PRODUCT_PHOTO3_REQUEST_CODE -> {
                    val files: ArrayList<MediaFile> =
                        data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!

                    if (files.size > 0) {
                        val fileUri = files[0].uri
                        startCrop(fileUri, Constants.PRODUCT_PHOTO3_CROP_REQUEST_CODE)
                    }
                }
                Constants.PRODUCT_PHOTO1_CROP_REQUEST_CODE -> {

                    val cropperUri = UCrop.getOutput(data!!)

                    hashMap[0] = PathCheck(0, cropperUri?.path!!, false)
                    Glide.with(context!!).load(cropperUri).into(photo1)

                    upl1.visibility = View.GONE
                    cross1.visibility = View.VISIBLE
                    photo1.isEnabled = false

                }
                Constants.PRODUCT_PHOTO2_CROP_REQUEST_CODE -> {

                    val cropperUri = UCrop.getOutput(data!!)

                    hashMap[1] = PathCheck(0, cropperUri?.path!!, false)
                    Glide.with(context!!).load(cropperUri).into(photo2)

                    upl2.visibility = View.GONE
                    cross2.visibility = View.VISIBLE
                    photo2.isEnabled = false

                }
                Constants.PRODUCT_PHOTO3_CROP_REQUEST_CODE -> {

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.photo1 -> getImage(Constants.PRODUCT_PHOTO1_REQUEST_CODE)
            R.id.photo2 -> getImage(Constants.PRODUCT_PHOTO2_REQUEST_CODE)
            R.id.photo3 -> getImage(Constants.PRODUCT_PHOTO3_REQUEST_CODE)
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


}