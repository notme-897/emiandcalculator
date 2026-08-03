package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.example.calculatoremi.R
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.io.Serializable

class PersonalLoanResultActivity : BaseResultActivity() {

    private var loanAmount: Double = 0.0
    private var emi: Double = 0.0
    private var totalInterest: Double = 0.0
    private var totalCost: Double = 0.0
    private var loanTitle: String = "Personal Loan Result"

    override fun getResultLayoutResId(): Int = R.layout.activity_loan_result

    override fun getResultTitle(): String = intent.getStringExtra("TITLE") ?: "Loan Result Breakdown"

    override fun getShareText(): String {
        return """
            EMI Calculator Result - $loanTitle
            ---------------------
            Loan Amount: ${formatCurrency(loanAmount)}
            Monthly EMI: ${formatCurrency(emi)}
            Total Interest: ${formatCurrency(totalInterest)}
            Total Cost: ${formatCurrency(totalCost)}
            ---------------------
            Calculated via Finance Hub
        """.trimIndent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)

        loanTitle = getResultTitle()

        // Get Data from Intent
        loanAmount = intent.getDoubleExtra("LOAN_AMOUNT", 0.0)
        val interestRate = intent.getFloatExtra("INTEREST_RATE", 0.0f)
        val years = intent.getIntExtra("LOAN_TERM_YEARS", 0)
        val months = intent.getIntExtra("LOAN_TERM_MONTHS", 0)
        val startDate = intent.getStringExtra("START_DATE") ?: "Today"
        
        emi = intent.getDoubleExtra("EMI", 0.0)
        totalInterest = intent.getDoubleExtra("TOTAL_INTEREST", 0.0)
        totalCost = intent.getDoubleExtra("TOTAL_COST", 0.0)
        val payoffDate = intent.getStringExtra("PAYOFF_DATE") ?: "Not specified"
        
        val schedule = getSerializableExtraCompat<ArrayList<PaymentScheduleItem>>("SCHEDULE")

        // Static Info Fields
        findViewById<TextView>(R.id.resInterestRate).text = "$interestRate %"
        
        val termText = when {
            years > 0 && months > 0 -> "$years Years $months Months"
            years > 0 -> "$years Years (${years * 12} Months)"
            else -> "$months Months"
        }
        findViewById<TextView>(R.id.resLoanTerm).text = termText
        findViewById<TextView>(R.id.resStartDate).text = if (startDate.isBlank()) "Today" else startDate
        findViewById<TextView>(R.id.resPayoffDate).text = if (payoffDate.isBlank()) "Standard Term" else payoffDate

        // Animate Result Monetary Values (Rolling Counter Animation with 64-bit Precision)
        val txtEmi = findViewById<TextView>(R.id.resEmi)
        val txtLoanAmount = findViewById<TextView>(R.id.resLoanAmount)
        val txtTotalInterest = findViewById<TextView>(R.id.resTotalInterest)
        val txtTotalCost = findViewById<TextView>(R.id.resTotalCost)

        animateNumberCounter(txtEmi, emi)
        animateNumberCounter(txtLoanAmount, loanAmount)
        animateNumberCounter(txtTotalInterest, totalInterest)
        animateNumberCounter(txtTotalCost, totalCost)

        // Save Calculation to History
        val category = when {
            loanTitle.contains("Loan", true) || loanTitle.contains("EMI", true) || loanTitle.contains("Mortgage", true) -> "Loans"
            loanTitle.contains("SIP", true) || loanTitle.contains("FD", true) || loanTitle.contains("RD", true) || loanTitle.contains("PPF", true) || loanTitle.contains("Lump", true) || loanTitle.contains("Annuity", true) || loanTitle.contains("Saving", true) -> "Investments"
            loanTitle.contains("Salary", true) || loanTitle.contains("Tax", true) || loanTitle.contains("CTC", true) || loanTitle.contains("Gross", true) || loanTitle.contains("Net", true) || loanTitle.contains("Pay", true) || loanTitle.contains("Gratuity", true) || loanTitle.contains("Buyout", true) -> "Salary"
            else -> "Utilities"
        }
        val details = "Principal: ${formatCurrency(loanAmount)} | Rate: $interestRate% | Term: $termText"
        com.example.calculatoremi.utils.HistoryManager.saveCalculation(
            context = this,
            title = loanTitle,
            category = category,
            primaryResultLabel = "Calculated Value",
            primaryResultValue = formatCurrency(emi),
            detailsSummary = details,
            loanAmount = loanAmount,
            interestRate = interestRate,
            years = years,
            months = months,
            startDate = startDate,
            emi = emi,
            totalInterest = totalInterest,
            totalCost = totalCost,
            payoffDate = payoffDate
        )

        // Action Buttons with Spring Feedback
        val btnBackHome = findViewById<MaterialButton>(R.id.btnBackHome)
        setupTouchScaleAnimation(btnBackHome)
        btnBackHome.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent)
            com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideOutTransition(this)
        }

        // --- PIE CHART CLICK LOGIC ---
        val btnPieChart = findViewById<LinearLayout>(R.id.btnPieChart)
        setupTouchScaleAnimation(btnPieChart)
        btnPieChart.setOnClickListener {
            val chartIntent = Intent(this, PieChartActivity::class.java).apply {
                putExtra("PRINCIPAL", loanAmount)
                putExtra("INTEREST", totalInterest)
                putExtra("TITLE", loanTitle)
            }
            startActivity(chartIntent)
            com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)
        }

        // --- PAYMENT SCHEDULE CLICK LOGIC ---
        val btnPaymentSchedule = findViewById<LinearLayout>(R.id.btnPaymentSchedule)
        setupTouchScaleAnimation(btnPaymentSchedule)
        btnPaymentSchedule.setOnClickListener {
            val scheduleIntent = Intent(this, AmortizationScheduleActivity::class.java).apply {
                putExtra("SCHEDULE", schedule)
            }
            startActivity(scheduleIntent)
            com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)
        }

        // --- COMPARE LOAN CLICK LOGIC ---
        val btnCompare = findViewById<MaterialButton>(R.id.btnCompare)
        setupTouchScaleAnimation(btnCompare)
        btnCompare.setOnClickListener {
            val compareIntent = Intent(this, LoanComparisonActivity::class.java).apply {
                putExtra("LOAN_A_AMOUNT", loanAmount)
                putExtra("LOAN_A_RATE", interestRate.toDouble())
                putExtra("LOAN_A_MONTHS", years * 12 + months)
            }
            startActivity(compareIntent)
            com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)
        }

        // Staggered Entrance Animation for Result Cards
        animateCardEntrance()
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 700
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            val currentValue = targetValue * fraction
            textView.text = formatCurrency(currentValue)
        }
        animator.start()
    }

    private fun animateCardEntrance() {
        val views = listOfNotNull(
            findViewById<View>(R.id.resEmi),
            findViewById<View>(R.id.btnPieChart),
            findViewById<View>(R.id.btnPaymentSchedule),
            findViewById<View>(R.id.btnCompare)
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 80).toLong())
                .setDuration(350)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    private inline fun <reified T : Serializable> getSerializableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            intent.getSerializableExtra(key) as? T
        }
    }
}