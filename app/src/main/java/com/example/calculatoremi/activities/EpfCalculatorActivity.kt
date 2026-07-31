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

class EpfCalculatorActivity : BaseInputActivity() {

    private lateinit var etBasicMonthly: EditText
    private lateinit var etCurrentAge: EditText
    private lateinit var etRetirementAge: EditText
    private lateinit var etAnnualHike: EditText
    private lateinit var etVpfPct: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_epf_calculator

    override fun getActivityTitle(): String = "EPF & VPF Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etBasicMonthly = findViewById(R.id.etBasicMonthly)
        etCurrentAge = findViewById(R.id.etCurrentAge)
        etRetirementAge = findViewById(R.id.etRetirementAge)
        etAnnualHike = findViewById(R.id.etAnnualHike)
        etVpfPct = findViewById(R.id.etVpfPct)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etBasicMonthly)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateEpfAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etBasicMonthly.setText("50,000")
        etCurrentAge.setText("25")
        etRetirementAge.setText("58")
        etAnnualHike.setText("5.0")
        etVpfPct.setText("0")
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

    private fun calculateEpfAndNavigate() {
        val basicStr = etBasicMonthly.text.toString().replace(",", "")
        val ageStr = etCurrentAge.text.toString()
        val retAgeStr = etRetirementAge.text.toString()
        val hikeStr = etAnnualHike.text.toString()
        val vpfStr = etVpfPct.text.toString()

        var currentBasic = basicStr.toDoubleOrNull() ?: 0.0
        val currentAge = ageStr.toIntOrNull() ?: 25
        val retirementAge = retAgeStr.toIntOrNull() ?: 58
        val annualHikePct = hikeStr.toDoubleOrNull() ?: 5.0
        val vpfPct = vpfStr.toDoubleOrNull() ?: 0.0

        if (currentBasic <= 0) {
            Toast.makeText(this, "Please enter a valid monthly basic salary", Toast.LENGTH_SHORT).show()
            return
        }

        val totalYears = max(1, retirementAge - currentAge)
        val interestRate = 0.0825 // 8.25% EPFO rate

        var totalEmployeeContribution = 0.0
        var totalEmployerContribution = 0.0
        var epfCorpus = 0.0

        for (year in 1..totalYears) {
            val empPfMonthly = currentBasic * (0.12 + (vpfPct / 100.0))
            val epsMonthly = min(1250.0, currentBasic * 0.0833)
            val employerEpfMonthly = max(0.0, (currentBasic * 0.12) - epsMonthly)

            val annualEmpContrib = empPfMonthly * 12.0
            val annualEmployerContrib = employerEpfMonthly * 12.0

            totalEmployeeContribution += annualEmpContrib
            totalEmployerContribution += annualEmployerContrib

            val annualAddition = annualEmpContrib + annualEmployerContrib
            epfCorpus = (epfCorpus + annualAddition) * (1.0 + interestRate)

            currentBasic *= (1.0 + (annualHikePct / 100.0))
        }

        val totalInvested = totalEmployeeContribution + totalEmployerContribution
        val totalInterestEarned = max(0.0, epfCorpus - totalInvested)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Total Employee PF + VPF", totalEmployeeContribution, totalEmployeeContribution / (totalYears * 12), 0.0, totalEmployeeContribution))
        scheduleList.add(PaymentScheduleItem(2, "Total Employer EPF Share", totalEmployerContribution, totalEmployerContribution / (totalYears * 12), 0.0, totalEmployerContribution))
        scheduleList.add(PaymentScheduleItem(3, "Compound Interest Earned (8.25%)", totalInterestEarned, totalInterestEarned / (totalYears * 12), 0.0, totalInterestEarned))
        scheduleList.add(PaymentScheduleItem(4, "Maturity EPF Corpus", epfCorpus, epfCorpus / (totalYears * 12), 0.0, epfCorpus))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "EPF Maturity Corpus Result")
            putExtra("LOAN_AMOUNT", epfCorpus) // Total EPF Corpus
            putExtra("INTEREST_RATE", 8.25f) // EPFO Rate
            putExtra("LOAN_TERM_YEARS", totalYears)
            putExtra("LOAN_TERM_MONTHS", totalYears * 12)
            putExtra("START_DATE", "Maturity at Age $retirementAge ($totalYears Years Compounding)")
            putExtra("EMI", (totalEmployeeContribution + totalEmployerContribution) / (totalYears * 12.0))
            putExtra("TOTAL_INTEREST", totalInterestEarned)
            putExtra("TOTAL_COST", totalInvested)
            putExtra("PAYOFF_DATE", "EPF Maturity Corpus: ₹" + commaFormat.format(epfCorpus.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etBasicMonthly.setText("50,000")
        etCurrentAge.setText("25")
        etRetirementAge.setText("58")
        etAnnualHike.setText("5.0")
        etVpfPct.setText("0")
    }
}
