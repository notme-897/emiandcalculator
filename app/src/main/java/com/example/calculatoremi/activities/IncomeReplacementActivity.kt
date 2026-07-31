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

class IncomeReplacementActivity : BaseInputActivity() {

    private lateinit var etMonthlyIncome: EditText
    private lateinit var etSupportYears: EditText
    private lateinit var etInflationRate: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_income_replacement

    override fun getActivityTitle(): String = "Income Replacement Cover"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etMonthlyIncome = findViewById(R.id.etMonthlyIncome)
        etSupportYears = findViewById(R.id.etSupportYears)
        etInflationRate = findViewById(R.id.etInflationRate)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etMonthlyIncome)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateReplacementCorpusAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etMonthlyIncome.setText("1,00,000")
        etSupportYears.setText("20")
        etInflationRate.setText("6.0")
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

    private fun calculateReplacementCorpusAndNavigate() {
        val monthlyIncomeStr = etMonthlyIncome.text.toString().replace(",", "")
        val supportYearsStr = etSupportYears.text.toString()
        val inflationStr = etInflationRate.text.toString()

        val monthlyIncome = monthlyIncomeStr.toDoubleOrNull() ?: 0.0
        val supportYears = supportYearsStr.toIntOrNull() ?: 20
        val inflationRatePct = inflationStr.toDoubleOrNull() ?: 6.0

        if (monthlyIncome <= 0) {
            Toast.makeText(this, "Please enter a valid monthly income", Toast.LENGTH_SHORT).show()
            return
        }

        val annualIncomeNeeded = monthlyIncome * 12.0
        val inflation = inflationRatePct / 100.0
        val returnRate = 0.08 // Expected conservative portfolio return (8%)
        val realRate = max(0.001, (1.0 + returnRate) / (1.0 + inflation) - 1.0)

        val pvFactor = (1.0 - (1.0 + realRate).pow(-supportYears.toDouble())) / realRate
        val requiredLumpSumCorpus = annualIncomeNeeded * pvFactor
        val totalNominalPayout = annualIncomeNeeded * supportYears

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Target Monthly Income", monthlyIncome, monthlyIncome, 0.0, monthlyIncome))
        scheduleList.add(PaymentScheduleItem(2, "Annual Family Paycheck", annualIncomeNeeded, monthlyIncome, 0.0, annualIncomeNeeded))
        scheduleList.add(PaymentScheduleItem(3, "Nominal Total Payout ($supportYears yrs)", totalNominalPayout, annualIncomeNeeded, 0.0, totalNominalPayout))
        scheduleList.add(PaymentScheduleItem(4, "Required Inflation-Adjusted Corpus", requiredLumpSumCorpus, requiredLumpSumCorpus / (supportYears * 12.0), 0.0, requiredLumpSumCorpus))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Income Replacement Corpus Result")
            putExtra("LOAN_AMOUNT", requiredLumpSumCorpus) // Required Corpus
            putExtra("INTEREST_RATE", inflationRatePct.toFloat())
            putExtra("LOAN_TERM_YEARS", supportYears)
            putExtra("LOAN_TERM_MONTHS", supportYears * 12)
            putExtra("START_DATE", "Support Duration: $supportYears Years @ $inflationRatePct% Inflation")
            putExtra("EMI", monthlyIncome)
            putExtra("TOTAL_INTEREST", totalNominalPayout)
            putExtra("TOTAL_COST", requiredLumpSumCorpus)
            putExtra("PAYOFF_DATE", "Lump-Sum Needed: ₹" + commaFormat.format(requiredLumpSumCorpus.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etMonthlyIncome.setText("1,00,000")
        etSupportYears.setText("20")
        etInflationRate.setText("6.0")
    }
}
