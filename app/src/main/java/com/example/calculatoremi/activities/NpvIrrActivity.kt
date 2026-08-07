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
import kotlin.math.abs
import kotlin.math.pow

class NpvIrrActivity : BaseInputActivity() {

    private lateinit var etInitialOutlay: EditText
    private lateinit var etDiscountRate: EditText

    private lateinit var etCashFlow1: EditText
    private lateinit var etCashFlow2: EditText
    private lateinit var etCashFlow3: EditText
    private lateinit var etCashFlow4: EditText
    private lateinit var etCashFlow5: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_npv_irr

    override fun getActivityTitle(): String = "NPV & IRR Cash Flow Engine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etInitialOutlay = findViewById(R.id.etInitialOutlay)
        etDiscountRate = findViewById(R.id.etDiscountRate)

        etCashFlow1 = findViewById(R.id.etCashFlow1)
        etCashFlow2 = findViewById(R.id.etCashFlow2)
        etCashFlow3 = findViewById(R.id.etCashFlow3)
        etCashFlow4 = findViewById(R.id.etCashFlow4)
        etCashFlow5 = findViewById(R.id.etCashFlow5)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etInitialOutlay)
        setupCommaFormatting(etCashFlow1)
        setupCommaFormatting(etCashFlow2)
        setupCommaFormatting(etCashFlow3)
        setupCommaFormatting(etCashFlow4)
        setupCommaFormatting(etCashFlow5)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateNpvIrrAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etInitialOutlay.setText("5,00,000")
        etDiscountRate.setText("10.0")
        etCashFlow1.setText("1,20,000")
        etCashFlow2.setText("1,50,000")
        etCashFlow3.setText("1,80,000")
        etCashFlow4.setText("2,00,000")
        etCashFlow5.setText("2,20,000")
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

    private fun calculateIRR(cashFlows: DoubleArray): Double {
        var rate = 0.1
        val maxIterations = 1000
        val precision = 1e-7

        for (i in 0 until maxIterations) {
            var npv = 0.0
            var derivativeNpv = 0.0

            for (t in cashFlows.indices) {
                val factor = (1.0 + rate).pow(t.toDouble())
                npv += cashFlows[t] / factor
                if (t > 0) {
                    derivativeNpv -= t * cashFlows[t] / (1.0 + rate).pow((t + 1).toDouble())
                }
            }

            if (abs(derivativeNpv) < 1e-10) break
            val nextRate = rate - npv / derivativeNpv
            if (abs(nextRate - rate) < precision) return nextRate
            rate = nextRate
        }
        return rate
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

    private fun calculateNpvIrrAndNavigate() {
        val outlay = etInitialOutlay.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val discountRatePct = etDiscountRate.text.toString().toDoubleOrNull() ?: 0.0
        val discountRate = discountRatePct / 100.0

        val cf1 = etCashFlow1.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val cf2 = etCashFlow2.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val cf3 = etCashFlow3.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val cf4 = etCashFlow4.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val cf5 = etCashFlow5.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (outlay <= 0) {
            Toast.makeText(this, "Please enter a valid initial investment outlay", Toast.LENGTH_SHORT).show()
            return
        }

        val flows = doubleArrayOf(-outlay, cf1, cf2, cf3, cf4, cf5)

        var npv = flows[0]
        val scheduleList = ArrayList<PaymentScheduleItem>()
        var cumPresentValue = 0.0

        for (t in 1 until flows.size) {
            val pv = flows[t] / (1.0 + discountRate).pow(t.toDouble())
            npv += pv
            cumPresentValue += pv
            scheduleList.add(PaymentScheduleItem(t, "Year $t Cash Flow", flows[t], pv, cumPresentValue, flows[t]))
        }

        val irr = calculateIRR(flows)

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "NPV & IRR Analysis Result")
            putExtra("LOAN_AMOUNT", abs(npv))
            putExtra("INTEREST_RATE", (irr * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 5)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "Outlay: ₹${commaFormat.format(outlay.toInt())} @ ${discountRatePct}% Hurdle")
            putExtra("EMI", abs(npv) / 60.0)
            putExtra("TOTAL_INTEREST", if (npv >= 0) npv else 0.0)
            putExtra("TOTAL_COST", outlay)
            putExtra("PAYOFF_DATE", "IRR: ${DecimalFormat("#0.1").format(irr * 100)}% | NPV: ₹" + commaFormat.format(abs(npv).toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etInitialOutlay.setText("5,00,000")
        etDiscountRate.setText("10.0")
        etCashFlow1.setText("1,20,000")
        etCashFlow2.setText("1,50,000")
        etCashFlow3.setText("1,80,000")
        etCashFlow4.setText("2,00,000")
        etCashFlow5.setText("2,20,000")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
