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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import kotlin.math.max

class DiscountCalculatorActivity : BaseInputActivity() {

    private lateinit var etOriginalPrice: EditText
    private lateinit var etDiscountRate: EditText
    private lateinit var etFlatDiscount: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_discount_calculator

    override fun getActivityTitle(): String = "Discount & Shopping Savings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etOriginalPrice = findViewById(R.id.etOriginalPrice)
        etDiscountRate = findViewById(R.id.etDiscountRate)
        etFlatDiscount = findViewById(R.id.etFlatDiscount)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etOriginalPrice)
        setupCommaFormatting(etFlatDiscount)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateDiscountAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etOriginalPrice.setText("5,000")
        etDiscountRate.setText("20")
        etFlatDiscount.setText("0")
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
                } catch (e: Exception) {}
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

    private fun calculateDiscountAndNavigate() {
        val originalPrice = etOriginalPrice.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val discountRate = etDiscountRate.text.toString().toDoubleOrNull() ?: 0.0
        val flatDiscount = etFlatDiscount.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (originalPrice <= 0 || discountRate < 0 || discountRate > 100) {
            Toast.makeText(this, "Please enter valid original price and discount rate (0-100%)", Toast.LENGTH_SHORT).show()
            return
        }

        val pctSavings = originalPrice * (discountRate / 100.0)
        val totalSavings = pctSavings + flatDiscount
        val finalNetPrice = max(0.0, originalPrice - totalSavings)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Original Tag Price", originalPrice, originalPrice / 12.0, 0.0, originalPrice))
        scheduleList.add(PaymentScheduleItem(2, "Percentage Off (${discountRate.toInt()}%)", pctSavings, pctSavings / 12.0, 0.0, pctSavings))
        scheduleList.add(PaymentScheduleItem(3, "Additional Coupon Discount", flatDiscount, flatDiscount / 12.0, 0.0, flatDiscount))
        scheduleList.add(PaymentScheduleItem(4, "Total Discount Saved", totalSavings, totalSavings / 12.0, 0.0, totalSavings))
        scheduleList.add(PaymentScheduleItem(5, "Final Net Sale Price", finalNetPrice, finalNetPrice / 12.0, 0.0, finalNetPrice))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Discount Savings Result")
            putExtra("LOAN_AMOUNT", finalNetPrice)
            putExtra("INTEREST_RATE", discountRate.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", "Saved ₹${commaFormat.format(totalSavings.toInt())} (${discountRate.toInt()}% Off)")
            putExtra("EMI", finalNetPrice)
            putExtra("TOTAL_INTEREST", totalSavings)
            putExtra("TOTAL_COST", originalPrice)
            putExtra("PAYOFF_DATE", "Net Final Price: ₹" + commaFormat.format(finalNetPrice.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etOriginalPrice.setText("5,000")
        etDiscountRate.setText("20")
        etFlatDiscount.setText("0")
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
