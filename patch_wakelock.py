import re

with open('app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt', 'r') as f:
    content = f.read()

content = content.replace(
'''        // Wake up screen and show when locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {''',
'''        // Wake up screen and show when locked
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {'''
)

with open('app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt', 'w') as f:
    f.write(content)
