package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.calculatoremi.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.fragment_settings) {

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

    private fun setupTouchScaleAnimation(view: View) {
        view.setOnTouchListener { v, event ->
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