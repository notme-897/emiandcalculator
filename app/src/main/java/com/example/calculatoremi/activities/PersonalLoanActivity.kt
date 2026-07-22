package com.example.calculatoremi.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

open class PersonalLoanActivity : BaseInputActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etRate: EditText
    private lateinit var seekBarTerm: SeekBar
    private lateinit var txtTermValueCombined: TextView
    private lateinit var txtInstallments: TextView
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var txtSelectedDate: TextView
    private lateinit var startDateContainer: View
    
    private lateinit var btnAdd: ImageView
    private lateinit var btnMinus: ImageView

    // Quick Select Chips
    private lateinit var chipAmount1L: MaterialButton
    private lateinit var chipAmount5L: MaterialButton
    private lateinit var chipAmount10L: MaterialButton
    private lateinit var chipAmount25L: MaterialButton

    private lateinit var chipTerm1Y: MaterialButton
    private lateinit var chipTerm3Y: MaterialButton
    private lateinit var chipTerm5Y: MaterialButton
    private lateinit var chipTerm10Y: MaterialButton

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    override fun getLayoutResId(): Int = R.layout.fragment_loan_calculator

    override fun getActivityTitle(): String = "Personal Loan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Find Views
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

        // Amount Chips
        chipAmount1L = findViewById(R.id.chipAmount1L)
        chipAmount5L = findViewById(R.id.chipAmount5L)
        chipAmount10L = findViewById(R.id.chipAmount10L)
        chipAmount25L = findViewById(R.id.chipAmount25L)

        // Term Chips
        chipTerm1Y = findViewById(R.id.chipTerm1Y)
        chipTerm3Y = findViewById(R.id.chipTerm3Y)
        chipTerm5Y = findViewById(R.id.chipTerm5Y)
        chipTerm10Y = findViewById(R.id.chipTerm10Y)

        // Setup Touch Scale Spring Animations on All Chips
        val allChips = listOf(chipAmount1L, chipAmount5L, chipAmount10L, chipAmount25L, chipTerm1Y, chipTerm3Y, chipTerm5Y, chipTerm10Y)
        allChips.forEach { setupChipTouchAnimation(it) }

        // Initialize Date
        txtSelectedDate.text = dateFormatter.format(calendar.time)

        // Setup Date Picker
        startDateContainer.setOnClickListener {
            showDatePicker()
        }

        // Setup SeekBar (1 to 360 months)
        seekBarTerm.max = 360
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualProgress = if (progress < 1) 1 else progress
                updateTermDisplay(actualProgress)
                if (fromUser) {
                    clearTermChipSelection()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Plus/Minus Buttons
        btnAdd.setOnClickListener { 
            if (seekBarTerm.progress < seekBarTerm.max) {
                seekBarTerm.progress++ 
                clearTermChipSelection()
            }
        }
        
        btnMinus.setOnClickListener { 
            if (seekBarTerm.progress > 1) {
                seekBarTerm.progress-- 
                clearTermChipSelection()
            }
        }

        // Quick Amount Chip Listeners
        chipAmount1L.setOnClickListener { 
            setQuickAmount(100000.0)
            highlightAmountChip(chipAmount1L)
        }
        chipAmount5L.setOnClickListener { 
            setQuickAmount(500000.0)
            highlightAmountChip(chipAmount5L)
        }
        chipAmount10L.setOnClickListener { 
            setQuickAmount(1000000.0)
            highlightAmountChip(chipAmount10L)
        }
        chipAmount25L.setOnClickListener { 
            setQuickAmount(2500000.0)
            highlightAmountChip(chipAmount25L)
        }

        // Quick Term Chip Listeners
        chipTerm1Y.setOnClickListener { 
            seekBarTerm.progress = 12
            highlightTermChip(chipTerm1Y)
        }
        chipTerm3Y.setOnClickListener { 
            seekBarTerm.progress = 36
            highlightTermChip(chipTerm3Y)
        }
        chipTerm5Y.setOnClickListener { 
            seekBarTerm.progress = 60
            highlightTermChip(chipTerm5Y)
        }
        chipTerm10Y.setOnClickListener { 
            seekBarTerm.progress = 120
            highlightTermChip(chipTerm10Y)
        }

        // Button Touch Feedback Animations
        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        // Calculate Button Logic -> Opens Result Screen
        btnCalculate.setOnClickListener {
            calculateAndNavigate()
        }

        // Reset Button Logic
        btnReset.setOnClickListener {
            resetFields()
        }

        // Set healthy defaults
        etAmount.setText("500000")
        etRate.setText("10.5")
        seekBarTerm.progress = 60
        updateTermDisplay(60)
        highlightAmountChip(chipAmount5L)
        highlightTermChip(chipTerm5Y)
    }

    private fun setQuickAmount(amount: Double) {
        etAmount.setText(amount.toLong().toString())
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
        val amountChips = listOf(chipAmount1L, chipAmount5L, chipAmount10L, chipAmount25L)
        amountChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
                chip.animate().scaleX(1.06f).scaleY(1.06f).setDuration(80).withEndAction {
                    chip.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(1.8f)).setDuration(120).start()
                }.start()
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun highlightTermChip(selectedChip: MaterialButton) {
        val termChips = listOf(chipTerm1Y, chipTerm3Y, chipTerm5Y, chipTerm10Y)
        termChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                chip.strokeWidth = 0
                chip.animate().scaleX(1.06f).scaleY(1.06f).setDuration(80).withEndAction {
                    chip.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(1.8f)).setDuration(120).start()
                }.start()
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun clearTermChipSelection() {
        val termChips = listOf(chipTerm1Y, chipTerm3Y, chipTerm5Y, chipTerm10Y)
        termChips.forEach { chip ->
            chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
            chip.setTextColor(Color.parseColor("#1E293B"))
            chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
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

    private fun resetFields() {
        etAmount.setText("500000")
        etRate.setText("10.5")
        
        seekBarTerm.progress = 60
        updateTermDisplay(60)
        highlightAmountChip(chipAmount5L)
        highlightTermChip(chipTerm5Y)
        
        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        
        val scrollView = findViewById<View>(R.id.centerBodyLayout).parent as? NestedScrollView
        scrollView?.smoothScrollTo(0, 0)
        
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }

    private fun updateTermDisplay(totalMonths: Int) {
        txtTermValueCombined.text = formatTerm(totalMonths)
        txtInstallments.text = "$totalMonths installments"
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
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalMonths = if (seekBarTerm.progress < 1) 1 else seekBarTerm.progress

        if (amount <= 0) {
            etAmount.error = "Please enter a valid loan amount"
            etAmount.requestFocus()
            return
        }
        if (rate <= 0) {
            etRate.error = "Please enter a valid interest rate"
            etRate.requestFocus()
            return
        }

        val monthlyRate = rate / (12 * 100)
        
        // EMI Formula
        val emi = (amount * monthlyRate * (1 + monthlyRate).pow(totalMonths.toDouble())) / 
                  ((1 + monthlyRate).pow(totalMonths.toDouble()) - 1)
        
        val totalCost = emi * totalMonths
        val totalInterest = totalCost - amount

        // Calculate Payoff Date
        val payoffCalendar = calendar.clone() as Calendar
        payoffCalendar.add(Calendar.MONTH, totalMonths)
        val payoffDate = dateFormatter.format(payoffCalendar.time)

        // Generate Schedule
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
}