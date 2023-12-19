package com.minbio.erp.quality_management.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.minbio.erp.product_management.model.PathCheck
import com.minbio.erp.quality_management.models.QualityTrashedModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.createPlainRequestBody
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.CustomSearchableSpinner
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_quality_trashed.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File
import java.util.*

class QualityTrashed : Fragment(), View.OnClickListener, ResponseCallBack {

    private lateinit var v: View

    private var alertDialog: AlertDialog? = null
    private val hashMap: HashMap<Int, PathCheck> = HashMap<Int, PathCheck>()

    private lateinit var tv_trash_date: TextView
    private lateinit var et_qt_quantity_income: EditText
    private lateinit var et_trashed_email: EditText
    private lateinit var et_trashed_comment: EditText

    private lateinit var lotNoSpinner: CustomSearchableSpinner
    private lateinit var trashedTopicSpinner: CustomSearchableSpinner
    private lateinit var spinner_handler_name_trashed: CustomSearchableSpinner
    private lateinit var statusSpinner: CustomSearchableSpinner

    private lateinit var photo1: ImageView
    private lateinit var photo2: ImageView
    private lateinit var photo3: ImageView
    private lateinit var upl1: ImageView
    private lateinit var upl2: ImageView
    private lateinit var upl3: ImageView
    private lateinit var cross1: ImageView
    private lateinit var cross2: ImageView
    private lateinit var cross3: ImageView

    private lateinit var btnSend: LinearLayout

    private var selectedVarietyId = 0
    private var lotId = 0
    private var handlerNameId = 0
    private var trashTopicId = 0
    private var statusId = ""
    private var trashDateId = ""
    private lateinit var qualityTrashedModel: QualityTrashedModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_quality_trashed, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        selectedVarietyId = arguments?.getInt("VarietyId")!!

        tv_trash_date = v.findViewById(R.id.tv_trash_date)
        tv_trash_date.setOnClickListener { showDatePicker() }
        et_qt_quantity_income = v.findViewById(R.id.et_qt_quantity_income)
        et_trashed_email = v.findViewById(R.id.et_trashed_email)
        et_trashed_comment = v.findViewById(R.id.et_trashed_comment)

        spinner_handler_name_trashed = v.findViewById(R.id.spinner_handler_name_trashed)
        lotNoSpinner = v.findViewById(R.id.spinner_lot_no_trashed)
        trashedTopicSpinner = v.findViewById(R.id.spinner_trashed_topic)
        statusSpinner = v.findViewById(R.id.spinner_qt_status)

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

        btnSend = v.findViewById(R.id.btn_qt_send)
        btnSend.setOnClickListener {
            validate()
        }

