package com.example

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class SplashActivity : ComponentActivity() {

    private val COUNTER_TIME = 2L // Shorter wait time for better UX
    private var secondsRemaining: Long = 0
    private var isAdShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background), 
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Study Planner",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        createTimer(COUNTER_TIME)
    }

    private fun createTimer(seconds: Long) {
        val countDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1
            }

            override fun onFinish() {
                secondsRemaining = 0
                val application = application as? MyApplication
                
                if (application != null) {
                    application.showAdIfAvailable(this@SplashActivity) {
                        if (!isAdShown) {
                            isAdShown = true
                            startMainActivity()
                        }
                    }
                } else {
                    if (!isAdShown) {
                        isAdShown = true
                        startMainActivity()
                    }
                }
            }
        }
        countDownTimer.start()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
