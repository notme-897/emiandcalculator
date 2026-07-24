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

class BusinessLoanActivity : BaseInputActivity() {

    private lateinit var etAmount: EditText
    private lateinit var chipAmount5L: MaterialButton
    private lateinit var chipAmount10L: MaterialButton
    private lateinit var chipAmount25L: MaterialButton
    private lateinit var chipAmount50L: MaterialButton
    private lateinit var chipAmount1Cr: MaterialButton

    private lateinit var etProcessingFee: EditText
    private lateinit var txtNetDisbursedDisplay: TextView

    private lateinit var btnFreqMonthly: MaterialButton
    private lateinit var btnFreqBiWeekly: MaterialButton
    private lateinit var btnFreqWeekly: MaterialButton
    private lateinit var btnFreqDaily: MaterialButton

    private lateinit var etRate: EditText
    private lateinit var chipRate10_5: MaterialButton
    private lateinit var chipRate12_0: MaterialButton
    private lateinit var chipRate14_5: MaterialButton
    private lateinit var chipRate16_0: MaterialButton

    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipBizTerm1Y: MaterialButton
    private lateinit var chipBizTerm2Y: MaterialButton
    private lateinit var chipBizTerm3Y: MaterialButton
    private lateinit var chipBizTerm5Y: MaterialButton
    private lateinit var chipBizTerm7Y: MaterialButton
    private lateinit var chipBizTerm10Y: MaterialButton

    private lateinit var etOperatingIncome: EditText
    private lateinit var etExistingBizDebt: EditText
    private lateinit var txtDscrRatioDisplay: TextView
    private lateinit var txtDscrStatusNotice: TextView

    private lateinit var etTaxRate: EditText
    private lateinit var chipTax15: MaterialButton
    private lateinit var chipTax22: MaterialButton
    private lateinit var chipTax25: MaterialButton
    private lateinit var chipTax30: MaterialButton
    private lateinit var txtTaxSavingsDisplay: TextView
    private lateinit var txtEffectiveInterestAfterTaxDisplay: TextView

    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    enum class RepaymentFreq(val periodsPerYear: Int, val label: String) {
        MONTHLY(12, "Monthly"),
        BI_WEEKLY(26, "Bi-Weekly"),
        WEEKLY(52, "Weekly"),
        DAILY(300, "Daily")
    }

    private var selectedFrequency = RepaymentFreq.MONTHLY
    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_business_loan

    override fun getActivityTitle(): String = "Business Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        etAmount = findViewById(R.id.editTextNumber)
        chipAmount5L = findViewById(R.id.chipAmount5L)
        chipAmount10L = findViewById(R.id.chipAmount10L)
        chipAmount25L = findViewById(R.id.chipAmount25L)
        chipAmount50L = findViewById(R.id.chipAmount50L)
        chipAmount1Cr = findViewById(R.id.chipAmount1Cr)

        etProcessingFee = findViewById(R.id.etProcessingFee)
        txtNetDisbursedDisplay = findViewById(R.id.txtNetDisbursedDisplay)

        btnFreqMonthly = findViewById(R.id.btnFreqMonthly)
        btnFreqBiWeekly = findViewById(R.id.btnFreqBiWeekly)
        btnFreqWeekly = findViewById(R.id.btnFreqWeekly)
        btnFreqDaily = findViewById(R.id.btnFreqDaily)

        etRate = findViewById(R.id.editTextNumber2)
        chipRate10_5 = findViewById(R.id.chipRate10_5)
        chipRate12_0 = findViewById(R.id.chipRate12_0)
        chipRate14_5 = findViewById(R.id.chipRate14_5)
        chipRate16_0 = findViewById(R.id.chipRate16_0)

        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipBizTerm1Y = findViewById(R.id.chipBizTerm1Y)
        chipBizTerm2Y = findViewById(R.id.chipBizTerm2Y)
        chipBizTerm3Y = findViewById(R.id.chipBizTerm3Y)
        chipBizTerm5Y = findViewById(R.id.chipBizTerm5Y)
        chipBizTerm7Y = findViewById(R.id.chipBizTerm7Y)
        chipBizTerm10Y = findViewById(R.id.chipBizTerm10Y)

        etOperatingIncome = findViewById(R.id.etOperatingIncome)
        etExistingBizDebt = findViewById(R.id.etExistingBizDebt)
        txtDscrRatioDisplay = findViewById(R.id.txtDscrRatioDisplay)
        txtDscrStatusNotice = findViewById(R.id.txtDscrStatusNotice)

