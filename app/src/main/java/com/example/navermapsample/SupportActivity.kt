package com.example.navermapsample

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SupportActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var helpText: TextView
    private lateinit var frequentlyAskedQuestions: TextView
    private lateinit var questionList: ListView
    private lateinit var supportButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        // 뷰 초기화
        toolbar = findViewById(R.id.toolbar)
        helpText = findViewById(R.id.helpText)
        frequentlyAskedQuestions = findViewById(R.id.frequentlyAskedQuestions)
        questionList = findViewById(R.id.questionList)
        supportButton = findViewById(R.id.supportButton)

        // 툴바 설정
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)     // 뒤로가기 버튼 활성화
            setDisplayShowTitleEnabled(true)    // 툴바 제목 표시 활성화
            title = "고객센터"                   // 툴바 제목 설정
        }

        // 자주하는 질문 목록 설정
        // JSON에서 FAQ 데이터 불러오기
        val faqList = loadFAQList(this)
        val questions = faqList.map { it.question }
        val answers = faqList.map { it.answer }

// ListView에 질문 목록 표시
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, questions)
        questionList.adapter = adapter

// 클릭 시 다이얼로그 또는 새 창으로 답변 표시
        questionList.setOnItemClickListener { _, _, position, _ ->
            val question = questions[position]
            val answer = answers[position]

            val dialog = FAQDialogFragment.newInstance(question, answer)
            dialog.show(supportFragmentManager, "faqDialog")
        }

        // 지원 요청 버튼 클릭 시 SupportRequestActivity로 이동
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportRequestActivity::class.java)
            startActivity(intent)
        }
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // 또는 finish()
        return true
    }

    fun loadFAQList(context: Context): List<FAQItem> {
        val inputStream = context.assets.open("faq.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<List<FAQItem>>() {}.type
        return gson.fromJson(jsonString, listType)
    }


    data class FAQItem(
        val question: String,
        val answer: String
    )


}

