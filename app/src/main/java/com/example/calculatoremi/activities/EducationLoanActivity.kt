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

class EducationLoanActivity : BaseInputActivity() {

    private lateinit var etAmount: EditText
    private lateinit var chipAmount5L: MaterialButton
    private lateinit var chipAmount10L: MaterialButton
    private lateinit var chipAmount20L: MaterialButton
    private lateinit var chipAmount35L: MaterialButton
    private lateinit var chipAmount50L: MaterialButton

    private lateinit var etRate: EditText
    private lateinit var chipRate8_5: MaterialButton
    private lateinit var chipRate9_5: MaterialButton
    private lateinit var chipRate10_5: MaterialButton
    private lateinit var chipRate11_5: MaterialButton

    private lateinit var chipCourse1Y: MaterialButton
    private lateinit var chipCourse2Y: MaterialButton
    private lateinit var chipCourse3Y: MaterialButton
    private lateinit var chipCourse4Y: MaterialButton

    private lateinit var chipGrace0: MaterialButton
    private lateinit var chipGrace6: MaterialButton
    private lateinit var chipGrace12: MaterialButton

    private lateinit var btnChoiceFullMoratorium: MaterialButton
    private lateinit var btnChoiceSimpleInterest: MaterialButton
    private lateinit var btnChoiceFullEmi: MaterialButton

    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView

    private lateinit var chipEduTerm5Y: MaterialButton
    private lateinit var chipEduTerm7Y: MaterialButton
    private lateinit var chipEduTerm10Y: MaterialButton
    private lateinit var chipEduTerm12Y: MaterialButton
    private lateinit var chipEduTerm15Y: MaterialButton

    private lateinit var txtMoratoriumInterestAccrued: TextView
    private lateinit var txtEffectivePrincipalDisplay: TextView
    private lateinit var txtSimpleInterestSavingsNotice: TextView

    private lateinit var chipTax10: MaterialButton
    private lateinit var chipTax20: MaterialButton
    private lateinit var chipTax30: MaterialButton
    private lateinit var txtTaxSavingsDisplay: TextView

    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View

    enum class StudyPaymentChoice {
        FULL_MORATORIUM,
        SIMPLE_INTEREST,
        IMMEDIATE_FULL_EMI
    }

