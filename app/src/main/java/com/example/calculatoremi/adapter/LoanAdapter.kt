package com.example.calculatoremi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.LoanItem

class LoanAdapter(
    private val loanList: List<LoanItem>,
    private val onItemClick: (LoanItem) -> Unit
) : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val subtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        val icon: ImageView = itemView.findViewById(R.id.imgLoan)
        val background: RelativeLayout = itemView.findViewById(R.id.cardBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loan, parent, false)

        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {

        val item = loanList[position]

        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.icon.setImageResource(item.icon)
        holder.background.setBackgroundResource(item.background)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = loanList.size
}