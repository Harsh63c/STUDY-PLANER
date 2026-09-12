sed -i '/val selectedDate by viewModel.selectedDateMillis.collectAsStateWithLifecycle()/i \
    val context = LocalContext.current' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
