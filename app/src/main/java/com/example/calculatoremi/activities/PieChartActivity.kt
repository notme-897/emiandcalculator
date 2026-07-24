package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.example.calculatoremi.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.DecimalFormat
import java.util.*

class PieChartActivity : BaseResultActivity() {

    private var principalAmount: Double = 0.0
    private var interestAmount: Double = 0.0
    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getResultLayoutResId(): Int = R.layout.activity_pie_chart

    override fun getResultTitle(): String = intent.getStringExtra("TITLE") ?: "Loan Breakdown"

    override fun getShareText(): String {
        return "Loan Breakdown - Principal: ${formatCurrency(principalAmount)}, Interest: ${formatCurrency(interestAmount)}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        principalAmount = intent.getDoubleExtra("PRINCIPAL", 0.0)
        interestAmount = intent.getDoubleExtra("INTEREST", 0.0)

        val pieChart = findViewById<PieChart>(R.id.pieChartLarge)
        val txtPrincipal = findViewById<TextView>(R.id.txtPrincipalValue)
        val txtInterest = findViewById<TextView>(R.id.txtInterestValue)

        animateNumberCounter(txtPrincipal, principalAmount)
        animateNumberCounter(txtInterest, interestAmount)

        setupPieChart(pieChart, principalAmount, interestAmount)
        animateCardsEntrance()
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 750
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            textView.text = "₹" + decimalFormat.format(currentValue.toDouble())
        }
        animator.start()
    }

    private fun animateCardsEntrance() {
        val views = listOfNotNull(
            findViewById<View>(R.id.pieChartLarge),
            findViewById<View>(R.id.txtPrincipalValue),
            findViewById<View>(R.id.txtInterestValue)
        )
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 30f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 90).toLong())
                .setDuration(380)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
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
        pieChart.animateY(1100, Easing.EaseInOutCubic)
        pieChart.invalidate()
    }
}