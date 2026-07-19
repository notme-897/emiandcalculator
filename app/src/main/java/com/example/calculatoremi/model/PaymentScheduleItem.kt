package com.example.calculatoremi.model

import java.io.Serializable

data class PaymentScheduleItem(
    val emiNo: Int,
    val date: String,
    val emi: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double
) : Serializable