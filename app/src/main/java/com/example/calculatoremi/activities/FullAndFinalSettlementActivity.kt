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

class FullAndFinalSettlementActivity : BaseInputActivity() {

    private lateinit var etMonthlyGross: EditText
    private lateinit var etWorkedDays: EditText
    private lateinit var etLeaveDays: EditText
    private lateinit var etPendingBonus: EditText
    private lateinit var etNoticeRecovery: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_full_and_final_settlement

    override fun getActivityTitle(): String = "Full & Final Settlement"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etMonthlyGross = findViewById(R.id.etMonthlyGross)
        etWorkedDays = findViewById(R.id.etWorkedDays)
        etLeaveDays = findViewById(R.id.etLeaveDays)
        etPendingBonus = findViewById(R.id.etPendingBonus)
        etNoticeRecovery = findViewById(R.id.etNoticeRecovery)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etMonthlyGross)
        setupCommaFormatting(etPendingBonus)
        setupCommaFormatting(etNoticeRecovery)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateFnFAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etMonthlyGross.setText("80,000")
        etWorkedDays.setText("15")
        etLeaveDays.setText("12")
        etPendingBonus.setText("20,000")
        etNoticeRecovery.setText("0")
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

    private fun calculateFnFAndNavigate() {
        val grossStr = etMonthlyGross.text.toString().replace(",", "")
        val workedDaysStr = etWorkedDays.text.toString()
        val leaveDaysStr = etLeaveDays.text.toString()
        val bonusStr = etPendingBonus.text.toString().replace(",", "")
        val recoveryStr = etNoticeRecovery.text.toString().replace(",", "")

        val monthlyGross = grossStr.toDoubleOrNull() ?: 0.0
        val workedDays = workedDaysStr.toIntOrNull() ?: 15
        val leaveDays = leaveDaysStr.toIntOrNull() ?: 12
        val pendingBonus = bonusStr.toDoubleOrNull() ?: 0.0
        val noticeRecovery = recoveryStr.toDoubleOrNull() ?: 0.0

        if (monthlyGross <= 0) {
            Toast.makeText(this, "Please enter a valid monthly gross salary", Toast.LENGTH_SHORT).show()
            return
        }

        val dailyGrossRate = monthlyGross / 30.0
        val workedDaysSalary = workedDays * dailyGrossRate

        // Leave Encashment Rate = (Basic + DA) / 30. Basic is approx 50% of Gross
        val leaveEncashmentRate = (monthlyGross * 0.5) / 30.0
        val leaveEncashmentPay = leaveDays * leaveEncashmentRate

        val totalGrossFnF = workedDaysSalary + leaveEncashmentPay + pendingBonus
        val netFnFPayout = max(0.0, totalGrossFnF - noticeRecovery)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Unpaid Worked Salary ($workedDays Days)", workedDaysSalary, workedDaysSalary / 12.0, 0.0, workedDaysSalary))
        scheduleList.add(PaymentScheduleItem(2, "Leave Encashment ($leaveDays Days)", leaveEncashmentPay, leaveEncashmentPay / 12.0, 0.0, leaveEncashmentPay))
        scheduleList.add(PaymentScheduleItem(3, "Unpaid Bonus / Performance Arrears", pendingBonus, pendingBonus / 12.0, 0.0, pendingBonus))
        scheduleList.add(PaymentScheduleItem(4, "Notice Recovery Deductions", noticeRecovery, noticeRecovery / 12.0, 0.0, noticeRecovery))
        scheduleList.add(PaymentScheduleItem(5, "Net F&F In-Hand Settlement", netFnFPayout, netFnFPayout / 12.0, 0.0, netFnFPayout))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Full & Final Settlement Result")
            putExtra("LOAN_AMOUNT", netFnFPayout) // Net Settlement
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", "Gross F&F: ₹${commaFormat.format(totalGrossFnF.toInt())} | Deductions: ₹${commaFormat.format(noticeRecovery.toInt())}")
            putExtra("EMI", netFnFPayout)
            putExtra("TOTAL_INTEREST", totalGrossFnF)
            putExtra("TOTAL_COST", noticeRecovery)
            putExtra("PAYOFF_DATE", "Net F&F In-Hand: ₹" + commaFormat.format(netFnFPayout.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etMonthlyGross.setText("80,000")
        etWorkedDays.setText("15")
        etLeaveDays.setText("12")
        etPendingBonus.setText("20,000")
        etNoticeRecovery.setText("0")
    }
}
