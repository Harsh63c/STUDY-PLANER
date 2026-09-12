package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.repository.PlannerRepository
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.studyplanner.FocusLockSetupScreen
import com.example.ui.screens.studyplanner.StudyPlannerScreen
import com.example.ui.screens.studyplanner.TimerScreen
import com.example.viewmodel.CalendarViewModel
import com.example.viewmodel.CalendarViewModelFactory
import com.example.viewmodel.StudyPlannerViewModel
import com.example.viewmodel.StudyPlannerViewModelFactory

import androidx.compose.runtime.LaunchedEffect

@Composable
fun AppNavigation(repository: PlannerRepository, startPlanId: Int? = null) {
    val navController = rememberNavController()

    LaunchedEffect(startPlanId) {
        if (startPlanId != null) {
            navController.navigate(TimerRoute(1, 1, startPlanId))
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text(androidx.compose.ui.res.stringResource(com.example.R.string.nav_calendar)) },
                    selected = currentDestination?.hierarchy?.any { it.route == CalendarRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(CalendarRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Study Planner") },
                    label = { Text(androidx.compose.ui.res.stringResource(com.example.R.string.nav_study)) },
                    selected = currentDestination?.hierarchy?.any { it.route == StudyPlannerRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(StudyPlannerRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text(androidx.compose.ui.res.stringResource(com.example.R.string.nav_settings)) },
                    selected = currentDestination?.hierarchy?.any { it.route == SettingsRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(SettingsRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CalendarRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<CalendarRoute> {
                val vm: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(repository))
                CalendarScreen(vm)
            }
            composable<StudyPlannerRoute> {
                val vm: StudyPlannerViewModel = viewModel(factory = StudyPlannerViewModelFactory(repository))
                StudyPlannerScreen(
                    viewModel = vm,
                    onNavigateToTimer = { subjectId, chapterId, studyPlanId ->
                        navController.navigate(TimerRoute(subjectId, chapterId, studyPlanId))
                    },
                    onNavigateToFocusLock = {
                        navController.navigate(FocusLockSetupRoute)
                    }
                )
            }
            composable<SettingsRoute> {
                // Settings Screen
                com.example.ui.screens.settings.SettingsScreen()
            }
            composable<TimerRoute> { backStackEntry ->
                val timerRoute = backStackEntry.toRoute<TimerRoute>()
                val vm: StudyPlannerViewModel = viewModel(factory = StudyPlannerViewModelFactory(repository))
                TimerScreen(
                    viewModel = vm,
                    subjectId = timerRoute.subjectId, 
                    chapterId = timerRoute.chapterId, 
                    studyPlanId = timerRoute.studyPlanId, 
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<FocusLockSetupRoute> {
                FocusLockSetupScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
