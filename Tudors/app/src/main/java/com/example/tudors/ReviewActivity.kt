package com.example.tudors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tudors.database.TudorUserDatabase

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Bring over keys from MainScreenActivity
        val userID = intent.getLongExtra("userID", 0L)
        val tutorID = intent.getLongExtra("tutorID", 0L)

        popFields(tutorID)

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            gotoMainScreen(userID)
        }
    }

    private fun popFields(uid: Long) {
        // Get instance of database
        val db = TudorUserDatabase.getInstance(this)

        val thread = Thread {
            // Fetch User
            val tudorUser = db.tudorUserDatabaseDao.get(uid)

            // Display the data
            if (tudorUser != null) {
                findViewById<TextView>(R.id.textViewTutorName).text = tudorUser.userName
                findViewById<TextView>(R.id.textViewTutorEmail).text = tudorUser.userEmail
                findViewById<TextView>(R.id.textViewTutorPhoneNumber).text = tudorUser.userPhone
                findViewById<TextView>(R.id.textViewTutorSubject).text = tudorUser.userSubject
            }
        }
        thread.start()
    }

    private fun gotoMainScreen(uid: Long) {
        Toast.makeText(this, "Your match has been confirmed", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainScreenActivity::class.java)
        intent.putExtra("primaryKey", uid)
        startActivity(intent)
    }
}
