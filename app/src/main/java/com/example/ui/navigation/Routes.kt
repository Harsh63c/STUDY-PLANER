package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object CalendarRoute

@Serializable
object StudyPlannerRoute

@Serializable
object SettingsRoute

@Serializable
data class TimerRoute(val subjectId: Int, val chapterId: Int, val studyPlanId: Int? = null)

@Serializable
object FocusLockSetupRoute
