package com.example.calculatoremi.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class DiscountCalculatorActivity : BaseInputActivity() {

    private lateinit var etOriginalPrice: EditText
    private lateinit var etDiscountRate: EditText
    private lateinit var btnCalculateDiscount: MaterialButton
    private lateinit var cardDiscountResult: MaterialCardView
    private lateinit var txtDiscountSavings: TextView
    private lateinit var txtFinalPrice: TextView

    override fun getLayoutResId(): Int = R.layout.activity_discount_calculator

    override fun getActivityTitle(): String = "Discount Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etOriginalPrice = findViewById(R.id.etOriginalPrice)
        etDiscountRate = findViewById(R.id.etDiscountRate)
        btnCalculateDiscount = findViewById(R.id.btnCalculateDiscount)
        cardDiscountResult = findViewById(R.id.cardDiscountResult)
        txtDiscountSavings = findViewById(R.id.txtDiscountSavings)
        txtFinalPrice = findViewById(R.id.txtFinalPrice)

        btnCalculateDiscount.setOnClickListener {
            calculateDiscount()
        }
    }

    private fun calculateDiscount() {
        val originalPrice = etOriginalPrice.text.toString().toDoubleOrNull() ?: 0.0
        val discountRate = etDiscountRate.text.toString().toDoubleOrNull() ?: 0.0

        if (originalPrice <= 0 || discountRate < 0 || discountRate > 100) {
            Toast.makeText(this, "Please enter valid original price and discount rate (0-100%)", Toast.LENGTH_SHORT).show()
            return
        }

        val savings = originalPrice * (discountRate / 100.0)
        val finalPrice = originalPrice - savings

        txtDiscountSavings.text = formatCurrency(savings)
        txtFinalPrice.text = formatCurrency(finalPrice)
        cardDiscountResult.visibility = View.VISIBLE
    }
}
