package com.example.calculatoremi.model

import java.io.Serializable

data class CalculationHistoryItem(
    val id: String,
    val title: String,
    val timestamp: Long,
    val formattedDate: String,
    val category: String,
    val primaryResultLabel: String,
    val primaryResultValue: String,
    val detailsSummary: String,
    val loanAmount: Double,
    val interestRate: Float,
    val years: Int,
    val months: Int,
    val startDate: String,
    val emi: Double,
    val totalInterest: Double,
    val totalCost: Double,
    val payoffDate: String
) : Serializable
