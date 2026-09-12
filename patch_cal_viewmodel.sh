sed -i 's/fun addTask(title: String, startTime: Long?, endTime: Long?, reminderTime: Long?, dateMillis: Long)/fun addTask(title: String, startTime: Long?, endTime: Long?, reminderTime: Long?, dateMillis: Long, onInserted: (Task) -> Unit = {})/g' app/src/main/java/com/example/viewmodel/CalendarViewModel.kt
sed -i 's/repository.insertTask(/val id = repository.insertTask(/g' app/src/main/java/com/example/viewmodel/CalendarViewModel.kt
sed -i '/val id = repository.insertTask(/,/)/!b;//!d;/)/a\
            onInserted(Task(id = id.toInt(), title = title, dateMillis = getStartOfDay(dateMillis), startTimeMillis = startTime, endTimeMillis = endTime, reminderTimeMillis = reminderTime))' app/src/main/java/com/example/viewmodel/CalendarViewModel.kt
