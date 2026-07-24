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

class GoldLoanActivity : BaseInputActivity() {

    private lateinit var etGoldWeight: EditText
    private lateinit var chipWeight10g: MaterialButton
    private lateinit var chipWeight20g: MaterialButton
    private lateinit var chipWeight50g: MaterialButton
    private lateinit var chipWeight100g: MaterialButton
    private lateinit var chipWeight250g: MaterialButton

    private lateinit var chipKarat18: MaterialButton
    private lateinit var chipKarat20: MaterialButton
    private lateinit var chipKarat22: MaterialButton
    private lateinit var chipKarat24: MaterialButton

    private lateinit var etGoldRate: EditText
    private lateinit var txtTotalGoldValueDisplay: TextView
    private lateinit var chipLtv60: MaterialButton
    private lateinit var chipLtv70: MaterialButton
    private lateinit var chipLtv75: MaterialButton
    private lateinit var txtMaxEligibleLoanDisplay: TextView
    private lateinit var txtLoanPerGramDisplay: TextView

    private lateinit var btnSchemeBullet: MaterialButton
    private lateinit var btnSchemeInterestOnly: MaterialButton
    private lateinit var btnSchemeEmi: MaterialButton

    private lateinit var etRate: EditText
    private lateinit var chipRate8_5: MaterialButton
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate10_5: MaterialButton
    private lateinit var chipRate12_0: MaterialButton

    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipGoldTerm3M: MaterialButton
    private lateinit var chipGoldTerm6M: MaterialButton
    private lateinit var chipGoldTerm12M: MaterialButton
    private lateinit var chipGoldTerm18M: MaterialButton
    private lateinit var chipGoldTerm24M: MaterialButton

    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    enum class GoldRepaymentScheme {
        BULLET,
        MONTHLY_INTEREST_ONLY,
        STANDARD_EMI
    }

    private var selectedKarat = 22.0
    private var selectedLtv = 75.0
    private var selectedScheme = GoldRepaymentScheme.BULLET

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_gold_loan

    override fun getActivityTitle(): String = "Gold Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        etGoldWeight = findViewById(R.id.etGoldWeight)
        chipWeight10g = findViewById(R.id.chipWeight10g)
        chipWeight20g = findViewById(R.id.chipWeight20g)
        chipWeight50g = findViewById(R.id.chipWeight50g)
        chipWeight100g = findViewById(R.id.chipWeight100g)
        chipWeight250g = findViewById(R.id.chipWeight250g)

        chipKarat18 = findViewById(R.id.chipKarat18)
        chipKarat20 = findViewById(R.id.chipKarat20)
        chipKarat22 = findViewById(R.id.chipKarat22)
        chipKarat24 = findViewById(R.id.chipKarat24)

        etGoldRate = findViewById(R.id.etGoldRate)
        txtTotalGoldValueDisplay = findViewById(R.id.txtTotalGoldValueDisplay)
        chipLtv60 = findViewById(R.id.chipLtv60)
        chipLtv70 = findViewById(R.id.chipLtv70)
        chipLtv75 = findViewById(R.id.chipLtv75)
        txtMaxEligibleLoanDisplay = findViewById(R.id.txtMaxEligibleLoanDisplay)
        txtLoanPerGramDisplay = findViewById(R.id.txtLoanPerGramDisplay)

        btnSchemeBullet = findViewById(R.id.btnSchemeBullet)
        btnSchemeInterestOnly = findViewById(R.id.btnSchemeInterestOnly)
        btnSchemeEmi = findViewById(R.id.btnSchemeEmi)

