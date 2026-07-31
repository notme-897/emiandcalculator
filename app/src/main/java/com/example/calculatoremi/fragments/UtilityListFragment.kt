package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.activities.*
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem

class UtilityListFragment : Fragment(R.layout.fragment_category_list) {

    private lateinit var recyclerUtilities: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? MainActivity)?.hideHeader()
        (requireActivity() as? MainActivity)?.hideBottomNav()

        val categoryHeader = view.findViewById<View>(R.id.categoryHeader)
        categoryHeader?.let { v ->
            val resourceId = requireContext().resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) {
                requireContext().resources.getDimensionPixelSize(resourceId)
            } else {
                (28 * requireContext().resources.displayMetrics.density).toInt()
            }
            val topOffset = (10 * requireContext().resources.displayMetrics.density).toInt()
            val bottomOffset = (10 * requireContext().resources.displayMetrics.density).toInt()

            v.setPadding(
                v.paddingLeft,
                statusBarHeight + topOffset,
                v.paddingRight,
                bottomOffset
            )
        }

        val txtCategoryTitle = view.findViewById<TextView>(R.id.txtCategoryTitle)
        val btnBackCategory = view.findViewById<View>(R.id.btnBackCategory)
        val btnBackContainer = view.findViewById<View>(R.id.btnBackContainer)

        txtCategoryTitle.text = arguments?.getString("CATEGORY_TITLE") ?: "Utility Tools"

        val backClickAction = View.OnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnBackCategory?.setOnClickListener(backClickAction)
        btnBackContainer?.setOnClickListener(backClickAction)

        recyclerUtilities = view.findViewById(R.id.recyclerLoans)

        val utilityList = listOf(
            LoanItem("Income Tax Regime Comparator", "Compare Old vs New Tax Regime tax liability for FY 2024-25", R.drawable.ic_tax, R.color.cat_tax),
            LoanItem("Rent vs Buy Decision Engine", "20-year net worth comparison: Rent & SIP vs Buy Property", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("SWP Retirement Cashflow", "Systematic Withdrawal Plan cashflow & corpus longevity simulation", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("NPV & IRR Cash Flow Engine", "Discounted cash flows, Secant IRR solver, Payback & PI", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Debt-to-Income (DTI) Health", "Front-end & Back-end credit risk matrix & approval tier", R.drawable.ic_account_balance, R.color.cat_salary),
            LoanItem("APY vs APR & Day-Count", "Effective yield, compounding frequencies & Act/365, Act/360 basis", R.drawable.ic_tools, R.color.cat_utility),
            LoanItem("Budget Rebalancing Engine", "50/30/20 framework with cascading auto-rebalancing algorithm", R.drawable.ic_salary, R.color.cat_property),
            LoanItem("GST Tax Calculator", "Calculate Exclusive & Inclusive GST (5%, 12%, 18%, 28%)", R.drawable.ic_tax, R.color.cat_tax),
            LoanItem("Discount & Shopping Savings", "Calculate percentage discount savings and final sale price", R.drawable.ic_tools, R.color.cat_utility),
            LoanItem("Compound Interest Calculator", "Simulate compound growth across annual, monthly & quarterly compounding", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Inflation & Future Value", "Compute future inflated cost of living & purchasing power erosion", R.drawable.ic_account_balance, R.color.cat_salary),
            LoanItem("Loan Prepayment Simulator", "Simulate interest saved & tenure reduction with extra EMI", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Compare 2 Loans", "Side-by-side EMI & total interest comparison between 2 loans", R.drawable.ic_business, R.color.cat_property)
        )

        recyclerUtilities.layoutManager = LinearLayoutManager(requireContext())
        recyclerUtilities.adapter = LoanAdapter(utilityList) { item ->
            val intent = when (item.title) {
                "Income Tax Regime Comparator" -> Intent(requireContext(), IncomeTaxRegimeActivity::class.java)
                "Rent vs Buy Decision Engine" -> Intent(requireContext(), RentVsBuyActivity::class.java)
                "SWP Retirement Cashflow" -> Intent(requireContext(), SwpCalculatorActivity::class.java)
                "NPV & IRR Cash Flow Engine" -> Intent(requireContext(), NpvIrrActivity::class.java)
                "Debt-to-Income (DTI) Health" -> Intent(requireContext(), DtiUtilityActivity::class.java)
                "APY vs APR & Day-Count" -> Intent(requireContext(), ApyAprCalculatorActivity::class.java)
                "Budget Rebalancing Engine" -> Intent(requireContext(), BudgetRebalancingActivity::class.java)
                "GST Tax Calculator" -> Intent(requireContext(), GstCalculatorActivity::class.java)
                "Discount & Shopping Savings" -> Intent(requireContext(), DiscountCalculatorActivity::class.java)
                "Compound Interest Calculator" -> Intent(requireContext(), CompoundInterestActivity::class.java)
                "Inflation & Future Value" -> Intent(requireContext(), InflationCalculatorActivity::class.java)
                "Loan Prepayment Simulator" -> Intent(requireContext(), PrepaymentSimulatorActivity::class.java)
                "Compare 2 Loans" -> Intent(requireContext(), LoanComparisonActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerUtilities.layoutAnimation = animation
        recyclerUtilities.scheduleLayoutAnimation()
    }
}
