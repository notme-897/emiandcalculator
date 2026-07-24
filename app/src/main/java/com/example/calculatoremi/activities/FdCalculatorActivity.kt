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

class FdCalculatorActivity : BaseInputActivity() {

    private lateinit var etFdAmount: EditText
    private lateinit var etFdRate: EditText
    private lateinit var etFdYears: EditText
    private lateinit var btnCalculateFd: MaterialButton
    private lateinit var cardFdResult: MaterialCardView
    private lateinit var txtFdPrincipal: TextView
    private lateinit var txtFdInterest: TextView
    private lateinit var txtFdMaturity: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_fd_calculator

    override fun getActivityTitle(): String = "FD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etFdAmount = findViewById(R.id.etFdAmount)
        etFdRate = findViewById(R.id.etFdRate)
        etFdYears = findViewById(R.id.etFdYears)
        btnCalculateFd = findViewById(R.id.btnCalculateFd)
        cardFdResult = findViewById(R.id.cardFdResult)
        txtFdPrincipal = findViewById(R.id.txtFdPrincipal)
        txtFdInterest = findViewById(R.id.txtFdInterest)
        txtFdMaturity = findViewById(R.id.txtFdMaturity)

        setupTouchScaleAnimation(btnCalculateFd)
        btnCalculateFd.setOnClickListener {
            calculateFd()
        }
    }

    private fun calculateFd() {
        val amount = etFdAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etFdRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etFdYears.text.toString().toIntOrNull() ?: 0

        if (amount <= 0 || rate <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid deposit amount, rate, and years", Toast.LENGTH_SHORT).show()
            return
        }

        // FD Compound Interest Formula (Quarterly compounding n=4)
        val n = 4.0
        val r = rate / 100.0
        val maturity = amount * (1 + r / n).pow(n * years)
        val interest = maturity - amount

        animateNumberCounter(txtFdPrincipal, amount)
        animateNumberCounter(txtFdInterest, interest)
        animateNumberCounter(txtFdMaturity, maturity)

        if (cardFdResult.visibility != View.VISIBLE) {
            cardFdResult.alpha = 0f
            cardFdResult.translationY = 40f
            cardFdResult.visibility = View.VISIBLE
            cardFdResult.animate()
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

