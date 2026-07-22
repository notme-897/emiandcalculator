package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.activities.GstCalculatorActivity
import com.example.calculatoremi.activities.SipCalculatorActivity
import com.example.calculatoremi.adapter.CategoryAdapter
import com.example.calculatoremi.model.CalculatorCategory
import com.example.calculatoremi.model.CategoryType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set status bar top padding cleanly without extra margins
        val headerContent = view.findViewById<View>(R.id.headerContentLayout)
        headerContent?.let { v ->
            val resourceId = requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) {
                requireContext().resources.getDimensionPixelSize(resourceId)
            } else {
                (28 * requireContext().resources.displayMetrics.density).toInt()
            }
            val topOffset = (8 * requireContext().resources.displayMetrics.density).toInt()
            val bottomOffset = (16 * requireContext().resources.displayMetrics.density).toInt()

            v.setPadding(
                v.paddingLeft,
                statusBarHeight + topOffset,
                v.paddingRight,
                bottomOffset
            )

            // Header Soft Entrance Animation
            v.alpha = 0f
            v.animate().alpha(1f).setDuration(350).start()
        }

        // 3D Depth Parallax Scroll Effect on Blue Header
        val nestedScrollView = view as? NestedScrollView
        val dashboardHeader = view.findViewById<View>(R.id.dashboardHeader)
        val imgDecoration = view.findViewById<View>(R.id.imgHeaderDecoration)

        nestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            imgDecoration?.translationY = scrollY * 0.35f
            dashboardHeader?.alpha = (1.0f - (scrollY.toFloat() / 380f)).coerceIn(0.3f, 1.0f)
        })

        // Set dynamic time-of-day greeting
        val txtGreeting = view.findViewById<TextView>(R.id.txtGreeting)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        txtGreeting?.text = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }

        // Set dynamic formatted date on right side of header
        val txtDate = view.findViewById<TextView>(R.id.txtDate)
        txtDate?.text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())

        // Set up categories recycler with spring overshoot entrance animation
        val recyclerCategories = view.findViewById<RecyclerView>(R.id.recyclerLoans)

        val categories = listOf(
            CalculatorCategory("Loan Calculators", "Personal, Home, Car, and more", R.drawable.ic_home, R.color.cat_loan, CategoryType.LOAN),
            CalculatorCategory("Investment Tools", "SIP, FD, RD, and Mutual Funds", R.drawable.ic_investment, R.color.cat_investment, CategoryType.INVESTMENT),
            CalculatorCategory("Tax & Finance", "GST, Income Tax, and Discount Calculator", R.drawable.ic_tax, R.color.cat_tax, CategoryType.TAX),
            CalculatorCategory("Salary Tools", "In-Hand Salary, Hikes, and Overtime", R.drawable.ic_salary, R.color.cat_salary, CategoryType.SALARY),
            CalculatorCategory("Insurance Tools", "Term, Health, and Life Insurance", R.drawable.ic_insurance, R.color.cat_insurance, CategoryType.INSURANCE),
            CalculatorCategory("Utility Tools", "Currency, Unit, and Age Converter", R.drawable.ic_tools, R.color.cat_utility, CategoryType.UTILITY)
        )

        recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        recyclerCategories.adapter = CategoryAdapter(categories) { category ->
            when (category.type) {
                CategoryType.LOAN -> {
                    val fragment = LoanListFragment().apply {
                        arguments = Bundle().apply {
                            putString("CATEGORY_TITLE", category.title)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                CategoryType.INVESTMENT -> {
                    startActivity(Intent(requireContext(), SipCalculatorActivity::class.java))
                }
                CategoryType.TAX -> {
                    startActivity(Intent(requireContext(), GstCalculatorActivity::class.java))
                }
                else -> {
                    Toast.makeText(requireContext(), "${category.title} coming soon!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Apply high-end spring overshoot entrance animation
        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerCategories.layoutAnimation = animation
        recyclerCategories.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.showHeader()
        (requireActivity() as? MainActivity)?.showBottomNav()
    }
}