package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.UserAssignmentListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.UserAssignment
import com.fcfm.poi.yourooms.login.data.models.dao.UserAssignmentDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Fragmento_Tareas: Fragment(R.layout.fragmento_tareas) {
    private lateinit var userAssignmentsRecyclerView : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAssignmentsRecyclerView = view.findViewById(R.id.Lista_tareas)
        userAssignmentsRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        loadUserAssignments()
    }

    private fun loadUserAssignments() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = AuthenticationManager().getCurrentUser()
            if (currentUser != null) {
                val userId = currentUser.uid
                val userAssignments = UserAssignmentDao().getUserAssignments(userId)

                val userAssignmentListAdapter = UserAssignmentListAdapter(userAssignments)

                userAssignmentListAdapter.setOnClickListener {
                    val i = Intent(activity, EntregarTareaActivity::class.java)
                    val userAssignment = it.tag as UserAssignment
                    i.putExtra("assignmentId", userAssignment.id)
                    startActivity(i)
                }

                withContext(Dispatchers.Main) {
                    userAssignmentsRecyclerView.adapter = userAssignmentListAdapter
                }
            }
        }
    }

}