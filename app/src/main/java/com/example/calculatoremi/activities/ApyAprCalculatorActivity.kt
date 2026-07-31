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
import kotlin.math.exp
import kotlin.math.pow

class ApyAprCalculatorActivity : BaseInputActivity() {

    private lateinit var etNominalApr: EditText

    private lateinit var chipCompDaily: MaterialButton
    private lateinit var chipCompMonthly: MaterialButton
    private lateinit var chipCompQuarterly: MaterialButton
    private lateinit var chipCompContinuous: MaterialButton

    private lateinit var chipCountAct365: MaterialButton
    private lateinit var chipCountAct360: MaterialButton
    private lateinit var chipCount30360: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var compoundingType = 2 // 0: Daily(365), 1: Monthly(12), 2: Quarterly(4), 3: Continuous
    private var dayCountBasis = 0  // 0: Actual/365, 1: Actual/360, 2: 30/360

    private val commaFormat = DecimalFormat("#,##,###")

    override fun getLayoutResId(): Int = R.layout.activity_apy_apr_calculator

    override fun getActivityTitle(): String = "APY vs APR & Day-Count"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etNominalApr = findViewById(R.id.etNominalApr)

        chipCompDaily = findViewById(R.id.chipCompDaily)
        chipCompMonthly = findViewById(R.id.chipCompMonthly)
        chipCompQuarterly = findViewById(R.id.chipCompQuarterly)
        chipCompContinuous = findViewById(R.id.chipCompContinuous)

        chipCountAct365 = findViewById(R.id.chipCountAct365)
        chipCountAct360 = findViewById(R.id.chipCountAct360)
        chipCount30360 = findViewById(R.id.chipCount30360)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        chipCompDaily.setOnClickListener { compoundingType = 0; highlightChips() }
        chipCompMonthly.setOnClickListener { compoundingType = 1; highlightChips() }
        chipCompQuarterly.setOnClickListener { compoundingType = 2; highlightChips() }
        chipCompContinuous.setOnClickListener { compoundingType = 3; highlightChips() }

        chipCountAct365.setOnClickListener { dayCountBasis = 0; highlightChips() }
        chipCountAct360.setOnClickListener { dayCountBasis = 1; highlightChips() }
        chipCount30360.setOnClickListener { dayCountBasis = 2; highlightChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateApyAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etNominalApr.setText("12.0")
        highlightChips()
    }

    private fun highlightChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)

        val compChips = listOf(chipCompDaily, chipCompMonthly, chipCompQuarterly, chipCompContinuous)
        compChips.forEachIndexed { index, chip ->
            if (index == compoundingType) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }

        val countChips = listOf(chipCountAct365, chipCountAct360, chipCount30360)
        countChips.forEachIndexed { index, chip ->
            if (index == dayCountBasis) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
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

    private fun calculateApyAndNavigate() {
        val aprPct = etNominalApr.text.toString().toDoubleOrNull() ?: 0.0

        if (aprPct <= 0) {
            Toast.makeText(this, "Please enter a valid nominal APR percentage", Toast.LENGTH_SHORT).show()
            return
        }

        val r = aprPct / 100.0
        val n = when (compoundingType) {
            0 -> 365.0
            1 -> 12.0
            2 -> 4.0
            else -> 0.0
        }

        val dayFactor = when (dayCountBasis) {
            1 -> 365.0 / 360.0
            else -> 1.0
        }

        val effectiveR = r * dayFactor
        val apy = if (compoundingType == 3) {
            exp(effectiveR) - 1.0
        } else {
            (1.0 + (effectiveR / n)).pow(n) - 1.0
        }

        val apyPct = apy * 100.0
        val compModeName = when (compoundingType) {
            0 -> "Daily (365)"
            1 -> "Monthly (12)"
            2 -> "Quarterly (4)"
            else -> "Continuous"
        }
        val dayBasisName = when (dayCountBasis) {
            0 -> "Actual/365"
            1 -> "Actual/360"
            else -> "30/360"
        }

        val principal = 100000.0
        val scheduleList = ArrayList<PaymentScheduleItem>()
        var currentVal = principal

        val periods = if (n > 0) n.toInt() else 12
        val periodRate = if (n > 0) effectiveR / n else effectiveR / 12.0

        for (p in 1..periods) {
            val interest = currentVal * periodRate
            currentVal += interest
            scheduleList.add(PaymentScheduleItem(p, "Period $p ($compModeName)", currentVal, interest, currentVal - principal, currentVal))
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Effective APY Yield Result")
            putExtra("LOAN_AMOUNT", principal)
            putExtra("INTEREST_RATE", apyPct.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 0)
            putExtra("START_DATE", "$compModeName Compounding | $dayBasisName Basis")
            putExtra("EMI", currentVal / 12.0)
            putExtra("TOTAL_INTEREST", currentVal - principal)
            putExtra("TOTAL_COST", currentVal)
            putExtra("PAYOFF_DATE", "Nominal: ${aprPct}% -> Effective APY: ${DecimalFormat("#0.00").format(apyPct)}%")
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etNominalApr.setText("12.0")
        compoundingType = 2
        dayCountBasis = 0
        highlightChips()
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
