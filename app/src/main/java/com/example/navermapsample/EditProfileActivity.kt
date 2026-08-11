package com.example.navermapsample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import android.app.AlertDialog


class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nicknameEditText: EditText
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // 툴바 초기화 및 설정
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar) // 툴바 ID
        setSupportActionBar(toolbar)
        supportActionBar?.title = "프로필 수정" // 툴바 제목 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화

        profileImageView = findViewById(R.id.profileImageView)
        nicknameEditText = findViewById(R.id.nicknameEditText)
        val saveButton: Button = findViewById(R.id.saveButton)

        val sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        // 닉네임 설정
        nicknameEditText.setText(sharedPref.getString("nickname", ""))

        // 프로필 이미지 설정
        val savedPath = sharedPref.getString("profileImagePath", null)
        if (!savedPath.isNullOrEmpty()) {
            val imageFile = File(savedPath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                profileImageView.setImageBitmap(bitmap)
            } else {
                profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
            }
        } else {
            profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
        }

        // 이미지 클릭 시 갤러리 열기
        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // 저장 버튼 클릭
        saveButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()

            // 닉네임 유효성 검사
            when {
                nickname.isEmpty() -> {
                    nicknameEditText.error = "닉네임을 입력해주세요"
                    return@setOnClickListener
                }
                nickname.length > 10 -> {
                    nicknameEditText.error = "닉네임은 최대 10자까지 입력 가능합니다"
                    return@setOnClickListener
                }
            }

            val editor = sharedPref.edit()
            editor.putString("nickname", nickname)

            selectedImageUri?.let { uri ->
                // 내부 저장소에 이미지 저장
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(filesDir, "profile.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // 파일 경로 저장
                editor.putString("profileImagePath", file.absolutePath)
            }

            editor.putBoolean("isProfileSet", true)
            editor.apply()

            setResult(Activity.RESULT_OK)
            finish()
        }

        val resetButton: Button = findViewById(R.id.resetButton)

        resetButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("초기화")
                .setMessage("정말 삭제하실건가요?")
                .setPositiveButton("네") { _, _ ->
                    val editor = sharedPref.edit()
                    editor.remove("nickname")
                    editor.remove("profileImagePath")
                    editor.putBoolean("isProfileSet", false)
                    editor.apply()

                    // 프로필 초기화
                    nicknameEditText.setText("")
                    profileImageView.setImageResource(R.drawable.ic_baseline_person_24)

                    // 저장된 이미지 파일 삭제
                    val file = File(filesDir, "profile.jpg")
                    if (file.exists()) file.delete()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

    }

    // 👉 이미지 선택 후 즉시 보기만!
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImageView.setImageURI(selectedImageUri)  // 보기만 설정
        }
    }


    // 뒤로가기 버튼 클릭 시 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // 뒤로가기 버튼을 눌렀을 때 이전 액티비티로 돌아가기
        return true
    }
}
