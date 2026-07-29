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

class HourlyToAnnualActivity : BaseInputActivity() {

    private lateinit var etHourlyRate: EditText
    private lateinit var etHoursPerWeek: EditText
    private lateinit var etWeeksPerYear: EditText
    private lateinit var etPtoDays: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_hourly_to_annual

    override fun getActivityTitle(): String = "Hourly to Annual Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etHourlyRate = findViewById(R.id.etHourlyRate)
        etHoursPerWeek = findViewById(R.id.etHoursPerWeek)
        etWeeksPerYear = findViewById(R.id.etWeeksPerYear)
        etPtoDays = findViewById(R.id.etPtoDays)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etHourlyRate)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAnnualPayAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etHourlyRate.setText("500")
        etHoursPerWeek.setText("40")
        etWeeksPerYear.setText("52")
        etPtoDays.setText("15")
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

    private fun calculateAnnualPayAndNavigate() {
        val hourlyRateStr = etHourlyRate.text.toString().replace(",", "")
        val hoursPerWeekStr = etHoursPerWeek.text.toString()
        val weeksPerYearStr = etWeeksPerYear.text.toString()
        val ptoDaysStr = etPtoDays.text.toString()

        val hourlyRate = hourlyRateStr.toDoubleOrNull() ?: 0.0
        val hoursPerWeek = hoursPerWeekStr.toDoubleOrNull() ?: 40.0
        val weeksPerYear = weeksPerYearStr.toDoubleOrNull() ?: 52.0
        val ptoDays = ptoDaysStr.toDoubleOrNull() ?: 0.0

        if (hourlyRate <= 0) {
            Toast.makeText(this, "Please enter a valid hourly pay rate", Toast.LENGTH_SHORT).show()
            return
        }

        val totalGrossBillableHoursAnnual = max(0.0, (hoursPerWeek * weeksPerYear) - (ptoDays * (hoursPerWeek / 5.0)))
        val annualGrossSalary = totalGrossBillableHoursAnnual * hourlyRate
        val monthlyGrossSalary = annualGrossSalary / 12.0
        val weeklyGrossSalary = annualGrossSalary / weeksPerYear

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Hourly Rate", hourlyRate, hourlyRate * hoursPerWeek, 0.0, hourlyRate))
        scheduleList.add(PaymentScheduleItem(2, "Weekly Pay", weeklyGrossSalary, weeklyGrossSalary * 4.33, 0.0, weeklyGrossSalary))
        scheduleList.add(PaymentScheduleItem(3, "Monthly Gross", monthlyGrossSalary, monthlyGrossSalary * 12.0, 0.0, monthlyGrossSalary))
        scheduleList.add(PaymentScheduleItem(4, "Annual Total Pay", annualGrossSalary, annualGrossSalary, 0.0, annualGrossSalary))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Hourly Salary Calculation")
            putExtra("LOAN_AMOUNT", monthlyGrossSalary) // Monthly Pay
            putExtra("INTEREST_RATE", hourlyRate.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", "Billable Hours: ${totalGrossBillableHoursAnnual.toInt()} hrs/yr")
            putExtra("EMI", monthlyGrossSalary)
            putExtra("TOTAL_INTEREST", 0.0)
            putExtra("TOTAL_COST", annualGrossSalary)
            putExtra("PAYOFF_DATE", "Annual Gross: ₹" + commaFormat.format(annualGrossSalary))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etHourlyRate.setText("500")
        etHoursPerWeek.setText("40")
        etWeeksPerYear.setText("52")
        etPtoDays.setText("15")
    }
}
