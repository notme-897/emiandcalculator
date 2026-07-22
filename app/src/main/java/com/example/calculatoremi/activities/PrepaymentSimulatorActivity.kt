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

class PrepaymentSimulatorActivity : BaseInputActivity() {

    private lateinit var etBalance: EditText
    private lateinit var etRate: EditText
    private lateinit var etMonths: EditText
    private lateinit var etExtraPay: EditText
    private lateinit var btnSimulate: MaterialButton
    private lateinit var cardResult: MaterialCardView
    private lateinit var txtInterestSaved: TextView
    private lateinit var txtTimeSaved: TextView

    override fun getLayoutResId(): Int = R.layout.activity_prepayment_simulator

    override fun getActivityTitle(): String = "Prepayment Simulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etBalance = findViewById(R.id.etBalance)
        etRate = findViewById(R.id.etRate)
        etMonths = findViewById(R.id.etMonths)
        etExtraPay = findViewById(R.id.etExtraPay)
        btnSimulate = findViewById(R.id.btnSimulate)
        cardResult = findViewById(R.id.cardResult)
        txtInterestSaved = findViewById(R.id.txtInterestSaved)
        txtTimeSaved = findViewById(R.id.txtTimeSaved)

        btnSimulate.setOnClickListener {
            runSimulation()
        }
    }

    private fun runSimulation() {
        val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = etMonths.text.toString().toIntOrNull() ?: 0
        val extraPay = etExtraPay.text.toString().toDoubleOrNull() ?: 0.0

        if (balance <= 0 || rate <= 0 || months <= 0) {
            Toast.makeText(this, "Please enter valid balance, rate, and months", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = rate / (12 * 100)
        val originalEmi = (balance * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) /
                ((1 + monthlyRate).pow(months.toDouble()) - 1)
        val originalTotalInterest = (originalEmi * months) - balance

        val newMonthlyPayment = originalEmi + extraPay

        // Simulate new payoff
        var remBalance = balance
        var newTotalInterest = 0.0
        var newMonthsCount = 0

        while (remBalance > 0 && newMonthsCount < months * 2) {
            val monthInterest = remBalance * monthlyRate
            val monthPrincipal = newMonthlyPayment - monthInterest
            remBalance -= monthPrincipal
            newTotalInterest += monthInterest
            newMonthsCount++
            if (remBalance < 0) break
        }

        val interestSaved = originalTotalInterest - newTotalInterest
        val monthsSaved = months - newMonthsCount

        cardResult.visibility = View.VISIBLE
        txtInterestSaved.text = "Total Interest Saved: ${formatCurrency(if (interestSaved > 0) interestSaved else 0.0)}"
        txtTimeSaved.text = "Tenure Reduced By: ${if (monthsSaved > 0) monthsSaved else 0} months (${formatTerm(if (monthsSaved > 0) monthsSaved else 0)})"
    }
}
