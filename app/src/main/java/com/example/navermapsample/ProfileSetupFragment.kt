package com.example.navermapsample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

class ProfileSetupFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var nicknameEditText: EditText
    private var imageUri: Uri? = null

    // 이미지 선택 런처
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            imageView.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile_setup, container, false)

        imageView = view.findViewById(R.id.profileImageView)
        nicknameEditText = view.findViewById(R.id.nicknameEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        // 이미지 선택 클릭
        imageView.setOnClickListener {
            if (hasImagePermission()) {
                openGallery()
            } else {
                requestImagePermission()
            }
        }

        // 저장 버튼 클릭
        saveButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            if (nickname.isEmpty()) {
                nicknameEditText.error = "닉네임을 입력해주세요"
                return@setOnClickListener
            }
            else if (nickname.length > 10) {
            nicknameEditText.error = "닉네임은 최대 10자까지 입력 가능합니다"
            return@setOnClickListener
            }

            // SharedPreferences에 저장
            val prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("nickname", nickname)
                .putBoolean("isProfileSet", true)
                .apply()

            // 이미지 저장 (내부저장소)
            saveProfileImageToInternalStorage(imageUri)

            // 키보드 내리기
            hideKeyboard(nicknameEditText)

            // 튜토리얼 다음 페이지로 이동
            (activity as? TutorialActivity)?.completeProfileSetup()
        }

        return view
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun hasImagePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun requestImagePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSION)
    }

    private fun saveProfileImageToInternalStorage(uri: Uri?) {
        uri ?: return

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val file = File(requireActivity().filesDir, "profile.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }
}
