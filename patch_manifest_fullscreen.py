import re

with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

# Add USE_FULL_SCREEN_INTENT
if 'android.permission.USE_FULL_SCREEN_INTENT' not in content:
    content = content.replace('<uses-permission android:name="android.permission.INTERNET" />',
                              '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />')

# Add AlarmRingingActivity
activity_xml = """        <activity
            android:name=".ui.screens.studyplanner.AlarmRingingActivity"
            android:exported="false"
            android:showOnLockScreen="true"
            android:theme="@style/Theme.MyApplication" />"""

if 'AlarmRingingActivity' not in content:
    content = content.replace('        <receiver android:name=".services.AlarmReceiver"', 
                              activity_xml + '\n        <receiver android:name=".services.AlarmReceiver"')

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
