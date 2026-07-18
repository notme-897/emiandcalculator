package com.example.calculatoremi.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.calculatoremi.R
import com.example.calculatoremi.MainActivity

class LoanCalculatorFragment : Fragment(R.layout.fragment_loan_calculator) {

    companion object {
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String): LoanCalculatorFragment {
            val fragment = LoanCalculatorFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE) ?: "Calculator"
        
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        txtTitle.text = title

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideHeader()
    }
}