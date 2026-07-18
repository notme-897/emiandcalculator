package com.example.calculatoremi.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import kotlin.math.pow

class PersonalLoanActivity : BaseLoanActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etRate: EditText
    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValue: TextView
    private lateinit var txtInstallments: TextView
    private lateinit var btnCalculate: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Find Views
        etAmount = findViewById(R.id.editTextNumber)
        etRate = findViewById(R.id.editTextNumber2)
        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValue = findViewById(R.id.txtMinYears) // We'll use this for real-time display
        txtInstallments = findViewById(R.id.txtInstallments)
        btnCalculate = findViewById(R.id.btnCalculate)

        // Setup SeekBar logic
        seekBarTerm.max = 30
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                txtInstallments.text = "${progress * 12} installments"
                txtTermValue.text = "$progress years"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Calculate Button Logic
        btnCalculate.setOnClickListener {
            calculateAndNavigate()
        }
    }

    private fun calculateAndNavigate() {
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = seekBarTerm.progress

        if (amount > 0 && rate > 0 && years > 0) {
            val monthlyRate = rate / (12 * 100)
            val months = years * 12
            
            // EMI Calculation Formula: [P x R x (1+R)^N]/[(1+R)^N-1]
            val emi = (amount * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) / 
                      ((1 + monthlyRate).pow(months.toDouble()) - 1)
            
            val totalCost = emi * months
            val totalInterest = totalCost - amount
            
            val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
                putExtra("LOAN_AMOUNT", amount)
                putExtra("INTEREST_RATE", rate.toFloat())
                putExtra("LOAN_TERM", years)
                putExtra("START_DATE", "17 Jul, 2026") // Dynamic date to be added next
                putExtra("EMI", emi)
                putExtra("TOTAL_INTEREST", totalInterest)
                putExtra("TOTAL_COST", totalCost)
                putExtra("PAYOFF_DATE", "17 Jul, ${2026 + years}")
            }
            startActivity(intent)
        }
    }

    override fun getLoanTitle(): String = "Personal Loan"
}