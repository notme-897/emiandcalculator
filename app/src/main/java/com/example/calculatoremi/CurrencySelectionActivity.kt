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
import com.example.calculatoremi.adapter.CurrencyAdapter
import com.example.calculatoremi.model.CurrencyItem
import com.example.calculatoremi.utils.ActivityTransitionUtils
import com.example.calculatoremi.utils.CurrencyManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class CurrencySelectionActivity : AppCompatActivity() {

    private lateinit var etSearchCurrency: EditText
    private lateinit var chipGroupContinents: ChipGroup
    private lateinit var rvCurrencies: RecyclerView
    private lateinit var btnThisIsOkay: MaterialButton

    private var currentSelectedContinent: String? = null
    private var currentQuery = ""
    private lateinit var selectedCurrency: CurrencyItem
    private lateinit var adapter: CurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !com.example.calculatoremi.utils.ThemeManager.isDarkMode(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_currency_selection)

        val mainLayout = findViewById<View>(R.id.mainCurrencySelection)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etSearchCurrency = findViewById(R.id.etSearchCurrency)
        chipGroupContinents = findViewById(R.id.chipGroupContinents)
        rvCurrencies = findViewById(R.id.rvCurrencies)
        btnThisIsOkay = findViewById(R.id.btnThisIsOkay)

        selectedCurrency = CurrencyManager.getSelectedCurrency(this)

        fun filterList(): List<CurrencyItem> {
            return CurrencyManager.ALL_CURRENCIES.filter { item ->
                val matchesContinent = currentSelectedContinent == null || item.continent.equals(currentSelectedContinent, ignoreCase = true)
                val matchesQuery = currentQuery.isEmpty() ||
                        item.countryName.contains(currentQuery, ignoreCase = true) ||
                        item.currencyName.contains(currentQuery, ignoreCase = true) ||
                        item.currencyCode.contains(currentQuery, ignoreCase = true) ||
                        item.symbol.contains(currentQuery, ignoreCase = true)
                matchesContinent && matchesQuery
            }
        }

        adapter = CurrencyAdapter(filterList(), selectedCurrency.countryName, selectedCurrency.currencyCode) { selectedItem ->
            selectedCurrency = selectedItem
            adapter.updateData(filterList(), selectedCurrency.countryName, selectedCurrency.currencyCode)
        }

        rvCurrencies.layoutManager = LinearLayoutManager(this)
        rvCurrencies.adapter = adapter

        etSearchCurrency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString()?.trim() ?: ""
                adapter.updateData(filterList(), selectedCurrency.countryName, selectedCurrency.currencyCode)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroupContinents.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: View.NO_ID
            currentSelectedContinent = when (checkedId) {
                R.id.chipAsia -> "Asia"
                R.id.chipEurope -> "Europe"
                R.id.chipNorthAmerica -> "North America"
                R.id.chipSouthAmerica -> "South America"
                R.id.chipAfrica -> "Africa"
                R.id.chipOceania -> "Oceania"
                else -> null
            }
            adapter.updateData(filterList(), selectedCurrency.countryName, selectedCurrency.currencyCode)
        }

        btnThisIsOkay.setOnClickListener {
            confirmCurrencyAndProceed()
        }
    }

    private fun confirmCurrencyAndProceed() {
        CurrencyManager.setCurrency(this, selectedCurrency)
        Toast.makeText(this, getString(R.string.msg_currency_set), Toast.LENGTH_SHORT).show()

        getSharedPreferences("app_preferences", MODE_PRIVATE)
            .edit()
            .putBoolean("has_completed_onboarding", true)
            .apply()

        startActivity(Intent(this, MainActivity::class.java))
        ActivityTransitionUtils.applySlideInTransition(this)
        finish()
    }
}
