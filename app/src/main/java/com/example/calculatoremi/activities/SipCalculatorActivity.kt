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

class SipCalculatorActivity : BaseInputActivity() {

    private lateinit var etMonthlySip: EditText
    private lateinit var chipSip1k: MaterialButton
    private lateinit var chipSip2_5k: MaterialButton
    private lateinit var chipSip5k: MaterialButton
    private lateinit var chipSip10k: MaterialButton
    private lateinit var chipSip25k: MaterialButton

    private lateinit var etSipRate: EditText
    private lateinit var chipRate8: MaterialButton
    private lateinit var chipRate10: MaterialButton
    private lateinit var chipRate12: MaterialButton
    private lateinit var chipRate15: MaterialButton

    private lateinit var chipStep0: MaterialButton
    private lateinit var chipStep5: MaterialButton
    private lateinit var chipStep10: MaterialButton
    private lateinit var chipStep15: MaterialButton

    private lateinit var tenureSelector: LoanTenureSelectorView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var selectedStepUpPct = 0.0

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_sip_calculator

    override fun getActivityTitle(): String = "SIP Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        etMonthlySip = findViewById(R.id.etMonthlySip)
        chipSip1k = findViewById(R.id.chipSip1k)
        chipSip2_5k = findViewById(R.id.chipSip2_5k)
        chipSip5k = findViewById(R.id.chipSip5k)
        chipSip10k = findViewById(R.id.chipSip10k)
        chipSip25k = findViewById(R.id.chipSip25k)

        etSipRate = findViewById(R.id.etSipRate)
        chipRate8 = findViewById(R.id.chipRate8)
        chipRate10 = findViewById(R.id.chipRate10)
        chipRate12 = findViewById(R.id.chipRate12)
        chipRate15 = findViewById(R.id.chipRate15)

        chipStep0 = findViewById(R.id.chipStep0)
        chipStep5 = findViewById(R.id.chipStep5)
        chipStep10 = findViewById(R.id.chipStep10)
        chipStep15 = findViewById(R.id.chipStep15)

        tenureSelector = findViewById(R.id.tenureSelector)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        // Setup touch animations
        val allChips = listOf(
            chipSip1k, chipSip2_5k, chipSip5k, chipSip10k, chipSip25k,
            chipRate8, chipRate10, chipRate12, chipRate15,
            chipStep0, chipStep5, chipStep10, chipStep15
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etMonthlySip) { updateSipSummary() }

        // Amount Chips
        chipSip1k.setOnClickListener { setQuickAmount(1000.0); highlightAmountChip(chipSip1k) }
        chipSip2_5k.setOnClickListener { setQuickAmount(2500.0); highlightAmountChip(chipSip2_5k) }
        chipSip5k.setOnClickListener { setQuickAmount(5000.0); highlightAmountChip(chipSip5k) }
        chipSip10k.setOnClickListener { setQuickAmount(10000.0); highlightAmountChip(chipSip10k) }
        chipSip25k.setOnClickListener { setQuickAmount(25000.0); highlightAmountChip(chipSip25k) }

        // Rate Manual change watcher
        etSipRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateSipSummary()
            }
        })

        // Rate Chips
        chipRate8.setOnClickListener { etSipRate.setText("8.0"); highlightRateChip(chipRate8) }
        chipRate10.setOnClickListener { etSipRate.setText("10.0"); highlightRateChip(chipRate10) }
        chipRate12.setOnClickListener { etSipRate.setText("12.0"); highlightRateChip(chipRate12) }
        chipRate15.setOnClickListener { etSipRate.setText("15.0"); highlightRateChip(chipRate15) }

        // Step-Up Chips
        chipStep0.setOnClickListener { selectedStepUpPct = 0.0; highlightStepChip(chipStep0); updateSipSummary() }
        chipStep5.setOnClickListener { selectedStepUpPct = 5.0; highlightStepChip(chipStep5); updateSipSummary() }
        chipStep10.setOnClickListener { selectedStepUpPct = 10.0; highlightStepChip(chipStep10); updateSipSummary() }
        chipStep15.setOnClickListener { selectedStepUpPct = 15.0; highlightStepChip(chipStep15); updateSipSummary() }

        tenureSelector.setOnTenureChangedListener {
            updateSipSummary()
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

        // Defaults
        etMonthlySip.setText("5,000")
        etSipRate.setText("12.0")
        tenureSelector.tenureMonths = 120 // 10 Years default

        highlightAmountChip(chipSip5k)
        highlightRateChip(chipRate12)
        highlightStepChip(chipStep0)
        updateSipSummary()
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
                updateSipSummary()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etMonthlySip.setText(commaFormat.format(amount))
        updateSipSummary()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateSipSummary() {
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
        val chips = listOf(chipSip1k, chipSip2_5k, chipSip5k, chipSip10k, chipSip25k)
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

    private fun highlightStepChip(selectedChip: MaterialButton) {
        val chips = listOf(chipStep0, chipStep5, chipStep10, chipStep15)
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
        val monthlySip = getRawValue(etMonthlySip)
        val rate = etSipRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = tenureSelector.tenureMonths.coerceAtLeast(1)

        if (monthlySip <= 0) {
            etMonthlySip.error = "Please enter valid monthly SIP amount"
            return
        }
        if (rate <= 0) {
            etSipRate.error = "Please enter valid expected rate"
            return
        }

        val monthlyRate = rate / (12 * 100)

        var totalInvested = 0.0
        var maturityValue = 0.0
        var currentMonthlySip = monthlySip

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val scheduleCalendar = Calendar.getInstance()

        for (m in 1..totalMonths) {
            totalInvested += currentMonthlySip
            maturityValue = (maturityValue + currentMonthlySip) * (1 + monthlyRate)

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = currentMonthlySip,
                    principal = totalInvested,
                    interest = maturityValue - totalInvested,
                    balance = maturityValue
                )
            )

            if (selectedStepUpPct > 0 && m % 12 == 0) {
                currentMonthlySip *= (1 + selectedStepUpPct / 100.0)
            }
        }

        val returnsGained = maturityValue - totalInvested
        val titleText = if (selectedStepUpPct > 0) "Step-Up SIP (${selectedStepUpPct.toInt()}%)" else "SIP Calculator"

        val yearsInt = totalMonths / 12
        val monthsInt = totalMonths % 12

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", totalInvested)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", monthlySip)
            putExtra("TOTAL_INTEREST", returnsGained)
            putExtra("TOTAL_COST", maturityValue)
            putExtra("PAYOFF_DATE", dateFormatter.format(scheduleCalendar.time))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etMonthlySip.setText("5,000")
        etSipRate.setText("12.0")
        tenureSelector.tenureMonths = 120

        selectedStepUpPct = 0.0

        highlightAmountChip(chipSip5k)
        highlightRateChip(chipRate12)
        highlightStepChip(chipStep0)
        updateSipSummary()

        findViewById<NestedScrollView>(R.id.scrollViewSip).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}

