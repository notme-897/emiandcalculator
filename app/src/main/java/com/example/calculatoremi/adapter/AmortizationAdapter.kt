package com.example.calculatoremi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatoremi.R
import com.example.calculatoremi.model.PaymentScheduleItem
import java.util.Locale

class AmortizationAdapter(
    private val scheduleList: List<PaymentScheduleItem>
) : RecyclerView.Adapter<AmortizationAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtEmiNo: TextView = itemView.findViewById(R.id.txtEmiNo)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtEmiAmount: TextView = itemView.findViewById(R.id.txtEmiAmount)
        val txtPrincipal: TextView = itemView.findViewById(R.id.txtPrincipal)
        val txtInterest: TextView = itemView.findViewById(R.id.txtInterest)
        val txtBalance: TextView = itemView.findViewById(R.id.txtBalance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_amortization_row, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = scheduleList[position]
        holder.txtEmiNo.text = "Month ${item.emiNo}"
        holder.txtDate.text = item.date
        holder.txtEmiAmount.text = formatCurrency(item.emi)
        holder.txtPrincipal.text = formatCurrency(item.principal)
        holder.txtInterest.text = formatCurrency(item.interest)
        holder.txtBalance.text = formatCurrency(item.balance)
    }

    override fun getItemCount(): Int = scheduleList.size

    private fun formatCurrency(value: Double): String {
        return String.format(Locale.US, "%,.2f $", value)
    }
}
