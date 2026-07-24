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
import kotlin.math.abs
import kotlin.math.pow

class LoanComparisonActivity : BaseInputActivity() {

    private lateinit var etAmountA: EditText
    private lateinit var etRateA: EditText
    private lateinit var etMonthsA: EditText

    private lateinit var etAmountB: EditText
    private lateinit var etRateB: EditText
    private lateinit var etMonthsB: EditText

    private lateinit var btnCompare: MaterialButton
    private lateinit var cardComparisonResult: MaterialCardView

    private lateinit var txtWinnerText: TextView
    private lateinit var txtEmiA: TextView
    private lateinit var txtEmiB: TextView
    private lateinit var txtInterestA: TextView
    private lateinit var txtInterestB: TextView
    private lateinit var txtCostA: TextView
    private lateinit var txtCostB: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_loan_comparison

    override fun getActivityTitle(): String = "Compare Loans"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etAmountA = findViewById(R.id.etAmountA)
        etRateA = findViewById(R.id.etRateA)
        etMonthsA = findViewById(R.id.etMonthsA)

        etAmountB = findViewById(R.id.etAmountB)
        etRateB = findViewById(R.id.etRateB)
        etMonthsB = findViewById(R.id.etMonthsB)

        btnCompare = findViewById(R.id.btnCompare)
        cardComparisonResult = findViewById(R.id.cardComparisonResult)

        txtWinnerText = findViewById(R.id.txtWinnerText)
        txtEmiA = findViewById(R.id.txtEmiA)
        txtEmiB = findViewById(R.id.txtEmiB)
        txtInterestA = findViewById(R.id.txtInterestA)
        txtInterestB = findViewById(R.id.txtInterestB)
        txtCostA = findViewById(R.id.txtCostA)
        txtCostB = findViewById(R.id.txtCostB)

        // Pre-fill Loan A if passed in Intent
        val loanAAmount = intent.getDoubleExtra("LOAN_A_AMOUNT", 0.0)
        val loanARate = intent.getDoubleExtra("LOAN_A_RATE", 0.0)
        val loanAMonths = intent.getIntExtra("LOAN_A_MONTHS", 0)

        if (loanAAmount > 0) etAmountA.setText(loanAAmount.toInt().toString())
        if (loanARate > 0) etRateA.setText(loanARate.toString())
        if (loanAMonths > 0) etMonthsA.setText(loanAMonths.toString())

        setupTouchScaleAnimation(btnCompare)
        btnCompare.setOnClickListener {
            compareLoans()
        }
    }

    private fun compareLoans() {
        val amountA = etAmountA.text.toString().toDoubleOrNull() ?: 0.0
        val rateA = etRateA.text.toString().toDoubleOrNull() ?: 0.0
        val monthsA = etMonthsA.text.toString().toIntOrNull() ?: 0

        val amountB = etAmountB.text.toString().toDoubleOrNull() ?: 0.0
        val rateB = etRateB.text.toString().toDoubleOrNull() ?: 0.0
        val monthsB = etMonthsB.text.toString().toIntOrNull() ?: 0

        if (amountA <= 0 || rateA <= 0 || monthsA <= 0 || amountB <= 0 || rateB <= 0 || monthsB <= 0) {
            Toast.makeText(this, "Please fill all fields for both Loan A and Loan B", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate Loan A
        val rA = rateA / (12 * 100)
        val emiA = (amountA * rA * (1 + rA).pow(monthsA.toDouble())) / ((1 + rA).pow(monthsA.toDouble()) - 1)
        val costA = emiA * monthsA
        val interestA = costA - amountA

        // Calculate Loan B
        val rB = rateB / (12 * 100)
        val emiB = (amountB * rB * (1 + rB).pow(monthsB.toDouble())) / ((1 + rB).pow(monthsB.toDouble()) - 1)
        val costB = emiB * monthsB
        val interestB = costB - amountB

        animateNumberCounter(txtEmiA, emiA)
        animateNumberCounter(txtEmiB, emiB)
        animateNumberCounter(txtInterestA, interestA)
        animateNumberCounter(txtInterestB, interestB)
        animateNumberCounter(txtCostA, costA)
        animateNumberCounter(txtCostB, costB)

        val diffInterest = abs(interestA - interestB)

        if (interestA < interestB) {
            txtWinnerText.text = "🎉 Loan Option A saves ${formatCurrency(diffInterest)} in total interest!"
        } else if (interestB < interestA) {
            txtWinnerText.text = "🎉 Loan Option B saves ${formatCurrency(diffInterest)} in total interest!"
        } else {
            txtWinnerText.text = "Both Loan Options have identical interest cost!"
        }

        if (cardComparisonResult.visibility != View.VISIBLE) {
            cardComparisonResult.alpha = 0f
            cardComparisonResult.translationY = 40f
            cardComparisonResult.visibility = View.VISIBLE
            cardComparisonResult.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 650
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            textView.text = "₹" + decimalFormat.format(currentValue.toDouble())
        }
        animator.start()
    }
}

