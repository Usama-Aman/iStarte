package com.minbio.erp.product_management.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.product_management.adapter.ProductInventoryAdapter
import com.minbio.erp.product_management.model.InventoryListData
import com.minbio.erp.product_management.model.InventoryModel
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.PermissionKeys
import org.json.JSONObject
import java.util.*

class ProductInventoryFragment : Fragment(), ResponseCallBack {

    private lateinit var productInventoryAdapter: ProductInventoryAdapter
    private lateinit var inventoryRecyclerView: RecyclerView

    private lateinit var tvListNull: TextView
    private lateinit var ivListNull: ImageView
    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    private lateinit var v: View

    var inventoryList: MutableList<InventoryListData?> = ArrayList()

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_product_inventory, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.product_management.split(",")

        initViews()
        setAdapter()
        initScrollListener()

        setUpPermissions()

        return v
    }

    private fun setUpPermissions() {
        if (loginModel.data.designation_id == 0) {
            getInventoryList(0)
        } else {
            if (permissionsList.contains(PermissionKeys.view_products)) {
                inventoryRecyclerView.visibility = View.VISIBLE
                getInventoryList(0)
            } else {
                inventoryRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        tvListNull = v.findViewById(R.id.tvListNull)
        ivListNull = v.findViewById(R.id.ivListNull)
    }


    private fun getInventoryList(c: Int) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.getInventoryList(0, c)
        RetrofitClient.apiCall(call, this, "GetInventory")
    }

    private fun setAdapter() {
        inventoryRecyclerView = v.findViewById(R.id.inventory_recycler)
        productInventoryAdapter = ProductInventoryAdapter(this, inventoryList)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(context)
        inventoryRecyclerView.adapter = productInventoryAdapter

    }

    private fun initScrollListener() {
        inventoryRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == inventoryList.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            inventoryList.add(null)
            productInventoryAdapter.notifyItemInserted(inventoryList.size - 1)
            if (CURRENT_PAGE != LAST_PAGE) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getInventoryList(CURRENT_PAGE + 1)
                    }
                }, 1000)
            } else {
                inventoryRecyclerView.post {
                    inventoryList.removeAt(inventoryList.size - 1)
                    productInventoryAdapter.notifyItemRemoved(inventoryList.size)
                }
            }
            productInventoryAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetInventory") {
            handleGetInventoryResponse(jsonObject)
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    private fun handleGetInventoryResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val inventoryData =
            gson.fromJson(jsonObject.toString(), InventoryModel::class.java)

        if (inventoryList.size > 0) {
            inventoryList.removeAt(inventoryList.size - 1)
            productInventoryAdapter.notifyItemRemoved(inventoryList.size)
        }


        CURRENT_PAGE = inventoryData.meta.current_page
        LAST_PAGE = inventoryData.meta.last_page

        inventoryList.addAll(inventoryData.data)

        if (inventoryList.isEmpty()) {
            tvListNull.visibility = View.VISIBLE
            inventoryRecyclerView.visibility = View.GONE
        } else {
            tvListNull.visibility = View.GONE
            inventoryRecyclerView.visibility = View.VISIBLE
        }

        productInventoryAdapter.notifyDataSetChanged()
        isLoading = false
    }


}