        etTaxRate = findViewById(R.id.etTaxRate)
        chipTax15 = findViewById(R.id.chipTax15)
        chipTax22 = findViewById(R.id.chipTax22)
        chipTax25 = findViewById(R.id.chipTax25)
        chipTax30 = findViewById(R.id.chipTax30)
        txtTaxSavingsDisplay = findViewById(R.id.txtTaxSavingsDisplay)
        txtEffectiveInterestAfterTaxDisplay = findViewById(R.id.txtEffectiveInterestAfterTaxDisplay)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Setup touch animations on all chips & buttons
        val allChips = listOf(
            chipAmount5L, chipAmount10L, chipAmount25L, chipAmount50L, chipAmount1Cr,
            btnFreqMonthly, btnFreqBiWeekly, btnFreqWeekly, btnFreqDaily,
            chipRate10_5, chipRate12_0, chipRate14_5, chipRate16_0,
            chipBizTerm1Y, chipBizTerm2Y, chipBizTerm3Y, chipBizTerm5Y, chipBizTerm7Y, chipBizTerm10Y,
            chipTax15, chipTax22, chipTax25, chipTax30
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etAmount) { updateNetDisbursed() }
        setupCommaFormatting(etProcessingFee) { updateNetDisbursed() }
        setupCommaFormatting(etOperatingIncome) { updateDscrAndTaxAnalysis() }
        setupCommaFormatting(etExistingBizDebt) { updateDscrAndTaxAnalysis() }

        // Amount Chips
        chipAmount5L.setOnClickListener { setQuickAmount(500000.0); highlightAmountChip(chipAmount5L) }
        chipAmount10L.setOnClickListener { setQuickAmount(1000000.0); highlightAmountChip(chipAmount10L) }
        chipAmount25L.setOnClickListener { setQuickAmount(2500000.0); highlightAmountChip(chipAmount25L) }
        chipAmount50L.setOnClickListener { setQuickAmount(5000000.0); highlightAmountChip(chipAmount50L) }
        chipAmount1Cr.setOnClickListener { setQuickAmount(10000000.0); highlightAmountChip(chipAmount1Cr) }