    private var selectedCourseYears = 2
    private var selectedGraceMonths = 6
    private var selectedChoice = StudyPaymentChoice.FULL_MORATORIUM
    private var selectedTaxBracket = 20.0

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)
    private val commaFormat = DecimalFormat("#,##,###")
    private var isFormatting = false

    override fun getLayoutResId(): Int = R.layout.activity_education_loan

    override fun getActivityTitle(): String = "Education Loan Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find Views
        etAmount = findViewById(R.id.editTextNumber)
        chipAmount5L = findViewById(R.id.chipAmount5L)
        chipAmount10L = findViewById(R.id.chipAmount10L)
        chipAmount20L = findViewById(R.id.chipAmount20L)
        chipAmount35L = findViewById(R.id.chipAmount35L)
        chipAmount50L = findViewById(R.id.chipAmount50L)

        etRate = findViewById(R.id.editTextNumber2)
        chipRate8_5 = findViewById(R.id.chipRate8_5)
        chipRate9_5 = findViewById(R.id.chipRate9_5)
        chipRate10_5 = findViewById(R.id.chipRate10_5)
        chipRate11_5 = findViewById(R.id.chipRate11_5)

        chipCourse1Y = findViewById(R.id.chipCourse1Y)
        chipCourse2Y = findViewById(R.id.chipCourse2Y)
        chipCourse3Y = findViewById(R.id.chipCourse3Y)
        chipCourse4Y = findViewById(R.id.chipCourse4Y)

        chipGrace0 = findViewById(R.id.chipGrace0)
        chipGrace6 = findViewById(R.id.chipGrace6)
        chipGrace12 = findViewById(R.id.chipGrace12)

        btnChoiceFullMoratorium = findViewById(R.id.btnChoiceFullMoratorium)
        btnChoiceSimpleInterest = findViewById(R.id.btnChoiceSimpleInterest)
        btnChoiceFullEmi = findViewById(R.id.btnChoiceFullEmi)

        seekBarTerm = findViewById(R.id.loanSeekBar)
        txtTermValueCombined = findViewById(R.id.txtTermValueCombined)
        txtInstallments = findViewById(R.id.txtInstallments)

        chipEduTerm5Y = findViewById(R.id.chipEduTerm5Y)
        chipEduTerm7Y = findViewById(R.id.chipEduTerm7Y)
        chipEduTerm10Y = findViewById(R.id.chipEduTerm10Y)
        chipEduTerm12Y = findViewById(R.id.chipEduTerm12Y)
        chipEduTerm15Y = findViewById(R.id.chipEduTerm15Y)

        txtMoratoriumInterestAccrued = findViewById(R.id.txtMoratoriumInterestAccrued)
        txtEffectivePrincipalDisplay = findViewById(R.id.txtEffectivePrincipalDisplay)
        txtSimpleInterestSavingsNotice = findViewById(R.id.txtSimpleInterestSavingsNotice)

        chipTax10 = findViewById(R.id.chipTax10)
        chipTax20 = findViewById(R.id.chipTax20)
        chipTax30 = findViewById(R.id.chipTax30)
        txtTaxSavingsDisplay = findViewById(R.id.txtTaxSavingsDisplay)

        btnAdd = findViewById(R.id.btnAdd)
        btnMinus = findViewById(R.id.btnMinus)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Setup touch animation on chips
        val allChips = listOf(
            chipAmount5L, chipAmount10L, chipAmount20L, chipAmount35L, chipAmount50L,
            chipRate8_5, chipRate9_5, chipRate10_5, chipRate11_5,
            chipCourse1Y, chipCourse2Y, chipCourse3Y, chipCourse4Y,
            chipGrace0, chipGrace6, chipGrace12,
            btnChoiceFullMoratorium, btnChoiceSimpleInterest, btnChoiceFullEmi,
            chipEduTerm5Y, chipEduTerm7Y, chipEduTerm10Y, chipEduTerm12Y, chipEduTerm15Y,
            chipTax10, chipTax20, chipTax30
        )
        allChips.forEach { setupChipTouchAnimation(it) }

        // Live Comma Formatting
        setupCommaFormatting(etAmount) { updateMoratoriumAnalysis() }

        // Amount Chips
        chipAmount5L.setOnClickListener { setQuickAmount(500000.0); highlightAmountChip(chipAmount5L) }
        chipAmount10L.setOnClickListener { setQuickAmount(1000000.0); highlightAmountChip(chipAmount10L) }
        chipAmount20L.setOnClickListener { setQuickAmount(2000000.0); highlightAmountChip(chipAmount20L) }
        chipAmount35L.setOnClickListener { setQuickAmount(3500000.0); highlightAmountChip(chipAmount35L) }
        chipAmount50L.setOnClickListener { setQuickAmount(5000000.0); highlightAmountChip(chipAmount50L) }

        // Rate Manual change watcher
        etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRateChipHighlights(s.toString().toDoubleOrNull())
                updateMoratoriumAnalysis()
            }
        })

        // Rate Chips
        chipRate8_5.setOnClickListener { etRate.setText("8.5"); highlightRateChip(chipRate8_5) }
        chipRate9_5.setOnClickListener { etRate.setText("9.5"); highlightRateChip(chipRate9_5) }
        chipRate10_5.setOnClickListener { etRate.setText("10.5"); highlightRateChip(chipRate10_5) }
        chipRate11_5.setOnClickListener { etRate.setText("11.5"); highlightRateChip(chipRate11_5) }

        // Course Duration Chips
        chipCourse1Y.setOnClickListener { selectedCourseYears = 1; highlightCourseChip(chipCourse1Y); updateMoratoriumAnalysis() }
        chipCourse2Y.setOnClickListener { selectedCourseYears = 2; highlightCourseChip(chipCourse2Y); updateMoratoriumAnalysis() }
        chipCourse3Y.setOnClickListener { selectedCourseYears = 3; highlightCourseChip(chipCourse3Y); updateMoratoriumAnalysis() }
        chipCourse4Y.setOnClickListener { selectedCourseYears = 4; highlightCourseChip(chipCourse4Y); updateMoratoriumAnalysis() }

        // Grace Period Chips
        chipGrace0.setOnClickListener { selectedGraceMonths = 0; highlightGraceChip(chipGrace0); updateMoratoriumAnalysis() }
        chipGrace6.setOnClickListener { selectedGraceMonths = 6; highlightGraceChip(chipGrace6); updateMoratoriumAnalysis() }
        chipGrace12.setOnClickListener { selectedGraceMonths = 12; highlightGraceChip(chipGrace12); updateMoratoriumAnalysis() }

        // Study Payment Choice Toggles
        btnChoiceFullMoratorium.setOnClickListener { setPaymentChoice(StudyPaymentChoice.FULL_MORATORIUM) }
        btnChoiceSimpleInterest.setOnClickListener { setPaymentChoice(StudyPaymentChoice.SIMPLE_INTEREST) }
        btnChoiceFullEmi.setOnClickListener { setPaymentChoice(StudyPaymentChoice.IMMEDIATE_FULL_EMI) }

        // Tax Bracket Chips
        chipTax10.setOnClickListener { selectedTaxBracket = 10.0; highlightTaxChip(chipTax10); updateMoratoriumAnalysis() }
        chipTax20.setOnClickListener { selectedTaxBracket = 20.0; highlightTaxChip(chipTax20); updateMoratoriumAnalysis() }
        chipTax30.setOnClickListener { selectedTaxBracket = 30.0; highlightTaxChip(chipTax30); updateMoratoriumAnalysis() }

        txtSelectedDate.text = dateFormatter.format(calendar.time)
        setupButtonAnimation(startDateContainer)
        startDateContainer.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDatePicker()
        }

        // SeekBar (12 to 180 months / 1 to 15 years for Education Loans)
        seekBarTerm.max = 180
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 12) 12 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) clearTermChipSelection()
                updateMoratoriumAnalysis()
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

        // Term Chips
        chipEduTerm5Y.setOnClickListener { seekBarTerm.progress = 60; highlightTermChip(chipEduTerm5Y) }
        chipEduTerm7Y.setOnClickListener { seekBarTerm.progress = 84; highlightTermChip(chipEduTerm7Y) }
        chipEduTerm10Y.setOnClickListener { seekBarTerm.progress = 120; highlightTermChip(chipEduTerm10Y) }
        chipEduTerm12Y.setOnClickListener { seekBarTerm.progress = 144; highlightTermChip(chipEduTerm12Y) }
        chipEduTerm15Y.setOnClickListener { seekBarTerm.progress = 180; highlightTermChip(chipEduTerm15Y) }

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
        etAmount.setText("20,00,000")
        etRate.setText("9.5")
        seekBarTerm.progress = 84
        updateTermDisplay(84)

        highlightAmountChip(chipAmount20L)
        highlightRateChip(chipRate9_5)
        highlightCourseChip(chipCourse2Y)
        highlightGraceChip(chipGrace6)
        highlightTermChip(chipEduTerm7Y)
        highlightTaxChip(chipTax20)
        setPaymentChoice(StudyPaymentChoice.FULL_MORATORIUM)
        updateMoratoriumAnalysis()
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
                updateMoratoriumAnalysis()
            }
        })
    }

    private fun setQuickAmount(amount: Double) {
        etAmount.setText(commaFormat.format(amount))
        updateMoratoriumAnalysis()
    }

    private fun getRawValue(editText: EditText): Double {
        val raw = editText.text.toString().replace(",", "")
        return raw.toDoubleOrNull() ?: 0.0
    }

    private fun setPaymentChoice(choice: StudyPaymentChoice) {
        selectedChoice = choice
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

        applyStyle(btnChoiceFullMoratorium, choice == StudyPaymentChoice.FULL_MORATORIUM)
        applyStyle(btnChoiceSimpleInterest, choice == StudyPaymentChoice.SIMPLE_INTEREST)
        applyStyle(btnChoiceFullEmi, choice == StudyPaymentChoice.IMMEDIATE_FULL_EMI)

        updateMoratoriumAnalysis()
    }

    private fun updateMoratoriumAnalysis() {
        val loanAmount = getRawValue(etAmount)
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val activeMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (loanAmount <= 0 || rate <= 0) return

        val moratoriumMonths = if (selectedChoice == StudyPaymentChoice.IMMEDIATE_FULL_EMI) {
            0
        } else {
            (selectedCourseYears * 12) + selectedGraceMonths
        }

        val moratoriumYears = moratoriumMonths / 12.0
        val moratoriumInterestAccrued = loanAmount * (rate / 100.0) * moratoriumYears

        val effectivePrincipal = when (selectedChoice) {
            StudyPaymentChoice.FULL_MORATORIUM -> loanAmount + moratoriumInterestAccrued
            StudyPaymentChoice.SIMPLE_INTEREST -> loanAmount
            StudyPaymentChoice.IMMEDIATE_FULL_EMI -> loanAmount
        }

        txtMoratoriumInterestAccrued.text = "₹" + commaFormat.format(moratoriumInterestAccrued.toLong())
        txtEffectivePrincipalDisplay.text = "₹" + commaFormat.format(effectivePrincipal.toLong())

        // Calculate active EMI
        val monthlyRate = rate / (12 * 100)
        val activeEmi = (effectivePrincipal * monthlyRate * (1 + monthlyRate).pow(activeMonths.toDouble())) /
                ((1 + monthlyRate).pow(activeMonths.toDouble()) - 1)
        val activeTotalInterest = (activeEmi * activeMonths) - effectivePrincipal

        val totalInterestPayable = if (selectedChoice == StudyPaymentChoice.FULL_MORATORIUM) {
            moratoriumInterestAccrued + activeTotalInterest
        } else {
            moratoriumInterestAccrued + activeTotalInterest
        }

        // Section 80E Tax Savings (100% interest deductible)
        val taxSavings = totalInterestPayable * (selectedTaxBracket / 100.0)
        txtTaxSavingsDisplay.text = "₹" + commaFormat.format(taxSavings.toLong())

        // Capitalization Tip Notice
        if (selectedChoice == StudyPaymentChoice.FULL_MORATORIUM && moratoriumMonths > 0) {
            val capitalizedEmiDifference = activeEmi - ((loanAmount * monthlyRate * (1 + monthlyRate).pow(activeMonths.toDouble())) /
                    ((1 + monthlyRate).pow(activeMonths.toDouble()) - 1))
            txtSimpleInterestSavingsNotice.text = String.format(
                Locale.US,
                "💡 Tip: Paying simple interest during college saves ~₹%s in capitalized interest!",
                commaFormat.format(moratoriumInterestAccrued.toLong())
            )
            txtSimpleInterestSavingsNotice.visibility = View.VISIBLE
        } else if (selectedChoice == StudyPaymentChoice.SIMPLE_INTEREST) {
            txtSimpleInterestSavingsNotice.text = "✅ Great choice! Paying simple interest prevents interest capitalization."
            txtSimpleInterestSavingsNotice.visibility = View.VISIBLE
        } else {
            txtSimpleInterestSavingsNotice.visibility = View.GONE
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
        val amountChips = listOf(chipAmount5L, chipAmount10L, chipAmount20L, chipAmount35L, chipAmount50L)
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
        val rateChips = listOf(chipRate8_5, chipRate9_5, chipRate10_5, chipRate11_5)
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
            11.5 -> highlightRateChip(chipRate11_5)
            else -> highlightRateChip(null)
        }
    }

    private fun highlightCourseChip(selectedChip: MaterialButton) {
        val chips = listOf(chipCourse1Y, chipCourse2Y, chipCourse3Y, chipCourse4Y)
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

    private fun highlightGraceChip(selectedChip: MaterialButton) {
        val chips = listOf(chipGrace0, chipGrace6, chipGrace12)
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

    private fun highlightTaxChip(selectedChip: MaterialButton) {
        val chips = listOf(chipTax10, chipTax20, chipTax30)
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

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipEduTerm5Y, chipEduTerm7Y, chipEduTerm10Y, chipEduTerm12Y, chipEduTerm15Y)
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
        val termChips = listOf(chipEduTerm5Y, chipEduTerm7Y, chipEduTerm10Y, chipEduTerm12Y, chipEduTerm15Y)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updateTermDisplay(totalMonths: Int) {
        txtTermValueCombined.text = formatTerm(totalMonths)
        txtInstallments.text = "$totalMonths EMIs"
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
        val activeMonths = if (seekBarTerm.progress < 12) 12 else seekBarTerm.progress

        if (amount <= 0) {
            etAmount.error = "Please enter a valid loan amount"
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter a valid interest rate"
            return
        }

        val moratoriumMonths = if (selectedChoice == StudyPaymentChoice.IMMEDIATE_FULL_EMI) {
            0
        } else {
            (selectedCourseYears * 12) + selectedGraceMonths
        }

        val moratoriumYears = moratoriumMonths / 12.0
        val moratoriumInterestAccrued = amount * (rate / 100.0) * moratoriumYears

        val effectivePrincipal = when (selectedChoice) {
            StudyPaymentChoice.FULL_MORATORIUM -> amount + moratoriumInterestAccrued
            StudyPaymentChoice.SIMPLE_INTEREST -> amount
            StudyPaymentChoice.IMMEDIATE_FULL_EMI -> amount
        }

        val monthlyRate = rate / (12 * 100)
        val activeEmi = (effectivePrincipal * monthlyRate * (1 + monthlyRate).pow(activeMonths.toDouble())) /
                ((1 + monthlyRate).pow(activeMonths.toDouble()) - 1)
        val activeTotalInterest = (activeEmi * activeMonths) - effectivePrincipal

        val totalCost = (activeEmi * activeMonths) + if (selectedChoice == StudyPaymentChoice.SIMPLE_INTEREST) moratoriumInterestAccrued else 0.0
        val totalInterest = totalCost - amount

        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, moratoriumMonths + activeMonths)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        val schedule = ArrayList<PaymentScheduleItem>()
        var balance = effectivePrincipal
        val scheduleCalendar = calendar.clone() as Calendar

        // Moratorium Months (if any)
        if (moratoriumMonths > 0) {
            val monthlySimpleInterest = amount * (rate / (12 * 100))
            for (m in 1..moratoriumMonths) {
                scheduleCalendar.add(Calendar.MONTH, 1)
                val isSimpleInterestPaid = (selectedChoice == StudyPaymentChoice.SIMPLE_INTEREST)
                schedule.add(
                    PaymentScheduleItem(
                        emiNo = m,
                        date = dateFormatter.format(scheduleCalendar.time),
                        emi = if (isSimpleInterestPaid) monthlySimpleInterest else 0.0,
                        principal = 0.0,
                        interest = monthlySimpleInterest,
                        balance = if (selectedChoice == StudyPaymentChoice.FULL_MORATORIUM) amount + (monthlySimpleInterest * m) else amount
                    )
                )
            }
        }

        // Active Repayment Phase
        for (i in 1..activeMonths) {
            val interestPayment = balance * monthlyRate
            val principalPayment = activeEmi - interestPayment
            balance -= principalPayment

            scheduleCalendar.add(Calendar.MONTH, 1)

            schedule.add(
                PaymentScheduleItem(
                    emiNo = moratoriumMonths + i,
                    date = dateFormatter.format(scheduleCalendar.time),
                    emi = activeEmi,
                    principal = principalPayment,
                    interest = interestPayment,
                    balance = if (balance < 0) 0.0 else balance
                )
            )
        }

        val yearsInt = activeMonths / 12
        val monthsInt = activeMonths % 12
        val choiceLabel = when (selectedChoice) {
            StudyPaymentChoice.FULL_MORATORIUM -> "Full Moratorium"
            StudyPaymentChoice.SIMPLE_INTEREST -> "Simple Interest"
            StudyPaymentChoice.IMMEDIATE_FULL_EMI -> "Full EMI"
        }
        val titleText = "Education Loan ($choiceLabel)"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", titleText)
            putExtra("LOAN_AMOUNT", amount)
            putExtra("INTEREST_RATE", rate.toFloat())
            putExtra("LOAN_TERM_YEARS", yearsInt)
            putExtra("LOAN_TERM_MONTHS", monthsInt)
            putExtra("START_DATE", txtSelectedDate.text.toString())
            putExtra("EMI", activeEmi)
            putExtra("TOTAL_INTEREST", totalInterest)
            putExtra("TOTAL_COST", totalCost)
            putExtra("PAYOFF_DATE", payoffDate)
            putExtra("SCHEDULE", schedule)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etAmount.setText("20,00,000")
        etRate.setText("9.5")
        seekBarTerm.progress = 84
        updateTermDisplay(84)

        selectedCourseYears = 2
        selectedGraceMonths = 6
        selectedTaxBracket = 20.0

        highlightAmountChip(chipAmount20L)
        highlightRateChip(chipRate9_5)
        highlightCourseChip(chipCourse2Y)
        highlightGraceChip(chipGrace6)
        highlightTermChip(chipEduTerm7Y)
        highlightTaxChip(chipTax20)
        setPaymentChoice(StudyPaymentChoice.FULL_MORATORIUM)
        updateMoratoriumAnalysis()

        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        findViewById<NestedScrollView>(R.id.scrollViewEducationLoan).smoothScrollTo(0, 0)

        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }
}