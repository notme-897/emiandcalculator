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
import kotlin.math.pow

class PrepaymentSimulatorActivity : BaseInputActivity() {

    private lateinit var etBalance: EditText
    private lateinit var etRate: EditText
    private lateinit var etMonths: EditText
    private lateinit var etExtraPay: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_prepayment_simulator

    override fun getActivityTitle(): String = "Loan Prepayment Simulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etBalance = findViewById(R.id.etBalance)
        etRate = findViewById(R.id.etRate)
        etMonths = findViewById(R.id.etMonths)
        etExtraPay = findViewById(R.id.etExtraPay)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etBalance)
        setupCommaFormatting(etExtraPay)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            runSimulationAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etBalance.setText("10,00,000")
        etRate.setText("9.5")
        etMonths.setText("60")
        etExtraPay.setText("5,000")
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

    private fun runSimulationAndNavigate() {
        val balanceStr = etBalance.text.toString().replace(",", "")
        val rateStr = etRate.text.toString()
        val monthsStr = etMonths.text.toString()
        val extraStr = etExtraPay.text.toString().replace(",", "")

        val balance = balanceStr.toDoubleOrNull() ?: 0.0
        val rate = rateStr.toDoubleOrNull() ?: 0.0
        val originalMonths = monthsStr.toIntOrNull() ?: 0
        val extraPay = extraStr.toDoubleOrNull() ?: 0.0

        if (balance <= 0 || rate <= 0 || originalMonths <= 0) {
            Toast.makeText(this, "Please enter valid balance, rate, and months", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = rate / (12 * 100)
        val originalEmi = (balance * monthlyRate * (1 + monthlyRate).pow(originalMonths.toDouble())) /
                ((1 + monthlyRate).pow(originalMonths.toDouble()) - 1)
        val originalTotalInterest = (originalEmi * originalMonths) - balance

        val newMonthlyPayment = originalEmi + extraPay

        var remBalance = balance
        var newTotalInterest = 0.0
        var newMonthsCount = 0

        val scheduleList = ArrayList<PaymentScheduleItem>()

        while (remBalance > 0 && newMonthsCount < originalMonths * 2) {
            val monthInterest = remBalance * monthlyRate
            val monthPrincipal = newMonthlyPayment - monthInterest
            remBalance -= monthPrincipal
            newTotalInterest += monthInterest
            newMonthsCount++

            val balanceEndMonth = max(0.0, remBalance)
            scheduleList.add(PaymentScheduleItem(newMonthsCount, "Month $newMonthsCount (Prepayment)", newMonthlyPayment, monthInterest, monthPrincipal, balanceEndMonth))

            if (remBalance <= 0) break
        }

        val interestSaved = max(0.0, originalTotalInterest - newTotalInterest)
        val monthsSaved = max(0, originalMonths - newMonthsCount)

        val statusText = "Saved ₹${commaFormat.format(interestSaved.toInt())} | Tenure Shortened by $monthsSaved Months"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Prepayment Simulation Result")
            putExtra("LOAN_AMOUNT", balance)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", (newMonthsCount / 12))
            putExtra("LOAN_TERM_MONTHS", newMonthsCount)
            putExtra("START_DATE", statusText)
            putExtra("EMI", newMonthlyPayment)
            putExtra("TOTAL_INTEREST", newTotalInterest)
            putExtra("TOTAL_COST", balance + newTotalInterest)
            putExtra("PAYOFF_DATE", "Total Interest Saved: ₹" + commaFormat.format(interestSaved.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etBalance.setText("10,00,000")
        etRate.setText("9.5")
        etMonths.setText("60")
        etExtraPay.setText("5,000")
    }
}
