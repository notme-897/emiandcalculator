package com.example.calculatoremi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.adapter.LanguageAdapter
import com.example.calculatoremi.model.LanguageItem
import com.example.calculatoremi.utils.ActivityTransitionUtils
import com.example.calculatoremi.utils.LanguageManager
import com.google.android.material.button.MaterialButton

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var etSearchLanguage: EditText
    private lateinit var rvLanguages: RecyclerView
    private lateinit var btnContinueLanguage: MaterialButton

    private var currentQuery = ""
    private lateinit var selectedLanguage: LanguageItem
    private lateinit var adapter: LanguageAdapter
    private var isFromSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !com.example.calculatoremi.utils.ThemeManager.isDarkMode(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_language_selection)

        isFromSettings = intent.getBooleanExtra("IS_FROM_SETTINGS", false)

        val mainLayout = findViewById<View>(R.id.mainLanguageSelection)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etSearchLanguage = findViewById(R.id.etSearchLanguage)
        rvLanguages = findViewById(R.id.rvLanguages)
        btnContinueLanguage = findViewById(R.id.btnContinueLanguage)

        selectedLanguage = LanguageManager.getSelectedLanguage(this)

        fun filterList(): List<LanguageItem> {
            return LanguageManager.ALL_LANGUAGES.filter { item ->
                currentQuery.isEmpty() ||
                        item.englishName.contains(currentQuery, ignoreCase = true) ||
                        item.nativeName.contains(currentQuery, ignoreCase = true) ||
                        item.languageCode.contains(currentQuery, ignoreCase = true)
            }
        }

        adapter = LanguageAdapter(filterList(), selectedLanguage.languageCode) { selectedItem ->
            selectedLanguage = selectedItem
            adapter.updateData(filterList(), selectedLanguage.languageCode)
        }

        rvLanguages.layoutManager = LinearLayoutManager(this)
        rvLanguages.adapter = adapter

        etSearchLanguage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString()?.trim() ?: ""
                adapter.updateData(filterList(), selectedLanguage.languageCode)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnContinueLanguage.setOnClickListener {
            confirmLanguageAndProceed()
        }
    }

    private fun confirmLanguageAndProceed() {
        LanguageManager.setLanguage(this, selectedLanguage)
        Toast.makeText(this, getString(R.string.msg_language_set), Toast.LENGTH_SHORT).show()

        if (isFromSettings) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            ActivityTransitionUtils.applySlideInTransition(this)
            finish()
        } else {
            startActivity(Intent(this, CurrencySelectionActivity::class.java))
            ActivityTransitionUtils.applySlideInTransition(this)
            finish()
        }
    }
}
