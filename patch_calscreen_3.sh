sed -i '/import android.app.AlarmManager/d' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
sed -i '/import android.app.PendingIntent/d' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
sed -i '/import android.content.Context/d' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
sed -i '/import android.content.Intent/d' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
sed -i '/import com.example.services.AlarmReceiver/d' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
sed -i '/package com.example.ui.screens.calendar/a \
import android.app.AlarmManager\
import android.app.PendingIntent\
import android.content.Context\
import android.content.Intent\
import com.example.services.AlarmReceiver\
' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt
