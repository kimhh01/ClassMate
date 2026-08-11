package com.example.navermapsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BlankFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View(context)
        view.post {
            (activity as? TutorialActivity)?.let {
                it.viewPager.setCurrentItem(2, true) // 자동으로 2번 페이지로 이동
            }
        }
        return view
    }
}
