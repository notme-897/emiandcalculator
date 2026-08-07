package com.example.calculatoremi.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.CurrencyAdapter
import com.example.calculatoremi.model.CurrencyItem
import com.example.calculatoremi.utils.CurrencyManager
import com.example.calculatoremi.utils.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var btnThemeLight: MaterialButton? = null
    private var btnThemeDark: MaterialButton? = null
    private var btnThemeSystem: MaterialButton? = null
    private var txtSelectedCurrencySummary: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerView = view.findViewById<View>(R.id.settingsHeader)
        headerView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val topPadding = systemBars.top + (12 * resources.displayMetrics.density).toInt()
                v.setPadding(v.paddingLeft, topPadding, v.paddingRight, v.paddingBottom)
                insets
            }
        }

        val cardPreferences = view.findViewById<MaterialCardView>(R.id.cardPreferences)
        val cardAppActions = view.findViewById<MaterialCardView>(R.id.cardAppActions)
        val switchHaptics = view.findViewById<SwitchMaterial>(R.id.switchHaptics)
        val rowShareApp = view.findViewById<View>(R.id.rowShareApp)
        val rowAboutApp = view.findViewById<View>(R.id.rowAboutApp)
        val rowCurrencySelection = view.findViewById<View>(R.id.rowCurrencySelection)
        txtSelectedCurrencySummary = view.findViewById(R.id.txtSelectedCurrencySummary)

        val rowLanguageSelection = view.findViewById<View>(R.id.rowLanguageSelection)
        val txtSelectedLanguageSummary = view.findViewById<TextView>(R.id.txtSelectedLanguageSummary)

        btnThemeLight = view.findViewById(R.id.btnThemeLight)
        btnThemeDark = view.findViewById(R.id.btnThemeDark)
        btnThemeSystem = view.findViewById(R.id.btnThemeSystem)

        // Highlight saved theme button & preferences summaries
        updateThemeButtonStates()
        updateCurrencySummaryText()

        val currentLang = com.example.calculatoremi.utils.LanguageManager.getSelectedLanguage(requireContext())
        txtSelectedLanguageSummary?.text = "${currentLang.flagEmoji} ${currentLang.nativeName} (${currentLang.englishName})"

        setupTouchScaleAnimation(btnThemeLight)
        setupTouchScaleAnimation(btnThemeDark)
        setupTouchScaleAnimation(btnThemeSystem)

        btnThemeLight?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ThemeManager.applyTheme(requireContext(), ThemeManager.THEME_LIGHT)
            updateThemeButtonStates()
        }

        btnThemeDark?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ThemeManager.applyTheme(requireContext(), ThemeManager.THEME_DARK)
            updateThemeButtonStates()
        }

        btnThemeSystem?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ThemeManager.applyTheme(requireContext(), ThemeManager.THEME_SYSTEM)
            updateThemeButtonStates()
        }

        rowCurrencySelection?.let { setupTouchScaleAnimation(it) }
        rowCurrencySelection?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showCurrencyPickerDialog()
        }

        rowLanguageSelection?.let { setupTouchScaleAnimation(it) }
        rowLanguageSelection?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val intent = Intent(requireContext(), com.example.calculatoremi.LanguageSelectionActivity::class.java).apply {
                putExtra("IS_FROM_SETTINGS", true)
            }
            startActivity(intent)
            com.example.calculatoremi.utils.ActivityTransitionUtils.applySlideInTransition(requireActivity())
        }

        // Card Entrance Animations
        val cards = listOfNotNull(cardPreferences, cardAppActions)
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 35f
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 100).toLong())
                .setDuration(380)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }

        switchHaptics?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                Toast.makeText(requireContext(), "Haptic Feedback Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Haptic Feedback Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        rowShareApp?.let { setupTouchScaleAnimation(it) }
        rowShareApp?.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "EMI Calculator App")
                putExtra(Intent.EXTRA_TEXT, "Check out this awesome EMI Calculator app!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share App Via"))
        }

        rowAboutApp?.let { setupTouchScaleAnimation(it) }
        rowAboutApp?.setOnClickListener {
            Toast.makeText(requireContext(), "EMI Calculator v1.0.0 (Ultimate Edition)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrencySummaryText() {
        val context = context ?: return
        val currentCurrency = CurrencyManager.getSelectedCurrency(context)
        txtSelectedCurrencySummary?.text = "${currentCurrency.flagEmoji} ${currentCurrency.currencyCode} - ${currentCurrency.currencyName} (${currentCurrency.symbol})"
    }

    private fun showCurrencyPickerDialog() {
        val context = context ?: return
        val dialog = BottomSheetDialog(context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_currency_picker, null)
        dialog.setContentView(dialogView)

        val etSearch = dialogView.findViewById<EditText>(R.id.etSearchCurrency)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnCloseDialog)
        val chipGroup = dialogView.findViewById<ChipGroup>(R.id.chipGroupContinents)
        val rvCurrencies = dialogView.findViewById<RecyclerView>(R.id.rvCurrencies)

        var currentSelectedContinent: String? = null
        var currentQuery = ""

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

        val initialSelected = CurrencyManager.getSelectedCurrency(context)
        val adapter = CurrencyAdapter(filterList(), initialSelected.countryName, initialSelected.currencyCode) { selectedItem ->
            CurrencyManager.setCurrency(context, selectedItem)
            updateCurrencySummaryText()
            Toast.makeText(context, "Currency set to ${selectedItem.countryName} (${selectedItem.symbol})", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        rvCurrencies.layoutManager = LinearLayoutManager(context)
        rvCurrencies.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString()?.trim() ?: ""
                val sel = CurrencyManager.getSelectedCurrency(context)
                adapter.updateData(filterList(), sel.countryName, sel.currencyCode)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
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
            val sel = CurrencyManager.getSelectedCurrency(context)
            adapter.updateData(filterList(), sel.countryName, sel.currencyCode)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateThemeButtonStates() {
        val context = context ?: return
        val currentMode = ThemeManager.getSavedThemeMode(context)

        val primaryColor = ContextCompat.getColor(context, R.color.primary)
        val textPrimaryColor = ContextCompat.getColor(context, R.color.text_primary)
        val borderStrokeColor = ContextCompat.getColor(context, R.color.border_stroke)

        fun styleButton(button: MaterialButton?, isSelected: Boolean) {
            button ?: return
            if (isSelected) {
                button.backgroundTintList = ColorStateList.valueOf(primaryColor)
                button.setTextColor(Color.WHITE)
                button.strokeWidth = 0
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                button.setTextColor(textPrimaryColor)
                button.strokeColor = ColorStateList.valueOf(borderStrokeColor)
                button.strokeWidth = (1 * resources.displayMetrics.density).toInt()
            }
        }

        styleButton(btnThemeLight, currentMode == ThemeManager.THEME_LIGHT)
        styleButton(btnThemeDark, currentMode == ThemeManager.THEME_DARK)
        styleButton(btnThemeSystem, currentMode == ThemeManager.THEME_SYSTEM)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.setBlackStatusBarIconsInLightMode()
    }

    private fun setupTouchScaleAnimation(view: View?) {
        view?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(160).start()
                }
            }
            false
        }
    }
}