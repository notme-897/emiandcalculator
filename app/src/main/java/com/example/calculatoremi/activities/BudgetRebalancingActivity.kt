package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.max

class BudgetRebalancingActivity : BaseInputActivity() {

    private lateinit var etNetIncome: EditText
    private lateinit var etActualNeeds: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_budget_rebalancing

    override fun getActivityTitle(): String = "Budget Rebalancing Engine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etNetIncome = findViewById(R.id.etNetIncome)
        etActualNeeds = findViewById(R.id.etActualNeeds)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etNetIncome)
        setupCommaFormatting(etActualNeeds)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            rebalanceBudgetAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etNetIncome.setText("1,00,000")
        etActualNeeds.setText("62,000")
    }

    private fun setupCommaFormatting(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
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
                } catch (e: Exception) {}
                isFormatting = false
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

    private fun rebalanceBudgetAndNavigate() {
        val netIncome = etNetIncome.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val actualNeeds = etActualNeeds.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (netIncome <= 0) {
            Toast.makeText(this, "Please enter a valid net monthly income", Toast.LENGTH_SHORT).show()
            return
        }

        val targetNeeds = netIncome * 0.50
        val targetWants = netIncome * 0.30
        val targetSavings = netIncome * 0.20

        val excessNeeds = max(0.0, actualNeeds - targetNeeds)
        val rebalancedWants = max(0.0, targetWants - excessNeeds)
        val remainingDeficit = max(0.0, excessNeeds - targetWants)
        val rebalancedSavings = max(0.0, targetSavings - remainingDeficit)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Net Monthly Income", netIncome, netIncome / 12.0, 0.0, netIncome))
        scheduleList.add(PaymentScheduleItem(2, "Essential Needs (50% Target)", actualNeeds, actualNeeds / 12.0, (actualNeeds / netIncome) * 100, actualNeeds))
        scheduleList.add(PaymentScheduleItem(3, "Rebalanced Wants Cap (30%)", rebalancedWants, rebalancedWants / 12.0, (rebalancedWants / netIncome) * 100, rebalancedWants))
        scheduleList.add(PaymentScheduleItem(4, "Protected Savings Goal (20%)", rebalancedSavings, rebalancedSavings / 12.0, (rebalancedSavings / netIncome) * 100, rebalancedSavings))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "50/30/20 Rebalanced Budget Result")
            putExtra("LOAN_AMOUNT", rebalancedSavings)
            putExtra("INTEREST_RATE", 20.0f)
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Needs: ₹${commaFormat.format(actualNeeds.toInt())} | Wants Cap: ₹${commaFormat.format(rebalancedWants.toInt())}")
            putExtra("EMI", rebalancedSavings)
            putExtra("TOTAL_INTEREST", rebalancedWants)
            putExtra("TOTAL_COST", netIncome)
            putExtra("PAYOFF_DATE", "Monthly Savings: ₹" + commaFormat.format(rebalancedSavings.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etNetIncome.setText("1,00,000")
        etActualNeeds.setText("62,000")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
