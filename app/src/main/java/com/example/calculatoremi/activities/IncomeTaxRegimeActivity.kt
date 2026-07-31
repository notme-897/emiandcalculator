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
import kotlin.math.abs
import kotlin.math.min

class IncomeTaxRegimeActivity : BaseInputActivity() {

    private lateinit var etGrossSalary: EditText
    private lateinit var etDeduction80C: EditText
    private lateinit var etDeduction80D: EditText
    private lateinit var etHraExemption: EditText
    private lateinit var etOtherDeductions: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_income_tax_regime

    override fun getActivityTitle(): String = "Income Tax Regime Comparator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etGrossSalary = findViewById(R.id.etGrossSalary)
        etDeduction80C = findViewById(R.id.etDeduction80C)
        etDeduction80D = findViewById(R.id.etDeduction80D)
        etHraExemption = findViewById(R.id.etHraExemption)
        etOtherDeductions = findViewById(R.id.etOtherDeductions)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etGrossSalary)
        setupCommaFormatting(etDeduction80C)
        setupCommaFormatting(etDeduction80D)
        setupCommaFormatting(etHraExemption)
        setupCommaFormatting(etOtherDeductions)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            compareRegimesAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etGrossSalary.setText("15,00,000")
        etDeduction80C.setText("1,50,000")
        etDeduction80D.setText("25,000")
        etHraExemption.setText("1,20,000")
        etOtherDeductions.setText("50,000")
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

    private fun computeNewRegimeTax(taxableIncome: Double): Double {
        if (taxableIncome <= 300000) return 0.0
        var tax = 0.0

        // ₹3L - ₹7L @ 5%
        if (taxableIncome > 300000) {
            tax += (min(taxableIncome, 700000.0) - 300000) * 0.05
        }
        // ₹7L - ₹10L @ 10%
        if (taxableIncome > 700000) {
            tax += (min(taxableIncome, 1000000.0) - 700000) * 0.10
        }
        // ₹10L - ₹12L @ 15%
        if (taxableIncome > 1000000) {
            tax += (min(taxableIncome, 1200000.0) - 1000000) * 0.15
        }
        // ₹12L - ₹15L @ 20%
        if (taxableIncome > 1200000) {
            tax += (min(taxableIncome, 1500000.0) - 1200000) * 0.20
        }
        // > ₹15L @ 30%
        if (taxableIncome > 1500000) {
            tax += (taxableIncome - 1500000) * 0.30
        }

        // Section 87A Rebate up to ₹7,00,000 under New Regime
        if (taxableIncome <= 700000) {
            tax = 0.0
        }

        val cess = tax * 0.04
        return tax + cess
    }

    private fun computeOldRegimeTax(taxableIncome: Double): Double {
        if (taxableIncome <= 250000) return 0.0
        var tax = 0.0

        // ₹2.5L - ₹5L @ 5%
        if (taxableIncome > 250000) {
            tax += (min(taxableIncome, 500000.0) - 250000) * 0.05
        }
        // ₹5L - ₹10L @ 20%
        if (taxableIncome > 500000) {
            tax += (min(taxableIncome, 1000000.0) - 500000) * 0.20
        }
        // > ₹10L @ 30%
        if (taxableIncome > 1000000) {
            tax += (taxableIncome - 1000000) * 0.30
        }

        // Section 87A Rebate up to ₹5,00,000 under Old Regime
        if (taxableIncome <= 500000) {
            tax = 0.0
        }

        val cess = tax * 0.04
        return tax + cess
    }

    private fun compareRegimesAndNavigate() {
        val grossSalaryStr = etGrossSalary.text.toString().replace(",", "")
        val grossSalary = grossSalaryStr.toDoubleOrNull() ?: 0.0

        if (grossSalary <= 0) {
            Toast.makeText(this, "Please enter a valid gross annual salary", Toast.LENGTH_SHORT).show()
            return
        }

        val d80C = min(150000.0, etDeduction80C.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0)
        val d80D = etDeduction80D.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val hra = etHraExemption.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val otherD = etOtherDeductions.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        // New Regime Computation
        val stdDeductionNew = 75000.0
        val taxableIncomeNew = abs(grossSalary - stdDeductionNew)
        val totalTaxNew = computeNewRegimeTax(taxableIncomeNew)

        // Old Regime Computation
        val stdDeductionOld = 50000.0
        val totalOldDeductions = stdDeductionOld + d80C + d80D + hra + otherD
        val taxableIncomeOld = abs(grossSalary - totalOldDeductions)
        val totalTaxOld = computeOldRegimeTax(taxableIncomeOld)

        val taxDifference = abs(totalTaxNew - totalTaxOld)

        val recommendationText = if (totalTaxNew < totalTaxOld) {
            "🎉 New Tax Regime saves ₹${commaFormat.format(taxDifference.toInt())} in annual tax!"
        } else if (totalTaxOld < totalTaxNew) {
            "🎉 Old Tax Regime saves ₹${commaFormat.format(taxDifference.toInt())} in annual tax!"
        } else {
            "Both Tax Regimes result in identical tax liability!"
        }

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Gross Annual Salary", grossSalary, grossSalary / 12.0, 0.0, grossSalary))
        scheduleList.add(PaymentScheduleItem(2, "New Regime Taxable Income (Std Ded ₹75k)", taxableIncomeNew, taxableIncomeNew / 12.0, 0.0, taxableIncomeNew))
        scheduleList.add(PaymentScheduleItem(3, "New Regime Total Tax (incl 4% Cess)", totalTaxNew, totalTaxNew / 12.0, 0.0, totalTaxNew))
        scheduleList.add(PaymentScheduleItem(4, "Old Regime Total Deductions (80C/80D/HRA)", totalOldDeductions, totalOldDeductions / 12.0, 0.0, totalOldDeductions))
        scheduleList.add(PaymentScheduleItem(5, "Old Regime Taxable Income", taxableIncomeOld, taxableIncomeOld / 12.0, 0.0, taxableIncomeOld))
        scheduleList.add(PaymentScheduleItem(6, "Old Regime Total Tax (incl 4% Cess)", totalTaxOld, totalTaxOld / 12.0, 0.0, totalTaxOld))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Tax Regime Comparison Result")
            putExtra("LOAN_AMOUNT", totalTaxNew) // New Regime Tax
            putExtra("INTEREST_RATE", ((totalTaxNew / grossSalary) * 100).toFloat()) // Effective Tax %
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", recommendationText)
            putExtra("EMI", totalTaxNew / 12.0)
            putExtra("TOTAL_INTEREST", totalTaxOld)
            putExtra("TOTAL_COST", grossSalary)
            putExtra("PAYOFF_DATE", "Tax Savings: ₹" + commaFormat.format(taxDifference.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etGrossSalary.setText("15,00,000")
        etDeduction80C.setText("1,50,000")
        etDeduction80D.setText("25,000")
        etHraExemption.setText("1,20,000")
        etOtherDeductions.setText("50,000")
    }
}
