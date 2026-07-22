package com.example.calculatoremi.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlin.math.pow

class FdCalculatorActivity : BaseInputActivity() {

    private lateinit var etFdAmount: EditText
    private lateinit var etFdRate: EditText
    private lateinit var etFdYears: EditText
    private lateinit var btnCalculateFd: MaterialButton
    private lateinit var cardFdResult: MaterialCardView
    private lateinit var txtFdPrincipal: TextView
    private lateinit var txtFdInterest: TextView
    private lateinit var txtFdMaturity: TextView

    override fun getLayoutResId(): Int = R.layout.activity_fd_calculator

    override fun getActivityTitle(): String = "FD Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etFdAmount = findViewById(R.id.etFdAmount)
        etFdRate = findViewById(R.id.etFdRate)
        etFdYears = findViewById(R.id.etFdYears)
        btnCalculateFd = findViewById(R.id.btnCalculateFd)
        cardFdResult = findViewById(R.id.cardFdResult)
        txtFdPrincipal = findViewById(R.id.txtFdPrincipal)
        txtFdInterest = findViewById(R.id.txtFdInterest)
        txtFdMaturity = findViewById(R.id.txtFdMaturity)

        btnCalculateFd.setOnClickListener {
            calculateFd()
        }
    }

    private fun calculateFd() {
        val amount = etFdAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etFdRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etFdYears.text.toString().toIntOrNull() ?: 0

        if (amount <= 0 || rate <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid deposit amount, rate, and years", Toast.LENGTH_SHORT).show()
            return
        }

        // FD Compound Interest Formula (Quarterly compounding n=4)
        val n = 4.0
        val r = rate / 100.0
        val maturity = amount * (1 + r / n).pow(n * years)
        val interest = maturity - amount

        txtFdPrincipal.text = formatCurrency(amount)
        txtFdInterest.text = formatCurrency(interest)
        txtFdMaturity.text = formatCurrency(maturity)
        cardFdResult.visibility = View.VISIBLE
    }
}
