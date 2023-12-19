package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

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
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBindingData
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBindingModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject
import java.util.*

class ExpenseReportBindingFragment : Fragment(), View.OnClickListener, ResponseCallBack {

    private lateinit var v: View
    private var mYear: Int = 0
    private lateinit var ivArrowPrev: ImageView
    private lateinit var ivArrowNext: ImageView
    private lateinit var tvYear: TextView

    private lateinit var notBindAdapter: ExpenseReportBindingAdapter
    private lateinit var bindAdapter: ExpenseReportBindingAdapter
    private lateinit var faNotBindRecyclerView: RecyclerView
    private lateinit var faBindRecyclerView: RecyclerView
    private lateinit var tvNoData1: TextView
    private lateinit var tvNoData2: TextView
    private lateinit var btnBindAutomatically: TextView
    private lateinit var faerbtvLinesNotBound: TextView
    private lateinit var faerbtvLinesBound: TextView

    private lateinit var api: Api
    private var expenseReportBindingData: ExpenseReportBindingData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_expense_report_binding,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()

        AppUtils.showDialog(context!!)
        getExpenseReportBinding()

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
        faerbtvLinesNotBound =v.findViewById(R.id.faerbtvLinesNotBound)
        faerbtvLinesBound =v.findViewById(R.id.faerbtvLinesBound)
        faerbtvLinesNotBound.text = context!!.resources.getString(
            R.string.faerbLabelLinesNotBound,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        faerbtvLinesBound.text = context!!.resources.getString(
            R.string.faerbLabelLinesBound,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )


        btnBindAutomatically.setOnClickListener {
            AppUtils.showDialog(context!!)
            val call = api.expenseReportBindAutomatically()
            RetrofitClient.apiCall(call, this, "ExpenseReportBindAutomatically")
        }
    }

    private fun setUpAdapters() {
        notBindAdapter = ExpenseReportBindingAdapter(expenseReportBindingData, false)
        bindAdapter = ExpenseReportBindingAdapter(expenseReportBindingData, true)

        faNotBindRecyclerView.layoutManager = LinearLayoutManager(context)
        faBindRecyclerView.layoutManager = LinearLayoutManager(context)

        faNotBindRecyclerView.adapter = notBindAdapter
        faBindRecyclerView.adapter = bindAdapter
    }

    private fun getExpenseReportBinding() {
        val call = api.getExpenseReportBinding(mYear)
        RetrofitClient.apiCall(call, this, "CustomerInvoiceBinding")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivArrowNext -> {
                mYear++
                tvYear.text = mYear.toString()
                AppUtils.showDialog(context!!)
                getExpenseReportBinding()
            }
            R.id.ivArrowPrev -> {
                mYear--
                tvYear.text = mYear.toString()
                AppUtils.showDialog(context!!)
                getExpenseReportBinding()
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {
            "CustomerInvoiceBinding" -> {
                AppUtils.dismissDialog()
                val model =
                    Gson().fromJson(jsonObject.toString(), ExpenseReportBindingModel::class.java)

                expenseReportBindingData = model.data

                if (expenseReportBindingData?.tobind?.report == null) {
                    tvNoData1.visibility = View.VISIBLE
                    faNotBindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData1.visibility = View.GONE
                    faNotBindRecyclerView.visibility = View.VISIBLE
                }

                if (expenseReportBindingData?.bound.isNullOrEmpty()) {
                    tvNoData2.visibility = View.VISIBLE
                    faBindRecyclerView.visibility = View.GONE
                } else {
                    tvNoData2.visibility = View.GONE
                    faBindRecyclerView.visibility = View.VISIBLE
                }

                setUpAdapters()
            }
            "ExpenseReportBindAutomatically" -> {
                getExpenseReportBinding()
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