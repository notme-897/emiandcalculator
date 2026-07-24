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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(getLayoutResId())

        setupToolbar()
    }

    protected open fun setupToolbar() {
        val headerView = findViewById<View>(R.id.calculatorHeader)
        if (headerView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val density = resources.displayMetrics.density
                val topPadding = systemBars.top + (8 * density).toInt()
                val bottomPadding = (8 * density).toInt()
                v.setPadding(systemBars.left, topPadding, systemBars.right, bottomPadding)
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

    protected fun setupTouchScaleAnimation(view: View) {
        view.setOnTouchListener { v, event ->
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    abstract fun getLayoutResId(): Int
    abstract fun getActivityTitle(): String

    protected fun formatCurrency(amount: Double): String {
        return String.format(Locale.US, "%,.2f $", amount)
    }

    protected fun formatTerm(months: Int): String {
        val years = months / 12
        val remMonths = months % 12
        return when {
            years > 0 && remMonths > 0 -> "$years yrs $remMonths mos"
            years > 0 -> "$years yrs"
            else -> "$remMonths mos"
        }
    }
}

