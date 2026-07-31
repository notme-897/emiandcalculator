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
import kotlin.math.min

class GratuityCalculatorActivity : BaseInputActivity() {

    private lateinit var etLastBasic: EditText
    private lateinit var etServiceYears: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_gratuity_calculator

    override fun getActivityTitle(): String = "Gratuity Payout Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etLastBasic = findViewById(R.id.etLastBasic)
        etServiceYears = findViewById(R.id.etServiceYears)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etLastBasic)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateGratuityAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etLastBasic.setText("60,000")
        etServiceYears.setText("7")
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

    private fun calculateGratuityAndNavigate() {
        val basicStr = etLastBasic.text.toString().replace(",", "")
        val yearsStr = etServiceYears.text.toString()

        val lastBasic = basicStr.toDoubleOrNull() ?: 0.0
        val serviceYears = yearsStr.toIntOrNull() ?: 7

        if (lastBasic <= 0) {
            Toast.makeText(this, "Please enter valid last drawn Basic + DA salary", Toast.LENGTH_SHORT).show()
            return
        }
        if (serviceYears < 5) {
            Toast.makeText(this, "Gratuity requires a minimum of 5 years continuous service", Toast.LENGTH_LONG).show()
            return
        }

        // Gratuity Act Formula: (15 / 26) * Last Basic * Completed Years
        val grossGratuity = (15.0 / 26.0) * lastBasic * serviceYears

        // Statutory Tax Exemption Cap = Rs 20,00,000 (20 Lakhs)
        val taxExemptLimit = 2000000.0
        val taxExemptGratuity = min(grossGratuity, taxExemptLimit)
        val taxableGratuity = max(0.0, grossGratuity - taxExemptGratuity)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Gross Gratuity Entitlement", grossGratuity, grossGratuity / 12.0, 0.0, grossGratuity))
        scheduleList.add(PaymentScheduleItem(2, "Tax-Exempt Gratuity Amount", taxExemptGratuity, taxExemptGratuity / 12.0, 0.0, taxExemptGratuity))
        scheduleList.add(PaymentScheduleItem(3, "Taxable Portion (Above ₹20L Cap)", taxableGratuity, taxableGratuity / 12.0, 0.0, taxableGratuity))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Gratuity Payout Result")
            putExtra("LOAN_AMOUNT", grossGratuity) // Gratuity Payout
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", serviceYears)
            putExtra("LOAN_TERM_MONTHS", serviceYears * 12)
            putExtra("START_DATE", "$serviceYears Years Service (${if (taxableGratuity > 0) "Taxable Exceeds 20L" else "100% Tax Free"})")
            putExtra("EMI", grossGratuity / 12.0)
            putExtra("TOTAL_INTEREST", taxableGratuity)
            putExtra("TOTAL_COST", taxExemptGratuity)
            putExtra("PAYOFF_DATE", "Gratuity Payout: ₹" + commaFormat.format(grossGratuity.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etLastBasic.setText("60,000")
        etServiceYears.setText("7")
    }
}
