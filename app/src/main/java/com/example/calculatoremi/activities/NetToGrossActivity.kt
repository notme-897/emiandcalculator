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
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class NetToGrossActivity : BaseInputActivity() {

    private lateinit var etTargetNet: EditText
    private lateinit var chipNet40k: MaterialButton
    private lateinit var chipNet75k: MaterialButton
    private lateinit var chipNet1L: MaterialButton
    private lateinit var chipNet1_5L: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_net_to_gross

    override fun getActivityTitle(): String = "Net-to-Gross (Reverse)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etTargetNet = findViewById(R.id.etTargetNet)
        chipNet40k = findViewById(R.id.chipNet40k)
        chipNet75k = findViewById(R.id.chipNet75k)
        chipNet1L = findViewById(R.id.chipNet1L)
        chipNet1_5L = findViewById(R.id.chipNet1_5L)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val allChips = listOf(chipNet40k, chipNet75k, chipNet1L, chipNet1_5L)
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etTargetNet)

        chipNet40k.setOnClickListener { setQuickAmount(40000.0); highlightNetChip(chipNet40k) }
        chipNet75k.setOnClickListener { setQuickAmount(75000.0); highlightNetChip(chipNet75k) }
        chipNet1L.setOnClickListener { setQuickAmount(100000.0); highlightNetChip(chipNet1L) }
        chipNet1_5L.setOnClickListener { setQuickAmount(150000.0); highlightNetChip(chipNet1_5L) }

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
        etTargetNet.setText("75,000")
        highlightNetChip(chipNet75k)
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

    private fun setQuickAmount(amount: Double) {
        etTargetNet.setText(commaFormat.format(amount))
    }

    private fun getRawNet(): Double {
        val raw = etTargetNet.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
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

    private fun highlightNetChip(selectedChip: MaterialButton) {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipNet40k, chipNet75k, chipNet1L, chipNet1_5L)
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
        val targetNetMonthly = getRawNet()
        if (targetNetMonthly <= 0) {
            etTargetNet.error = "Please enter valid target net in-hand"
            return
        }

        // Reverse Search for Required Gross CTC
        var lowCtc = targetNetMonthly * 12.0
        var highCtc = targetNetMonthly * 12.0 * 2.0
        var requiredCtc = lowCtc

        // Binary Search Convergence over 25 iterations
        for (i in 0..25) {
            val midCtc = (lowCtc + highCtc) / 2.0
            val computedNetMonthly = computeNetMonthlyFromCtc(midCtc)
            if (computedNetMonthly < targetNetMonthly) {
                lowCtc = midCtc
            } else {
                highCtc = midCtc
            }
            requiredCtc = midCtc
        }

        val requiredMonthlyGross = requiredCtc / 12.0
        val monthlyDeductions = (requiredMonthlyGross - targetNetMonthly).coerceAtLeast(0.0)

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val calendar = Calendar.getInstance()

        for (m in 1..12) {
            calendar.add(Calendar.MONTH, 1)
            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(calendar.time),
                    emi = targetNetMonthly,
                    principal = requiredMonthlyGross,
                    interest = monthlyDeductions,
                    balance = (targetNetMonthly * 12.0) - (targetNetMonthly * m)
                )
            )
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Net-to-Gross Result")
            putExtra("LOAN_AMOUNT", requiredCtc)
            putExtra("INTEREST_RATE", ((monthlyDeductions / requiredMonthlyGross) * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", targetNetMonthly)
            putExtra("TOTAL_INTEREST", monthlyDeductions * 12.0)
            putExtra("TOTAL_COST", requiredCtc)
            putExtra("PAYOFF_DATE", "Monthly Gross: ₹" + commaFormat.format(requiredMonthlyGross.toInt()))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun computeNetMonthlyFromCtc(annualCtc: Double): Double {
        val monthlyGross = annualCtc / 12.0
        val monthlyBasic = monthlyGross * 0.50
        val monthlyEmpPf = 1800.0 // Standard Capped EPF
        val monthlyPt = 200.0

        val stdDeduction = 75000.0
        val taxableIncome = (annualCtc - (monthlyEmpPf * 12.0) - stdDeduction).coerceAtLeast(0.0)

        var tax = 0.0
        var rem = taxableIncome
        if (rem > 700000.0) {
            if (rem > 1500000) { tax += (rem - 1500000) * 0.30; rem = 1500000.0 }
            if (rem > 1200000) { tax += (rem - 1200000) * 0.20; rem = 1200000.0 }
            if (rem > 1000000) { tax += (rem - 1000000) * 0.15; rem = 1000000.0 }
            if (rem > 700000) { tax += (rem - 700000) * 0.10; rem = 700000.0 }
            if (rem > 300000) { tax += (rem - 300000) * 0.05; rem = 300000.0 }
            tax *= 1.04
        }

        val monthlyTds = tax / 12.0
        return (monthlyGross - monthlyEmpPf - monthlyPt - monthlyTds).coerceAtLeast(0.0)
    }

    private fun resetFields() {
        etTargetNet.setText("75,000")
        highlightNetChip(chipNet75k)

        findViewById<NestedScrollView>(R.id.centerBodyLayout).smoothScrollTo(0, 0)
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
