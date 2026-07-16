package com.example.calculatoremi.fragments

import androidx.fragment.app.Fragment
import com.example.calculatoremi.R
import com.example.calculatoremi.MainActivity

class LoanCalculatorFragment : Fragment(R.layout.fragment_loan_calculator) {
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideHeader()
    }
}