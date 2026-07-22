package com.example.calculatoremi.activities

import android.os.Bundle
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
        val headerView = findViewById<android.view.View>(R.id.calculatorHeader)
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
        val btnBackContainer = findViewById<android.view.View>(R.id.btnBackContainer)

        txtTitle?.text = getActivityTitle()
        
        val backClick = android.view.View.OnClickListener { finish() }
        btnBack?.setOnClickListener(backClick)
        btnBackContainer?.setOnClickListener(backClick)
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
