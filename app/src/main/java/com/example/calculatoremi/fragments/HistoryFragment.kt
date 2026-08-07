package com.example.calculatoremi.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.activities.PersonalLoanResultActivity
import com.example.calculatoremi.adapter.HistoryAdapter
import com.example.calculatoremi.model.CalculationHistoryItem
import com.example.calculatoremi.utils.HistoryManager
import com.google.android.material.button.MaterialButton

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var btnClearHistory: MaterialButton
    private lateinit var imgEmptyHistory: ImageView
    private lateinit var recyclerHistory: RecyclerView

    private lateinit var chipFilterAll: MaterialButton
    private lateinit var chipFilterLoans: MaterialButton
    private lateinit var chipFilterInvestments: MaterialButton
    private lateinit var chipFilterSalary: MaterialButton
    private lateinit var chipFilterUtilities: MaterialButton

    private lateinit var historyAdapter: HistoryAdapter
    private var allHistoryItems = listOf<CalculationHistoryItem>()
    private var selectedCategory = "All"

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
        recyclerHistory = view.findViewById(R.id.recyclerHistory)

        chipFilterAll = view.findViewById(R.id.chipFilterAll)
        chipFilterLoans = view.findViewById(R.id.chipFilterLoans)
        chipFilterInvestments = view.findViewById(R.id.chipFilterInvestments)
        chipFilterSalary = view.findViewById(R.id.chipFilterSalary)
        chipFilterUtilities = view.findViewById(R.id.chipFilterUtilities)

        setupFilterChips()

        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(
            items = emptyList(),
            onItemClick = { item -> openResultScreen(item) },
            onDeleteClick = { item -> confirmDeleteSingleItem(item) }
        )
        recyclerHistory.adapter = historyAdapter

        setupTouchScaleAnimation(btnClearHistory)
        btnClearHistory.setOnClickListener {
            if (allHistoryItems.isEmpty()) {
                Toast.makeText(requireContext(), "History is already empty", Toast.LENGTH_SHORT).show()
            } else {
                confirmClearAll()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.setBlackStatusBarIconsInLightMode()
        loadHistoryData()
    }

    private fun loadHistoryData() {
        allHistoryItems = HistoryManager.getHistoryList(requireContext())
        applyFilter(selectedCategory)
    }

    private fun setupFilterChips() {
        val chips = listOf(chipFilterAll, chipFilterLoans, chipFilterInvestments, chipFilterSalary, chipFilterUtilities)
        for (chip in chips) {
            setupTouchScaleAnimation(chip)
            chip.setOnClickListener {
                selectedCategory = chip.text.toString()
                updateChipSelectionUI(selectedCategory)
                applyFilter(selectedCategory)
            }
        }
    }

    private fun updateChipSelectionUI(selected: String) {
        val chipsMap = mapOf(
            "All" to chipFilterAll,
            "Loans" to chipFilterLoans,
            "Investments" to chipFilterInvestments,
            "Salary" to chipFilterSalary,
            "Utilities" to chipFilterUtilities
        )

        val primaryColor = ContextCompat.getColor(requireContext(), R.color.primary)
        val textPrimaryColor = Color.parseColor("#475569")
        val strokeColor = Color.parseColor("#CBD5E1")

        for ((name, button) in chipsMap) {
            if (name.equals(selected, ignoreCase = true)) {
                button.backgroundTintList = ColorStateList.valueOf(primaryColor)
                button.setTextColor(Color.WHITE)
                button.strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                button.setTextColor(textPrimaryColor)
                button.strokeColor = ColorStateList.valueOf(strokeColor)
            }
        }
    }

    private fun applyFilter(category: String) {
        val filtered = if (category.equals("All", ignoreCase = true)) {
            allHistoryItems
        } else {
            allHistoryItems.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (filtered.isEmpty()) {
            recyclerHistory.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            animateEmptyState()
        } else {
            layoutEmptyState.visibility = View.GONE
            recyclerHistory.visibility = View.VISIBLE
            historyAdapter.updateList(filtered)
            
            val animation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_spring)
            recyclerHistory.layoutAnimation = animation
            recyclerHistory.scheduleLayoutAnimation()
        }
    }

    private fun openResultScreen(item: CalculationHistoryItem) {
        val intent = Intent(requireContext(), com.example.calculatoremi.activities.HistoryDetailActivity::class.java).apply {
            putExtra("EXTRA_HISTORY_ITEM", item)
        }
        startActivity(intent)
    }

    private fun confirmDeleteSingleItem(item: CalculationHistoryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete History Item")
            .setMessage("Are you sure you want to remove '${item.title}' from your history?")
            .setPositiveButton("Delete") { dialog, _ ->
                HistoryManager.deleteHistoryItem(requireContext(), item.id)
                loadHistoryData()
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun confirmClearAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear History")
            .setMessage("Are you sure you want to clear all calculation history?")
            .setPositiveButton("Clear All") { dialog, _ ->
                HistoryManager.clearAllHistory(requireContext())
                loadHistoryData()
                Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun animateEmptyState() {
        layoutEmptyState.alpha = 0f
        layoutEmptyState.translationY = 20f
        layoutEmptyState.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(350)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        imgEmptyHistory.animate()
            .translationYBy(-8f)
            .setDuration(1200)
            .withEndAction {
                imgEmptyHistory.animate()
                    .translationYBy(8f)
                    .setDuration(1200)
                    .start()
            }.start()
    }

    private fun setupTouchScaleAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(160).start()
                }
            }
            false
        }
    }
}