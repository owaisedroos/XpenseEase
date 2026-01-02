// NavHostScreen.kt
package com.codewithfk.expensetracker.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.codewithfk.expensetracker.android.R
import com.codewithfk.expensetracker.android.data.ExpenseDatabase
import com.codewithfk.expensetracker.android.data.repository.ExpenseRepository
import com.codewithfk.expensetracker.android.data.repository.SharedExpenseRepository
import com.codewithfk.expensetracker.android.feature.add_expense.AddExpense
import com.codewithfk.expensetracker.android.feature.add_expense.AddSharedExpenseScreen
import com.codewithfk.expensetracker.android.feature.home.HomeScreen
import com.codewithfk.expensetracker.android.feature.stats.StatsScreen
import com.codewithfk.expensetracker.android.feature.transactionlist.TransactionListScreen
import com.codewithfk.expensetracker.android.ui.tabs.EventManagerTab
import com.codewithfk.expensetracker.android.ui.tabs.SplitwiseTab
import com.codewithfk.expensetracker.android.ui.tabs.VisualizationTab
import com.codewithfk.expensetracker.android.ui.theme.Zinc
import com.codewithfk.expensetracker.android.viewmodel.ExpenseViewModel
import com.codewithfk.expensetracker.android.viewmodel.ExpenseViewModelFactory
import com.codewithfk.expensetracker.android.viewmodel.SharedExpenseViewModel
import com.codewithfk.expensetracker.android.viewmodel.SharedExpenseViewModelFactory

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    var bottomBarVisibility by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val expenseRepository = ExpenseRepository(ExpenseDatabase.getInstance(context).expenseDao())
    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(expenseRepository)
    )

    val sharedExpenseRepository = SharedExpenseRepository(ExpenseDatabase.getInstance(context).sharedExpenseDao())
    val sharedExpenseViewModel: SharedExpenseViewModel = viewModel(
        factory = SharedExpenseViewModelFactory(sharedExpenseRepository)
    )

    Scaffold(bottomBar = {
        AnimatedVisibility(visible = bottomBarVisibility) {
            NavigationBottomBar(
                navController = navController,
                items = listOf(
                    NavItem(route = "/home", icon = R.drawable.ic_home),
                    NavItem(route = "/stats", icon = R.drawable.ic_stats),
                    NavItem(route = "/splitwise", icon = R.drawable.ic_splitwise),
                    NavItem(route = "/visualization", icon = R.drawable.ic_visualization),
                    NavItem(route = "/event_manager", icon = R.drawable.ic_event) // Add new tab
                )
            )
        }
    }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "/home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("/home") {
                bottomBarVisibility = true
                HomeScreen(navController)
            }
            composable("/stats") {
                bottomBarVisibility = true
                StatsScreen(navController)
            }
            composable("/add_income") {
                bottomBarVisibility = false
                AddExpense(navController, isIncome = true)
            }
            composable("/add_exp") {
                bottomBarVisibility = false
                AddExpense(navController, isIncome = false)
            }
            composable("/all_transactions") {
                bottomBarVisibility = true
                TransactionListScreen(navController)
            }
            composable("/splitwise") {
                bottomBarVisibility = true
                val sharedExpenses by sharedExpenseViewModel.sharedExpenses.collectAsState()
                SplitwiseTab(sharedExpenses = sharedExpenses, onAddExpenseClick = {
                    navController.navigate("/add_shared_expense")
                })
            }
            composable("/add_shared_expense") {
                bottomBarVisibility = false
                AddSharedExpenseScreen(navController, sharedExpenseViewModel)
            }
            composable("/visualization") {
                bottomBarVisibility = true
                VisualizationTab(expenseViewModel = expenseViewModel)
            }
            composable("/event_manager") {  // Add Event Manager destination
                bottomBarVisibility = true
                EventManagerTab()
            }
        }
    }
}

data class NavItem(
    val route: String,
    val icon: Int
)

@Composable
fun NavigationBottomBar(
    navController: NavController,
    items: List<NavItem>
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    BottomAppBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = item.icon), contentDescription = null)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Zinc,
                    selectedIconColor = Zinc,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}