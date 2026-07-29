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

class SalaryHikeActivity : BaseInputActivity() {

    private lateinit var etCurrentCtc: EditText
    private lateinit var etHikePercentage: EditText

    private lateinit var chipHike10: MaterialButton
    private lateinit var chipHike15: MaterialButton
    private lateinit var chipHike20: MaterialButton
    private lateinit var chipHike30: MaterialButton
    private lateinit var chipHike50: MaterialButton

    private lateinit var chipRegimeNew: MaterialButton
    private lateinit var chipRegimeOld: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isNewRegime = true
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_salary_hike

    override fun getActivityTitle(): String = "Salary Hike Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCurrentCtc = findViewById(R.id.etCurrentCtc)
        etHikePercentage = findViewById(R.id.etHikePercentage)

        chipHike10 = findViewById(R.id.chipHike10)
        chipHike15 = findViewById(R.id.chipHike15)
        chipHike20 = findViewById(R.id.chipHike20)
        chipHike30 = findViewById(R.id.chipHike30)
        chipHike50 = findViewById(R.id.chipHike50)

        chipRegimeNew = findViewById(R.id.chipRegimeNew)
        chipRegimeOld = findViewById(R.id.chipRegimeOld)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val allChips = listOf(
            chipHike10, chipHike15, chipHike20, chipHike30, chipHike50,
            chipRegimeNew, chipRegimeOld
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etCurrentCtc)

        chipHike10.setOnClickListener { etHikePercentage.setText("10"); highlightHikeChip(chipHike10) }
        chipHike15.setOnClickListener { etHikePercentage.setText("15"); highlightHikeChip(chipHike15) }
        chipHike20.setOnClickListener { etHikePercentage.setText("20"); highlightHikeChip(chipHike20) }
        chipHike30.setOnClickListener { etHikePercentage.setText("30"); highlightHikeChip(chipHike30) }
        chipHike50.setOnClickListener { etHikePercentage.setText("50"); highlightHikeChip(chipHike50) }

        chipRegimeNew.setOnClickListener {
            isNewRegime = true
            highlightRegimeChips()
        }
        chipRegimeOld.setOnClickListener {
            isNewRegime = false
            highlightRegimeChips()
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
        etCurrentCtc.setText("10,00,000")
        etHikePercentage.setText("20")
        highlightHikeChip(chipHike20)
        highlightRegimeChips()
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

    private fun highlightHikeChip(selectedChip: MaterialButton) {
        val chips = listOf(chipHike10, chipHike15, chipHike20, chipHike30, chipHike50)
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        chips.forEach { chip ->
            if (chip == selectedChip) {
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

    private fun highlightRegimeChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (isNewRegime) {
            chipRegimeNew.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipRegimeNew.setTextColor(Color.WHITE)
            chipRegimeNew.strokeWidth = 0

            chipRegimeOld.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipRegimeOld.setTextColor(Color.parseColor("#1E293B"))
            chipRegimeOld.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipRegimeOld.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipRegimeOld.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipRegimeOld.setTextColor(Color.WHITE)
            chipRegimeOld.strokeWidth = 0

            chipRegimeNew.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipRegimeNew.setTextColor(Color.parseColor("#1E293B"))
            chipRegimeNew.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipRegimeNew.strokeWidth = (1 * resources.displayMetrics.density).toInt()
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

    private fun computeNetInHand(ctc: Double): Double {
        val monthlyGross = ctc / 12.0
        val monthlyBasic = monthlyGross * 0.50
        val pfMonthly = minOf(monthlyBasic * 0.12, 1800.0)
        val ptMonthly = 200.0

        val annualTaxable = max(0.0, ctc - 75000.0) // Standard deduction
        var annualTds = 0.0

        if (isNewRegime) {
            val taxable = annualTaxable
            if (taxable <= 700000.0) {
                annualTds = 0.0
            } else {
                var rem = taxable
                if (rem > 300000) {
                    val chunk = minOf(rem - 300000, 400000.0)
                    annualTds += chunk * 0.05
                }
                if (rem > 700000) {
                    val chunk = minOf(rem - 700000, 300000.0)
                    annualTds += chunk * 0.10
                }
                if (rem > 1000000) {
                    val chunk = minOf(rem - 1000000, 200000.0)
                    annualTds += chunk * 0.15
                }
                if (rem > 1200000) {
                    val chunk = minOf(rem - 1200000, 300000.0)
                    annualTds += chunk * 0.20
                }
                if (rem > 1500000) {
                    annualTds += (rem - 1500000) * 0.30
                }
            }
        } else {
            val taxable = annualTaxable
            if (taxable <= 500000.0) {
                annualTds = 0.0
            } else {
                if (taxable > 250000) {
                    val chunk = minOf(taxable - 250000, 250000.0)
                    annualTds += chunk * 0.05
                }
                if (taxable > 500000) {
                    val chunk = minOf(taxable - 500000, 500000.0)
                    annualTds += chunk * 0.20
                }
                if (taxable > 1000000) {
                    annualTds += (taxable - 1000000) * 0.30
                }
            }
        }

        val monthlyTds = annualTds / 12.0
        return max(0.0, monthlyGross - pfMonthly - ptMonthly - monthlyTds)
    }

    private fun calculateAndNavigate() {
        val ctcStr = etCurrentCtc.text.toString().replace(",", "")
        val hikePctStr = etHikePercentage.text.toString().trim()

        val currentCtc = ctcStr.toDoubleOrNull()
        val hikePct = hikePctStr.toDoubleOrNull()

        if (currentCtc == null || currentCtc <= 0) {
            Toast.makeText(this, "Please enter a valid current annual CTC", Toast.LENGTH_SHORT).show()
            return
        }
        if (hikePct == null || hikePct < 0) {
            Toast.makeText(this, "Please enter a valid hike percentage", Toast.LENGTH_SHORT).show()
            return
        }

        val hikeAmountAnnual = currentCtc * (hikePct / 100.0)
        val newCtc = currentCtc + hikeAmountAnnual

        val oldNetMonthly = computeNetInHand(currentCtc)
        val newNetMonthly = computeNetInHand(newCtc)

        val netIncreaseMonthly = max(0.0, newNetMonthly - oldNetMonthly)
        val grossIncreaseMonthly = hikeAmountAnnual / 12.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Current CTC", currentCtc, oldNetMonthly, currentCtc - oldNetMonthly * 12, currentCtc))
        scheduleList.add(PaymentScheduleItem(2, "Hike Amount", hikeAmountAnnual, netIncreaseMonthly, hikeAmountAnnual - netIncreaseMonthly * 12, hikeAmountAnnual))
        scheduleList.add(PaymentScheduleItem(3, "New Revised CTC", newCtc, newNetMonthly, newCtc - newNetMonthly * 12, newCtc))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Salary Hike Analysis")
            putExtra("LOAN_AMOUNT", newNetMonthly)
            putExtra("INTEREST_RATE", hikePct.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", "Hike Mode: ${if (isNewRegime) "New Regime" else "Old Regime"}")
            putExtra("EMI", newNetMonthly)
            putExtra("TOTAL_INTEREST", netIncreaseMonthly * 12.0)
            putExtra("TOTAL_COST", newCtc)
            putExtra("PAYOFF_DATE", "Monthly Hike: +₹" + commaFormat.format(netIncreaseMonthly))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etCurrentCtc.setText("10,00,000")
        etHikePercentage.setText("20")
        isNewRegime = true
        highlightHikeChip(chipHike20)
        highlightRegimeChips()
    }
}
