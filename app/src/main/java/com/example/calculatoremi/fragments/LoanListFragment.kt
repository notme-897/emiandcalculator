package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.activities.*

class LoanListFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerLoans: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reuse fragment_home layout which just has a RecyclerView
        recyclerLoans = view.findViewById(R.id.recyclerLoans)

        val loanList = listOf(
            LoanItem("Personal Loan", "Calculate EMI in seconds", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Home Loan", "Mortgage EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Car Loan", "Vehicle EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Business Loan", "Business EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Education Loan", "Education EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Gold Loan", "Gold Loan EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Bike Loan", "Bike Loan EMI Calculator", R.drawable.ic_home, R.color.cat_loan),
            LoanItem("Mortgage Loan", "Mortgage EMI Calculator", R.drawable.ic_home, R.color.cat_loan)
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
    }

    override fun onResume() {
        super.onResume()
        // Ensure header is hidden or shown correctly
        (requireActivity() as MainActivity).showHeader()
    }
}