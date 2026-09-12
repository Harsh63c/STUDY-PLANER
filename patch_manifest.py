with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

if 'android.permission.USE_EXACT_ALARM' not in content:
    content = content.replace(
        '<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />',
        '<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />\n    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />'
    )

# Add BOOT_COMPLETED receiver
if 'android.intent.action.BOOT_COMPLETED' not in content:
    boot_receiver = """        <receiver android:name=".services.BootReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.LOCKED_BOOT_COMPLETED" />
            </intent-filter>
        </receiver>"""
    
    content = content.replace(
        '<receiver android:name=".services.AlarmReceiver" android:exported="false" />',
        '<receiver android:name=".services.AlarmReceiver" android:exported="false" />\n' + boot_receiver
    )
    
    # Add RECEIVE_BOOT_COMPLETED permission
    content = content.replace(
        '<uses-permission android:name="android.permission.VIBRATE" />',
        '<uses-permission android:name="android.permission.VIBRATE" />\n    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />'
    )

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
