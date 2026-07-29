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

class PpfCalculatorActivity : BaseInputActivity() {

    private lateinit var etPpfAmount: EditText
    private lateinit var chipPpf25k: MaterialButton
    private lateinit var chipPpf50k: MaterialButton
    private lateinit var chipPpf1L: MaterialButton
    private lateinit var chipPpf1_5L: MaterialButton

    private lateinit var tenureSelector: LoanTenureSelectorView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val ppfRate = 7.1 // Government set PPF interest rate

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_ppf_calculator

    override fun getActivityTitle(): String = "PPF Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPpfAmount = findViewById(R.id.etPpfAmount)
        chipPpf25k = findViewById(R.id.chipPpf25k)
        chipPpf50k = findViewById(R.id.chipPpf50k)
        chipPpf1L = findViewById(R.id.chipPpf1L)
        chipPpf1_5L = findViewById(R.id.chipPpf1_5L)

        tenureSelector = findViewById(R.id.tenureSelector)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val allChips = listOf(
            chipPpf25k, chipPpf50k, chipPpf1L, chipPpf1_5L
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etPpfAmount) { updatePpfSummary() }

        chipPpf25k.setOnClickListener { setQuickAmount(25000.0); highlightAmountChip(chipPpf25k) }
        chipPpf50k.setOnClickListener { setQuickAmount(50000.0); highlightAmountChip(chipPpf50k) }
        chipPpf1L.setOnClickListener { setQuickAmount(100000.0); highlightAmountChip(chipPpf1L) }
        chipPpf1_5L.setOnClickListener { setQuickAmount(150000.0); highlightAmountChip(chipPpf1_5L) }

        tenureSelector.setOnTenureChangedListener {
            updatePpfSummary()
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

        etPpfAmount.setText("1,50,000")
        tenureSelector.tenureMonths = 180 // 15 Years default

        highlightAmountChip(chipPpf1_5L)
        updatePpfSummary()
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
                            val cappedVal = if (doubleVal > 150000) 150000.0 else doubleVal
                            val formatted = commaFormat.format(cappedVal)
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
                isFormatting = false
                onFormatted?.invoke()
                updatePpfSummary()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etPpfAmount.setText(commaFormat.format(amount))
        updatePpfSummary()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updatePpfSummary() {
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
        val chips = listOf(chipPpf25k, chipPpf50k, chipPpf1L, chipPpf1_5L)
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
        val yearlyContribution = getRawValue(etPpfAmount)

        if (yearlyContribution <= 0) {
            etPpfAmount.error = "Please enter valid yearly contribution"
            return
        }

        val totalMonths = tenureSelector.tenureMonths.coerceAtLeast(12)
        // PPF is a yearly instrument — round to nearest whole year (minimum 1)
        val totalYears = (totalMonths / 12).coerceAtLeast(1)

        val totalInvested = yearlyContribution * totalYears
        var maturityValue = 0.0

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val scheduleCalendar = Calendar.getInstance()

        // PPF compound formula: add contribution at start of each year, interest at end of year
        for (year in 1..totalYears) {
            maturityValue = (maturityValue + yearlyContribution) * (1 + ppfRate / 100.0)
            val totalInvestedSoFar = yearlyContribution * year.toDouble()
            val interestSoFar = maturityValue - totalInvestedSoFar

            // Create one schedule entry per year (each representing the annual statement)
            for (m in 1..12) {
                scheduleCalendar.add(Calendar.MONTH, 1)
                val monthIndex = (year - 1) * 12 + m
                schedule.add(
                    PaymentScheduleItem(
                        emiNo = monthIndex,
                        date = dateFormatter.format(scheduleCalendar.time),
                        emi = if (m == 1) yearlyContribution else 0.0,
                        principal = totalInvestedSoFar,
                        interest = if (interestSoFar > 0) interestSoFar else 0.0,
                        balance = maturityValue
                    )
                )
            }
        }

        val interestEarned = maturityValue - totalInvested


        val yearsInt = totalYears
        val monthsInt = 0 // PPF operates on whole years only

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "PPF Calculator (Tax-Free)")
            putExtra("LOAN_AMOUNT", totalInvested)
            putExtra("INTEREST_RATE", ppfRate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", yearlyContribution)
            putExtra("TOTAL_INTEREST", interestEarned)
            putExtra("TOTAL_COST", maturityValue)
            putExtra("PAYOFF_DATE", dateFormatter.format(scheduleCalendar.time))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPpfAmount.setText("1,50,000")
        tenureSelector.tenureMonths = 180

        highlightAmountChip(chipPpf1_5L)
        updatePpfSummary()

        findViewById<NestedScrollView>(R.id.scrollViewPpf).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}
