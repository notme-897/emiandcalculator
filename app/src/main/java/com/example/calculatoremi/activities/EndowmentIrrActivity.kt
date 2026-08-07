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

class EndowmentIrrActivity : BaseInputActivity() {

    private lateinit var etAnnualPremium: EditText
    private lateinit var etPayYears: EditText
    private lateinit var etPolicyYears: EditText
    private lateinit var etMaturityPayout: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_endowment_irr

    override fun getActivityTitle(): String = "Endowment Policy Return (IRR)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etAnnualPremium = findViewById(R.id.etAnnualPremium)
        etPayYears = findViewById(R.id.etPayYears)
        etPolicyYears = findViewById(R.id.etPolicyYears)
        etMaturityPayout = findViewById(R.id.etMaturityPayout)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etAnnualPremium)
        setupCommaFormatting(etMaturityPayout)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateIrrAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etAnnualPremium.setText("50,000")
        etPayYears.setText("15")
        etPolicyYears.setText("20")
        etMaturityPayout.setText("15,00,000")
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

    private fun calculateInternalRateOfReturn(
        premium: Double,
        payYears: Int,
        policyYears: Int,
        maturityPayout: Double
    ): Double {
        if (premium <= 0 || payYears <= 0 || policyYears <= 0 || maturityPayout <= 0) return 0.0

        var r = 0.05
        for (i in 0..100) {
            var npv = 0.0
            var dNpv = 0.0

            for (t in 1..payYears) {
                npv -= premium / (1.0 + r).pow(t.toDouble())
                dNpv += (t * premium) / (1.0 + r).pow((t + 1).toDouble())
            }

            npv += maturityPayout / (1.0 + r).pow(policyYears.toDouble())
            dNpv -= (policyYears * maturityPayout) / (1.0 + r).pow((policyYears + 1).toDouble())

            val nextR = r - npv / dNpv
            if (kotlin.math.abs(nextR - r) < 1e-6) return nextR
            r = nextR
        }
        return r
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

    private fun calculateIrrAndNavigate() {
        val annualPremium = etAnnualPremium.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val payYears = etPayYears.text.toString().toIntOrNull() ?: 0
        val policyYears = etPolicyYears.text.toString().toIntOrNull() ?: 0
        val maturityPayout = etMaturityPayout.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (annualPremium <= 0 || payYears <= 0 || policyYears <= 0 || maturityPayout <= 0) {
            Toast.makeText(this, "Please enter valid policy parameters", Toast.LENGTH_SHORT).show()
            return
        }

        val totalInvested = annualPremium * payYears
        val netGain = maturityPayout - totalInvested
        val irr = calculateInternalRateOfReturn(annualPremium, payYears, policyYears, maturityPayout) * 100.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        for (y in 1..policyYears) {
            val outflow = if (y <= payYears) annualPremium else 0.0
            val payout = if (y == policyYears) maturityPayout else 0.0
            val netCashFlow = payout - outflow
            scheduleList.add(PaymentScheduleItem(y, "Year $y Cash Flow", netCashFlow, outflow, payout, netCashFlow))
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Endowment Policy IRR Result")
            putExtra("LOAN_AMOUNT", maturityPayout)
            putExtra("INTEREST_RATE", irr.toFloat())
            putExtra("LOAN_TERM_YEARS", policyYears)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Pay Term: $payYears Yrs | Policy Term: $policyYears Yrs")
            putExtra("EMI", totalInvested / payYears)
            putExtra("TOTAL_INTEREST", netGain)
            putExtra("TOTAL_COST", totalInvested)
            putExtra("PAYOFF_DATE", "Net Policy IRR: ${DecimalFormat("#0.00").format(irr)}% p.a.")
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etAnnualPremium.setText("50,000")
        etPayYears.setText("15")
        etPolicyYears.setText("20")
        etMaturityPayout.setText("15,00,000")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
