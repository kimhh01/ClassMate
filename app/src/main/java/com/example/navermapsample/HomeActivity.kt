package com.example.navermapsample

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 지도 버튼
        val mapButton: ImageButton = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // 기존 MainActivity로 이동
            startActivity(intent)
        }

        // 시간표 버튼
        val timetableButton: Button = findViewById(R.id.timetableButton)
        timetableButton.setOnClickListener {
            val intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
        }

        // 강의실 찾기 버튼
        val roomFinderButton: Button = findViewById(R.id.roomFinderButton)
        roomFinderButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}
