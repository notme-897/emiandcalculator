package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class BillSplitterActivity : BaseInputActivity() {

    private lateinit var etTotalBill: EditText
    private lateinit var chipTip0: MaterialButton
    private lateinit var chipTip5: MaterialButton
    private lateinit var chipTip10: MaterialButton
    private lateinit var chipTip15: MaterialButton
    private lateinit var etCustomTip: EditText
    private lateinit var etNumPeople: EditText
    private lateinit var btnMinusPeople: MaterialCardView
    private lateinit var btnAddPeople: MaterialCardView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var selectedTipPercent: Double = 5.0
    private val commaFormat = DecimalFormat("#,##,###.##")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_bill_splitter

    override fun getActivityTitle(): String = "Bill Splitter & Tip Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etTotalBill = findViewById(R.id.etTotalBill)
        chipTip0 = findViewById(R.id.chipTip0)
        chipTip5 = findViewById(R.id.chipTip5)
        chipTip10 = findViewById(R.id.chipTip10)
        chipTip15 = findViewById(R.id.chipTip15)
        etCustomTip = findViewById(R.id.etCustomTip)
        etNumPeople = findViewById(R.id.etNumPeople)
        btnMinusPeople = findViewById(R.id.btnMinusPeople)
        btnAddPeople = findViewById(R.id.btnAddPeople)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)
        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        chipTip0.setOnClickListener { selectedTipPercent = 0.0; etCustomTip.setText(""); highlightTipChips() }
        chipTip5.setOnClickListener { selectedTipPercent = 5.0; etCustomTip.setText(""); highlightTipChips() }
        chipTip10.setOnClickListener { selectedTipPercent = 10.0; etCustomTip.setText(""); highlightTipChips() }
        chipTip15.setOnClickListener { selectedTipPercent = 15.0; etCustomTip.setText(""); highlightTipChips() }

        etCustomTip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val custom = s.toString().toDoubleOrNull()
                if (custom != null && custom >= 0) {
                    selectedTipPercent = custom
                    highlightTipChips(clearAll = true)
                }
            }
        })

        btnMinusPeople.setOnClickListener {
            val people = (etNumPeople.text.toString().toIntOrNull() ?: 2) - 1
            if (people >= 1) etNumPeople.setText(people.toString())
        }

        btnAddPeople.setOnClickListener {
            val people = (etNumPeople.text.toString().toIntOrNull() ?: 2) + 1
            etNumPeople.setText(people.toString())
        }

        setupCommaFormatting(etTotalBill)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        etTotalBill.setText("2,400")
        highlightTipChips()
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
                            val formatted = DecimalFormat("#,##,###").format(doubleVal)
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        }
                    }
                } catch (e: Exception) {}
                isFormatting = false
            }
        })
    }

    private fun highlightTipChips(clearAll: Boolean = false) {
        val chips = listOf(0.0 to chipTip0, 5.0 to chipTip5, 10.0 to chipTip10, 15.0 to chipTip15)
        chips.forEach { (pct, chip) ->
            if (!clearAll && pct == selectedTipPercent) {
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

    private fun calculateAndNavigate() {
        val bill = etTotalBill.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
        val people = (etNumPeople.text.toString().toIntOrNull() ?: 1).coerceAtLeast(1)

        if (bill <= 0) {
            etTotalBill.error = "Please enter valid total bill amount"
            return
        }

        val tipAmount = bill * (selectedTipPercent / 100.0)
        val totalWithTip = bill + tipAmount
        val perPerson = totalWithTip / people

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Bill Splitter & Tip Result")
            putExtra("LOAN_AMOUNT", bill)
            putExtra("INTEREST_RATE", selectedTipPercent.toFloat())
            putExtra("LOAN_TERM_YEARS", 0)
            putExtra("LOAN_TERM_MONTHS", people)
            putExtra("START_DATE", "Split Among $people People")
            putExtra("EMI", perPerson)
            putExtra("TOTAL_INTEREST", tipAmount)
            putExtra("TOTAL_COST", totalWithTip)
            putExtra("PAYOFF_DATE", "Per Person: ₹" + commaFormat.format(perPerson))
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etTotalBill.setText("2,400")
        selectedTipPercent = 5.0
        etCustomTip.setText("")
        etNumPeople.setText("2")
        highlightTipChips()
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show()
    }
}
