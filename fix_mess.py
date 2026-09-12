import re

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "r") as f:
    content = f.read()

content = re.sub(r'Text\(Alarm 1: \$\{alarm1Time\?\.let \{ timeFormat\.format\(it\) \} \?: "Not set"\}"Not set"\}\"\)', 
                 r'Text("Alarm 1: ${alarm1Time?.let { timeFormat.format(it) } ?: \"Not set\"}")', content)

content = re.sub(r'Text\(Alarm 2: \$\{alarm2Time\?\.let \{ timeFormat\.format\(it\) \} \?: "Not set"\}"Not set"\}\"\)', 
                 r'Text("Alarm 2: ${alarm2Time?.let { timeFormat.format(it) } ?: \"Not set\"}")', content)

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "w") as f:
    f.write(content)

