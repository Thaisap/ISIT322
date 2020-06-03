package com.example.tudors

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import  android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val username = findViewById<EditText>(R.id.usernameInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCreateUser = findViewById<Button>(R.id.btnCreateAccount)

        btnCreateUser.setOnClickListener {
            Toast.makeText( this, "One Step to become a member", Toast.LENGTH_SHORT).show()
            var intent = Intent(applicationContext, ScrollingActivityNewUserScreen::class.java)
            startActivity(intent)

        }



     /*   btnCreateAccount.setOnClickListener {
            Toast.makeText( this, "Button Click", Toast.LENGTH_LONG).show()
        } */
        }
}




