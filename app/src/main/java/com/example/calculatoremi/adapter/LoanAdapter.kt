package com.example.calculatoremi.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.LoanItem
import com.google.android.material.card.MaterialCardView

class LoanAdapter(
    private val loanList: List<LoanItem>,
    private val onItemClick: (LoanItem) -> Unit
) : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView as MaterialCardView
        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val subtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        val icon: ImageView = itemView.findViewById(R.id.imgLoan)
        val background: RelativeLayout = itemView.findViewById(R.id.cardBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val item = loanList[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.icon.setImageResource(item.icon)
        
        try {
            val color = ContextCompat.getColor(holder.itemView.context, item.background)
            holder.background.setBackgroundColor(color)
        } catch (e: Exception) {
            holder.background.setBackgroundResource(item.background)
        }

        // Tactile Spring Scale Press & Elevation Lift Micro-Interactions
        holder.itemView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .translationZ(12f)
                        .setDuration(120)
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .translationZ(0f)
                        .setInterpolator(OvershootInterpolator(1.8f))
                        .setDuration(220)
                        .start()
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = loanList.size
}