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

class PensionAnnuityActivity : BaseInputActivity() {

    private lateinit var etCorpusAmount: EditText
    private lateinit var etAnnuityRate: EditText
    private lateinit var chipFrequencyToggle: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isMonthlyFrequency = true

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_pension_annuity

    override fun getActivityTitle(): String = "Pension & Annuity Estimator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCorpusAmount = findViewById(R.id.etCorpusAmount)
        etAnnuityRate = findViewById(R.id.etAnnuityRate)
        chipFrequencyToggle = findViewById(R.id.chipFrequencyToggle)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etCorpusAmount)

        chipFrequencyToggle.setOnClickListener {
            isMonthlyFrequency = !isMonthlyFrequency
            updateFrequencyButton()
        }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculatePensionAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etCorpusAmount.setText("1,00,00,000")
        etAnnuityRate.setText("6.5")
        updateFrequencyButton()
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

    private fun updateFrequencyButton() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (isMonthlyFrequency) {
            chipFrequencyToggle.text = "Monthly Pension"
            chipFrequencyToggle.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipFrequencyToggle.setTextColor(Color.WHITE)
            chipFrequencyToggle.strokeWidth = 0
        } else {
            chipFrequencyToggle.text = "Annual Pension"
            chipFrequencyToggle.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipFrequencyToggle.setTextColor(Color.parseColor("#1E293B"))
            chipFrequencyToggle.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipFrequencyToggle.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
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

    private fun calculatePensionAndNavigate() {
        val corpusStr = etCorpusAmount.text.toString().replace(",", "")
        val rateStr = etAnnuityRate.text.toString()

        val corpus = corpusStr.toDoubleOrNull() ?: 0.0
        val annuityRatePct = rateStr.toDoubleOrNull() ?: 6.5

        if (corpus <= 0) {
            Toast.makeText(this, "Please enter a valid retirement corpus", Toast.LENGTH_SHORT).show()
            return
        }

        val annualPension = corpus * (annuityRatePct / 100.0)
        val monthlyPension = annualPension / 12.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Purchased Annuity Corpus", corpus, corpus / 12.0, 0.0, corpus))
        scheduleList.add(PaymentScheduleItem(2, "Guaranteed Annual Pension", annualPension, monthlyPension, 0.0, annualPension))
        scheduleList.add(PaymentScheduleItem(3, "Guaranteed Monthly Pension", monthlyPension, monthlyPension, 0.0, monthlyPension))
        scheduleList.add(PaymentScheduleItem(4, "Total 20-Year Lifetime Payout", annualPension * 20.0, annualPension, 0.0, annualPension * 20.0))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Pension & Annuity Payout Result")
            putExtra("LOAN_AMOUNT", monthlyPension) // Monthly Pension
            putExtra("INTEREST_RATE", annuityRatePct.toFloat())
            putExtra("LOAN_TERM_YEARS", 20)
            putExtra("LOAN_TERM_MONTHS", 240)
            putExtra("START_DATE", "Guaranteed Lifetime Rate: $annuityRatePct% p.a.")
            putExtra("EMI", monthlyPension)
            putExtra("TOTAL_INTEREST", annualPension * 20.0)
            putExtra("TOTAL_COST", corpus)
            putExtra("PAYOFF_DATE", "Monthly Pension: ₹" + commaFormat.format(monthlyPension.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etCorpusAmount.setText("1,00,00,000")
        etAnnuityRate.setText("6.5")
        isMonthlyFrequency = true
        updateFrequencyButton()
    }
}
