package com.example.calculatoremi.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem
import com.example.calculatoremi.MainActivity

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerLoans: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerLoans = view.findViewById(R.id.recyclerLoans)

        val loanList = listOf(

            LoanItem(
                "Personal Loan",
                "Calculate EMI in seconds",
                R.drawable.ic_home,
                R.drawable.loan_blue
            ),

            LoanItem(
                "Home Loan",
                "Mortgage EMI Calculator",
                R.drawable.ic_home,
                R.drawable.loan_blue
            ),

            LoanItem(
                "Car Loan",
                "Vehicle EMI Calculator",
                R.drawable.ic_home,
                R.drawable.loan_blue
            ),

            LoanItem(
                "Business Loan",
                "Business EMI Calculator",
                R.drawable.ic_home,
                R.drawable.loan_blue
            ),

            LoanItem(
                "Education Loan",
                "Education EMI Calculator",
                R.drawable.ic_home,
                R.drawable.loan_blue
            ),

            LoanItem(
                "More Calculators",
                "FD • SIP • GST • Tax",
                R.drawable.ic_tools,
                R.drawable.loan_blue
            )
        )

        recyclerLoans.layoutManager = LinearLayoutManager(requireContext())
        recyclerLoans.adapter = LoanAdapter(loanList) { loan ->

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    LoanCalculatorFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).showHeader()
    }
}