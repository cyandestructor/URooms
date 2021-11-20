package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Assignment
import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.dao.AssignmentDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CrearTareaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_creartarea)

        findViewById<Button>(R.id.btn_AsignTar).setOnClickListener {
            addAssignment(it)
        }

        findViewById<EditText>(R.id.edit_assignment_due_date).setOnClickListener {
            showDatePickerDialog(it as EditText)
        }
    }

    private fun showDatePickerDialog(it: EditText) {
        val fragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener {_, year, month, day ->
            val selectedDate = year.toString() + "-" + twoDigits(month + 1) + "-" + twoDigits(day)
            it.setText(selectedDate)
        })

        fragment.show(supportFragmentManager, "datePicker")
    }

    private fun twoDigits(n: Int): String? {
        return if (n <= 9) "0$n" else n.toString()
    }

    @SuppressLint("SimpleDateFormat")
    private fun stringToDate(input: String): Date? {
        val formatter = SimpleDateFormat("yyyy-mm-dd")
        return  formatter.parse(input)
    }

    private fun addAssignment(it: View) {
        it.isEnabled = false

        val titleEdit = findViewById<EditText>(R.id.edit_assignment_name)
        val descriptionEdit = findViewById<EditText>(R.id.edit_assignment_description)
        val scoreEdit = findViewById<EditText>(R.id.edit_assignment_score)
        val dueDateEdit = findViewById<EditText>(R.id.edit_assignment_due_date)

        val title = titleEdit.text.toString()
        val description = descriptionEdit.text.toString()
        val score = scoreEdit.text.toString()
        val dueDateString = dueDateEdit.text.toString()
        val dueDate = stringToDate(dueDateString)

        val channelId = intent.getStringExtra("channelId") ?: return
        val channelName = intent.getStringExtra("channelName") ?: return

        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            var assignmentId : String? = null

            val userModel = UserDao().getUser(userId)

            if (userModel != null) {
                val channel = Channel(
                    channelId,
                    channelName
                )

                val assignment = Assignment(
                    null,
                    title,
                    description,
                    dueDate,
                    Date(),
                    userModel,
                    channel,
                    false,
                    score.toInt()
                )

                assignmentId = AssignmentDao().addAssignment(assignment)
            }

            withContext(Dispatchers.Main) {
                if (assignmentId != null) {
                    titleEdit.text.clear()
                    descriptionEdit.text.clear()
                    scoreEdit.text.clear()
                    dueDateEdit.text.clear()

                    Toast.makeText(applicationContext, "Se ha generado la tarea", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "No se pudo crear la tarea", Toast.LENGTH_SHORT).show()
                }

                it.isEnabled = true
            }
        }
    }
}