package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already signed in
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isSignedIn = sharedPref.getBoolean("isSignedIn", false)

        if (isSignedIn) {
            val userId = sharedPref.getLong("USER_ID", -1)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpButton)
        databaseHelper = DatabaseHelper(this)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = databaseHelper.getUserId(username, password)
            if (userId != null) {
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isSignedIn", true)
                editor.putLong("USER_ID", userId)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
