package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.activities.*
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem

class ToolsFragment : Fragment(R.layout.fragment_tools) {

    private lateinit var recyclerTools: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerView = view.findViewById<View>(R.id.toolsHeader)
        headerView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val topPadding = systemBars.top + (14 * resources.displayMetrics.density).toInt()
                v.setPadding(v.paddingLeft, topPadding, v.paddingRight, v.paddingBottom)
                insets
            }
        }

        recyclerTools = view.findViewById(R.id.recyclerTools)

        val toolList = listOf(
            LoanItem("Bill Splitter & Tip", "Split restaurant bills & tips among friends", R.drawable.ic_tools, R.color.cat_utility),
            LoanItem("Fuel & Trip Cost", "Calculate travel distance, fuel liters & cost", R.drawable.ic_car, R.color.cat_loan),
            LoanItem("Cash Counter", "Count cash notes (₹500, ₹200, ₹100...) & total sum", R.drawable.ic_salary, R.color.cat_salary),
            LoanItem("Percentage & Margin", "Simple % of value, increase/decrease & profit", R.drawable.ic_pie_chart, R.color.cat_utility),
            LoanItem("Daily Savings Goal", "Track days remaining to reach target savings", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Unit Price Shopping", "Compare Item A vs Item B for best deal per gram/liter", R.drawable.ic_home, R.color.cat_utility),
            LoanItem("Discount & Shopping", "Calculate sale price, extra tax & total savings", R.drawable.ic_tax, R.color.cat_utility),
            LoanItem("SIP Calculator", "Mutual Fund wealth growth & returns", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("FD Calculator", "Fixed Deposit compounding returns", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Prepayment Simulator", "Calculate interest saved by paying extra EMI", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Compare Loans", "Side-by-side comparison between Loan A & Loan B", R.drawable.ic_home, R.color.cat_loan)
        )

        recyclerTools.layoutManager = LinearLayoutManager(requireContext())
        recyclerTools.adapter = LoanAdapter(toolList) { tool ->
            val intent = when (tool.title) {
                "Bill Splitter & Tip" -> Intent(requireContext(), BillSplitterActivity::class.java)
                "Fuel & Trip Cost" -> Intent(requireContext(), FuelTripCalculatorActivity::class.java)
                "Cash Counter" -> Intent(requireContext(), CashDenominationActivity::class.java)
                "Percentage & Margin" -> Intent(requireContext(), PercentageCalculatorActivity::class.java)
                "Daily Savings Goal" -> Intent(requireContext(), SavingsGoalActivity::class.java)
                "Unit Price Shopping" -> Intent(requireContext(), UnitPriceComparatorActivity::class.java)
                "Discount & Shopping" -> Intent(requireContext(), DiscountCalculatorActivity::class.java)
                "SIP Calculator" -> Intent(requireContext(), SipCalculatorActivity::class.java)
                "FD Calculator" -> Intent(requireContext(), FdCalculatorActivity::class.java)
                "Prepayment Simulator" -> Intent(requireContext(), PrepaymentSimulatorActivity::class.java)
                "Compare Loans" -> Intent(requireContext(), LoanComparisonActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        // Apply high-end spring entrance animation
        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerTools.layoutAnimation = animation
        recyclerTools.scheduleLayoutAnimation()
    }
}