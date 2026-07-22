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

class SipCalculatorActivity : BaseInputActivity() {

    private lateinit var etMonthlySip: EditText
    private lateinit var etSipRate: EditText
    private lateinit var etSipYears: EditText
    private lateinit var btnCalculateSip: MaterialButton
    private lateinit var cardSipResult: MaterialCardView
    private lateinit var txtTotalInvested: TextView
    private lateinit var txtEstReturns: TextView
    private lateinit var txtMaturityValue: TextView

    override fun getLayoutResId(): Int = R.layout.activity_sip_calculator

    override fun getActivityTitle(): String = "SIP Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etMonthlySip = findViewById(R.id.etMonthlySip)
        etSipRate = findViewById(R.id.etSipRate)
        etSipYears = findViewById(R.id.etSipYears)
        btnCalculateSip = findViewById(R.id.btnCalculateSip)
        cardSipResult = findViewById(R.id.cardSipResult)
        txtTotalInvested = findViewById(R.id.txtTotalInvested)
        txtEstReturns = findViewById(R.id.txtEstReturns)
        txtMaturityValue = findViewById(R.id.txtMaturityValue)

        btnCalculateSip.setOnClickListener {
            calculateSip()
        }
    }

    private fun calculateSip() {
        val monthlySip = etMonthlySip.text.toString().toDoubleOrNull() ?: 0.0
        val expectedRate = etSipRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etSipYears.text.toString().toIntOrNull() ?: 0

        if (monthlySip <= 0 || expectedRate <= 0 || years <= 0) {
            Toast.makeText(this, "Please enter valid monthly investment, rate, and years", Toast.LENGTH_SHORT).show()
            return
        }

        val totalMonths = years * 12
        val i = expectedRate / (12 * 100)

        // SIP Formula: M = P * ({[1 + i]^n - 1} / i) * (1 + i)
        val maturity = monthlySip * (((1 + i).pow(totalMonths.toDouble()) - 1) / i) * (1 + i)
        val totalInvested = monthlySip * totalMonths
        val estimatedReturns = maturity - totalInvested

        txtTotalInvested.text = formatCurrency(totalInvested)
        txtEstReturns.text = formatCurrency(estimatedReturns)
        txtMaturityValue.text = formatCurrency(maturity)
        cardSipResult.visibility = View.VISIBLE
    }
}
