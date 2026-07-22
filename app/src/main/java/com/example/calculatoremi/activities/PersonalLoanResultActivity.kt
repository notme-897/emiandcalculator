package com.example.calculatoremi.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
    private var loanTitle: String = "Personal Loan"

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

        // Display Data
        findViewById<TextView>(R.id.resLoanAmount).text = formatCurrency(loanAmount)
        findViewById<TextView>(R.id.resInterestRate).text = "$interestRate %"
        
        val termText = when {
            years > 0 && months > 0 -> "$years years $months months"
            years > 0 -> "$years years"
            else -> "$months months"
        }
        findViewById<TextView>(R.id.resLoanTerm).text = termText
        findViewById<TextView>(R.id.resStartDate).text = startDate

        findViewById<TextView>(R.id.resEmi).text = formatCurrency(emi)
        findViewById<TextView>(R.id.resTotalInterest).text = formatCurrency(totalInterest)
        findViewById<TextView>(R.id.resTotalCost).text = formatCurrency(totalCost)
        findViewById<TextView>(R.id.resPayoffDate).text = payoffDate

        // Action Buttons
        findViewById<MaterialButton>(R.id.btnBackHome).setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent)
        }

        // --- PIE CHART CLICK LOGIC ---
        findViewById<LinearLayout>(R.id.btnPieChart).setOnClickListener {
            val chartIntent = Intent(this, PieChartActivity::class.java).apply {
                putExtra("PRINCIPAL", loanAmount)
                putExtra("INTEREST", totalInterest)
                putExtra("TITLE", loanTitle)
            }
            startActivity(chartIntent)
        }

        // --- PAYMENT SCHEDULE CLICK LOGIC ---
        findViewById<LinearLayout>(R.id.btnPaymentSchedule).setOnClickListener {
            val scheduleIntent = Intent(this, AmortizationScheduleActivity::class.java).apply {
                putExtra("SCHEDULE", schedule)
            }
            startActivity(scheduleIntent)
        }

        // --- COMPARE LOAN CLICK LOGIC ---
        findViewById<MaterialButton>(R.id.btnCompare).setOnClickListener {
            val compareIntent = Intent(this, LoanComparisonActivity::class.java).apply {
                putExtra("LOAN_A_AMOUNT", loanAmount)
                putExtra("LOAN_A_RATE", interestRate.toDouble())
                putExtra("LOAN_A_MONTHS", years * 12 + months)
            }
            startActivity(compareIntent)
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