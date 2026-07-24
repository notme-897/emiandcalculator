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

class SipCalculatorActivity : BaseInputActivity() {

    private lateinit var etMonthlySip: EditText
    private lateinit var etSipRate: EditText
    private lateinit var etSipYears: EditText
    private lateinit var btnCalculateSip: MaterialButton
    private lateinit var cardSipResult: MaterialCardView
    private lateinit var txtTotalInvested: TextView
    private lateinit var txtEstReturns: TextView
    private lateinit var txtMaturityValue: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_sip_calculator

    override fun getActivityTitle(): String = "SIP Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etMonthlySip = findViewById(R.id.etMonthlySip)
        etSipRate = findViewById(R.id.etSipRate)
        etSipYears = findViewById(R.id.etSipYears)
        btnCalculateSip = findViewById(R.id.btnCalculateSip)
        cardSipResult = findViewById(R.id.cardSipResult)
        txtTotalInvested = findViewById(R.id.txtTotalInvested)
        txtEstReturns = findViewById(R.id.txtEstReturns)
        txtMaturityValue = findViewById(R.id.txtMaturityValue)

        setupTouchScaleAnimation(btnCalculateSip)
        btnCalculateSip.setOnClickListener {
            calculateSip()
        }
    }

    private fun calculateSip() {
        val monthlySip = etMonthlySip.text.toString().toDoubleOrNull() ?: 0.0
        val expectedRate = etSipRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etSipYears.text.toString().toIntOrNull() ?: 0

        if (monthlySip <= 0 || expectedRate <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid monthly investment, rate, and years", Toast.LENGTH_SHORT).show()
            return
        }

        val totalMonths = years * 12
        val i = expectedRate / (12 * 100)

        // SIP Formula: M = P * ({[1 + i]^n - 1} / i) * (1 + i)
        val maturity = monthlySip * (((1 + i).pow(totalMonths.toDouble()) - 1) / i) * (1 + i)
        val totalInvested = monthlySip * totalMonths
        val estimatedReturns = maturity - totalInvested

        animateNumberCounter(txtTotalInvested, totalInvested)
        animateNumberCounter(txtEstReturns, estimatedReturns)
        animateNumberCounter(txtMaturityValue, maturity)

        if (cardSipResult.visibility != View.VISIBLE) {
            cardSipResult.alpha = 0f
            cardSipResult.translationY = 40f
            cardSipResult.visibility = View.VISIBLE
            cardSipResult.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
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
}

