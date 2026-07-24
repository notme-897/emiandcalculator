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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

class HomeLoanActivity : BaseInputActivity() {

    // Sub-Calculator Navigation Tabs
    private lateinit var tabEmiCalc: MaterialButton
    private lateinit var tabEligibility: MaterialButton
    private lateinit var tabPrepayment: MaterialButton

    private lateinit var containerEmiCalc: LinearLayout
    private lateinit var containerEligibility: LinearLayout
    private lateinit var containerPrepayment: LinearLayout
    private lateinit var scrollViewHomeLoan: NestedScrollView

    // ================= EMI CALCULATOR VIEWS =================
    private lateinit var switchPropertyCalc: SwitchMaterial
    private lateinit var layoutPropertyInputs: LinearLayout
    private lateinit var etPropertyValue: EditText
    private lateinit var etDownPayment: EditText
    private lateinit var txtDownPaymentPercentDisplay: TextView
    private lateinit var txtCalculatedNetLoan: TextView

    private lateinit var chipDp10: MaterialButton
    private lateinit var chipDp15: MaterialButton
    private lateinit var chipDp20: MaterialButton
    private lateinit var chipDp25: MaterialButton

    private lateinit var etAmount: EditText
    private lateinit var etRate: EditText
    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView
    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    private lateinit var chipAmount20L: MaterialButton
    private lateinit var chipAmount50L: MaterialButton
    private lateinit var chipAmount75L: MaterialButton
    private lateinit var chipAmount1Cr: MaterialButton

    private lateinit var chipRate7_5: MaterialButton
    private lateinit var chipRate8_5: MaterialButton
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate10_5: MaterialButton

    private lateinit var chipTerm5Y: MaterialButton
    private lateinit var chipTerm10Y: MaterialButton
    private lateinit var chipTerm15Y: MaterialButton
    private lateinit var chipTerm20Y: MaterialButton
    private lateinit var chipTerm25Y: MaterialButton
    private lateinit var chipTerm30Y: MaterialButton

    // ================= ELIGIBILITY VIEWS =================
    private lateinit var etMonthlyIncome: EditText
    private lateinit var etExistingEmi: EditText
    private lateinit var etEligRate: EditText
    private lateinit var etEligTenureYears: EditText
    private lateinit var btnCalculateEligibility: MaterialButton
    private lateinit var cardEligibilityResult: MaterialCardView
    private lateinit var txtEligMaxLoan: TextView
    private lateinit var txtEligMaxEmi: TextView
    private lateinit var txtEligTotalCost: TextView

    // ================= PREPAYMENT VIEWS =================
    private lateinit var etPreOutstanding: EditText
    private lateinit var etPreRate: EditText
    private lateinit var etPreRemainingYears: EditText
    private lateinit var etPreLumpSum: EditText
    private lateinit var btnCalculatePrepayment: MaterialButton
    private lateinit var cardPrepaymentResult: MaterialCardView
    private lateinit var txtPreInterestSaved: TextView
    private lateinit var txtPreTenureSaved: TextView
    private lateinit var txtPreNewTenure: TextView

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_home_loan

    override fun getActivityTitle(): String = "Home Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Navigation Tabs & Containers
        tabEmiCalc = findViewById(R.id.tabEmiCalc)
        tabEligibility = findViewById(R.id.tabEligibility)
        tabPrepayment = findViewById(R.id.tabPrepayment)

        containerEmiCalc = findViewById(R.id.containerEmiCalc)
        containerEligibility = findViewById(R.id.containerEligibility)
        containerPrepayment = findViewById(R.id.containerPrepayment)
        scrollViewHomeLoan = findViewById(R.id.scrollViewHomeLoan)

        setupSubCalculatorTabs()

        // Initialize EMI Calculator Views
        initEmiCalculatorViews()

        // Initialize Eligibility Calculator Views
        initEligibilityCalculatorViews()

