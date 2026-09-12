sed -i '/super.onCreate(savedInstanceState)/a \
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {\
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {\
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)\
            }\
        }' app/src/main/java/com/example/MainActivity.kt
