package com.example.navermapsample


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SupportRequestActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var categorySpinner: Spinner
    private lateinit var messageEditText: EditText
    private lateinit var addImageButton: Button
    private lateinit var sendButton: Button
    private lateinit var imageContainer: LinearLayout
    private val selectedImages = ArrayList<Uri>() // 최대 5개 이미지 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_request)

        // 뷰 초기화
        toolbar = findViewById(R.id.toolbar)
        categorySpinner = findViewById(R.id.categorySpinner)
        messageEditText = findViewById(R.id.messageEditText)
        addImageButton = findViewById(R.id.addImageButton)
        sendButton = findViewById(R.id.sendButton)
        imageContainer = findViewById(R.id.imageContainer)

        // 툴바 설정
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)     // 뒤로가기 버튼 활성화
            setDisplayShowTitleEnabled(true)    // 툴바 제목 표시 활성화
            title = "요청 보내기"                // 툴바 제목 설정
        }

        // 문의 유형 설정
        val categories = arrayOf("문의 유형을 선택하세요", "앱 사용 방법", "계정 관련", "기기 문제")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        // 이미지 추가 버튼 클릭
        addImageButton.setOnClickListener {
            if (selectedImages.size < 5) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 다중 선택 허용
                }
                startActivityForResult(intent, IMAGE_PICK_CODE)
            } else {
                Toast.makeText(this, "최대 5개의 이미지만 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 보내기 버튼 클릭
        sendButton.setOnClickListener {
            sendEmail()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                // 다중 이미지 선택
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    if (selectedImages.size < 5) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        if (!selectedImages.contains(imageUri)) { // 중복 추가 방지
                            selectedImages.add(imageUri)
                        }
                    }
                }
            } else if (data?.data != null) {
                // 단일 이미지 선택
                if (selectedImages.size < 5 && !selectedImages.contains(data.data!!)) {
                    selectedImages.add(data.data!!)
                }
            }
            updateImagePreview()
        }
    }

    private fun updateImagePreview() {
        imageContainer.removeAllViews() // 기존 이미지 초기화

        for (i in selectedImages.indices) {
            val imageUri = selectedImages[i]

            // 이미지 뷰 동적 생성
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                    setMargins(10, 10, 10, 10)
                }
                setImageURI(imageUri)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // X 버튼 동적 생성
            val deleteButton = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                    setMargins(5, 5, 5, 5)
                }
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                // 클릭 시 해당 이미지 삭제
                setOnClickListener {
                    selectedImages.removeAt(i)
                    updateImagePreview() // 다시 업데이트
                }
            }

            // 이미지와 X 버튼을 감싸는 레이아웃
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                addView(imageView)
                addView(deleteButton)
            }

            imageContainer.addView(container)
        }
    }

    private fun sendEmail() {
        val recipient = "khs10049731@gmail.com"
        val subject = "문의가 접수되었습니다!"

        val message = "문의 유형: ${categorySpinner.selectedItem}"
        val message2 = messageEditText.text.toString().trim() // 앞뒤 공백 제거
        val selectedCategory = categorySpinner.selectedItem.toString()

        // 문의 유형이 선택되지 않은 경우
        if (selectedCategory == "문의 유형을 선택하세요") {
            Toast.makeText(this, "문의 유형을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (message2.isEmpty()) {
            Toast.makeText(this, "문의 내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            messageEditText.requestFocus()
            return
        }

        val mailSender = MailSender(this)
        mailSender.sendEmail(recipient, subject, message, message2, selectedImages)
    }



    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // 또는 finish()
        return true
    }
}
