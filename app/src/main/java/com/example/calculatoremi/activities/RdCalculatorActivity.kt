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

class RdCalculatorActivity : BaseInputActivity() {

    private lateinit var etRdAmount: EditText
    private lateinit var chipRd1k: MaterialButton
    private lateinit var chipRd2_5k: MaterialButton
    private lateinit var chipRd5k: MaterialButton
    private lateinit var chipRd10k: MaterialButton
    private lateinit var chipRd25k: MaterialButton

    private lateinit var etRdRate: EditText
    private lateinit var chipRdRate6_5: MaterialButton
    private lateinit var chipRdRate7: MaterialButton
    private lateinit var chipRdRate7_5: MaterialButton
    private lateinit var chipRdRate8: MaterialButton

    private lateinit var tenureSelector: LoanTenureSelectorView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_rd_calculator

    override fun getActivityTitle(): String = "RD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etRdAmount = findViewById(R.id.etRdAmount)
        chipRd1k = findViewById(R.id.chipRd1k)
        chipRd2_5k = findViewById(R.id.chipRd2_5k)
        chipRd5k = findViewById(R.id.chipRd5k)
        chipRd10k = findViewById(R.id.chipRd10k)
        chipRd25k = findViewById(R.id.chipRd25k)

        etRdRate = findViewById(R.id.etRdRate)
        chipRdRate6_5 = findViewById(R.id.chipRdRate6_5)
        chipRdRate7 = findViewById(R.id.chipRdRate7)
        chipRdRate7_5 = findViewById(R.id.chipRdRate7_5)
        chipRdRate8 = findViewById(R.id.chipRdRate8)

        tenureSelector = findViewById(R.id.tenureSelector)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val allChips = listOf(
            chipRd1k, chipRd2_5k, chipRd5k, chipRd10k, chipRd25k,
            chipRdRate6_5, chipRdRate7, chipRdRate7_5, chipRdRate8
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etRdAmount) { updateRdSummary() }

        chipRd1k.setOnClickListener { setQuickAmount(1000.0); highlightAmountChip(chipRd1k) }
        chipRd2_5k.setOnClickListener { setQuickAmount(2500.0); highlightAmountChip(chipRd2_5k) }
        chipRd5k.setOnClickListener { setQuickAmount(5000.0); highlightAmountChip(chipRd5k) }
        chipRd10k.setOnClickListener { setQuickAmount(10000.0); highlightAmountChip(chipRd10k) }
        chipRd25k.setOnClickListener { setQuickAmount(25000.0); highlightAmountChip(chipRd25k) }

        etRdRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateRdSummary()
            }
        })

        chipRdRate6_5.setOnClickListener { etRdRate.setText("6.5"); highlightRateChip(chipRdRate6_5) }
        chipRdRate7.setOnClickListener { etRdRate.setText("7.0"); highlightRateChip(chipRdRate7) }
        chipRdRate7_5.setOnClickListener { etRdRate.setText("7.5"); highlightRateChip(chipRdRate7_5) }
        chipRdRate8.setOnClickListener { etRdRate.setText("8.0"); highlightRateChip(chipRdRate8) }

        tenureSelector.setOnTenureChangedListener {
            updateRdSummary()
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

        etRdAmount.setText("5,000")
        etRdRate.setText("7.0")
        tenureSelector.tenureMonths = 60 // 5 Years default

        highlightAmountChip(chipRd5k)
        highlightRateChip(chipRdRate7)
        updateRdSummary()
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
                updateRdSummary()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etRdAmount.setText(commaFormat.format(amount))
        updateRdSummary()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateRdSummary() {
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
        val chips = listOf(chipRd1k, chipRd2_5k, chipRd5k, chipRd10k, chipRd25k)
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
        val rateChips = listOf(chipRdRate6_5, chipRdRate7, chipRdRate7_5, chipRdRate8)
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
            6.5 -> highlightRateChip(chipRdRate6_5)
            7.0 -> highlightRateChip(chipRdRate7)
            7.5 -> highlightRateChip(chipRdRate7_5)
            8.0 -> highlightRateChip(chipRdRate8)
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
        val monthlyDeposit = getRawValue(etRdAmount)
        val rate = etRdRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = tenureSelector.tenureMonths.coerceAtLeast(1)

        if (monthlyDeposit <= 0) {
            etRdAmount.error = "Please enter valid monthly deposit"
            return
        }
        if (rate <= 0) {
            etRdRate.error = "Please enter valid interest rate"
            return
        }

        val totalInvested = monthlyDeposit * totalMonths

        var maturityValue = 0.0
        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val scheduleCalendar = Calendar.getInstance()

        for (m in 1..totalMonths) {
            val monthsCompounded = (totalMonths - m + 1) / 12.0
            val depositContribution = monthlyDeposit * (1 + rate / 400.0).pow(4.0 * monthsCompounded)
            maturityValue += depositContribution

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = monthlyDeposit,
                    principal = monthlyDeposit * m,
                    interest = maturityValue - (monthlyDeposit * m),
                    balance = maturityValue
                )
            )
        }

        val interestEarned = maturityValue - totalInvested

        val yearsInt = totalMonths / 12
        val monthsInt = totalMonths % 12

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "RD Calculator")
            putExtra("LOAN_AMOUNT", totalInvested)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", monthlyDeposit)
            putExtra("TOTAL_INTEREST", interestEarned)
            putExtra("TOTAL_COST", maturityValue)
            putExtra("PAYOFF_DATE", dateFormatter.format(scheduleCalendar.time))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etRdAmount.setText("5,000")
        etRdRate.setText("7.0")
        tenureSelector.tenureMonths = 60

        highlightAmountChip(chipRd5k)
        highlightRateChip(chipRdRate7)
        updateRdSummary()

        findViewById<NestedScrollView>(R.id.scrollViewRd).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}
