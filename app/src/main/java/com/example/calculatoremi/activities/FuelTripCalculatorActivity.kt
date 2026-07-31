package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class FuelTripCalculatorActivity : BaseInputActivity() {

    private lateinit var etDistance: EditText
    private lateinit var etMileage: EditText
    private lateinit var etFuelPrice: EditText
    private lateinit var etTravelers: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_fuel_trip

    override fun getActivityTitle(): String = "Fuel Cost & Trip Estimator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etDistance = findViewById(R.id.etDistance)
        etMileage = findViewById(R.id.etMileage)
        etFuelPrice = findViewById(R.id.etFuelPrice)
        etTravelers = findViewById(R.id.etTravelers)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)
        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        etDistance.setText("350")
        etMileage.setText("16.5")
        etFuelPrice.setText("96.72")
        etTravelers.setText("4")
    }

    private fun calculateAndNavigate() {
        val dist = etDistance.text.toString().toDoubleOrNull() ?: 0.0
        val mileage = etMileage.text.toString().toDoubleOrNull() ?: 0.0
        val price = etFuelPrice.text.toString().toDoubleOrNull() ?: 0.0
        val travelers = (etTravelers.text.toString().toIntOrNull() ?: 1).coerceAtLeast(1)

        if (dist <= 0 || mileage <= 0 || price <= 0) {
            Toast.makeText(this, "Please enter valid distance, mileage, and fuel price", Toast.LENGTH_SHORT).show()
            return
        }

        val liters = dist / mileage
        val totalCost = liters * price
        val perPerson = totalCost / travelers

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Fuel & Trip Cost Result")
            putExtra("LOAN_AMOUNT", totalCost)
            putExtra("INTEREST_RATE", mileage.toFloat())
            putExtra("LOAN_TERM_YEARS", 0)
            putExtra("LOAN_TERM_MONTHS", travelers)
            putExtra("START_DATE", "$dist km @ $mileage km/L")
            putExtra("EMI", perPerson)
            putExtra("TOTAL_INTEREST", 0.0)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", "Per Person: ₹" + commaFormat.format(perPerson))
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etDistance.setText("350")
        etMileage.setText("16.5")
        etFuelPrice.setText("96.72")
        etTravelers.setText("4")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
