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
import kotlin.math.min

class GrossToNetActivity : BaseInputActivity() {

    private lateinit var etGrossMonthly: EditText
    private lateinit var chipGross30k: MaterialButton
    private lateinit var chipGross50k: MaterialButton
    private lateinit var chipGross80k: MaterialButton
    private lateinit var chipGross1_5L: MaterialButton

    private lateinit var chipPfFull: MaterialButton
    private lateinit var chipPfCapped: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isPfCapped = true

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_gross_to_net

    override fun getActivityTitle(): String = "Gross to Net Salary"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etGrossMonthly = findViewById(R.id.etGrossMonthly)
        chipGross30k = findViewById(R.id.chipGross30k)
        chipGross50k = findViewById(R.id.chipGross50k)
        chipGross80k = findViewById(R.id.chipGross80k)
        chipGross1_5L = findViewById(R.id.chipGross1_5L)

        chipPfFull = findViewById(R.id.chipPfFull)
        chipPfCapped = findViewById(R.id.chipPfCapped)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val allChips = listOf(
            chipGross30k, chipGross50k, chipGross80k, chipGross1_5L,
            chipPfFull, chipPfCapped
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etGrossMonthly)

        chipGross30k.setOnClickListener { setQuickAmount(30000.0); highlightGrossChip(chipGross30k) }
        chipGross50k.setOnClickListener { setQuickAmount(50000.0); highlightGrossChip(chipGross50k) }
        chipGross80k.setOnClickListener { setQuickAmount(80000.0); highlightGrossChip(chipGross80k) }
        chipGross1_5L.setOnClickListener { setQuickAmount(150000.0); highlightGrossChip(chipGross1_5L) }

        chipPfCapped.setOnClickListener {
            isPfCapped = true
            highlightPfChips()
        }
        chipPfFull.setOnClickListener {
            isPfCapped = false
            highlightPfChips()
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
        etGrossMonthly.setText("80,000")
        highlightGrossChip(chipGross80k)
        highlightPfChips()
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
        etGrossMonthly.setText(commaFormat.format(amount))
    }

    private fun getRawGross(): Double {
        val raw = etGrossMonthly.text.toString().replace(",", "")
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

    private fun highlightGrossChip(selectedChip: MaterialButton) {
        val chips = listOf(chipGross30k, chipGross50k, chipGross80k, chipGross1_5L)
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

    private fun highlightPfChips() {
        if (isPfCapped) {
            chipPfCapped.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
            chipPfCapped.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            chipPfCapped.strokeWidth = 0

            chipPfFull.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chipPfFull.setTextColor(Color.parseColor("#1E293B"))
            chipPfFull.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipPfFull.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipPfFull.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
            chipPfFull.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            chipPfFull.strokeWidth = 0

            chipPfCapped.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chipPfCapped.setTextColor(Color.parseColor("#1E293B"))
            chipPfCapped.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipPfCapped.strokeWidth = (1 * resources.displayMetrics.density).toInt()
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
            etGrossMonthly.error = "Please enter valid gross monthly salary"
            return
        }

        val basicPay = grossMonthly * 0.50
        val employeePf = if (isPfCapped) 1800.0 else (basicPay * 0.12)
        val pt = 200.0

        // Annualized Tax Calculation (New Regime 2024-25 default)
        val annualGross = grossMonthly * 12.0
        val stdDeduction = 75000.0
        val taxableIncome = (annualGross - stdDeduction).coerceAtLeast(0.0)

        val annualTax = calculateTax(taxableIncome)
        val monthlyTds = annualTax / 12.0

        val totalMonthlyDeductions = employeePf + pt + monthlyTds
        val netMonthlyInHand = (grossMonthly - totalMonthlyDeductions).coerceAtLeast(0.0)
        val annualNet = netMonthlyInHand * 12.0

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val calendar = Calendar.getInstance()

        for (m in 1..12) {
            calendar.add(Calendar.MONTH, 1)
            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(calendar.time),
                    emi = netMonthlyInHand,
                    principal = basicPay,
                    interest = totalMonthlyDeductions,
                    balance = annualNet - (netMonthlyInHand * m)
                )
            )
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Gross to Net Salary")
            putExtra("LOAN_AMOUNT", annualGross)
            putExtra("INTEREST_RATE", ((totalMonthlyDeductions / grossMonthly) * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", netMonthlyInHand)
            putExtra("TOTAL_INTEREST", totalMonthlyDeductions * 12.0)
            putExtra("TOTAL_COST", annualNet)
            putExtra("PAYOFF_DATE", "Annual Net: ₹" + commaFormat.format(annualNet))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun calculateTax(income: Double): Double {
        if (income <= 700000.0) return 0.0
        var tax = 0.0
        var rem = income

        if (rem > 1500000) { tax += (rem - 1500000) * 0.30; rem = 1500000.0 }
        if (rem > 1200000) { tax += (rem - 1200000) * 0.20; rem = 1200000.0 }
        if (rem > 1000000) { tax += (rem - 1000000) * 0.15; rem = 1000000.0 }
        if (rem > 700000) { tax += (rem - 700000) * 0.10; rem = 700000.0 }
        if (rem > 300000) { tax += (rem - 300000) * 0.05; rem = 300000.0 }

        return tax * 1.04
    }

    private fun resetFields() {
        etGrossMonthly.setText("80,000")
        isPfCapped = true

        highlightGrossChip(chipGross80k)
        highlightPfChips()

        findViewById<NestedScrollView>(R.id.centerBodyLayout).smoothScrollTo(0, 0)
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
