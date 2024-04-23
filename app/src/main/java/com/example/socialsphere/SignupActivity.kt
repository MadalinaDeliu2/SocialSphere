package com.example.socialsphere

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.socialsphere.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")

        binding.signupButton.setOnClickListener{
            val signupUsername = binding.signupUsername.text.toString()
            val signupEmail = binding.signupEmail.text.toString()
            val signupPhoneNumber = binding.signupPhoneNumber.text.toString()
            val signupPassword = binding.signupPassword.text.toString()


            if(signupUsername.isNotEmpty() && signupEmail.isNotEmpty() && signupPhoneNumber.isNotEmpty() && signupPassword.isNotEmpty()){
                signupUser(signupUsername, signupEmail, signupPhoneNumber, signupPassword)
            }else{
                Toast.makeText(this@SignupActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun signupUser(username: String, email: String, phoneNumber: String, password: String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                if(!dataSnapshot.exists()){
                    val id = databaseReference.push().key
                    val userData = UserData(id, username, email, phoneNumber, password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                } else{
                    Toast.makeText(this@SignupActivity, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignupActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}