        etRate = findViewById(R.id.editTextNumber2)
        chipRate8_5 = findViewById(R.id.chipRate8_5)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate10_5 = findViewById(R.id.chipRate10_5)
        chipRate12_0 = findViewById(R.id.chipRate12_0)

        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipGoldTerm3M = findViewById(R.id.chipGoldTerm3M)
        chipGoldTerm6M = findViewById(R.id.chipGoldTerm6M)
        chipGoldTerm12M = findViewById(R.id.chipGoldTerm12M)
        chipGoldTerm18M = findViewById(R.id.chipGoldTerm18M)
        chipGoldTerm24M = findViewById(R.id.chipGoldTerm24M)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Setup touch animations
        val allChips = listOf(
            chipWeight10g, chipWeight20g, chipWeight50g, chipWeight100g, chipWeight250g,
            chipKarat18, chipKarat20, chipKarat22, chipKarat24,
            chipLtv60, chipLtv70, chipLtv75,
            btnSchemeBullet, btnSchemeInterestOnly, btnSchemeEmi,
            chipRate8_5, chipRate9_5, chipRate10_5, chipRate12_0,
            chipGoldTerm3M, chipGoldTerm6M, chipGoldTerm12M, chipGoldTerm18M, chipGoldTerm24M
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etGoldWeight) { updateGoldValuation() }
        setupCommaFormatting(etGoldRate) { updateGoldValuation() }

        // Weight Chips
        chipWeight10g.setOnClickListener { etGoldWeight.setText("10"); highlightWeightChip(chipWeight10g) }
        chipWeight20g.setOnClickListener { etGoldWeight.setText("20"); highlightWeightChip(chipWeight20g) }
        chipWeight50g.setOnClickListener { etGoldWeight.setText("50"); highlightWeightChip(chipWeight50g) }
        chipWeight100g.setOnClickListener { etGoldWeight.setText("100"); highlightWeightChip(chipWeight100g) }
        chipWeight250g.setOnClickListener { etGoldWeight.setText("250"); highlightWeightChip(chipWeight250g) }

        // Karat Chips
        chipKarat18.setOnClickListener { selectedKarat = 18.0; highlightKaratChip(chipKarat18); updateGoldValuation() }
        chipKarat20.setOnClickListener { selectedKarat = 20.0; highlightKaratChip(chipKarat20); updateGoldValuation() }
        chipKarat22.setOnClickListener { selectedKarat = 22.0; highlightKaratChip(chipKarat22); updateGoldValuation() }
        chipKarat24.setOnClickListener { selectedKarat = 24.0; highlightKaratChip(chipKarat24); updateGoldValuation() }

        // LTV Chips
        chipLtv60.setOnClickListener { selectedLtv = 60.0; highlightLtvChip(chipLtv60); updateGoldValuation() }
        chipLtv70.setOnClickListener { selectedLtv = 70.0; highlightLtvChip(chipLtv70); updateGoldValuation() }
        chipLtv75.setOnClickListener { selectedLtv = 75.0; highlightLtvChip(chipLtv75); updateGoldValuation() }

        // Scheme Selector Toggles
        btnSchemeBullet.setOnClickListener { setRepaymentScheme(GoldRepaymentScheme.BULLET) }
        btnSchemeInterestOnly.setOnClickListener { setRepaymentScheme(GoldRepaymentScheme.MONTHLY_INTEREST_ONLY) }
        btnSchemeEmi.setOnClickListener { setRepaymentScheme(GoldRepaymentScheme.STANDARD_EMI) }

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

