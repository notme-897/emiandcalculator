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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class CriticalIllnessActivity : BaseInputActivity() {

    private lateinit var etAnnualIncome: EditText
    private lateinit var etTreatmentCost: EditText
    private lateinit var etWorkLossYears: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_critical_illness

    override fun getActivityTitle(): String = "Critical Illness Cover"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etAnnualIncome = findViewById(R.id.etAnnualIncome)
        etTreatmentCost = findViewById(R.id.etTreatmentCost)
        etWorkLossYears = findViewById(R.id.etWorkLossYears)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etAnnualIncome)
        setupCommaFormatting(etTreatmentCost)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateCriticalIllnessAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etAnnualIncome.setText("12,00,000")
        etTreatmentCost.setText("15,00,000")
        etWorkLossYears.setText("2")
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
                } catch (e: Exception) {}
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

    private fun calculateCriticalIllnessAndNavigate() {
        val annualIncome = etAnnualIncome.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val treatmentCost = etTreatmentCost.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val years = etWorkLossYears.text.toString().toIntOrNull() ?: 0

        if (annualIncome <= 0 || treatmentCost <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid financial parameters", Toast.LENGTH_SHORT).show()
            return
        }

        val incomeReplacement = annualIncome * years
        val totalRequiredCover = incomeReplacement + treatmentCost

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Est. Advanced Treatment Costs", treatmentCost, treatmentCost / 12.0, (treatmentCost / totalRequiredCover) * 100, treatmentCost))
        scheduleList.add(PaymentScheduleItem(2, "Income Replacement ($years Yrs)", incomeReplacement, incomeReplacement / (years * 12), (incomeReplacement / totalRequiredCover) * 100, incomeReplacement))
        scheduleList.add(PaymentScheduleItem(3, "Total Recommended Sum Insured", totalRequiredCover, totalRequiredCover / 12.0, 100.0, totalRequiredCover))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Critical Illness Cover Result")
            putExtra("LOAN_AMOUNT", totalRequiredCover)
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", years)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Treatment: ₹${commaFormat.format(treatmentCost.toInt())} | Income ($years Yrs): ₹${commaFormat.format(incomeReplacement.toInt())}")
            putExtra("EMI", totalRequiredCover / 12.0)
            putExtra("TOTAL_INTEREST", incomeReplacement)
            putExtra("TOTAL_COST", totalRequiredCover)
            putExtra("PAYOFF_DATE", "Recommended Sum: ₹" + commaFormat.format(totalRequiredCover.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etAnnualIncome.setText("12,00,000")
        etTreatmentCost.setText("15,00,000")
        etWorkLossYears.setText("2")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
