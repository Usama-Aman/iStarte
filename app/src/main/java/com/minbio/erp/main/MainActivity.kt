package com.minbio.erp.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.accounting.AccountingFragment
import com.minbio.erp.auth.LoginActivity
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.cashier_desk.CashierDeskFragment
import com.minbio.erp.corporate_management.CorporateManagementFragment
import com.minbio.erp.customer_management.CustomerManagementFragment
import com.minbio.erp.dashboard.DashboardFragment
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.human_resources.HumanResourceFragment
import com.minbio.erp.main.models.SettingsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.order_for_preparation.OrderPreparationFragment
import com.minbio.erp.product_management.ProManagementFragment
import com.minbio.erp.quality_management.QualityManagementFragment
import com.minbio.erp.room.AppDatabase
import com.minbio.erp.sales_user_interface.SalesFragment
import com.minbio.erp.supplier_management.SupplierManagementFragment
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.getLoginModel
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.PermissionKeys
import com.minbio.erp.utils.SharedPreference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_layout.*
import org.json.JSONObject


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    ResponseCallBack {

    private var doubleBackToExitPressedOnce = false
    lateinit var countriesModel: CountriesModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var loginModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        getCountriesList()
        getSettings()
    }

    private fun initViews() {
        loginModel = getLoginModel(this)
        username.text = loginModel.data.first_name
        Glide
            .with(this)
            .load(loginModel.data.image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(ivProfile)


        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        menuPermissionsHide(navigationView.menu)
        val logout: LinearLayout = nav_view_container.findViewById(R.id.btnLogout)
        logout.setOnClickListener {
            SharedPreference.saveBoolean(this, Constants.isLoggedIn, false)
            startActivity(
                Intent(this@MainActivity, LoginActivity::class.java)
            )
            finish()
        }

//        val db: AppDatabase? = AppDatabase.getAppDataBase(context = applicationContext)
//        db!!.salesDAO().deleteAllSales()
//        db.salesDAO().deleteAllSaleOrders()
//        db.salesDAO().deleteAllSaleOrderItems()


        drawer_toggle.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START, true) }

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, DashboardFragment())
            .commit()
    }

    private fun menuPermissionsHide(menu: Menu) {
        val permissions = loginModel.data.permissions
        if (loginModel.data.designation_key != PermissionKeys.ADMIN) {
            menu.findItem(R.id.nav_corporate).isVisible = permissions.corporate_management != ""
            menu.findItem(R.id.nav_customer).isVisible = permissions.customer_management != ""
            menu.findItem(R.id.nav_supplier).isVisible = permissions.supplier_management != ""
            menu.findItem(R.id.nav_quality).isVisible = permissions.quality_management != ""
            menu.findItem(R.id.nav_sales).isVisible = permissions.sales_management != ""
            menu.findItem(R.id.nav_order_prep).isVisible = permissions.order_preparation != ""
            menu.findItem(R.id.nav_cashier).isVisible = permissions.cashier_desk != ""
            menu.findItem(R.id.nav_hr).isVisible = permissions.hr_management != ""
            menu.findItem(R.id.nav_product).isVisible = permissions.product_management != ""
            menu.findItem(R.id.nav_accounting).isVisible = permissions.accounting != ""
            menu.findItem(R.id.nav_financial).isVisible = permissions.accounting != ""
        } else {
            menu.findItem(R.id.nav_corporate).isVisible = true
            menu.findItem(R.id.nav_customer).isVisible = true
            menu.findItem(R.id.nav_supplier).isVisible = true
            menu.findItem(R.id.nav_quality).isVisible = true
            menu.findItem(R.id.nav_sales).isVisible = true
            menu.findItem(R.id.nav_order_prep).isVisible = true
            menu.findItem(R.id.nav_cashier).isVisible = true
            menu.findItem(R.id.nav_hr).isVisible = true
            menu.findItem(R.id.nav_product).isVisible = true
            menu.findItem(R.id.nav_accounting).isVisible = true
        }
    }

    fun checkModulePermission(fragment: Fragment) {
        Handler().postDelayed({
            if (loginModel.data.designation_key != PermissionKeys.ADMIN) {
                when (fragment) {
                    is DashboardFragment -> {
                        launchFragment(fragment)
                    }
                    is CorporateManagementFragment -> {
                        if (loginModel.data.permissions.corporate_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is CustomerManagementFragment -> {
                        if (loginModel.data.permissions.customer_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is SupplierManagementFragment -> {
                        if (loginModel.data.permissions.supplier_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is ProManagementFragment -> {
                        if (loginModel.data.permissions.product_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is OrderPreparationFragment -> {
                        if (loginModel.data.permissions.order_preparation != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is CashierDeskFragment -> {
                        if (loginModel.data.permissions.cashier_desk != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is SalesFragment -> {
                        if (loginModel.data.permissions.sales_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is QualityManagementFragment -> {
                        if (loginModel.data.permissions.quality_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is AccountingFragment -> {
                        if (loginModel.data.permissions.accounting != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is HumanResourceFragment -> {
                        if (loginModel.data.permissions.hr_management != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                    is FinancialManagementFragment -> {
                        if (loginModel.data.permissions.accounting != "")
                            launchFragment(fragment)
                        else
                            showPermissionToast()
                    }
                }
            } else
                launchFragment(fragment)
        }, 330)
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, fragment)
            .commit()
    }

    fun showPermissionToast() {
        AppUtils.showToast(this, resources.getString(R.string.dontHaveModulePermission), false)
    }

    fun setToolbarTitle(text: String) {
        page_title.text = text
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_corporate -> checkModulePermission(CorporateManagementFragment())
            R.id.nav_customer -> checkModulePermission(CustomerManagementFragment())
            R.id.nav_supplier -> checkModulePermission(SupplierManagementFragment())
            R.id.nav_dashboard -> checkModulePermission(DashboardFragment())
            R.id.nav_accounting -> checkModulePermission(AccountingFragment())
            R.id.nav_cashier -> checkModulePermission(CashierDeskFragment())
            R.id.nav_order_prep -> checkModulePermission(OrderPreparationFragment())
            R.id.nav_quality -> checkModulePermission(QualityManagementFragment())
            R.id.nav_sales -> checkModulePermission(SalesFragment())
            R.id.nav_product -> checkModulePermission(ProManagementFragment())
            R.id.nav_hr -> checkModulePermission(HumanResourceFragment())
            R.id.nav_financial -> checkModulePermission(FinancialManagementFragment())
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    fun countriesList(): CountriesModel {
        return countriesModel
    }

    private fun getCountriesList() {
        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.getCountiesList()
        RetrofitClient.apiCall(call, this, "CountriesList")
    }

    private fun getSettings() {
        val api = RetrofitClient.getClientNoToken(this).create(Api::class.java)
        val call = api.getSettings()
        RetrofitClient.apiCall(call, this, "Settings")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "CountriesList" -> {
                countriesModel = Gson().fromJson(jsonObject.toString(), CountriesModel::class.java)
            }
            "Settings" -> {
                val settingsModel =
                    Gson().fromJson(jsonObject.toString(), SettingsModel::class.java)
                val settingsStringData: String = Gson().toJson(settingsModel)
                SharedPreference.saveSimpleString(this, Constants.settingsData, settingsStringData)
                SharedPreference.saveSimpleString(
                    this,
                    Constants.defaultCurrency,
                    settingsModel.data.settings.default_currency
                )
            }

        }

    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.showToast(this, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.showToast(this, message!!, false)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        } else {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this@MainActivity,
                resources.getString(R.string.back_pressed_again),
                Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyDataBase()
    }


}