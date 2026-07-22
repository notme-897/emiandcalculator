package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.activities.*
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem

class LoanListFragment : Fragment(R.layout.fragment_category_list) {

    private lateinit var recyclerLoans: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide main header and bottom navigation for full-screen category view
        (requireActivity() as? MainActivity)?.hideHeader()
        (requireActivity() as? MainActivity)?.hideBottomNav()

        // Apply status bar top padding to category header dynamically
        val categoryHeader = view.findViewById<View>(R.id.categoryHeader)
        categoryHeader?.let { v ->
            val resourceId = requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) {
                requireContext().resources.getDimensionPixelSize(resourceId)
            } else {
                (28 * requireContext().resources.displayMetrics.density).toInt()
            }
            v.setPadding(
                v.paddingLeft,
                statusBarHeight,
                v.paddingRight,
                v.paddingBottom
            )
        }

        val txtCategoryTitle = view.findViewById<TextView>(R.id.txtCategoryTitle)
        val btnBackCategory = view.findViewById<ImageView>(R.id.btnBackCategory)

        txtCategoryTitle.text = arguments?.getString("CATEGORY_TITLE") ?: "Loan Calculators"
        btnBackCategory.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recyclerLoans = view.findViewById(R.id.recyclerLoans)

        val loanList = listOf(
            LoanItem("Personal Loan", "Instant personal EMI calculation & interest schedule", R.drawable.ic_person, R.color.cat_loan),
            LoanItem("Home Loan", "Housing loan EMI, tenure options & tax breakdown", R.drawable.ic_home, R.color.cat_investment),
            LoanItem("Car Loan", "Auto loan EMI with down payment simulator", R.drawable.ic_car, R.color.cat_tax),
            LoanItem("Business Loan", "Commercial capital loan & monthly repayment plan", R.drawable.ic_business, R.color.cat_salary),
            LoanItem("Education Loan", "Student loan EMI with grace period planning", R.drawable.ic_school, R.color.cat_insurance),
            LoanItem("Gold Loan", "Jewelry ornament valuation & per gram EMI rate", R.drawable.ic_gold, R.color.cat_utility),
            LoanItem("Bike Loan", "Two-wheeler loan EMI with processing fee calculation", R.drawable.ic_two_wheeler, R.color.cat_loan),
            LoanItem("Mortgage Loan", "Property-collateralized loan with flexible tenure", R.drawable.ic_account_balance, R.color.cat_investment)
        )

        recyclerLoans.layoutManager = LinearLayoutManager(requireContext())
        recyclerLoans.adapter = LoanAdapter(loanList) { loan ->
            val intent = when (loan.title) {
                "Personal Loan" -> Intent(requireContext(), PersonalLoanActivity::class.java)
                "Home Loan" -> Intent(requireContext(), HomeLoanActivity::class.java)
                "Car Loan" -> Intent(requireContext(), CarLoanActivity::class.java)
                "Business Loan" -> Intent(requireContext(), BusinessLoanActivity::class.java)
                "Education Loan" -> Intent(requireContext(), EducationLoanActivity::class.java)
                "Gold Loan" -> Intent(requireContext(), GoldLoanActivity::class.java)
                "Bike Loan" -> Intent(requireContext(), BikeLoanActivity::class.java)
                "Mortgage Loan" -> Intent(requireContext(), MortgageLoanActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        // Apply staggered spring entrance animation
        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerLoans.layoutAnimation = animation
        recyclerLoans.scheduleLayoutAnimation()
    }
}