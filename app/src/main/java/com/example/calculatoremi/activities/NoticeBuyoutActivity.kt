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

class NoticeBuyoutActivity : BaseInputActivity() {

    private lateinit var etMonthlyGross: EditText
    private lateinit var etTotalNoticeDays: EditText
    private lateinit var etShortfallDays: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_notice_buyout

    override fun getActivityTitle(): String = "Notice Period Buyout Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etMonthlyGross = findViewById(R.id.etMonthlyGross)
        etTotalNoticeDays = findViewById(R.id.etTotalNoticeDays)
        etShortfallDays = findViewById(R.id.etShortfallDays)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etMonthlyGross)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateBuyoutAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etMonthlyGross.setText("1,00,000")
        etTotalNoticeDays.setText("90")
        etShortfallDays.setText("30")
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

    private fun calculateBuyoutAndNavigate() {
        val grossStr = etMonthlyGross.text.toString().replace(",", "")
        val totalDaysStr = etTotalNoticeDays.text.toString()
        val shortfallDaysStr = etShortfallDays.text.toString()

        val monthlyGross = grossStr.toDoubleOrNull() ?: 0.0
        val totalNoticeDays = totalDaysStr.toIntOrNull() ?: 90
        val shortfallDays = shortfallDaysStr.toIntOrNull() ?: 30

        if (monthlyGross <= 0 || totalNoticeDays <= 0) {
            Toast.makeText(this, "Please enter valid gross salary and notice days", Toast.LENGTH_SHORT).show()
            return
        }

        val dailyPayRate = monthlyGross / 30.0
        val servedDays = maxOf(0, totalNoticeDays - shortfallDays)

        val servedPay = servedDays * dailyPayRate
        val buyoutRecoveryAmount = shortfallDays * dailyPayRate

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Served Days Salary ($servedDays Days)", servedPay, servedPay / 12.0, 0.0, servedPay))
        scheduleList.add(PaymentScheduleItem(2, "Buyout Recovery Amount ($shortfallDays Days)", buyoutRecoveryAmount, buyoutRecoveryAmount / 12.0, 0.0, buyoutRecoveryAmount))
        scheduleList.add(PaymentScheduleItem(3, "Total Notice Pay equivalent", monthlyGross * (totalNoticeDays / 30.0), monthlyGross, 0.0, monthlyGross * (totalNoticeDays / 30.0)))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Notice Buyout Result")
            putExtra("LOAN_AMOUNT", buyoutRecoveryAmount) // Buyout Cost
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", "$shortfallDays Days Shortfall ($servedDays Days Served)")
            putExtra("EMI", buyoutRecoveryAmount)
            putExtra("TOTAL_INTEREST", buyoutRecoveryAmount)
            putExtra("TOTAL_COST", servedPay)
            putExtra("PAYOFF_DATE", "Buyout Amount: ₹" + commaFormat.format(buyoutRecoveryAmount.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etMonthlyGross.setText("1,00,000")
        etTotalNoticeDays.setText("90")
        etShortfallDays.setText("30")
    }
}
