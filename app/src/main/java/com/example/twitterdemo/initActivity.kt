package com.example.twitterdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class initActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
    }


    fun toLogin(view: View){
        var intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun toRegister(view: View){
        var intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}
