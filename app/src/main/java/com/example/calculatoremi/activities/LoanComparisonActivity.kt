package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private lateinit var btnReset: MaterialButton
    private lateinit var cardComparisonResult: MaterialCardView

    private lateinit var txtWinnerText: TextView
    private lateinit var txtEmiA: TextView
    private lateinit var txtEmiB: TextView
    private lateinit var txtInterestA: TextView
    private lateinit var txtInterestB: TextView
    private lateinit var txtCostA: TextView
    private lateinit var txtCostB: TextView

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormattingA = false
    private var isFormattingB = false

    override fun getLayoutResId(): Int = R.layout.activity_loan_comparison

    override fun getActivityTitle(): String = "Compare Loans"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etAmountA = findViewById(R.id.etAmountA)
        etRateA = findViewById(R.id.etRateA)
        etMonthsA = findViewById(R.id.etMonthsA)

        etAmountB = findViewById(R.id.etAmountB)
        etRateB = findViewById(R.id.etRateB)
        etMonthsB = findViewById(R.id.etMonthsB)

        btnCompare = findViewById(R.id.btnCompare)
        btnReset = findViewById(R.id.btnReset)
        cardComparisonResult = findViewById(R.id.cardComparisonResult)

        txtWinnerText = findViewById(R.id.txtWinnerText)
        txtEmiA = findViewById(R.id.txtEmiA)
        txtEmiB = findViewById(R.id.txtEmiB)
        txtInterestA = findViewById(R.id.txtInterestA)
        txtInterestB = findViewById(R.id.txtInterestB)
        txtCostA = findViewById(R.id.txtCostA)
        txtCostB = findViewById(R.id.txtCostB)

        setupCommaFormatting(etAmountA, isA = true)
        setupCommaFormatting(etAmountB, isA = false)

        setupButtonAnimation(btnCompare)
        setupButtonAnimation(btnReset)

        btnCompare.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            compareLoans()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etAmountA.setText("5,00,000")
        etRateA.setText("10.5")
        etMonthsA.setText("60")

        etAmountB.setText("5,00,000")
        etRateB.setText("9.5")
        etMonthsB.setText("60")
    }

    private fun resetFields() {
        etAmountA.setText("5,00,000")
        etRateA.setText("10.5")
        etMonthsA.setText("60")

        etAmountB.setText("5,00,000")
        etRateB.setText("9.5")
        etMonthsB.setText("60")
        cardComparisonResult.visibility = View.GONE
    }

    private fun setupCommaFormatting(editText: EditText, isA: Boolean) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isA && isFormattingA) return
                if (!isA && isFormattingB) return

                if (isA) isFormattingA = true else isFormattingB = true
                try {
                    val rawString = s.toString().replace(",", "")
                    if (rawString.isNotEmpty()) {
                        val doubleVal = rawString.toDoubleOrNull()
                        if (doubleVal != null && doubleVal > 0) {
                            val formatted = commaFormat.format(doubleVal)
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
                if (isA) isFormattingA = false else isFormattingB = false
            }
        })
    }

    private fun setupButtonAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(OvershootInterpolator(2.2f))
                        .setDuration(160)
                        .start()
                }
            }
            false
        }
    }

    private fun compareLoans() {
        val amountAStr = etAmountA.text.toString().replace(",", "")
        val rateAStr = etRateA.text.toString()
        val monthsAStr = etMonthsA.text.toString()

        val amountBStr = etAmountB.text.toString().replace(",", "")
        val rateBStr = etRateB.text.toString()
        val monthsBStr = etMonthsB.text.toString()

        val amountA = amountAStr.toDoubleOrNull() ?: 0.0
        val rateA = rateAStr.toDoubleOrNull() ?: 0.0
        val monthsA = monthsAStr.toIntOrNull() ?: 0

        val amountB = amountBStr.toDoubleOrNull() ?: 0.0
        val rateB = rateBStr.toDoubleOrNull() ?: 0.0
        val monthsB = monthsBStr.toIntOrNull() ?: 0

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
            txtWinnerText.text = "🎉 Loan Option A saves ₹${commaFormat.format(diffInterest.toInt())} in total interest!"
        } else if (interestB < interestA) {
            txtWinnerText.text = "🎉 Loan Option B saves ₹${commaFormat.format(diffInterest.toInt())} in total interest!"
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
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 650
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            val currentValue = targetValue * fraction
            val formatter = DecimalFormat("#,##,##0")
            textView.text = "₹" + formatter.format(currentValue)
        }
        animator.start()
    }
}
