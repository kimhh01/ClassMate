package com.example.navermapsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class Info_main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // 툴바 제목 설정
        supportActionBar?.title = "정보"

        // 툴바 내비게이션 아이콘 설정 (홈 버튼)
        toolbar.setNavigationIcon(R.drawable.ic_home)

        // 내비게이션 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // LinearLayoutManager를 사용하여 한 줄에 하나씩 카드가 보이도록 설정
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = CardviewAdapter { position ->
            val url = when (position) {
                0 -> "https://nsu.ac.kr/"
                1 -> "https://www.instagram.com/nsu_31_dream/"
                2 -> "https://www.facebook.com/nsuniv/?locale=ko_KR"
                3 -> "https://blog.naver.com/nsuniversity"
                4 -> "https://www.youtube.com/namseouluniv"
                else -> "https://nsu.ac.kr/"
            }

            val intent = Intent(this, WebView::class.java).apply {
                putExtra("URL", url)
            }
            startActivity(intent)
        }

        // RecyclerView에 어댑터 설정
        recyclerView.adapter = adapter

        // 카드 간의 간격 조정 (ItemDecoration)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}