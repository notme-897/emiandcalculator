package com.example.calculatoremi.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoremi.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*

class PieChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pie_chart)

        // Applying insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val principal = intent.getDoubleExtra("PRINCIPAL", 0.0)
        val interest = intent.getDoubleExtra("INTEREST", 0.0)
        val title = intent.getStringExtra("TITLE") ?: "Loan Breakdown"

        findViewById<TextView>(R.id.txtChartTitle).text = title
        findViewById<ImageView>(R.id.btnBackChart).setOnClickListener { finish() }

        val pieChart = findViewById<PieChart>(R.id.pieChartLarge)
        val txtPrincipal = findViewById<TextView>(R.id.txtPrincipalValue)
        val txtInterest = findViewById<TextView>(R.id.txtInterestValue)

        txtPrincipal.text = String.format(Locale.US, "%,.2f $", principal)
        txtInterest.text = String.format(Locale.US, "%,.2f $", interest)

        setupPieChart(pieChart, principal, interest)
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