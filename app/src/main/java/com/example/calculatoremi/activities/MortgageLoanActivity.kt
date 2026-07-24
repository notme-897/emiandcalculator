package com.example.calculatoremi.activities

import android.app.DatePickerDialog
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class MortgageLoanActivity : BaseInputActivity() {

    private lateinit var btnModeLap: MaterialButton
    private lateinit var btnModePiti: MaterialButton

    private lateinit var etPropertyValue: EditText
    private lateinit var chipProp25L: MaterialButton
    private lateinit var chipProp50L: MaterialButton
    private lateinit var chipProp1Cr: MaterialButton
    private lateinit var chipProp2_5Cr: MaterialButton
    private lateinit var chipProp5Cr: MaterialButton

    private lateinit var etAmount: EditText
    private lateinit var chipLoan15L: MaterialButton
    private lateinit var chipLoan30L: MaterialButton
    private lateinit var chipLoan60L: MaterialButton
    private lateinit var chipLoan1_5Cr: MaterialButton
    private lateinit var chipLoan3Cr: MaterialButton

    private lateinit var txtLtvRatioDisplay: TextView
    private lateinit var txtLtvStatusNotice: TextView

    private lateinit var containerPitiInputs: LinearLayout
    private lateinit var etAnnualTax: EditText
    private lateinit var etAnnualInsurance: EditText

    private lateinit var etRate: EditText
    private lateinit var chipRate8_5: MaterialButton
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate10_5: MaterialButton
    private lateinit var chipRate12_0: MaterialButton

    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipMortTerm5Y: MaterialButton
    private lateinit var chipMortTerm10Y: MaterialButton
    private lateinit var chipMortTerm15Y: MaterialButton
    private lateinit var chipMortTerm20Y: MaterialButton
    private lateinit var chipMortTerm25Y: MaterialButton
    private lateinit var chipMortTerm30Y: MaterialButton

    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    private var isPitiMode = false

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_mortgage_loan

    override fun getActivityTitle(): String = "Mortgage & LAP Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        btnModeLap = findViewById(R.id.btnModeLap)
        btnModePiti = findViewById(R.id.btnModePiti)

        etPropertyValue = findViewById(R.id.etPropertyValue)
        chipProp25L = findViewById(R.id.chipProp25L)
        chipProp50L = findViewById(R.id.chipProp50L)
        chipProp1Cr = findViewById(R.id.chipProp1Cr)
        chipProp2_5Cr = findViewById(R.id.chipProp2_5Cr)
        chipProp5Cr = findViewById(R.id.chipProp5Cr)

        etAmount = findViewById(R.id.editTextNumber)
        chipLoan15L = findViewById(R.id.chipLoan15L)
        chipLoan30L = findViewById(R.id.chipLoan30L)
        chipLoan60L = findViewById(R.id.chipLoan60L)
        chipLoan1_5Cr = findViewById(R.id.chipLoan1_5Cr)
        chipLoan3Cr = findViewById(R.id.chipLoan3Cr)

        txtLtvRatioDisplay = findViewById(R.id.txtLtvRatioDisplay)
        txtLtvStatusNotice = findViewById(R.id.txtLtvStatusNotice)

        containerPitiInputs = findViewById(R.id.containerPitiInputs)
        etAnnualTax = findViewById(R.id.etAnnualTax)
        etAnnualInsurance = findViewById(R.id.etAnnualInsurance)

        etRate = findViewById(R.id.editTextNumber2)
        chipRate8_5 = findViewById(R.id.chipRate8_5)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate10_5 = findViewById(R.id.chipRate10_5)
        chipRate12_0 = findViewById(R.id.chipRate12_0)

        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipMortTerm5Y = findViewById(R.id.chipMortTerm5Y)
        chipMortTerm10Y = findViewById(R.id.chipMortTerm10Y)
        chipMortTerm15Y = findViewById(R.id.chipMortTerm15Y)
        chipMortTerm20Y = findViewById(R.id.chipMortTerm20Y)
        chipMortTerm25Y = findViewById(R.id.chipMortTerm25Y)
        chipMortTerm30Y = findViewById(R.id.chipMortTerm30Y)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Setup touch animations
        val allChips = listOf(
            btnModeLap, btnModePiti,
            chipProp25L, chipProp50L, chipProp1Cr, chipProp2_5Cr, chipProp5Cr,
            chipLoan15L, chipLoan30L, chipLoan60L, chipLoan1_5Cr, chipLoan3Cr,
            chipRate8_5, chipRate9_5, chipRate10_5, chipRate12_0,
            chipMortTerm5Y, chipMortTerm10Y, chipMortTerm15Y, chipMortTerm20Y, chipMortTerm25Y, chipMortTerm30Y
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etPropertyValue) { updateLtvAnalysis() }
        setupCommaFormatting(etAmount) { updateLtvAnalysis() }
        setupCommaFormatting(etAnnualTax)
        setupCommaFormatting(etAnnualInsurance)

        // Mode Toggles
        btnModeLap.setOnClickListener { setMortgageMode(false) }
        btnModePiti.setOnClickListener { setMortgageMode(true) }

        // Property Value Chips
        chipProp25L.setOnClickListener { setQuickPropValue(2500000.0); highlightPropChip(chipProp25L) }
        chipProp50L.setOnClickListener { setQuickPropValue(5000000.0); highlightPropChip(chipProp50L) }
        chipProp1Cr.setOnClickListener { setQuickPropValue(10000000.0); highlightPropChip(chipProp1Cr) }
        chipProp2_5Cr.setOnClickListener { setQuickPropValue(25000000.0); highlightPropChip(chipProp2_5Cr) }
        chipProp5Cr.setOnClickListener { setQuickPropValue(50000000.0); highlightPropChip(chipProp5Cr) }

        // Loan Amount Chips
        chipLoan15L.setOnClickListener { setQuickLoanAmount(1500000.0); highlightLoanChip(chipLoan15L) }
        chipLoan30L.setOnClickListener { setQuickLoanAmount(3000000.0); highlightLoanChip(chipLoan30L) }
        chipLoan60L.setOnClickListener { setQuickLoanAmount(6000000.0); highlightLoanChip(chipLoan60L) }
        chipLoan1_5Cr.setOnClickListener { setQuickLoanAmount(15000000.0); highlightLoanChip(chipLoan1_5Cr) }
        chipLoan3Cr.setOnClickListener { setQuickLoanAmount(30000000.0); highlightLoanChip(chipLoan3Cr) }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
            }
        })

        // Rate Chips
        chipRate8_5.setOnClickListener { etRate.setText("8.5"); highlightRateChip(chipRate8_5) }
        chipRate9_5.setOnClickListener { etRate.setText("9.5"); highlightRateChip(chipRate9_5) }
        chipRate10_5.setOnClickListener { etRate.setText("10.5"); highlightRateChip(chipRate10_5) }
        chipRate12_0.setOnClickListener { etRate.setText("12.0"); highlightRateChip(chipRate12_0) }

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // SeekBar (60 to 360 months / 5 to 30 Years)
        seekBarTerm.max = 360
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 60) 60 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnAdd.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (seekBarTerm.progress < seekBarTerm.max) {
                seekBarTerm.progress += 12
                clearTermChipSelection()
            }
        }
        btnMinus.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (seekBarTerm.progress > 60) {
                seekBarTerm.progress -= 12
                clearTermChipSelection()
            }
        }

        // Tenure Chips
        chipMortTerm5Y.setOnClickListener { seekBarTerm.progress = 60; highlightTermChip(chipMortTerm5Y) }
        chipMortTerm10Y.setOnClickListener { seekBarTerm.progress = 120; highlightTermChip(chipMortTerm10Y) }
        chipMortTerm15Y.setOnClickListener { seekBarTerm.progress = 180; highlightTermChip(chipMortTerm15Y) }
        chipMortTerm20Y.setOnClickListener { seekBarTerm.progress = 240; highlightTermChip(chipMortTerm20Y) }
        chipMortTerm25Y.setOnClickListener { seekBarTerm.progress = 300; highlightTermChip(chipMortTerm25Y) }
        chipMortTerm30Y.setOnClickListener { seekBarTerm.progress = 360; highlightTermChip(chipMortTerm30Y) }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etPropertyValue.setText("1,00,00,000")
        etAmount.setText("60,00,000")
        etAnnualTax.setText("24,000")
        etAnnualInsurance.setText("12,000")
        etRate.setText("9.5")
        seekBarTerm.progress = 180
        updateTermDisplay(180)

        highlightPropChip(chipProp1Cr)
        highlightLoanChip(chipLoan60L)
        highlightRateChip(chipRate9_5)
        highlightTermChip(chipMortTerm15Y)
        setMortgageMode(false)
        updateLtvAnalysis()
    }

    private fun setupCommaFormatting(editText: EditText, onFormatted: (() -> Unit)? = null) {
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
                onFormatted?.invoke()
                updateLtvAnalysis()
            }
        })
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun setMortgageMode(isPiti: Boolean) {
        isPitiMode = isPiti
        val selectedBlue = ContextCompat.getColor(this, R.color.custom_blue)
        val selectedText = ContextCompat.getColor(this, android.R.color.white)
        val unselectedBg = Color.parseColor("#F8FAFC")
        val unselectedText = Color.parseColor("#1E293B")
        val strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        val strokeWidth = (1 * resources.displayMetrics.density).toInt()

        if (isPiti) {
            btnModePiti.setBackgroundColor(selectedBlue)
            btnModePiti.setTextColor(selectedText)
            btnModePiti.strokeWidth = 0

            btnModeLap.setBackgroundColor(unselectedBg)
            btnModeLap.setTextColor(unselectedText)
            btnModeLap.strokeColor = strokeColor
            btnModeLap.strokeWidth = strokeWidth

            containerPitiInputs.visibility = View.VISIBLE
        } else {
            btnModeLap.setBackgroundColor(selectedBlue)
            btnModeLap.setTextColor(selectedText)
            btnModeLap.strokeWidth = 0

            btnModePiti.setBackgroundColor(unselectedBg)
            btnModePiti.setTextColor(unselectedText)
            btnModePiti.strokeColor = strokeColor
            btnModePiti.strokeWidth = strokeWidth

            containerPitiInputs.visibility = View.GONE
        }
    }

    private fun setQuickPropValue(valAmount: Double) {
        etPropertyValue.setText(commaFormat.format(valAmount))
        val loanVal = valAmount * 0.60
        etAmount.setText(commaFormat.format(loanVal))
        updateLtvAnalysis()
    }

    private fun setQuickLoanAmount(amount: Double) {
        etAmount.setText(commaFormat.format(amount))
        updateLtvAnalysis()
    }

    private fun updateLtvAnalysis() {
        val propValue = getRawValue(etPropertyValue)
        val loanAmount = getRawValue(etAmount)

        if (propValue > 0 && loanAmount > 0) {
            val ltv = (loanAmount / propValue) * 100.0
            txtLtvRatioDisplay.text = String.format(Locale.US, "%.1f%%", ltv)

            if (ltv <= 65.0) {
                txtLtvStatusNotice.text = "🟢 Safe LTV Range (Within standard 65% bank limit for LAP)"
                txtLtvStatusNotice.setTextColor(Color.parseColor("#00C853"))
                txtLtvRatioDisplay.setTextColor(Color.parseColor("#00C853"))
            } else {
                txtLtvStatusNotice.text = "🔴 High LTV Warning (Lenders cap LAP at 50% - 65% of market value)"
                txtLtvStatusNotice.setTextColor(Color.parseColor("#E53935"))
                txtLtvRatioDisplay.setTextColor(Color.parseColor("#E53935"))
            }
        } else {
            txtLtvRatioDisplay.text = "0.0%"
            txtLtvStatusNotice.text = "Enter Property Value & Loan Amount"
            txtLtvStatusNotice.setTextColor(Color.parseColor("#64748B"))
        }
    }

    private fun setupChipTouchAnimation(button: MaterialButton) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(60).start()
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

    private fun highlightPropChip(selectedChip: MaterialButton) {
        val chips = listOf(chipProp25L, chipProp50L, chipProp1Cr, chipProp2_5Cr, chipProp5Cr)
        chips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun highlightLoanChip(selectedChip: MaterialButton) {
        val chips = listOf(chipLoan15L, chipLoan30L, chipLoan60L, chipLoan1_5Cr, chipLoan3Cr)
        chips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun highlightRateChip(selectedChip: MaterialButton?) {
        val rateChips = listOf(chipRate8_5, chipRate9_5, chipRate10_5, chipRate12_0)
        rateChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun updateRateChipHighlights(rate: Double?) {
        when (rate) {
            8.5 -> highlightRateChip(chipRate8_5)
            9.5 -> highlightRateChip(chipRate9_5)
            10.5 -> highlightRateChip(chipRate10_5)
            12.0 -> highlightRateChip(chipRate12_0)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipMortTerm5Y, chipMortTerm10Y, chipMortTerm15Y, chipMortTerm20Y, chipMortTerm25Y, chipMortTerm30Y)
        termChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun clearTermChipSelection() {
        val termChips = listOf(chipMortTerm5Y, chipMortTerm10Y, chipMortTerm15Y, chipMortTerm20Y, chipMortTerm25Y, chipMortTerm30Y)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(months: Int) {
        val years = months / 12
        txtTermValueCombined.text = "$years yrs ($months mos)"
        txtInstallments.text = "$months EMIs"
    }

    private fun setupButtonAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(150).start()
                }
            }
            false
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                txtSelectedDate.text = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun calculateAndNavigate() {
        val loanAmount = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = if (seekBarTerm.progress < 60) 60 else seekBarTerm.progress

        if (loanAmount <= 0) {
            etAmount.error = "Please enter valid mortgage loan amount"
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter valid interest rate"
            return
        }

        val monthlyRate = rate / (12 * 100)
        val baseEmi = (loanAmount * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) /
                ((1 + monthlyRate).pow(months.toDouble()) - 1)

        val annualTax = if (isPitiMode) getRawValue(etAnnualTax) else 0.0
        val annualInsurance = if (isPitiMode) getRawValue(etAnnualInsurance) else 0.0

        val monthlyTax = annualTax / 12.0
        val monthlyInsurance = annualInsurance / 12.0
        val totalMonthlyPiti = baseEmi + monthlyTax + monthlyInsurance

        val baseTotalRepayment = baseEmi * months
        val totalInterest = baseTotalRepayment - loanAmount
        val years = months / 12.0
        val totalCost = baseTotalRepayment + (annualTax * years) + (annualInsurance * years)

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, months)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = loanAmount
        val scheduleCalendar = calendar.clone() as Calendar

        for (i in 1..months) {
            val interestPayment = balance * monthlyRate
            val principalPayment = baseEmi - interestPayment
            balance -= principalPayment

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = i,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = totalMonthlyPiti,
                    principal = principalPayment,
                    interest = interestPayment + monthlyTax + monthlyInsurance,
                    balance = if (balance < 0) 0.0 else balance
                )
            )
        }

        val yearsInt = months / 12
        val monthsInt = months % 12
        val modeLabel = if (isPitiMode) "PITI Mode" else "LAP Mode"
        val titleText = "Mortgage Loan ($modeLabel)"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", loanAmount)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", txtSelectedDate.text.toString())
            putExtra("EMI", totalMonthlyPiti)
            putExtra("TOTAL_INTEREST", totalInterest)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", payoffDate)
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etPropertyValue.setText("1,00,00,000")
        etAmount.setText("60,00,000")
        etAnnualTax.setText("24,000")
        etAnnualInsurance.setText("12,000")
        etRate.setText("9.5")
        seekBarTerm.progress = 180
        updateTermDisplay(180)

        highlightPropChip(chipProp1Cr)
        highlightLoanChip(chipLoan60L)
        highlightRateChip(chipRate9_5)
        highlightTermChip(chipMortTerm15Y)
        setMortgageMode(false)
        updateLtvAnalysis()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewMortgageLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}