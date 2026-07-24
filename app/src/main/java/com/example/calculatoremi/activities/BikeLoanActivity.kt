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

class BikeLoanActivity : BaseInputActivity() {

    private lateinit var btnPriceOnRoad: MaterialButton
    private lateinit var btnPriceExShowroom: MaterialButton
    private lateinit var txtBikePriceLabel: TextView
    private lateinit var etBikePrice: EditText
    private lateinit var chipPrice80k: MaterialButton
    private lateinit var chipPrice120k: MaterialButton
    private lateinit var chipPrice180k: MaterialButton
    private lateinit var chipPrice250k: MaterialButton
    private lateinit var chipPrice400k: MaterialButton

    private lateinit var etDownPayment: EditText
    private lateinit var chipDp0: MaterialButton
    private lateinit var chipDp10: MaterialButton
    private lateinit var chipDp20: MaterialButton
    private lateinit var chipDp30: MaterialButton

    private lateinit var etProcessingFee: EditText
    private lateinit var txtNetLoanDisplay: TextView
    private lateinit var txtTotalOwnershipCostDisplay: TextView

    private lateinit var etRate: EditText
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate11_5: MaterialButton
    private lateinit var chipRate13_5: MaterialButton
    private lateinit var chipRate15_5: MaterialButton

    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipBikeTerm12M: MaterialButton
    private lateinit var chipBikeTerm24M: MaterialButton
    private lateinit var chipBikeTerm36M: MaterialButton
    private lateinit var chipBikeTerm48M: MaterialButton

    private lateinit var txtEvSavingsDisplay: TextView
    private lateinit var txtNetEffectiveEmiDisplay: TextView

    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    private var isOnRoadMode = true

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_bike_loan

    override fun getActivityTitle(): String = "Bike Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        btnPriceOnRoad = findViewById(R.id.btnPriceOnRoad)
        btnPriceExShowroom = findViewById(R.id.btnPriceExShowroom)
        txtBikePriceLabel = findViewById(R.id.txtBikePriceLabel)
        etBikePrice = findViewById(R.id.etBikePrice)
        chipPrice80k = findViewById(R.id.chipPrice80k)
        chipPrice120k = findViewById(R.id.chipPrice120k)
        chipPrice180k = findViewById(R.id.chipPrice180k)
        chipPrice250k = findViewById(R.id.chipPrice250k)
        chipPrice400k = findViewById(R.id.chipPrice400k)

        etDownPayment = findViewById(R.id.etDownPayment)
        chipDp0 = findViewById(R.id.chipDp0)
        chipDp10 = findViewById(R.id.chipDp10)
        chipDp20 = findViewById(R.id.chipDp20)
        chipDp30 = findViewById(R.id.chipDp30)

        etProcessingFee = findViewById(R.id.etProcessingFee)
        txtNetLoanDisplay = findViewById(R.id.txtNetLoanDisplay)
        txtTotalOwnershipCostDisplay = findViewById(R.id.txtTotalOwnershipCostDisplay)

        etRate = findViewById(R.id.editTextNumber2)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate11_5 = findViewById(R.id.chipRate11_5)
        chipRate13_5 = findViewById(R.id.chipRate13_5)
        chipRate15_5 = findViewById(R.id.chipRate15_5)

        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipBikeTerm12M = findViewById(R.id.chipBikeTerm12M)
        chipBikeTerm24M = findViewById(R.id.chipBikeTerm24M)
        chipBikeTerm36M = findViewById(R.id.chipBikeTerm36M)
        chipBikeTerm48M = findViewById(R.id.chipBikeTerm48M)

        txtEvSavingsDisplay = findViewById(R.id.txtEvSavingsDisplay)
        txtNetEffectiveEmiDisplay = findViewById(R.id.txtNetEffectiveEmiDisplay)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Setup touch animations
        val allChips = listOf(
            btnPriceOnRoad, btnPriceExShowroom,
            chipPrice80k, chipPrice120k, chipPrice180k, chipPrice250k, chipPrice400k,
            chipDp0, chipDp10, chipDp20, chipDp30,
            chipRate9_5, chipRate11_5, chipRate13_5, chipRate15_5,
            chipBikeTerm12M, chipBikeTerm24M, chipBikeTerm36M, chipBikeTerm48M
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etBikePrice) { updateBikeLoanSummary() }
        setupCommaFormatting(etDownPayment) { updateBikeLoanSummary() }
        setupCommaFormatting(etProcessingFee) { updateBikeLoanSummary() }

        // Pricing Mode Toggles
        btnPriceOnRoad.setOnClickListener { setPricingMode(true) }
        btnPriceExShowroom.setOnClickListener { setPricingMode(false) }

