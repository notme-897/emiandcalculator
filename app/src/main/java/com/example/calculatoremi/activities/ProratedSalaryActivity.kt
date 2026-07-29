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

class ProratedSalaryActivity : BaseInputActivity() {

    private lateinit var etFullMonthlySalary: EditText
    private lateinit var etTotalMonthDays: EditText
    private lateinit var etDaysWorked: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_prorated_salary

    override fun getActivityTitle(): String = "Prorated Salary Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etFullMonthlySalary = findViewById(R.id.etFullMonthlySalary)
        etTotalMonthDays = findViewById(R.id.etTotalMonthDays)
        etDaysWorked = findViewById(R.id.etDaysWorked)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etFullMonthlySalary)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateProratedPayAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etFullMonthlySalary.setText("75,000")
        etTotalMonthDays.setText("30")
        etDaysWorked.setText("12")
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

    private fun calculateProratedPayAndNavigate() {
        val fullSalaryStr = etFullMonthlySalary.text.toString().replace(",", "")
        val totalDaysStr = etTotalMonthDays.text.toString()
        val daysWorkedStr = etDaysWorked.text.toString()

        val fullSalary = fullSalaryStr.toDoubleOrNull() ?: 0.0
        val totalDays = totalDaysStr.toDoubleOrNull() ?: 30.0
        val daysWorked = daysWorkedStr.toDoubleOrNull() ?: 0.0

        if (fullSalary <= 0) {
            Toast.makeText(this, "Please enter a valid full monthly salary", Toast.LENGTH_SHORT).show()
            return
        }
        if (totalDays <= 0 || daysWorked < 0 || daysWorked > totalDays) {
            Toast.makeText(this, "Please enter valid days worked (0 to total month days)", Toast.LENGTH_SHORT).show()
            return
        }

        val dailySalaryRate = fullSalary / totalDays
        val proratedEarnedPay = dailySalaryRate * daysWorked
        val unpaidDeductionAmount = fullSalary - proratedEarnedPay

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Full Monthly Salary", fullSalary, fullSalary, 0.0, fullSalary))
        scheduleList.add(PaymentScheduleItem(2, "Daily Salary Rate", dailySalaryRate, dailySalaryRate * 30.0, 0.0, dailySalaryRate))
        scheduleList.add(PaymentScheduleItem(3, "Days Worked Payout", proratedEarnedPay, proratedEarnedPay, 0.0, proratedEarnedPay))
        scheduleList.add(PaymentScheduleItem(4, "Unpaid Days Deduction", unpaidDeductionAmount, unpaidDeductionAmount, 0.0, unpaidDeductionAmount))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Prorated Partial Salary Result")
            putExtra("LOAN_AMOUNT", proratedEarnedPay) // Earned Pay
            putExtra("INTEREST_RATE", (daysWorked / totalDays * 100.0).toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", "Worked ${daysWorked.toInt()} of ${totalDays.toInt()} Days")
            putExtra("EMI", proratedEarnedPay)
            putExtra("TOTAL_INTEREST", unpaidDeductionAmount)
            putExtra("TOTAL_COST", fullSalary)
            putExtra("PAYOFF_DATE", "Prorated Payout: ₹" + commaFormat.format(proratedEarnedPay))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etFullMonthlySalary.setText("75,000")
        etTotalMonthDays.setText("30")
        etDaysWorked.setText("12")
    }
}
