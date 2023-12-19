package com.minbio.erp.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.main.models.SettingsModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@Suppress("DEPRECATION")
class AppUtils {

    companion object {

        private var dialog: ProgressDialog? = null

        fun showDialog(context: Context) {
            try {
                hideKeyboard(context as AppCompatActivity)
                if (dialog != null) {
                    if (!dialog?.isShowing!!) {
                        dialog?.show()
                    }
                } else
                    initializedDialog(context)

            } catch (e: Exception) {
                dialog = null
                e.printStackTrace()
            }
        }

        private fun initializedDialog(context: Context) {
            dialog = ProgressDialog(context)
            dialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog?.setTitle(context.resources.getString(R.string.dialogTitle))
            dialog?.setMessage(context.resources.getString(R.string.dialogLoadingText))
            dialog?.isIndeterminate = true
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.show()
        }

        fun dismissDialog() {
            try {
                if (dialog != null) {
                    if (dialog?.isShowing!!)
                        dialog?.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return (netInfo != null && netInfo.isConnectedOrConnecting
                    && cm.activeNetworkInfo!!.isAvailable
                    && cm.activeNetworkInfo!!.isConnected)
        }


        fun hideKeyboard(context: Activity) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = context.currentFocus
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }

        fun validateEmail(email: String): Boolean {
            return !(email.isEmpty() || !isValidEmail(email))
        }

        private fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(
                email
            )
                .matches()
        }

        fun validatePhone(phone: String): Boolean {
            return !(phone.isEmpty() || !isValidPhone(phone));
        }

        fun isValidPhone(phone: String): Boolean {
            return !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone)
                .matches()
        }

        fun setBackground(view: View, bg: Int) {
            view.setBackgroundResource(bg)
        }

        fun showToast(activity: Activity?, message: String, isSuccess: Boolean) {
            try {
                if (isSuccess)
                    Sneaker.with(activity!!)
                        .setTitle(activity.resources.getString(R.string.toastSuccessTitle))
                        .autoHide(true)
                        .setDuration(1800)
                        .setMessage(message, activity.resources.getColor(R.color.colorWhite))
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .sneakSuccess()
                else
                    Sneaker.with(activity!!)
                        .setTitle(activity.resources.getString(R.string.toastErrorTitle))
                        .autoHide(true)
                        .setDuration(1800)
                        .setMessage(message, activity.resources.getColor(R.color.colorWhite))
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .sneakError()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun createPlainRequestBody(string: String): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), string)

        fun createFileMultiPart(
            imagePath: String,
            key: String,
            ext: String
        ): MultipartBody.Part {

            val file = File(imagePath)

            val requestBody = if (ext == ".pdf")
                file.asRequestBody("application/pdf".toMediaTypeOrNull())
            else
                file.asRequestBody("image/*".toMediaTypeOrNull())

            return MultipartBody.Part.createFormData(key, file.name, requestBody)
        }

        fun getLoginModel(context: Context): LoginModel {
            val gson = Gson()
            return gson.fromJson(
                SharedPreference.getSimpleString(context, Constants.userData),
                LoginModel::class.java
            )
        }

        fun getDateFormat(
            context: Context,
            day: Int,
            monthOfYear: Int,
            year: Int
        ): String {
            val gson = Gson()
            val settingModel = gson.fromJson(
                SharedPreference.getSimpleString(context, Constants.settingsData),
                SettingsModel::class.java
            )

            for (i in settingModel.data.date_formats.indices)
                if (settingModel.data.date_formats[i].value == settingModel.data.settings.date_format.toInt())
                    when (settingModel.data.date_formats[i].format) {
                        "YYYY-MM-DD" -> {
                            return "$year-$monthOfYear-$day"
                        }
                        "DD-MM-YYYY" -> {
                            return "$day-$monthOfYear-$year"
                        }
                        "YYYY/MM/DD" -> {
                            return "$year/$monthOfYear/$day"
                        }
                        "DD/MM/YYYY" -> {
                            return "$day/$monthOfYear/$year"
                        }
                        else -> {
                            return "$day-$monthOfYear-$year"
                        }
                    }
            return ""
        }

        fun preventTwoClick(view: View) {
            view.isEnabled = false
            view.postDelayed({ view.isEnabled = true }, 500)
        }

        fun round(value: Double, places: Int): Double {
            var value = value
            require(places >= 0)
            val factor = Math.pow(10.0, places.toDouble()).toLong()
            value *= factor
            val tmp = Math.round(value)
            return tmp.toDouble() / factor
        }

        fun appendCurrency(p: String, context: Context): String {
            return "$p${
                SharedPreference.getSimpleString(
                    context,
                    Constants.defaultCurrency
                )
            }"
        }

    }
}