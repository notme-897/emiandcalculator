package com.example.calculatoremi.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton

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
        findViewById<TextView>(R.id.txtResultTitle).text = "Personal Loan Result"
        findViewById<ImageView>(R.id.btnBackResult).setOnClickListener { finish() }

        // Get Data from Intent
        val loanAmount = intent.getDoubleExtra("LOAN_AMOUNT", 0.0)
        val interestRate = intent.getFloatExtra("INTEREST_RATE", 0.0f)
        val loanTerm = intent.getIntExtra("LOAN_TERM", 0)
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        
        val emi = intent.getDoubleExtra("EMI", 0.0)
        val totalInterest = intent.getDoubleExtra("TOTAL_INTEREST", 0.0)
        val totalCost = intent.getDoubleExtra("TOTAL_COST", 0.0)
        val payoffDate = intent.getStringExtra("PAYOFF_DATE") ?: ""

        // Display Data
        findViewById<TextView>(R.id.resLoanAmount).text = "₹${String.format("%,.0f", loanAmount)}"
        findViewById<TextView>(R.id.resInterestRate).text = "$interestRate%"
        findViewById<TextView>(R.id.resLoanTerm).text = "$loanTerm Years"
        findViewById<TextView>(R.id.resStartDate).text = startDate

        findViewById<TextView>(R.id.resEmi).text = "₹${String.format("%,.0f", emi)}"
        findViewById<TextView>(R.id.resTotalInterest).text = "₹${String.format("%,.0f", totalInterest)}"
        findViewById<TextView>(R.id.resTotalCost).text = "₹${String.format("%,.0f", totalCost)}"
        findViewById<TextView>(R.id.resPayoffDate).text = payoffDate

        // Action Buttons
        findViewById<MaterialButton>(R.id.btnBackHome).setOnClickListener {
            finish() // Or navigate back to Home clear top
        }
    }
}