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
import java.text.DecimalFormat

class PersonalLoanResultActivity : BaseResultActivity() {

    private var loanAmount: Double = 0.0
    private var emi: Double = 0.0
    private var totalInterest: Double = 0.0
    private var totalCost: Double = 0.0
    private var loanTitle: String = "Personal Loan"
    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getResultLayoutResId(): Int = R.layout.activity_loan_result

    override fun getResultTitle(): String = intent.getStringExtra("TITLE") ?: "Personal Loan"

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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        loanTitle = getResultTitle()

        // Get Data from Intent
        loanAmount = intent.getDoubleExtra("LOAN_AMOUNT", 0.0)
        val interestRate = intent.getFloatExtra("INTEREST_RATE", 0.0f)
        val years = intent.getIntExtra("LOAN_TERM_YEARS", 0)
        val months = intent.getIntExtra("LOAN_TERM_MONTHS", 0)
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        
        emi = intent.getDoubleExtra("EMI", 0.0)
        totalInterest = intent.getDoubleExtra("TOTAL_INTEREST", 0.0)
        totalCost = intent.getDoubleExtra("TOTAL_COST", 0.0)
        val payoffDate = intent.getStringExtra("PAYOFF_DATE") ?: ""
        
        val schedule = getSerializableExtraCompat<ArrayList<PaymentScheduleItem>>("SCHEDULE")

        // Static Info Fields
        findViewById<TextView>(R.id.resInterestRate).text = "$interestRate %"
        
        val termText = when {
            years > 0 && months > 0 -> "$years years $months months"
            years > 0 -> "$years years"
            else -> "$months months"
        }
        findViewById<TextView>(R.id.resLoanTerm).text = termText
        findViewById<TextView>(R.id.resStartDate).text = startDate
        findViewById<TextView>(R.id.resPayoffDate).text = payoffDate

        // Animate Result Monetary Values (Rolling Counter Animation)
        val txtEmi = findViewById<TextView>(R.id.resEmi)
        val txtLoanAmount = findViewById<TextView>(R.id.resLoanAmount)
        val txtTotalInterest = findViewById<TextView>(R.id.resTotalInterest)
        val txtTotalCost = findViewById<TextView>(R.id.resTotalCost)

        animateNumberCounter(txtEmi, emi)
        animateNumberCounter(txtLoanAmount, loanAmount)
        animateNumberCounter(txtTotalInterest, totalInterest)
        animateNumberCounter(txtTotalCost, totalCost)

        // Action Buttons with Spring Feedback
        val btnBackHome = findViewById<MaterialButton>(R.id.btnBackHome)
        setupTouchScaleAnimation(btnBackHome)
        btnBackHome.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // --- PAYMENT SCHEDULE CLICK LOGIC ---
        val btnPaymentSchedule = findViewById<LinearLayout>(R.id.btnPaymentSchedule)
        setupTouchScaleAnimation(btnPaymentSchedule)
        btnPaymentSchedule.setOnClickListener {
            val scheduleIntent = Intent(this, AmortizationScheduleActivity::class.java).apply {
                putExtra("SCHEDULE", schedule)
            }
            startActivity(scheduleIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Staggered Entrance Animation for Result Cards
        animateCardEntrance()
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 750
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            textView.text = "₹" + decimalFormat.format(currentValue.toDouble())
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