package com.example.calculatoremi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.util.*

class PersonalLoanResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loan_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resultHeader)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Setup Header
        val loanTitle = intent.getStringExtra("TITLE") ?: "Personal Loan"
        findViewById<TextView>(R.id.txtResultTitle).text = loanTitle
        findViewById<ImageView>(R.id.btnBackResult).setOnClickListener { finish() }

        // Get Data from Intent
        val loanAmount = intent.getDoubleExtra("LOAN_AMOUNT", 0.0)
        val interestRate = intent.getFloatExtra("INTEREST_RATE", 0.0f)
        val years = intent.getIntExtra("LOAN_TERM_YEARS", 0)
        val months = intent.getIntExtra("LOAN_TERM_MONTHS", 0)
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        
        val emi = intent.getDoubleExtra("EMI", 0.0)
        val totalInterest = intent.getDoubleExtra("TOTAL_INTEREST", 0.0)
        val totalCost = intent.getDoubleExtra("TOTAL_COST", 0.0)
        val payoffDate = intent.getStringExtra("PAYOFF_DATE") ?: ""
        
        @Suppress("UNCHECKED_CAST")
        val schedule = intent.getSerializableExtra("SCHEDULE") as? ArrayList<PaymentScheduleItem>

        // Display Data
        findViewById<TextView>(R.id.resLoanAmount).text = "${formatCurrency(loanAmount)} $"
        findViewById<TextView>(R.id.resInterestRate).text = "$interestRate %"
        
        val termText = if (years > 0 && months > 0) {
            "$months months $years years"
        } else if (years > 0) {
            "$years years"
        } else {
            "$months months"
        }
        findViewById<TextView>(R.id.resLoanTerm).text = termText
        findViewById<TextView>(R.id.resStartDate).text = startDate

        findViewById<TextView>(R.id.resEmi).text = "${formatCurrency(emi)} $"
        findViewById<TextView>(R.id.resTotalInterest).text = "${formatCurrency(totalInterest)} $"
        findViewById<TextView>(R.id.resTotalCost).text = "${formatCurrency(totalCost)} $"
        findViewById<TextView>(R.id.resPayoffDate).text = payoffDate

        // Action Buttons
        findViewById<MaterialButton>(R.id.btnBackHome).setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent)
        }

        // --- NEW PIE CHART CLICK LOGIC ---
        findViewById<LinearLayout>(R.id.btnPieChart).setOnClickListener {
            val chartIntent = Intent(this, PieChartActivity::class.java).apply {
                putExtra("PRINCIPAL", loanAmount)
                putExtra("INTEREST", totalInterest)
                putExtra("TITLE", loanTitle)
            }
            startActivity(chartIntent)
        }

        findViewById<LinearLayout>(R.id.btnPaymentSchedule).setOnClickListener {
            // Future Payment Schedule Implementation
        }
        
        findViewById<ImageView>(R.id.btnShare).setOnClickListener {
            shareResult(loanAmount, emi, totalInterest, totalCost)
        }
    }

    private fun formatCurrency(value: Double): String {
        return String.format(Locale.US, "%,.2f", value)
    }

    private fun shareResult(amount: Double, emi: Double, interest: Double, cost: Double) {
        val shareBody = """
            EMI Calculator Result
            ---------------------
            Loan Amount: ${formatCurrency(amount)} $
            Monthly EMI: ${formatCurrency(emi)} $
            Total Interest: ${formatCurrency(interest)} $
            Total Cost: ${formatCurrency(cost)} $
            ---------------------
            Calculated via Finance Hub
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Loan Calculation Result")
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}