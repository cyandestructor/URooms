package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.AssignmentResponse
import com.fcfm.poi.yourooms.login.data.models.dao.AssignmentDao
import com.fcfm.poi.yourooms.login.data.models.dao.AssignmentResponseDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EntregarTareaActivity : AppCompatActivity() {
    var assignmentId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_mandar_tarea)

        assignmentId = intent.getStringExtra("assignmentId")
        if (assignmentId != null) {
            loadAssignmentInfo()

            findViewById<Button>(R.id.button_EntregarTarea).setOnClickListener {
                deliverAssignment()
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun loadAssignmentInfo() {
        val assignmentGroupNameText = findViewById<TextView>(R.id.textView_equiponame)
        val assignmentNameText = findViewById<TextView>(R.id.textView_tematarea)
        val assignmentDueDate = findViewById<TextView>(R.id.textView_VencimientoTarea)
        val assignmentDescription = findViewById<TextView>(R.id.textView_instruccionestarea)

        CoroutineScope(Dispatchers.IO).launch {
            val assignment = AssignmentDao().getAssignment(assignmentId!!)

            withContext(Dispatchers.Main) {
                if (assignment != null) {
                    assignmentGroupNameText.text = assignment.group?.name
                    assignmentNameText.text = assignment.name
                    assignmentDescription.text = assignment.description


                    var dueDateString : String? = null
                    if (assignment.dueDate != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        dueDateString = sdf.format(assignment.dueDate)
                    }
                    assignmentDueDate.text = "Vence el: $dueDateString"
                }
                else {
                    assignmentGroupNameText.text = ""
                    assignmentNameText.text = ""
                    assignmentDueDate.text = ""
                    assignmentDescription.text = ""

                    Toast.makeText(applicationContext, "No se pudo cargar la tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deliverAssignment() {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)

            val assignmentResponse = AssignmentResponse(
                null,
                Date(),
                userModel,
                assignmentId
            )

            val id = AssignmentResponseDao().addAssignmentResponse(assignmentResponse)

            withContext(Dispatchers.Main) {
                if (id != null) {
                    Toast.makeText(applicationContext, "¡Felicidades, has ganado puntos por esta tarea!", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "No se pudo entregar la tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}