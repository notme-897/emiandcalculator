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
import kotlin.math.ceil

class SavingsGoalActivity : BaseInputActivity() {

    private lateinit var etTargetGoal: EditText
    private lateinit var etCurrentSaved: EditText
    private lateinit var etDailySavings: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")

    override fun getLayoutResId(): Int = R.layout.activity_savings_goal

    override fun getActivityTitle(): String = "Daily Savings Goal Tracker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etTargetGoal = findViewById(R.id.etTargetGoal)
        etCurrentSaved = findViewById(R.id.etCurrentSaved)
        etDailySavings = findViewById(R.id.etDailySavings)

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

        etTargetGoal.setText("50,000")
        etCurrentSaved.setText("10,000")
        etDailySavings.setText("200")
    }

    private fun calculateAndNavigate() {
        val target = etTargetGoal.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val current = etCurrentSaved.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val daily = etDailySavings.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (target <= 0 || daily <= 0) {
            Toast.makeText(this, "Please enter valid target goal and daily savings", Toast.LENGTH_SHORT).show()
            return
        }

        val deficit = (target - current).coerceAtLeast(0.0)
        val days = ceil(deficit / daily).toInt()
        val monthlySaving = daily * 30.0

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Savings Goal Timeline")
            putExtra("LOAN_AMOUNT", target)
            putExtra("INTEREST_RATE", ((current / target) * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", days / 365)
            putExtra("LOAN_TERM_MONTHS", (days % 365) / 30)
            putExtra("START_DATE", "Saving ₹$daily per day")
            putExtra("EMI", monthlySaving)
            putExtra("TOTAL_INTEREST", 0.0)
            putExtra("TOTAL_COST", target)
            putExtra("PAYOFF_DATE", "$days Days to Target")
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etTargetGoal.setText("50,000")
        etCurrentSaved.setText("10,000")
        etDailySavings.setText("200")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
