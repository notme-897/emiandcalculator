package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow

class RentVsBuyActivity : BaseInputActivity() {

    private lateinit var etPropertyPrice: EditText
    private lateinit var etMonthlyRent: EditText
    private lateinit var etPropertyAppreciation: EditText
    private lateinit var etEquityReturn: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_rent_vs_buy

    override fun getActivityTitle(): String = "Rent vs Buy Decision Engine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPropertyPrice = findViewById(R.id.etPropertyPrice)
        etMonthlyRent = findViewById(R.id.etMonthlyRent)
        etPropertyAppreciation = findViewById(R.id.etPropertyAppreciation)
        etEquityReturn = findViewById(R.id.etEquityReturn)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etPropertyPrice)
        setupCommaFormatting(etMonthlyRent)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            analyzeRentVsBuyAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etPropertyPrice.setText("75,00,000")
        etMonthlyRent.setText("25,000")
        etPropertyAppreciation.setText("6.0")
        etEquityReturn.setText("12.0")
    }

    private fun setupCommaFormatting(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                try {
                    val rawString = s.toString().replace(",", "")
                    if (rawString.isNotEmpty()) {
                        val doubleVal = rawString.toDoubleOrNull()
                        if (doubleVal != null && doubleVal > 0) {
                            val formatted = commaFormat.format(doubleVal)
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
                isFormatting = false
            }
        })
    }

    private fun setupButtonAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(OvershootInterpolator(2.2f))
                        .setDuration(160)
                        .start()
                }
            }
            false
        }
    }

    private fun analyzeRentVsBuyAndNavigate() {
        val priceStr = etPropertyPrice.text.toString().replace(",", "")
        val rentStr = etMonthlyRent.text.toString().replace(",", "")
        val propGrowthStr = etPropertyAppreciation.text.toString()
        val equityGrowthStr = etEquityReturn.text.toString()

        val propertyPrice = priceStr.toDoubleOrNull() ?: 0.0
        val monthlyRent = rentStr.toDoubleOrNull() ?: 0.0
        val propGrowthRate = (propGrowthStr.toDoubleOrNull() ?: 6.0) / 100.0
        val equityGrowthRate = (equityGrowthStr.toDoubleOrNull() ?: 12.0) / 100.0

        if (propertyPrice <= 0 || monthlyRent <= 0) {
            Toast.makeText(this, "Please enter valid property price and rent", Toast.LENGTH_SHORT).show()
            return
        }

        val horizonYears = 20
        val downPayment = propertyPrice * 0.20 // 20% down payment
        val loanAmount = propertyPrice * 0.80  // 80% home loan
        val homeLoanInterestRate = 0.0875       // 8.75% home loan rate

        // Home Loan EMI
        val r = homeLoanInterestRate / 12.0
        val n = horizonYears * 12
        val emi = (loanAmount * r * (1.0 + r).pow(n.toDouble())) / ((1.0 + r).pow(n.toDouble()) - 1.0)

        // Scenario A: Buying
        val futurePropertyValue = propertyPrice * (1.0 + propGrowthRate).pow(horizonYears.toDouble())
        val totalEmiPaid = emi * n
        val buyingNetWorth = futurePropertyValue - totalEmiPaid + loanAmount // Net worth from home

        // Scenario B: Renting & Investing Down Payment + EMI Surplus in Equity SIP
        val futureDownPaymentCorpus = downPayment * (1.0 + equityGrowthRate).pow(horizonYears.toDouble())

        // Monthly SIP from difference (EMI - Rent)
        val initialSipContribution = maxOf(0.0, emi - monthlyRent)
        val monthlyEquityReturn = (1.0 + equityGrowthRate).pow(1.0 / 12.0) - 1.0

        var sipCorpus = 0.0
        for (month in 1..n) {
            sipCorpus = (sipCorpus + initialSipContribution) * (1.0 + monthlyEquityReturn)
        }

        val totalRenterWealth = futureDownPaymentCorpus + sipCorpus

        val netDifference = abs(totalRenterWealth - buyingNetWorth)

        val recommendation = if (totalRenterWealth > buyingNetWorth) {
            "🎉 Renting & Investing saves ₹${commaFormat.format(netDifference.toInt())} more in 20-year net worth!"
        } else {
            "🎉 Buying Property creates ₹${commaFormat.format(netDifference.toInt())} more in 20-year net worth!"
        }

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Property Purchase Price", propertyPrice, propertyPrice / 240.0, 0.0, propertyPrice))
        scheduleList.add(PaymentScheduleItem(2, "Buying: Future Property Value (20Y @ ${String.format("%.1f", propGrowthRate * 100)}%)", futurePropertyValue, futurePropertyValue / 240.0, 0.0, futurePropertyValue))
        scheduleList.add(PaymentScheduleItem(3, "Buying: Net Buying Equity (Val - EMIs)", buyingNetWorth, buyingNetWorth / 240.0, 0.0, buyingNetWorth))
        scheduleList.add(PaymentScheduleItem(4, "Renting: Down Payment Equity Corpus (20Y @ ${String.format("%.1f", equityGrowthRate * 100)}%)", futureDownPaymentCorpus, futureDownPaymentCorpus / 240.0, 0.0, futureDownPaymentCorpus))
        scheduleList.add(PaymentScheduleItem(5, "Renting: Monthly EMI-Rent Savings SIP Corpus", sipCorpus, sipCorpus / 240.0, 0.0, sipCorpus))
        scheduleList.add(PaymentScheduleItem(6, "Renting: Total 20-Year Renter Net Worth", totalRenterWealth, totalRenterWealth / 240.0, 0.0, totalRenterWealth))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Rent vs Buy Decision Result")
            putExtra("LOAN_AMOUNT", totalRenterWealth) // Renter Wealth
            putExtra("INTEREST_RATE", (equityGrowthRate * 100).toFloat())
            putExtra("LOAN_TERM_YEARS", 20)
            putExtra("LOAN_TERM_MONTHS", 240)
            putExtra("START_DATE", recommendation)
            putExtra("EMI", emi)
            putExtra("TOTAL_INTEREST", buyingNetWorth)
            putExtra("TOTAL_COST", propertyPrice)
            putExtra("PAYOFF_DATE", "Wealth Difference: ₹" + commaFormat.format(netDifference.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPropertyPrice.setText("75,00,000")
        etMonthlyRent.setText("25,000")
        etPropertyAppreciation.setText("6.0")
        etEquityReturn.setText("12.0")
    }
}
