package com.example.calculatoremi

import  android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val progressBar = findViewById<LinearProgressIndicator>(R.id.progressBar)
        val progressText = findViewById<TextView>(R.id.progressText)

        lifecycleScope.launch {
            for (i in 0..100){
                progressBar.progress =i
                progressText.text=(getString(R.string.percentage_format,i))
                delay(30L)
            }
            val intent = Intent(this@LoadingActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
       }


    }
}