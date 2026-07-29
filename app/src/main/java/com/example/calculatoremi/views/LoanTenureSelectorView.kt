package com.example.calculatoremi.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider

/**
 * Premium reusable Material 3 Loan Tenure selector custom view.
 * Stores tenure internally in months (12 to 360).
 * Displays human-friendly "X Years Y Months" and "X Installments".
 * Features 6 quick-selection chips (5, 10, 15, 20, 25, 30 Years).
 */
class LoanTenureSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val MIN_MONTHS = 12
    private val MAX_MONTHS = 360

    private val txtTenureValue: TextView
    private val txtInstallmentsCount: TextView
    private val sliderTenure: Slider
    private val btnMinus: ImageView
    private val btnAdd: ImageView

    private val chip5Y: MaterialButton
    private val chip10Y: MaterialButton
    private val chip15Y: MaterialButton
    private val chip20Y: MaterialButton
    private val chip25Y: MaterialButton
    private val chip30Y: MaterialButton

    private val quickChips: List<Pair<Int, MaterialButton>>

    private var targetTenureMonths: Int = 60
    private var onTenureChangedListener: ((totalMonths: Int) -> Unit)? = null

    /**
     * Gets or sets the tenure in total months (clamped between 12 and 360).
     */
    var tenureMonths: Int
        get() = targetTenureMonths
        set(value) {
            val clamped = value.coerceIn(MIN_MONTHS, MAX_MONTHS)
            targetTenureMonths = clamped
            sliderTenure.value = clamped.toFloat()
            updateDisplay(clamped)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loan_tenure_selector, this, true)

        txtTenureValue = findViewById(R.id.txtTenureValue)
        txtInstallmentsCount = findViewById(R.id.txtInstallmentsCount)
        sliderTenure = findViewById(R.id.sliderTenure)
        btnMinus = findViewById(R.id.btnMinus)
        btnAdd = findViewById(R.id.btnAdd)

        chip5Y = findViewById(R.id.chip5Y)
        chip10Y = findViewById(R.id.chip10Y)
        chip15Y = findViewById(R.id.chip15Y)
        chip20Y = findViewById(R.id.chip20Y)
        chip25Y = findViewById(R.id.chip25Y)
        chip30Y = findViewById(R.id.chip30Y)

        quickChips = listOf(
            60 to chip5Y,
            120 to chip10Y,
            180 to chip15Y,
            240 to chip20Y,
            300 to chip25Y,
            360 to chip30Y
        )

        quickChips.forEach { (_, chip) -> setupChipTouchAnimation(chip) }

        sliderTenure.valueFrom = MIN_MONTHS.toFloat()
        sliderTenure.valueTo = MAX_MONTHS.toFloat()
        sliderTenure.stepSize = 1f
        sliderTenure.value = 60f // Default 5 years (60 months)
        targetTenureMonths = 60

        sliderTenure.addOnChangeListener { _, value, fromUser ->
            val months = value.toInt()
            if (fromUser) {
                targetTenureMonths = months
            }
            updateDisplay(months)
            onTenureChangedListener?.invoke(months)
        }

        btnMinus.setOnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            v.animate().scaleX(0.85f).scaleY(0.85f).setDuration(60).withEndAction {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
            }.start()

            if (targetTenureMonths > MIN_MONTHS) {
                setTenureValue(targetTenureMonths - 1)
            }
        }

        btnAdd.setOnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            v.animate().scaleX(0.85f).scaleY(0.85f).setDuration(60).withEndAction {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
            }.start()

            if (targetTenureMonths < MAX_MONTHS) {
                setTenureValue(targetTenureMonths + 1)
            }
        }

        quickChips.forEach { (months, chip) ->
            chip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                setTenureValue(months)
            }
        }

        updateDisplay(60)
    }

    /**
     * Registers a callback invoked whenever the tenure changes.
     */
    fun setOnTenureChangedListener(listener: (totalMonths: Int) -> Unit) {
        this.onTenureChangedListener = listener
    }

    private fun updateDisplay(months: Int) {
        txtTenureValue.text = formatTenureString(months)
        txtInstallmentsCount.text = "$months Installments"
        updateChipHighlights(months)
    }

    private fun setTenureValue(targetMonths: Int) {
        val clampedTarget = targetMonths.coerceIn(MIN_MONTHS, MAX_MONTHS)
        targetTenureMonths = clampedTarget
        sliderTenure.value = clampedTarget.toFloat()
        updateDisplay(clampedTarget)
        onTenureChangedListener?.invoke(clampedTarget)
    }

    private fun updateChipHighlights(selectedMonths: Int) {
        quickChips.forEach { (m, chip) ->
            if (m == selectedMonths) {
                chip.setBackgroundColor(ContextCompat.getColor(context, R.color.custom_blue))
                chip.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                chip.strokeWidth = 0
            } else {
                chip.setBackgroundColor(Color.parseColor("#F8FAFC"))
                chip.setTextColor(Color.parseColor("#1E293B"))
                chip.strokeColor = ColorStateList.valueOf(Color.parseColor("#CBD5E1"))
                chip.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
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

    companion object {
        fun formatTenureString(totalMonths: Int): String {
            val safeMonths = totalMonths.coerceIn(1, 1200)
            val years = safeMonths / 12
            val months = safeMonths % 12

            if (years == 0) {
                return if (months == 1) "1 Month" else "$months Months"
            }

            val yearStr = if (years == 1) "1 Year" else "$years Years"

            // Don't append "0 Months" — just show "X Years" when there's no remainder
            if (months == 0) return yearStr

            val monthStr = if (months == 1) "1 Month" else "$months Months"
            return "$yearStr $monthStr"
        }
    }
}

