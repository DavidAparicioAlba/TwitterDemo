package com.example.twitterdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*


class Register : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
    }

    fun registerToFirebase(email:String, password:String){
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("message", "account created")
                    val user = mAuth!!.currentUser
                    var intent = Intent(this, initActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("message", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "this account already exists or is not valid.",
                        Toast.LENGTH_SHORT).show()

                }

            }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.getCurrentUser()


    }

    fun checkRegister(view: View){
        Log.d("register user", email.text.toString())
        Log.d("register password", password.text.toString())
        registerToFirebase(editTextEmail.text.toString(), editTextEmail.text.toString())
    }
}
