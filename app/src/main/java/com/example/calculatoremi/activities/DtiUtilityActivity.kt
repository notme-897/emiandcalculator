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

class DtiUtilityActivity : BaseInputActivity() {

    private lateinit var etGrossIncome: EditText
    private lateinit var etHousingCost: EditText
    private lateinit var etOtherDebts: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_dti_utility

    override fun getActivityTitle(): String = "Debt-to-Income (DTI) Health"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etGrossIncome = findViewById(R.id.etGrossIncome)
        etHousingCost = findViewById(R.id.etHousingCost)
        etOtherDebts = findViewById(R.id.etOtherDebts)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etGrossIncome)
        setupCommaFormatting(etHousingCost)
        setupCommaFormatting(etOtherDebts)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateDtiAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etGrossIncome.setText("1,20,000")
        etHousingCost.setText("30,000")
        etOtherDebts.setText("15,000")
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

    private fun calculateDtiAndNavigate() {
        val grossIncome = etGrossIncome.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val housingCost = etHousingCost.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val otherDebts = etOtherDebts.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (grossIncome <= 0) {
            Toast.makeText(this, "Please enter a valid gross monthly income", Toast.LENGTH_SHORT).show()
            return
        }

        val totalDebts = housingCost + otherDebts
        val backEndDti = (totalDebts / grossIncome) * 100.0
        val frontEndDti = (housingCost / grossIncome) * 100.0
        val disposableIncome = grossIncome - totalDebts

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Gross Monthly Income", grossIncome, grossIncome / 12.0, 0.0, grossIncome))
        scheduleList.add(PaymentScheduleItem(2, "Housing EMI (P+I)", housingCost, housingCost / 12.0, frontEndDti, housingCost))
        scheduleList.add(PaymentScheduleItem(3, "Other Loan EMIs", otherDebts, otherDebts / 12.0, 0.0, otherDebts))
        scheduleList.add(PaymentScheduleItem(4, "Total Monthly Obligations", totalDebts, totalDebts / 12.0, backEndDti, totalDebts))
        scheduleList.add(PaymentScheduleItem(5, "Net Remaining Surplus", disposableIncome, disposableIncome / 12.0, 0.0, disposableIncome))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Debt-to-Income Health Result")
            putExtra("LOAN_AMOUNT", totalDebts)
            putExtra("INTEREST_RATE", backEndDti.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", "Front-End: ${DecimalFormat("#0.0").format(frontEndDti)}% | Back-End: ${DecimalFormat("#0.0").format(backEndDti)}%")
            putExtra("EMI", totalDebts)
            putExtra("TOTAL_INTEREST", disposableIncome)
            putExtra("TOTAL_COST", grossIncome)
            putExtra("PAYOFF_DATE", "DTI Ratio: ${DecimalFormat("#0.1").format(backEndDti)}%")
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etGrossIncome.setText("1,20,000")
        etHousingCost.setText("30,000")
        etOtherDebts.setText("15,000")
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
