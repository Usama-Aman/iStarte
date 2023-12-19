package com.minbio.erp.network

import org.json.JSONObject

interface ResponseCallBack {

    fun onSuccess(jsonObject: JSONObject, tag: String)
    fun onError(jsonObject: JSONObject, tag: String)
    fun onException(message: String?, tag: String)

}