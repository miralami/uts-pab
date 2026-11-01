package com.example.questlog.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val QUESTS = "quests"
    const val HERO = "hero"
    const val SHOP = "shop"
    const val QUEST_FORM = "quest_form?id={id}"

    fun questForm(id: Int) = "quest_form?id=$id"
}

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.QUESTS, "Quest", Icons.AutoMirrored.Filled.List),
    TopLevelDestination(Routes.HERO, "Hero", Icons.Default.Person),
    TopLevelDestination(Routes.SHOP, "Shop", Icons.Default.ShoppingCart)
)

@Composable
fun QuestLogApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.QUESTS,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(Routes.QUESTS) {
                QuestBoardScreen(
                    onAddQuest = { navController.navigate(Routes.questForm(0)) },
                    onEditQuest = { id -> navController.navigate(Routes.questForm(id)) }
                )
            }
            composable(Routes.HERO) {
                HeroScreen()
            }
            composable(Routes.SHOP) {
                ShopScreen()
            }
            composable(
                route = Routes.QUEST_FORM,
                arguments = listOf(navArgument("id") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { entry ->
                QuestFormScreen(
                    questId = entry.arguments?.getInt("id") ?: 0,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
