package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendEmailActivity : AppCompatActivity() {
    private var memberEmails : MutableList<String>? = null

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)

        val membersIds = intent.getStringArrayExtra("ids")
        getMembersEmails(membersIds);

        findViewById<Button>(R.id.btn_send).setOnClickListener {
            if (memberEmails != null) {
                val subjectText = findViewById<EditText>(R.id.mail_subject)
                val messageText = findViewById<EditText>(R.id.mail_body)

                val subject = subjectText.text.toString()
                val message = messageText.text.toString()

                if (subject.isNotEmpty() && message.isNotEmpty()) {

                    val mailIntent = Intent(Intent.ACTION_SENDTO)
                    mailIntent.putExtra(Intent.EXTRA_EMAIL, memberEmails!!.toTypedArray())
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    mailIntent.putExtra(Intent.EXTRA_TEXT, message)
                    mailIntent.data = Uri.parse("mailto:")

                    if (mailIntent.resolveActivity(packageManager) != null) {
                        startActivity(mailIntent)
                    }
                }
            }
        }
    }

    private fun getMembersEmails(membersIds: Array<String>?) {
        memberEmails = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            if (membersIds != null) {
                for (id in membersIds) {
                    val userModel = UserDao().getUser(id)
                    if (userModel?.email != null) {
                        memberEmails!! += userModel.email
                    }
                }
            }
        }
    }
}