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

class HomeReinstatementActivity : BaseInputActivity() {

    private lateinit var etBuiltUpArea: EditText
    private lateinit var etConstructionRate: EditText
    private lateinit var etContentsValue: EditText

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_home_reinstatement

    override fun getActivityTitle(): String = "Home Reinstatement Cost"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etBuiltUpArea = findViewById(R.id.etBuiltUpArea)
        etConstructionRate = findViewById(R.id.etConstructionRate)
        etContentsValue = findViewById(R.id.etContentsValue)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        setupCommaFormatting(etBuiltUpArea)
        setupCommaFormatting(etConstructionRate)
        setupCommaFormatting(etContentsValue)

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateReinstatementAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etBuiltUpArea.setText("1,500")
        etConstructionRate.setText("2,500")
        etContentsValue.setText("5,00,000")
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

    private fun calculateReinstatementAndNavigate() {
        val areaStr = etBuiltUpArea.text.toString().replace(",", "")
        val rateStr = etConstructionRate.text.toString().replace(",", "")
        val contentsStr = etContentsValue.text.toString().replace(",", "")

        val builtUpArea = areaStr.toDoubleOrNull() ?: 0.0
        val constructionRate = rateStr.toDoubleOrNull() ?: 2500.0
        val contentsValue = contentsStr.toDoubleOrNull() ?: 0.0

        if (builtUpArea <= 0) {
            Toast.makeText(this, "Please enter a valid built-up area", Toast.LENGTH_SHORT).show()
            return
        }

        val structureRebuildingCost = builtUpArea * constructionRate
        val totalPropertySumInsured = structureRebuildingCost + contentsValue

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Structure Rebuilding Cost", structureRebuildingCost, structureRebuildingCost / 12.0, 0.0, structureRebuildingCost))
        scheduleList.add(PaymentScheduleItem(2, "Home Contents Sum Insured", contentsValue, contentsValue / 12.0, 0.0, contentsValue))
        scheduleList.add(PaymentScheduleItem(3, "Total Property Reinstatement Cover", totalPropertySumInsured, totalPropertySumInsured / 12.0, 0.0, totalPropertySumInsured))

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Home Reinstatement Result")
            putExtra("LOAN_AMOUNT", totalPropertySumInsured) // Total Insured Sum
            putExtra("INTEREST_RATE", constructionRate.toFloat()) // Rate per Sq. Ft.
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", "Area: ${builtUpArea.toInt()} Sq. Ft. @ ₹${constructionRate.toInt()}/sq.ft")
            putExtra("EMI", totalPropertySumInsured / 12.0)
            putExtra("TOTAL_INTEREST", contentsValue)
            putExtra("TOTAL_COST", totalPropertySumInsured)
            putExtra("PAYOFF_DATE", "Total Reinstatement Cover: ₹" + commaFormat.format(totalPropertySumInsured.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etBuiltUpArea.setText("1,500")
        etConstructionRate.setText("2,500")
        etContentsValue.setText("5,00,000")
    }
}
