package com.example.calculatoremi

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoImage = findViewById<ImageView>(R.id.logo_image)
        val progressBar = findViewById<LinearProgressIndicator>(R.id.progressBar)
        val progressText = findViewById<TextView>(R.id.progressText)

        // Logo Breathing Pulse Animation
        logoImage?.let { logo ->
            val scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.94f, 1.06f)
            val scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.94f, 1.06f)
            val pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(logo, scaleX, scaleY).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }
            pulseAnimator.start()
        }

        lifecycleScope.launch {
            for (i in 0..100) {
                progressBar.progress = i
                progressText.text = getString(R.string.percentage_format, i)
                delay(22L)
            }
            val intent = Intent(this@LoadingActivity, OnboardingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}