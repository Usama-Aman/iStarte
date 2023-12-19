package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding

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
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBindingData
import com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models.VendorInvoiceBindingModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class VendorInvoiceBindingFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    private var mYear: Int = 0
    private lateinit var v: View
    private lateinit var ivArrowPrev: ImageView
    private lateinit var ivArrowNext: ImageView
    private lateinit var tvYear: TextView

    private lateinit var notBindAdapter: VendorInvoiceBindingAdapter
    private lateinit var bindAdapter: VendorInvoiceBindingAdapter
    private lateinit var faNotBindRecyclerView: RecyclerView
    private lateinit var faBindRecyclerView: RecyclerView
    private lateinit var tvNoData1: TextView
    private lateinit var tvNoData2: TextView
    private lateinit var btnBindAutomatically: TextView
    private lateinit var favibtvLinesNotBound: TextView
    private lateinit var favibtvLinesBound: TextView

    private lateinit var api: Api
    private var vendorInvoiceBindingData: VendorInvoiceBindingData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_vendor_invoice_binding,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        AppUtils.showDialog(context!!)
        getVendorInvoiceBinding()

        return v
    }

    private fun initViews() {
        mYear = Calendar.getInstance().get(Calendar.YEAR)

        ivArrowPrev = v.findViewById(R.id.ivArrowPrev)
        ivArrowNext = v.findViewById(R.id.ivArrowNext)
        tvYear = v.findViewById(R.id.tvYear)
        tvYear.text = mYear.toString()
        ivArrowPrev.setOnClickListener(this)
        ivArrowNext.setOnClickListener(this)

        faNotBindRecyclerView = v.findViewById(R.id.faNotBindRecyclerView)
        faBindRecyclerView = v.findViewById(R.id.faBindRecyclerView)
        tvNoData1 = v.findViewById(R.id.tvNoData1)
        tvNoData2 = v.findViewById(R.id.tvNoData2)
        btnBindAutomatically = v.findViewById(R.id.btnBindAutomatically)
        favibtvLinesNotBound =v.findViewById(R.id.favibtvLinesNotBound)
        favibtvLinesBound =v.findViewById(R.id.favibtvLinesBound)
        favibtvLinesNotBound.text = context!!.resources.getString(
            R.string.favibLabelLinesNotBound,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        favibtvLinesBound.text = context!!.resources.getString(
            R.string.favibLabelLinesBound,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        btnBindAutomatically.setOnClickListener {
            AppUtils.showDialog(context!!)
            val call = api.vendorInvoiceBindAutomatically()
            RetrofitClient.apiCall(call, this, "VendorInvoiceBindAutomatically")
        }

    }

    private fun setUpAdapters() {
        notBindAdapter = VendorInvoiceBindingAdapter(vendorInvoiceBindingData, false)
        bindAdapter = VendorInvoiceBindingAdapter(vendorInvoiceBindingData, true)

        faNotBindRecyclerView.layoutManager = LinearLayoutManager(context)
        faBindRecyclerView.layoutManager = LinearLayoutManager(context)

        faNotBindRecyclerView.adapter = notBindAdapter
        faBindRecyclerView.adapter = bindAdapter
    }

    private fun getVendorInvoiceBinding() {
        val call = api.getVendorInvoiceBinding(mYear)
        RetrofitClient.apiCall(call, this, "VendorInvoiceBinding")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivArrowNext -> {
                mYear++
                tvYear.text = mYear.toString()
                AppUtils.showDialog(context!!)
                getVendorInvoiceBinding()
            }
            R.id.ivArrowPrev -> {
                mYear--
                tvYear.text = mYear.toString()
                AppUtils.showDialog(context!!)
                getVendorInvoiceBinding()
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()

        when (tag) {
            "VendorInvoiceBinding" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), VendorInvoiceBindingModel::class.java)

                vendorInvoiceBindingData = model.data

                if (vendorInvoiceBindingData?.tobind?.report == null) {
                    tvNoData1.visibility = View.VISIBLE
                    faNotBindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData1.visibility = View.GONE
                    faNotBindRecyclerView.visibility = View.VISIBLE
                }

                if (vendorInvoiceBindingData?.bound.isNullOrEmpty()) {
                    tvNoData2.visibility = View.VISIBLE
                    faBindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData2.visibility = View.GONE
                    faBindRecyclerView.visibility = View.VISIBLE
                }

                setUpAdapters()


            }
            "VendorInvoiceBindAutomatically" -> {
                getVendorInvoiceBinding()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
            }
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


}