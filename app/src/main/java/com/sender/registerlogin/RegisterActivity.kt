package com.sender

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sender.messages.LatestMessagesActivity
import com.sender.models.User
import com.sender.registerlogin.LoginActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var selectPhoto: CircleImageView
    private lateinit var username: EditText
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var registerButton: Button
    private lateinit var alreadyHaveAnAccount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        selectPhoto = findViewById(R.id.selectPhoto_imageView_register)
        username = findViewById(R.id.username_edittext_register)
        email=findViewById(R.id.email_edittext_register)
        password=findViewById(R.id.password_edittext_register)
        registerButton=findViewById(R.id.register_button_register)
        alreadyHaveAnAccount=findViewById(R.id.backToRegister_textview_login)


        selectPhoto.setOnClickListener {
            Log.d("debugMain","Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }

        registerButton.setOnClickListener {
            performRegister()
        }

        alreadyHaveAnAccount.setOnClickListener {
            Log.d("debugMain", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data !=null){
            Log.d("debugMain", "Photo was selected!")
            selectedPhotoUri = data.data
            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)//
            selectPhoto.setImageURI(selectedPhotoUri)
        }
    }

    private fun performRegister(){
        Log.d("debugMain","Email is : ${email.text}")
        Log.d("debugMain","Password is : ${password.text}")

        if(username.text.isEmpty() || email.text.isEmpty()||password.text.isEmpty()){
            Toast.makeText(baseContext, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
            return
        }
        else if (password.text.length<6){
            Toast.makeText(baseContext, "Password should contain at least 6 characters!", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener

                    } else {
                        Toast.makeText(baseContext, "Authentication Success.", Toast.LENGTH_SHORT).show()
                        Log.d("debugMain", "UID : ${task.result.user?.uid}")
                        uploadImageToFirebaseStorage()
                    }
                }
                .addOnFailureListener {
                    Log.d("debugMain", "${it.message}")
                }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri==null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("debugMain", "Image Upload Success ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("debugMain", "Download Link : $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid, username.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("debugMain", "Finally saving user to firebase database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("debugMain", "${it.message}")
            }
    }

}

