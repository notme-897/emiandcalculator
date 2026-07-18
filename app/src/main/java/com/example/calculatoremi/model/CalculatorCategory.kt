package com.example.calculatoremi.model

data class CalculatorCategory(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val colorRes: Int,
    val type: CategoryType
)

enum class CategoryType {
    LOAN, INVESTMENT, TAX, SALARY, PROPERTY, INSURANCE, UTILITY
}