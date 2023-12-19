package com.minbio.erp.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.http.*

interface Api {


    /*Auth Module Apis*/

    @FormUrlEncoded
    @POST("erp/login")
    fun login(
        @Field("siret_no") siret_no: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/register")
    fun register(
        @Field("company_name") company_name: String,
        @Field("siret_no") siret_no: String,
        @Field("company_address") company_address: String,
        @Field("zip_code") zip_code: String,
        @Field("country_id") country_id: Int,
        @Field("email") email: String,
        @Field("contact_first_name") contact_first_name: String,
        @Field("contact_last_name") contact_last_name: String,
        @Field("contact_title") contact_title: String,
        @Field("phone") phone: String,
        @Field("country_code") country_code: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
        @Field("iso2") iso2: String,
        @Field("lat") lat: String,
        @Field("lng") lng: String
    ): Call<ResponseBody>


    @GET("countries")
    fun getCountiesList(): Call<ResponseBody>

    @GET("erp/settings")
    fun getSettings(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/confirm_otp")
    fun confirmOtp(
        @Field("phone") phone: String?,
        @Field("otp") otp: String?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/password_email")
    fun forgotPassword(
        @Field("email") email: String?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/reset_password")
    fun resetPassword(
        @Field("email") email: String?,
        @Field("otp") otp: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") password_confirmation: String?
    ): Call<ResponseBody>


    /*Corporate Profile Apis*/

    @GET("erp/users")
    fun getCorporateUsers(@Query("page") page: Int): Call<ResponseBody>


    @GET("erp/designations")
    fun getDesignations(): Call<ResponseBody>

    @Multipart
    @POST("erp/users")
    fun postCorporateUser(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") mobile: RequestBody,
        @Part("country_code") countryCode: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("designation_id") designationId: RequestBody,
        @Part("is_active") isActive: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @POST("erp/users/{id}")
    fun updateCorporateUser(
        @Path("id") id: Int,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") mobile: RequestBody,
        @Part("country_code") countryCode: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("designation_id") designationId: RequestBody,
        @Part("is_active") isActive: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>

    @GET("erp/profile")
    fun getCorporateCompanyDetails(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/profile")
    fun postCorporateCompanyDetails(
        @Field("company_name") company_name: String?,
        @Field("siret_no") siret_no: String?,
        @Field("company_address") company_address: String?,
        @Field("zip_code") zip_code: String?,
        @Field("country_id") country_id: Int,
        @Field("email") email: String?,
        @Field("contact_first_name") contact_first_name: String?,
        @Field("contact_last_name") contact_last_name: String?,
        @Field("contact_title") contact_title: String?,
        @Field("phone") phone: String?,
        @Field("country_code") country_code: String?,
        @Field("iso2") iso2: String?,
        @Field("lat") lat: String?,
        @Field("lng") lng: String?
    ): Call<ResponseBody>

    @GET("erp/bank/detail")
    fun getBankDetails(): Call<ResponseBody>

    @Multipart
    @POST("erp/bank/detail")
    fun postBankDetails(
        @Part("bic_code") bic_code: RequestBody,
        @Part("iban") iban: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>


    /*Customer Management Apis*/

    @GET("erp/customers")
    fun getCustomers(@Query("page") page: Int): Call<ResponseBody>

    @GET("erp/customers/list/all")
    fun getAllCustomersList(): Call<ResponseBody>


    @Multipart
    @POST("erp/customers")
    fun postCustomerDate(
        @Part("name") name: RequestBody,
        @Part("company_name") company_name: RequestBody,
        @Part("siret_no") siret_no: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("address") address: RequestBody,
        @Part("overdraft_amount") overdraft_amount: RequestBody,
        @Part customer_image: MultipartBody.Part?,
        @Part kbis_file: MultipartBody.Part?,
        @Part id_file: MultipartBody.Part?
    ): Call<ResponseBody>


    @Multipart
    @POST("erp/customers/{id}")
    fun updateCustomerDate(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("company_name") company_name: RequestBody,
        @Part("siret_no") siret_no: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("address") address: RequestBody,
        @Part("overdraft_amount") overdraft_amount: RequestBody,
        @Part customer_image: MultipartBody.Part?,
        @Part kbis_file: MultipartBody.Part?,
        @Part id_file: MultipartBody.Part?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/customers/overdraft/setting")
    fun postCustomerOverdraftSetting(
        @Field("allow_overdraft") allow_overdraft: Int,
        @Field("overdraft_amount") overdraft_amount: String,
        @Field("customer_id") customer_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/coveragePayment")
    fun postCustomerOverdraftPayment(
        @Field("stripe_token") stripe_token: String,
        @Field("customer_id") customer_id: Int
    ): Call<ResponseBody>


    @GET("erp/complaints/metadata/{id}")
    fun getCustomerComplaintData(@Path("id") id: Int): Call<ResponseBody>

    @Multipart
    @POST("erp/complaints")
    fun postComplaint(
        @Part("order_id") order_id: RequestBody,
        @Part("item") item: RequestBody,
        @Part("complaint_type_id") complaint_type_id: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part("customer_id") customer_id: RequestBody,
        @Part("complaint_no") complaint_no: RequestBody,
        @Part("status") status: RequestBody,
        @Part part: Array<MultipartBody.Part>?
    ): Call<ResponseBody>

    /*Supplier Management Apis*/

    @GET("erp/suppliers")
    fun getSuppliers(@Query("page") page: Int): Call<ResponseBody>

    @Multipart
    @POST("erp/suppliers")
    fun postSupplierData(
        @Part("contact_full_name") name: RequestBody,
        @Part("company_name") company_name: RequestBody,
        @Part("siret_no") siret_no: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("address") address: RequestBody,
        @Part("allowed_overdraft") overdraft_amount: RequestBody,
        @Part customer_image: MultipartBody.Part?,
        @Part kbis_file: MultipartBody.Part?,
        @Part id_file: MultipartBody.Part?
    ): Call<ResponseBody>

    @Multipart
    @POST("erp/suppliers/{id}")
    fun updateSupplierData(
        @Path("id") id: Int,
        @Part("contact_full_name") name: RequestBody,
        @Part("company_name") company_name: RequestBody,
        @Part("siret_no") siret_no: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("iso2") iso2: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("address") address: RequestBody,
        @Part("allowed_overdraft") overdraft_amount: RequestBody,
        @Part customer_image: MultipartBody.Part?,
        @Part kbis_file: MultipartBody.Part?,
        @Part id_file: MultipartBody.Part?
    ): Call<ResponseBody>


    @GET("erp/suppliers/balance/{id}")
    fun getSupplierBalance(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/complaints/suppliers/list/{id}")
    fun getSupplierComplaints(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/complaints/suppliers/creditnotes/{id}")
    fun getSupplierCreditNotes(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>


    /*Product Management Apis*/

    @GET("erp/categories")
    fun getProductCategories(): Call<ResponseBody>

    @GET("erp/gradients/{id}")
    fun getGradients(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/categories/{id}")
    fun getProductList(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/products/metadata/{id}")
    fun getProductListForForm(@Path("id") id: Int): Call<ResponseBody>

    @DELETE("erp/files/{id}")
    fun deleteFiles(@Path("id") id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/default_lot")
    fun setDefaultLot(@Field("lot_id") lot_id: Int): Call<ResponseBody>


    @GET("erp/products/{id}/edit")
    fun getProductData(@Path("id") id: Int): Call<ResponseBody>

    @Multipart
    @POST("erp/products")
    fun postProductData(
        @Part("id") selectedProductVariety: RequestBody,
        @Part("product_variety") product_variety: RequestBody,
        @Part("product_id") product_id: RequestBody,
        @Part("supplier_id") supplier_id: RequestBody,
        @Part("supplier_note") supplier_note: RequestBody,
        @Part("gross_weight") gross_weight: RequestBody,
        @Part("supplier_tare") supplier_tare: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("buying_price") buying_price: RequestBody,
        @Part("income_packing_quantity") income_packing_quantity: RequestBody,
        @Part("size_id") size_id: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("supplier_vat") supplier_vat: RequestBody,
        @Part("other_cost") other_cost: RequestBody,
        @Part("shipping_cost") shipping_cost: RequestBody,
        @Part("income_unit_id") income_unit_id: RequestBody,
        @Part("selling_unit_id") selling_unit_id: RequestBody,
        @Part("selling_packing_unit_id") selling_packing_unit_id: RequestBody,
        @Part("selling_price") selling_price: RequestBody,
        @Part("selling_tare") selling_tare: RequestBody,
        @Part("selling_vat_id") selling_vat_id: RequestBody,
        @Part("interfel_tax") interfel_tax: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part("is_bio") is_bio: RequestBody,
        @Part("is_nego") is_nego: RequestBody,
        @Part("class_id") class_id: RequestBody,
        @Part("incoming_date") incoming_date: RequestBody,
        @Part("sale_account_id") sale_account_id: RequestBody,
        @Part("purchase_account_id") purchase_account_id: RequestBody,
        @Part delivery_note_file: MultipartBody.Part?,
        @Part label_file: MultipartBody.Part?,
        @Part part: Array<MultipartBody.Part>?
    ): Call<ResponseBody>

    @Multipart
    @POST("erp/products/{id}")
    fun updateProductData(
        @Path("id") id: Int,
        @Part("product_variety") product_variety: RequestBody,
        @Part("product_id") product_id: RequestBody,
        @Part("supplier_id") supplier_id: RequestBody,
        @Part("supplier_note") supplier_note: RequestBody,
        @Part("gross_weight") gross_weight: RequestBody,
        @Part("supplier_tare") supplier_tare: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("buying_price") buying_price: RequestBody,
        @Part("income_packing_quantity") income_packing_quantity: RequestBody,
        @Part("size_id") size_id: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("supplier_vat") supplier_vat: RequestBody,
        @Part("other_cost") other_cost: RequestBody,
        @Part("shipping_cost") shipping_cost: RequestBody,
        @Part("income_unit_id") income_unit_id: RequestBody,
        @Part("selling_unit_id") selling_unit_id: RequestBody,
        @Part("selling_packing_unit_id") selling_packing_unit_id: RequestBody,
        @Part("selling_price") selling_price: RequestBody,
        @Part("selling_tare") selling_tare: RequestBody,
        @Part("selling_vat_id") selling_vat_id: RequestBody,
        @Part("interfel_tax") interfel_tax: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part("is_bio") is_bio: RequestBody,
        @Part("is_nego") is_nego: RequestBody,
        @Part("class_id") class_id: RequestBody,
        @Part("incoming_date") incoming_date: RequestBody,
        @Part("sale_account_id") sale_account_id: RequestBody,
        @Part("purchase_account_id") purchase_account_id: RequestBody,
        @Part delivery_note_file: MultipartBody.Part?,
        @Part label_file: MultipartBody.Part?,
        @Part part: Array<MultipartBody.Part>?
    ): Call<ResponseBody>

    @GET("erp/variety/lots/{id}")
    fun getInventoryList(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/complaints/suppliers/{id}")
    fun getProCustomerComplaint(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>


    @GET("erp/complaints")
    fun getProSupplierComplaint(@Query("page") page: Int): Call<ResponseBody>

    /*Sale Management Apis*/
    @FormUrlEncoded
    @POST("erp/deliverycharges")
    fun getDeliveryCharges(@Field("lat") lat: String, @Field("lng") lng: String): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/orders")
    fun postOrder(
        @Field("products") products: JSONArray,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("address") address: String,
        @Field("delivery_type") delivery_type: String,
        @Field("customer_id") customer_id: Int
    ): Call<ResponseBody>

    /*Order Preparation Apis*/

    @GET("erp/customers/orders/pending")
    fun getCustomerOrders(@Query("page") page: Int): Call<ResponseBody>

    @GET("erp/cashier/orders/pending")
    fun getPendingOrders(@Query("page") page: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/orders/changestatus")
    fun changeOrderStatus(@Field("id") id: Int, @Field("status") status: String): Call<ResponseBody>


    /*Cashier Desk Apis*/
    @GET("erp/cashier/orders/ready")
    fun getCashierOrders(@Query("page") page: Int): Call<ResponseBody>

    @GET("erp/customers/orders/ready")
    fun getCashierCustomerOrders(@Query("page") page: Int): Call<ResponseBody>


    @GET("erp/customers/orders/{id}")
    fun getCustomerBalance(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/complaints/orders/{id}")
    fun getCreditNoteOrders(@Path("id") id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/complaints/creditnote")
    fun postCreditNoteData(
        @Field("comment") comment: String,
        @Field("complaint_status") complaint_status: String,
        @Field("id") id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/orders/payment")
    fun postOrderPayment(
        @Field("order_id") order_id: Int,
        @Field("stripe_token") stripe_token: String,
        @Field("overdraft_logs") overdraft_logs: String,
        @Field("is_coverage") is_coverage: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/getcoverage")
    fun checkForCoverage(
        @Field("order_id") order_id: Int
    ): Call<ResponseBody>


    /*Quality Management Apis*/
    @GET("erp/complaints/suppliers/metadata/{id}")
    fun getQualityComplaintData(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/complaints/trashed/metadata/{id}")
    fun getQualityTrashedData(@Path("id") id: Int): Call<ResponseBody>


    @Multipart
    @POST("erp/complaints/suppliers/post")
    fun postQualityComplaint(
        @Part("product_user_id") product_user_id: RequestBody,
        @Part("product_lot_id") product_lot_id: RequestBody,
        @Part("complaint_type_id") complaint_type_id: RequestBody,
        @Part("supplier_id") supplier_id: RequestBody,
        @Part("status") status: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part part: Array<MultipartBody.Part>?
    ): Call<ResponseBody>

    /*Accounting Management Apis*/

    @GET("erp/complaints/suppliers/orders/{id}")
    fun getAccountingCreditNote(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/suppliers/setting/{id}")
    fun getFileTransferSettings(@Path("id") id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/suppliers/setting/{id}")
    fun postFileTransferSettings(
        @Path("id") id: Int,
        @Field("third_party_download_upload") third_party_download_upload: String,
        @Field("order_transfer") order_transfer: String,
        @Field("customer_list_transfer") customer_list_transfer: String,
        @Field("inventory_transfer") inventory_transfer: String,
        @Field("product_list_transfer") product_list_transfer: String,
        @Field("supplier_list_transfer") supplier_list_transfer: String,
        @Field("invoice_list_transfer") invoice_list_transfer: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/complaints/suppliers/creditnotes")
    fun postAccountingCreditNote(
        @Field("comment") comment: String,
        @Field("complaint_status") complaint_status: String,
        @Field("id") id: Int
    ): Call<ResponseBody>


    @Multipart
    @POST("erp/complaints/trashed/post")
    fun postQualityTrashedData(
        @Part("product_lot_id") product_lot_id: RequestBody,
        @Part("trashed_date") trashed_date: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("handler_id") handler_id: RequestBody,
        @Part("email") email: RequestBody,
        @Part("topic_id") topic_id: RequestBody,
        @Part("trashed_status") trashed_status: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part part: Array<MultipartBody.Part>?
    ): Call<ResponseBody>


    /*------------------------------------Human Resource Module _--------------------------------------*/

    @FormUrlEncoded
    @POST("erp/leaves")
    fun postLeaveData(
        @Field("leave_type") leave_type: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("start_half") start_half: String,
        @Field("end_half") end_half: String,
        @Field("note") note: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/expenses")
    fun postExpenseData(
        @Field("date") date: String,
        @Field("amount") amount: String,
        @Field("note") note: String,
        @Field("vat_account_id") vat_account_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/leaves/assign")
    fun assignLeave(
        @Field("leave_id") leaveId: Int,
        @Field("company_user_id") company_user_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/expenses/assign")
    fun assignExpense(
        @Field("expense_id") leaveId: Int,
        @Field("company_user_id") company_user_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/expenses/status")
    fun acceptRejectExpense(
        @Field("expense_id") leaveId: Int,
        @Field("status") status: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/leaves/status")
    fun acceptRejectLeave(
        @Field("leave_id") leaveId: Int,
        @Field("status") status: String
    ): Call<ResponseBody>

    @DELETE("erp/leaves/{id}")
    fun deleteLeave(@Path("id") id: Int): Call<ResponseBody>

    @DELETE("erp/expenses/{id}")
    fun deleteExpense(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/leaves/{id}")
    fun getLeaves(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/expenses/{id}")
    fun getExpenses(@Path("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("erp/user/sendotp/{id}")
    fun sendOTP(@Path("id") id: Int): Call<ResponseBody>


    @Multipart
    @POST("erp/user/verify")
    fun verifyUser(
        @Part("company_user_id") company_user_id: RequestBody,
        @Part("otp") otp: RequestBody,
        @Part("date") date: RequestBody,
        @Part part: MultipartBody.Part?
    ): Call<ResponseBody>


    /*Financial Management*/

    @GET("erp/financial/accounts/metadata")
    fun getBankCashMetaData(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/financial/accounts")
    fun postFinancialAccount(
        @Field("reference") reference: String,
        @Field("label") label: String,
        @Field("account_type_id") account_type: Int,
        @Field("currency_id") currency_id: Int,
        @Field("status") status: String,
        @Field("country_id") country_id: Int,
        @Field("state") state: String,
        @Field("web") web: String,
        @Field("comment") comment: String,
        @Field("initial_balance") initial_balance: String,
        @Field("date") date: String,
        @Field("minimum_allowed_balance") minimum_allowed_balance: String,
        @Field("minimum_desired_balance") minimum_desired_balance: String,
        @Field("bank_name") bank_name: String,
        @Field("bank_code") bank_code: String,
        @Field("account_no") account_no: String,
        @Field("iban") iban: String,
        @Field("bic_swift_code") bic_swift_code: String,
        @Field("bank_address") bank_address: String,
        @Field("account_holder") account_holder: String,
        @Field("account_holder_address") account_holder_address: String,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("accounting_journal_id") accounting_journal_id: Int,
        @Field("bank_lat") bank_lat: Double,
        @Field("bank_lng") bank_lng: Double,
        @Field("owner_lat") owner_lat: Double,
        @Field("owner_lng") owner_lng: Double
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/financial/accounts")
    fun updateFinancialAccount(
        @Field("id") id: Int,
        @Field("reference") reference: String,
        @Field("label") label: String,
        @Field("account_type_id") account_type: Int,
        @Field("currency_id") currency_id: Int,
        @Field("status") status: String,
        @Field("country_id") country_id: Int,
        @Field("state") state: String,
        @Field("web") web: String,
        @Field("comment") comment: String,
        @Field("initial_balance") initial_balance: String,
        @Field("date") date: String,
        @Field("minimum_allowed_balance") minimum_allowed_balance: String,
        @Field("minimum_desired_balance") minimum_desired_balance: String,
        @Field("bank_name") bank_name: String,
        @Field("bank_code") bank_code: String,
        @Field("account_no") account_no: String,
        @Field("iban") iban: String,
        @Field("bic_swift_code") bic_swift_code: String,
        @Field("bank_address") bank_address: String,
        @Field("account_holder") account_holder: String,
        @Field("account_holder_address") account_holder_address: String,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("accounting_journal_id") accounting_journal_id: Int,
        @Field("bank_lat") bank_lat: Double,
        @Field("bank_lng") bank_lng: Double,
        @Field("owner_lat") owner_lat: Double,
        @Field("owner_lng") owner_lng: Double
    ): Call<ResponseBody>


    @GET("erp/financial/accounts")
    fun getFinancialAccountList(
        @Query("page") page: Int,
        @Query("bank_name") bank_account: String,
        @Query("label") label: String,
        @Query("account_no") number: String,
        @Query("status") status: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/financial/accounts/transfer")
    fun postFinancialInternalTransfer(
        @Field("from_id") from_id: Int,
        @Field("to_id") to_id: Int,
        @Field("description") description: String,
        @Field("amount") amount: String,
        @Field("date") date: String,
        @Field("payment_type_id") payment_type: Int
    ): Call<ResponseBody>

    @GET("erp/financial/accounts/entries/0")
    fun getFinancialEntries(@Query("page") page: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/financial/accounts/payment")
    fun postFinancialEntry(
        @Field("date") date: String,
        @Field("value_date") value_date: String,
        @Field("label") label: String,
        @Field("type") type: String,
        @Field("amount") amount: String,
        @Field("financial_account_id") financial_account_id: Int,
        @Field("payment_type_id") payment_type_id: Int,
        @Field("number") number: String,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("subledger_account") subledger_account: String

    ): Call<ResponseBody>

    @GET("erp/billing/customers/orders/{status}")
    fun getCustomerInvoiceList(
        @Path("status") status: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/billing/vendors/orders/{status}")
    fun getVendorInvoiceList(
        @Path("status") status: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/billing/customers/payments")
    fun getCustomerPayments(
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/billing/vendors/payments")
    fun getVendorPayments(
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/billing/customers/stats")
    fun getBillingCustomerStatisticsData(): Call<ResponseBody>


    @GET("erp/billing/vendors/stats")
    fun getBillingVendorStatisticsData(): Call<ResponseBody>

    @GET("erp/billing/customers/report/{month}/{year}")
    fun getBillingCustomerReport(
        @Path("month") month: Int,
        @Path("year") year: String
    ): Call<ResponseBody>

    @GET("erp/billing/vendors/report/{month}/{year}")
    fun getBillingVendorReport(
        @Path("month") month: Int,
        @Path("year") year: String
    ): Call<ResponseBody>

    /*Financial Accounting Apis */

    @GET("erp/accounting/journals/natures")
    fun getJournalNatures(): Call<ResponseBody>

    @GET("erp/accounting/journals")
    fun getAccountingJournals(@Query("page") page: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/journals")
    fun postAccountingJournal(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("journal_nature_id") journal_nature_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/journals")
    fun updateAccountingJournal(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("journal_nature_id") journal_nature_id: Int,
        @Field("id") id: Int
    ): Call<ResponseBody>

    @DELETE("erp/accounting/journals/{id}")
    fun deleteAccountingJournals(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/accounting/journals/status/{id}/{status}")
    fun updateJournalStatus(@Path("id") id: Int, @Path("status") status: Int): Call<ResponseBody>


    @GET("erp/accounting/chart/accounts/models/{country_id}")
    fun getAccountingChartModels(
        @Path("country_id") country_id: Int,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/chart/account/all/models")
    fun getAllAccountingChartModels(): Call<ResponseBody>


    @DELETE("erp/accounting/chart/accounts/models/{id}")
    fun deleteAccountChartModel(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/accounting/chart/accounts/models/status/{id}/{status}")
    fun updateChartModelStatus(
        @Path("id") id: Int,
        @Path("status") status: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/chart/accounts/models")
    fun postChartModel(
        @Field("chart_accounts_model") chart_accounts_model: String,
        @Field("label") label: String,
        @Field("country_id") country_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/chart/accounts/models")
    fun updateChartModel(
        @Field("chart_accounts_model") chart_accounts_model: String,
        @Field("label") label: String,
        @Field("country_id") country_id: Int,
        @Field("id") id: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/chart/accounts")
    fun getChartAccounts(
        @Query("page") page: Int,
        @Query("model_id") model_id: Int,
        @Query("label") label: String,
        @Query("short_label") short_label: String,
        @Query("account_number") account_number: String,
        @Query("parent_id") parent_id: Int,
        @Query("account_group") account_group: String
    ): Call<ResponseBody>


    @DELETE("erp/accounting/chart/accounts/{id}")
    fun deleteAccountChart(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/accounting/chart/accounts/status/{id}/{status}")
    fun updateChartAccountStatus(
        @Path("id") id: Int,
        @Path("status") status: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/chart/all/accounts")
    fun getParentAccountList(): Call<ResponseBody>

    @GET("erp/accounting/vat/account/country/list")
    fun getCountryBaseVatList(): Call<ResponseBody>

    @GET("erp/accounting/journalslist")
    fun getJournalCodeAccounts(): Call<ResponseBody>

    @GET("erp/accounting/personalized/user/groups")
    fun getPersonalizedGroupList(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/chart/accounts")
    fun postChartAccount(
        @Field("chart_accounts_model_id") chart_accounts_model_id: Int,
        @Field("account_number") account_number: String,
        @Field("label") label: String,
        @Field("short_label") short_label: String,
        @Field("parent_id") parent_id: Int,
        @Field("personalized_group_id") personalized_group_id: Int,
        @Field("account_group") account_group: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/chart/accounts")
    fun updateChartAccount(
        @Field("chart_accounts_model_id") chart_accounts_model_id: Int,
        @Field("account_number") account_number: String,
        @Field("label") label: String,
        @Field("short_label") short_label: String,
        @Field("parent_id") parent_id: Int,
        @Field("personalized_group_id") personalized_group_id: Int,
        @Field("account_group") account_group: String,
        @Field("id") id: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/default/accounts")
    fun updateDefaultAccounts(
        @Field("account_for_customer_third_parties") account_for_customer_third_parties: Int,
        @Field("account_for_vendor_third_parties") account_for_vendor_third_parties: Int,
        @Field("account_for_user_third_parties") account_for_user_third_parties: Int,
        @Field("account_for_sold_products") account_for_sold_products: Int,
        @Field("account_for_products_sold_and_exported_out_of_eec") account_for_products_sold_and_exported_out_of_eec: Int,
        @Field("account_for_bought_products") account_for_bought_products: Int,
        @Field("account_for_bought_products_and_imported_out_of_ecc") account_for_bought_products_and_imported_out_of_ecc: Int,
        @Field("account_for_sold_services") account_for_sold_services: Int,
        @Field("account_for_services_sold_and_exported_out_of_ecc") account_for_services_sold_and_exported_out_of_ecc: Int,
        @Field("account_for_bought_services") account_for_bought_services: Int,
        @Field("account_for_bought_services_and_imported_out_of_ecc") account_for_bought_services_and_imported_out_of_ecc: Int,
        @Field("account_for_vat_on_purchases") account_for_vat_on_purchases: Int,
        @Field("account_for_vat_on_sales") account_for_vat_on_sales: Int,
        @Field("account_for_paying_vat") account_for_paying_vat: Int,
        @Field("account_of_wait") account_of_wait: Int,
        @Field("account_of_transitional_bank_transfer") account_of_transitional_bank_transfer: Int,
        @Field("account_to_register_donations") account_to_register_donations: Int,
        @Field("account_capital") account_capital: Int,
        @Field("account_interest") account_interest: Int,
        @Field("account_insurance") account_insurance: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/default/accounts")
    fun getDefaultAccounts(): Call<ResponseBody>

    @GET("erp/accounting/personalized/groups/{country_id}")
    fun getPersonalizedGroupData(
        @Path("country_id") country_id: Int,
        @Query("page") page: Int
    ): Call<ResponseBody>


    @DELETE("erp/accounting/personalized/groups/{id}")
    fun deletePersonalizedGroups(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/accounting/personalized/groups/status/{id}/{status}")
    fun updatePersonalizedGroupStatus(
        @Path("id") id: Int,
        @Path("status") status: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/personalized/groups")
    fun postPersonalizedGroup(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("calculated") calculated: Int,
        @Field("position") position: String,
        @Field("formula") formula: String,
        @Field("comment") comment: String,
        @Field("country_id") country_id: Int,
        @Field("chart_accounts") chart_accounts: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/personalized/groups")
    fun updatePersonalizedGroup(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("calculated") calculated: Int,
        @Field("position") position: String,
        @Field("formula") formula: String,
        @Field("comment") comment: String,
        @Field("country_id") country_id: Int,
        @Field("chart_accounts") chart_accounts: String,
        @Field("id") id: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/export/options")
    fun getFileExportOptions(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/export/options")
    fun postFileExportOptions(
        @Field("date_format") date_format: String,
        @Field("file_name_prefix") file_name_prefix: String,
        @Field("file_format") file_format: String
    ): Call<ResponseBody>


    @GET("erp/accounting/vat/accounts/{country_id}/{code}")
    fun getVatAccountsList(
        @Path("country_id") country_id: Int,
        @Path("code") code: String,
        @Query("page") page: Int
    ): Call<ResponseBody>


    @DELETE("erp/accounting/vat/accounts/{id}")
    fun deleteVatAccount(@Path("id") id: Int): Call<ResponseBody>


    @GET("erp/accounting/vat/account/status/{id}/{status}")
    fun updateVatAccountStatus(@Path("id") id: Int, @Path("status") status: Int): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/vat/accounts")
    fun postVatAccount(
        @Field("country_id") country_id: Int,
        @Field("code") code: String,
        @Field("rate") rate: String,
        @Field("include_tax_2") include_tax_2: Int,
        @Field("rate_2") rate_2: String,
        @Field("include_tax_3") include_tax_3: Int,
        @Field("rate_3") rate_3: String,
        @Field("npr") npr: Int,
        @Field("sale_chart_account_id") sale_chart_account_id: Int,
        @Field("purchase_chart_account_id") purchase_chart_account_id: Int,
        @Field("note") note: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/vat/accounts")
    fun updateVatAccount(
        @Field("country_id") country_id: Int,
        @Field("code") code: String,
        @Field("rate") rate: String,
        @Field("include_tax_2") include_tax_2: Int,
        @Field("rate_2") rate_2: String,
        @Field("include_tax_3") include_tax_3: Int,
        @Field("rate_3") rate_3: String,
        @Field("npr") npr: Int,
        @Field("sale_chart_account_id") sale_chart_account_id: Int,
        @Field("purchase_chart_account_id") purchase_chart_account_id: Int,
        @Field("note") note: String,
        @Field("id") id: Int
    ): Call<ResponseBody>

    /*Tax Accounts*/

    @GET("erp/accounting/tax/accounts/{country_id}/{code}")
    fun getTaxAccountsList(
        @Path("country_id") country_id: Int,
        @Path("code") code: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/tax/account/status/{id}/{status}")
    fun updateTaxAccountStatus(@Path("id") id: Int, @Path("status") status: Int): Call<ResponseBody>


    @DELETE("erp/accounting/tax/accounts/{id}")
    fun deleteTaxAccount(@Path("id") id: Int): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/tax/accounts")
    fun postTaxAccount(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("country_id") country_id: Int,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("deductible") deductible: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/tax/accounts")
    fun updateTaxAccount(
        @Field("id") id: Int,
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("country_id") country_id: Int,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("deductible") deductible: Int
    ): Call<ResponseBody>


    /*Expense report accounts*/
    @GET("erp/accounting/expense/report/accounts/{code}")
    fun getExpenseReportData(
        @Path("code") code: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @DELETE("erp/accounting/expense/report/accounts/{id}")
    fun deleteExpenseReportAccount(@Path("id") id: Int): Call<ResponseBody>

    @GET("erp/accounting/expense/report/accounts/status/{id}/{status}")
    fun updateExpenseReportStatus(
        @Path("id") id: Int,
        @Path("status") status: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/expense/report/accounts")
    fun postExpenseReportAccount(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("chart_account_id") chart_account_id: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("erp/accounting/expense/report/accounts")
    fun updateExpenseReportAccount(
        @Field("code") code: String,
        @Field("label") label: String,
        @Field("chart_account_id") chart_account_id: Int,
        @Field("id") id: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/products/account")
    fun getProductAccounts(
        @Query("product_id") product_id: String,
        @Query("product_variety") product_variety: String,
        @Query("account_number") account_number: String,
        @Query("search_type") search_type: String,
        @Query("mode") mode: String,
        @Query("page") page: Int
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/products/account")
    fun postProductAccountData(
        @Field("chart_account_id") chart_account_id: Int,
        @Field("mode") mode: String,
        @Field("product") product: Int
    ): Call<ResponseBody>


    /*Customer Invoice Binding Apis*/

    @GET("erp/accounting/customers/invoices/binding/{year}")
    fun getCustomerInvoiceBinding(
        @Path("year") year: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/customers/invoices/auto/binding")
    fun customerInvoiceBindAutomatically(): Call<ResponseBody>


    @GET("erp/accounting/customers/invoices/tobind")
    fun getCustomerInvoiceToBind(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("invoice") invoice: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("product_variety") productRef: String,
        @Query("desc") desc: String,
        @Query("amount") amount: String,
        @Query("vat") vat: String,
        @Query("customer_name") thirdParty: String,
        @Query("country_id") country_id: Int,
        @Query("vat_id") vatId: String
    ): Call<ResponseBody>

    @GET("erp/accounting/customers/invoices/bound")
    fun getCustomerInvoiceBound(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("invoice") invoice: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("product_variety") productRef: String,
        @Query("desc") desc: String,
        @Query("amount") amount: String,
        @Query("vat") vat: String,
        @Query("customer_name") thirdParty: String,
        @Query("country_id") country_id: Int,
        @Query("vat_id") vatId: String,
        @Query("chart_account_number") chart_account_number: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/customers/invoices/binding")
    fun postCustomerInvoiceBinding(
        @Field("account") account: Int,
        @Field("invoices_to_bind") invoices_to_bind: String
    ): Call<ResponseBody>

    /*Expense Report Binding Apis*/

    @GET("erp/accounting/expenses/reports/binding/{year}")
    fun getExpenseReportBinding(
        @Path("year") year: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/expenses/reports/auto/binding")
    fun expenseReportBindAutomatically(): Call<ResponseBody>


    @GET("erp/accounting/expenses/reports/tobind")
    fun getExpenseReportToBind(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("expense_report_account_label") expenseReport: String,
        @Query("note") description: String,
        @Query("amount") amount: String,
        @Query("vat_percentage") vat_percentage: String
    ): Call<ResponseBody>


    @GET("erp/accounting/expenses/reports/bound")
    fun getExpenseReportBound(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("expense_account_label") expenseReport: String,
        @Query("note") description: String,
        @Query("amount") amount: String,
        @Query("vat_percentage") vat_percentage: String,
        @Query("chart_account_number") chart_account_number: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/expenses/reports/binding")
    fun postExpenseReportBinding(
        @Field("account") account: Int,
        @Field("expenses_report") expenses_report: String
    ): Call<ResponseBody>

    /*Vendor Invoice Binding apis*/


    @GET("erp/accounting/vendors/invoices/binding/{year}")
    fun getVendorInvoiceBinding(
        @Path("year") year: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/vendors/invoices/auto/binding")
    fun vendorInvoiceBindAutomatically(): Call<ResponseBody>


    @GET("erp/accounting/vendors/invoices/tobind")
    fun getVendorInvoiceToBind(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("invoice_no") invoice_no: String,
        @Query("supplier_note") invoiceLabel: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("product_variety") productRef: String,
        @Query("comment") desc: String,
        @Query("amount") amount: String,
        @Query("tax") tax: String,
        @Query("supplier_name") thirdParty: String,
        @Query("country_id") country_id: Int,
        @Query("vat_id") vatId: String
    ): Call<ResponseBody>


    @GET("erp/accounting/vendors/invoices/bound")
    fun getVendorInvoiceBound(
        @Query("page") page: Int,
        @Query("id") idLine: String,
        @Query("invoice_no") invoice: String,
        @Query("supplier_note") invoiceLabel: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("product_variety") productRef: String,
        @Query("comment") desc: String,
        @Query("amount") amount: String,
        @Query("tax") vat: String,
        @Query("supplier_name") thirdParty: String,
        @Query("country_id") country_id: Int,
        @Query("vat_id") vatId: String,
        @Query("chart_account_number") chart_account_number: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("erp/accounting/vendors/invoices/binding")
    fun postVendorInvoiceBinding(
        @Field("account") account: Int,
        @Field("invoice") invoices_to_bind: String
    ): Call<ResponseBody>


    /*Registration in Accounting (Journals) Apis*/

    @GET("erp/accounting/journalslist/menu")
    fun getJournalAccountForMenu(): Call<ResponseBody>


    @GET("erp/accounting/journal/data")
    fun getJournalsData(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("status") status: String,
        @Query("nature_id") journalNatureId: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/ledgers/export/csv")
    fun getLedgerExportCsv(): Call<ResponseBody>


    @POST("erp/accounting/ledgers/create")
    @FormUrlEncoded
    fun registerInLedger(
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("nature_id") journalNatureId: Int,
        @Field("journal_id") journalId: Int
    ): Call<ResponseBody>

    /*Ledger Apis*/

    @GET("erp/accounting/ledgers/list")
    fun getLedgerData(
        @Query("page") page: Int,
        @Query("id") id: String,
        @Query("description") description: String,
        @Query("transactions_start_date") transactions_start_date: String,
        @Query("transactions_end_date") transactions_end_date: String,
        @Query("account_from") account_from: Int,
        @Query("account_to") account_to: Int,
        @Query("sub_ledger_account_from") sub_ledger_account_from: String,
        @Query("sub_ledger_account_to") sub_ledger_account_to: String,
        @Query("label") label: String,
        @Query("debit") debit: String,
        @Query("credit") credit: String,
        @Query("journal") journal: String,
        @Query("exported_start_date") exported_start_date: String,
        @Query("exported_end_date") exported_end_date: String,
        @Query("with_exported") with_exported: Int
    ): Call<ResponseBody>


    @DELETE("erp/accounting/ledgers/delete")
    fun deleteLedgerLines(
        @Query("type") type: String,
        @Query("id") id: Int,
        @Query("year") year: String,
        @Query("month") month: String,
        @Query("journal") journal: Int
    ): Call<ResponseBody>

    @POST("erp/accounting/ledgers/create/transaction")
    @FormUrlEncoded
    fun createLedgerTransaction(
        @Field("date") date: String,
        @Field("accounting_doc") accounting_doc: String,
        @Field("journal_id") journalId: Int,
        @Field("movements") movements: JSONArray
    ): Call<ResponseBody>

    @POST("erp/accounting/ledgers/update/transaction")
    @FormUrlEncoded
    fun updateLedgerTransaction(
        @Field("id") ledger_id: Int,
        @Field("date") date: String,
        @Field("accounting_doc") accounting_doc: String,
        @Field("journal_id") journalId: Int,
        @Field("movements") movements: JSONArray
    ): Call<ResponseBody>

    @GET("erp/accounting/ledgers/edit/{id}")
    fun getTransactionData(
        @Path("id") id: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/ledgers/groupby/accounts")
    fun getLedgerDataGroupByAccounts(
        @Query("page") page: Int,
        @Query("transaction_id") numTransactionToSearch: String,
        @Query("description") accountingDocToSearch: String,
        @Query("label") labelToSearch: String,
        @Query("debit") debitToSearch: String,
        @Query("credit") creditToSearch: String,
        @Query("journal") journalToSearch: String,
        @Query("transactions_start_date") fromDateToSearch: String,
        @Query("transactions_end_date") toDateToSearch: String,
        @Query("account_from") fromAccountToSearch: Int,
        @Query("account_to") toAccountToSearch: Int
    ): Call<ResponseBody>

    /*Account Balance Apis*/
    @GET("erp/accounting/accounts/balance")
    fun getAccountBalance(
        @Query("page") page: Int,
        @Query("transactions_start_date") startDate: String,
        @Query("transactions_end_date") endDate: String,
        @Query("account_from") fromAccountId: Int,
        @Query("account_to") toAccountId: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/accounts/balance/export/csv")
    fun getAccountBalanceExportCSV(
        @Query("transactions_start_date") startDate: String,
        @Query("transactions_end_date") endDate: String,
        @Query("account_from") fromAccountId: Int,
        @Query("account_to") toAccountId: Int
    ): Call<ResponseBody>

    /*Financial Accounting Reporting apis */

    @GET("erp/accounting/reports/income/expenses")
    fun getExpenseIncomeReportingData(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ResponseBody>


    @GET("erp/accounting/reports/predefined/groups")
    fun getPredefinedGroupReporting(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("show_detail_by_account") show_detail_by_account: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/personalized/groups")
    fun getPersonalizedGroupReporting(
        @Query("year") year: Int,
        @Query("show_detail_by_account") show_detail_by_account: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/invoiced/thirdparties")
    fun getTurnOverInvoicedByThirdParties(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("customer_name") customer_name: String,
        @Query("country_id") country_id: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/invoiced/user")
    fun getTurnOverInvoicedByUser(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/invoiced/product")
    fun getTurnOverInvoicedByProductService(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("customer_id") customer_id: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/invoiced/taxrate")
    fun getTurnOverInvoicedBySaleTaxRate(
        @Query("year") year: Int
    ): Call<ResponseBody>

    @GET("erp/suppliers/list/all")
    fun getAllSuppliersList(): Call<ResponseBody>

    @GET("erp/accounting/reports/purchase/turnover/invoiced/product")
    fun getPurchaseTurnOverInvoicedByProductService(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("supplier_id") supplier_id: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/reports/purchase/turnover/invoiced/thirdparties")
    fun getPurchaseTurnOverInvoicedByThirdParties(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("customer_name") customer_name: String,
        @Query("country_id") country_id: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/reports/purchase/turnover/collected/thirdparties")
    fun getPurchaseTurnOverCollectedByThirdParties(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("supplier_company_name") supplier_company_name: String,
        @Query("country_id") country_id: Int
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/collected")
    fun getTurnOverCollected(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/purchase/turnover/collected")
    fun getPurchaseTurnOverCollected(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/turnover/invoiced")
    fun getTurnOverInvoiced(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ResponseBody>

    @GET("erp/accounting/reports/purchase/turnover/invoiced")
    fun getPurchaseTurnOverInvoiced(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<ResponseBody>


    @GET("erp/accounting/export/documents")
    fun getExportAccoutningDocument(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("invoices") invoices: Int,
        @Query("vender_invoices") vender_invoices: Int,
        @Query("miscellaneous_payments") miscellaneous_payments: Int,
        @Query("expense_reports") expense_reports: Int
    ): Call<ResponseBody>


    @GET("erp/accounting/export/documents/pdf/{id}")
    fun downloadExportAccountingDocument(
        @Path("id") id: Int,
        @Query("type") type: String
    ): Call<ResponseBody>

}





















