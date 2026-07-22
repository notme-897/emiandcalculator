package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        recyclerTools = view.findViewById(R.id.recyclerTools)

        val toolList = listOf(
            LoanItem("SIP Calculator", "Mutual Fund wealth growth & returns", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("FD Calculator", "Fixed Deposit compounding returns", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("RD Calculator", "Recurring Deposit maturity calculator", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("GST Calculator", "Add or extract GST tax amounts", R.drawable.ic_tax, R.color.cat_tax),
            LoanItem("Discount Calculator", "Calculate retail savings & net price", R.drawable.ic_tax, R.color.cat_tax),
            LoanItem("Prepayment Simulator", "Calculate interest saved by paying extra EMI", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Compare Loans", "Side-by-side comparison between Loan A & Loan B", R.drawable.ic_home, R.color.cat_loan)
        )

        recyclerTools.layoutManager = LinearLayoutManager(requireContext())
        recyclerTools.adapter = LoanAdapter(toolList) { tool ->
            val intent = when (tool.title) {
                "SIP Calculator" -> Intent(requireContext(), SipCalculatorActivity::class.java)
                "FD Calculator" -> Intent(requireContext(), FdCalculatorActivity::class.java)
                "RD Calculator" -> Intent(requireContext(), RdCalculatorActivity::class.java)
                "GST Calculator" -> Intent(requireContext(), GstCalculatorActivity::class.java)
                "Discount Calculator" -> Intent(requireContext(), DiscountCalculatorActivity::class.java)
                "Prepayment Simulator" -> Intent(requireContext(), PrepaymentSimulatorActivity::class.java)
                "Compare Loans" -> Intent(requireContext(), LoanComparisonActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }
    }
}