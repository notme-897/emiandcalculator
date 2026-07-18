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
        enableEdgeToEdge()
        setContentView(R.layout.fragment_loan_calculator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calculatorHeader)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        txtTitle.text = getLoanTitle()

        btnBack.setOnClickListener {
            finish()
        }
    }

    abstract fun getLoanTitle(): String
}