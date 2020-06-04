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
import com.example.tudors.database.TudorUser
import com.example.tudors.database.TudorUserDatabase
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var db: TudorUserDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        db = TudorUserDatabase.getInstance(applicationContext)

        val username = findViewById<EditText>(R.id.usernameInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCreateUser = findViewById<Button>(R.id.btnCreateAccount)

        btnLogin.setOnClickListener {
            getUserDB(it)
        }

        btnCreateUser.setOnClickListener {
            Toast.makeText(this, "One Step to become a member", Toast.LENGTH_SHORT).show()
            var intent = Intent(applicationContext, ScrollingActivityNewUserScreen::class.java)
            startActivity(intent)

        }

        /*   btnCreateAccount.setOnClickListener {
               Toast.makeText( this, "Button Click", Toast.LENGTH_LONG).show()
           } */
    }

    private fun getUserDB(view : View) {


        val user = TudorUser()
        val thread = Thread {
            val loggedInUser: TudorUser? =
                db.tudorUserDatabaseDao.login(
                    usernameInput.text.toString(),
                    passwordInput.text.toString()
                )
            if (loggedInUser != null) {
                user.userId = loggedInUser.userId

            }
        }
        thread.start()

        Toast.makeText(this, "You are in the main screen", Toast.LENGTH_SHORT).show()

        val intent = Intent(applicationContext, MainScreenActivity::class.java)
        intent.putExtra("primaryKey", user.userId)
        startActivity(intent);




    }

}

