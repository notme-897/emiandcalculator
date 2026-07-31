package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class PercentageCalculatorActivity : BaseInputActivity() {

    private lateinit var etPercentRate: EditText
    private lateinit var etBaseValue: EditText
    private lateinit var etFromValue: EditText
    private lateinit var etToValue: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_percentage_calculator

    override fun getActivityTitle(): String = "Percentage & Change Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPercentRate = findViewById(R.id.etPercentRate)
        etBaseValue = findViewById(R.id.etBaseValue)
        etFromValue = findViewById(R.id.etFromValue)
        etToValue = findViewById(R.id.etToValue)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)
        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        etPercentRate.setText("18")
        etBaseValue.setText("15,000")
        etFromValue.setText("500")
        etToValue.setText("650")
    }

    private fun calculateAndNavigate() {
        val pctRate = etPercentRate.text.toString().toDoubleOrNull() ?: 0.0
        val baseVal = etBaseValue.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (baseVal <= 0) {
            Toast.makeText(this, "Please enter valid base amount", Toast.LENGTH_SHORT).show()
            return
        }

        val pctResult = baseVal * (pctRate / 100.0)

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Percentage Calculation Result")
            putExtra("LOAN_AMOUNT", baseVal)
            putExtra("INTEREST_RATE", pctRate.toFloat())
            putExtra("LOAN_TERM_YEARS", 0)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", "$pctRate% of ₹${decimalFormat.format(baseVal)}")
            putExtra("EMI", pctResult)
            putExtra("TOTAL_INTEREST", pctResult)
            putExtra("TOTAL_COST", baseVal + pctResult)
            putExtra("PAYOFF_DATE", "Calculated Portion: ₹" + decimalFormat.format(pctResult))
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPercentRate.setText("18")
        etBaseValue.setText("15,000")
        etFromValue.setText("500")
        etToValue.setText("650")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
