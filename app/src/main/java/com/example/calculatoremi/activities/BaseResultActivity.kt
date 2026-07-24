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
import java.util.Locale

abstract class BaseResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(getResultLayoutResId())

        setupResultToolbar()
    }

    protected open fun setupResultToolbar() {
        val headerView = findViewById<View>(R.id.resultHeader)
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
        val formatter = java.text.DecimalFormat("#,##,###.##")
        return "₹" + formatter.format(amount)
    }
}


