package com.example.navermapsample

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class TutorialActivity : AppCompatActivity() {

    internal lateinit var viewPager: ViewPager2
    private lateinit var prefs: SharedPreferences
    private var isProfileSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // "다시 보지 않기" 체크 여부 확인
        val tutorialPrefs = getSharedPreferences("TutorialPrefs", MODE_PRIVATE)
        val doNotShowAgain = tutorialPrefs.getBoolean("doNotShowAgain", false)

        if (doNotShowAgain) {
            navigateToHome()  // 바로 홈으로 이동
            return
        }

        setContentView(R.layout.activity_tutorial)

        prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        isProfileSet = prefs.getBoolean("isProfileSet", false)

        viewPager = findViewById(R.id.viewPager)

        // Adapter 설정
        viewPager.adapter = TutorialAdapter(this, isProfileSet)

        // 항상 첫 페이지에서 시작
        viewPager.setCurrentItem(0, false)
    }


    /**
     * 프로필 설정 완료 시 호출됨
     */
    fun completeProfileSetup() {
        prefs.edit().putBoolean("isProfileSet", true).apply()
        isProfileSet = true

        // TutorialAdapter를 새로 설정하여 ProfileSetupFragment 제거
        viewPager.adapter = TutorialAdapter(this, isProfileSet)

        // 튜토리얼 설명 1로 이동
        viewPager.setCurrentItem(1, false)
    }

    /**
     * 홈으로 이동
     */
    fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 다음 페이지로 이동
     */
    fun goToNextPage() {
        val nextItem = viewPager.currentItem + 1
        val totalPages = viewPager.adapter?.itemCount ?: 0
        if (nextItem < totalPages) {
            viewPager.setCurrentItem(nextItem, true)
        }
    }

    /**
     * 이전 페이지로 이동
     */
    fun goToPreviousPage() {
        val previousItem = viewPager.currentItem - 1
        if (previousItem >= 0) {
            viewPager.setCurrentItem(previousItem, true)
        }
    }

    fun getIsProfileSet(): Boolean {
        return isProfileSet
    }
}
