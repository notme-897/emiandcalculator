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

class RdCalculatorActivity : BaseInputActivity() {

    private lateinit var etRdAmount: EditText
    private lateinit var etRdRate: EditText
    private lateinit var etRdMonths: EditText
    private lateinit var btnCalculateRd: MaterialButton
    private lateinit var cardRdResult: MaterialCardView
    private lateinit var txtRdInvested: TextView
    private lateinit var txtRdInterest: TextView
    private lateinit var txtRdMaturity: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_rd_calculator

    override fun getActivityTitle(): String = "RD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etRdAmount = findViewById(R.id.etRdAmount)
        etRdRate = findViewById(R.id.etRdRate)
        etRdMonths = findViewById(R.id.etRdMonths)
        btnCalculateRd = findViewById(R.id.btnCalculateRd)
        cardRdResult = findViewById(R.id.cardRdResult)
        txtRdInvested = findViewById(R.id.txtRdInvested)
        txtRdInterest = findViewById(R.id.txtRdInterest)
        txtRdMaturity = findViewById(R.id.txtRdMaturity)

        setupTouchScaleAnimation(btnCalculateRd)
        btnCalculateRd.setOnClickListener {
            calculateRd()
        }
    }

    private fun calculateRd() {
        val amount = etRdAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRdRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = etRdMonths.text.toString().toIntOrNull() ?: 0

        if (amount <= 0 || rate <= 0 || months <= 0) {
            Toast.makeText(this, "Please enter valid monthly deposit, rate, and months", Toast.LENGTH_SHORT).show()
            return
        }

        // RD Standard Formula: I = P * N(N+1)/2 * (R/1200)
        val interest = amount * (months * (months + 1) / 2.0) * (rate / 1200.0)
        val totalInvested = amount * months
        val maturity = totalInvested + interest

        animateNumberCounter(txtRdInvested, totalInvested)
        animateNumberCounter(txtRdInterest, interest)
        animateNumberCounter(txtRdMaturity, maturity)

        if (cardRdResult.visibility != View.VISIBLE) {
            cardRdResult.alpha = 0f
            cardRdResult.translationY = 40f
            cardRdResult.visibility = View.VISIBLE
            cardRdResult.animate()
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

