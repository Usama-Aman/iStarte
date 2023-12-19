package com.minbio.erp.room

import androidx.room.TypeConverter
import com.google.gson.Gson

class SalesOrderConverter {

    @TypeConverter
    fun listToJson(value: List<SalesOrders>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        Gson().fromJson(value, Array<SalesOrders>::class.java).toList()

}

class SalesOrderItemsConverter {

    @TypeConverter
    fun listToJson(value: List<SalesOrderItems>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        Gson().fromJson(value, Array<SalesOrderItems>::class.java).toList()

}