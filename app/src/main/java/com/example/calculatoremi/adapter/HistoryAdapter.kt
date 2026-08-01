package com.example.calculatoremi.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.CalculationHistoryItem

class HistoryAdapter(
    private var items: List<CalculationHistoryItem>,
    private val onItemClick: (CalculationHistoryItem) -> Unit,
    private val onDeleteClick: (CalculationHistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    fun updateList(newItems: List<CalculationHistoryItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_card, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemClick, onDeleteClick)
    }

    override fun getItemCount(): Int = items.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCategory: TextView = itemView.findViewById(R.id.txtHistoryCategory)
        private val txtDate: TextView = itemView.findViewById(R.id.txtHistoryDate)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtHistoryTitle)
        private val txtPrimaryValue: TextView = itemView.findViewById(R.id.txtHistoryPrimaryValue)
        private val txtDetails: TextView = itemView.findViewById(R.id.txtHistoryDetails)
        private val btnDelete: View = itemView.findViewById(R.id.btnDeleteHistory)

        fun bind(
            item: CalculationHistoryItem,
            onItemClick: (CalculationHistoryItem) -> Unit,
            onDeleteClick: (CalculationHistoryItem) -> Unit
        ) {
            txtTitle.text = item.title
            txtDate.text = item.formattedDate
            txtPrimaryValue.text = item.primaryResultValue
            txtDetails.text = item.detailsSummary
            txtCategory.text = item.category.uppercase()

            // Badge Color based on category
            when (item.category.lowercase()) {
                "loans", "loan" -> {
                    txtCategory.setBackgroundColor(Color.parseColor("#DBEAFE"))
                    txtCategory.setTextColor(Color.parseColor("#1D4ED8"))
                }
                "investments", "investment" -> {
                    txtCategory.setBackgroundColor(Color.parseColor("#DCFCE7"))
                    txtCategory.setTextColor(Color.parseColor("#15803D"))
                }
                "salary", "income" -> {
                    txtCategory.setBackgroundColor(Color.parseColor("#F3E8FF"))
                    txtCategory.setTextColor(Color.parseColor("#7E22CE"))
                }
                else -> {
                    txtCategory.setBackgroundColor(Color.parseColor("#FFEDD5"))
                    txtCategory.setTextColor(Color.parseColor("#C2410C"))
                }
            }

            itemView.setOnClickListener {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onItemClick(item)
            }

            btnDelete.setOnClickListener {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onDeleteClick(item)
            }

            setupTouchScaleAnimation(itemView)
            setupTouchScaleAnimation(btnDelete)
        }

        private fun setupTouchScaleAnimation(view: View) {
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(OvershootInterpolator(2.0f)).setDuration(160).start()
                    }
                }
                false
            }
        }
    }
}
