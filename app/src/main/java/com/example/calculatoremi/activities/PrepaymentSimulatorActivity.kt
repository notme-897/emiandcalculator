package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat
import kotlin.math.pow

class PrepaymentSimulatorActivity : BaseInputActivity() {

    private lateinit var etBalance: EditText
    private lateinit var etRate: EditText
    private lateinit var etMonths: EditText
    private lateinit var etExtraPay: EditText
    private lateinit var btnSimulate: MaterialButton
    private lateinit var cardResult: MaterialCardView
    private lateinit var txtInterestSaved: TextView
    private lateinit var txtTimeSaved: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_prepayment_simulator

    override fun getActivityTitle(): String = "Prepayment Simulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etBalance = findViewById(R.id.etBalance)
        etRate = findViewById(R.id.etRate)
        etMonths = findViewById(R.id.etMonths)
        etExtraPay = findViewById(R.id.etExtraPay)
        btnSimulate = findViewById(R.id.btnSimulate)
        cardResult = findViewById(R.id.cardResult)
        txtInterestSaved = findViewById(R.id.txtInterestSaved)
        txtTimeSaved = findViewById(R.id.txtTimeSaved)

        setupTouchScaleAnimation(btnSimulate)
        btnSimulate.setOnClickListener {
            runSimulation()
        }
    }

    private fun runSimulation() {
        val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = etMonths.text.toString().toIntOrNull() ?: 0
        val extraPay = etExtraPay.text.toString().toDoubleOrNull() ?: 0.0

        if (balance <= 0 || rate <= 0 || months <= 0) {
            Toast.makeText(this, "Please enter valid balance, rate, and months", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = rate / (12 * 100)
        val originalEmi = (balance * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) /
                ((1 + monthlyRate).pow(months.toDouble()) - 1)
        val originalTotalInterest = (originalEmi * months) - balance

        val newMonthlyPayment = originalEmi + extraPay

        // Simulate new payoff
        var remBalance = balance
        var newTotalInterest = 0.0
        var newMonthsCount = 0

        while (remBalance > 0 && newMonthsCount < months * 2) {
            val monthInterest = remBalance * monthlyRate
            val monthPrincipal = newMonthlyPayment - monthInterest
            remBalance -= monthPrincipal
            newTotalInterest += monthInterest
            newMonthsCount++
            if (remBalance < 0) break
        }

        val interestSaved = originalTotalInterest - newTotalInterest
        val monthsSaved = months - newMonthsCount

        val savedAmount = if (interestSaved > 0) interestSaved else 0.0
        val savedMonths = if (monthsSaved > 0) monthsSaved else 0

        animateInterestCounter(savedAmount)
        txtTimeSaved.text = "Tenure Reduced By: $savedMonths months (${formatTerm(savedMonths)})"

        if (cardResult.visibility != View.VISIBLE) {
            cardResult.alpha = 0f
            cardResult.translationY = 40f
            cardResult.visibility = View.VISIBLE
            cardResult.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    private fun animateInterestCounter(targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 750
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            txtInterestSaved.text = "Total Interest Saved: ₹" + decimalFormat.format(currentValue.toDouble())
        }
        animator.start()
    }
}

