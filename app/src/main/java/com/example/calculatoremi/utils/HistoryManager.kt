package com.example.calculatoremi.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.calculatoremi.model.CalculationHistoryItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HistoryManager {

    private const val PREF_NAME = "emi_calc_history_pref"
    private const val KEY_HISTORY_JSON = "key_history_list_json"
    private const val MAX_HISTORY_ITEMS = 50

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun saveCalculation(
        context: Context,
        title: String,
        category: String,
        primaryResultLabel: String,
        primaryResultValue: String,
        detailsSummary: String,
        loanAmount: Double,
        interestRate: Float,
        years: Int,
        months: Int,
        startDate: String,
        emi: Double,
        totalInterest: Double,
        totalCost: Double,
        payoffDate: String
    ) {
        try {
            val list = getHistoryList(context).toMutableList()
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(now))
            val id = "hist_" + now + "_" + (1000..9999).random()

            val newItem = CalculationHistoryItem(
                id = id,
                title = title,
                timestamp = now,
                formattedDate = formattedDate,
                category = category,
                primaryResultLabel = primaryResultLabel,
                primaryResultValue = primaryResultValue,
                detailsSummary = detailsSummary,
                loanAmount = loanAmount,
                interestRate = interestRate,
                years = years,
                months = months,
                startDate = startDate,
                emi = emi,
                totalInterest = totalInterest,
                totalCost = totalCost,
                payoffDate = payoffDate
            )

            // Prevent exact duplicate contiguous logs within 5 seconds
            if (list.isNotEmpty()) {
                val last = list.first()
                if (last.title == title && last.primaryResultValue == primaryResultValue && (now - last.timestamp) < 5000) {
                    return
                }
            }

            list.add(0, newItem)
            if (list.size > MAX_HISTORY_ITEMS) {
                list.removeAt(list.size - 1)
            }

            saveHistoryList(context, list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getHistoryList(context: Context): List<CalculationHistoryItem> {
        val list = mutableListOf<CalculationHistoryItem>()
        try {
            val jsonString = getPrefs(context).getString(KEY_HISTORY_JSON, null) ?: return emptyList()
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = CalculationHistoryItem(
                    id = obj.optString("id", ""),
                    title = obj.optString("title", "Calculation"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    formattedDate = obj.optString("formattedDate", ""),
                    category = obj.optString("category", "Loans"),
                    primaryResultLabel = obj.optString("primaryResultLabel", "Result"),
                    primaryResultValue = obj.optString("primaryResultValue", "₹0"),
                    detailsSummary = obj.optString("detailsSummary", ""),
                    loanAmount = obj.optDouble("loanAmount", 0.0),
                    interestRate = obj.optDouble("interestRate", 0.0).toFloat(),
                    years = obj.optInt("years", 0),
                    months = obj.optInt("months", 0),
                    startDate = obj.optString("startDate", ""),
                    emi = obj.optDouble("emi", 0.0),
                    totalInterest = obj.optDouble("totalInterest", 0.0),
                    totalCost = obj.optDouble("totalCost", 0.0),
                    payoffDate = obj.optString("payoffDate", "")
                )
                list.add(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @Synchronized
    fun deleteHistoryItem(context: Context, id: String) {
        val list = getHistoryList(context).filter { it.id != id }
        saveHistoryList(context, list)
    }

    @Synchronized
    fun clearAllHistory(context: Context) {
        getPrefs(context).edit().remove(KEY_HISTORY_JSON).apply()
    }

    private fun saveHistoryList(context: Context, list: List<CalculationHistoryItem>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("timestamp", item.timestamp)
            obj.put("formattedDate", item.formattedDate)
            obj.put("category", item.category)
            obj.put("primaryResultLabel", item.primaryResultLabel)
            obj.put("primaryResultValue", item.primaryResultValue)
            obj.put("detailsSummary", item.detailsSummary)
            obj.put("loanAmount", item.loanAmount)
            obj.put("interestRate", item.interestRate.toDouble())
            obj.put("years", item.years)
            obj.put("months", item.months)
            obj.put("startDate", item.startDate)
            obj.put("emi", item.emi)
            obj.put("totalInterest", item.totalInterest)
            obj.put("totalCost", item.totalCost)
            obj.put("payoffDate", item.payoffDate)
            jsonArray.put(obj)
        }
        getPrefs(context).edit().putString(KEY_HISTORY_JSON, jsonArray.toString()).apply()
    }
}
