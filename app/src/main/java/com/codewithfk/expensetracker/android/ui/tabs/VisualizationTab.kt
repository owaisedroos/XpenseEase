// VisualizationTab.kt
package com.codewithfk.expensetracker.android.ui.tabs

import android.graphics.Typeface
import android.view.LayoutInflater
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.codewithfk.expensetracker.android.R
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.ui.theme.Typography
import com.codewithfk.expensetracker.android.ui.theme.Zinc
import com.codewithfk.expensetracker.android.utils.Utils
import com.codewithfk.expensetracker.android.viewmodel.ExpenseViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun VisualizationTab(expenseViewModel: ExpenseViewModel) {
    val expenses by expenseViewModel.allExpenses.collectAsState(initial = emptyList())

    if (expenses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No expense data available.", style = Typography.bodyLarge)
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Expense Visualization",
                style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TotalExpensesSummary(expenses = expenses)
            Spacer(modifier = Modifier.height(16.dp))
            PieChartView(expenses = expenses)
        }
    }
}

@Composable
fun TotalExpensesSummary(expenses: List<ExpenseEntity>) {
    val totalIncome = expenses.filter { it.type.equals("income", ignoreCase = true) }.sumOf { it.amount }
    val totalExpense = expenses.filterNot { it.type.equals("income", ignoreCase = true) }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Zinc),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem(title = "Income", amount = totalIncome, color = Color.Green)
            SummaryItem(title = "Expenses", amount = totalExpense, color = Color.Red)
            SummaryItem(title = "Balance", amount = balance, color = Color.White)
        }
    }
}

@Composable
fun SummaryItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = Typography.bodyMedium, color = Color.LightGray)
        Text(
            text = Utils.formatCurrency(amount),
            style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}

@Composable
fun PieChartView(expenses: List<ExpenseEntity>) {
    AndroidView(
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.pie_chart_layout, null)
            view
        },
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
    ) { view ->
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        val categoryExpenses = expenses.groupBy { it.title }
            .mapValues { entry ->
                entry.value.sumOf { it.amount }
            }

        val entries = categoryExpenses.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Expenses by Category").apply {
            colors = getChartColors()
            valueTextSize = 14f
            valueTypeface = Typeface.DEFAULT_BOLD
            valueFormatter = PercentFormatter(pieChart)
        }

        val pieData = PieData(dataSet).apply {
            setValueTextColor(android.graphics.Color.WHITE)
        }

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            setEntryLabelColor(android.graphics.Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.apply {
                isEnabled = true
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                isWordWrapEnabled = true
                formSize = 12f
                textSize = 12f
            }
            isDrawHoleEnabled = true
            holeRadius = 50f
            transparentCircleRadius = 55f
            setHoleColor(android.graphics.Color.WHITE)
            setUsePercentValues(true)
            animateY(1400, Easing.EaseInOutQuad)
            invalidate()
        }
    }
}

fun getChartColors(): List<Int> {
    val colors = mutableListOf<Int>()
    for (c in ColorTemplate.MATERIAL_COLORS) {
        colors.add(c)
    }
    for (c in ColorTemplate.VORDIPLOM_COLORS) {
        colors.add(c)
    }
    for (c in ColorTemplate.COLORFUL_COLORS) {
        colors.add(c)
    }
    for (c in ColorTemplate.LIBERTY_COLORS) {
        colors.add(c)
    }
    for (c in ColorTemplate.PASTEL_COLORS) {
        colors.add(c)
    }
    return colors
}