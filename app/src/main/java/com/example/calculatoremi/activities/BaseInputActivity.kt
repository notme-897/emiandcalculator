package com.example.calculatoremi.activities

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoremi.R
import java.util.Locale

abstract class BaseInputActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(com.example.calculatoremi.utils.LanguageManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)
        enableEdgeToEdge()
        setContentView(getLayoutResId())

        setupToolbar()
    }

    protected open fun setupToolbar() {
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !com.example.calculatoremi.utils.ThemeManager.isDarkMode(this)
        val headerView = findViewById<View>(R.id.calculatorHeader)
        if (headerView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val density = resources.displayMetrics.density
                val sidePadding = (16 * density).toInt()
                val topPadding = systemBars.top + (10 * density).toInt()
                val bottomPadding = (10 * density).toInt()
                v.setPadding(systemBars.left + sidePadding, topPadding, systemBars.right + sidePadding, bottomPadding)
                insets
            }
        }

        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnBackContainer = findViewById<View>(R.id.btnBackContainer)

        txtTitle?.text = getActivityTitle()
        
        val backClick = View.OnClickListener { finish() }
        btnBack?.setOnClickListener(backClick)
        btnBackContainer?.let {
            setupTouchScaleAnimation(it)
            it.setOnClickListener(backClick)
        }
    }

    protected fun setupTouchScaleAnimation(view: View?) {
        view?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(160).start()
                }
            }
            false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is android.widget.EditText) {
                val outRect = android.graphics.Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
        super.finish()
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideOutTransition(this)
    }

    abstract fun getLayoutResId(): Int
    abstract fun getActivityTitle(): String

    protected fun formatCurrency(amount: Double): String {
        return com.example.calculatoremi.utils.CurrencyManager.formatAmount(this, amount)
    }

    protected fun formatTenureMonths(totalMonths: Int): String {
        val safeMonths = if (totalMonths < 1) 1 else totalMonths
        val years = safeMonths / 12
        val months = safeMonths % 12

        val yearPart = when (years) {
            0 -> ""
            1 -> "1 Year"
            else -> "$years Years"
        }

        val monthPart = when (months) {
            0 -> ""
            1 -> "1 Month"
            else -> "$months Months"
        }

        return when {
            yearPart.isNotEmpty() && monthPart.isNotEmpty() -> "$yearPart $monthPart"
            yearPart.isNotEmpty() -> yearPart
            monthPart.isNotEmpty() -> monthPart
            else -> "1 Month"
        }
    }

    protected fun formatTerm(months: Int): String {
        return formatTenureMonths(months)
    }
}

