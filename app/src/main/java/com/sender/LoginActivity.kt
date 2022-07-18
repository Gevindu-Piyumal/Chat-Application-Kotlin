package com.sender

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var backToRegister:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email_edittext_login)
        password = findViewById(R.id.password_edittext_login)
        loginButton = findViewById(R.id.login_button_login)
        backToRegister=findViewById(R.id.backToRegister_textview_login)

        loginButton.setOnClickListener {
            Log.d("LoginActivity","Attempt login with email/pw: ${email.text}/***")
        }

        backToRegister.setOnClickListener {
            finish()
        }
    }
}