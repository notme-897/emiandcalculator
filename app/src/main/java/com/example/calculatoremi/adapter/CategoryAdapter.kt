package com.example.calculatoremi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.CalculatorCategory

class CategoryAdapter(
    private val categories: List<CalculatorCategory>,
    private val onItemClick: (CalculatorCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val subtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        val icon: ImageView = itemView.findViewById(R.id.imgLoan)
        val background: RelativeLayout = itemView.findViewById(R.id.cardBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.icon.setImageResource(item.icon)
        
        // Use the category color
        holder.background.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, item.colorRes))

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = categories.size
}