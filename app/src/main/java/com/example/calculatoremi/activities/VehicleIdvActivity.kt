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

class VehicleIdvActivity : BaseInputActivity() {

    private lateinit var etExShowroomPrice: EditText

    private lateinit var chipAge6m: MaterialButton
    private lateinit var chipAge1y: MaterialButton
    private lateinit var chipAge2y: MaterialButton
    private lateinit var chipAge3y: MaterialButton
    private lateinit var chipAge4y: MaterialButton
    private lateinit var chipAge5y: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var selectedSlab = 2 // Default: 1 - 2 Yrs (20%)

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_vehicle_idv

    override fun getActivityTitle(): String = "Vehicle IDV Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etExShowroomPrice = findViewById(R.id.etExShowroomPrice)

        chipAge6m = findViewById(R.id.chipAge6m)
        chipAge1y = findViewById(R.id.chipAge1y)
        chipAge2y = findViewById(R.id.chipAge2y)
        chipAge3y = findViewById(R.id.chipAge3y)
        chipAge4y = findViewById(R.id.chipAge4y)
        chipAge5y = findViewById(R.id.chipAge5y)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etExShowroomPrice)

        chipAge6m.setOnClickListener { selectedSlab = 0; highlightSlabChips() }
        chipAge1y.setOnClickListener { selectedSlab = 1; highlightSlabChips() }
        chipAge2y.setOnClickListener { selectedSlab = 2; highlightSlabChips() }
        chipAge3y.setOnClickListener { selectedSlab = 3; highlightSlabChips() }
        chipAge4y.setOnClickListener { selectedSlab = 4; highlightSlabChips() }
        chipAge5y.setOnClickListener { selectedSlab = 5; highlightSlabChips() }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateIdvAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etExShowroomPrice.setText("10,00,000")
        highlightSlabChips()
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

    private fun highlightSlabChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipAge6m, chipAge1y, chipAge2y, chipAge3y, chipAge4y, chipAge5y)

        chips.forEachIndexed { index, chip ->
            if (index == selectedSlab) {
                chip.backgroundTintList = ColorStateList.valueOf(primaryColor)
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
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

    private fun calculateIdvAndNavigate() {
        val exShowroomStr = etExShowroomPrice.text.toString().replace(",", "")
        val exShowroomPrice = exShowroomStr.toDoubleOrNull() ?: 0.0

        if (exShowroomPrice <= 0) {
            Toast.makeText(this, "Please enter a valid ex-showroom price", Toast.LENGTH_SHORT).show()
            return
        }

        val depreciationPct = when (selectedSlab) {
            0 -> 5.0   // < 6 months
            1 -> 15.0  // 6m - 1 yr
            2 -> 20.0  // 1 - 2 yrs
            3 -> 30.0  // 2 - 3 yrs
            4 -> 40.0  // 3 - 4 yrs
            else -> 50.0 // 4 - 5 yrs
        }

        val depreciationAmount = exShowroomPrice * (depreciationPct / 100.0)
        val idvAmount = exShowroomPrice - depreciationAmount

        val slabNames = listOf("< 6 Months", "6m - 1 Year", "1 - 2 Years", "2 - 3 Years", "3 - 4 Years", "4 - 5 Years")
        val statusText = "Age: ${slabNames[selectedSlab]} (${depreciationPct.toInt()}% Depreciation)"

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Ex-Showroom Original Price", exShowroomPrice, exShowroomPrice / 12.0, 0.0, exShowroomPrice))
        scheduleList.add(PaymentScheduleItem(2, "Depreciation Deduction (${depreciationPct.toInt()}%)", depreciationAmount, depreciationAmount / 12.0, 0.0, depreciationAmount))
        scheduleList.add(PaymentScheduleItem(3, "Net Insured Declared Value (IDV)", idvAmount, idvAmount / 12.0, 0.0, idvAmount))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Vehicle IDV Result")
            putExtra("LOAN_AMOUNT", idvAmount) // IDV Value
            putExtra("INTEREST_RATE", depreciationPct.toFloat())
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", statusText)
            putExtra("EMI", idvAmount / 12.0)
            putExtra("TOTAL_INTEREST", depreciationAmount)
            putExtra("TOTAL_COST", exShowroomPrice)
            putExtra("PAYOFF_DATE", "Maximum Claim Cover: ₹" + commaFormat.format(idvAmount.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etExShowroomPrice.setText("10,00,000")
        selectedSlab = 2
        highlightSlabChips()
    }
}
