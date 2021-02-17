package com.jskaleel.fte.ui.feedback

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.R
import com.jskaleel.fte.databinding.ActivityFeedbackBinding
import com.jskaleel.fte.utils.DeviceUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private val feedbackViewModel: FeedbackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivSend.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val comments = binding.edtComments.text.toString()
            submitFeedBack(name, email, comments)
        }
    }

    private fun submitFeedBack(name: String, email: String, comments: String) {
        val builder = AlertDialog.Builder(this@FeedbackActivity, R.style.CustomAlertDialog)
        builder.setCancelable(false)
        builder.setView(R.layout.loader_view)
        val dialog = builder.create()


        if (name.isBlank() || name.isEmpty()) {
            binding.edtName.error = "Mandatory"
            return
        }

        if (email.isBlank() || email.isEmpty()) {
            binding.edtEmail.error = "Mandatory"
            return
        }

        if (!DeviceUtils.isEmailValid(email)) {
            binding.edtEmail.error = "Invalid e-mail"
            return
        }

        if (comments.isBlank() || comments.isEmpty()) {
            binding.edtComments.error = "Mandatory"
            return
        }

        DeviceUtils.hideSoftKeyboard(this@FeedbackActivity)
        dialog.show()

        feedbackViewModel.submitFeedback(name, email, "[FTE]-$comments")
        feedbackViewModel.viewState.observe(this, {
            if (it) {
                dialog.dismiss()

                binding.edtName.setText("")
                binding.edtEmail.setText("")
                binding.edtComments.setText("")
                Toast.makeText(baseContext, "Feedback submitted", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    /*https://docs.google.com/forms/d/e/1FAIpQLSc_BbE7RfJdUCEgzwGSeiaiUe3ugBdITgJIZY71ED93puqQ3g/formResponse
    * Name : entry_359626196
    * Email: entry_1250945452
    * Comments : entry_1221905149
    * */
}