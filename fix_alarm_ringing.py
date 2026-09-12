with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "r") as f:
    content = f.read()

content = content.replace("""                    )
    }""", """                    )
                }
            }
        }
    }""")

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "w") as f:
    f.write(content)
