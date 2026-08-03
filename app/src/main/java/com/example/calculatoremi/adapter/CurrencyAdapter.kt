package com.example.calculatoremi.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.CurrencyItem
import com.example.calculatoremi.utils.AnimationUtilsHelper
import com.google.android.material.card.MaterialCardView

class CurrencyAdapter(
    private var currencyList: List<CurrencyItem>,
    private var selectedCountryName: String,
    private var selectedCurrencyCode: String,
    private val onCurrencySelected: (CurrencyItem) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    fun updateData(newList: List<CurrencyItem>, newSelectedCountry: String, newSelectedCode: String) {
        currencyList = newList
        selectedCountryName = newSelectedCountry
        selectedCurrencyCode = newSelectedCode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency_row, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = currencyList[position]
        val isSelected = item.countryName.equals(selectedCountryName, ignoreCase = true) && item.currencyCode == selectedCurrencyCode
        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = currencyList.size

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardRow: MaterialCardView = itemView.findViewById(R.id.cardCurrencyRow)
        private val txtFlag: TextView = itemView.findViewById(R.id.txtFlagEmoji)
        private val txtCountry: TextView = itemView.findViewById(R.id.txtCountryName)
        private val txtDetails: TextView = itemView.findViewById(R.id.txtCurrencyDetails)
        private val txtBadge: TextView = itemView.findViewById(R.id.txtCurrencySymbolBadge)
        private val imgCheckmark: ImageView = itemView.findViewById(R.id.imgCheckmark)

        fun bind(item: CurrencyItem, isSelected: Boolean) {
            txtFlag.text = item.flagEmoji
            txtCountry.text = item.countryName
            txtDetails.text = "${item.currencyCode} - ${item.currencyName} (${item.symbol})"
            txtBadge.text = item.symbol

            txtCountry.setTextColor(Color.parseColor("#0F172A"))
            txtDetails.setTextColor(Color.parseColor("#64748B"))

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
                onCurrencySelected(item)
            }
        }
    }
}
