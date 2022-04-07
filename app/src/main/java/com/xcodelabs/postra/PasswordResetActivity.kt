package com.xcodelabs.postra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class PasswordResetActivity : AppCompatActivity()
{
    lateinit var mEmail : EditText
    lateinit var mForgetBtn : Button
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        mEmail = findViewById(R.id.editTextEmail)
        mForgetBtn = findViewById(R.id.buttonReset)

        mAuth = FirebaseAuth.getInstance()

        mForgetBtn.setOnClickListener {
            val email = mEmail.text.toString()
            if (email.isEmpty())
            {
                mEmail.error = "Enter email"
                return@setOnClickListener
            }

            forgetPassword(email)
        }
    }


    private fun forgetPassword(email: String)
    {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {  task: Task<Void> ->
            if (task.isComplete)
            {
                val loginIntent = Intent(this, SignInActivity::class.java)
                startActivity(loginIntent)
                Toast.makeText(this,"please check your email inbox and reset your password",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}