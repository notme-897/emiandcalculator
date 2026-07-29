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

class RelocationAdjusterActivity : BaseInputActivity() {

    private lateinit var etCurrentCtc: EditText

    private lateinit var chipFromTier1: MaterialButton
    private lateinit var chipFromTier2: MaterialButton
    private lateinit var chipFromTier3: MaterialButton

    private lateinit var chipToTier1: MaterialButton
    private lateinit var chipToTier2: MaterialButton
    private lateinit var chipToTier3: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var fromTier = 2 // Tier 2 default
    private var toTier = 1   // Tier 1 Metro default

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_relocation_adjuster

    override fun getActivityTitle(): String = "Relocation Salary Adjuster"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCurrentCtc = findViewById(R.id.etCurrentCtc)

        chipFromTier1 = findViewById(R.id.chipFromTier1)
        chipFromTier2 = findViewById(R.id.chipFromTier2)
        chipFromTier3 = findViewById(R.id.chipFromTier3)

        chipToTier1 = findViewById(R.id.chipToTier1)
        chipToTier2 = findViewById(R.id.chipToTier2)
        chipToTier3 = findViewById(R.id.chipToTier3)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val allChips = listOf(
            chipFromTier1, chipFromTier2, chipFromTier3,
            chipToTier1, chipToTier2, chipToTier3
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etCurrentCtc)

        chipFromTier1.setOnClickListener { fromTier = 1; highlightFromTierChips() }
        chipFromTier2.setOnClickListener { fromTier = 2; highlightFromTierChips() }
        chipFromTier3.setOnClickListener { fromTier = 3; highlightFromTierChips() }

        chipToTier1.setOnClickListener { toTier = 1; highlightToTierChips() }
        chipToTier2.setOnClickListener { toTier = 2; highlightToTierChips() }
        chipToTier3.setOnClickListener { toTier = 3; highlightToTierChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateRelocationAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etCurrentCtc.setText("12,00,000")
        highlightFromTierChips()
        highlightToTierChips()
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

    private fun highlightFromTierChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipFromTier1, chipFromTier2, chipFromTier3)
        val selectedIndex = fromTier - 1

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

    private fun highlightToTierChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipToTier1, chipToTier2, chipToTier3)
        val selectedIndex = toTier - 1

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

    private fun getCityIndex(tier: Int): Double {
        return when (tier) {
            1 -> 1.40 // Tier 1 Metro (Mumbai/Bengaluru/Delhi) cost index
            2 -> 1.00 // Tier 2 baseline
            3 -> 0.75 // Tier 3 / Remote baseline
            else -> 1.00
        }
    }

    private fun calculateRelocationAndNavigate() {
        val currentCtcStr = etCurrentCtc.text.toString().replace(",", "")
        val currentCtc = currentCtcStr.toDoubleOrNull()

        if (currentCtc == null || currentCtc <= 0) {
            Toast.makeText(this, "Please enter a valid current CTC", Toast.LENGTH_SHORT).show()
            return
        }

        val fromIndex = getCityIndex(fromTier)
        val toIndex = getCityIndex(toTier)

        val relocationFactor = toIndex / fromIndex
        val targetRequiredCtc = currentCtc * relocationFactor

        val ctcDifference = targetRequiredCtc - currentCtc
        val pctHikeNeeded = ((relocationFactor - 1.0) * 100.0)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Current City CTC", currentCtc, currentCtc / 12.0, 0.0, currentCtc))
        scheduleList.add(PaymentScheduleItem(2, "Cost Adjustment Needed", Math.abs(ctcDifference), Math.abs(ctcDifference) / 12.0, 0.0, Math.abs(ctcDifference)))
        scheduleList.add(PaymentScheduleItem(3, "Required Relocation CTC", targetRequiredCtc, targetRequiredCtc / 12.0, 0.0, targetRequiredCtc))

        val tierNames = mapOf(1 to "Tier 1 Metro", 2 to "Tier 2 City", 3 to "Tier 3 / Remote")
        val statusText = "Moving from ${tierNames[fromTier]} to ${tierNames[toTier]}"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Relocation Salary Result")
            putExtra("LOAN_AMOUNT", targetRequiredCtc / 12.0)
            putExtra("INTEREST_RATE", pctHikeNeeded.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", statusText)
            putExtra("EMI", targetRequiredCtc / 12.0)
            putExtra("TOTAL_INTEREST", Math.abs(ctcDifference))
            putExtra("TOTAL_COST", targetRequiredCtc)
            putExtra("PAYOFF_DATE", "Target Annual CTC: ₹" + commaFormat.format(targetRequiredCtc))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etCurrentCtc.setText("12,00,000")
        fromTier = 2
        toTier = 1
        highlightFromTierChips()
        highlightToTierChips()
    }
}