        // Price Chips
        chipPrice80k.setOnClickListener { setQuickPrice(80000.0); highlightPriceChip(chipPrice80k) }
        chipPrice120k.setOnClickListener { setQuickPrice(120000.0); highlightPriceChip(chipPrice120k) }
        chipPrice180k.setOnClickListener { setQuickPrice(180000.0); highlightPriceChip(chipPrice180k) }
        chipPrice250k.setOnClickListener { setQuickPrice(250000.0); highlightPriceChip(chipPrice250k) }
        chipPrice400k.setOnClickListener { setQuickPrice(400000.0); highlightPriceChip(chipPrice400k) }

        // Down Payment Chips
        chipDp0.setOnClickListener { setQuickDownPaymentPct(0.0); highlightDpChip(chipDp0) }
        chipDp10.setOnClickListener { setQuickDownPaymentPct(10.0); highlightDpChip(chipDp10) }
        chipDp20.setOnClickListener { setQuickDownPaymentPct(20.0); highlightDpChip(chipDp20) }
        chipDp30.setOnClickListener { setQuickDownPaymentPct(30.0); highlightDpChip(chipDp30) }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateBikeLoanSummary()
            }
        })

        // Rate Chips
        chipRate9_5.setOnClickListener { etRate.setText("9.5"); highlightRateChip(chipRate9_5) }
        chipRate11_5.setOnClickListener { etRate.setText("11.5"); highlightRateChip(chipRate11_5) }
        chipRate13_5.setOnClickListener { etRate.setText("13.5"); highlightRateChip(chipRate13_5) }
        chipRate15_5.setOnClickListener { etRate.setText("15.5"); highlightRateChip(chipRate15_5) }

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // SeekBar (12 to 48 Months for Bike Loans)
        seekBarTerm.max = 48
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 12) 12 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
                updateBikeLoanSummary()
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

        // Tenure Chips
        chipBikeTerm12M.setOnClickListener { seekBarTerm.progress = 12; highlightTermChip(chipBikeTerm12M) }
        chipBikeTerm24M.setOnClickListener { seekBarTerm.progress = 24; highlightTermChip(chipBikeTerm24M) }
        chipBikeTerm36M.setOnClickListener { seekBarTerm.progress = 36; highlightTermChip(chipBikeTerm36M) }
        chipBikeTerm48M.setOnClickListener { seekBarTerm.progress = 48; highlightTermChip(chipBikeTerm48M) }

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
        etBikePrice.setText("1,20,000")
        etDownPayment.setText("24,000")
        etProcessingFee.setText("1,500")
        etRate.setText("11.5")
        seekBarTerm.progress = 36
        updateTermDisplay(36)

        highlightPriceChip(chipPrice120k)
        highlightDpChip(chipDp20)
        highlightRateChip(chipRate11_5)
        highlightTermChip(chipBikeTerm36M)
        setPricingMode(true)
        updateBikeLoanSummary()
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
                updateBikeLoanSummary()
            }
        })
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun setPricingMode(isOnRoad: Boolean) {
        isOnRoadMode = isOnRoad
        val selectedBlue = ContextCompat.getColor(this, R.color.custom_blue)
        val selectedText = ContextCompat.getColor(this, android.R.color.white)
        val unselectedBg = Color.parseColor("#F8FAFC")
        val unselectedText = Color.parseColor("#1E293B")
        val strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
        val strokeWidth = (1 * resources.displayMetrics.density).toInt()

        if (isOnRoad) {
            btnPriceOnRoad.setBackgroundColor(selectedBlue)
            btnPriceOnRoad.setTextColor(selectedText)
            btnPriceOnRoad.strokeWidth = 0

            btnPriceExShowroom.setBackgroundColor(unselectedBg)
            btnPriceExShowroom.setTextColor(unselectedText)
            btnPriceExShowroom.strokeColor = strokeColor
            btnPriceExShowroom.strokeWidth = strokeWidth

            txtBikePriceLabel.text = "On-Road Bike Price (₹)"
        } else {
            btnPriceExShowroom.setBackgroundColor(selectedBlue)
            btnPriceExShowroom.setTextColor(selectedText)
            btnPriceExShowroom.strokeWidth = 0

            btnPriceOnRoad.setBackgroundColor(unselectedBg)
            btnPriceOnRoad.setTextColor(unselectedText)
            btnPriceOnRoad.strokeColor = strokeColor
            btnPriceOnRoad.strokeWidth = strokeWidth

            txtBikePriceLabel.text = "Ex-Showroom Price (₹)"
        }

        updateBikeLoanSummary()
    }

    private fun setQuickPrice(price: Double) {
        etBikePrice.setText(commaFormat.format(price))
        val dp = price * 0.20
        etDownPayment.setText(commaFormat.format(dp))
        updateBikeLoanSummary()
    }

    private fun setQuickDownPaymentPct(pct: Double) {
        val price = getRawValue(etBikePrice)
        val dp = price * (pct / 100.0)
        etDownPayment.setText(commaFormat.format(dp))
        updateBikeLoanSummary()
    }

    private fun updateBikeLoanSummary() {
        val rawPrice = getRawValue(etBikePrice)
        val downPayment = getRawValue(etDownPayment)
        val processingFee = getRawValue(etProcessingFee)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        val effectiveOnRoadPrice = if (isOnRoadMode) rawPrice else rawPrice * 1.15
        val netLoan = if (effectiveOnRoadPrice > downPayment) effectiveOnRoadPrice - downPayment else 0.0

        txtNetLoanDisplay.text = "₹" + commaFormat.format(netLoan.toLong())

        if (netLoan > 0 && rate > 0 && months > 0) {
            val monthlyRate = rate / (12 * 100)
            val emi = (netLoan * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) /
                    ((1 + monthlyRate).pow(months.toDouble()) - 1)
            val totalRepayment = emi * months
            val totalOwnershipCost = downPayment + totalRepayment + processingFee

            txtTotalOwnershipCostDisplay.text = "₹" + commaFormat.format(totalOwnershipCost.toLong())

            // EV Fuel Savings Offset Calculation (Average ₹2,200/mo petrol savings)
            val evFuelSavings = 2200.0
            val netEffectiveEmi = if (emi > evFuelSavings) emi - evFuelSavings else 0.0

            txtEvSavingsDisplay.text = "₹" + commaFormat.format(evFuelSavings.toLong()) + " / mo"
            txtNetEffectiveEmiDisplay.text = "₹" + commaFormat.format(netEffectiveEmi.toLong()) + " / mo"
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

    private fun highlightPriceChip(selectedChip: MaterialButton) {
        val chips = listOf(chipPrice80k, chipPrice120k, chipPrice180k, chipPrice250k, chipPrice400k)
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

    private fun highlightDpChip(selectedChip: MaterialButton) {
        val chips = listOf(chipDp0, chipDp10, chipDp20, chipDp30)
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
        val rateChips = listOf(chipRate9_5, chipRate11_5, chipRate13_5, chipRate15_5)
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
            9.5 -> highlightRateChip(chipRate9_5)
            11.5 -> highlightRateChip(chipRate11_5)
            13.5 -> highlightRateChip(chipRate13_5)
            15.5 -> highlightRateChip(chipRate15_5)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipBikeTerm12M, chipBikeTerm24M, chipBikeTerm36M, chipBikeTerm48M)
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
        val termChips = listOf(chipBikeTerm12M, chipBikeTerm24M, chipBikeTerm36M, chipBikeTerm48M)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(months: Int) {
        val years = months / 12
        txtTermValueCombined.text = "$months mos ($years yrs)"
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
        val rawPrice = getRawValue(etBikePrice)
        val downPayment = getRawValue(etDownPayment)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val months = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (rawPrice <= 0) {
            etBikePrice.error = "Please enter valid bike price"
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter valid interest rate"
            return
        }

        val effectiveOnRoadPrice = if (isOnRoadMode) rawPrice else rawPrice * 1.15
        val netLoan = if (effectiveOnRoadPrice > downPayment) effectiveOnRoadPrice - downPayment else 0.0

        if (netLoan <= 0) {
            etDownPayment.error = "Down payment cannot exceed vehicle price"
            return
        }

        val monthlyRate = rate / (12 * 100)
        val emi = (netLoan * monthlyRate * (1 + monthlyRate).pow(months.toDouble())) /
                ((1 + monthlyRate).pow(months.toDouble()) - 1)
        val totalCost = emi * months
        val totalInterest = totalCost - netLoan

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, months)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = netLoan
        val scheduleCalendar = calendar.clone() as Calendar

        for (i in 1..months) {
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

        val yearsInt = months / 12
        val monthsInt = months % 12
        val priceModeLabel = if (isOnRoadMode) "On-Road" else "Ex-Showroom"
        val titleText = "Bike Loan ($priceModeLabel)"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", netLoan)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
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
        etBikePrice.setText("1,20,000")
        etDownPayment.setText("24,000")
        etProcessingFee.setText("1,500")
        etRate.setText("11.5")
        seekBarTerm.progress = 36
        updateTermDisplay(36)

        highlightPriceChip(chipPrice120k)
        highlightDpChip(chipDp20)
        highlightRateChip(chipRate11_5)
        highlightTermChip(chipBikeTerm36M)
        setPricingMode(true)
        updateBikeLoanSummary()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewBikeLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}