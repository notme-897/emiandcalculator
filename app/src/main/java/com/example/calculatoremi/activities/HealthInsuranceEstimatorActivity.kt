package com.example.calculatoremi.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class HealthInsuranceEstimatorActivity : BaseInputActivity() {

    private lateinit var chipSelfOnly: MaterialButton
    private lateinit var chipCouple: MaterialButton
    private lateinit var chipFamily: MaterialButton

    private lateinit var etOldestAge: EditText
    private lateinit var chipPreExistingToggle: MaterialButton

    private lateinit var chipTier1: MaterialButton
    private lateinit var chipTier2: MaterialButton

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnReset: MaterialButton

    private var planType = 3 // 1: Self, 2: Couple, 3: Family Floater
    private var hasPreExisting = false
    private var isTier1 = true

    private val commaFormat = DecimalFormat("#,##,###")

    override fun getLayoutResId(): Int = R.layout.activity_health_insurance_estimator

    override fun getActivityTitle(): String = "Health Cover Estimator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chipSelfOnly = findViewById(R.id.chipSelfOnly)
        chipCouple = findViewById(R.id.chipCouple)
        chipFamily = findViewById(R.id.chipFamily)

        etOldestAge = findViewById(R.id.etOldestAge)
        chipPreExistingToggle = findViewById(R.id.chipPreExistingToggle)

        chipTier1 = findViewById(R.id.chipTier1)
        chipTier2 = findViewById(R.id.chipTier2)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        btnCalculate.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btnCalculate.setTextColor(Color.WHITE)

        btnReset.setTextColor(primaryColor)
        btnReset.strokeColor = ColorStateList.valueOf(primaryColor)

        chipSelfOnly.setOnClickListener { planType = 1; highlightPlanTypeChips() }
        chipCouple.setOnClickListener { planType = 2; highlightPlanTypeChips() }
        chipFamily.setOnClickListener { planType = 3; highlightPlanTypeChips() }

        chipTier1.setOnClickListener { isTier1 = true; highlightTierChips() }
        chipTier2.setOnClickListener { isTier1 = false; highlightTierChips() }

        chipPreExistingToggle.setOnClickListener {
            hasPreExisting = !hasPreExisting
            updatePreExistingButton()
        }

        setupButtonAnimation(btnCalculate)
        setupButtonAnimation(btnReset)

        btnCalculate.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            calculateHealthCoverAndNavigate()
        }

        btnReset.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            resetFields()
        }

        // Defaults
        etOldestAge.setText("35")
        highlightPlanTypeChips()
        highlightTierChips()
        updatePreExistingButton()
    }

    private fun highlightPlanTypeChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val chips = listOf(chipSelfOnly, chipCouple, chipFamily)
        val selectedIndex = planType - 1

        chips.forEachIndexed { index, chip ->
            if (index == selectedIndex) {
                chip.backgroundTintList = ColorStateList.valueOf(primaryColor)
                chip.setTextColor(Color.WHITE)
                chip.strokeWidth = 0
            } else {
                chip.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }
    }

    private fun highlightTierChips() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (isTier1) {
            chipTier1.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipTier1.setTextColor(Color.WHITE)
            chipTier1.strokeWidth = 0

            chipTier2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipTier2.setTextColor(Color.parseColor("#1E293B"))
            chipTier2.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipTier2.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        } else {
            chipTier2.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipTier2.setTextColor(Color.WHITE)
            chipTier2.strokeWidth = 0

            chipTier1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipTier1.setTextColor(Color.parseColor("#1E293B"))
            chipTier1.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipTier1.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun updatePreExistingButton() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        if (hasPreExisting) {
            chipPreExistingToggle.text = "Pre-Existing (Add Super Top-up)"
            chipPreExistingToggle.backgroundTintList = ColorStateList.valueOf(primaryColor)
            chipPreExistingToggle.setTextColor(Color.WHITE)
            chipPreExistingToggle.strokeWidth = 0
        } else {
            chipPreExistingToggle.text = "No Pre-Existing Condition"
            chipPreExistingToggle.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
            chipPreExistingToggle.setTextColor(Color.parseColor("#1E293B"))
            chipPreExistingToggle.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
            chipPreExistingToggle.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
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
                        .setInterpolator(OvershootInterpolator(2.2f))
                        .setDuration(160)
                        .start()
                }
            }
            false
        }
    }

    private fun calculateHealthCoverAndNavigate() {
        val age = etOldestAge.text.toString().toIntOrNull() ?: 35

        // Base recommended health sum insured (INR):
        // Tier 1 Metro base = 10 Lakhs (Individual), 15 Lakhs (Couple), 25 Lakhs (Family)
        var baseCover = when (planType) {
            1 -> 1000000.0
            2 -> 1500000.0
            else -> 2500000.0
        }

        // Adjust for age > 45
        if (age > 45) {
            baseCover *= 1.35
        } else if (age > 55) {
            baseCover *= 1.70
        }

        // Adjust for Tier 1 vs Tier 2
        if (!isTier1) {
            baseCover *= 0.75
        }

        // Adjust for pre-existing conditions (recommend super top-up)
        val superTopUpAddon = if (hasPreExisting) baseCover * 0.50 else 0.0
        val totalRecommendedCover = baseCover + superTopUpAddon

        // Estimated annual premium calculation
        val estPremiumAnnual = (baseCover * 0.012) + (superTopUpAddon * 0.004)

        val scheduleList = ArrayList<PaymentScheduleItem>()
        scheduleList.add(PaymentScheduleItem(1, "Base Health Cover", baseCover, baseCover / 12.0, 0.0, baseCover))
        scheduleList.add(PaymentScheduleItem(2, "Super Top-up Buffer", superTopUpAddon, superTopUpAddon / 12.0, 0.0, superTopUpAddon))
        scheduleList.add(PaymentScheduleItem(3, "Total Recommended Cover", totalRecommendedCover, totalRecommendedCover / 12.0, 0.0, totalRecommendedCover))
        scheduleList.add(PaymentScheduleItem(4, "Est. Annual Health Premium", estPremiumAnnual, estPremiumAnnual / 12.0, 0.0, estPremiumAnnual))

        val planTypeName = when (planType) { 1 -> "Individual"; 2 -> "Couple"; else -> "Family Floater" }
        val statusText = "$planTypeName (${if (isTier1) "Tier 1 Metro" else "Tier 2/3 City"})"

        val intent = Intent(this, PersonalLoanResultActivity::class.java).apply {
            putExtra("TITLE", "Health Insurance Cover Result")
            putExtra("LOAN_AMOUNT", totalRecommendedCover) // Total Cover
            putExtra("INTEREST_RATE", (estPremiumAnnual / totalRecommendedCover * 100).toFloat()) // % Premium Ratio
            putExtra("LOAN_TERM_YEARS", 1)
            putExtra("LOAN_TERM_MONTHS", 12)
            putExtra("START_DATE", statusText)
            putExtra("EMI", totalRecommendedCover / 12.0)
            putExtra("TOTAL_INTEREST", estPremiumAnnual)
            putExtra("TOTAL_COST", totalRecommendedCover)
            putExtra("PAYOFF_DATE", "Recommended Cover: ₹" + commaFormat.format(totalRecommendedCover.toInt()))
            putExtra("SCHEDULE", scheduleList)
        }
        startActivity(intent)
    }

    private fun resetFields() {
        etOldestAge.setText("35")
        planType = 3
        hasPreExisting = false
        isTier1 = true
        highlightPlanTypeChips()
        highlightTierChips()
        updatePreExistingButton()
    }
}
