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
import kotlin.math.pow

class HlvCalculatorActivity : BaseInputActivity() {

    private lateinit var etCurrentAge: EditText
    private lateinit var etRetirementAge: EditText
    private lateinit var chipTobaccoNo: MaterialButton
    private lateinit var chipTobaccoYes: MaterialButton

    private lateinit var etAnnualIncome: EditText
    private lateinit var etPersonalExpenses: EditText
    private lateinit var etTotalLoans: EditText
    private lateinit var etFutureGoals: EditText
    private lateinit var etExistingCover: EditText
    private lateinit var etLiquidAssets: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var isTobaccoUser = false

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_hlv_calculator

    override fun getActivityTitle(): String = "Human Life Value (HLV)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCurrentAge = findViewById(R.id.etCurrentAge)
        etRetirementAge = findViewById(R.id.etRetirementAge)

        chipTobaccoNo = findViewById(R.id.chipTobaccoNo)
        chipTobaccoYes = findViewById(R.id.chipTobaccoYes)

        etAnnualIncome = findViewById(R.id.etAnnualIncome)
        etPersonalExpenses = findViewById(R.id.etPersonalExpenses)
        etTotalLoans = findViewById(R.id.etTotalLoans)
        etFutureGoals = findViewById(R.id.etFutureGoals)
        etExistingCover = findViewById(R.id.etExistingCover)
        etLiquidAssets = findViewById(R.id.etLiquidAssets)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etAnnualIncome)
        setupCommaFormatting(etPersonalExpenses)
        setupCommaFormatting(etTotalLoans)
        setupCommaFormatting(etFutureGoals)
        setupCommaFormatting(etExistingCover)
        setupCommaFormatting(etLiquidAssets)

        chipTobaccoNo.setOnClickListener { isTobaccoUser = false; highlightTobaccoChips() }
        chipTobaccoYes.setOnClickListener { isTobaccoUser = true; highlightTobaccoChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateHlvAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etCurrentAge.setText("32")
        etRetirementAge.setText("60")
        etAnnualIncome.setText("15,00,000")
        etPersonalExpenses.setText("3,00,000")
        etTotalLoans.setText("35,00,000")
        etFutureGoals.setText("20,00,000")
        etExistingCover.setText("10,00,000")
        etLiquidAssets.setText("15,00,000")
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

    private fun calculateHlvAndNavigate() {
        val age = etCurrentAge.text.toString().toIntOrNull() ?: 32
        val retirementAge = etRetirementAge.text.toString().toIntOrNull() ?: 60

        val annualIncome = etAnnualIncome.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val personalExpenses = etPersonalExpenses.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val totalLoans = etTotalLoans.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val futureGoals = etFutureGoals.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val existingCover = etExistingCover.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val liquidAssets = etLiquidAssets.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (annualIncome <= 0) {
            Toast.makeText(this, "Please enter a valid annual income", Toast.LENGTH_SHORT).show()
            return
        }
        val remainingYears = max(1, retirementAge - age)

        val netAnnualContribution = max(0.0, annualIncome - personalExpenses)

        // Real rate r = (1 + return) / (1 + inflation) - 1. E.g. return 8%, inflation 6% => r ~ 1.88%
        val returnRate = 0.08
        val inflationRate = 0.06
        val realRate = (1.0 + returnRate) / (1.0 + inflationRate) - 1.0

        val pvFactor = (1.0 - (1.0 + realRate).pow(-remainingYears.toDouble())) / realRate
        val incomeReplacementCorpus = netAnnualContribution * pvFactor

        val grossNeed = incomeReplacementCorpus + totalLoans + futureGoals
        val existingCushion = existingCover + liquidAssets
        val netTermCoverRequired = max(0.0, grossNeed - existingCushion)

        val ruleOfThumbMin = annualIncome * 10.0
        val ruleOfThumbMax = annualIncome * 20.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Income Replacement Corpus", incomeReplacementCorpus, incomeReplacementCorpus / 12.0, 0.0, incomeReplacementCorpus))
        scheduleList.add(PaymentScheduleItem(2, "Outstanding Debt Liabilities", totalLoans, totalLoans, 0.0, totalLoans))
        scheduleList.add(PaymentScheduleItem(3, "Future Major Goals", futureGoals, futureGoals, 0.0, futureGoals))
        scheduleList.add(PaymentScheduleItem(4, "Less: Existing Cover & Assets", existingCushion, existingCushion, 0.0, existingCushion))
        scheduleList.add(PaymentScheduleItem(5, "Net Recommended Term Cover", netTermCoverRequired, netTermCoverRequired / 12.0, 0.0, netTermCoverRequired))

        val benchmarkText = "10x-20x Rule Benchmark: ₹${commaFormat.format(ruleOfThumbMin.toInt())} - ₹${commaFormat.format(ruleOfThumbMax.toInt())}"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Human Life Value (HLV) Result")
            putExtra("LOAN_AMOUNT", netTermCoverRequired) // Main Cover Needed
            putExtra("INTEREST_RATE", ((netTermCoverRequired / annualIncome)).toFloat()) // Multiple of Salary
            putExtra("LOAN_TERM_YEARS", remainingYears)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", benchmarkText)
            putExtra("EMI", netTermCoverRequired / 12.0)
            putExtra("TOTAL_INTEREST", grossNeed)
            putExtra("TOTAL_COST", netTermCoverRequired)
            putExtra("PAYOFF_DATE", "Ideal Sum Assured: ₹" + commaFormat.format(netTermCoverRequired))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etCurrentAge.setText("32")
        etRetirementAge.setText("60")
        etAnnualIncome.setText("15,00,000")
        etPersonalExpenses.setText("3,00,000")
        etTotalLoans.setText("35,00,000")
        etFutureGoals.setText("20,00,000")
        etExistingCover.setText("10,00,000")
        etLiquidAssets.setText("15,00,000")
        isTobaccoUser = false
        highlightTobaccoChips()
    }
}
