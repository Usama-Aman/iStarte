package com.minbio.erp.network

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.minbio.erp.R
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit


object RetrofitClient {


    private const val BASE_URL = "http://elxdrive.com/minbiov2/api/v1/"
    private lateinit var context: Context

    fun getClient(context: Context): Retrofit {

        val token = SharedPreference.getSimpleString(context, Constants.accessToken)
        this.context = context

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                    .build()
                chain.proceed(newRequest)
            }.build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getClientNoToken(context: Context): Retrofit {
        this.context = context

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(newRequest)
            }.build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun apiCall(call: Call<ResponseBody>, responseCallBack: ResponseCallBack, tag: String) {

        if (!AppUtils.isNetworkAvailable(context)) {
            AppUtils.dismissDialog()
            AppUtils.showToast(
                context as AppCompatActivity,
                context.resources.getString(R.string.no_internet_connection),
                false
            )
            return
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                try {
                    if (response.isSuccessful) {
                        responseCallBack.onSuccess(JSONObject(response.body()!!.string()), tag)
                    } else {
                        println("Exception in api $response")
                        val errorJSONObject = JSONObject(response.errorBody()!!.string())
                        responseCallBack.onError(errorJSONObject, tag)
                        Log.d("Exception", errorJSONObject.getString("message"))

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Response Failure", t.localizedMessage!!)
                responseCallBack.onException(t.localizedMessage, tag)
            }

        })
    }


}