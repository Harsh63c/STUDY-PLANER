import re

with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

content = content.replace("private var mediaPlayer: MediaPlayer? = null", "private var mediaPlayer: MediaPlayer? = null\n    private var countdownJob: Job? = null")

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
