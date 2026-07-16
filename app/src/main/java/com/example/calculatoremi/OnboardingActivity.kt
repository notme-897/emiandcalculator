package com.example.calculatoremi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.calculatoremi.adapter.OnboardingAdapter
import com.example.calculatoremi.model.OnboardingItem
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: MaterialButton
    private lateinit var tvSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewPager)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
        tvSkip = findViewById(R.id.tvSkip)

        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Welcome to EMI Calculator",
                "Calculate your monthly EMI, total interest and total repayment instantly."
            ),
            OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Smart Finance Tools",
                "Compare different loan options and plan your finances like a pro."
            ),
            OnboardingItem(
                R.drawable.ic_launcher_foreground,
                "Track Your History",
                "Save your calculations and access them anytime, anywhere."
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        btnNext.setOnClickListener {
            if (viewPager.currentItem + 1 < onboardingItems.size) {
                viewPager.currentItem += 1
            } else {
                navigateToMain()
            }
        }

        tvSkip.setOnClickListener {
            navigateToMain()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingItems.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
                updateDots(position)
            }
        })
    }

    private fun updateDots(position: Int) {
        val dot1 = findViewById<View>(R.id.dot1)
        val dot2 = findViewById<View>(R.id.dot2)
        val dot3 = findViewById<View>(R.id.dot3)

        dot1.setBackgroundResource(if (position == 0) R.drawable.dot_selected else R.drawable.dot_unselected)
        dot2.setBackgroundResource(if (position == 1) R.drawable.dot_selected else R.drawable.dot_unselected)
        dot3.setBackgroundResource(if (position == 2) R.drawable.dot_selected else R.drawable.dot_unselected)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}