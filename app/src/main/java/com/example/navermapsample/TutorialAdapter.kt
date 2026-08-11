package com.example.navermapsample

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TutorialAdapter(
    fragmentActivity: FragmentActivity,
    private val isProfileSet: Boolean
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = mutableListOf<Fragment>()

    init {
        fragments.add(WelcomeFragment())

        if (!isProfileSet) {
            fragments.add(ProfileSetupFragment())
        }

        fragments.add(
            TutorialStepFragment.newInstance(
                gifResId = R.drawable.tutorial_maps,
                description = "복잡한 검색은 이제 그만 ❗ \n 검색창으로 쉽게 경로를 찾을 수 있습니다❗",
                isProfileSet = isProfileSet,
                stepIndex = 0
            )
        )

        fragments.add(
            TutorialStepFragment.newInstance(
                gifResId = R.drawable.tutorial_beacon,
                description = "아직도 강의실을 헤메시나요 ❓ \n 강의실 번호 입력으로 쉽게 찾아가세요❗ ",
                isProfileSet = isProfileSet,
                stepIndex = 1
            )
        )
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}

