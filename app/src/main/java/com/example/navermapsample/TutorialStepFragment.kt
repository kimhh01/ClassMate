package com.example.navermapsample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TutorialStepFragment : Fragment() {

    private var gifResId: Int = 0
    private var description: String? = null
    private var isProfileSet: Boolean = false
    private var stepIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gifResId = it.getInt(ARG_GIF_RES_ID)
            description = it.getString(ARG_DESCRIPTION)
            isProfileSet = it.getBoolean(ARG_IS_PROFILE_SET)
            stepIndex = it.getInt(ARG_STEP_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutorial_step, container, false)

        val gifImageView = view.findViewById<ImageView>(R.id.gifImageView)
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val prevButton = view.findViewById<Button>(R.id.prevButton)
        val actionButton = view.findViewById<Button>(R.id.actionButton)
        val dontShowAgainCheckBox = view.findViewById<CheckBox>(R.id.dontShowAgainCheckBox)

        // Glide를 사용하여 GIF 로드 및 모서리 둥글게 처리
        val cornerRadiusPx = 17 // 모서리 반지름 값 (px)
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(cornerRadiusPx))

        Glide.with(this)
            .asGif()
            .load(gifResId)
            .apply(requestOptions)
            .into(gifImageView)

        descriptionTextView.text = description

        val tutorialActivity = activity as? TutorialActivity

        // '이전' 버튼 로직: 항상 보이고 클릭 리스너 설정
        prevButton.visibility = View.VISIBLE // 항상 보이도록 설정
        prevButton.setOnClickListener {
            tutorialActivity?.goToPreviousPage() // 부모 액티비티의 이전 페이지 이동 함수 호출
        }
        // 참고: 첫 번째 스텝에서 goToPreviousPage() 호출 시 동작은 TutorialActivity 구현에 따라 달라집니다.


        // '다음'/'완료' 버튼 및 체크박스 로직
        val totalStepCount = if (isProfileSet) 2 else 3
        val isLastStep = stepIndex == totalStepCount - 1

        if (isLastStep) {
            dontShowAgainCheckBox.visibility = View.VISIBLE
            actionButton.text = "시작하기"
            actionButton.setOnClickListener {
                if (dontShowAgainCheckBox.isChecked) {
                    saveDoNotShowAgain()
                }
                tutorialActivity?.navigateToHome()
            }
        } else {
            dontShowAgainCheckBox.visibility = View.GONE
            actionButton.text = "다음"
            actionButton.setOnClickListener {
                tutorialActivity?.goToNextPage()
            }
        }

        return view
    }

    private fun saveDoNotShowAgain() {
        val sharedPreferences = requireContext().getSharedPreferences("TutorialPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("doNotShowAgain", true)
            apply()
        }
    }

    companion object {
        private const val ARG_GIF_RES_ID = "gifResId"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IS_PROFILE_SET = "isProfileSet"
        private const val ARG_STEP_INDEX = "stepIndex"

        fun newInstance(
            gifResId: Int,
            description: String,
            isProfileSet: Boolean,
            stepIndex: Int
        ): TutorialStepFragment {
            val fragment = TutorialStepFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_GIF_RES_ID, gifResId)
                putString(ARG_DESCRIPTION, description)
                putBoolean(ARG_IS_PROFILE_SET, isProfileSet)
                putInt(ARG_STEP_INDEX, stepIndex)
            }
            return fragment
        }
    }
}