package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class CashDenominationActivity : BaseInputActivity() {

    private lateinit var etCount500: EditText
    private lateinit var etCount200: EditText
    private lateinit var etCount100: EditText
    private lateinit var etCount50: EditText
    private lateinit var etCount20: EditText
    private lateinit var etCount10: EditText

    private lateinit var txtSubTotal500: TextView
    private lateinit var txtSubTotal200: TextView
    private lateinit var txtSubTotal100: TextView
    private lateinit var txtSubTotal50: TextView
    private lateinit var txtSubTotal20: TextView
    private lateinit var txtSubTotal10: TextView

    private lateinit var txtGrandCashTotal: TextView
    private lateinit var txtTotalNotesCount: TextView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private val commaFormat = DecimalFormat("#,##,###")

    override fun getLayoutResId(): Int = R.layout.activity_cash_denomination

    override fun getActivityTitle(): String = "Cash Denomination Counter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etCount500 = findViewById(R.id.etCount500)
        etCount200 = findViewById(R.id.etCount200)
        etCount100 = findViewById(R.id.etCount100)
        etCount50 = findViewById(R.id.etCount50)
        etCount20 = findViewById(R.id.etCount20)
        etCount10 = findViewById(R.id.etCount10)

        txtSubTotal500 = findViewById(R.id.txtSubTotal500)
        txtSubTotal200 = findViewById(R.id.txtSubTotal200)
        txtSubTotal100 = findViewById(R.id.txtSubTotal100)
        txtSubTotal50 = findViewById(R.id.txtSubTotal50)
        txtSubTotal20 = findViewById(R.id.txtSubTotal20)
        txtSubTotal10 = findViewById(R.id.txtSubTotal10)

        txtGrandCashTotal = findViewById(R.id.txtGrandCashTotal)
        txtTotalNotesCount = findViewById(R.id.txtTotalNotesCount)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)
        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateLiveSummary() }
        }

        listOf(etCount500, etCount200, etCount100, etCount50, etCount20, etCount10).forEach {
            it.addTextChangedListener(watcher)
        }

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        updateLiveSummary()
    }

    private fun updateLiveSummary() {
        val n500 = etCount500.text.toString().toLongOrNull() ?: 0L
        val n200 = etCount200.text.toString().toLongOrNull() ?: 0L
        val n100 = etCount100.text.toString().toLongOrNull() ?: 0L
        val n50 = etCount50.text.toString().toLongOrNull() ?: 0L
        val n20 = etCount20.text.toString().toLongOrNull() ?: 0L
        val n10 = etCount10.text.toString().toLongOrNull() ?: 0L

        val sum500 = n500 * 500
        val sum200 = n200 * 200
        val sum100 = n100 * 100
        val sum50 = n50 * 50
        val sum20 = n20 * 20
        val sum10 = n10 * 10

        val totalGrand = sum500 + sum200 + sum100 + sum50 + sum20 + sum10
        val totalNotes = n500 + n200 + n100 + n50 + n20 + n10

        val symbol = com.example.calculatoremi.utils.CurrencyManager.getCurrencySymbol(this)
        txtSubTotal500.text = "= $symbol" + commaFormat.format(sum500)
        txtSubTotal200.text = "= $symbol" + commaFormat.format(sum200)
        txtSubTotal100.text = "= $symbol" + commaFormat.format(sum100)
        txtSubTotal50.text = "= $symbol" + commaFormat.format(sum50)
        txtSubTotal20.text = "= $symbol" + commaFormat.format(sum20)
        txtSubTotal10.text = "= $symbol" + commaFormat.format(sum10)

        txtGrandCashTotal.text = com.example.calculatoremi.utils.CurrencyManager.formatAmountLong(this, totalGrand)
        txtTotalNotesCount.text = "Total Notes / Coins: $totalNotes"
    }

    private fun calculateAndNavigate() {
        val n500 = etCount500.text.toString().toLongOrNull() ?: 0L
        val n200 = etCount200.text.toString().toLongOrNull() ?: 0L
        val n100 = etCount100.text.toString().toLongOrNull() ?: 0L
        val n50 = etCount50.text.toString().toLongOrNull() ?: 0L
        val n20 = etCount20.text.toString().toLongOrNull() ?: 0L
        val n10 = etCount10.text.toString().toLongOrNull() ?: 0L

        val totalGrand = (n500 * 500 + n200 * 200 + n100 * 100 + n50 * 50 + n20 * 20 + n10 * 10).toDouble()
        val totalNotes = n500 + n200 + n100 + n50 + n20 + n10

        if (totalGrand <= 0) {
            Toast.makeText(this, "Please enter at least one note count", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Cash Counter Result")
            putExtra("LOAN_AMOUNT", totalGrand)
            putExtra("INTEREST_RATE", 0.0f)
            putExtra("LOAN_TERM_YEARS", 0)
            putExtra("LOAN_TERM_MONTHS", totalNotes.toInt())
            putExtra("START_DATE", "$totalNotes Total Notes")
            putExtra("EMI", totalGrand)
            putExtra("TOTAL_INTEREST", 0.0)
            putExtra("TOTAL_COST", totalGrand)
            putExtra("PAYOFF_DATE", "Grand Total Cash: ₹" + commaFormat.format(totalGrand))
        }
        startActivity(intent)
    }

    private fun resetFields() {
        listOf(etCount500, etCount200, etCount100, etCount50, etCount20, etCount10).forEach { it.setText("") }
        updateLiveSummary()
        Toast.makeText(this, getString(R.string.msg_fields_reset), Toast.LENGTH_SHORT).show()
    }
}
