package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var taskEditText: EditText
    private lateinit var addButton: Button
    private lateinit var listView: ListView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var cursorAdapter: SimpleCursorAdapter
    private lateinit var signOutButton: Button
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskEditText = findViewById(R.id.taskEditText)
        addButton = findViewById(R.id.addButton)
        listView = findViewById(R.id.listView)
        signOutButton = findViewById(R.id.signOutButton)
        databaseHelper = DatabaseHelper(this)

        // Get user ID from Intent
        userId = intent.getLongExtra("USER_ID", -1)

        // Setup ListView adapter
        val from = arrayOf(DatabaseHelper.COLUMN_TASK)
        val to = intArrayOf(android.R.id.text1)
        cursorAdapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, databaseHelper.getAllTasks(userId), from, to, 0)
        listView.adapter = cursorAdapter

        addButton.setOnClickListener {
            val task = taskEditText.text.toString().trim()
            if (task.isNotEmpty()) {
                databaseHelper.addTask(userId, task)
                taskEditText.text.clear()
                cursorAdapter.changeCursor(databaseHelper.getAllTasks(userId))
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val cursor = cursorAdapter.cursor
            cursor.moveToPosition(position)
            val taskId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            val taskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK))
            showEditDeleteDialog(taskId, taskName)
        }

        signOutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("isSignedIn", false)
            editor.remove("USER_ID") // Clear user ID
            editor.apply()

            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showEditDeleteDialog(taskId: Long, taskName: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Edit or Delete Task")

        val input = EditText(this)
        input.setText(taskName)

        dialogBuilder.setView(input)

        dialogBuilder.setPositiveButton("Update") { _, _ ->
            val updatedTask = input.text.toString().trim()
            if (updatedTask.isNotEmpty()) {
                databaseHelper.updateTask(taskId, updatedTask)
                cursorAdapter.changeCursor(databaseHelper.getAllTasks(userId))
            } else {
                Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Delete") { _, _ ->
            databaseHelper.deleteTask(taskId)
            cursorAdapter.changeCursor(databaseHelper.getAllTasks(userId))
        }

        dialogBuilder.setNeutralButton("Cancel", null)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}