        if (selectedVarietyId != 0)
            getTrashedData()
        else
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSomethingIsNotRight), false
            )
    }

    private fun validate() {

        if (tv_trash_date.text.toString().isBlank()) {
            error_trash_date.visibility = View.VISIBLE
            error_trash_date.setText(R.string.qtErrorTrashedDate);
            AppUtils.setBackground(tv_trash_date, R.drawable.input_border_bottom_red)
            tv_trash_date.requestFocus()
            return
        } else {
            AppUtils.setBackground(tv_trash_date, R.drawable.input_border_bottom)
            error_trash_date.visibility = View.INVISIBLE
        }

        if (et_qt_quantity_income.text.toString().isBlank()) {
            error_qt_quantity_income.visibility = View.VISIBLE
            error_qt_quantity_income.setText(R.string.qtErrorQuantityIncomeUnite);
            AppUtils.setBackground(et_qt_quantity_income, R.drawable.input_border_bottom_red)
            et_qt_quantity_income.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_qt_quantity_income, R.drawable.input_border_bottom)
            error_qt_quantity_income.visibility = View.INVISIBLE
        }

        if (!AppUtils.validateEmail(et_trashed_email.text.toString())) {
            error_trashed_email.visibility = View.VISIBLE
            error_trashed_email.setText(R.string.qtErrorEmail);
            AppUtils.setBackground(et_trashed_email, R.drawable.input_border_bottom_red)
            et_trashed_email.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_trashed_email, R.drawable.input_border_bottom)
            error_trashed_email.visibility = View.INVISIBLE
        }

        if (et_trashed_comment.text.toString().isBlank()) {
            error_trashed_comment.visibility = View.VISIBLE
            error_trashed_comment.setText(R.string.qtErrorComment);
            AppUtils.setBackground(et_trashed_comment, R.drawable.round_box_stroke_red)
            et_trashed_comment.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_trashed_comment, R.drawable.round_box_light_stroke)
            error_trashed_comment.visibility = View.INVISIBLE
        }

        if (hashMap.size == 0) {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.qtErrorHandlerPhotos),
                false
            )
            return
        }

        postTrashedData()

    }

    private fun postTrashedData() {
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

        val call = api.postQualityTrashedData(
            createPlainRequestBody(lotId.toString()),
            createPlainRequestBody(trashDateId),
            createPlainRequestBody(et_qt_quantity_income.text.toString()),
            createPlainRequestBody(handlerNameId.toString()),
            createPlainRequestBody(et_trashed_email.text.toString()),
            createPlainRequestBody(trashTopicId.toString()),
            createPlainRequestBody(statusId),
            createPlainRequestBody(et_trashed_comment.text.toString()),
            multipartBodiesArray
        )
        RetrofitClient.apiCall(call, this, "PostTrashedData")
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                tv_trash_date.text =
                    AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                trashDateId = "$day-${monthOfYear + 1}-$year"
            },
            year,
            month,
            day
        )

        datePicker.show()
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

    private fun getTrashedData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getQualityTrashedData(selectedVarietyId)
        RetrofitClient.apiCall(call, this, "GetQualityTrashedData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "DeleteFiles" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
            }
            "GetQualityTrashedData" -> {
                val gson = Gson()
                qualityTrashedModel =
                    gson.fromJson(jsonObject.toString(), QualityTrashedModel::class.java)
                setUpSpinners()
            }
            "PostTrashedData" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                tv_trash_date.text = ""
                et_qt_quantity_income.setText("")
                et_trashed_email.setText("")
                et_trashed_comment.setText("")

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
        val lotNoItems: MutableList<String> = ArrayList()

        for (i in qualityTrashedModel.data.indices)
            lotNoItems.add(qualityTrashedModel.data[i].lot_no)

        val lotNoAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, lotNoItems)

        lotNoSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        lotNoSpinner.setTitle(resources.getString(R.string.lotNoSpinnerTitle))
        lotNoSpinner.adapter = lotNoAdapter
        lotNoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                lotId = qualityTrashedModel.data[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        val handlerNameItems: MutableList<String> = ArrayList()

        for (i in qualityTrashedModel.handlers.indices)
            handlerNameItems.add(qualityTrashedModel.handlers[i].first_name + " " + qualityTrashedModel.handlers[i].last_name)

        val handlerNameAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, handlerNameItems)

        spinner_handler_name_trashed.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinner_handler_name_trashed.setTitle(resources.getString(R.string.handlerNameSpinnerTitle))
        spinner_handler_name_trashed.adapter = handlerNameAdapter
        spinner_handler_name_trashed.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    handlerNameId =
                        qualityTrashedModel.handlers[adapterView.selectedItemPosition].id
                    AppUtils.hideKeyboard(activity!!)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        //trashedTopic Spinner
        val trashedTopicItems = ArrayList<String>()
        for (i in qualityTrashedModel.topics.indices)
            trashedTopicItems.add(qualityTrashedModel!!.topics[i].topic)

        val trashedTopicAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, trashedTopicItems)

        trashedTopicSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        trashedTopicSpinner.setTitle(resources.getString(R.string.trashTypeSpinnerTitle))
        trashedTopicSpinner.adapter = trashedTopicAdapter
        trashedTopicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                trashTopicId = qualityTrashedModel!!.topics[adapterView.selectedItemPosition].id
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //status Spinner
        val statusItems = ArrayList<String>()
        for (i in qualityTrashedModel.trashed_status.indices)
            statusItems.add(qualityTrashedModel.trashed_status[i])

        val statusAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, statusItems)
        statusSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        statusSpinner.setTitle(resources.getString(R.string.statusSpinnerTitle))
        statusSpinner.adapter = statusAdapter
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                statusId = qualityTrashedModel.trashed_status[adapterView.selectedItemPosition]
                AppUtils.hideKeyboard(activity!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


}