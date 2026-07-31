package com.example.calculatoremi.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.calculatoremi.R
import com.google.android.material.button.MaterialButton

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var btnClearHistory: MaterialButton
    private lateinit var imgEmptyHistory: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerView = view.findViewById<View>(R.id.historyHeader)
        headerView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val topPadding = systemBars.top + (12 * resources.displayMetrics.density).toInt()
                v.setPadding(v.paddingLeft, topPadding, v.paddingRight, v.paddingBottom)
                insets
            }
        }

        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        btnClearHistory = view.findViewById(R.id.btnClearHistory)
        imgEmptyHistory = view.findViewById(R.id.imgEmptyHistory)

        // Show Empty State with Soft Float Animation
        layoutEmptyState.visibility = View.VISIBLE
        layoutEmptyState.alpha = 0f
        layoutEmptyState.translationY = 30f
        layoutEmptyState.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        // Floating animation on empty icon
        imgEmptyHistory.animate()
            .translationYBy(-10f)
            .setDuration(1200)
            .withEndAction {
                imgEmptyHistory.animate()
                    .translationYBy(10f)
                    .setDuration(1200)
                    .start()
            }.start()

        setupTouchScaleAnimation(btnClearHistory)
        btnClearHistory.setOnClickListener {
            Toast.makeText(requireContext(), "History is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTouchScaleAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(160).start()
                }
            }
            false
        }
    }
}