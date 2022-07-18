package com.sender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var registerButton: Button
    private lateinit var alreadyHaveAnAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email=findViewById(R.id.email_edittext_register)
        password=findViewById(R.id.password_edittext_register)
        registerButton=findViewById(R.id.register_button_register)
        alreadyHaveAnAccount=findViewById(R.id.backToRegister_textview_login)

        

        registerButton.setOnClickListener {
            Log.d("MainActivity","Email is : ${email.text}")
            Log.d("MainActivity","Password is : ${password.text}")
        }
        alreadyHaveAnAccount.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}