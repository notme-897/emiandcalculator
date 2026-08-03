package com.example.calculatoremi.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoremi.R

abstract class BaseLoanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(this)
        enableEdgeToEdge()
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContentView(R.layout.fragment_loan_calculator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calculatorHeader)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val density = resources.displayMetrics.density
            val sidePadding = (16 * density).toInt()
            val topPadding = systemBars.top + (10 * density).toInt()
            val bottomPadding = (10 * density).toInt()
            v.setPadding(systemBars.left + sidePadding, topPadding, systemBars.right + sidePadding, bottomPadding)
            insets
        }

        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        txtTitle.text = getLoanTitle()

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideOutTransition(this)
    }

    abstract fun getLoanTitle(): String
}