package com.example.calculatoremi.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class PersonalLoanActivity : BaseLoanActivity() {

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
    private lateinit var btnRemoveLoan: ImageView

    private var calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.US)

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
        btnRemoveLoan = findViewById(R.id.removebtnloan)
        
        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        startDateContainer = findViewById(R.id.startDateContainer)

        // Initialize Date
        txtSelectedDate.text = dateFormatter.format(calendar.time)

        // Setup Date Picker
        startDateContainer.setOnClickListener {
            showDatePicker()
        }

        // Setup SeekBar
        seekBarTerm.max = 360 // 30 years
        seekBarTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTermDisplay(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Plus/Minus Buttons
        btnAdd.setOnClickListener { if (seekBarTerm.progress < seekBarTerm.max) seekBarTerm.progress++ }
        
        btnMinus.setOnClickListener { decreaseTerm() }
        btnRemoveLoan.setOnClickListener { decreaseTerm() }

        // Calculate Button Logic
        btnCalculate.setOnClickListener {
            calculateAndNavigate()
        }

        // Reset Button Logic
        btnReset.setOnClickListener {
            resetFields()
        }

        // Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Initial update
        updateTermDisplay(0)
    }

    private fun resetFields() {
        // Clear input fields
        etAmount.setText("")
        etRate.setText("")
        
        // Reset SeekBar and labels
        seekBarTerm.progress = 0
        updateTermDisplay(0)
        
        // Reset Date to current day
        calendar = Calendar.getInstance()
        txtSelectedDate.text = dateFormatter.format(calendar.time)
        
        // Scroll to top
        val scrollView = findViewById<View>(R.id.centerBodyLayout).parent as? NestedScrollView
        scrollView?.smoothScrollTo(0, 0)
        
        Toast.makeText(this, "Fields reset successfully", Toast.LENGTH_SHORT).show()
    }

    private fun decreaseTerm() {
        if (seekBarTerm.progress > 0) {
            seekBarTerm.progress--
        }
    }

    private fun updateTermDisplay(totalMonths: Int) {
        val years = totalMonths / 12
        val months = totalMonths % 12
        
        txtTermValueCombined.text = "$months months $years years"
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
        val totalMonths = seekBarTerm.progress

        if (amount <= 0) {
            Toast.makeText(this, "Please enter a valid loan amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (rate <= 0) {
            Toast.makeText(this, "Please enter a valid interest rate", Toast.LENGTH_SHORT).show()
            return
        }
        if (totalMonths <= 0) {
            Toast.makeText(this, "Please select a loan term", Toast.LENGTH_SHORT).show()
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

    override fun getLoanTitle(): String = "Personal Loan"
}