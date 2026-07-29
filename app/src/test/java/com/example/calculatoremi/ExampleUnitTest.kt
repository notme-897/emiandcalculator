package com.example.calculatoremi

import com.example.calculatoremi.views.LoanTenureSelectorView
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test for LoanTenureSelectorView formatting.
 */
class ExampleUnitTest {
    @Test
    fun testLoanTenureFormatting() {
        assertEquals("5 Years 8 Months", LoanTenureSelectorView.formatTenureString(68))
        assertEquals("2 Years 3 Months", LoanTenureSelectorView.formatTenureString(27))
        assertEquals("18 Years 0 Months", LoanTenureSelectorView.formatTenureString(216))
        assertEquals("5 Years 0 Months", LoanTenureSelectorView.formatTenureString(60))
        assertEquals("10 Years 0 Months", LoanTenureSelectorView.formatTenureString(120))
        assertEquals("30 Years 0 Months", LoanTenureSelectorView.formatTenureString(360))
        assertEquals("1 Year 0 Months", LoanTenureSelectorView.formatTenureString(12))
        assertEquals("1 Year 1 Month", LoanTenureSelectorView.formatTenureString(13))
        assertEquals("6 Months", LoanTenureSelectorView.formatTenureString(6))
    }
}