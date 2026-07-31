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

class TermPremiumEstimatorActivity : BaseInputActivity() {

    private lateinit var etSumAssured: EditText
    private lateinit var etAge: EditText
    private lateinit var etPolicyTerm: EditText

    private lateinit var chipTobaccoNo: MaterialButton
    private lateinit var chipTobaccoYes: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isTobaccoUser = false

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_term_premium_estimator

    override fun getActivityTitle(): String = "Term Premium Estimator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etSumAssured = findViewById(R.id.etSumAssured)
        etAge = findViewById(R.id.etAge)
        etPolicyTerm = findViewById(R.id.etPolicyTerm)

        chipTobaccoNo = findViewById(R.id.chipTobaccoNo)
        chipTobaccoYes = findViewById(R.id.chipTobaccoYes)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etSumAssured)

        chipTobaccoNo.setOnClickListener { isTobaccoUser = false; highlightTobaccoChips() }
        chipTobaccoYes.setOnClickListener { isTobaccoUser = true; highlightTobaccoChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculatePremiumAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etSumAssured.setText("1,00,00,000")
        etAge.setText("30")
        etPolicyTerm.setText("30")
        highlightTobaccoChips()
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

    private fun highlightTobaccoChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (!isTobaccoUser) {
            chipTobaccoNo.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipTobaccoNo.setTextColor(Color.WHITE)
            chipTobaccoNo.strokeWidth = 0

            chipTobaccoYes.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipTobaccoYes.setTextColor(Color.parseColor("#1E293B"))
            chipTobaccoYes.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipTobaccoYes.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipTobaccoYes.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipTobaccoYes.setTextColor(Color.WHITE)
            chipTobaccoYes.strokeWidth = 0

            chipTobaccoNo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipTobaccoNo.setTextColor(Color.parseColor("#1E293B"))
            chipTobaccoNo.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipTobaccoNo.strokeWidth = (1 * resources.displayMetrics.density).toInt()
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

    private fun calculatePremiumAndNavigate() {
        val sumAssuredStr = etSumAssured.text.toString().replace(",", "")
        val ageStr = etAge.text.toString()
        val termStr = etPolicyTerm.text.toString()

        val sumAssured = sumAssuredStr.toDoubleOrNull() ?: 0.0
        val age = ageStr.toIntOrNull() ?: 30
        val term = termStr.toIntOrNull() ?: 30

        if (sumAssured <= 0) {
            Toast.makeText(this, "Please enter a valid sum assured", Toast.LENGTH_SHORT).show()
            return
        }

        // Standard Actuarial Term Premium Rate per 1 Lakh Sum Assured:
        // Base rate ~ Rs. 80 per Lakh for age 25, scaling with age & tobacco
        val ageMultiplier = 1.0 + (age - 25).coerceAtLeast(0) * 0.045
        val tobaccoMultiplier = if (isTobaccoUser) 1.55 else 1.0
        val baseRatePerLakh = 85.0 * ageMultiplier * tobaccoMultiplier

        val annualBasePremium = (sumAssured / 100000.0) * baseRatePerLakh
        val annualGst = annualBasePremium * 0.18 // 18% GST on term plan
        val totalAnnualPremium = annualBasePremium + annualGst
        val monthlyPremium = totalAnnualPremium / 12.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Base Annual Premium", annualBasePremium, annualBasePremium / 12.0, 0.0, annualBasePremium))
        scheduleList.add(PaymentScheduleItem(2, "GST Tax (18%)", annualGst, annualGst / 12.0, 0.0, annualGst))
        scheduleList.add(PaymentScheduleItem(3, "Total Annual Premium", totalAnnualPremium, monthlyPremium, 0.0, totalAnnualPremium))
        scheduleList.add(PaymentScheduleItem(4, "Total Lifetime Premium ($term yrs)", totalAnnualPremium * term, totalAnnualPremium, 0.0, totalAnnualPremium * term))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Term Premium Calculation")
            putExtra("LOAN_AMOUNT", monthlyPremium) // Monthly Premium
            putExtra("INTEREST_RATE", 18.0f) // GST rate
            putExtra("LOAN_TERM_YEARS", term)
            putExtra("LOAN_TERM_MONTHS", term * 12)
            putExtra("START_DATE", "Coverage: ₹" + commaFormat.format(sumAssured) + " (${if (isTobaccoUser) "Smoker" else "Non-Smoker"})")
            putExtra("EMI", monthlyPremium)
            putExtra("TOTAL_INTEREST", annualGst * term)
            putExtra("TOTAL_COST", totalAnnualPremium * term)
            putExtra("PAYOFF_DATE", "Annual Premium: ₹" + commaFormat.format(totalAnnualPremium.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etSumAssured.setText("1,00,00,000")
        etAge.setText("30")
        etPolicyTerm.setText("30")
        isTobaccoUser = false
        highlightTobaccoChips()
    }
}
