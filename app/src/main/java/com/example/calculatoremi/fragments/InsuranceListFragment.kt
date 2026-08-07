package com.example.calculatoremi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.MainActivity
import com.example.calculatoremi.R
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem

class InsuranceListFragment : Fragment(R.layout.fragment_category_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? MainActivity)?.hideHeader()
        (requireActivity() as? MainActivity)?.hideBottomNav()
        (requireActivity() as? MainActivity)?.setBlackStatusBarIconsInLightMode()

        val categoryTitle = arguments?.getString("CATEGORY_TITLE") ?: "Insurance Tools"
        val txtCategoryTitle = view.findViewById<TextView>(R.id.txtCategoryTitle)
        txtCategoryTitle?.text = categoryTitle

        val btnBackCategory = view.findViewById<ImageView>(R.id.btnBackCategory)
        val btnBackContainer = view.findViewById<CardView>(R.id.btnBackContainer)

        val backClickAction = View.OnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnBackCategory?.setOnClickListener(backClickAction)
        btnBackContainer?.setOnClickListener(backClickAction)

        val recyclerLoans = view.findViewById<RecyclerView>(R.id.recyclerLoans)

        val insuranceList = listOf(
            LoanItem("Human Life Value (HLV)", "Calculate ideal life insurance cover based on earnings & debts", R.drawable.ic_insurance, R.color.cat_insurance),
            LoanItem("Term Premium Estimator", "Estimate monthly/annual term premiums by age & sum assured", R.drawable.ic_tax, R.color.cat_salary),
            LoanItem("Income Replacement Cover", "Calculate lump-sum needed to replace monthly income for dependents", R.drawable.ic_account_balance, R.color.cat_loan),
            LoanItem("Health Insurance Estimator", "Optimal health cover based on family size & medical inflation", R.drawable.ic_person, R.color.cat_property),
            LoanItem("NCB & Deductible Simulator", "Simulate premium savings using No-Claim-Bonus & voluntary deductible", R.drawable.ic_tools, R.color.cat_utility),
            LoanItem("Critical Illness Cover", "Estimate funds needed for major illness recovery & lost wages", R.drawable.ic_sparkle, R.color.cat_insurance),
            LoanItem("BTIR vs ULIP Comparator", "Compare Pure Term + Mutual Funds vs ULIP returns", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Endowment Policy Return (IRR)", "Calculate true Internal Rate of Return (IRR) of traditional plans", R.drawable.ic_business, R.color.cat_property),
            LoanItem("Pension & Annuity Estimator", "Project lifetime monthly pension from retirement lump-sum", R.drawable.ic_calendar, R.color.cat_loan),
            LoanItem("Vehicle IDV Calculator", "Compute depreciated market value slab of your vehicle for claim cover", R.drawable.ic_car, R.color.cat_utility),
            LoanItem("Home Reinstatement Cost", "Estimate rebuilding costs vs land value for property insurance", R.drawable.ic_home, R.color.cat_insurance)
        )

        recyclerLoans.layoutManager = LinearLayoutManager(requireContext())
        recyclerLoans.adapter = LoanAdapter(insuranceList) { item ->
            val intent = when (item.title) {
                "Human Life Value (HLV)" -> Intent(requireContext(), com.example.calculatoremi.activities.HlvCalculatorActivity::class.java)
                "Term Premium Estimator" -> Intent(requireContext(), com.example.calculatoremi.activities.TermPremiumEstimatorActivity::class.java)
                "Income Replacement Cover" -> Intent(requireContext(), com.example.calculatoremi.activities.IncomeReplacementActivity::class.java)
                "Health Insurance Estimator" -> Intent(requireContext(), com.example.calculatoremi.activities.HealthInsuranceEstimatorActivity::class.java)
                "NCB & Deductible Simulator" -> Intent(requireContext(), com.example.calculatoremi.activities.NcbDeductibleSimulatorActivity::class.java)
                "Critical Illness Cover" -> Intent(requireContext(), com.example.calculatoremi.activities.CriticalIllnessActivity::class.java)
                "BTIR vs ULIP Comparator" -> Intent(requireContext(), com.example.calculatoremi.activities.BtirVsUlipActivity::class.java)
                "Endowment Policy Return (IRR)" -> Intent(requireContext(), com.example.calculatoremi.activities.EndowmentIrrActivity::class.java)
                "Pension & Annuity Estimator" -> Intent(requireContext(), com.example.calculatoremi.activities.PensionAnnuityActivity::class.java)
                "Vehicle IDV Calculator" -> Intent(requireContext(), com.example.calculatoremi.activities.VehicleIdvActivity::class.java)
                "Home Reinstatement Cost" -> Intent(requireContext(), com.example.calculatoremi.activities.HomeReinstatementActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerLoans.layoutAnimation = animation
        recyclerLoans.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.setBlackStatusBarIconsInLightMode()
    }
}
