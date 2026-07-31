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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.pow

class SwpCalculatorActivity : BaseInputActivity() {

    private lateinit var etInvestmentCorpus: EditText
    private lateinit var etMonthlyWithdrawal: EditText
    private lateinit var etExpectedReturn: EditText
    private lateinit var etTenureYears: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_swp_calculator

    override fun getActivityTitle(): String = "SWP Retirement Cashflow Engine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etInvestmentCorpus = findViewById(R.id.etInvestmentCorpus)
        etMonthlyWithdrawal = findViewById(R.id.etMonthlyWithdrawal)
        etExpectedReturn = findViewById(R.id.etExpectedReturn)
        etTenureYears = findViewById(R.id.etTenureYears)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etInvestmentCorpus)
        setupCommaFormatting(etMonthlyWithdrawal)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            simulateSwpAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etInvestmentCorpus.setText("50,00,000")
        etMonthlyWithdrawal.setText("35,000")
        etExpectedReturn.setText("8.5")
        etTenureYears.setText("15")
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
                } catch (e: Exception) {
                    // Ignore
                }
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

    private fun simulateSwpAndNavigate() {
        val corpusStr = etInvestmentCorpus.text.toString().replace(",", "")
        val withdrawalStr = etMonthlyWithdrawal.text.toString().replace(",", "")
        val rateStr = etExpectedReturn.text.toString()
        val yearsStr = etTenureYears.text.toString()

        val initialCorpus = corpusStr.toDoubleOrNull() ?: 0.0
        val monthlyWithdrawal = withdrawalStr.toDoubleOrNull() ?: 0.0
        val annualRate = rateStr.toDoubleOrNull() ?: 8.5
        val years = yearsStr.toIntOrNull() ?: 15

        if (initialCorpus <= 0 || monthlyWithdrawal <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid corpus, withdrawal and tenure", Toast.LENGTH_SHORT).show()
            return
        }

        val totalMonths = years * 12
        val rMonthly = (annualRate / 100.0) / 12.0

        var balance = initialCorpus
        var totalWithdrawn = 0.0
        var totalInterestEarned = 0.0
        var zeroMonth = -1

        val scheduleList = ArrayList<PaymentScheduleItem>()

        for (m in 1..totalMonths) {
            val monthlyInterest = balance * rMonthly
            balance += monthlyInterest
            totalInterestEarned += monthlyInterest

            val actualWithdrawal = if (balance >= monthlyWithdrawal) monthlyWithdrawal else balance
            balance -= actualWithdrawal
            totalWithdrawn += actualWithdrawal

            if (m % 12 == 0 || m == totalMonths || balance <= 0) {
                val yearNum = (m + 11) / 12
                scheduleList.add(
                    PaymentScheduleItem(
                        yearNum,
                        "Year $yearNum Ending Balance",
                        actualWithdrawal,
                        monthlyInterest,
                        totalWithdrawn,
                        max(0.0, balance)
                    )
                )
            }

            if (balance <= 0 && zeroMonth == -1) {
                zeroMonth = m
                break
            }
        }

        val statusText = if (zeroMonth != -1) {
            "⚠️ Corpus Depleted in Year ${zeroMonth / 12} Month ${zeroMonth % 12}!"
        } else {
            "🎉 Sustainable SWP! Final Balance: ₹${commaFormat.format(max(0.0, balance).toInt())}"
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "SWP Retirement Cashflow Result")
            putExtra("LOAN_AMOUNT", totalWithdrawn) // Total Withdrawn
            putExtra("INTEREST_RATE", annualRate.toFloat())
            putExtra("LOAN_TERM_YEARS", years)
            putExtra("LOAN_TERM_MONTHS", totalMonths)
            putExtra("START_DATE", statusText)
            putExtra("EMI", monthlyWithdrawal)
            putExtra("TOTAL_INTEREST", totalInterestEarned)
            putExtra("TOTAL_COST", initialCorpus)
            putExtra("PAYOFF_DATE", "Remaining Balance: ₹" + commaFormat.format(max(0.0, balance).toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etInvestmentCorpus.setText("50,00,000")
        etMonthlyWithdrawal.setText("35,000")
        etExpectedReturn.setText("8.5")
        etTenureYears.setText("15")
    }
}
