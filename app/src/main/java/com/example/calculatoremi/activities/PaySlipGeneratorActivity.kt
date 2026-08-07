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

class PaySlipGeneratorActivity : BaseInputActivity() {

    private lateinit var etGrossPay: EditText
    private lateinit var chipPay50k: MaterialButton
    private lateinit var chipPay1L: MaterialButton
    private lateinit var chipPay1_5L: MaterialButton
    private lateinit var chipPay2L: MaterialButton

    private lateinit var chipHraMetro: MaterialButton
    private lateinit var chipHraNonMetro: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isMetro = true

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_payslip_generator

    override fun getActivityTitle(): String = "Pay Slip Generator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etGrossPay = findViewById(R.id.etGrossPay)
        chipPay50k = findViewById(R.id.chipPay50k)
        chipPay1L = findViewById(R.id.chipPay1L)
        chipPay1_5L = findViewById(R.id.chipPay1_5L)
        chipPay2L = findViewById(R.id.chipPay2L)

        chipHraMetro = findViewById(R.id.chipHraMetro)
        chipHraNonMetro = findViewById(R.id.chipHraNonMetro)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val allChips = listOf(
            chipPay50k, chipPay1L, chipPay1_5L, chipPay2L,
            chipHraMetro, chipHraNonMetro
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etGrossPay)

        chipPay50k.setOnClickListener { setQuickAmount(50000.0); highlightPayChip(chipPay50k) }
        chipPay1L.setOnClickListener { setQuickAmount(100000.0); highlightPayChip(chipPay1L) }
        chipPay1_5L.setOnClickListener { setQuickAmount(150000.0); highlightPayChip(chipPay1_5L) }
        chipPay2L.setOnClickListener { setQuickAmount(200000.0); highlightPayChip(chipPay2L) }

        chipHraMetro.setOnClickListener {
            isMetro = true
            highlightHraChips()
        }
        chipHraNonMetro.setOnClickListener {
            isMetro = false
            highlightHraChips()
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
        etGrossPay.setText("1,00,000")
        highlightPayChip(chipPay1L)
        highlightHraChips()
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
        etGrossPay.setText(commaFormat.format(amount))
    }

    private fun getRawGross(): Double {
        val raw = etGrossPay.text.toString().replace(",", "")
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

    private fun highlightPayChip(selectedChip: MaterialButton) {
        val chips = listOf(chipPay50k, chipPay1L, chipPay1_5L, chipPay2L)
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

    private fun highlightHraChips() {
        if (isMetro) {
            chipHraMetro.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
            chipHraMetro.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            chipHraMetro.strokeWidth = 0

            chipHraNonMetro.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chipHraNonMetro.setTextColor(Color.parseColor("#1E293B"))
            chipHraNonMetro.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipHraNonMetro.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipHraNonMetro.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
            chipHraNonMetro.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            chipHraNonMetro.strokeWidth = 0

            chipHraMetro.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chipHraMetro.setTextColor(Color.parseColor("#1E293B"))
            chipHraMetro.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipHraMetro.strokeWidth = (1 * resources.displayMetrics.density).toInt()
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
        val grossMonthly = getRawGross()
        if (grossMonthly <= 0) {
            etGrossPay.error = "Please enter valid monthly gross salary"
            return
        }

        // Standard Statutory Split Formula
        val basicPay = grossMonthly * 0.50
        val hraPct = if (isMetro) 0.50 else 0.40
        val hra = basicPay * hraPct
        val lta = grossMonthly * 0.05
        val specialAllowance = (grossMonthly - basicPay - hra - lta).coerceAtLeast(0.0)

        // Statutory Deductions
        val employeePf = 1800.0 // Capped standard EPF
        val pt = 200.0 // Standard Professional Tax

        val totalDeductions = employeePf + pt
        val netTakeHome = (grossMonthly - totalDeductions).coerceAtLeast(0.0)

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)

        // Add 5 Component Rows to Schedule Data List
        schedule.add(PaymentScheduleItem(1, "Basic Salary (50%)", grossMonthly, basicPay, 0.0, netTakeHome))
        schedule.add(PaymentScheduleItem(2, "HRA Allowance (${if (isMetro) "50%" else "40%"})", grossMonthly, hra, 0.0, netTakeHome))
        schedule.add(PaymentScheduleItem(3, "Special Allowance", grossMonthly, specialAllowance, 0.0, netTakeHome))
        schedule.add(PaymentScheduleItem(4, "Leave Travel Allowance (LTA 5%)", grossMonthly, lta, 0.0, netTakeHome))
        schedule.add(PaymentScheduleItem(5, "Employee PF & PT Deduction", grossMonthly, 0.0, totalDeductions, netTakeHome))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Pay Slip Components")
            putExtra("LOAN_AMOUNT", grossMonthly)
            putExtra("INTEREST_RATE", ((totalDeductions / grossMonthly) * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", netTakeHome)
            putExtra("TOTAL_INTEREST", totalDeductions * 12.0)
            putExtra("TOTAL_COST", grossMonthly * 12.0)
            putExtra("PAYOFF_DATE", "Basic: ₹" + commaFormat.format(basicPay))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etGrossPay.setText("1,00,000")
        isMetro = true

        highlightPayChip(chipPay1L)
        highlightHraChips()

        findViewById<NestedScrollView>(R.id.centerBodyLayout).smoothScrollTo(0, 0)
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
