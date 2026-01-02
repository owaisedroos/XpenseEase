package com.codewithfk.expensetracker.android.utils

import com.codewithfk.expensetracker.android.R
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Utils {

    fun formatDateToHumanReadableForm(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatDateForChart(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd-MMM", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatCurrency(amount: Double, locale: Locale = Locale("en", "IN")): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        return currencyFormatter.format(amount)
    }

    fun formatDayMonthYear(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatDayMonth(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd/MMM", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatToDecimalValue(d: Double): String {
        return String.format("%.2f", d)
    }

    fun formatStringDateToMonthDayYear(date: String): String {
        val millis = getMillisFromDate(date)
        return formatDayMonthYear(millis)
    }

    fun getMillisFromDate(date: String): Long {
        return getMilliFromDate(date)
    }

    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println("Today is $date")
        return date.time
    }

    fun getItemIcon(item: ExpenseEntity): Int {
        return when (item.title) {  // Use a when statement for more categories
            "Pocket Money" -> R.drawable.ic_pocketmoney
            "Part-Time Job" -> R.drawable.ic_parttime
            "Freelance Work" -> R.drawable.ic_freelancer
            "Scholarship" -> R.drawable.ic_scholar
            "Allowance" -> R.drawable.ic_allowance
            "Tuition Fees" -> R.drawable.ic_tution
            "Textbooks & Supplies" -> R.drawable.ic_book
            "Rent/Dorm" -> R.drawable.ic_rent
            "Utilities" -> R.drawable.ic_utilities
            "Groceries" -> R.drawable.ic_groceries
            "Food & Dining" -> R.drawable.ic_food
            "Transportation" -> R.drawable.ic_transport
            "Entertainment" -> R.drawable.ic_entertain
            "Personal Care" -> R.drawable.ic_care
            "Healthcare" -> R.drawable.ic_health
            "Subscriptions" -> R.drawable.ic_sub
            "Clothing" -> R.drawable.ic_clothing
            "Other Income" -> R.drawable.ic_in
            "Other Expenses" -> R.drawable.ic_cost

            else -> R.drawable.ic_default_icon // Provide a default icon
        }
    }

}