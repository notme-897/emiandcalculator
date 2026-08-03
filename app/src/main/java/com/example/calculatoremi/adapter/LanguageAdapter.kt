package com.example.calculatoremi.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.LanguageItem
import com.example.calculatoremi.utils.AnimationUtilsHelper
import com.google.android.material.card.MaterialCardView

class LanguageAdapter(
    private var languageList: List<LanguageItem>,
    private var selectedLanguageCode: String,
    private val onLanguageSelected: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    fun updateData(newList: List<LanguageItem>, newSelectedCode: String) {
        languageList = newList
        selectedLanguageCode = newSelectedCode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_row, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val item = languageList[position]
        val isSelected = item.languageCode.equals(selectedLanguageCode, ignoreCase = true)
        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = languageList.size

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardRow: MaterialCardView = itemView.findViewById(R.id.cardLanguageRow)
        private val txtFlag: TextView = itemView.findViewById(R.id.txtFlagEmoji)
        private val txtNative: TextView = itemView.findViewById(R.id.txtNativeName)
        private val txtEnglish: TextView = itemView.findViewById(R.id.txtEnglishName)
        private val txtBadge: TextView = itemView.findViewById(R.id.txtLanguageCodeBadge)
        private val imgCheckmark: ImageView = itemView.findViewById(R.id.imgCheckmark)

        fun bind(item: LanguageItem, isSelected: Boolean) {
            txtFlag.text = item.flagEmoji
            txtNative.text = item.nativeName
            txtEnglish.text = item.englishName
            txtBadge.text = item.languageCode.uppercase()

            txtNative.setTextColor(Color.parseColor("#0F172A"))
            txtEnglish.setTextColor(Color.parseColor("#64748B"))

            val density = itemView.context.resources.displayMetrics.density

            if (isSelected) {
                cardRow.setStrokeColor(Color.parseColor("#2563EB"))
                cardRow.strokeWidth = (2 * density).toInt()
                cardRow.setCardBackgroundColor(Color.parseColor("#EFF6FF"))
                cardRow.cardElevation = 4 * density
                imgCheckmark.visibility = View.VISIBLE
                imgCheckmark.setColorFilter(Color.parseColor("#2563EB"))
            } else {
                cardRow.setStrokeColor(Color.parseColor("#E2E8F0"))
                cardRow.strokeWidth = (1 * density).toInt()
                cardRow.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardRow.cardElevation = 2 * density
                imgCheckmark.visibility = View.GONE
            }

            AnimationUtilsHelper.attachSpringTouchFeedback(cardRow)
            cardRow.setOnClickListener {
                onLanguageSelected(item)
            }
        }
    }
}
