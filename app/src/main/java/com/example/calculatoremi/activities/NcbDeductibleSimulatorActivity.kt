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

class NcbDeductibleSimulatorActivity : BaseInputActivity() {

    private lateinit var etBasePremium: EditText

    private lateinit var chipDed0: MaterialButton
    private lateinit var chipDed25k: MaterialButton
    private lateinit var chipDed50k: MaterialButton
    private lateinit var chipDed1L: MaterialButton

    private lateinit var chipNcb0: MaterialButton
    private lateinit var chipNcb20: MaterialButton
    private lateinit var chipNcb35: MaterialButton
    private lateinit var chipNcb50: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var deductibleAmount = 25000.0
    private var ncbDiscountPct = 20.0

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_ncb_deductible_simulator

    override fun getActivityTitle(): String = "NCB & Deductible Simulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etBasePremium = findViewById(R.id.etBasePremium)

        chipDed0 = findViewById(R.id.chipDed0)
        chipDed25k = findViewById(R.id.chipDed25k)
        chipDed50k = findViewById(R.id.chipDed50k)
        chipDed1L = findViewById(R.id.chipDed1L)

        chipNcb0 = findViewById(R.id.chipNcb0)
        chipNcb20 = findViewById(R.id.chipNcb20)
        chipNcb35 = findViewById(R.id.chipNcb35)
        chipNcb50 = findViewById(R.id.chipNcb50)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etBasePremium)

        chipDed0.setOnClickListener { deductibleAmount = 0.0; highlightDedChips() }
        chipDed25k.setOnClickListener { deductibleAmount = 25000.0; highlightDedChips() }
        chipDed50k.setOnClickListener { deductibleAmount = 50000.0; highlightDedChips() }
        chipDed1L.setOnClickListener { deductibleAmount = 100000.0; highlightDedChips() }

        chipNcb0.setOnClickListener { ncbDiscountPct = 0.0; highlightNcbChips() }
        chipNcb20.setOnClickListener { ncbDiscountPct = 20.0; highlightNcbChips() }
        chipNcb35.setOnClickListener { ncbDiscountPct = 35.0; highlightNcbChips() }
        chipNcb50.setOnClickListener { ncbDiscountPct = 50.0; highlightNcbChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            simulateNcbAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etBasePremium.setText("25,000")
        highlightDedChips()
        highlightNcbChips()
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

    private fun highlightDedChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipDed0, chipDed25k, chipDed50k, chipDed1L)
        val selectedIndex = when (deductibleAmount) { 0.0 -> 0; 25000.0 -> 1; 50000.0 -> 2; else -> 3 }

        chips.forEachIndexed { index, chip ->
            if (index == selectedIndex) {
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

    private fun highlightNcbChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipNcb0, chipNcb20, chipNcb35, chipNcb50)
        val selectedIndex = when (ncbDiscountPct) { 0.0 -> 0; 20.0 -> 1; 35.0 -> 2; else -> 3 }

        chips.forEachIndexed { index, chip ->
            if (index == selectedIndex) {
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

    private fun simulateNcbAndNavigate() {
        val basePremiumStr = etBasePremium.text.toString().replace(",", "")
        val basePremium = basePremiumStr.toDoubleOrNull() ?: 0.0

        if (basePremium <= 0) {
            Toast.makeText(this, "Please enter a valid base premium", Toast.LENGTH_SHORT).show()
            return
        }

        // Voluntary deductible discount percentage (approx 10% discount for 25k, 20% for 50k, 30% for 1L)
        val deductibleDiscountPct = when (deductibleAmount) {
            0.0 -> 0.0
            25000.0 -> 12.0
            50000.0 -> 22.0
            else -> 32.0
        }

        val premiumAfterDeductible = basePremium * (1.0 - (deductibleDiscountPct / 100.0))
        val ncbDiscountAmount = premiumAfterDeductible * (ncbDiscountPct / 100.0)
        val finalPayablePremium = premiumAfterDeductible - ncbDiscountAmount

        val totalAnnualSavings = basePremium - finalPayablePremium
        val totalSavingsPct = (totalAnnualSavings / basePremium) * 100.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Base Standard Premium", basePremium, basePremium / 12.0, 0.0, basePremium))
        scheduleList.add(PaymentScheduleItem(2, "Voluntary Deductible Savings", basePremium - premiumAfterDeductible, (basePremium - premiumAfterDeductible) / 12.0, 0.0, basePremium - premiumAfterDeductible))
        scheduleList.add(PaymentScheduleItem(3, "NCB Bonus Discount (${ncbDiscountPct.toInt()}%)", ncbDiscountAmount, ncbDiscountAmount / 12.0, 0.0, ncbDiscountAmount))
        scheduleList.add(PaymentScheduleItem(4, "Final Net Payable Premium", finalPayablePremium, finalPayablePremium / 12.0, 0.0, finalPayablePremium))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "NCB & Deductible Result")
            putExtra("LOAN_AMOUNT", finalPayablePremium) // Final Premium
            putExtra("INTEREST_RATE", totalSavingsPct.toFloat()) // Total % Saved
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", "Saved ₹" + commaFormat.format(totalAnnualSavings.toInt()) + " (${totalSavingsPct.toInt()}% Off)")
            putExtra("EMI", finalPayablePremium / 12.0)
            putExtra("TOTAL_INTEREST", totalAnnualSavings)
            putExtra("TOTAL_COST", finalPayablePremium)
            putExtra("PAYOFF_DATE", "Net Payable: ₹" + commaFormat.format(finalPayablePremium.toInt()) + " / yr")
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etBasePremium.setText("25,000")
        deductibleAmount = 25000.0
        ncbDiscountPct = 20.0
        highlightDedChips()
        highlightNcbChips()
    }
}
