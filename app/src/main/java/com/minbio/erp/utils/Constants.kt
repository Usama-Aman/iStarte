package com.minbio.erp.utils

object Constants {

    /*--------------------------------------------------------------------------------------------*/

    /*Generic Keys*/
    const val isLoggedIn = "isLoggedIn"
    const val userData = "userData"
    const val permissionData = "permissionData"
    const val accessToken = "accessToken"
    const val isFirstLogin = "isFirstLogin"
    const val fingerPrintUserEmail = "fingerPrintUserEmail"
    const val fingerPrintUserSiren = "fingerPrintUserSiren"
    const val fingerPrintUserPassword = "fingerPrintUserPassword"
    const val settingsData = "settingsData"
    const val defaultCurrency = "defaultCurrency"
    var mLastClickTime: Long = 0


    /*--------------------------------------------------------------------------------------------*/

    /*Corporate Management Keys*/
    const val CORPORATE_PROFILE_REQUEST_CODE = 201
    const val CORPORATE_PROFILE_CROP_REQUEST_CODE = 202
    const val CORPORATE_BANK_FILE_REQUEST_CODE = 203
    const val CORPORATE_BANK_FILE_CROP_REQUEST_CODE = 204

    /*--------------------------------------------------------------------------------------------*/

    /*Customer Management Keys*/
    const val CUSTOMER_IMAGE_REQUEST_CODE = 301
    const val CUSTOMER_IMAGE_CROP_REQUEST_CODE = 302
    const val CUSTOMER_KBIS_REQUEST_CODE = 303
    const val CUSTOMER_KBIS_CROP_REQUEST_CODE = 304
    const val CUSTOMER_ID_REQUEST_CODE = 305
    const val CUSTOMER_ID_CROP_REQUEST_CODE = 306

    /*--------------------------------------------------------------------------------------------*/

    /*Supplier Management Keys*/
    const val SUPPLIER_IMAGE_REQUEST_CODE = 401
    const val SUPPLIER_IMAGE_CROP_REQUEST_CODE = 402
    const val SUPPLIER_KBIS_REQUEST_CODE = 403
    const val SUPPLIER_KBIS_CROP_REQUEST_CODE = 404
    const val SUPPLIER_ID_REQUEST_CODE = 405
    const val SUPPLIER_ID_CROP_REQUEST_CODE = 406

    /*--------------------------------------------------------------------------------------------*/

    /*Product Management Keys*/
    const val PRODUCT_PHOTO1_REQUEST_CODE = 501
    const val PRODUCT_PHOTO1_CROP_REQUEST_CODE = 502
    const val PRODUCT_PHOTO2_REQUEST_CODE = 503
    const val PRODUCT_PHOTO2_CROP_REQUEST_CODE = 504
    const val PRODUCT_PHOTO3_REQUEST_CODE = 505
    const val PRODUCT_PHOTO3_CROP_REQUEST_CODE = 506
    const val PRODUCT_DELIVERY_NOTE_REQUEST_CODE = 507
    const val PRODUCT_DELIVERY_NOTE_CROP_REQUEST_CODE = 508
    const val PRODUCT_LABEL_REQUEST_CODE = 509
    const val PRODUCT_LABEL_CROP_REQUEST_CODE = 510

    /*--------------------------------------------------------------------------------------------*/

    /*Sales Management Keys*/
    const val pickUpDelivery = "pickup_delivery"
    const val fastDelivery = "fast_delivery"
    const val DELIVERY_LATLNG_CODE = 601

    /*--------------------------------------------------------------------------------------------*/

    /*Finance Management Keys*/
    const val BANK_ADDRESS_CODE = 701
    const val ACCOUNT_OWNER_ADDRESS_CODE = 702

    /*--------------------------------------------------------------------------------------------*/

    /*Others*/
    const val   ADDRESS_RESULT_CODE_KEY = 1000
    const val LOCATION_PERMISSION_CODE = 1001
    const val STORAGE_PERMISSION_CODE = 1002

    /*--------------------------------------------------------------------------------------------*/

}