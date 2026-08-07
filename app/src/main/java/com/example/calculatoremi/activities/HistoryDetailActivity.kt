package com.example.calculatoremi.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.calculatoremi.R
import com.example.calculatoremi.model.CalculationHistoryItem
import com.example.calculatoremi.utils.HistoryManager
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class HistoryDetailActivity : BaseInputActivity() {

    private var historyItem: CalculationHistoryItem? = null
    private val decimalFormat = DecimalFormat("#,##,###.##")

    override fun getLayoutResId(): Int = R.layout.activity_history_detail

    override fun getActivityTitle(): String = "Saved Calculation Breakdown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)

        historyItem = getSerializableExtraCompat<CalculationHistoryItem>("EXTRA_HISTORY_ITEM")

        val item = historyItem
        if (item == null) {
            Toast.makeText(this, "Calculation details not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Header and Category
        val txtCategoryBadge = findViewById<TextView>(R.id.txtCategoryBadge)
        val txtCalculatedDate = findViewById<TextView>(R.id.txtCalculatedDate)
        val txtTitleHeader = findViewById<TextView>(R.id.txtTitleHeader)
        val txtDetailsSummaryHeader = findViewById<TextView>(R.id.txtDetailsSummaryHeader)

        txtCategoryBadge.text = item.category.uppercase()
        txtCalculatedDate.text = item.formattedDate
        txtTitleHeader.text = item.title
        txtDetailsSummaryHeader.text = item.detailsSummary

        // Category Badge Tinting
        when (item.category.lowercase()) {
            "loans", "loan" -> {
                txtCategoryBadge.setBackgroundColor(Color.parseColor("#DBEAFE"))
                txtCategoryBadge.setTextColor(Color.parseColor("#1D4ED8"))
            }
            "investments", "investment" -> {
                txtCategoryBadge.setBackgroundColor(Color.parseColor("#DCFCE7"))
                txtCategoryBadge.setTextColor(Color.parseColor("#15803D"))
            }
            "salary", "income" -> {
                txtCategoryBadge.setBackgroundColor(Color.parseColor("#F3E8FF"))
                txtCategoryBadge.setTextColor(Color.parseColor("#7E22CE"))
            }
            else -> {
                txtCategoryBadge.setBackgroundColor(Color.parseColor("#FFEDD5"))
                txtCategoryBadge.setTextColor(Color.parseColor("#C2410C"))
            }
        }

        // Primary Values
        val txtPrimaryValue = findViewById<TextView>(R.id.txtPrimaryValue)
        val txtPrincipalAmount = findViewById<TextView>(R.id.txtPrincipalAmount)
        val txtTotalInterest = findViewById<TextView>(R.id.txtTotalInterest)
        val txtTotalCost = findViewById<TextView>(R.id.txtTotalCost)

        animateNumberCounter(txtPrimaryValue, item.emi)
        animateNumberCounter(txtPrincipalAmount, item.loanAmount)
        animateNumberCounter(txtTotalInterest, item.totalInterest)
        animateNumberCounter(txtTotalCost, item.totalCost)

        // Saved Parameters Breakdown
        val txtInterestRate = findViewById<TextView>(R.id.txtInterestRate)
        val txtLoanTerm = findViewById<TextView>(R.id.txtLoanTerm)
        val txtStartDate = findViewById<TextView>(R.id.txtStartDate)
        val txtPayoffDate = findViewById<TextView>(R.id.txtPayoffDate)

        txtInterestRate.text = "${item.interestRate} %"
        val termText = when {
            item.years > 0 && item.months > 0 -> "${item.years} Yrs ${item.months} Mos"
            item.years > 0 -> "${item.years} Years (${item.years * 12} Mos)"
            else -> "${item.months} Months"
        }
        txtLoanTerm.text = termText
        txtStartDate.text = if (item.startDate.isBlank()) "Today" else item.startDate
        txtPayoffDate.text = if (item.payoffDate.isBlank()) "Standard Term" else item.payoffDate

        // Action Buttons
        val btnShareDetail = findViewById<MaterialButton>(R.id.btnShareDetail)
        val btnDeleteRecord = findViewById<MaterialButton>(R.id.btnDeleteRecord)

        setupButtonAnimation(btnShareDetail)
        setupButtonAnimation(btnDeleteRecord)

        btnShareDetail.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            shareCalculation(item)
        }

        btnDeleteRecord.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            confirmDeleteRecord(item)
        }
    }

    private fun shareCalculation(item: CalculationHistoryItem) {
        val shareText = """
            Calculation Result - ${item.title}
            ---------------------
            Result: ${item.primaryResultValue}
            Calculated: ${item.formattedDate}
            Parameters: ${item.detailsSummary}
            ---------------------
            Calculated via Finance Hub
        """.trimIndent()

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Calculation Result")
        startActivity(shareIntent)
    }

    private fun confirmDeleteRecord(item: CalculationHistoryItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Saved Record")
            .setMessage("Are you sure you want to delete '${item.title}' from calculation history?")
            .setPositiveButton("Delete") { dialog, _ ->
                HistoryManager.deleteHistoryItem(this, item.id)
                Toast.makeText(this, getString(R.string.msg_record_deleted), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun animateNumberCounter(textView: TextView, targetValue: Double) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = 600
        animator.interpolator = DecelerateInterpolator()
        val symbol = com.example.calculatoremi.utils.CurrencyManager.getCurrencySymbol(this)
        animator.addUpdateListener { anim ->
            val value = (anim.animatedValue as Float).toDouble()
            textView.text = "$symbol " + decimalFormat.format(value)
        }
        animator.start()
    }

    private fun setupButtonAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .setDuration(160)
                        .start()
                }
            }
            false
        }
    }

    private inline fun <reified T : java.io.Serializable> getSerializableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? T
        }
    }

    override fun finish() {
        super.finish()
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideOutTransition(this)
    }
}
