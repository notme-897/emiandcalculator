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
import kotlin.math.pow

class InflationCalculatorActivity : BaseInputActivity() {

    private lateinit var etTodayAmount: EditText
    private lateinit var etInflationRate: EditText
    private lateinit var etHorizonYears: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_inflation_calculator

    override fun getActivityTitle(): String = "Inflation & Future Value"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etTodayAmount = findViewById(R.id.etTodayAmount)
        etInflationRate = findViewById(R.id.etInflationRate)
        etHorizonYears = findViewById(R.id.etHorizonYears)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etTodayAmount)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateInflationAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etTodayAmount.setText("1,00,000")
        etInflationRate.setText("6.0")
        etHorizonYears.setText("15")
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

    private fun calculateInflationAndNavigate() {
        val todayAmount = etTodayAmount.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val rate = etInflationRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etHorizonYears.text.toString().toIntOrNull() ?: 0

        if (todayAmount <= 0 || rate <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid today's amount, inflation rate, and years", Toast.LENGTH_SHORT).show()
            return
        }

        val futureCost = todayAmount * (1.0 + (rate / 100.0)).pow(years.toDouble())
        val inflationLoss = futureCost - todayAmount

        val scheduleList = ArrayList<PaymentScheduleItem>()
        var currentCost = todayAmount
        for (y in 1..years) {
            currentCost *= (1.0 + (rate / 100.0))
            val cumLoss = currentCost - todayAmount
            scheduleList.add(PaymentScheduleItem(y, "Year $y Cost", currentCost, currentCost / 12.0, cumLoss, currentCost))
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Inflation & Future Cost Result")
            putExtra("LOAN_AMOUNT", futureCost)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", years)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Cost in $years Years @ $rate% Inflation")
            putExtra("EMI", futureCost / (years * 12.0))
            putExtra("TOTAL_INTEREST", inflationLoss)
            putExtra("TOTAL_COST", futureCost)
            putExtra("PAYOFF_DATE", "Future Cost: ₹" + commaFormat.format(futureCost.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etTodayAmount.setText("1,00,000")
        etInflationRate.setText("6.0")
        etHorizonYears.setText("15")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
