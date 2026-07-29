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

class InvestmentListFragment : Fragment(R.layout.fragment_category_list) {

    private lateinit var recyclerInvestments: RecyclerView

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

        txtCategoryTitle.text = arguments?.getString("CATEGORY_TITLE") ?: "Investment Tools"

        val backClickAction = View.OnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnBackCategory?.setOnClickListener(backClickAction)
        btnBackContainer?.setOnClickListener(backClickAction)

        recyclerInvestments = view.findViewById(R.id.recyclerLoans)

        val investmentList = listOf(
            LoanItem("SIP Calculator", "Systematic Investment Plan with annual Step-Up", R.drawable.ic_investment, R.color.cat_investment),
            LoanItem("Lump Sum Calculator", "One-time mutual fund & wealth growth simulator", R.drawable.ic_investment, R.color.cat_loan),
            LoanItem("FD Calculator", "Fixed Deposit with compounding & Senior Citizen benefit", R.drawable.ic_account_balance, R.color.cat_tax),
            LoanItem("RD Calculator", "Recurring Deposit monthly savings scheme", R.drawable.ic_account_balance, R.color.cat_salary),
            LoanItem("PPF Calculator", "Public Provident Fund 15-year tax-free wealth builder", R.drawable.ic_tools, R.color.cat_insurance)
        )

        recyclerInvestments.layoutManager = LinearLayoutManager(requireContext())
        recyclerInvestments.adapter = LoanAdapter(investmentList) { item ->
            val intent = when (item.title) {
                "SIP Calculator" -> Intent(requireContext(), SipCalculatorActivity::class.java)
                "Lump Sum Calculator" -> Intent(requireContext(), LumpsumCalculatorActivity::class.java)
                "FD Calculator" -> Intent(requireContext(), FdCalculatorActivity::class.java)
                "RD Calculator" -> Intent(requireContext(), RdCalculatorActivity::class.java)
                "PPF Calculator" -> Intent(requireContext(), PpfCalculatorActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        val resId = R.anim.layout_animation_spring
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recyclerInvestments.layoutAnimation = animation
        recyclerInvestments.scheduleLayoutAnimation()
    }
}
