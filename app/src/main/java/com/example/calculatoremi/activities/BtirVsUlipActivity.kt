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
import kotlin.math.pow

class BtirVsUlipActivity : BaseInputActivity() {

    private lateinit var etAnnualBudget: EditText
    private lateinit var etTenureYears: EditText
    private lateinit var etEquityReturn: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_btir_vs_ulip

    override fun getActivityTitle(): String = "BTIR vs ULIP Comparator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etAnnualBudget = findViewById(R.id.etAnnualBudget)
        etTenureYears = findViewById(R.id.etTenureYears)
        etEquityReturn = findViewById(R.id.etEquityReturn)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etAnnualBudget)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            compareBtirVsUlipAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etAnnualBudget.setText("1,00,000")
        etTenureYears.setText("15")
        etEquityReturn.setText("12.0")
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

    private fun compareBtirVsUlipAndNavigate() {
        val annualBudget = etAnnualBudget.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val years = etTenureYears.text.toString().toIntOrNull() ?: 0
        val equityReturn = (etEquityReturn.text.toString().toDoubleOrNull() ?: 0.0) / 100.0

        if (annualBudget <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid budget and tenure", Toast.LENGTH_SHORT).show()
            return
        }

        val termPremium = max(10000.0, annualBudget * 0.12)
        val btirSipAmount = max(0.0, annualBudget - termPremium)

        val monthlyRate = equityReturn / 12.0
        val months = years * 12

        val btirCorpus = if (monthlyRate > 0) {
            (btirSipAmount / 12.0) * (((1.0 + monthlyRate).pow(months.toDouble()) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
        } else {
            btirSipAmount * years
        }

        val ulipFeeFactor = 0.88
        val ulipInvested = (annualBudget / 12.0) * ulipFeeFactor
        val ulipCorpus = if (monthlyRate > 0) {
            ulipInvested * (((1.0 + (monthlyRate * 0.85)).pow(months.toDouble()) - 1.0) / (monthlyRate * 0.85)) * (1.0 + (monthlyRate * 0.85))
        } else {
            annualBudget * years * ulipFeeFactor
        }

        val totalInvested = annualBudget * years
        val gain = btirCorpus - ulipCorpus

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Total Annual Budget ($years Yrs)", totalInvested, totalInvested / months, 0.0, totalInvested))
        scheduleList.add(PaymentScheduleItem(2, "Pure Term Insurance Premium", termPremium * years, (termPremium * years) / months, 0.0, termPremium * years))
        scheduleList.add(PaymentScheduleItem(3, "BTIR Equity Mutual Fund SIP Corpus", btirCorpus, btirCorpus / months, (btirCorpus / totalInvested) * 100, btirCorpus))
        scheduleList.add(PaymentScheduleItem(4, "ULIP Net Maturity Corpus (Fee Deducted)", ulipCorpus, ulipCorpus / months, (ulipCorpus / totalInvested) * 100, ulipCorpus))
        scheduleList.add(PaymentScheduleItem(5, "BTIR Outperformance Wealth Surplus", gain, gain / months, (gain / totalInvested) * 100, gain))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "BTIR vs ULIP Wealth Result")
            putExtra("LOAN_AMOUNT", btirCorpus)
            putExtra("INTEREST_RATE", (equityReturn * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", years)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "BTIR: ₹${commaFormat.format(btirCorpus.toInt())} | ULIP: ₹${commaFormat.format(ulipCorpus.toInt())}")
            putExtra("EMI", btirCorpus / months)
            putExtra("TOTAL_INTEREST", gain)
            putExtra("TOTAL_COST", totalInvested)
            putExtra("PAYOFF_DATE", "BTIR Outperforms ULIP by ₹" + commaFormat.format(gain.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etAnnualBudget.setText("1,00,000")
        etTenureYears.setText("15")
        etEquityReturn.setText("12.0")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
