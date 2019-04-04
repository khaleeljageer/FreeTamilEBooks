package com.jskaleel.fte.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jskaleel.fte.R
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {
    companion object {
        fun getInstance(): FeedbackFragment {
            return FeedbackFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ivSend.setOnClickListener{
            submitFeedBack()
        }
    }

    private fun submitFeedBack() {
        val email = edtEmail.text.toString()
        val name = edtName.text.toString()
        val comments = edtComments.text.toString()

        if (name.isBlank() || name.isEmpty()) {
            edtName.error = "Mandatory"
            return
        }

        if (email.isBlank() || email.isEmpty()) {
            edtEmail.error = "Mandatory"
            return
        }

        if (comments.isBlank() || comments.isEmpty()) {
            edtComments.error = "Mandatory"
            return
        }
    }
}