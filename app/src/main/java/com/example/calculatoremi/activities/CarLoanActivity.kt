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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class CarLoanActivity : BaseInputActivity() {

    private lateinit var etVehiclePrice: EditText
    private lateinit var etCarDownPayment: EditText
    private lateinit var txtCarDpPercentDisplay: TextView
    private lateinit var etTradeInValue: EditText
    private lateinit var txtCarNetLoanDisplay: TextView
    private lateinit var txtLtvRatioDisplay: TextView

    private lateinit var chipCarDp10: MaterialButton
    private lateinit var chipCarDp15: MaterialButton
    private lateinit var chipCarDp20: MaterialButton
    private lateinit var chipCarDp25: MaterialButton
    private lateinit var chipCarDp30: MaterialButton

    private lateinit var btnRateTypeReducing: MaterialButton
    private lateinit var btnRateTypeFlat: MaterialButton
    private lateinit var cardFlatRateWarning: MaterialCardView
    private lateinit var txtFlatRateEquivalentDisplay: TextView

    private lateinit var etAmount: EditText
    private lateinit var etRate: EditText
    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipRate7_5: MaterialButton
    private lateinit var chipRate8_5: MaterialButton
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate10_5: MaterialButton

    private lateinit var chipCarTerm1Y: MaterialButton
    private lateinit var chipCarTerm2Y: MaterialButton
    private lateinit var chipCarTerm3Y: MaterialButton
    private lateinit var chipCarTerm4Y: MaterialButton
    private lateinit var chipCarTerm5Y: MaterialButton
    private lateinit var chipCarTerm7Y: MaterialButton

    private lateinit var etProcessingFee: EditText
    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    private lateinit var txtTotalOwnershipCostDisplay: TextView
    private lateinit var txtYear3CarValueDisplay: TextView
    private lateinit var txtEquityStatusNotice: TextView

    private var isFlatRateMode = false
    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_car_loan

    override fun getActivityTitle(): String = "Car Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        etVehiclePrice = findViewById(R.id.etVehiclePrice)
        etCarDownPayment = findViewById(R.id.etCarDownPayment)
        txtCarDpPercentDisplay = findViewById(R.id.txtCarDpPercentDisplay)
        etTradeInValue = findViewById(R.id.etTradeInValue)
        txtCarNetLoanDisplay = findViewById(R.id.txtCarNetLoanDisplay)
        txtLtvRatioDisplay = findViewById(R.id.txtLtvRatioDisplay)

        chipCarDp10 = findViewById(R.id.chipCarDp10)
        chipCarDp15 = findViewById(R.id.chipCarDp15)
        chipCarDp20 = findViewById(R.id.chipCarDp20)
        chipCarDp25 = findViewById(R.id.chipCarDp25)
        chipCarDp30 = findViewById(R.id.chipCarDp30)

        btnRateTypeReducing = findViewById(R.id.btnRateTypeReducing)
        btnRateTypeFlat = findViewById(R.id.btnRateTypeFlat)
        cardFlatRateWarning = findViewById(R.id.cardFlatRateWarning)
        txtFlatRateEquivalentDisplay = findViewById(R.id.txtFlatRateEquivalentDisplay)

        etAmount = findViewById(R.id.editTextNumber)
        etRate = findViewById(R.id.editTextNumber2)
        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipRate7_5 = findViewById(R.id.chipRate7_5)
        chipRate8_5 = findViewById(R.id.chipRate8_5)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate10_5 = findViewById(R.id.chipRate10_5)

        chipCarTerm1Y = findViewById(R.id.chipCarTerm1Y)
        chipCarTerm2Y = findViewById(R.id.chipCarTerm2Y)
        chipCarTerm3Y = findViewById(R.id.chipCarTerm3Y)
        chipCarTerm4Y = findViewById(R.id.chipCarTerm4Y)
        chipCarTerm5Y = findViewById(R.id.chipCarTerm5Y)
        chipCarTerm7Y = findViewById(R.id.chipCarTerm7Y)

        etProcessingFee = findViewById(R.id.etProcessingFee)
        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        txtTotalOwnershipCostDisplay = findViewById(R.id.txtTotalOwnershipCostDisplay)
        txtYear3CarValueDisplay = findViewById(R.id.txtYear3CarValueDisplay)
        txtEquityStatusNotice = findViewById(R.id.txtEquityStatusNotice)

        // Setup touch animations on all chips
        val allChips = listOf(
            chipCarDp10, chipCarDp15, chipCarDp20, chipCarDp25, chipCarDp30,
            chipRate7_5, chipRate8_5, chipRate9_5, chipRate10_5,
            chipCarTerm1Y, chipCarTerm2Y, chipCarTerm3Y, chipCarTerm4Y, chipCarTerm5Y, chipCarTerm7Y,
            btnRateTypeReducing, btnRateTypeFlat
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etVehiclePrice) { updateCarNetLoan() }
        setupCommaFormatting(etCarDownPayment) { updateCarNetLoan() }
        setupCommaFormatting(etTradeInValue) { updateCarNetLoan() }
        setupCommaFormatting(etAmount)
        setupCommaFormatting(etProcessingFee)

        // Down Payment Chips
        chipCarDp10.setOnClickListener { setDownPaymentPercent(0.10); highlightDpChip(chipCarDp10) }
        chipCarDp15.setOnClickListener { setDownPaymentPercent(0.15); highlightDpChip(chipCarDp15) }
        chipCarDp20.setOnClickListener { setDownPaymentPercent(0.20); highlightDpChip(chipCarDp20) }
        chipCarDp25.setOnClickListener { setDownPaymentPercent(0.25); highlightDpChip(chipCarDp25) }
        chipCarDp30.setOnClickListener { setDownPaymentPercent(0.30); highlightDpChip(chipCarDp30) }

        // Flat Rate vs Reducing Toggle Listeners
        btnRateTypeReducing.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            setRateCalculationMode(false)
        }
        btnRateTypeFlat.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            setRateCalculationMode(true)
        }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateFlatRateWarningDisplay()
            }
        })

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // Car Loan SeekBar (12 to 84 months / 1 to 7 years)
        seekBarTerm.max = 84
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 12) 12 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
                updateFlatRateWarningDisplay()
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
        chipRate7_5.setOnClickListener { etRate.setText("7.5"); highlightRateChip(chipRate7_5) }
        chipRate8_5.setOnClickListener { etRate.setText("8.5"); highlightRateChip(chipRate8_5) }
        chipRate9_5.setOnClickListener { etRate.setText("9.5"); highlightRateChip(chipRate9_5) }
        chipRate10_5.setOnClickListener { etRate.setText("10.5"); highlightRateChip(chipRate10_5) }

        // Car Term Chips
        chipCarTerm1Y.setOnClickListener { seekBarTerm.progress = 12; highlightTermChip(chipCarTerm1Y) }
        chipCarTerm2Y.setOnClickListener { seekBarTerm.progress = 24; highlightTermChip(chipCarTerm2Y) }
        chipCarTerm3Y.setOnClickListener { seekBarTerm.progress = 36; highlightTermChip(chipCarTerm3Y) }
        chipCarTerm4Y.setOnClickListener { seekBarTerm.progress = 48; highlightTermChip(chipCarTerm4Y) }
        chipCarTerm5Y.setOnClickListener { seekBarTerm.progress = 60; highlightTermChip(chipCarTerm5Y) }
        chipCarTerm7Y.setOnClickListener { seekBarTerm.progress = 84; highlightTermChip(chipCarTerm7Y) }

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

        // Defaults for Car Loan
        etVehiclePrice.setText("10,00,000")
        etCarDownPayment.setText("2,00,000")
        etTradeInValue.setText("1,00,000")
        updateCarNetLoan()
        etRate.setText("9.0")
        seekBarTerm.progress = 60
        etProcessingFee.setText("5,000")
        updateTermDisplay(60)

        highlightRateChip(null)
        highlightTermChip(chipCarTerm5Y)
        highlightDpChip(chipCarDp20)
        setRateCalculationMode(false)
        updateDepreciationAnalysisPreview()
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
                updateDepreciationAnalysisPreview()
            }
        })
    }

    private fun setRateCalculationMode(isFlat: Boolean) {
        isFlatRateMode = isFlat
        val selectedBlue = ContextCompat.getColor(this, R.color.custom_blue)
        val selectedText = ContextCompat.getColor(this, android.R.color.white)
        val unselectedBg = Color.parseColor("#F8FAFC")
        val unselectedText = Color.parseColor("#1E293B")
        val strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        val strokeWidth = (1 * resources.displayMetrics.density).toInt()

        if (isFlat) {
            btnRateTypeFlat.setBackgroundColor(selectedBlue)
            btnRateTypeFlat.setTextColor(selectedText)
            btnRateTypeFlat.strokeWidth = 0

            btnRateTypeReducing.setBackgroundColor(unselectedBg)
            btnRateTypeReducing.setTextColor(unselectedText)
            btnRateTypeReducing.strokeColor = strokeColor
            btnRateTypeReducing.strokeWidth = strokeWidth

            cardFlatRateWarning.visibility = View.VISIBLE
            updateFlatRateWarningDisplay()
        } else {
            btnRateTypeReducing.setBackgroundColor(selectedBlue)
            btnRateTypeReducing.setTextColor(selectedText)
            btnRateTypeReducing.strokeWidth = 0

            btnRateTypeFlat.setBackgroundColor(unselectedBg)
            btnRateTypeFlat.setTextColor(unselectedText)
            btnRateTypeFlat.strokeColor = strokeColor
            btnRateTypeFlat.strokeWidth = strokeWidth

            cardFlatRateWarning.visibility = View.GONE
        }
    }

    private fun updateFlatRateWarningDisplay() {
        if (!isFlatRateMode) return
        val flatRate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress
        val years = months / 12.0

        if (flatRate > 0 && years > 0) {
            // Approx Equivalent Reducing Rate: Approx (2 * n / (n + 1)) * flatRate
            val equivRate = (2.0 * months / (months + 1.0)) * flatRate
            txtFlatRateEquivalentDisplay.text = String.format(
                Locale.US,
                "A %.1f%% Flat Rate costs significantly more! It is equivalent to an Effective ~%.1f%% Annual Reducing Balance Rate over %.1f years.",
                flatRate, equivRate, years
            )
        }
    }

    private fun updateCarNetLoan() {
        val vehiclePrice = getRawValue(etVehiclePrice)
        val downPayment = getRawValue(etCarDownPayment)
        val tradeIn = getRawValue(etTradeInValue)

        if (vehiclePrice > 0) {
            val percent = (downPayment / vehiclePrice) * 100
            txtCarDpPercentDisplay.text = String.format(Locale.US, "(%.1f%%)", percent)

            val ltv = ((vehiclePrice - (downPayment + tradeIn)) / vehiclePrice) * 100
            txtLtvRatioDisplay.text = String.format(Locale.US, "%.1f%%", if (ltv > 0) ltv else 0.0)
        } else {
            txtCarDpPercentDisplay.text = "(0%)"
            txtLtvRatioDisplay.text = "0.0%"
        }

        val netLoan = if (vehiclePrice > (downPayment + tradeIn)) vehiclePrice - (downPayment + tradeIn) else 0.0
        txtCarNetLoanDisplay.text = "₹" + commaFormat.format(netLoan)
        if (netLoan > 0) {
            etAmount.setText(commaFormat.format(netLoan))
        }
    }

    private fun setDownPaymentPercent(percentage: Double) {
        val vehiclePrice = getRawValue(etVehiclePrice)
        val dpVal = vehiclePrice * percentage
        etCarDownPayment.setText(commaFormat.format(dpVal))
        updateCarNetLoan()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun updateDepreciationAnalysisPreview() {
        val vehiclePrice = getRawValue(etVehiclePrice)
        val downPayment = getRawValue(etCarDownPayment)
        val tradeIn = getRawValue(etTradeInValue)
        val netLoan = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val processingFee = getRawValue(etProcessingFee)
        val totalMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (netLoan <= 0 || rate <= 0 || vehiclePrice <= 0) return

        val monthlyRate = rate / (12 * 100)
        val emi = if (isFlatRateMode) {
            val totalInt = (netLoan * rate * (totalMonths / 12.0)) / 100.0
            (netLoan + totalInt) / totalMonths
        } else {
            (netLoan * monthlyRate * (1 + monthlyRate).pow(totalMonths.toDouble())) /
                    ((1 + monthlyRate).pow(totalMonths.toDouble()) - 1)
        }

        val totalCostOfOwnership = downPayment + tradeIn + (emi * totalMonths) + processingFee
        txtTotalOwnershipCostDisplay.text = "₹" + commaFormat.format(totalCostOfOwnership.toLong())

        // Vehicle Depreciation: ~35% depreciation at Year 3
        val year3CarValue = vehiclePrice * 0.65
        txtYear3CarValueDisplay.text = "₹" + commaFormat.format(year3CarValue.toLong())

        // Calculate remaining loan balance at Month 36 (Year 3)
        var balance = netLoan
        for (i in 1..minOf(36, totalMonths)) {
            val intPay = balance * monthlyRate
            val prinPay = emi - intPay
            balance -= prinPay
        }
        if (balance < 0) balance = 0.0

        if (balance > year3CarValue && totalMonths > 36) {
            txtEquityStatusNotice.text = "⚠️ Negative Equity Warning: Loan balance (₹${commaFormat.format(balance.toLong())}) exceeds estimated market car value at Year 3."
            txtEquityStatusNotice.setTextColor(Color.parseColor("#E53935"))
        } else {
            txtEquityStatusNotice.text = "✅ Positive Equity: Healthy loan-to-value ratio maintained."
            txtEquityStatusNotice.setTextColor(Color.parseColor("#00C853"))
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

    private fun highlightDpChip(selectedChip: MaterialButton) {
        val dpChips = listOf(chipCarDp10, chipCarDp15, chipCarDp20, chipCarDp25, chipCarDp30)
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
        val termChips = listOf(chipCarTerm1Y, chipCarTerm2Y, chipCarTerm3Y, chipCarTerm4Y, chipCarTerm5Y, chipCarTerm7Y)
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
        val termChips = listOf(chipCarTerm1Y, chipCarTerm2Y, chipCarTerm3Y, chipCarTerm4Y, chipCarTerm5Y, chipCarTerm7Y)
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

    private fun calculateAndNavigate() {
        val amount = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (amount <= 0) {
            etAmount.error = "Please enter a valid net loan amount"
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter a valid interest rate"
            return
        }

        val monthlyRate = rate / (12 * 100)
        val emi: Double
        val totalInterest: Double
        val totalCost: Double

        if (isFlatRateMode) {
            totalInterest = (amount * rate * (totalMonths / 12.0)) / 100.0
            totalCost = amount + totalInterest
            emi = totalCost / totalMonths
        } else {
            emi = (amount * monthlyRate * (1 + monthlyRate).pow(totalMonths.toDouble())) /
                    ((1 + monthlyRate).pow(totalMonths.toDouble()) - 1)
            totalCost = emi * totalMonths
            totalInterest = totalCost - amount
        }

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, totalMonths)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = amount
        val scheduleCalendar = calendar.clone() as Calendar

        for (i in 1..totalMonths) {
            val interestPayment = if (isFlatRateMode) totalInterest / totalMonths else balance * monthlyRate
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

        val titleMode = if (isFlatRateMode) "Car Loan (Flat Rate)" else getActivityTitle()

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleMode)
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

    private fun resetFields() {
        etVehiclePrice.setText("10,00,000")
        etCarDownPayment.setText("2,00,000")
        etTradeInValue.setText("1,00,000")
        updateCarNetLoan()
        etRate.setText("9.0")
        seekBarTerm.progress = 60
        etProcessingFee.setText("5,000")
        updateTermDisplay(60)

        highlightRateChip(null)
        highlightTermChip(chipCarTerm5Y)
        highlightDpChip(chipCarDp20)
        setRateCalculationMode(false)
        updateDepreciationAnalysisPreview()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewCarLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}