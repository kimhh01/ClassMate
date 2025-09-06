package com.example.navermapsample

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class Help : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로 가기 버튼 활성화
        supportActionBar?.title = "도움말" // 툴바 제목 설정

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button1.setOnClickListener {
            showAlertDialog("건물위치는 다음과 같이 검색할 수 있습니다. \n" +"\n"+"1. 검색창에 원하는 건물의 이름을 입력합니다.\n\n"
            +"2. 키보드의 검색 버튼이나 검색창 옆 검색버튼을 클릭합니다. \n\n" + "3. 검색한 건물의 마커가 표시되고 경로가 표시됩니다.")
        }

        button2.setOnClickListener {
            showAlertDialog("강의실 번호는 다음과 같이 검색할 수 있습니다. \n\n"+"1. 위의 메뉴 버튼을 클릭합니다.\n\n"
            +"2.'실외에서 강의실 창기' 창을 클릭합니다.\n\n"+"3. 비콘이 검색이 완료되면 찾고자 하는 강의실 번호를 입력합니다.\n\n"
            +"4. 로딩이 완료되면 찾고자 하는 강의실 까지의 경로가 표시됩니다.")
        }

        button3.setOnClickListener {
            showAlertDialog("현재 강의실을 찾는데 사용중인 알고리즘은 완성형이 아닌 베타버전입니다.\n\n"+"추후 속도를 개선하여 출시할 예정입니다")
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("이해하였습니다") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // 뒤로 가기 버튼 클릭 시 동작
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