        // Frequency Toggles
        btnFreqMonthly.setOnClickListener { setRepaymentFrequency(RepaymentFreq.MONTHLY) }
        btnFreqBiWeekly.setOnClickListener { setRepaymentFrequency(RepaymentFreq.BI_WEEKLY) }
        btnFreqWeekly.setOnClickListener { setRepaymentFrequency(RepaymentFreq.WEEKLY) }
        btnFreqDaily.setOnClickListener { setRepaymentFrequency(RepaymentFreq.DAILY) }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateDscrAndTaxAnalysis()
            }
        })

        // Tax Rate Manual change watcher
        etTaxRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateTaxChipHighlights(s.toString().toDoubleOrNull())
                updateDscrAndTaxAnalysis()
            }
        })

        // Tax Rate Chips
        chipTax15.setOnClickListener { etTaxRate.setText("15"); highlightTaxChip(chipTax15) }
        chipTax22.setOnClickListener { etTaxRate.setText("22"); highlightTaxChip(chipTax22) }
        chipTax25.setOnClickListener { etTaxRate.setText("25"); highlightTaxChip(chipTax25) }
        chipTax30.setOnClickListener { etTaxRate.setText("30"); highlightTaxChip(chipTax30) }

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // SeekBar (12 to 120 months / 1 to 10 years for Business Loans)
        seekBarTerm.max = 120
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 12) 12 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
                updateDscrAndTaxAnalysis()
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

        // Rate Chips
        chipRate10_5.setOnClickListener { etRate.setText("10.5"); highlightRateChip(chipRate10_5) }
        chipRate12_0.setOnClickListener { etRate.setText("12.0"); highlightRateChip(chipRate12_0) }
        chipRate14_5.setOnClickListener { etRate.setText("14.5"); highlightRateChip(chipRate14_5) }
        chipRate16_0.setOnClickListener { etRate.setText("16.0"); highlightRateChip(chipRate16_0) }

        // Term Chips
        chipBizTerm1Y.setOnClickListener { seekBarTerm.progress = 12; highlightTermChip(chipBizTerm1Y) }
        chipBizTerm2Y.setOnClickListener { seekBarTerm.progress = 24; highlightTermChip(chipBizTerm2Y) }
        chipBizTerm3Y.setOnClickListener { seekBarTerm.progress = 36; highlightTermChip(chipBizTerm3Y) }
        chipBizTerm5Y.setOnClickListener { seekBarTerm.progress = 60; highlightTermChip(chipBizTerm5Y) }
        chipBizTerm7Y.setOnClickListener { seekBarTerm.progress = 84; highlightTermChip(chipBizTerm7Y) }
        chipBizTerm10Y.setOnClickListener { seekBarTerm.progress = 120; highlightTermChip(chipBizTerm10Y) }

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

        // Defaults for Business Loan
        etAmount.setText("15,00,000")
        etProcessingFee.setText("30,000")
        updateNetDisbursed()

        etRate.setText("13.5")
        seekBarTerm.progress = 36
        updateTermDisplay(36)

        etOperatingIncome.setText("2,50,000")
        etExistingBizDebt.setText("20,000")
        etTaxRate.setText("25")

        highlightAmountChip(chipAmount15LOrClosest())
        highlightRateChip(null)
        highlightTermChip(chipBizTerm3Y)
        highlightTaxChip(chipTax25)
        setRepaymentFrequency(RepaymentFreq.MONTHLY)
        updateDscrAndTaxAnalysis()
    }

    private fun chipAmount15LOrClosest(): MaterialButton = chipAmount10L

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
                updateDscrAndTaxAnalysis()
            }
        })
    }

    private fun setRepaymentFrequency(freq: RepaymentFreq) {
        selectedFrequency = freq
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

        applyStyle(btnFreqMonthly, freq == RepaymentFreq.MONTHLY)
        applyStyle(btnFreqBiWeekly, freq == RepaymentFreq.BI_WEEKLY)
        applyStyle(btnFreqWeekly, freq == RepaymentFreq.WEEKLY)
        applyStyle(btnFreqDaily, freq == RepaymentFreq.DAILY)

        val totalMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress
        updateTermDisplay(totalMonths)
        updateDscrAndTaxAnalysis()
    }

    private fun updateNetDisbursed() {
        val loanAmount = getRawValue(etAmount)
        val fee = getRawValue(etProcessingFee)
        val netDisbursed = if (loanAmount > fee) loanAmount - fee else 0.0
        txtNetDisbursedDisplay.text = "₹" + commaFormat.format(netDisbursed.toLong())
    }

    private fun setQuickAmount(amount: Double) {
        etAmount.setText(commaFormat.format(amount))
        updateNetDisbursed()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateDscrAndTaxAnalysis() {
        val loanAmount = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress
        val years = totalMonths / 12.0

        val operatingIncome = getRawValue(etOperatingIncome)
        val existingDebt = getRawValue(etExistingBizDebt)
        val taxRate = etTaxRate.text.toString().toDoubleOrNull() ?: 0.0

        if (loanAmount <= 0 || rate <= 0 || years <= 0) return

        val totalPeriods = (years * selectedFrequency.periodsPerYear).toInt()
        val periodicRate = rate / (selectedFrequency.periodsPerYear * 100)

        // EMI / Periodic Installment Formula
        val periodicPayment = (loanAmount * periodicRate * (1 + periodicRate).pow(totalPeriods.toDouble())) /
                ((1 + periodicRate).pow(totalPeriods.toDouble()) - 1)

        val totalRepayment = periodicPayment * totalPeriods
        val totalInterest = totalRepayment - loanAmount

        // 1. Tax Shield Calculation
        val taxSavings = totalInterest * (taxRate / 100.0)
        val netInterestAfterTax = totalInterest - taxSavings

        txtTaxSavingsDisplay.text = "₹" + commaFormat.format(taxSavings.toLong())
        txtEffectiveInterestAfterTaxDisplay.text = "₹" + commaFormat.format(netInterestAfterTax.toLong())

        // 2. DSCR Calculation
        val monthlyEquivalentEmi = periodicPayment * (selectedFrequency.periodsPerYear / 12.0)
        val totalMonthlyDebtService = monthlyEquivalentEmi + existingDebt

        if (operatingIncome > 0 && totalMonthlyDebtService > 0) {
            val dscr = operatingIncome / totalMonthlyDebtService
            txtDscrRatioDisplay.text = String.format(Locale.US, "%.2f", dscr)

            when {
                dscr >= 1.25 -> {
                    txtDscrStatusNotice.text = "🟢 DSCR >= 1.25: Strong Bank Approval Likelihood"
                    txtDscrStatusNotice.setTextColor(Color.parseColor("#00C853"))
                    txtDscrRatioDisplay.setTextColor(Color.parseColor("#00C853"))
                }
                dscr >= 1.0 -> {
                    txtDscrStatusNotice.text = "🟡 DSCR 1.0 - 1.24: Moderate Cash Flow Coverage"
                    txtDscrStatusNotice.setTextColor(Color.parseColor("#FF9800"))
                    txtDscrRatioDisplay.setTextColor(Color.parseColor("#FF9800"))
                }
                else -> {
                    txtDscrStatusNotice.text = "🔴 DSCR < 1.00: High Risk (Cash Flow Insufficient for Repayment)"
                    txtDscrStatusNotice.setTextColor(Color.parseColor("#E53935"))
                    txtDscrRatioDisplay.setTextColor(Color.parseColor("#E53935"))
                }
            }
        } else {
            txtDscrRatioDisplay.text = "0.00"
            txtDscrStatusNotice.text = "Enter Operating Income to compute DSCR ratio"
            txtDscrStatusNotice.setTextColor(Color.parseColor("#64748B"))
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

    private fun highlightAmountChip(selectedChip: MaterialButton) {
        val amountChips = listOf(chipAmount5L, chipAmount10L, chipAmount25L, chipAmount50L, chipAmount1Cr)
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
        val rateChips = listOf(chipRate10_5, chipRate12_0, chipRate14_5, chipRate16_0)
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
            10.5 -> highlightRateChip(chipRate10_5)
            12.0 -> highlightRateChip(chipRate12_0)
            14.5 -> highlightRateChip(chipRate14_5)
            16.0 -> highlightRateChip(chipRate16_0)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightTaxChip(selectedChip: MaterialButton?) {
        val taxChips = listOf(chipTax15, chipTax22, chipTax25, chipTax30)
        taxChips.forEach { chip ->
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

    private fun updateTaxChipHighlights(tax: Double?) {
        when (tax) {
            15.0 -> highlightTaxChip(chipTax15)
            22.0 -> highlightTaxChip(chipTax22)
            25.0 -> highlightTaxChip(chipTax25)
            30.0 -> highlightTaxChip(chipTax30)
            else -> highlightTaxChip(null)
        }
    }

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipBizTerm1Y, chipBizTerm2Y, chipBizTerm3Y, chipBizTerm5Y, chipBizTerm7Y, chipBizTerm10Y)
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
        val termChips = listOf(chipBizTerm1Y, chipBizTerm2Y, chipBizTerm3Y, chipBizTerm5Y, chipBizTerm7Y, chipBizTerm10Y)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(totalMonths: Int) {
        val years = totalMonths / 12.0
        val totalPeriods = (years * selectedFrequency.periodsPerYear).toInt()
        txtTermValueCombined.text = formatTerm(totalMonths)
        txtInstallments.text = "$totalPeriods ${selectedFrequency.label.lowercase()} installments"
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

        val years = totalMonths / 12.0
        val totalPeriods = (years * selectedFrequency.periodsPerYear).toInt()
        val periodicRate = rate / (selectedFrequency.periodsPerYear * 100)

        val periodicPayment = (amount * periodicRate * (1 + periodicRate).pow(totalPeriods.toDouble())) /
                ((1 + periodicRate).pow(totalPeriods.toDouble()) - 1)
        val totalCost = periodicPayment * totalPeriods
        val totalInterest = totalCost - amount

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, totalMonths)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = amount
        val scheduleCalendar = calendar.clone() as Calendar

        val daysPerPeriod = when (selectedFrequency) {
            RepaymentFreq.MONTHLY -> 30
            RepaymentFreq.BI_WEEKLY -> 14
            RepaymentFreq.WEEKLY -> 7
            RepaymentFreq.DAILY -> 1
        }

        for (i in 1..totalPeriods) {
            val interestPayment = balance * periodicRate
            val principalPayment = periodicPayment - interestPayment
            balance -= principalPayment

            scheduleCalendar.add(Calendar.DAY_OF_YEAR, daysPerPeriod)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = i,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = periodicPayment,
                    principal = principalPayment,
                    interest = interestPayment,
                    balance = if (balance < 0) 0.0 else balance
                )
            )
        }

        val yearsInt = totalMonths / 12
        val monthsInt = totalMonths % 12
        val titleText = "Business Loan (${selectedFrequency.label})"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", amount)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", txtSelectedDate.text.toString())
            putExtra("EMI", periodicPayment)
            putExtra("TOTAL_INTEREST", totalInterest)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", payoffDate)
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etAmount.setText("15,00,000")
        etProcessingFee.setText("30,000")
        updateNetDisbursed()

        etRate.setText("13.5")
        seekBarTerm.progress = 36
        updateTermDisplay(36)

        etOperatingIncome.setText("2,50,000")
        etExistingBizDebt.setText("20,000")
        etTaxRate.setText("25")

        highlightAmountChip(chipAmount10L)
        highlightRateChip(null)
        highlightTermChip(chipBizTerm3Y)
        highlightTaxChip(chipTax25)
        setRepaymentFrequency(RepaymentFreq.MONTHLY)
        updateDscrAndTaxAnalysis()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewBusinessLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}