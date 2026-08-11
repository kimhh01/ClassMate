package com.example.navermapsample

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.navermapsample.databinding.FragmentFaqDialogBinding

class FAQDialogFragment : DialogFragment() {

    private var _binding: FragmentFaqDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_QUESTION = "question"
        private const val ARG_ANSWER = "answer"

        fun newInstance(question: String, answer: String): FAQDialogFragment {
            val fragment = FAQDialogFragment()
            val args = Bundle().apply {
                putString(ARG_QUESTION, question)
                putString(ARG_ANSWER, answer)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val question = arguments?.getString(ARG_QUESTION) ?: ""
        val answer = arguments?.getString(ARG_ANSWER) ?: ""

        binding.tvQuestion.text = question
        binding.tvAnswer.text = answer

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