        // Initialize Prepayment Calculator Views
        initPrepaymentCalculatorViews()
    }

    private fun setupSubCalculatorTabs() {
        val tabs = listOf(tabEmiCalc, tabEligibility, tabPrepayment)
        tabs.forEach { setupChipTouchAnimation(it) }

        tabEmiCalc.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            switchSubCalculatorTab(0)
        }
        tabEligibility.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            switchSubCalculatorTab(1)
        }
        tabPrepayment.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            switchSubCalculatorTab(2)
        }
    }

    private fun switchSubCalculatorTab(tabIndex: Int) {
        val selectedBlue = ContextCompat.getColor(this, R.color.custom_blue)
        val selectedText = ContextCompat.getColor(this, android.R.color.white)
        val unselectedBg = Color.parseColor("#F8FAFC")
        val unselectedText = Color.parseColor("#1E293B")
        val strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        val strokeWidth = (1 * resources.displayMetrics.density).toInt()

        fun applySelectedStyle(btn: MaterialButton) {
            btn.setBackgroundColor(selectedBlue)
            btn.setTextColor(selectedText)
            btn.strokeWidth = 0
        }

        fun applyUnselectedStyle(btn: MaterialButton) {
            btn.setBackgroundColor(unselectedBg)
            btn.setTextColor(unselectedText)
            btn.strokeColor = strokeColor
            btn.strokeWidth = strokeWidth
        }

        when (tabIndex) {
            0 -> {
                applySelectedStyle(tabEmiCalc)
                applyUnselectedStyle(tabEligibility)
                applyUnselectedStyle(tabPrepayment)
                containerEmiCalc.visibility = View.VISIBLE
                containerEligibility.visibility = View.GONE
                containerPrepayment.visibility = View.GONE
            }
            1 -> {
                applyUnselectedStyle(tabEmiCalc)
                applySelectedStyle(tabEligibility)
                applyUnselectedStyle(tabPrepayment)
                containerEmiCalc.visibility = View.GONE
                containerEligibility.visibility = View.VISIBLE
                containerPrepayment.visibility = View.GONE
            }
            2 -> {
                applyUnselectedStyle(tabEmiCalc)
                applyUnselectedStyle(tabEligibility)
                applySelectedStyle(tabPrepayment)
                containerEmiCalc.visibility = View.GONE
                containerEligibility.visibility = View.GONE
                containerPrepayment.visibility = View.VISIBLE
            }
        }
        scrollViewHomeLoan.smoothScrollTo(0, 0)
    }

    private fun initEmiCalculatorViews() {
        switchPropertyCalc = findViewById(R.id.switchPropertyCalc)
        layoutPropertyInputs = findViewById(R.id.layoutPropertyInputs)
        etPropertyValue = findViewById(R.id.etPropertyValue)
        etDownPayment = findViewById(R.id.etDownPayment)
        txtDownPaymentPercentDisplay = findViewById(R.id.txtDownPaymentPercentDisplay)
        txtCalculatedNetLoan = findViewById(R.id.txtCalculatedNetLoan)

        chipDp10 = findViewById(R.id.chipDp10)
        chipDp15 = findViewById(R.id.chipDp15)
        chipDp20 = findViewById(R.id.chipDp20)
        chipDp25 = findViewById(R.id.chipDp25)

        etAmount = findViewById(R.id.editTextNumber)
        etRate = findViewById(R.id.editTextNumber2)
        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        chipAmount20L = findViewById(R.id.chipAmount20L)
        chipAmount50L = findViewById(R.id.chipAmount50L)
        chipAmount75L = findViewById(R.id.chipAmount75L)
        chipAmount1Cr = findViewById(R.id.chipAmount1Cr)

        chipRate7_5 = findViewById(R.id.chipRate7_5)
        chipRate8_5 = findViewById(R.id.chipRate8_5)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate10_5 = findViewById(R.id.chipRate10_5)

        chipTerm5Y = findViewById(R.id.chipTerm5Y)
        chipTerm10Y = findViewById(R.id.chipTerm10Y)
        chipTerm15Y = findViewById(R.id.chipTerm15Y)
        chipTerm20Y = findViewById(R.id.chipTerm20Y)
        chipTerm25Y = findViewById(R.id.chipTerm25Y)
        chipTerm30Y = findViewById(R.id.chipTerm30Y)

        val emiChips = listOf(
            chipDp10, chipDp15, chipDp20, chipDp25,
            chipAmount20L, chipAmount50L, chipAmount75L, chipAmount1Cr,
            chipRate7_5, chipRate8_5, chipRate9_5, chipRate10_5,
            chipTerm5Y, chipTerm10Y, chipTerm15Y, chipTerm20Y, chipTerm25Y, chipTerm30Y
        )
        emiChips.forEach { setupChipTouchAnimation(it) }

        // Switch listener for Property Calc
        switchPropertyCalc.setOnCheckedChangeListener { _, isChecked ->
            layoutPropertyInputs.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Live Comma Formatting for Property Value & Down Payment
        setupCommaFormatting(etPropertyValue) { updatePropertyNetLoan() }
        setupCommaFormatting(etDownPayment) { updatePropertyNetLoan() }
        setupCommaFormatting(etAmount)

        // Down Payment Chips
        chipDp10.setOnClickListener { setDownPaymentPercent(0.10); highlightDpChip(chipDp10) }
        chipDp15.setOnClickListener { setDownPaymentPercent(0.15); highlightDpChip(chipDp15) }
        chipDp20.setOnClickListener { setDownPaymentPercent(0.20); highlightDpChip(chipDp20) }
        chipDp25.setOnClickListener { setDownPaymentPercent(0.25); highlightDpChip(chipDp25) }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
            }
        })

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // SeekBar (12 to 360 months / 1 to 30 years for Home Loans)
        seekBarTerm.max = 360
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 12) 12 else progress
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
            if (seekBarTerm.progress > 12) {
                seekBarTerm.progress -= 12
                clearTermChipSelection()
            }
        }

        // Amount Chips
        chipAmount20L.setOnClickListener { setQuickAmount(2000000.0); highlightAmountChip(chipAmount20L) }
        chipAmount50L.setOnClickListener { setQuickAmount(5000000.0); highlightAmountChip(chipAmount50L) }
        chipAmount75L.setOnClickListener { setQuickAmount(7500000.0); highlightAmountChip(chipAmount75L) }
        chipAmount1Cr.setOnClickListener { setQuickAmount(10000000.0); highlightAmountChip(chipAmount1Cr) }

        // Rate Chips
        chipRate7_5.setOnClickListener { etRate.setText("7.5"); highlightRateChip(chipRate7_5) }
        chipRate8_5.setOnClickListener { etRate.setText("8.5"); highlightRateChip(chipRate8_5) }
        chipRate9_5.setOnClickListener { etRate.setText("9.5"); highlightRateChip(chipRate9_5) }
        chipRate10_5.setOnClickListener { etRate.setText("10.5"); highlightRateChip(chipRate10_5) }

        // Term Chips
        chipTerm5Y.setOnClickListener { seekBarTerm.progress = 60; highlightTermChip(chipTerm5Y) }
        chipTerm10Y.setOnClickListener { seekBarTerm.progress = 120; highlightTermChip(chipTerm10Y) }
        chipTerm15Y.setOnClickListener { seekBarTerm.progress = 180; highlightTermChip(chipTerm15Y) }
        chipTerm20Y.setOnClickListener { seekBarTerm.progress = 240; highlightTermChip(chipTerm20Y) }
        chipTerm25Y.setOnClickListener { seekBarTerm.progress = 300; highlightTermChip(chipTerm25Y) }
        chipTerm30Y.setOnClickListener { seekBarTerm.progress = 360; highlightTermChip(chipTerm30Y) }

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

        // Set Healthy Home Loan Defaults
        etPropertyValue.setText("50,00,000")
        etDownPayment.setText("10,00,000")
        updatePropertyNetLoan()
        etRate.setText("8.5")
        seekBarTerm.progress = 240
        updateTermDisplay(240)
        highlightAmountChip(chipAmount50L)
        highlightRateChip(chipRate8_5)
        highlightTermChip(chipTerm20Y)
        highlightDpChip(chipDp20)
    }

    private fun initEligibilityCalculatorViews() {
        etMonthlyIncome = findViewById(R.id.etMonthlyIncome)
        etExistingEmi = findViewById(R.id.etExistingEmi)
        etEligRate = findViewById(R.id.etEligRate)
        etEligTenureYears = findViewById(R.id.etEligTenureYears)

        btnCalculateEligibility = findViewById(R.id.btnCalculateEligibility)
        cardEligibilityResult = findViewById(R.id.cardEligibilityResult)

        txtEligMaxLoan = findViewById(R.id.txtEligMaxLoan)
        txtEligMaxEmi = findViewById(R.id.txtEligMaxEmi)
        txtEligTotalCost = findViewById(R.id.txtEligTotalCost)

        setupCommaFormatting(etMonthlyIncome)
        setupCommaFormatting(etExistingEmi)

        etEligRate.setText("8.5")
        etEligTenureYears.setText("20")
        etMonthlyIncome.setText("1,00,000")
        etExistingEmi.setText("15,000")

        setupButtonAnimation(btnCalculateEligibility)
        btnCalculateEligibility.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateEligibility()
        }
    }

    private fun initPrepaymentCalculatorViews() {
        etPreOutstanding = findViewById(R.id.etPreOutstanding)
        etPreRate = findViewById(R.id.etPreRate)
        etPreRemainingYears = findViewById(R.id.etPreRemainingYears)
        etPreLumpSum = findViewById(R.id.etPreLumpSum)

        btnCalculatePrepayment = findViewById(R.id.btnCalculatePrepayment)
        cardPrepaymentResult = findViewById(R.id.cardPrepaymentResult)

        txtPreInterestSaved = findViewById(R.id.txtPreInterestSaved)
        txtPreTenureSaved = findViewById(R.id.txtPreTenureSaved)
        txtPreNewTenure = findViewById(R.id.txtPreNewTenure)

        setupCommaFormatting(etPreOutstanding)
        setupCommaFormatting(etPreLumpSum)

        etPreOutstanding.setText("30,00,000")
        etPreRate.setText("8.5")
        etPreRemainingYears.setText("15")
        etPreLumpSum.setText("5,00,000")

        setupButtonAnimation(btnCalculatePrepayment)
        btnCalculatePrepayment.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculatePrepayment()
        }
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
            }
        })
    }

    private fun updatePropertyNetLoan() {
        if (!switchPropertyCalc.isChecked) return
        val propertyVal = getRawValue(etPropertyValue)
        val downPayVal = getRawValue(etDownPayment)

        if (propertyVal > 0) {
            val percent = (downPayVal / propertyVal) * 100
            txtDownPaymentPercentDisplay.text = String.format(Locale.US, "(%.1f%%)", percent)
        } else {
            txtDownPaymentPercentDisplay.text = "(0%)"
        }

        val netLoan = if (propertyVal > downPayVal) propertyVal - downPayVal else 0.0
        txtCalculatedNetLoan.text = "₹" + commaFormat.format(netLoan)
        if (netLoan > 0) {
            etAmount.setText(commaFormat.format(netLoan))
        }
    }

    private fun setDownPaymentPercent(percentage: Double) {
        val propertyVal = getRawValue(etPropertyValue)
        val dpVal = propertyVal * percentage
        etDownPayment.setText(commaFormat.format(dpVal))
        updatePropertyNetLoan()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun setQuickAmount(amount: Double) {
        etAmount.setText(commaFormat.format(amount))
    }

    private fun calculateEligibility() {
        val income = getRawValue(etMonthlyIncome)
        val existingEmi = getRawValue(etExistingEmi)
        val rate = etEligRate.text.toString().toDoubleOrNull() ?: 0.0
        val years = etEligTenureYears.text.toString().toIntOrNull() ?: 0

        if (income <= 0) {
            etMonthlyIncome.error = "Please enter valid monthly income"
            return
        }
        if (rate <= 0) {
            etEligRate.error = "Please enter valid interest rate"
            return
        }
        if (years <= 0) {
            etEligTenureYears.error = "Please enter valid loan tenure"
            return
        }

        // Standard 50% Banking FOIR Capacity
        val maxAllowedTotalEmi = income * 0.50
        val availableEmiForNewLoan = maxAllowedTotalEmi - existingEmi

        if (availableEmiForNewLoan <= 0) {
            Toast.makeText(this, "Existing EMIs exceed 50% income capacity limit.", Toast.LENGTH_LONG).show()
            return
        }

        val totalMonths = years * 12
        val r = rate / (12 * 100)

        // Reverse EMI Formula to calculate principal
        val maxPrincipal = (availableEmiForNewLoan * ((1 + r).pow(totalMonths.toDouble()) - 1)) /
                (r * (1 + r).pow(totalMonths.toDouble()))

        val totalCost = availableEmiForNewLoan * totalMonths

        txtEligMaxLoan.text = "₹" + commaFormat.format(maxPrincipal.toLong())
        txtEligMaxEmi.text = "₹" + commaFormat.format(availableEmiForNewLoan.toLong()) + " / mo"
        txtEligTotalCost.text = "₹" + commaFormat.format(totalCost.toLong())

        cardEligibilityResult.visibility = View.VISIBLE
    }

    private fun calculatePrepayment() {
        val outstanding = getRawValue(etPreOutstanding)
        val rate = etPreRate.text.toString().toDoubleOrNull() ?: 0.0
        val remainingYears = etPreRemainingYears.text.toString().toIntOrNull() ?: 0
        val lumpSum = getRawValue(etPreLumpSum)

        if (outstanding <= 0 || lumpSum <= 0 || rate <= 0 || remainingYears <= 0) {
            Toast.makeText(this, "Please enter valid prepayment details", Toast.LENGTH_SHORT).show()
            return
        }

        val months = remainingYears * 12
        val r = rate / (12 * 100)

        // Original EMI
        val originalEmi = (outstanding * r * (1 + r).pow(months.toDouble())) /
                ((1 + r).pow(months.toDouble()) - 1)
        val originalTotalInterest = (originalEmi * months) - outstanding

        // New Principal after lump sum prepayment
        val newPrincipal = outstanding - lumpSum

        if (newPrincipal <= 0) {
            txtPreInterestSaved.text = "₹" + commaFormat.format(originalTotalInterest.toLong())
            txtPreTenureSaved.text = "$months months (100% Paid Off!)"
            txtPreNewTenure.text = "0 months"
            cardPrepaymentResult.visibility = View.VISIBLE
            return
        }

        // New remaining tenure in months keeping EMI same
        val newMonthsDouble = ln(originalEmi / (originalEmi - (newPrincipal * r))) / ln(1 + r)
        val newMonths = newMonthsDouble.toInt()

        val newTotalInterest = (originalEmi * newMonths) - newPrincipal
        val interestSaved = originalTotalInterest - newTotalInterest
        val monthsSaved = months - newMonths

        txtPreInterestSaved.text = "₹" + commaFormat.format(if (interestSaved > 0) interestSaved.toLong() else 0)
        val yrsSaved = String.format(Locale.US, "%.1f", monthsSaved / 12.0)
        txtPreTenureSaved.text = "$monthsSaved months ($yrsSaved yrs)"
        txtPreNewTenure.text = "$newMonths months"

        cardPrepaymentResult.visibility = View.VISIBLE
    }

    private fun calculateAndNavigate() {
        val amount = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (amount <= 0) {
            etAmount.error = "Please enter a valid loan amount"
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter a valid interest rate"
            return
        }

        val monthlyRate = rate / (12 * 100)
        val emi = (amount * monthlyRate * (1 + monthlyRate).pow(totalMonths.toDouble())) /
                ((1 + monthlyRate).pow(totalMonths.toDouble()) - 1)
        val totalCost = emi * totalMonths
        val totalInterest = totalCost - amount

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, totalMonths)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = amount
        val scheduleCalendar = calendar.clone() as Calendar

        for (i in 1..totalMonths) {
            val interestPayment = balance * monthlyRate
            val principalPayment = emi - interestPayment
            balance -= principalPayment

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = i,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = emi,
                    principal = principalPayment,
                    interest = interestPayment,
                    balance = if (balance < 0) 0.0 else balance
                )
            )
        }

        val years = totalMonths / 12
        val months = totalMonths % 12

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", getActivityTitle())
            putExtra("LOAN_AMOUNT", amount)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", years)
            putExtra("LOAN_TERM_MONTHS", months)
            putExtra("START_DATE", txtSelectedDate.text.toString())
            putExtra("EMI", emi)
            putExtra("TOTAL_INTEREST", totalInterest)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", payoffDate)
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
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

    private fun highlightDpChip(selectedChip: MaterialButton) {
        val dpChips = listOf(chipDp10, chipDp15, chipDp20, chipDp25)
        dpChips.forEach { chip ->
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

    private fun highlightAmountChip(selectedChip: MaterialButton) {
        val amountChips = listOf(chipAmount20L, chipAmount50L, chipAmount75L, chipAmount1Cr)
        amountChips.forEach { chip ->
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
        val rateChips = listOf(chipRate7_5, chipRate8_5, chipRate9_5, chipRate10_5)
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
            7.5 -> highlightRateChip(chipRate7_5)
            8.5 -> highlightRateChip(chipRate8_5)
            9.5 -> highlightRateChip(chipRate9_5)
            10.5 -> highlightRateChip(chipRate10_5)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipTerm5Y, chipTerm10Y, chipTerm15Y, chipTerm20Y, chipTerm25Y, chipTerm30Y)
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
        val termChips = listOf(chipTerm5Y, chipTerm10Y, chipTerm15Y, chipTerm20Y, chipTerm25Y, chipTerm30Y)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(totalMonths: Int) {
        txtTermValueCombined.text = formatTerm(totalMonths)
        txtInstallments.text = "$totalMonths installments"
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

    private fun resetFields() {
        etPropertyValue.setText("50,00,000")
        etDownPayment.setText("10,00,000")
        updatePropertyNetLoan()
        etRate.setText("8.5")
        seekBarTerm.progress = 240
        updateTermDisplay(240)
        highlightAmountChip(chipAmount50L)
        highlightRateChip(chipRate8_5)
        highlightTermChip(chipTerm20Y)
        highlightDpChip(chipDp20)

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        scrollViewHomeLoan.smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}