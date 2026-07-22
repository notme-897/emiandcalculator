package com.example.calculatoremi.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class RdCalculatorActivity : BaseInputActivity() {

    private lateinit var etRdAmount: EditText
    private lateinit var etRdRate: EditText
    private lateinit var etRdMonths: EditText
    private lateinit var btnCalculateRd: MaterialButton
    private lateinit var cardRdResult: MaterialCardView
    private lateinit var txtRdInvested: TextView
    private lateinit var txtRdInterest: TextView
    private lateinit var txtRdMaturity: TextView

    override fun getLayoutResId(): Int = R.layout.activity_rd_calculator

    override fun getActivityTitle(): String = "RD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etRdAmount = findViewById(R.id.etRdAmount)
        etRdRate = findViewById(R.id.etRdRate)
        etRdMonths = findViewById(R.id.etRdMonths)
        btnCalculateRd = findViewById(R.id.btnCalculateRd)
        cardRdResult = findViewById(R.id.cardRdResult)
        txtRdInvested = findViewById(R.id.txtRdInvested)
        txtRdInterest = findViewById(R.id.txtRdInterest)
        txtRdMaturity = findViewById(R.id.txtRdMaturity)

        btnCalculateRd.setOnClickListener {
            calculateRd()
        }
    }

    private fun calculateRd() {
        val amount = etRdAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRdRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = etRdMonths.text.toString().toIntOrNull() ?: 0

        if (amount <= 0 || rate <= 0 || months <= 0) {
            Toast.makeText(this, "Please enter valid monthly deposit, rate, and months", Toast.LENGTH_SHORT).show()
            return
        }

        // RD Standard Formula: I = P * N(N+1)/2 * (R/1200)
        val interest = amount * (months * (months + 1) / 2.0) * (rate / 1200.0)
        val totalInvested = amount * months
        val maturity = totalInvested + interest

        txtRdInvested.text = formatCurrency(totalInvested)
        txtRdInterest.text = formatCurrency(interest)
        txtRdMaturity.text = formatCurrency(maturity)
        cardRdResult.visibility = View.VISIBLE
    }
}
