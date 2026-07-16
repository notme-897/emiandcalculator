package com.example.calculatoremi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.OnboardingItem

class OnboardingAdapter(
    private val onboardingItems: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgOnboarding: ImageView = view.findViewById(R.id.imgOnboarding)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = onboardingItems[position]
        holder.imgOnboarding.setImageResource(item.image)
        holder.txtTitle.text = item.title
        holder.txtDescription.text = item.description
    }

    override fun getItemCount(): Int = onboardingItems.size
}