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
import kotlin.math.max

class FreelanceRateEstimatorActivity : BaseInputActivity() {

    private lateinit var etTargetAnnualCtc: EditText
    private lateinit var etAnnualOverhead: EditText
    private lateinit var etBillableHoursPerWeek: EditText
    private lateinit var etUnpaidVacationWeeks: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_freelance_rate_estimator

    override fun getActivityTitle(): String = "Freelance / Contractor Estimator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etTargetAnnualCtc = findViewById(R.id.etTargetAnnualCtc)
        etAnnualOverhead = findViewById(R.id.etAnnualOverhead)
        etBillableHoursPerWeek = findViewById(R.id.etBillableHoursPerWeek)
        etUnpaidVacationWeeks = findViewById(R.id.etUnpaidVacationWeeks)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etTargetAnnualCtc)
        setupCommaFormatting(etAnnualOverhead)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateFreelanceRateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etTargetAnnualCtc.setText("15,00,000")
        etAnnualOverhead.setText("1,50,000")
        etBillableHoursPerWeek.setText("30")
        etUnpaidVacationWeeks.setText("4")
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

    private fun calculateFreelanceRateAndNavigate() {
        val targetCtc = etTargetAnnualCtc.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val overhead = etAnnualOverhead.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val hoursPerWeek = etBillableHoursPerWeek.text.toString().toIntOrNull() ?: 0
        val vacationWeeks = etUnpaidVacationWeeks.text.toString().toIntOrNull() ?: 0

        if (targetCtc <= 0 || hoursPerWeek <= 0) {
            Toast.makeText(this, "Please enter valid income and hours", Toast.LENGTH_SHORT).show()
            return
        }

        val workingWeeks = max(1, 52 - vacationWeeks)
        val annualBillableHours = workingWeeks * hoursPerWeek
        val totalRevenueNeeded = targetCtc + overhead

        val hourlyRate = totalRevenueNeeded / annualBillableHours
        val dayRate = hourlyRate * 8.0
        val monthlyRevenue = totalRevenueNeeded / 12.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Target Take-Home Income", targetCtc, targetCtc / 12.0, (targetCtc / totalRevenueNeeded) * 100, targetCtc))
        scheduleList.add(PaymentScheduleItem(2, "Annual Business Overhead", overhead, overhead / 12.0, (overhead / totalRevenueNeeded) * 100, overhead))
        scheduleList.add(PaymentScheduleItem(3, "Gross Required Revenue", totalRevenueNeeded, monthlyRevenue, 100.0, totalRevenueNeeded))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Freelance Billing Rate Result")
            putExtra("LOAN_AMOUNT", totalRevenueNeeded)
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Hourly: ₹${commaFormat.format(hourlyRate.toInt())} | Day (8h): ₹${commaFormat.format(dayRate.toInt())}")
            putExtra("EMI", monthlyRevenue)
            putExtra("TOTAL_INTEREST", overhead)
            putExtra("TOTAL_COST", totalRevenueNeeded)
            putExtra("PAYOFF_DATE", "Min Hourly Rate: ₹" + commaFormat.format(hourlyRate.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etTargetAnnualCtc.setText("15,00,000")
        etAnnualOverhead.setText("1,50,000")
        etBillableHoursPerWeek.setText("30")
        etUnpaidVacationWeeks.setText("4")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
