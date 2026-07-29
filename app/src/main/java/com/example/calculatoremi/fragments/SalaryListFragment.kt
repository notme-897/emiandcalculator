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
import com.example.calculatoremi.activities.CtcToInHandActivity
import com.example.calculatoremi.activities.GrossToNetActivity
import com.example.calculatoremi.activities.PaySlipGeneratorActivity
import com.example.calculatoremi.activities.NetToGrossActivity
import com.example.calculatoremi.adapter.LoanAdapter
import com.example.calculatoremi.model.LoanItem

class SalaryListFragment : Fragment(R.layout.fragment_category_list) {

    private lateinit var recyclerLoans: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide main header and bottom navigation for full-screen category view
        (requireActivity() as? MainActivity)?.hideHeader()
        (requireActivity() as? MainActivity)?.hideBottomNav()

        // Apply status bar top padding
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

        txtCategoryTitle.text = arguments?.getString("CATEGORY_TITLE") ?: "Salary Tools"
        
        val backClickAction = View.OnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnBackCategory?.setOnClickListener(backClickAction)
        btnBackContainer?.setOnClickListener(backClickAction)

        recyclerLoans = view.findViewById(R.id.recyclerLoans)

        val salaryList = listOf(
            LoanItem("CTC to In-Hand", "Convert annual CTC into monthly bank take-home pay", R.drawable.ic_salary, R.color.cat_salary),
            LoanItem("Gross to Net Salary", "Compute net salary directly from gross monthly earnings", R.drawable.ic_person, R.color.cat_loan),
            LoanItem("Pay Slip Component Generator", "Auto-split gross salary into Basic, HRA, Special Allowance & LTA", R.drawable.ic_business, R.color.cat_investment),
            LoanItem("Net-to-Gross (Reverse)", "Calculate required gross CTC from target net in-hand cash", R.drawable.ic_account_balance, R.color.cat_utility),
            LoanItem("Salary Hike / Appraisal", "Calculate post-appraisal revised salary & net monthly increase", R.drawable.ic_salary, R.color.cat_property),
            LoanItem("Job Offer CTC Comparator", "Compare 2+ job offers side-by-side on real in-hand cash basis", R.drawable.ic_business, R.color.cat_insurance),
            LoanItem("Relocation Salary Adjuster", "Calculate required salary hike when moving between city tiers", R.drawable.ic_account_balance, R.color.cat_salary),
            LoanItem("Hourly to Annual Salary", "Convert hourly billing rate to equivalent annual & monthly pay", R.drawable.ic_history, R.color.cat_investment),
            LoanItem("Freelance / Contractor Rate", "Estimate min hourly/day billing rate to match full-time CTC", R.drawable.ic_tools, R.color.cat_tax),
            LoanItem("Prorated Partial Salary", "Compute partial month payout for mid-month joining/leaving", R.drawable.ic_calendar, R.color.cat_loan)
        )

        recyclerLoans.layoutManager = LinearLayoutManager(requireContext())
        recyclerLoans.adapter = LoanAdapter(salaryList) { item ->
            val intent = when (item.title) {
                "CTC to In-Hand" -> Intent(requireContext(), CtcToInHandActivity::class.java)
                "Gross to Net Salary" -> Intent(requireContext(), GrossToNetActivity::class.java)
                "Pay Slip Component Generator" -> Intent(requireContext(), PaySlipGeneratorActivity::class.java)
                "Net-to-Gross (Reverse)" -> Intent(requireContext(), NetToGrossActivity::class.java)
                "Salary Hike / Appraisal" -> Intent(requireContext(), com.example.calculatoremi.activities.SalaryHikeActivity::class.java)
                "Job Offer CTC Comparator" -> Intent(requireContext(), com.example.calculatoremi.activities.JobOfferComparatorActivity::class.java)
                "Relocation Salary Adjuster" -> Intent(requireContext(), com.example.calculatoremi.activities.RelocationAdjusterActivity::class.java)
                "Hourly to Annual Salary" -> Intent(requireContext(), com.example.calculatoremi.activities.HourlyToAnnualActivity::class.java)
                "Freelance / Contractor Rate" -> Intent(requireContext(), com.example.calculatoremi.activities.FreelanceRateEstimatorActivity::class.java)
                "Prorated Partial Salary" -> Intent(requireContext(), com.example.calculatoremi.activities.ProratedSalaryActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerLoans.layoutAnimation = animation
        recyclerLoans.scheduleLayoutAnimation()
    }
}