        // SeekBar (3 to 24 Months for Gold Loans)
        seekBarTerm.max = 24
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 3) 3 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnAdd.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (seekBarTerm.progress < seekBarTerm.max) {
                seekBarTerm.progress += 3
                clearTermChipSelection()
            }
        }
        btnMinus.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (seekBarTerm.progress > 3) {
                seekBarTerm.progress -= 3
                clearTermChipSelection()
            }
        }

        // Tenure Chips
        chipGoldTerm3M.setOnClickListener { seekBarTerm.progress = 3; highlightTermChip(chipGoldTerm3M) }
        chipGoldTerm6M.setOnClickListener { seekBarTerm.progress = 6; highlightTermChip(chipGoldTerm6M) }
        chipGoldTerm12M.setOnClickListener { seekBarTerm.progress = 12; highlightTermChip(chipGoldTerm12M) }
        chipGoldTerm18M.setOnClickListener { seekBarTerm.progress = 18; highlightTermChip(chipGoldTerm18M) }
        chipGoldTerm24M.setOnClickListener { seekBarTerm.progress = 24; highlightTermChip(chipGoldTerm24M) }

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
        etGoldWeight.setText("50")
        etGoldRate.setText("7,200")
        etRate.setText("9.5")
        seekBarTerm.progress = 12
        updateTermDisplay(12)

        highlightWeightChip(chipWeight50g)
        highlightKaratChip(chipKarat22)
        highlightLtvChip(chipLtv75)
        highlightRateChip(chipRate9_5)
        highlightTermChip(chipGoldTerm12M)
        setRepaymentScheme(GoldRepaymentScheme.BULLET)
        updateGoldValuation()
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
                updateGoldValuation()
            }
        })
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateGoldValuation() {
        val weight = getRawValue(etGoldWeight)
        val ratePerGram = getRawValue(etGoldRate)

        if (weight <= 0 || ratePerGram <= 0) return

        val pureWeight = weight * (selectedKarat / 24.0)
        val totalGoldMarketValue = pureWeight * ratePerGram
        val maxLoanCash = totalGoldMarketValue * (selectedLtv / 100.0)
        val loanPerGram = if (weight > 0) maxLoanCash / weight else 0.0

        txtTotalGoldValueDisplay.text = "₹" + commaFormat.format(totalGoldMarketValue.toLong())
        txtMaxEligibleLoanDisplay.text = "₹" + commaFormat.format(maxLoanCash.toLong())
        txtLoanPerGramDisplay.text = "₹" + commaFormat.format(loanPerGram.toLong()) + " / gram"
    }

    private fun setRepaymentScheme(scheme: GoldRepaymentScheme) {
        selectedScheme = scheme
        val selectedBlue = ContextCompat.getColor(this, R.color.custom_blue)
        val selectedText = ContextCompat.getColor(this, android.R.color.white)
        val unselectedBg = Color.parseColor("#F8FAFC")
        val unselectedText = Color.parseColor("#1E293B")
        val strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        val strokeWidth = (1 * resources.displayMetrics.density).toInt()

        fun applyStyle(btn: MaterialButton, isSelected: Boolean) {
            if (isSelected) {
                btn.setBackgroundColor(selectedBlue)
                btn.setTextColor(selectedText)
                btn.strokeWidth = 0
            } else {
                btn.setBackgroundColor(unselectedBg)
                btn.setTextColor(unselectedText)
                btn.strokeColor = strokeColor
                btn.strokeWidth = strokeWidth
            }
        }

        applyStyle(btnSchemeBullet, scheme == GoldRepaymentScheme.BULLET)
        applyStyle(btnSchemeInterestOnly, scheme == GoldRepaymentScheme.MONTHLY_INTEREST_ONLY)
        applyStyle(btnSchemeEmi, scheme == GoldRepaymentScheme.STANDARD_EMI)
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

    private fun highlightWeightChip(selectedChip: MaterialButton) {
        val chips = listOf(chipWeight10g, chipWeight20g, chipWeight50g, chipWeight100g, chipWeight250g)
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

    private fun highlightKaratChip(selectedChip: MaterialButton) {
        val chips = listOf(chipKarat18, chipKarat20, chipKarat22, chipKarat24)
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

    private fun highlightLtvChip(selectedChip: MaterialButton) {
        val chips = listOf(chipLtv60, chipLtv70, chipLtv75)
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
        val termChips = listOf(chipGoldTerm3M, chipGoldTerm6M, chipGoldTerm12M, chipGoldTerm18M, chipGoldTerm24M)
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
        val termChips = listOf(chipGoldTerm3M, chipGoldTerm6M, chipGoldTerm12M, chipGoldTerm18M, chipGoldTerm24M)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(months: Int) {
        txtTermValueCombined.text = "$months months"
        txtInstallments.text = "$months payments"
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
        val weight = getRawValue(etGoldWeight)
        val ratePerGram = getRawValue(etGoldRate)
        val interestRate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = if (seekBarTerm.progress < 3) 3 else seekBarTerm.progress

        if (weight <= 0) {
            etGoldWeight.error = "Please enter valid gold weight"
            return
        }
        if (ratePerGram <= 0) {
            etGoldRate.error = "Please enter valid gold rate"
            return
        }
        if (interestRate <= 0) {
            etRate.error = "Please enter valid interest rate"
            return
        }

        val pureWeight = weight * (selectedKarat / 24.0)
        val totalGoldMarketValue = pureWeight * ratePerGram
        val loanAmount = totalGoldMarketValue * (selectedLtv / 100.0)

        val monthlyInterestRate = interestRate / (12 * 100)

        val periodicEmi: Double
        val totalInterest: Double
        val totalCost: Double

        when (selectedScheme) {
            GoldRepaymentScheme.BULLET -> {
                totalInterest = loanAmount * (interestRate / 100.0) * (months / 12.0)
                totalCost = loanAmount + totalInterest
                periodicEmi = 0.0 // Pay $0 monthly
            }
            GoldRepaymentScheme.MONTHLY_INTEREST_ONLY -> {
                periodicEmi = loanAmount * monthlyInterestRate
                totalInterest = periodicEmi * months
                totalCost = loanAmount + totalInterest
            }
            GoldRepaymentScheme.STANDARD_EMI -> {
                periodicEmi = (loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(months.toDouble())) /
                        ((1 + monthlyInterestRate).pow(months.toDouble()) - 1)
                totalCost = periodicEmi * months
                totalInterest = totalCost - loanAmount
            }
        }

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, months)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = loanAmount
        val scheduleCalendar = calendar.clone() as Calendar

        for (i in 1..months) {
            scheduleCalendar.add(Calendar.MONTH, 1)

            when (selectedScheme) {
                GoldRepaymentScheme.BULLET -> {
                    val isLastMonth = (i == months)
                    val monthInterest = loanAmount * monthlyInterestRate
                    schedule.add(
                        PaymentScheduleItem(
                            emiNo = i,
                            date = dateFormatter.format(scheduleCalendar.time),
                            emi = if (isLastMonth) totalCost else 0.0,
                            principal = if (isLastMonth) loanAmount else 0.0,
                            interest = monthInterest,
                            balance = if (isLastMonth) 0.0 else loanAmount
                        )
                    )
                }
                GoldRepaymentScheme.MONTHLY_INTEREST_ONLY -> {
                    val isLastMonth = (i == months)
                    val monthInterest = loanAmount * monthlyInterestRate
                    schedule.add(
                        PaymentScheduleItem(
                            emiNo = i,
                            date = dateFormatter.format(scheduleCalendar.time),
                            emi = if (isLastMonth) monthInterest + loanAmount else monthInterest,
                            principal = if (isLastMonth) loanAmount else 0.0,
                            interest = monthInterest,
                            balance = if (isLastMonth) 0.0 else loanAmount
                        )
                    )
                }
                GoldRepaymentScheme.STANDARD_EMI -> {
                    val interestPayment = balance * monthlyInterestRate
                    val principalPayment = periodicEmi - interestPayment
                    balance -= principalPayment

                    schedule.add(
                        PaymentScheduleItem(
                            emiNo = i,
                            date = dateFormatter.format(scheduleCalendar.time),
                            emi = periodicEmi,
                            principal = principalPayment,
                            interest = interestPayment,
                            balance = if (balance < 0) 0.0 else balance
                        )
                    )
                }
            }
        }

        val yearsInt = months / 12
        val monthsInt = months % 12
        val schemeLabel = when (selectedScheme) {
            GoldRepaymentScheme.BULLET -> "Bullet Scheme"
            GoldRepaymentScheme.MONTHLY_INTEREST_ONLY -> "Interest-Only"
            GoldRepaymentScheme.STANDARD_EMI -> "Standard EMI"
        }
        val titleText = "Gold Loan ($schemeLabel)"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", loanAmount)
            putExtra("INTEREST_RATE", interestRate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", txtSelectedDate.text.toString())
            putExtra("EMI", if (selectedScheme == GoldRepaymentScheme.BULLET) totalCost else periodicEmi)
            putExtra("TOTAL_INTEREST", totalInterest)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", payoffDate)
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etGoldWeight.setText("50")
        etGoldRate.setText("7,200")
        etRate.setText("9.5")
        seekBarTerm.progress = 12
        updateTermDisplay(12)

        selectedKarat = 22.0
        selectedLtv = 75.0

        highlightWeightChip(chipWeight50g)
        highlightKaratChip(chipKarat22)
        highlightLtvChip(chipLtv75)
        highlightRateChip(chipRate9_5)
        highlightTermChip(chipGoldTerm12M)
        setRepaymentScheme(GoldRepaymentScheme.BULLET)
        updateGoldValuation()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewGoldLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}