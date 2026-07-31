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
import kotlin.math.pow

class CompoundInterestActivity : BaseInputActivity() {

    private lateinit var etPrincipalAmount: EditText
    private lateinit var etInterestRate: EditText
    private lateinit var etTenureYears: EditText

    private lateinit var chipCompMonthly: MaterialButton
    private lateinit var chipCompQuarterly: MaterialButton
    private lateinit var chipCompHalfYearly: MaterialButton
    private lateinit var chipCompAnnually: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var compoundingFrequency = 4 // 1: Monthly (12), 2: Quarterly (4), 3: Half-Yearly (2), 4: Annually (1)

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_compound_interest

    override fun getActivityTitle(): String = "Compound Interest Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPrincipalAmount = findViewById(R.id.etPrincipalAmount)
        etInterestRate = findViewById(R.id.etInterestRate)
        etTenureYears = findViewById(R.id.etTenureYears)

        chipCompMonthly = findViewById(R.id.chipCompMonthly)
        chipCompQuarterly = findViewById(R.id.chipCompQuarterly)
        chipCompHalfYearly = findViewById(R.id.chipCompHalfYearly)
        chipCompAnnually = findViewById(R.id.chipCompAnnually)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etPrincipalAmount)

        chipCompMonthly.setOnClickListener { compoundingFrequency = 1; highlightFreqChips() }
        chipCompQuarterly.setOnClickListener { compoundingFrequency = 2; highlightFreqChips() }
        chipCompHalfYearly.setOnClickListener { compoundingFrequency = 3; highlightFreqChips() }
        chipCompAnnually.setOnClickListener { compoundingFrequency = 4; highlightFreqChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateCompoundInterestAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etPrincipalAmount.setText("1,00,000")
        etInterestRate.setText("10.0")
        etTenureYears.setText("10")
        highlightFreqChips()
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

    private fun highlightFreqChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipCompMonthly, chipCompQuarterly, chipCompHalfYearly, chipCompAnnually)

        chips.forEachIndexed { index, chip ->
            if ((index + 1) == compoundingFrequency) {
                chip.backgroundTintList = ColorStateList.valueOf(primaryColor)
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
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

    private fun calculateCompoundInterestAndNavigate() {
        val principalStr = etPrincipalAmount.text.toString().replace(",", "")
        val rateStr = etInterestRate.text.toString()
        val tenureStr = etTenureYears.text.toString()

        val principal = principalStr.toDoubleOrNull() ?: 0.0
        val interestRatePct = rateStr.toDoubleOrNull() ?: 10.0
        val tenureYears = tenureStr.toIntOrNull() ?: 10

        if (principal <= 0 || tenureYears <= 0) {
            Toast.makeText(this, "Please enter valid principal amount and tenure", Toast.LENGTH_SHORT).show()
            return
        }

        val n = when (compoundingFrequency) {
            1 -> 12.0 // Monthly
            2 -> 4.0  // Quarterly
            3 -> 2.0  // Half-Yearly
            else -> 1.0 // Annually
        }

        val freqName = when (compoundingFrequency) {
            1 -> "Monthly"
            2 -> "Quarterly"
            3 -> "Half-Yearly"
            else -> "Annual"
        }

        val r = interestRatePct / 100.0
        val maturityAmount = principal * (1.0 + (r / n)).pow(n * tenureYears)
        val interestEarned = maturityAmount - principal

        val scheduleList = ArrayList<PaymentScheduleItem>()
        var currentBal = principal

        for (year in 1..tenureYears) {
            val endOfYearBal = principal * (1.0 + (r / n)).pow(n * year)
            val yrInterest = endOfYearBal - currentBal
            currentBal = endOfYearBal
            scheduleList.add(PaymentScheduleItem(year, "Year $year Maturity Value", endOfYearBal, yrInterest, 0.0, endOfYearBal))
        }

        val statusText = "Compounded $freqName @ $interestRatePct% p.a. over $tenureYears Years"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Compound Interest Result")
            putExtra("LOAN_AMOUNT", maturityAmount) // Maturity Total
            putExtra("INTEREST_RATE", interestRatePct.toFloat())
            putExtra("LOAN_TERM_YEARS", tenureYears)
            putExtra("LOAN_TERM_MONTHS", tenureYears * 12)
            putExtra("START_DATE", statusText)
            putExtra("EMI", interestEarned / (tenureYears * 12.0))
            putExtra("TOTAL_INTEREST", interestEarned)
            putExtra("TOTAL_COST", principal)
            putExtra("PAYOFF_DATE", "Maturity Corpus: ₹" + commaFormat.format(maturityAmount.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPrincipalAmount.setText("1,00,000")
        etInterestRate.setText("10.0")
        etTenureYears.setText("10")
        compoundingFrequency = 4
        highlightFreqChips()
    }
}
