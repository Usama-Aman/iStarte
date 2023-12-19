package com.minbio.erp.sales_user_interface

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.DragShadowBuilder
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.minbio.erp.maps.AddressMapsActivity
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.room.AppDatabase
import com.minbio.erp.room.SalesModel
import com.minbio.erp.room.SalesOrderItems
import com.minbio.erp.room.SalesOrders
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.product_management.model.*
import com.minbio.erp.sales_user_interface.adapter.SalesLeftTabsAdapter
import com.minbio.erp.sales_user_interface.adapter.SalesOrderAdapter
import com.minbio.erp.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SalesFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    val quantityStrings: MutableList<String> = ArrayList()
    var saleItemsToDeleteIDs: MutableList<Int> = ArrayList()

    private var deliveryType: String = ""
    private var deliveryAddress: String = ""
    private var deliveryCharges: String = "0.0"
    private var deliveryLatLng: LatLng = LatLng(0.0, 0.0)

    private lateinit var v: View

    private var categoryId: Int = 0
    private var salesCustomerId = 0
    private val selectedOrderItems: MutableList<SalesOrderItems> = ArrayList()

    private var draggedLotData: Lot? = null
    private var expandedVarieties: MutableList<Variety> = ArrayList()

    private lateinit var salesProductListAdapter: SalesLeftTabsAdapter
    private lateinit var salesLeftRecyclerView: RecyclerView

    private lateinit var salesOrderAdapter: SalesOrderAdapter
    private lateinit var salesOrderRecyclerView: RecyclerView

    private lateinit var bottomLayout: ConstraintLayout
    private lateinit var saleProfileLayout: LinearLayout

    private lateinit var sales_pick_check_box: RadioButton
    private lateinit var sales_fast_check_box: RadioButton
    private lateinit var btnDelete: LinearLayout
    private lateinit var btnAdd: LinearLayout
    private lateinit var btnSend: LinearLayout
    private lateinit var btnPreparation: LinearLayout
    private lateinit var btnInvoice: LinearLayout
    private lateinit var btnPrint: LinearLayout

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    lateinit var pullToRefresh: SwipeRefreshLayout
    lateinit var btn_add: TextView

    var productList: MutableList<ProductListData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    private lateinit var tvTotalHTAnswer: TextView
    private lateinit var tvTotalTaxAnswer: TextView
    private lateinit var tvDeliverChargesAnswer: TextView
    private lateinit var tvTotalTAVAnswer: TextView
    private lateinit var tvTotalTTCAnswer: TextView
    private lateinit var tvDeliverCharges: TextView

    private lateinit var salePaymentLayout: ConstraintLayout
    private lateinit var salesCompanyName: TextView
    private lateinit var salesPaymentStatus: TextView
    private lateinit var salesBalanceAmount: TextView
    private lateinit var salesOverdraftAmount: TextView
    private lateinit var salesProfileName: TextView
    private lateinit var salesProfileSiren: TextView
    private lateinit var salesProfileTAV: TextView
    private lateinit var companyImage: CircleImageView
    private lateinit var customerProileImage: CircleImageView
    private lateinit var et_sales_search: EditText
    private lateinit var sotvPrice: TextView
    private lateinit var sotvVat: TextView
    private lateinit var sotvTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_sales_user, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.sales_management.split(",")


        (activity as MainActivity).setToolbarTitle(context?.resources?.getString(R.string.salesPageTitle)!!)

        initViews()
        setupAdapter()  /*Category Product Adapter*/
        initScrollListener() /*Category Product Scroll Listener*/
        setupOrderAdapter() /*Order Right Tabs Adapter*/
        setUpOrdersTabs() /*Order Right Tab List*/

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getCategories()
        } else {
            if (permissionsList.contains(PermissionKeys.view_categories)) {
                getCategories()
                pullToRefresh.isEnabled = true
            } else {
                pullToRefresh.isEnabled = false
            }
        }
    }

    private fun initViews() {
        salesLeftRecyclerView = v.findViewById(R.id.sales_recycler_view)
        salesOrderRecyclerView = v.findViewById(R.id.sales_order_recycler_view)

        pullToRefresh = v.findViewById(R.id.pull_to_refresh_sales)
        pullToRefresh.setOnRefreshListener { getProducts(0) }

        tvTotalHTAnswer = v.findViewById(R.id.totalHTAnswer)
        tvTotalTaxAnswer = v.findViewById(R.id.totalTaxAnswer)
        tvDeliverChargesAnswer = v.findViewById(R.id.deliveryAnswer)
        tvTotalTAVAnswer = v.findViewById(R.id.totalTAVAnswer)
        tvTotalTTCAnswer = v.findViewById(R.id.totalTTCAnswer)
        tvDeliverCharges = v.findViewById(R.id.delivery)

        salesProfileName = v.findViewById(R.id.salesProfileName)
        salesProfileSiren = v.findViewById(R.id.salesProfileSiren)
        salesProfileTAV = v.findViewById(R.id.saleProfileTVANo)
        salesPaymentStatus = v.findViewById(R.id.salesPaymentStatus)
        salesBalanceAmount = v.findViewById(R.id.salesBalanceAmount)
        salesOverdraftAmount = v.findViewById(R.id.salesOverdraftAmount)
        salePaymentLayout = v.findViewById(R.id.salePaymentLayout)
        et_sales_search = v.findViewById(R.id.et_sales_search)

        sotvPrice = v.findViewById(R.id.sotvPrice)
        sotvVat = v.findViewById(R.id.sotvVat)
        sotvTotal = v.findViewById(R.id.sotvTotal)

        sotvPrice.text = context!!.resources.getString(
            R.string.salesOrderPrice,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sotvVat.text = context!!.resources.getString(
            R.string.salesOrderVAT,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        sotvTotal.text = context!!.resources.getString(
            R.string.salesOrderTotal,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )



        et_sales_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                salesProductListAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        saleProfileLayout = v.findViewById(R.id.saleProfileLayout)
        bottomLayout = v.findViewById(R.id.bottomLayout)
        sales_pick_check_box = v.findViewById(R.id.sales_pick_check_box)
        sales_pick_check_box.setOnClickListener {
            deliveryType = Constants.pickUpDelivery
            deliveryAddress = Constants.pickUpDelivery
            deliveryLatLng = LatLng(0.0, 0.0)
            deliveryCharges = "0"
            tvDeliverCharges.visibility = View.GONE
            tvDeliverChargesAnswer.visibility = View.GONE
            calculateCharges(selectedOrderItems)

        }
        sales_fast_check_box = v.findViewById(R.id.sales_fast_check_box)
        sales_fast_check_box.setOnClickListener {

            val intent = Intent(context, AddressMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putDouble("lat", deliveryLatLng.latitude)
            bundle.putDouble("lng", deliveryLatLng.longitude)
            bundle.putString("address", deliveryAddress)
            intent.putExtras(bundle)
            startActivityForResult(intent, Constants.DELIVERY_LATLNG_CODE)
        }

        btnDelete = v.findViewById(R.id.btn_sales_delete)
        btnDelete.setOnClickListener(this)
        btnAdd = v.findViewById(R.id.btn_sales_add)
        btnAdd.setOnClickListener(this)
        btnSend = v.findViewById(R.id.btn_sales_send)
        btnSend.setOnClickListener(this)
        btnPrint = v.findViewById(R.id.btn_sales_print)
        btnPrint.setOnClickListener(this)
        btnInvoice = v.findViewById(R.id.btn_sales_invoice)
        btnInvoice.setOnClickListener(this)
        btnPreparation = v.findViewById(R.id.btn_sales_preparation)
        btnPreparation.setOnClickListener(this)

        companyImage = v.findViewById(R.id.salesCompanyImage)
        customerProileImage = v.findViewById(R.id.salesCustomerProfile)
        salesCompanyName = v.findViewById(R.id.salesCompanyName)

        salesCompanyName.text = loginModel.data.company_name
        Glide
            .with(context!!)
            .load(loginModel.data.company_image_path)
            .centerCrop()
            .placeholder(R.drawable.ic_plc)
            .into(companyImage)


        salesOrderRecyclerView.setOnDragListener { v: View?, event: DragEvent? ->
            when (event!!.action) {
                DragEvent.ACTION_DROP -> {

                    //handle the dragged view being dropped over a drop view
                    val view = event.localState as View
                    //view being dragged and dropped
                    val dropped = view as RelativeLayout

                    if (salesCustomerId != 0) {

                        for (i in expandedVarieties.indices)
                            for (j in expandedVarieties[i].lots.indices)
                                if (expandedVarieties[i].lots[j].id == dropped.id)
                                    draggedLotData = expandedVarieties[i].lots[j]

                        if (draggedLotData?.stock?.toDouble()!! != 0.0) {

                            val salesOrderItems = SalesOrderItems(
                                0,
                                loginModel.data.id.toString(),
                                salesCustomerId.toString(),
                                draggedLotData?.id.toString(),
                                draggedLotData?.lot_no.toString(),
                                draggedLotData?.product_name.toString(),
                                draggedLotData?.product_variety.toString(),
                                draggedLotData?.product_user_id.toString(),
                                draggedLotData?.`class`.toString(),
                                draggedLotData?.size.toString(),
                                draggedLotData?.origin.toString(),
                                draggedLotData?.selling_price.toString(),
                                draggedLotData?.selling_tare.toString(),
                                draggedLotData?.selling_tare.toString(),
                                draggedLotData?.stock.toString(),
                                draggedLotData?.selling_packing_unit.toString(),
                                draggedLotData?.income_packing_quantity.toString(),
                                draggedLotData?.vat.toString(),
                                ((draggedLotData?.selling_tare?.toDouble()!! * draggedLotData?.selling_price?.toDouble()!!) /*+ draggedLotData?.vat?.toDouble()!!*/).toString()
                            )

                            val db = AppDatabase.getAppDataBase(context = context!!)

                            val dbLotList = db!!.salesDAO().getSpecificLot(
                                draggedLotData?.id!!.toString(),
                                salesCustomerId.toString(),
                                loginModel.data.id.toString()
                            )
                            if (dbLotList.isEmpty()) {
                                db.salesDAO().insertSalesOrderItems(salesOrderItems)
                                activity!!.runOnUiThread {
                                    selectedOrderItems.add(salesOrderItems)
                                }
                            } else {
                                for (i in selectedOrderItems.indices) {
                                    if (selectedOrderItems[i].lotId.toInt() == draggedLotData?.id) {
                                        val check =
                                            dbLotList[0].quantity.toDouble() + draggedLotData?.selling_tare!!.toDouble()

                                        if ((check) <= selectedOrderItems[i].totalQuantity.toDouble()) {
                                            val q =
                                                (draggedLotData?.selling_tare?.toDouble()!! + selectedOrderItems[i].quantity.toDouble()).toString()

                                            selectedOrderItems[i].quantity = q

                                            val total =
                                                ((selectedOrderItems[i].quantity.toDouble() * selectedOrderItems[i].price.toDouble()) + selectedOrderItems[i].vat.toDouble()).toString()
                                            selectedOrderItems[i].total = total


                                            db.salesDAO().updateQuantityAndTotalOfSpecificLot(
                                                q, total, salesCustomerId.toString(),
                                                draggedLotData?.id!!.toString(),
                                                loginModel.data.id.toString()
                                            )
                                        } else {
                                            AppUtils.showToast(
                                                activity,
                                                "Seller doesn't have enough stock",
                                                false
                                            )
                                        }


                                    }
                                }
                            }

                            activity!!.runOnUiThread {
                                salesOrderAdapter.notifyDataSetChanged()
                                calculateCharges(selectedOrderItems)
                                bottomLayout.visibility = View.VISIBLE
                            }
                        } else {
                            AppUtils.showToast(
                                activity,
                                context!!.resources.getString(R.string.errorSalesNoQuantity),
                                false
                            )
                        }

                    } else {
                        AppUtils.showToast(
                            activity,
                            context!!.resources.getString(R.string.errorPleaseAddCustomerFirst),
                            false
                        )
                    }
                }
            }
            true
        }

    }

    private fun calculateCharges(selectedOrderItems: MutableList<SalesOrderItems>) {

        var totalHT = 0.0
        var totalTAX = 0.0
        var totalTAV = 0.0
        var totalTTC = 0.0

        for (i in selectedOrderItems.indices) {
            totalHT += selectedOrderItems[i].total.toDouble()
            totalTAV += (((selectedOrderItems[i].quantity.toDouble() * selectedOrderItems[i].price.toDouble()) / 100)
                    * selectedOrderItems[i].vat.toDouble())
            totalTAX += (((selectedOrderItems[i].quantity.toDouble() * selectedOrderItems[i].price.toDouble()) / 100)
                    * selectedOrderItems[i].vat.toDouble())
        }

        totalTTC = totalHT + totalTAV + deliveryCharges.toDouble()

        tvTotalHTAnswer.text = AppUtils.appendCurrency(totalHT.toString(), context!!)
        tvTotalTAVAnswer.text = AppUtils.appendCurrency(totalTAV.toString(), context!!)
        tvDeliverChargesAnswer.text = AppUtils.appendCurrency(deliveryCharges, context!!)
        tvTotalTaxAnswer.text = AppUtils.appendCurrency(totalTAX.toString(), context!!)
        tvTotalTTCAnswer.text = AppUtils.appendCurrency(totalTTC.toString(), context!!)

    }

    @SuppressLint("CheckResult")
    private fun setUpOrdersTabs() {
        val radioGroup: RadioGroup = v.findViewById(R.id.salesOrdersRadioGroup)
        radioGroup.removeAllViews()

        val db = AppDatabase.getAppDataBase(context = context!!)

        val salesList: MutableList<SalesModel> = ArrayList()
        db!!.salesDAO()
            .getSalesOfLoggedInUser(loggedInUserId = loginModel.data.id.toString()).forEach {
                salesList.add(SalesModel(it.id, it.loggedInUserId))
            }

        if (salesList.isNotEmpty()) {

            val orderList: List<SalesOrders> = db.salesDAO()
                .getSalesOrders(loggedInUserId = loginModel.data.id.toString())

            if (orderList.isNotEmpty()) {

                val inflater: LayoutInflater = LayoutInflater.from(context)

                for (i in orderList.indices) {
                    val radioButton: RadioButton =
                        inflater.inflate(R.layout.right_tab_radio_button, null) as RadioButton

                    if (orderList.size == 1)
                        radioButton.background =
                            ContextCompat.getDrawable(
                                context!!,
                                R.drawable.tab_left_right_button_selector
                            )
                    else
                        when (i) {
                            0 -> radioButton.background =
                                ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.tab_left_button_selector
                                )
                            orderList.size - 1 -> radioButton.background =
                                ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.tab_right_button_selector
                                )
                            else -> radioButton.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.tab_mid_button_selector
                                )
                        }

                    radioButton.id = orderList[i].customerId.toInt()
                    radioButton.text = "${orderList[i].customerName}'s Order"
                    radioButton.buttonDrawable = null
                    radioButton.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    radioButton.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
                    radioGroup.addView(radioButton)
                    radioButton.setOnClickListener {
                        for (a in 0 until radioGroup.childCount) {
                            val rb: RadioButton = radioGroup.getChildAt(a) as RadioButton
                            rb.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.colorWhite
                                )
                            )
                        }

                        radioButton
                            .isChecked = true

                        radioButton.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.colorDarkBlue
                            )
                        )
                        salesCustomerId = radioButton.id
                        saleProfileLayout.visibility = View.VISIBLE

                        for (j in orderList.indices) {
                            if (orderList[j].customerId.toInt() == salesCustomerId) {
                                Glide
                                    .with(context!!)
                                    .load(orderList[j].customerImage)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_plc)
                                    .into(customerProileImage)

                                salePaymentLayout.visibility = View.VISIBLE
                                salesPaymentStatus.text = orderList[j].customerPayStatus
                                salesBalanceAmount.text =
                                    AppUtils.appendCurrency(orderList[j].customerBalance, context!!)
                                salesOverdraftAmount.text = AppUtils.appendCurrency(
                                    orderList[j].customerOverdraft,
                                    context!!
                                )
                                salesProfileName.text = orderList[j].customerName
                                salesProfileSiren.text = context!!.resources.getString(
                                    R.string.salesLabelSiren,
                                    orderList[j].customerSiren
                                )
//                                salesProfileTAV.text =context!!.resources.getString(
//                                    R.string.salesLabelNoTVA,
//                                    orderList[j].customerNTVA
//                                )
                            }

                        }


                        selectedOrderItems.clear()
                        selectedOrderItems.addAll(
                            db.salesDAO()
                                .getSalesOrderItems(
                                    loginModel.data.id.toString(),
                                    salesCustomerId.toString()
                                )
                        )

                        if (selectedOrderItems.isNotEmpty()) {
                            salesOrderAdapter.notifyDataSetChanged()
                            bottomLayout.visibility = View.VISIBLE
                            calculateCharges(selectedOrderItems)
                        } else {
                            bottomLayout.visibility = View.GONE
                        }


                    }
                }

                val select = arguments?.getInt("customerId")
                if (select != null) {
                    for (i in orderList.indices)
                        if (orderList[i].customerId.toInt() == select)
                            radioGroup.getChildAt(i).callOnClick()
                } else
                    radioGroup.getChildAt(0).callOnClick() // To get the products at first time

            } else {
                salePaymentLayout.visibility = View.INVISIBLE
                saleProfileLayout.visibility = View.INVISIBLE
            }
        }

    }


    private fun setupOrderAdapter() {
        salesOrderAdapter = SalesOrderAdapter(this, selectedOrderItems)
        salesOrderRecyclerView.layoutManager = LinearLayoutManager(context!!)
        salesOrderRecyclerView.adapter = salesOrderAdapter
    }

    private fun setupAdapter() {
        salesProductListAdapter = SalesLeftTabsAdapter(this, productList)
        salesLeftRecyclerView.layoutManager = LinearLayoutManager(context!!)
        salesLeftRecyclerView.adapter = salesProductListAdapter
    }

    private fun initScrollListener() {
        salesLeftRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            productList.add(null)
            salesProductListAdapter.notifyItemInserted(productList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getProducts(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                salesLeftRecyclerView.post {
                    productList.removeAt(productList.size - 1)
                    salesProductListAdapter.notifyItemRemoved(productList.size)
                }
            }
            salesProductListAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun prodLotDetail(
        layout: LinearLayout,
        varieties: List<Variety>,
        productListData: ProductListData
    ) {
        layout.removeAllViews()
        val inflater: LayoutInflater = LayoutInflater.from(context)

        expandedVarieties.addAll(varieties)

        for (i in varieties.indices) {
            val view = inflater.inflate(R.layout.pro_category_list_detail, null, false)
            view.findViewById<TextView>(R.id.variatey_title).text = varieties[i].product_variety
            val line = view.findViewById<LinearLayout>(R.id.variatey_detail)

            view.findViewById<TextView>(R.id.productTvSell).text = context!!.resources.getString(
                R.string.pro_lot_de_lot_sell,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )

            val btnVarietyEdit = view.findViewById<Button>(R.id.btn_edit_variety)
            val btnspace = view.findViewById<Button>(R.id.btnspace)
            btnVarietyEdit.visibility = View.GONE
            btnspace.visibility = View.GONE

            val vari_lot = varieties[i].lots
            for (index in vari_lot.indices) {
                val lot_ob = vari_lot[index]
                val view_2 = inflater.inflate(R.layout.pro_category_list_detail_item, null, false)
                val productLotLayout = view_2.findViewById<RelativeLayout>(R.id.productLotLayout)
                productLotLayout.id = vari_lot[index].id
                if (permissionsList.contains(PermissionKeys.create_orders))
                    productLotLayout.setOnTouchListener(ItemTouchListener())
                view_2.findViewById<TextView>(R.id.lot_no).text = lot_ob.lot_no
                view_2.findViewById<TextView>(R.id.lot_quantity).text = lot_ob.stock
                view_2.findViewById<TextView>(R.id.lot_origin).text = lot_ob.origin
                view_2.findViewById<TextView>(R.id.lot_sell).text =
                    "${lot_ob.selling_price} / ${lot_ob.selling_unit}"
                view_2.findViewById<TextView>(R.id.lot_incom).text = lot_ob.incoming_date

                val btnLotEdit = view_2.findViewById<Button>(R.id.btn_edit)
                btnLotEdit.visibility = View.GONE


                line.addView(view_2)
            }
            layout.addView(view)
        }

    }

    private fun getCategories() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductCategories()
        RetrofitClient.apiCall(call, this, "GetProductCategories")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetProductCategories" -> {
                handleCategoriesResponse(jsonObject)
            }
            "GetProductsList" -> {
                handleListResponse(jsonObject)
            }
            "GetDeliveryCharges" -> {
                val data = jsonObject.getJSONObject("data")
                if (data.has("delivery_charges")) {
                    deliveryCharges = data.getString("delivery_charges")
                    tvDeliverCharges.visibility = View.VISIBLE
                    tvDeliverChargesAnswer.visibility = View.VISIBLE
                    calculateCharges(selectedOrderItems)
                }
            }
            "PostOrder" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity, jsonObject.getString("message"), true)

                val db = AppDatabase.getAppDataBase(context!!)
                db!!.salesDAO()
                    .deleteLotsOfCustomer(
                        salesCustomerId.toString(), loginModel.data.id.toString()
                    )

                db.salesDAO()
                    .deleteSalesOfCustomer(
                        salesCustomerId.toString(), loginModel.data.id.toString()
                    )
                selectedOrderItems.clear()
                salesOrderAdapter.notifyDataSetChanged()
                bottomLayout.visibility = View.GONE
                salesCustomerId = 0
                setUpOrdersTabs()
            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
        }
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
        }
        AppUtils.showToast(activity, message!!, false)
    }

    private fun handleCategoriesResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val categoriesModel =
            gson.fromJson(jsonObject.toString(), ProductCategoriesModel::class.java)

        val radioGroup: RadioGroup = v.findViewById(R.id.categoriesRadioGroup)
        val inflater: LayoutInflater = LayoutInflater.from(context)

        for (i in categoriesModel.data.indices) {
            val radioButton: RadioButton =
                inflater.inflate(R.layout.left_tab_radio_button, null) as RadioButton

            when (i) {
                0 -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_left_button_selector)
                categoriesModel.data.size - 1 -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_right_button_selector)
                else -> radioButton.background =
                    ContextCompat.getDrawable(context!!, R.drawable.tab_mid_button_selector)
            }

            radioButton.id = categoriesModel.data[i].id
            radioButton.text = categoriesModel.data[i].name
            radioButton.buttonDrawable = null
            radioButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            radioButton.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
            radioGroup.addView(radioButton)
            radioButton.setOnClickListener {
                for (a in 0 until radioGroup.childCount) {
                    val rb: RadioButton = radioGroup.getChildAt(a) as RadioButton
                    rb.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorWhite
                        )
                    )
                }

                radioButton
                    .isChecked = true

                radioButton.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                categoryId = radioButton.id
                productList.clear()

                if (loginModel.data.designation_id == 0) {
                    getProducts(0)
                } else {
                    if (permissionsList.contains(PermissionKeys.view_products)) {
                        salesLeftRecyclerView.visibility = View.VISIBLE
                        pullToRefresh.isEnabled = true
                        getProducts(0)
                    } else {
                        salesLeftRecyclerView.visibility = View.GONE
                        pullToRefresh.isEnabled = false
                    }
                }

            }
        }

        radioGroup.getChildAt(0).callOnClick() // To get the products at first time


    }


    fun getProducts(c: Int) {
        if (!pullToRefresh.isRefreshing)
            AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getProductList(categoryId, c)
        RetrofitClient.apiCall(call, this, "GetProductsList")
    }

    private fun handleListResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val proLisModel =
            gson.fromJson(jsonObject.toString(), ProductListModel::class.java)

        if (pullToRefresh.isRefreshing) {
            pullToRefresh.isRefreshing = false
            productList.clear()
        }

        if (productList.size > 0) {
            productList.removeAt(productList.size - 1)
            salesProductListAdapter.notifyItemRemoved(productList.size)
        }


        CURRENT_PAGE = proLisModel.meta.current_page
        LAST_PAGE = proLisModel.meta.last_page

        productList.addAll(proLisModel.data)


        salesProductListAdapter.notifyDataSetChanged()
        isLoading = false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sales_delete -> {
                if (saleItemsToDeleteIDs.isNotEmpty()) {
                    val db = AppDatabase.getAppDataBase(context!!)

                    for (i in selectedOrderItems.indices) {
                        for (j in saleItemsToDeleteIDs.indices) {
                            if (selectedOrderItems[i].lotId.toInt() == saleItemsToDeleteIDs[j]) {
                                db!!.salesDAO().deleteLots(
                                    salesCustomerId.toString(),
                                    selectedOrderItems[i].lotId,
                                    loginModel.data.id.toString()
                                )
                            }
                        }
                    }
                    saleItemsToDeleteIDs.clear()
                    selectedOrderItems.clear()
                    selectedOrderItems.addAll(
                        db!!.salesDAO().getSalesOrderItems(
                            loginModel.data.id.toString(),
                            salesCustomerId.toString()
                        )
                    )
                    salesOrderAdapter.notifyDataSetChanged()

                    if (selectedOrderItems.isNotEmpty()) {
                        salesOrderAdapter.notifyDataSetChanged()
                        bottomLayout.visibility = View.VISIBLE
                        calculateCharges(selectedOrderItems)
                    } else {
                        bottomLayout.visibility = View.GONE
                    }

                } else {
                    AppUtils.showToast(
                        activity,
                        context!!.resources.getString(R.string.errorPleaseSelectItemToDelete),
                        false
                    )
                }
            }
            R.id.btn_sales_send -> {
                postOrder()
            }
        }
    }

    private fun postOrder() {

        if (deliveryType == "") {
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.errorSelectDeliveryType),
                false
            )
            return
        }

        if (deliveryType == Constants.fastDelivery)
            if (deliveryAddress == "") {
                AppUtils.showToast(
                    activity,
                    context!!.resources.getString(R.string.errorSelectDeliveryAddress),
                    false
                )
                return
            }

        val jsonArray = JSONArray()
        for (i in selectedOrderItems.indices) {
            val jsonObject = JSONObject()
            jsonObject.put(
                "vat",
                (((selectedOrderItems[i].quantity.toDouble() * selectedOrderItems[i].price.toDouble()) / 100)
                        * selectedOrderItems[i].vat.toDouble()).toString()
            )
            jsonObject.put("product_id", selectedOrderItems[i].varietyId)
            jsonObject.put("product_lot_id", selectedOrderItems[i].lotId)
            jsonObject.put("quantity", selectedOrderItems[i].quantity)
            jsonObject.put("discount", "0.0")

            jsonArray.put(jsonObject)
        }

        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postOrder(
            jsonArray,
            deliveryLatLng.latitude.toString(),
            deliveryLatLng.longitude.toString(),
            deliveryAddress,
            deliveryType,
            salesCustomerId
        )
        RetrofitClient.apiCall(call, this, "PostOrder")
    }

    inner class ItemTouchListener : View.OnTouchListener {
        @SuppressLint("NewApi", "ClickableViewAccessibility")
        override fun onTouch(
            view: View,
            motionEvent: MotionEvent
        ): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = DragShadowBuilder(view)
                view.startDragAndDrop(data, shadowBuilder, view, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.DELIVERY_LATLNG_CODE) {

                deliveryAddress = data?.getStringExtra("address")!!

                if (deliveryAddress == null)
                    deliveryAddress = ""

                if (deliveryAddress != "") {
                    deliveryType = Constants.fastDelivery
                    deliveryLatLng =
                        LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0))

                    AppUtils.showDialog(context!!)
                    val api = RetrofitClient.getClient(context!!).create(Api::class.java)
                    val call = api.getDeliveryCharges(
                        deliveryLatLng.latitude.toString(),
                        deliveryLatLng.longitude.toString()
                    )
                    RetrofitClient.apiCall(call, this, "GetDeliveryCharges")

                } else {
                    deliveryAddress = ""
                    sales_fast_check_box.isChecked = false
                }

            }
        }
    }

    fun showQuantitySpinner(
        salesOrderItems: SalesOrderItems,
        position: Int,
        quantitySpinner: CustomSearchableSpinner
    ) {

        quantityStrings.clear()
        var i = salesOrderItems.tare.toDouble()
        while (i <= salesOrderItems.totalQuantity.toDouble()) {
            quantityStrings.add(String.format(Locale.ENGLISH, "%.2f", i))
            i += salesOrderItems.tare.toDouble()
        }

        quantitySpinner.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        quantitySpinner.setTitle(context!!.resources.getString(R.string.quantitySpinnerTitle))

        val quantityAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, quantityStrings
        )
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        quantitySpinner.adapter = quantityAdapter
        quantitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {

                    val newQuantity = quantityStrings[adapterView.selectedItemPosition]
                    selectedOrderItems[position].quantity = newQuantity

                    selectedOrderItems[position].total =
                        ((selectedOrderItems[position].quantity.toDouble() * selectedOrderItems[position].price.toDouble()) /*+ selectedOrderItems[position].vat.toDouble()*/).toString()

                    val db = AppDatabase.getAppDataBase(context!!)
                    db!!.salesDAO().updateQuantityAndTotalOfSpecificLot(
                        selectedOrderItems[position].quantity,
                        selectedOrderItems[position].total,
                        salesCustomerId.toString(),
                        selectedOrderItems[position].lotId,
                        loginModel.data.id.toString()
                    )

                    salesOrderAdapter.notifyDataSetChanged()
                    calculateCharges(selectedOrderItems)

                    AppUtils.hideKeyboard(activity!!)

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        for (x in quantityStrings.indices)
            if (quantityStrings[x] == selectedOrderItems[position].quantity)
                quantitySpinner.setSelection(x, true)

        val motionEvent: MotionEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0
        )
        quantitySpinner.dispatchTouchEvent(motionEvent)

    }

}