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
import androidx.core.widget.NestedScrollView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.example.calculatoremi.views.LoanTenureSelectorView
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class FdCalculatorActivity : BaseInputActivity() {

    private lateinit var etFdAmount: EditText
    private lateinit var chipFd50k: MaterialButton
    private lateinit var chipFd1L: MaterialButton
    private lateinit var chipFd2L: MaterialButton
    private lateinit var chipFd5L: MaterialButton
    private lateinit var chipFd10L: MaterialButton

    private lateinit var etFdRate: EditText
    private lateinit var chipFdRate6_5: MaterialButton
    private lateinit var chipFdRate7: MaterialButton
    private lateinit var chipFdRate7_5: MaterialButton
    private lateinit var chipFdRate8: MaterialButton
    private lateinit var btnSeniorCitizenToggle: MaterialButton

    private lateinit var chipFreqQuarterly: MaterialButton
    private lateinit var chipFreqMonthly: MaterialButton
    private lateinit var chipFreqHalfYearly: MaterialButton
    private lateinit var chipFreqYearly: MaterialButton

    private lateinit var tenureSelector: LoanTenureSelectorView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isSeniorCitizen = false
    private var compoundingFrequencyPerYear = 4 // Default Quarterly (4)

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_fd_calculator

    override fun getActivityTitle(): String = "FD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etFdAmount = findViewById(R.id.etFdAmount)
        chipFd50k = findViewById(R.id.chipFd50k)
        chipFd1L = findViewById(R.id.chipFd1L)
        chipFd2L = findViewById(R.id.chipFd2L)
        chipFd5L = findViewById(R.id.chipFd5L)
        chipFd10L = findViewById(R.id.chipFd10L)

        etFdRate = findViewById(R.id.etFdRate)
        chipFdRate6_5 = findViewById(R.id.chipFdRate6_5)
        chipFdRate7 = findViewById(R.id.chipFdRate7)
        chipFdRate7_5 = findViewById(R.id.chipFdRate7_5)
        chipFdRate8 = findViewById(R.id.chipFdRate8)
        btnSeniorCitizenToggle = findViewById(R.id.btnSeniorCitizenToggle)

        chipFreqQuarterly = findViewById(R.id.chipFreqQuarterly)
        chipFreqMonthly = findViewById(R.id.chipFreqMonthly)
        chipFreqHalfYearly = findViewById(R.id.chipFreqHalfYearly)
        chipFreqYearly = findViewById(R.id.chipFreqYearly)

        tenureSelector = findViewById(R.id.tenureSelector)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val allChips = listOf(
            chipFd50k, chipFd1L, chipFd2L, chipFd5L, chipFd10L,
            chipFdRate6_5, chipFdRate7, chipFdRate7_5, chipFdRate8,
            chipFreqQuarterly, chipFreqMonthly, chipFreqHalfYearly, chipFreqYearly
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etFdAmount) { updateFdSummary() }

        chipFd50k.setOnClickListener { setQuickAmount(50000.0); highlightAmountChip(chipFd50k) }
        chipFd1L.setOnClickListener { setQuickAmount(100000.0); highlightAmountChip(chipFd1L) }
        chipFd2L.setOnClickListener { setQuickAmount(200000.0); highlightAmountChip(chipFd2L) }
        chipFd5L.setOnClickListener { setQuickAmount(500000.0); highlightAmountChip(chipFd5L) }
        chipFd10L.setOnClickListener { setQuickAmount(1000000.0); highlightAmountChip(chipFd10L) }

        etFdRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateFdSummary()
            }
        })

        chipFdRate6_5.setOnClickListener { etFdRate.setText("6.5"); highlightRateChip(chipFdRate6_5) }
        chipFdRate7.setOnClickListener { etFdRate.setText("7.0"); highlightRateChip(chipFdRate7) }
        chipFdRate7_5.setOnClickListener { etFdRate.setText("7.5"); highlightRateChip(chipFdRate7_5) }
        chipFdRate8.setOnClickListener { etFdRate.setText("8.0"); highlightRateChip(chipFdRate8) }

        // Senior Citizen toggle
        btnSeniorCitizenToggle.setOnClickListener {
            isSeniorCitizen = !isSeniorCitizen
            updateSeniorCitizenUI()
            updateFdSummary()
        }

        // Compounding frequency options
        chipFreqQuarterly.setOnClickListener { compoundingFrequencyPerYear = 4; highlightFreqChip(chipFreqQuarterly); updateFdSummary() }
        chipFreqMonthly.setOnClickListener { compoundingFrequencyPerYear = 12; highlightFreqChip(chipFreqMonthly); updateFdSummary() }
        chipFreqHalfYearly.setOnClickListener { compoundingFrequencyPerYear = 2; highlightFreqChip(chipFreqHalfYearly); updateFdSummary() }
        chipFreqYearly.setOnClickListener { compoundingFrequencyPerYear = 1; highlightFreqChip(chipFreqYearly); updateFdSummary() }

        tenureSelector.setOnTenureChangedListener {
            updateFdSummary()
        }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        etFdAmount.setText("2,00,000")
        etFdRate.setText("7.0")
        tenureSelector.tenureMonths = 60 // 5 Years default

        highlightAmountChip(chipFd2L)
        highlightRateChip(chipFdRate7)
        highlightFreqChip(chipFreqQuarterly)
        updateFdSummary()
    }

    private fun setupCommaFormatting(editText: EditText, onFormatted: (() -> Unit)? = null) {
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
                onFormatted?.invoke()
                updateFdSummary()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etFdAmount.setText(commaFormat.format(amount))
        updateFdSummary()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateSeniorCitizenUI() {
        if (isSeniorCitizen) {
            btnSeniorCitizenToggle.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
            btnSeniorCitizenToggle.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btnSeniorCitizenToggle.strokeWidth = 0
        } else {
            btnSeniorCitizenToggle.setBackgroundColor(Color.parseColor("#F8FAFC"))
            btnSeniorCitizenToggle.setTextColor(Color.parseColor("#1E293B"))
            btnSeniorCitizenToggle.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            btnSeniorCitizenToggle.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun getEffectiveRate(): Double {
        val baseRate = etFdRate.text.toString().toDoubleOrNull() ?: 0.0
        return if (isSeniorCitizen) baseRate + 0.50 else baseRate
    }

    private fun updateFdSummary() {
        // Summary card removed — no-op
    }

    private fun setupChipTouchAnimation(button: MaterialButton) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(60).start()
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

    private fun highlightAmountChip(selectedChip: MaterialButton) {
        val chips = listOf(chipFd50k, chipFd1L, chipFd2L, chipFd5L, chipFd10L)
        chips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun highlightRateChip(selectedChip: MaterialButton?) {
        val rateChips = listOf(chipFdRate6_5, chipFdRate7, chipFdRate7_5, chipFdRate8)
        rateChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun updateRateChipHighlights(rate: Double?) {
        when (rate) {
            6.5 -> highlightRateChip(chipFdRate6_5)
            7.0 -> highlightRateChip(chipFdRate7)
            7.5 -> highlightRateChip(chipFdRate7_5)
            8.0 -> highlightRateChip(chipFdRate8)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightFreqChip(selectedChip: MaterialButton) {
        val freqChips = listOf(chipFreqQuarterly, chipFreqMonthly, chipFreqHalfYearly, chipFreqYearly)
        freqChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
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
                    view.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(150).start()
                }
            }
            false
        }
    }

    private fun calculateAndNavigate() {
        val principal = getRawValue(etFdAmount)
        val effectiveRate = getEffectiveRate()
        val totalMonths = tenureSelector.tenureMonths.coerceAtLeast(1)
        val yearsDecimal = totalMonths / 12.0

        if (principal <= 0) {
            etFdAmount.error = "Please enter valid deposit amount"
            return
        }
        if (effectiveRate <= 0) {
            etFdRate.error = "Please enter valid interest rate"
            return
        }

        val n = compoundingFrequencyPerYear
        val r = effectiveRate / 100.0

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val scheduleCalendar = Calendar.getInstance()

        for (m in 1..totalMonths) {
            val currentYearDecimal = m / 12.0
            val currentMaturity = principal * (1 + r / n).pow(n * currentYearDecimal)
            val currentInterest = currentMaturity - principal

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = 0.0,
                    principal = principal,
                    interest = currentInterest,
                    balance = currentMaturity
                )
            )
        }

        val maturityValue = principal * (1 + r / n).pow(n * yearsDecimal)
        val interestEarned = maturityValue - principal
        val titleText = if (isSeniorCitizen) "FD Calculator (Senior Citizen)" else "FD Calculator"

        val yearsInt = totalMonths / 12
        val monthsInt = totalMonths % 12

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", principal)
            putExtra("INTEREST_RATE", effectiveRate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", 0.0)
            putExtra("TOTAL_INTEREST", interestEarned)
            putExtra("TOTAL_COST", maturityValue)
            putExtra("PAYOFF_DATE", dateFormatter.format(scheduleCalendar.time))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etFdAmount.setText("2,00,000")
        etFdRate.setText("7.0")
        tenureSelector.tenureMonths = 60

        isSeniorCitizen = false
        compoundingFrequencyPerYear = 4

        updateSeniorCitizenUI()
        highlightAmountChip(chipFd2L)
        highlightRateChip(chipFdRate7)
        highlightFreqChip(chipFreqQuarterly)
        updateFdSummary()

        findViewById<NestedScrollView>(R.id.scrollViewFd).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}
