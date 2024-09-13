package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        databaseHelper = DatabaseHelper(this)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = databaseHelper.createUser(username, password)

            if (userId != null) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
