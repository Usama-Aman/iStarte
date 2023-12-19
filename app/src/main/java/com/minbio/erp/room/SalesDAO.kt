package com.minbio.erp.room

import androidx.room.*

@Dao
interface SalesDAO {

    /*Inserting Sale*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSales(sales: SalesModel)

    /*Inserting Sale Orders*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSalesOrders(salesOrders: SalesOrders)

    /*Inserting Sale Orders*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSalesOrderItems(salesOrderItems: SalesOrderItems)

    /*Getting Sales of Logged In User*/
    @Query("SELECT * FROM SalesModel WHERE loggedInUserId == :loggedInUserId")
    fun getSalesOfLoggedInUser(loggedInUserId: String): List<SalesModel>

    /*Getting Sales Order of Logged In User*/
    @Query("SELECT * FROM SalesOrders WHERE loggedInUserId == :loggedInUserId ")
    fun getSalesOrders(loggedInUserId: String): List<SalesOrders>

    /*Update the pending overdraft of current customer*/
    @Query("UPDATE SalesOrders SET customerOverdraft = :pendingOverdraft WHERE loggedInUserId == :loggedInUserId AND customerId=:customerId")
    fun updateSalesCustomerPendingOverdraft(
        loggedInUserId: String,
        customerId: String,
        pendingOverdraft : String
    )

    /*Getting Sales Order of Logged In User*/
    @Query("SELECT * FROM SalesOrders WHERE loggedInUserId == :loggedInUserId AND customerId=:customerId")
    fun getSalesOrdersOfASpecificCustomer(
        loggedInUserId: String,
        customerId: String
    ): List<SalesOrders>

    /*Getting Sales Order Items of Logged In User*/
    @Query("SELECT * FROM SalesOrderItems WHERE loggedInUserId == :loggedInUserId AND customerId == :customerId")
    fun getSalesOrderItems(loggedInUserId: String, customerId: String): List<SalesOrderItems>

    @Query("SELECT * FROM SalesOrderItems WHERE lotId = :lotId AND customerId = :customerId AND loggedInUserId = :loggedInUserId")
    fun getSpecificLot(
        lotId: String,
        customerId: String,
        loggedInUserId: String
    ): List<SalesOrderItems>

    /*Updating the sale Order Items*/
    @Query("UPDATE SalesOrderItems SET quantity= :quantity , total=:total  WHERE lotId = :lotId AND customerId = :customerId AND loggedInUserId = :loggedInUserId")
    fun updateQuantityAndTotalOfSpecificLot(
        quantity: String,
        total: String,
        customerId: String,
        lotId: String,
//        vatPercentage: String,
        loggedInUserId: String
    )

    /*Delete ALl sales*/
    @Query("DELETE FROM SalesModel")
    fun deleteAllSales()

    /*Delete all SalesOrders*/
    @Query("DELETE FROM SalesOrders")
    fun deleteAllSaleOrders()

    /*Delete All Sale Order Items*/
    @Query("DELETE FROM SalesOrderItems")
    fun deleteAllSaleOrderItems()

    @Query("DELETE FROM SalesOrderItems WHERE lotId = :lotId AND customerId = :customerId AND loggedInUserId = :loggedInUserId")
    fun deleteLots(
        customerId: String,
        lotId: String,
        loggedInUserId: String
    )

    @Query("DELETE FROM SalesOrderItems WHERE customerId = :customerId AND loggedInUserId = :loggedInUserId")
    fun deleteLotsOfCustomer(
        customerId: String,
        loggedInUserId: String
    )

    @Query("DELETE FROM SalesOrders WHERE customerId = :customerId AND loggedInUserId = :loggedInUserId")
    fun deleteSalesOfCustomer(
        customerId: String,
        loggedInUserId: String
    )

}