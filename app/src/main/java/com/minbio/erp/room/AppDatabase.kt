package com.minbio.erp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SalesModel::class, SalesOrders::class, SalesOrderItems::class],
    version = 1,
    exportSchema = false
)
//@TypeConverters(SalesOrderConverter::class, SalesOrderItemsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun salesDAO(): SalesDAO

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "MyDataBase"
                    ).fallbackToDestructiveMigration()
                        .allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            if (INSTANCE != null)
                if (INSTANCE?.isOpen == true)
                    INSTANCE?.close()
            INSTANCE = null
        }
    }
}