package com.sender.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sender.R
import com.sender.RegisterActivity
import com.sender.messages.LatestMessagesActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var backToRegister:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        email = findViewById(R.id.email_edittext_login)
        password = findViewById(R.id.password_edittext_login)
        loginButton = findViewById(R.id.login_button_login)
        backToRegister=findViewById(R.id.backToRegister_textview_login)

        loginButton.setOnClickListener {
            if(email.text.isEmpty()||password.text.isEmpty()){
                Toast.makeText(baseContext, "Fill the Email & Password fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                performLogin()
            }
        }

        backToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        Log.d("LoginActivity","Attempt login with email/pw: ${email.text}/***")
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(baseContext, "Sign in Failed.", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener

                } else {
                    Toast.makeText(baseContext, "Sign in Success.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Log.d("debugMain", "UID : ${task.result.user?.uid}")
                }
            }
            .addOnFailureListener {
                Log.d("debugMain", "${it.message}")
            }
    }
}