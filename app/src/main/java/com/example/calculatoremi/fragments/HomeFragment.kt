package com.example.calculatoremi.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.CategoryAdapter
import com.example.calculatoremi.model.CalculatorCategory
import com.example.calculatoremi.model.CategoryType
import com.example.calculatoremi.MainActivity
import android.widget.Toast

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerCategories: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerCategories = view.findViewById(R.id.recyclerLoans)

        val categories = listOf(
            CalculatorCategory("Loan Calculators", "Personal, Home, Car, and more", R.drawable.ic_home, R.color.cat_loan, CategoryType.LOAN),
            CalculatorCategory("Investment Tools", "SIP, FD, RD, and Mutual Funds", R.drawable.ic_investment, R.color.cat_investment, CategoryType.INVESTMENT),
            CalculatorCategory("Tax & Finance", "GST, Income Tax, and Capital Gains", R.drawable.ic_tax, R.color.cat_tax, CategoryType.TAX),
            CalculatorCategory("Salary Tools", "In-Hand Salary, Hikes, and Overtime", R.drawable.ic_salary, R.color.cat_salary, CategoryType.SALARY),
            CalculatorCategory("Insurance Tools", "Term, Health, and Life Insurance", R.drawable.ic_insurance, R.color.cat_insurance, CategoryType.INSURANCE),
            CalculatorCategory("Utility Tools", "Currency, Unit, and Age Converter", R.drawable.ic_tools, R.color.cat_utility, CategoryType.UTILITY)
        )

        recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        recyclerCategories.adapter = CategoryAdapter(categories) { category ->
            when (category.type) {
                CategoryType.LOAN -> {
                    // Navigate to Loan list fragment (Phase 1)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoanListFragment())
                        .addToBackStack(null)
                        .commit()
                }
                else -> {
                    Toast.makeText(requireContext(), "${category.title} coming soon!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showHeader()
    }
}