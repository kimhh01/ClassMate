package com.example.navermapsample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val doNotShowAgain = prefs.getBoolean("doNotShowAgain", false)

        val intent = if (doNotShowAgain) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, TutorialActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
