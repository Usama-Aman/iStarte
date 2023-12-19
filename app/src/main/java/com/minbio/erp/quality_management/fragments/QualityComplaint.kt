package com.minbio.erp.quality_management.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.minbio.erp.product_management.model.PathCheck
import com.minbio.erp.quality_management.models.QualityComplaintData
import com.minbio.erp.quality_management.models.QualityComplaintModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_quality_complaint.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class QualityComplaint : Fragment(), ResponseCallBack, View.OnClickListener {

    private lateinit var v: View
    private lateinit var lotNoSpinner: CustomSearchableSpinner
    private lateinit var complaintTopicSpinner: CustomSearchableSpinner
    private lateinit var statusSpinner: CustomSearchableSpinner
    private lateinit var tvSupplier: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvConactName: TextView
    private lateinit var btnSend: LinearLayout

    private lateinit var et_qc_comment: EditText

    private lateinit var photo1: ImageView
    private lateinit var photo2: ImageView
    private lateinit var photo3: ImageView
    private lateinit var upl1: ImageView
    private lateinit var upl2: ImageView
    private lateinit var upl3: ImageView
    private lateinit var cross1: ImageView
    private lateinit var cross2: ImageView
    private lateinit var cross3: ImageView

    private lateinit var complaintMainLayout: ConstraintLayout

    private var selectedVarietyId = 0
    private lateinit var qualityComplaintModel: QualityComplaintModel
    private var complaintTypeId: Int = 0
    private var statusId: String = ""
    private var lotId: Int = 0
    private var supplierId: Int = 0

    private var alertDialog: AlertDialog? = null
    private val hashMap: HashMap<Int, PathCheck> = HashMap<Int, PathCheck>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_quality_complaint, container, false)


        initViews()

        return v
    }

    private fun initViews() {
        selectedVarietyId = arguments?.getInt("VarietyId")!!

        et_qc_comment = v.findViewById(R.id.et_qc_comment)
        lotNoSpinner = v.findViewById(R.id.spinner_lot_no_quality)
        complaintTopicSpinner = v.findViewById(R.id.spinner_qc_complaint_topic)
        statusSpinner = v.findViewById(R.id.spinner_qc_status)

        tvSupplier = v.findViewById(R.id.supplier_quality)
        tvEmail = v.findViewById(R.id.tv_qc_email)
        tvPhone = v.findViewById(R.id.tv_qc_mobile_number)
        tvConactName = v.findViewById(R.id.tv_qc_contact_name)
        complaintMainLayout = v.findViewById(R.id.complaintMainLayout)

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


        btnSend = v.findViewById(R.id.btn_qc_send)
        btnSend.setOnClickListener {
            validate()
        }

        if (selectedVarietyId != 0)
            getQualityComplaintData()
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight), false
            )
    }

    private fun validate() {

        if (et_qc_comment.text.toString().isBlank()) {
            error_qc_comment.visibility = View.VISIBLE
            error_qc_comment.setText(R.string.qcErrorComment);
            AppUtils.setBackground(et_qc_comment, R.drawable.round_box_stroke_red)
            et_qc_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_qc_comment, R.drawable.round_box_light_stroke)
            error_qc_comment.visibility = View.INVISIBLE
        }

        if (hashMap.size == 0) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.qcErrorComplaintPhoto),
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

        val call = api.postQualityComplaint(
            AppUtils.createPlainRequestBody(selectedVarietyId.toString()),
            AppUtils.createPlainRequestBody(lotId.toString()),
            AppUtils.createPlainRequestBody(complaintTypeId.toString()),
            AppUtils.createPlainRequestBody(supplierId.toString()),
            AppUtils.createPlainRequestBody(statusId),
            AppUtils.createPlainRequestBody(et_qc_comment.text.toString()),
            multipartBodiesArray
        )
        RetrofitClient.apiCall(call, this, "PostComplaintData")
    }

    private fun getQualityComplaintData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getQualityComplaintData(selectedVarietyId)
        RetrofitClient.apiCall(call, this, "GetQualityComplaintData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetQualityComplaintData") {
            val gson = Gson()
            qualityComplaintModel =
                gson.fromJson(jsonObject.toString(), QualityComplaintModel::class.java)
            setUpSpinners()
        } else if (tag == "PostComplaintData") {
            AppUtils.showToast(activity, jsonObject.getString("message"), true)
            et_qc_comment.setText("")

            hashMap.clear()
            photo1.setImageDrawable(null)
            photo2.setImageDrawable(null)
            photo3.setImageDrawable(null)

            photo1.isEnabled = true
            photo2.isEnabled = true
            photo3.isEnabled = true
            upl1.visibility = View.VISIBLE
            upl2.visibility = View.VISIBLE
            upl3.visibility = View.VISIBLE
            cross1.visibility = View.GONE
            cross2.visibility = View.GONE
            cross3.visibility = View.GONE
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

    private fun setUpSpinners() {
        //lotNo Spinner
        val orderNoItems: MutableList<String> = ArrayList()

        for (i in qualityComplaintModel.data.indices)
            orderNoItems.add(qualityComplaintModel.data[i].lot_no)

        val orderNoAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, orderNoItems)

        lotNoSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        lotNoSpinner.setTitle(resources.getString(R.string.lotNoSpinnerTitle))
        lotNoSpinner.adapter = orderNoAdapter
        lotNoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                lotId = qualityComplaintModel.data[adapterView.selectedItemPosition].id
                supplierId =
                    qualityComplaintModel.data[adapterView.selectedItemPosition].supplier.id
                renderData(qualityComplaintModel.data[adapterView.selectedItemPosition])
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        //complaintTopic Spinner
        val complaintTopicItems = ArrayList<String>()
        for (i in qualityComplaintModel.types.indices)
            complaintTopicItems.add(qualityComplaintModel!!.types[i].value)
        val complaintTopicAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, complaintTopicItems)

        complaintTopicSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        complaintTopicSpinner.setTitle(resources.getString(R.string.complaintTypeSpinnerTitle))
        complaintTopicSpinner.adapter = complaintTopicAdapter
        complaintTopicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                complaintTypeId = qualityComplaintModel!!.types[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //status Spinner
        val statusItems = ArrayList<String>()
        for (i in qualityComplaintModel.complaint_status.indices)
            statusItems.add(qualityComplaintModel.complaint_status[i])

        val statusAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        statusSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        statusSpinner.setTitle(resources.getString(R.string.statusSpinnerTitle))
        statusSpinner.adapter = statusAdapter
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                statusId = qualityComplaintModel.complaint_status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    @SuppressLint("SetTextI18n")
    private fun renderData(qualityComplaintData: QualityComplaintData) {
        tvEmail.text = qualityComplaintData.supplier.email
        tvPhone.text =
            "${qualityComplaintData.supplier.country_code} ${qualityComplaintData.supplier.phone}"
        tvConactName.text = qualityComplaintData.supplier.contact_full_name
        tvSupplier.text = qualityComplaintData.supplier.company_name
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
