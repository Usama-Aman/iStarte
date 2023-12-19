package com.minbio.erp.financial_management.biling_payments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.biling_payments.adapters.BillingStatisticsAdapter
import com.minbio.erp.financial_management.biling_payments.models.BillingStatisticsData
import com.minbio.erp.financial_management.biling_payments.models.BillingStatisticsModel
import com.minbio.erp.financial_management.biling_payments.models.MonthlyStats
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference
import org.json.JSONObject

class BillingStatisticsFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 0
    private var CURRENT_PAGE: Int = 0
    private var isLoading: Boolean = false

    lateinit var v: View
    lateinit var noOfInvoicesChart: BarChart
    lateinit var invoiceAmountChart: BarChart
    lateinit var avgAmountChart: BarChart
    lateinit var graphsScroll: ScrollView

    private lateinit var statRecyclerView: RecyclerView
    private lateinit var billingStatisticsAdapter: BillingStatisticsAdapter
    private var statData: MutableList<BillingStatisticsData?> = ArrayList()
    private var fromWhere = -1

    private lateinit var bstattvTotalAmount : TextView
    private lateinit var bstattvAvgAmount : TextView
    private lateinit var invoiceAmount : TextView
    private lateinit var avgAmount : TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_billing_statistics, container, false)

        initViews()
        setAdapter()
        getBillingStatisticsData()

        return v
    }

    private fun initViews() {
        fromWhere = arguments?.getInt("fromWhere", -1)!!

        graphsScroll = v.findViewById(R.id.graphsScroll)
        noOfInvoicesChart = v.findViewById(R.id.noOfInvoicesChart)
        invoiceAmountChart = v.findViewById(R.id.invoiceAmountChart)
        avgAmountChart = v.findViewById(R.id.avgAmountChart)
        bstattvTotalAmount = v.findViewById(R.id.bstattvTotalAmount)
        bstattvAvgAmount = v.findViewById(R.id.bstattvAvgAmount)
        invoiceAmount =v.findViewById(R.id.invoiceAmount)
        avgAmount =v.findViewById(R.id.avgAmount)

        bstattvTotalAmount.text = context!!.resources.getString(
            R.string.bstatLabelTotalAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        bstattvAvgAmount.text = context!!.resources.getString(
            R.string.bstatLabelAvgAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        invoiceAmount.text = context!!.resources.getString(
            R.string.bstatLabelInvoiceAmountMonth,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )
        avgAmount.text = context!!.resources.getString(
            R.string.bstatLabelAvgAmount,
            SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
        )

    }

    private fun setAdapter() {
        statRecyclerView = v.findViewById(R.id.statsRecyclerView)
        billingStatisticsAdapter = BillingStatisticsAdapter(statData, this)
        statRecyclerView.layoutManager = LinearLayoutManager(context)
        statRecyclerView.adapter = billingStatisticsAdapter

    }


    private fun getBillingStatisticsData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call =
            if (fromWhere == 1) api.getBillingVendorStatisticsData()
            else api.getBillingCustomerStatisticsData()

        RetrofitClient.apiCall(call, this, "GetBillingStatisticsData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        if (tag == "GetBillingStatisticsData") {
            handleOrderResponse(jsonObject)
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


    private fun handleOrderResponse(jsonObject: JSONObject) {
        val gson = Gson()
        val model =
            gson.fromJson(jsonObject.toString(), BillingStatisticsModel::class.java)

        statData.addAll(model.data)

        billingStatisticsAdapter.notifyDataSetChanged()
        isLoading = false
    }


    fun setChartDataInvoices(
        chart: BarChart,
        monthlyStats: MonthlyStats?
    ) {

        chart.clear()
        chart.invalidate()

        chart.description.isEnabled = false
        chart.setTouchEnabled(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        /*X Axis Labels*/
        val xAxisLabel: ArrayList<String> = ArrayList()
        xAxisLabel.add("")
        xAxisLabel.add("Jan")
        xAxisLabel.add("Feb")
        xAxisLabel.add("Mar")
        xAxisLabel.add("Apr")
        xAxisLabel.add("May")
        xAxisLabel.add("Jun")
        xAxisLabel.add("Jul")
        xAxisLabel.add("Aug")
        xAxisLabel.add("Sep")
        xAxisLabel.add("Oct")
        xAxisLabel.add("Nov")
        xAxisLabel.add("Dec")

        val xAxis: XAxis = chart.xAxis;
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        xAxis.labelCount = xAxisLabel.size

        val values: ArrayList<BarEntry> = ArrayList()
        values.add(BarEntry(1f, monthlyStats?.Jan?.total_orders?.toFloat()!!))
        values.add(BarEntry(2f, monthlyStats.Feb.total_orders.toFloat()))
        values.add(BarEntry(3f, monthlyStats.Mar.total_orders.toFloat()))
        values.add(BarEntry(4f, monthlyStats.Apr.total_orders.toFloat()))
        values.add(BarEntry(5f, monthlyStats.May.total_orders.toFloat()))
        values.add(BarEntry(6f, monthlyStats.Jun.total_orders.toFloat()))
        values.add(BarEntry(7f, monthlyStats.Jul.total_orders.toFloat()))
        values.add(BarEntry(8f, monthlyStats.Aug.total_orders.toFloat()))
        values.add(BarEntry(9f, monthlyStats.Sep.total_orders.toFloat()))
        values.add(BarEntry(10f, monthlyStats.Oct.total_orders.toFloat()))
        values.add(BarEntry(11f, monthlyStats.Nov.total_orders.toFloat()))
        values.add(BarEntry(12f, monthlyStats.Dec.total_orders.toFloat()))

        populateChartData(chart, values)
        chart.animateY(1500)
    }


    fun setChartDataAmount(
        chart: BarChart,
        monthlyStats: MonthlyStats?
    ) {
        chart.clear()
        chart.invalidate()
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        /*X Axis Labels*/
        val xAxisLabel: ArrayList<String> = ArrayList()
        xAxisLabel.add("")
        xAxisLabel.add("Jan")
        xAxisLabel.add("Feb")
        xAxisLabel.add("Mar")
        xAxisLabel.add("Apr")
        xAxisLabel.add("May")
        xAxisLabel.add("Jun")
        xAxisLabel.add("Jul")
        xAxisLabel.add("Aug")
        xAxisLabel.add("Sep")
        xAxisLabel.add("Oct")
        xAxisLabel.add("Nov")
        xAxisLabel.add("Dec")

        val xAxis: XAxis = chart.xAxis;
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        xAxis.labelCount = xAxisLabel.size

        val values: ArrayList<BarEntry> = ArrayList()
        values.add(BarEntry(1f, monthlyStats?.Jan?.total_amount?.toFloat()!!))
        values.add(BarEntry(2f, monthlyStats.Feb.total_amount.toFloat()))
        values.add(BarEntry(3f, monthlyStats.Mar.total_amount.toFloat()))
        values.add(BarEntry(4f, monthlyStats.Apr.total_amount.toFloat()))
        values.add(BarEntry(5f, monthlyStats.May.total_amount.toFloat()))
        values.add(BarEntry(6f, monthlyStats.Jun.total_amount.toFloat()))
        values.add(BarEntry(7f, monthlyStats.Jul.total_amount.toFloat()))
        values.add(BarEntry(8f, monthlyStats.Aug.total_amount.toFloat()))
        values.add(BarEntry(9f, monthlyStats.Sep.total_amount.toFloat()))
        values.add(BarEntry(10f, monthlyStats.Oct.total_amount.toFloat()))
        values.add(BarEntry(11f, monthlyStats.Nov.total_amount.toFloat()))
        values.add(BarEntry(12f, monthlyStats.Dec.total_amount.toFloat()))

        populateChartData(chart, values)
        chart.animateY(1500)
    }

    fun setChartDataAvgAmount(
        chart: BarChart,
        monthlyStats: MonthlyStats?
    ) {
        chart.clear()
        chart.invalidate()
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        /*X Axis Labels*/
        val xAxisLabel: ArrayList<String> = ArrayList()
        xAxisLabel.add("")
        xAxisLabel.add("Jan")
        xAxisLabel.add("Feb")
        xAxisLabel.add("Mar")
        xAxisLabel.add("Apr")
        xAxisLabel.add("May")
        xAxisLabel.add("Jun")
        xAxisLabel.add("Jul")
        xAxisLabel.add("Aug")
        xAxisLabel.add("Sep")
        xAxisLabel.add("Oct")
        xAxisLabel.add("Nov")
        xAxisLabel.add("Dec")

        val xAxis: XAxis = chart.xAxis;
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        xAxis.labelCount = xAxisLabel.size

        val values: ArrayList<BarEntry> = ArrayList()
        values.add(BarEntry(1f, monthlyStats?.Jan?.average_amount?.toFloat()!!))
        values.add(BarEntry(2f, monthlyStats.Feb.average_amount.toFloat()))
        values.add(BarEntry(3f, monthlyStats.Mar.average_amount.toFloat()))
        values.add(BarEntry(4f, monthlyStats.Apr.average_amount.toFloat()))
        values.add(BarEntry(5f, monthlyStats.May.average_amount.toFloat()))
        values.add(BarEntry(6f, monthlyStats.Jun.average_amount.toFloat()))
        values.add(BarEntry(7f, monthlyStats.Jul.average_amount.toFloat()))
        values.add(BarEntry(8f, monthlyStats.Aug.average_amount.toFloat()))
        values.add(BarEntry(9f, monthlyStats.Sep.average_amount.toFloat()))
        values.add(BarEntry(10f, monthlyStats.Oct.average_amount.toFloat()))
        values.add(BarEntry(11f, monthlyStats.Nov.average_amount.toFloat()))
        values.add(BarEntry(12f, monthlyStats.Dec.average_amount.toFloat()))

        populateChartData(chart, values)
        chart.animateY(1500)
    }


    private fun populateChartData(
        chart: BarChart,
        values: ArrayList<BarEntry>
    ) {


        val set: BarDataSet

        if (chart.data == null || chart.data.dataSetCount < 0) {

            set = BarDataSet(values, "")
            set.setDrawIcons(false)
            set.color = ContextCompat.getColor(context!!, R.color.tab_btn_normal)

            set.setDrawValues(false)

            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set)

            val lineData = BarData(dataSets)
            chart.data = lineData
            chart.setFitBars(true)

        }

    }


}