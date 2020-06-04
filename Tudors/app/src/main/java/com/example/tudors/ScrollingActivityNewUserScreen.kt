package com.example.tudors

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tudors.database.TudorUser
import com.example.tudors.database.TudorUserDatabase
import com.example.tudors.databinding.ActivityScrollingNewUserScreenBinding


class ScrollingActivityNewUserScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityScrollingNewUserScreenBinding
    private lateinit var subjectSelected: String
    private var usrID: Long = 0
    private lateinit var db: TudorUserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scrolling_new_user_screen)

        db = TudorUserDatabase.getInstance(applicationContext)

        val spinner: Spinner = binding.scrollView.newUserSubjectSpinner
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.subjects_array,
            android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        binding.scrollView.newUserSaveButton.setOnClickListener {
            saveNewUserProfile(it)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Use the subject at the top of the list.
        subjectSelected = parent!!.getItemAtPosition(0).toString()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item was selected. Save it.
        subjectSelected = parent!!.getItemAtPosition(position).toString()
    }

    private fun saveNewUserProfile(view: View) {

        Thread {
            val newUser = TudorUser()
            newUser.userName = binding.scrollView.newUserNameEditText.toString()
            newUser.userPassword = binding.scrollView.newUserPasswordEditText.toString()
            newUser.userLocation = binding.scrollView.newUserLocationEditText.toString()
            newUser.userPhone = binding.scrollView.newUserPhoneEditText.toString()
            newUser.userEmail = binding.scrollView.newUserEmailEditText.toString()
            newUser.userSubject = subjectSelected
            newUser.isStudent = binding.scrollView.newUserStudentRadioButton.isChecked

            db.tudorUserDatabaseDao.insert(newUser)

            val loggedInUser: TudorUser? = db.tudorUserDatabaseDao.login(newUser.userName, newUser.userPassword)
            if (loggedInUser != null) {
                usrID = loggedInUser.userId
            }
        }.start()


    }

}
