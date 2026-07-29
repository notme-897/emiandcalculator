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

class JobOfferComparatorActivity : BaseInputActivity() {

    private lateinit var etCtcA: EditText
    private lateinit var etVariableA: EditText
    private lateinit var etEsopA: EditText

    private lateinit var etCtcB: EditText
    private lateinit var etVariableB: EditText
    private lateinit var etEsopB: EditText

    private lateinit var btnCompare: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_job_offer_comparator

    override fun getActivityTitle(): String = "Job Offer CTC Comparator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCtcA = findViewById(R.id.etCtcA)
        etVariableA = findViewById(R.id.etVariableA)
        etEsopA = findViewById(R.id.etEsopA)

        etCtcB = findViewById(R.id.etCtcB)
        etVariableB = findViewById(R.id.etVariableB)
        etEsopB = findViewById(R.id.etEsopB)

        btnCompare = findViewById(R.id.btnCompare)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCompare.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCompare.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etCtcA)
        setupCommaFormatting(etCtcB)
        setupCommaFormatting(etEsopA)
        setupCommaFormatting(etEsopB)

        setupButtonAnimation(btnCompare)
        setupButtonAnimation(btnReset)

        btnCompare.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            compareOffersAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etCtcA.setText("15,00,000")
        etVariableA.setText("10")
        etEsopA.setText("0")

        etCtcB.setText("20,00,000")
        etVariableB.setText("10")
        etEsopB.setText("4,00,000")
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

    private fun computeRealNetMonthly(ctc: Double, variablePct: Double, totalEsop4Yr: Double): Double {
        val annualEsopPerYear = totalEsop4Yr / 4.0
        val guaranteedCashCtc = max(0.0, ctc - annualEsopPerYear)
        val fixedBaseCtc = guaranteedCashCtc * (1.0 - (variablePct / 100.0))

        val monthlyGrossFixed = fixedBaseCtc / 12.0
        val pfMonthly = minOf((monthlyGrossFixed * 0.5) * 0.12, 1800.0)
        val ptMonthly = 200.0

        val taxable = max(0.0, fixedBaseCtc - 75000.0)
        var annualTds = 0.0
        if (taxable > 700000.0) {
            var rem = taxable
            if (rem > 300000) annualTds += minOf(rem - 300000, 400000.0) * 0.05
            if (rem > 700000) annualTds += minOf(rem - 700000, 300000.0) * 0.10
            if (rem > 1000000) annualTds += minOf(rem - 1000000, 200000.0) * 0.15
            if (rem > 1200000) annualTds += minOf(rem - 1200000, 300000.0) * 0.20
            if (rem > 1500000) annualTds += (rem - 1500000) * 0.30
        }

        return max(0.0, monthlyGrossFixed - pfMonthly - ptMonthly - (annualTds / 12.0))
    }

    private fun compareOffersAndNavigate() {
        val ctcA = etCtcA.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val varA = etVariableA.text.toString().toDoubleOrNull() ?: 0.0
        val esopA = etEsopA.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        val ctcB = etCtcB.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val varB = etVariableB.text.toString().toDoubleOrNull() ?: 0.0
        val esopB = etEsopB.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (ctcA <= 0 || ctcB <= 0) {
            Toast.makeText(this, "Please enter valid CTC amounts for both offers", Toast.LENGTH_SHORT).show()
            return
        }

        val netMonthlyA = computeRealNetMonthly(ctcA, varA, esopA)
        val netMonthlyB = computeRealNetMonthly(ctcB, varB, esopB)

        val diffMonthly = netMonthlyB - netMonthlyA

        val winnerText = if (diffMonthly > 0) {
            "Offer B gives +${formatCurrency(diffMonthly)}/month MORE real cash!"
        } else if (diffMonthly < 0) {
            "Offer A gives +${formatCurrency(-diffMonthly)}/month MORE real cash!"
        } else {
            "Both Offers give equal net monthly cash!"
        }

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Offer A Real Cash", ctcA, netMonthlyA, ctcA - netMonthlyA * 12, ctcA))
        scheduleList.add(PaymentScheduleItem(2, "Offer B Real Cash", ctcB, netMonthlyB, ctcB - netMonthlyB * 12, ctcB))
        scheduleList.add(PaymentScheduleItem(3, "Monthly Difference", Math.abs(ctcB - ctcA), Math.abs(diffMonthly), Math.abs(diffMonthly) * 12, Math.abs(ctcB - ctcA)))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Job Offer Comparison Result")
            putExtra("LOAN_AMOUNT", Math.max(netMonthlyA, netMonthlyB))
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", winnerText)
            putExtra("EMI", Math.abs(diffMonthly))
            putExtra("TOTAL_INTEREST", Math.abs(diffMonthly) * 12.0)
            putExtra("TOTAL_COST", Math.max(ctcA, ctcB))
            putExtra("PAYOFF_DATE", "Offer A: ₹" + commaFormat.format(netMonthlyA) + "/mo | Offer B: ₹" + commaFormat.format(netMonthlyB) + "/mo")
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etCtcA.setText("15,00,000")
        etVariableA.setText("10")
        etEsopA.setText("0")

        etCtcB.setText("20,00,000")
        etVariableB.setText("10")
        etEsopB.setText("4,00,000")
    }
}
