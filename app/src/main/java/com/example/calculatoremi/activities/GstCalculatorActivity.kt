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

class GstCalculatorActivity : BaseInputActivity() {

    private lateinit var etGstAmount: EditText

    private lateinit var chipGst5: MaterialButton
    private lateinit var chipGst12: MaterialButton
    private lateinit var chipGst18: MaterialButton
    private lateinit var chipGst28: MaterialButton

    private lateinit var btnGstTypeToggle: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var selectedGstRate = 18.0
    private var isExclusiveGst = true // true: Add GST, false: Remove/Inclusive GST

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_gst_calculator

    override fun getActivityTitle(): String = "GST Tax Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etGstAmount = findViewById(R.id.etGstAmount)

        chipGst5 = findViewById(R.id.chipGst5)
        chipGst12 = findViewById(R.id.chipGst12)
        chipGst18 = findViewById(R.id.chipGst18)
        chipGst28 = findViewById(R.id.chipGst28)

        btnGstTypeToggle = findViewById(R.id.btnGstTypeToggle)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etGstAmount)

        chipGst5.setOnClickListener { selectedGstRate = 5.0; highlightRateChips() }
        chipGst12.setOnClickListener { selectedGstRate = 12.0; highlightRateChips() }
        chipGst18.setOnClickListener { selectedGstRate = 18.0; highlightRateChips() }
        chipGst28.setOnClickListener { selectedGstRate = 28.0; highlightRateChips() }

        btnGstTypeToggle.setOnClickListener {
            isExclusiveGst = !isExclusiveGst
            updateTypeButton()
        }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateGstAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etGstAmount.setText("10,000")
        highlightRateChips()
        updateTypeButton()
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

    private fun highlightRateChips() {
        val chips = listOf(5.0 to chipGst5, 12.0 to chipGst12, 18.0 to chipGst18, 28.0 to chipGst28)
        chips.forEach { (rate, chip) ->
            if (rate == selectedGstRate) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun updateTypeButton() {
        if (isExclusiveGst) {
            btnGstTypeToggle.text = "Add GST (Exclusive)"
            btnGstTypeToggle.setBackgroundColor(Color.parseColor("#EFF6FF"))
            btnGstTypeToggle.setTextColor(ContextCompat.getColor(this, R.color.primary))
            btnGstTypeToggle.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary))
        } else {
            btnGstTypeToggle.text = "Remove GST (Inclusive)"
            btnGstTypeToggle.setBackgroundColor(Color.parseColor("#F0FDF4"))
            btnGstTypeToggle.setTextColor(Color.parseColor("#16A34A"))
            btnGstTypeToggle.strokeColor = ColorStateList.valueOf(Color.parseColor("#16A34A"))
        }
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

    private fun calculateGstAndNavigate() {
        val amount = etGstAmount.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0

        if (amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val baseAmount: Double
        val gstTax: Double
        val totalAmount: Double

        if (isExclusiveGst) {
            baseAmount = amount
            gstTax = baseAmount * (selectedGstRate / 100.0)
            totalAmount = baseAmount + gstTax
        } else {
            totalAmount = amount
            baseAmount = totalAmount / (1.0 + (selectedGstRate / 100.0))
            gstTax = totalAmount - baseAmount
        }

        val cgst = gstTax / 2.0
        val sgst = gstTax / 2.0

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Net Base Amount", baseAmount, baseAmount / 12.0, 0.0, baseAmount))
        scheduleList.add(PaymentScheduleItem(2, "CGST (${selectedGstRate / 2}%)", cgst, cgst / 12.0, 0.0, cgst))
        scheduleList.add(PaymentScheduleItem(3, "SGST (${selectedGstRate / 2}%)", sgst, sgst / 12.0, 0.0, sgst))
        scheduleList.add(PaymentScheduleItem(4, "Total GST Tax", gstTax, gstTax / 12.0, 0.0, gstTax))
        scheduleList.add(PaymentScheduleItem(5, "Total Gross Amount", totalAmount, totalAmount / 12.0, 0.0, totalAmount))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "GST Calculation Result")
            putExtra("LOAN_AMOUNT", totalAmount)
            putExtra("INTEREST_RATE", selectedGstRate.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 1)
            putExtra("START_DATE", if (isExclusiveGst) "Add GST Mode" else "Remove GST Mode")
            putExtra("EMI", totalAmount)
            putExtra("TOTAL_INTEREST", gstTax)
            putExtra("TOTAL_COST", totalAmount)
            putExtra("PAYOFF_DATE", "Tax: ₹" + commaFormat.format(gstTax.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etGstAmount.setText("10,000")
        selectedGstRate = 18.0
        isExclusiveGst = true
        highlightRateChips()
        updateTypeButton()
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
