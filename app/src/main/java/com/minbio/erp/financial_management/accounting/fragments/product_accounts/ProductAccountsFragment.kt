package com.minbio.erp.financial_management.accounting.fragments.product_accounts

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProductAccountsFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    var LAST_PAGE: Int = 1
    var CURRENT_PAGE: Int = 1

    private lateinit var rbForSale: RadioButton
    private lateinit var rbForSaleExported: RadioButton
    private lateinit var rbPurchase: RadioButton
    private lateinit var rbForPurchaseImported: RadioButton
    private lateinit var newAssignedSpinner: CustomSearchableSpinner

    private lateinit var searchItem: ImageView
    private lateinit var fapaRecyclerView: RecyclerView
    private lateinit var productAccountsAdapter: ProductAccountsAdapter

    private var modeId = ""
    var refToSearch = ""
    var labelToSearch = ""
    var forSaleToSearch = ""
    var dedicatedAccountNumberToSearch = ""
    var dedicatedAccountIdToSearch = ""

    private var itemPosition = -1

    var productAccountsData: MutableList<ProductAccountsData?> = ArrayList()
    var parentAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var parentAccountListStrings: MutableList<String> = ArrayList()

    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_product_accunts,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()
        setUpAdapter()
        initScrollListener()

        return v
    }

    private fun getPareAccountList() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "ParentAccountList")
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            refToSearch = ""
            labelToSearch = ""
            forSaleToSearch = ""
            dedicatedAccountNumberToSearch = ""
            dedicatedAccountIdToSearch = ""

            CURRENT_PAGE = 1
            LAST_PAGE = 1
            productAccountsData.clear()
            getProductAccounts()
        }
        newAssignedSpinner = v.findViewById(R.id.newAssignedSpinner)
        fapaRecyclerView = v.findViewById(R.id.fapaRecyclerView)
        searchItem = v.findViewById(R.id.searchItem)
        searchItem.setOnClickListener {
            val searchDialog = ProductAccountSearchDialog(context!!, this)
            searchDialog.setOwnerActivity(activity!!)
            searchDialog.show()
        }
        rbForSale = v.findViewById(R.id.rbForSale)
        rbForSaleExported = v.findViewById(R.id.rbForSaleExported)
        rbPurchase = v.findViewById(R.id.rbPurchase)
        rbForPurchaseImported = v.findViewById(R.id.rbForPurchaseImported)

        rbForSale.setOnClickListener { updateRadioButtons(rbForSale) }
        rbForSaleExported.setOnClickListener { updateRadioButtons(rbForSaleExported) }
        rbPurchase.setOnClickListener { updateRadioButtons(rbPurchase) }
        rbForPurchaseImported.setOnClickListener { updateRadioButtons(rbForPurchaseImported) }

        rbForSale.callOnClick()

    }

    private fun setUpAdapter() {
        productAccountsAdapter =
            ProductAccountsAdapter(this, productAccountsData, parentAccountList)
        fapaRecyclerView.layoutManager = LinearLayoutManager(context!!)
        fapaRecyclerView.adapter = productAccountsAdapter
    }

    private fun updateRadioButtons(view: RadioButton) {
        rbForSale.isChecked = false
        rbForSaleExported.isChecked = false
        rbPurchase.isChecked = false
        rbForPurchaseImported.isChecked = false


        view.isChecked = true

        CURRENT_PAGE = 1
        LAST_PAGE = 1

        refToSearch = ""
        labelToSearch = ""
        forSaleToSearch = ""
        dedicatedAccountIdToSearch = ""

        modeId = when (view.id) {
            R.id.rbForSale -> "mode_sales"
            R.id.rbForSaleExported -> "mode_sales_exported"
            R.id.rbPurchase -> "mode_purchases"
            R.id.rbForPurchaseImported -> "mode_purchases_imported"
            else -> ""
        }

        productAccountsData.clear()
        parentAccountList.clear()
        parentAccountListStrings.clear()
        getPareAccountList()

    }


    private fun initScrollListener() {
        fapaRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productAccountsData.size - 1) {
                    recyclerView.post {
                        try {
                            if (CURRENT_PAGE < LAST_PAGE) {
                                Handler().postDelayed({
                                    CURRENT_PAGE++
                                    getProductAccounts()
                                }, 1000)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }


    fun getProductAccounts() {
        val call = api.getProductAccounts(
            refToSearch,
            labelToSearch,
            dedicatedAccountNumberToSearch,
            dedicatedAccountIdToSearch,
            modeId, CURRENT_PAGE
        )
        RetrofitClient.apiCall(call, this, "GetProductAccounts")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "GetProductAccounts" -> {
                AppUtils.dismissDialog()

                val model =
                    Gson().fromJson(jsonObject.toString(), ProductAccountsModel::class.java)

                if (productAccountsData.size > 0) {
                    productAccountsData.removeAt(productAccountsData.size - 1)
                    productAccountsAdapter.notifyItemRemoved(productAccountsData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                productAccountsData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (productAccountsData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    fapaRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    fapaRecyclerView.visibility = View.VISIBLE
                }

                if (CURRENT_PAGE < LAST_PAGE) {
                    productAccountsData.add(null)
                    productAccountsAdapter.notifyItemInserted(productAccountsData.size - 1)
                } else
                    productAccountsAdapter.notifyDataSetChanged()


            }
            "ParentAccountList" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                parentAccountList.add(
                    ParentAccountsData(
                        "",
                        0,
                        ""
                    )
                )
                parentAccountList.addAll(model.data)

                for (i in parentAccountList.indices) {
                    if (i > 0)
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + " - " + parentAccountList[i]?.label)
                    else
                        parentAccountListStrings.add(parentAccountList[i]?.account_number!! + parentAccountList[i]?.label)
                }

                getProductAccounts()
            }
            "PostProductAccounts" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
            }
        }
    }


    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, message!!, false)
    }

    fun showAccountSpinner(position: Int) {
        var byUser = false

        newAssignedSpinner.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        newAssignedSpinner.setTitle(context!!.resources.getString(R.string.newAssignedAccountSpinnerTitle))

        val quantityAdapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_item, parentAccountListStrings
        )
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        newAssignedSpinner.adapter = quantityAdapter
        newAssignedSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long
                ) {

                    productAccountsData[position]?.account_number =
                        parentAccountList[i]?.account_number!!
                    productAccountsData[position]?.accounting_chart_account_id =
                        parentAccountList[i]?.id!!
                    productAccountsData[position]?.chart_account_label =
                        parentAccountList[i]?.label!!

                    productAccountsAdapter.notifyItemChanged(position)
                    AppUtils.hideKeyboard(activity!!)

                    if (byUser)
                        postProductAccountData(productAccountsData[position])

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        for (x in parentAccountList.indices)
            if (parentAccountList[x]?.id == productAccountsData[position]?.accounting_chart_account_id) {
                newAssignedSpinner.setSelection(x)
            }

        val motionEvent: MotionEvent = MotionEvent.obtain(
            0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0
        )

        newAssignedSpinner.dispatchTouchEvent(motionEvent)

        Handler().postDelayed({
            byUser = true
        }, (parentAccountList.size + 100).toLong())
    }

    private fun postProductAccountData(productAccountsData: ProductAccountsData?) {
        AppUtils.showDialog(context!!)
        val call = api.postProductAccountData(
            productAccountsData?.accounting_chart_account_id!!, modeId, productAccountsData.id!!
        )
        RetrofitClient.apiCall(call, this, "PostProductAccounts")
    }


}