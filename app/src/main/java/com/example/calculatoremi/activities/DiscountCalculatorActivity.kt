package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat

class DiscountCalculatorActivity : BaseInputActivity() {

    private lateinit var etOriginalPrice: EditText
    private lateinit var etDiscountRate: EditText
    private lateinit var btnCalculateDiscount: MaterialButton
    private lateinit var cardDiscountResult: MaterialCardView
    private lateinit var txtDiscountSavings: TextView
    private lateinit var txtFinalPrice: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_discount_calculator

    override fun getActivityTitle(): String = "Discount Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etOriginalPrice = findViewById(R.id.etOriginalPrice)
        etDiscountRate = findViewById(R.id.etDiscountRate)
        btnCalculateDiscount = findViewById(R.id.btnCalculateDiscount)
        cardDiscountResult = findViewById(R.id.cardDiscountResult)
        txtDiscountSavings = findViewById(R.id.txtDiscountSavings)
        txtFinalPrice = findViewById(R.id.txtFinalPrice)

        setupTouchScaleAnimation(btnCalculateDiscount)
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

        animateNumberCounter(txtDiscountSavings, savings)
        animateNumberCounter(txtFinalPrice, finalPrice)

        if (cardDiscountResult.visibility != View.VISIBLE) {
            cardDiscountResult.alpha = 0f
            cardDiscountResult.translationY = 40f
            cardDiscountResult.visibility = View.VISIBLE
            cardDiscountResult.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 750
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            textView.text = "₹" + decimalFormat.format(currentValue.toDouble())
        }
        animator.start()
    }
}

