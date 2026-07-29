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

class LumpsumCalculatorActivity : BaseInputActivity() {

    private lateinit var etLumpsumAmount: EditText
    private lateinit var chipLump10k: MaterialButton
    private lateinit var chipLump50k: MaterialButton
    private lateinit var chipLump1L: MaterialButton
    private lateinit var chipLump5L: MaterialButton
    private lateinit var chipLump10L: MaterialButton

    private lateinit var etLumpsumRate: EditText
    private lateinit var chipRate8: MaterialButton
    private lateinit var chipRate10: MaterialButton
    private lateinit var chipRate12: MaterialButton
    private lateinit var chipRate15: MaterialButton

    private lateinit var tenureSelector: LoanTenureSelectorView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_lumpsum_calculator

    override fun getActivityTitle(): String = "Lump Sum Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etLumpsumAmount = findViewById(R.id.etLumpsumAmount)
        chipLump10k = findViewById(R.id.chipLump10k)
        chipLump50k = findViewById(R.id.chipLump50k)
        chipLump1L = findViewById(R.id.chipLump1L)
        chipLump5L = findViewById(R.id.chipLump5L)
        chipLump10L = findViewById(R.id.chipLump10L)

        etLumpsumRate = findViewById(R.id.etLumpsumRate)
        chipRate8 = findViewById(R.id.chipRate8)
        chipRate10 = findViewById(R.id.chipRate10)
        chipRate12 = findViewById(R.id.chipRate12)
        chipRate15 = findViewById(R.id.chipRate15)

        tenureSelector = findViewById(R.id.tenureSelector)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val allChips = listOf(
            chipLump10k, chipLump50k, chipLump1L, chipLump5L, chipLump10L,
            chipRate8, chipRate10, chipRate12, chipRate15
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etLumpsumAmount) { updateLumpSummary() }

        chipLump10k.setOnClickListener { setQuickAmount(10000.0); highlightAmountChip(chipLump10k) }
        chipLump50k.setOnClickListener { setQuickAmount(50000.0); highlightAmountChip(chipLump50k) }
        chipLump1L.setOnClickListener { setQuickAmount(100000.0); highlightAmountChip(chipLump1L) }
        chipLump5L.setOnClickListener { setQuickAmount(500000.0); highlightAmountChip(chipLump5L) }
        chipLump10L.setOnClickListener { setQuickAmount(1000000.0); highlightAmountChip(chipLump10L) }

        etLumpsumRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateLumpSummary()
            }
        })

        chipRate8.setOnClickListener { etLumpsumRate.setText("8.0"); highlightRateChip(chipRate8) }
        chipRate10.setOnClickListener { etLumpsumRate.setText("10.0"); highlightRateChip(chipRate10) }
        chipRate12.setOnClickListener { etLumpsumRate.setText("12.0"); highlightRateChip(chipRate12) }
        chipRate15.setOnClickListener { etLumpsumRate.setText("15.0"); highlightRateChip(chipRate15) }

        tenureSelector.setOnTenureChangedListener {
            updateLumpSummary()
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

        etLumpsumAmount.setText("1,00,000")
        etLumpsumRate.setText("12.0")
        tenureSelector.tenureMonths = 120 // 10 Years default

        highlightAmountChip(chipLump1L)
        highlightRateChip(chipRate12)
        updateLumpSummary()
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
                updateLumpSummary()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etLumpsumAmount.setText(commaFormat.format(amount))
        updateLumpSummary()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateLumpSummary() {
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
        val chips = listOf(chipLump10k, chipLump50k, chipLump1L, chipLump5L, chipLump10L)
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
        val rateChips = listOf(chipRate8, chipRate10, chipRate12, chipRate15)
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
            8.0 -> highlightRateChip(chipRate8)
            10.0 -> highlightRateChip(chipRate10)
            12.0 -> highlightRateChip(chipRate12)
            15.0 -> highlightRateChip(chipRate15)
            else -> highlightRateChip(null)
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
        val principal = getRawValue(etLumpsumAmount)
        val rate = etLumpsumRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = tenureSelector.tenureMonths.coerceAtLeast(1)
        val yearsDecimal = totalMonths / 12.0

        if (principal <= 0) {
            etLumpsumAmount.error = "Please enter valid principal amount"
            return
        }
        if (rate <= 0) {
            etLumpsumRate.error = "Please enter valid expected rate"
            return
        }

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val scheduleCalendar = Calendar.getInstance()

        for (m in 1..totalMonths) {
            val currentYearDecimal = m / 12.0
            val currentMaturity = principal * (1 + rate / 100.0).pow(currentYearDecimal)
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

        val maturityValue = principal * (1 + rate / 100.0).pow(yearsDecimal)
        val returnsGained = maturityValue - principal

        val yearsInt = totalMonths / 12
        val monthsInt = totalMonths % 12

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Lump Sum Calculator")
            putExtra("LOAN_AMOUNT", principal)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", 0.0)
            putExtra("TOTAL_INTEREST", returnsGained)
            putExtra("TOTAL_COST", maturityValue)
            putExtra("PAYOFF_DATE", dateFormatter.format(scheduleCalendar.time))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etLumpsumAmount.setText("1,00,000")
        etLumpsumRate.setText("12.0")
        tenureSelector.tenureMonths = 120

        highlightAmountChip(chipLump1L)
        highlightRateChip(chipRate12)
        updateLumpSummary()

        findViewById<NestedScrollView>(R.id.scrollViewLumpsum).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}
