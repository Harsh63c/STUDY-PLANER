sed -i '/fun CalendarScreen(/i \
import android.app.AlarmManager\
import android.app.PendingIntent\
import android.content.Context\
import android.content.Intent\
import com.example.services.AlarmReceiver\
' app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt

cat << 'INNER_EOF' >> app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt

fun scheduleTaskAlarm(context: Context, task: com.example.data.models.Task) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("TASK_ID", task.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, task.id * 10, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)
    
    if (task.reminderTimeMillis != null && task.reminderTimeMillis > System.currentTimeMillis() && !task.isCompleted) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderTimeMillis, pendingIntent)
    }
}
INNER_EOF
