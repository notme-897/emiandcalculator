package com.example.calculatoremi.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.example.calculatoremi.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*

class PieChartActivity : BaseResultActivity() {

    private var principalAmount: Double = 0.0
    private var interestAmount: Double = 0.0

    override fun getResultLayoutResId(): Int = R.layout.activity_pie_chart

    override fun getResultTitle(): String = intent.getStringExtra("TITLE") ?: "Loan Breakdown"

    override fun getShareText(): String {
        return "Loan Breakdown - Principal: ${formatCurrency(principalAmount)}, Interest: ${formatCurrency(interestAmount)}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        principalAmount = intent.getDoubleExtra("PRINCIPAL", 0.0)
        interestAmount = intent.getDoubleExtra("INTEREST", 0.0)

        val pieChart = findViewById<PieChart>(R.id.pieChartLarge)
        val txtPrincipal = findViewById<TextView>(R.id.txtPrincipalValue)
        val txtInterest = findViewById<TextView>(R.id.txtInterestValue)

        txtPrincipal.text = formatCurrency(principalAmount)
        txtInterest.text = formatCurrency(interestAmount)

        setupPieChart(pieChart, principalAmount, interestAmount)
    }

    private fun setupPieChart(pieChart: PieChart, principal: Double, interest: Double) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(principal.toFloat(), "Principal"))
        entries.add(PieEntry(interest.toFloat(), "Interest"))

        val dataSet = PieDataSet(entries, "")
        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#1565C0")) // Principal Blue
        colors.add(Color.parseColor("#E53935")) // Interest Red
        
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 16f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 45f
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.animateY(1200)
        pieChart.invalidate()
    }
}