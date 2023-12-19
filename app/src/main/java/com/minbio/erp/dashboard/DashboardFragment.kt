package com.minbio.erp.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.minbio.erp.main.MainActivity
import com.minbio.erp.R


class DashboardFragment : Fragment() {

    private lateinit var v: View

    private lateinit var turnOverVsOverdueChart: LineChart
    private lateinit var turnOverVsInterfelChart: LineChart
    private lateinit var turnOverVsSupplierChart: LineChart
    private lateinit var turnOverVsProductLostChart: LineChart
    private lateinit var productLostVsTrashChart: LineChart
    private lateinit var creditNoteVsProductLostChart: LineChart
    private lateinit var totalLineChart: LineChart

    private lateinit var dashboardTabCustomer: RadioButton
    private lateinit var dashboardTabSupplier: RadioButton
    private lateinit var dashboardTabInvoice: RadioButton
    private lateinit var dashboardTabOrder: RadioButton
    private lateinit var dashboardTabInventory: RadioButton
    private lateinit var dashboardTabBalance: RadioButton

    private lateinit var totalPieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_dashboard, container, false)

        (activity as MainActivity).setToolbarTitle(context!!.resources.getString(R.string.dashboardPageTitle))

        initViews()

        return v
    }


    private fun initViews() {
        turnOverVsOverdueChart = v.findViewById(R.id.turnOverVsOverdueChart)
        turnOverVsInterfelChart = v.findViewById(R.id.turnOverVsInterfelChart)
        turnOverVsSupplierChart = v.findViewById(R.id.turnOverVsSupplierChart)
        turnOverVsProductLostChart = v.findViewById(R.id.turnOverVsProductLostChart)
        productLostVsTrashChart = v.findViewById(R.id.productLostVsTrashChart)
        creditNoteVsProductLostChart = v.findViewById(R.id.creditNoteVsProductLostChart)
        totalLineChart = v.findViewById(R.id.totalLineChart)

        dashboardTabCustomer = v.findViewById(R.id.dashboardTabCustomer)
        dashboardTabSupplier = v.findViewById(R.id.dashboardTabSupplier)
        dashboardTabInvoice = v.findViewById(R.id.dashboardTabInvoice)
        dashboardTabOrder = v.findViewById(R.id.dashboardTabOrder)
        dashboardTabInventory = v.findViewById(R.id.dashboardTabInventory)
        dashboardTabBalance = v.findViewById(R.id.dashboardTabBalance)

        totalPieChart = v.findViewById(R.id.totalPieChart)

        setChartDataAndStyle(turnOverVsOverdueChart)
        setChartDataAndStyle(turnOverVsInterfelChart)
        setChartDataAndStyle(turnOverVsSupplierChart)
        setChartDataAndStyle(turnOverVsProductLostChart)
        setChartDataAndStyle(productLostVsTrashChart)
        setChartDataAndStyle(creditNoteVsProductLostChart)

        setChartDataAndStyle(totalLineChart)
        setPieChart(totalPieChart)


        dashboardTabCustomer.setOnClickListener { updateView(dashboardTabCustomer) }
        dashboardTabSupplier.setOnClickListener { updateView(dashboardTabSupplier) }
        dashboardTabInvoice.setOnClickListener { updateView(dashboardTabInvoice) }
        dashboardTabOrder.setOnClickListener { updateView(dashboardTabOrder) }
        dashboardTabInventory.setOnClickListener { updateView(dashboardTabInventory) }
        dashboardTabBalance.setOnClickListener { updateView(dashboardTabBalance) }

        dashboardTabCustomer.callOnClick()
    }


    private fun updateView(view: RadioButton) {

        dashboardTabCustomer.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        dashboardTabSupplier.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        dashboardTabInvoice.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        dashboardTabOrder.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        dashboardTabInventory.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        dashboardTabBalance.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))

        var profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabCustomer.setCompoundDrawables(profile_drawable, null, null, null);

        var supplier_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_supplier)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabSupplier.setCompoundDrawables(supplier_drawable, null, null, null);

        var invoice_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_invoice)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabInvoice.setCompoundDrawables(invoice_drawable, null, null, null)

        var order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabOrder.setCompoundDrawables(order_drawable, null, null, null);

        var inventory_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_inventory)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabInventory.setCompoundDrawables(inventory_drawable, null, null, null);

        var balance_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_eblance)
            ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        dashboardTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

        when (view.id) {
            R.id.dashboardTabCustomer -> {
                dashboardTabCustomer.isChecked = true

                dashboardTabCustomer.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                profile_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_profile_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabCustomer.setCompoundDrawables(profile_drawable, null, null, null);


            }
            R.id.dashboardTabSupplier -> {
                dashboardTabSupplier.isChecked = true

                dashboardTabSupplier.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                supplier_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_supplier_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabSupplier.setCompoundDrawables(supplier_drawable, null, null, null);

            }
            R.id.dashboardTabInvoice -> {
                dashboardTabInvoice.isChecked = true

                dashboardTabInvoice.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                invoice_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_invoice_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabInvoice.setCompoundDrawables(invoice_drawable, null, null, null);

            }
            R.id.dashboardTabOrder -> {
                dashboardTabOrder.isChecked = true

                dashboardTabOrder.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                order_drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_order_sel)
                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabOrder.setCompoundDrawables(order_drawable, null, null, null);

            }
            R.id.dashboardTabInventory -> {
                dashboardTabInventory.isChecked = true

                dashboardTabInventory.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                inventory_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_inventory_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabInventory.setCompoundDrawables(inventory_drawable, null, null, null);

            }
            R.id.dashboardTabBalance -> {
                dashboardTabBalance.isChecked = true

                dashboardTabBalance.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorDarkBlue
                    )
                )

                balance_drawable =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_eblance_sel)
                        ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                dashboardTabBalance.setCompoundDrawables(balance_drawable, null, null, null);

            }
        }
    }


    private fun setChartDataAndStyle(chart: LineChart) {

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


        /*Marker View*/
        if (chart.id == R.id.totalLineChart) {
//            chart.setTouchEnabled(true)
            val markerView = MarkerView(context!!, R.layout.custom_marker_view)
            markerView.chartView = chart
            chart.marker = markerView
        }

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
        xAxis.granularity = 1f

        // axis range
        val yAxis: YAxis = chart.axisLeft
        yAxis.axisMinimum = 0f

        populateChartData(chart)
        chart.animateX(1500)
    }

    private fun populateChartData(chart: LineChart) {

        val values: ArrayList<Entry> = ArrayList<Entry>()

        for (i in 1..12) {
            val random: Float = ((Math.random() * 30) + 30).toFloat()
            values.add(Entry(i.toFloat(), random))
        }

        var set: LineDataSet

        if (chart.data == null || chart.data.dataSetCount < 0) {

            set = LineDataSet(values, "")
            set.setDrawIcons(false)
            set.disableDashedLine()

            // black lines and points
            set.color = ContextCompat.getColor(context!!, R.color.tab_btn_normal)
            set.setDrawCircleHole(true)
            set.setCircleColor(ContextCompat.getColor(context!!, R.color.tab_btn_normal))

            // line thickness and point size
            set.lineWidth = 2f
            set.circleRadius = 3f

            set.setDrawValues(false)


            // set color of filled area
            if (chart.id == R.id.totalLineChart) {
                if (Utils.getSDKInt() >= 18) {
                    // drawables only supported on api level 18 and above
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.fade_chart_blue);
                    set.fillDrawable = drawable
                }
                set.setDrawFilled(true)
            }

            // set the filled area


            val dataSets: ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
            dataSets.add(set)

            val lineData = LineData(dataSets)

            chart.data = lineData

        }

    }

    private fun setPieChart(chart: PieChart) {
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 5f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 0.95f

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 60f
        chart.transparentCircleRadius = 61f

        chart.rotationAngle = 0f
        chart.isRotationEnabled = false
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true
        chart.setDrawCenterText(false)

        populatePieChart(chart, 5)

        //Animate Chart
        chart.animateY(1400, Easing.EaseInOutQuad)

        // chart.spin(2000, 0, 360);
        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

    }

    private fun populatePieChart(chart: PieChart, range: Int) {
        var entries: ArrayList<PieEntry> = ArrayList()

        for (i in 1..2) entries.add(PieEntry((((Math.random() * range + range / 5).toFloat()))))

        var dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(false)

        // add a lot of colors
        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.tab_btn_normal))
        colors.add(ContextCompat.getColor(context!!, R.color.light_gray))

        dataSet.colors = colors

        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        chart.data = data

        // undo all highlights
        chart.highlightValues(null)

        chart.invalidate()

    }

}