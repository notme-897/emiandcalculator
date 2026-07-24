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

class GstCalculatorActivity : BaseInputActivity() {

    private lateinit var etGstAmount: EditText
    private lateinit var etGstRate: EditText
    private lateinit var btnAddGst: MaterialButton
    private lateinit var btnRemoveGst: MaterialButton
    private lateinit var cardGstResult: MaterialCardView
    private lateinit var txtGstNet: TextView
    private lateinit var txtGstTax: TextView
    private lateinit var txtGstGross: TextView

    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_gst_calculator

    override fun getActivityTitle(): String = "GST Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        etGstAmount = findViewById(R.id.etGstAmount)
        etGstRate = findViewById(R.id.etGstRate)
        btnAddGst = findViewById(R.id.btnAddGst)
        btnRemoveGst = findViewById(R.id.btnRemoveGst)
        cardGstResult = findViewById(R.id.cardGstResult)
        txtGstNet = findViewById(R.id.txtGstNet)
        txtGstTax = findViewById(R.id.txtGstTax)
        txtGstGross = findViewById(R.id.txtGstGross)

        setupTouchScaleAnimation(btnAddGst)
        setupTouchScaleAnimation(btnRemoveGst)

        btnAddGst.setOnClickListener {
            calculateGst(isInclusive = false)
        }

        btnRemoveGst.setOnClickListener {
            calculateGst(isInclusive = true)
        }
    }

    private fun calculateGst(isInclusive: Boolean) {
        val amount = etGstAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etGstRate.text.toString().toDoubleOrNull() ?: 0.0

        if (amount <= 0 || rate <= 0) {
            Toast.makeText(this, "Please enter valid amount and GST rate", Toast.LENGTH_SHORT).show()
            return
        }

        val netAmount: Double
        val gstTax: Double
        val grossAmount: Double

        if (!isInclusive) {
            // Exclusive GST: Add GST to amount
            netAmount = amount
            gstTax = amount * (rate / 100.0)
            grossAmount = netAmount + gstTax
        } else {
            // Inclusive GST: Extract GST from total
            grossAmount = amount
            netAmount = grossAmount / (1 + (rate / 100.0))
            gstTax = grossAmount - netAmount
        }

        animateNumberCounter(txtGstNet, netAmount)
        animateNumberCounter(txtGstTax, gstTax)
        animateNumberCounter(txtGstGross, grossAmount)

        if (cardGstResult.visibility != View.VISIBLE) {
            cardGstResult.alpha = 0f
            cardGstResult.translationY = 40f
            cardGstResult.visibility = View.VISIBLE
            cardGstResult.animate()
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

