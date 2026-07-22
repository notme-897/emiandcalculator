package com.example.calculatoremi.activities

import android.content.Intent
import android.os.Bundle
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
        val headerView = findViewById<android.view.View>(R.id.resultHeader)
        if (headerView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
                insets
            }
        }

        val txtTitle = findViewById<TextView>(R.id.txtResultTitle)
        val btnBack = findViewById<ImageView>(R.id.btnBackResult)
        val btnShare = findViewById<ImageView>(R.id.btnShare)

        txtTitle?.text = getResultTitle()
        btnBack?.setOnClickListener { finish() }
        btnShare?.setOnClickListener { shareResult() }
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
        return String.format(Locale.US, "%,.2f $", amount)
    }
}
