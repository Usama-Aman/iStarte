package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedSaleTaxRate
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedSaleTaxRateData
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedSaleTaxRateModel
import com.minbio.erp.main.models.SettingsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TurnOverInvoicedSaleTaxFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    private lateinit var v: View
    private var mYear: Int = 0
    private lateinit var ivArrowPrev: ImageView
    private lateinit var ivArrowNext: ImageView
    private lateinit var tvYear: TextView
    private lateinit var tvTotalJanSale: TextView
    private lateinit var tvTotalFebSale: TextView
    private lateinit var tvTotalMarSale: TextView
    private lateinit var tvTotalAprSale: TextView
    private lateinit var tvTotalMaySale: TextView
    private lateinit var tvTotalJunSale: TextView
    private lateinit var tvTotalJulSale: TextView
    private lateinit var tvTotalAugSale: TextView
    private lateinit var tvTotalSepSale: TextView
    private lateinit var tvTotalOctSale: TextView
    private lateinit var tvTotalNovSale: TextView
    private lateinit var tvTotalDecSale: TextView
    private lateinit var tvTotalJanPurchase: TextView
    private lateinit var tvTotalFebPurchase: TextView
    private lateinit var tvTotalMarPurchase: TextView
    private lateinit var tvTotalAprPurchase: TextView
    private lateinit var tvTotalMayPurchase: TextView
    private lateinit var tvTotalJunPurchase: TextView
    private lateinit var tvTotalJulPurchase: TextView
    private lateinit var tvTotalAugPurchase: TextView
    private lateinit var tvTotalSepPurchase: TextView
    private lateinit var tvTotalOctPurchase: TextView
    private lateinit var tvTotalNovPurchase: TextView
    private lateinit var tvTotalDecPurchase: TextView
    private lateinit var tvTotalSale: TextView
    private lateinit var tvTotalPurchase: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var btnRefresh: TextView
    private lateinit var tvNoData1: TextView
    private lateinit var tvNoData2: TextView
    private lateinit var purchaseTotalLayout: LinearLayout
    private lateinit var saleTotalLayout: LinearLayout
    private lateinit var fatoistrSaleRecyclerView: RecyclerView
    private lateinit var fatoistrPurchaseRecyclerView: RecyclerView
    private lateinit var saleAdapter: TurnOverInvoicedSaleTaxAdapter
    private lateinit var purchaseAdapter: TurnOverInvoicedSaleTaxAdapter

    private lateinit var toInvoicedSaleTaxRateData: TOInvoicedSaleTaxRateData
    private var saleTaxRate: MutableList<TOInvoicedSaleTaxRate> = ArrayList()
    private var purchaseTaxRate: MutableList<TOInvoicedSaleTaxRate> = ArrayList()
    private lateinit var fatoistrtvTotalExclTax1: TextView
    private lateinit var fatoistrtvTotalExclTax2: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fa_turnover_invoiced_sale_tax_rate, container, false)

        initViews()
        setUpAdapters()

        getInvoicedSaleTaxData()

        return v
    }

    private fun initViews() {
        tvTotalJanSale = v.findViewById(R.id.tvTotalJanSale)
        tvTotalFebSale = v.findViewById(R.id.tvTotalFebSale)
        tvTotalMarSale = v.findViewById(R.id.tvTotalMarSale)
        tvTotalAprSale = v.findViewById(R.id.tvTotalAprSale)
        tvTotalMaySale = v.findViewById(R.id.tvTotalMaySale)
        tvTotalJunSale = v.findViewById(R.id.tvTotalJunSale)
        tvTotalJulSale = v.findViewById(R.id.tvTotalJulSale)
        tvTotalAugSale = v.findViewById(R.id.tvTotalAugSale)
        tvTotalSepSale = v.findViewById(R.id.tvTotalSepSale)
        tvTotalOctSale = v.findViewById(R.id.tvTotalOctSale)
        tvTotalNovSale = v.findViewById(R.id.tvTotalNovSale)
        tvTotalDecSale = v.findViewById(R.id.tvTotalDecSale)
        tvTotalJanPurchase = v.findViewById(R.id.tvTotalJanPurchase)
        tvTotalFebPurchase = v.findViewById(R.id.tvTotalFebPurchase)
        tvTotalMarPurchase = v.findViewById(R.id.tvTotalMarPurchase)
        tvTotalAprPurchase = v.findViewById(R.id.tvTotalAprPurchase)
        tvTotalMayPurchase = v.findViewById(R.id.tvTotalMayPurchase)
        tvTotalJunPurchase = v.findViewById(R.id.tvTotalJunPurchase)
        tvTotalJulPurchase = v.findViewById(R.id.tvTotalJulPurchase)
        tvTotalAugPurchase = v.findViewById(R.id.tvTotalAugPurchase)
        tvTotalSepPurchase = v.findViewById(R.id.tvTotalSepPurchase)
        tvTotalOctPurchase = v.findViewById(R.id.tvTotalOctPurchase)
        tvTotalNovPurchase = v.findViewById(R.id.tvTotalNovPurchase)
        tvTotalDecPurchase = v.findViewById(R.id.tvTotalDecPurchase)
        tvTotalSale = v.findViewById(R.id.tvTotalSale)
        tvTotalPurchase = v.findViewById(R.id.tvTotalPurchase)
        currentDateTime = v.findViewById(R.id.currentDateTime)
        btnRefresh = v.findViewById(R.id.btnRefresh)
        tvNoData1 = v.findViewById(R.id.tvNoData1)
        tvNoData2 = v.findViewById(R.id.tvNoData2)
        fatoistrSaleRecyclerView = v.findViewById(R.id.fatoistrSaleRecyclerView)
        fatoistrPurchaseRecyclerView = v.findViewById(R.id.fatoistrPurchaseRecyclerView)
        purchaseTotalLayout = v.findViewById(R.id.purchaseTotalLayout)
        saleTotalLayout = v.findViewById(R.id.saleTotalLayout)

        fatoistrtvTotalExclTax1 = v.findViewById(R.id.fatoistrtvTotalExclTax1)
        fatoistrtvTotalExclTax2 = v.findViewById(R.id.fatoistrtvTotalExclTax2)

        fatoistrtvTotalExclTax1.text = context!!.resources.getString(
            R.string.fatoistrLabelTotalExclTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        fatoistrtvTotalExclTax2.text = context!!.resources.getString(
            R.string.fatoistrLabelTotalExclTax,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

        mYear = Calendar.getInstance().get(Calendar.YEAR)
        ivArrowPrev = v.findViewById(R.id.ivArrowPrev)
        ivArrowNext = v.findViewById(R.id.ivArrowNext)
        tvYear = v.findViewById(R.id.tvYear)
        tvYear.text = mYear.toString()
        ivArrowPrev.setOnClickListener(this)
        ivArrowNext.setOnClickListener(this)

        btnRefresh.setOnClickListener {
            getInvoicedSaleTaxData()
        }

        setDate()
    }

    private fun setDate() {
        val gson = Gson()
        val settingModel = gson.fromJson(
            SharedPreference.getSimpleString(context, Constants.settingsData),
            SettingsModel::class.java
        )
        var format: String = ""
        for (i in settingModel.data.date_formats.indices)
            if (settingModel.data.date_formats[i].value == settingModel.data.settings.date_format.toInt()) {
                when (settingModel.data.date_formats[i].format) {
                    "YYYY-MM-DD" -> {
                        format = "yyyy-MM-dd"
                    }
                    "DD-MM-YYYY" -> {
                        format = "dd-MM-yyyy"
                    }
                    "YYYY/MM/DD" -> {
                        format = "yyyy/MM/dd"
                    }
                    "DD/MM/YYYY" -> {
                        format = "dd-MM-yyyy"
                    }
                    else -> {
                        format = "dd-MM-yyyy"
                    }
                }

                val sdf = SimpleDateFormat(
                    "$format H:mm a",
                    Locale.getDefault()
                )
                val cdt: String = sdf.format(Date())
                currentDateTime.text = cdt
                break
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivArrowNext -> {
                mYear++
                tvYear.text = mYear.toString()
                getInvoicedSaleTaxData()
            }
            R.id.ivArrowPrev -> {
                mYear--
                tvYear.text = mYear.toString()
                getInvoicedSaleTaxData()
            }
        }
    }

    private fun setUpAdapters() {
        saleAdapter = TurnOverInvoicedSaleTaxAdapter(saleTaxRate)
        purchaseAdapter = TurnOverInvoicedSaleTaxAdapter(purchaseTaxRate)

        fatoistrSaleRecyclerView.layoutManager = object : LinearLayoutManager(context!!) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fatoistrSaleRecyclerView.adapter = saleAdapter

        fatoistrPurchaseRecyclerView.layoutManager = object : LinearLayoutManager(context!!) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fatoistrPurchaseRecyclerView.adapter = purchaseAdapter
    }

    private fun getInvoicedSaleTaxData() {
        AppUtils.showDialog(context!!)
        val call =
            RetrofitClient.getClient(context!!).create(Api::class.java)
                .getTurnOverInvoicedBySaleTaxRate(mYear)
        RetrofitClient.apiCall(call, this, " InvoicedBySaleTaxRate")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        val model =
            Gson().fromJson(jsonObject.toString(), TOInvoicedSaleTaxRateModel::class.java)

        toInvoicedSaleTaxRateData = model.data!!

        saleTaxRate.clear()
        purchaseTaxRate.clear()
        toInvoicedSaleTaxRateData.sale?.let { saleTaxRate.addAll(it) }
        toInvoicedSaleTaxRateData.purchase?.let { purchaseTaxRate.addAll(it) }

        if (saleTaxRate.isNullOrEmpty()) {
            fatoistrSaleRecyclerView.visibility = View.GONE
            saleTotalLayout.visibility = View.GONE
            tvNoData1.visibility = View.VISIBLE
        } else {
            fatoistrSaleRecyclerView.visibility = View.VISIBLE
            saleTotalLayout.visibility = View.VISIBLE
            tvNoData1.visibility = View.GONE

            tvTotalJanSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.January
            tvTotalFebSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.February
            tvTotalMarSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.March
            tvTotalAprSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.April
            tvTotalMaySale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.May
            tvTotalJunSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.June
            tvTotalJulSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.July
            tvTotalAugSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.August
            tvTotalSepSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.September
            tvTotalOctSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.October
            tvTotalNovSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.November
            tvTotalDecSale.text = toInvoicedSaleTaxRateData.sale_amount_by_months?.December
            tvTotalSale.text = toInvoicedSaleTaxRateData.sale_end_total
        }

        if (purchaseTaxRate.isNullOrEmpty()) {
            fatoistrPurchaseRecyclerView.visibility = View.GONE
            purchaseTotalLayout.visibility = View.GONE
            tvNoData2.visibility = View.VISIBLE
        } else {
            fatoistrPurchaseRecyclerView.visibility = View.VISIBLE
            purchaseTotalLayout.visibility = View.VISIBLE
            tvNoData2.visibility = View.GONE

            tvTotalJanPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.January
            tvTotalFebPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.February
            tvTotalMarPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.March
            tvTotalAprPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.April
            tvTotalMayPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.May
            tvTotalJunPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.June
            tvTotalJulPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.July
            tvTotalAugPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.August
            tvTotalSepPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.September
            tvTotalOctPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.October
            tvTotalNovPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.November
            tvTotalDecPurchase.text = toInvoicedSaleTaxRateData.purchase_amount_by_months?.December
            tvTotalPurchase.text = toInvoicedSaleTaxRateData.purchase_end_total
        }

        saleAdapter.notifyDataSetChanged()
        purchaseAdapter.notifyDataSetChanged()
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity!!, message!!, false)
    }


}