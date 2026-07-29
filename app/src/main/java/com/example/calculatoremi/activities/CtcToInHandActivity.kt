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

class CtcToInHandActivity : BaseInputActivity() {

    private lateinit var etAnnualCtc: EditText
    private lateinit var chipCtc6L: MaterialButton
    private lateinit var chipCtc12L: MaterialButton
    private lateinit var chipCtc18L: MaterialButton
    private lateinit var chipCtc25L: MaterialButton

    private lateinit var chipRegimeNew: MaterialButton
    private lateinit var chipRegimeOld: MaterialButton

    private lateinit var chipPfFull: MaterialButton
    private lateinit var chipPfCapped: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isNewRegime = true
    private var isPfCapped = true

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_ctc_to_inhand

    override fun getActivityTitle(): String = "CTC to In-Hand"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etAnnualCtc = findViewById(R.id.etAnnualCtc)
        chipCtc6L = findViewById(R.id.chipCtc6L)
        chipCtc12L = findViewById(R.id.chipCtc12L)
        chipCtc18L = findViewById(R.id.chipCtc18L)
        chipCtc25L = findViewById(R.id.chipCtc25L)

        chipRegimeNew = findViewById(R.id.chipRegimeNew)
        chipRegimeOld = findViewById(R.id.chipRegimeOld)

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
            chipCtc6L, chipCtc12L, chipCtc18L, chipCtc25L,
            chipRegimeNew, chipRegimeOld, chipPfFull, chipPfCapped
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        setupCommaFormatting(etAnnualCtc)

        chipCtc6L.setOnClickListener { setQuickAmount(600000.0); highlightCtcChip(chipCtc6L) }
        chipCtc12L.setOnClickListener { setQuickAmount(1200000.0); highlightCtcChip(chipCtc12L) }
        chipCtc18L.setOnClickListener { setQuickAmount(1800000.0); highlightCtcChip(chipCtc18L) }
        chipCtc25L.setOnClickListener { setQuickAmount(2500000.0); highlightCtcChip(chipCtc25L) }

        chipRegimeNew.setOnClickListener {
            isNewRegime = true
            highlightRegimeChips()
        }
        chipRegimeOld.setOnClickListener {
            isNewRegime = false
            highlightRegimeChips()
        }

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
        etAnnualCtc.setText("12,00,000")
        highlightCtcChip(chipCtc12L)
        highlightRegimeChips()
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
        etAnnualCtc.setText(commaFormat.format(amount))
    }

    private fun getRawCtc(): Double {
        val raw = etAnnualCtc.text.toString().replace(",", "")
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

    private fun highlightCtcChip(selectedChip: MaterialButton) {
        val chips = listOf(chipCtc6L, chipCtc12L, chipCtc18L, chipCtc25L)
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

    private fun highlightPfChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (isPfCapped) {
            chipPfCapped.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipPfCapped.setTextColor(Color.WHITE)
            chipPfCapped.strokeWidth = 0

            chipPfFull.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipPfFull.setTextColor(Color.parseColor("#1E293B"))
            chipPfFull.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipPfFull.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipPfFull.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipPfFull.setTextColor(Color.WHITE)
            chipPfFull.strokeWidth = 0

            chipPfCapped.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
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
        val annualCtc = getRawCtc()
        if (annualCtc <= 0) {
            etAnnualCtc.error = "Please enter valid annual CTC"
            return
        }

        val monthlyGross = annualCtc / 12.0
        val monthlyBasic = monthlyGross * 0.50

        val monthlyEmpPf = if (isPfCapped) 1800.0 else (monthlyBasic * 0.12)
        val monthlyEmployeePf = if (isPfCapped) 1800.0 else (monthlyBasic * 0.12)
        val monthlyPt = 200.0 // Standard Professional Tax

        // Taxable Salary Calculation (New Regime 2024-25 default)
        val stdDeduction = if (isNewRegime) 75000.0 else 50000.0
        val annualEmployerPf = monthlyEmpPf * 12.0
        val taxableIncome = (annualCtc - annualEmployerPf - stdDeduction).coerceAtLeast(0.0)

        val annualTax = if (isNewRegime) calculateNewRegimeTax(taxableIncome) else calculateOldRegimeTax(taxableIncome)
        val monthlyTax = annualTax / 12.0

        val totalMonthlyDeductions = monthlyEmpPf + monthlyEmployeePf + monthlyPt + monthlyTax
        val netMonthlyInHand = (monthlyGross - monthlyEmpPf - monthlyEmployeePf - monthlyPt - monthlyTax).coerceAtLeast(0.0)
        val annualInHand = netMonthlyInHand * 12.0

        val schedule = ArrayList<PaymentScheduleItem>()
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val calendar = Calendar.getInstance()

        // Generate 12-Month Component Breakup
        for (m in 1..12) {
            calendar.add(Calendar.MONTH, 1)
            schedule.add(
                PaymentScheduleItem(
                    emiNo = m,
                    date = dateFormatter.format(calendar.time),
                    emi = netMonthlyInHand,
                    principal = monthlyBasic,
                    interest = monthlyTax + monthlyPt + monthlyEmployeePf,
                    balance = annualInHand - (netMonthlyInHand * m)
                )
            )
        }

        val titleText = if (isNewRegime) "In-Hand Salary (New Tax Regime)" else "In-Hand Salary (Old Tax Regime)"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", annualCtc)
            putExtra("INTEREST_RATE", ((totalMonthlyDeductions / monthlyGross) * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", dateFormatter.format(Date()))
            putExtra("EMI", netMonthlyInHand)
            putExtra("TOTAL_INTEREST", totalMonthlyDeductions * 12.0)
            putExtra("TOTAL_COST", annualInHand)
            putExtra("PAYOFF_DATE", "Annual Total: ₹" + commaFormat.format(annualInHand))
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun calculateNewRegimeTax(income: Double): Double {
        if (income <= 700000.0) return 0.0 // Sec 87A rebate makes tax 0 up to 7L
        var tax = 0.0
        var rem = income

        if (rem > 1500000) { tax += (rem - 1500000) * 0.30; rem = 1500000.0 }
        if (rem > 1200000) { tax += (rem - 1200000) * 0.20; rem = 1200000.0 }
        if (rem > 1000000) { tax += (rem - 1000000) * 0.15; rem = 1000000.0 }
        if (rem > 700000) { tax += (rem - 700000) * 0.10; rem = 700000.0 }
        if (rem > 300000) { tax += (rem - 300000) * 0.05; rem = 300000.0 }

        return tax * 1.04 // 4% Health & Education Cess
    }

    private fun calculateOldRegimeTax(income: Double): Double {
        if (income <= 500000.0) return 0.0 // Sec 87A rebate
        var tax = 0.0
        var rem = income

        if (rem > 1000000) { tax += (rem - 1000000) * 0.30; rem = 1000000.0 }
        if (rem > 500000) { tax += (rem - 500000) * 0.20; rem = 500000.0 }
        if (rem > 250000) { tax += (rem - 250000) * 0.05; rem = 250000.0 }

        return tax * 1.04
    }

    private fun resetFields() {
        etAnnualCtc.setText("12,00,000")
        isNewRegime = true
        isPfCapped = true

        highlightCtcChip(chipCtc12L)
        highlightRegimeChips()
        highlightPfChips()

        findViewById<NestedScrollView>(R.id.centerBodyLayout).smoothScrollTo(0, 0)
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}
