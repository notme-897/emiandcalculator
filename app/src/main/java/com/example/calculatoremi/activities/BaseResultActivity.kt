package com.example.calculatoremi.activities

import android.content.Intent
import android.os.Bundle
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
import java.text.DecimalFormat
import kotlin.math.abs

abstract class BaseResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(getResultLayoutResId())

        setupResultToolbar()
    }

    protected open fun setupResultToolbar() {
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        val headerView = findViewById<View>(R.id.resultHeader)
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

        val txtTitle = findViewById<TextView>(R.id.txtResultTitle)
        val btnBack = findViewById<ImageView>(R.id.btnBackResult)
        val btnBackContainer = findViewById<View>(R.id.btnBackResultContainer)
        val btnShare = findViewById<ImageView>(R.id.btnShare)

        txtTitle?.text = getResultTitle()

        val backClick = View.OnClickListener { finish() }
        btnBack?.setOnClickListener(backClick)
        btnBackContainer?.let {
            setupTouchScaleAnimation(it)
            it.setOnClickListener(backClick)
        }
        btnShare?.let {
            setupTouchScaleAnimation(it)
            it.setOnClickListener { shareResult() }
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

    abstract fun getResultLayoutResId(): Int
    abstract fun getResultTitle(): String

    protected open fun shareResult() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getResultTitle())
            putExtra(Intent.EXTRA_TEXT, getShareText())
        }
        startActivity(Intent.createChooser(shareIntent, "Share Calculation"))
    }

    abstract fun getShareText(): String

    protected fun formatCurrency(amount: Double): String {
        val absAmount = abs(amount)
        val formatter = if (absAmount % 1.0 < 0.01 || absAmount % 1.0 > 0.99) {
            DecimalFormat("#,##,##0")
        } else {
            DecimalFormat("#,##,##0.00")
        }
        val prefix = if (amount < 0) "-₹" else "₹"
        return prefix + formatter.format(absAmount)
    }
}
