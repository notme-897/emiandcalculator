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

class UnitPriceComparatorActivity : BaseInputActivity() {

    private lateinit var etPriceA: EditText
    private lateinit var etQtyA: EditText
    private lateinit var etPriceB: EditText
    private lateinit var etQtyB: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_unit_price_comparator

    override fun getActivityTitle(): String = "Unit Price Shopping Comparator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etPriceA = findViewById(R.id.etPriceA)
        etQtyA = findViewById(R.id.etQtyA)
        etPriceB = findViewById(R.id.etPriceB)
        etQtyB = findViewById(R.id.etQtyB)

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

        etPriceA.setText("180")
        etQtyA.setText("500")
        etPriceB.setText("320")
        etQtyB.setText("1000")
    }

    private fun calculateAndNavigate() {
        val priceA = etPriceA.text.toString().toDoubleOrNull() ?: 0.0
        val qtyA = etQtyA.text.toString().toDoubleOrNull() ?: 0.0
        val priceB = etPriceB.text.toString().toDoubleOrNull() ?: 0.0
        val qtyB = etQtyB.text.toString().toDoubleOrNull() ?: 0.0

        if (priceA <= 0 || qtyA <= 0 || priceB <= 0 || qtyB <= 0) {
            Toast.makeText(this, "Please fill all fields for both Item A and Item B", Toast.LENGTH_SHORT).show()
            return
        }

        val unitA = priceA / qtyA
        val unitB = priceB / qtyB

        val winner = if (unitA < unitB) "Item A" else if (unitB < unitA) "Item B" else "Equal"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Unit Price Comparison")
            putExtra("LOAN_AMOUNT", priceA)
            putExtra("INTEREST_RATE", qtyA.toFloat())
            putExtra("LOAN_TERM_YEARS", 0)
            putExtra("LOAN_TERM_MONTHS", 2)
            putExtra("START_DATE", "A: ₹${decimalFormat.format(priceA)} / $qtyA vs B: ₹${decimalFormat.format(priceB)} / $qtyB")
            putExtra("EMI", unitA)
            putExtra("TOTAL_INTEREST", unitB)
            putExtra("TOTAL_COST", priceB)
            putExtra("PAYOFF_DATE", "Cheaper Option: $winner")
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPriceA.setText("180")
        etQtyA.setText("500")
        etPriceB.setText("320")
        etQtyB.setText("1000")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
