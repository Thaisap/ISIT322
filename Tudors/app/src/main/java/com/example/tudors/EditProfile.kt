package com.example.tudors

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tudors.database.TudorUser
import com.example.tudors.database.TudorUserDatabase
import com.example.tudors.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var subjectSelected: String
    private var usrID: Long = 0
    private lateinit var db: TudorUserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_edit_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        db = TudorUserDatabase.getInstance(applicationContext)

        // Get the primaryKey from the intent
        usrID = intent.getLongExtra("primaryKey", 0L)

        val spinner: Spinner = binding.editProfileSubjectSpinner
        spinner.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this, R.array.subjects_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        binding.editProfileSaveButton.setOnClickListener {
            saveEditedProfile()
        }

        binding.editProfileDeleteButton.setOnClickListener {
            deleteProfile()
        }

        // Populate all the profile fields
        populateProfile()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Use the subject at the top of the list.
        // subjectSelected = parent!!.getItemAtPosition(0).toString()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item was selected. Save it.
        subjectSelected = parent!!.getItemAtPosition(position).toString()
    }

    private fun populateProfile() {
        val thread = Thread {

            val user = db.tudorUserDatabaseDao.get(usrID)

            if (user != null) {
                binding.editProfileNameEditText.setText(user.userName)
                binding.editProfilePasswordEditText.setText(user.userPassword)
                binding.editProfileLocationEditText.setText(user.userLocation)
                binding.editProfilePhoneEditText.setText(user.userPhone)
                binding.editProfileEmailEditText.setText(user.userEmail)
                if (user.isStudent) {
                    binding.editProfileStudentRadioButton.isChecked = true
                } else {
                    binding.editProfileTutorRadioButton.isChecked = true
                }
                subjectSelected = user.userSubject

                ArrayAdapter.createFromResource(this, R.array.subjects_array, android.R.layout.simple_spinner_item
                ).also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.editProfileSubjectSpinner.adapter = adapter
                    binding.editProfileSubjectSpinner.setSelection(adapter.getPosition(subjectSelected))
                }
            }
        }
        thread.start()
    }

    private fun saveEditedProfile() {
        val thread = Thread {
            val newUser = TudorUser()
            newUser.userId = usrID
            newUser.userName = binding.editProfileNameEditText.text.toString()
            newUser.userPassword = binding.editProfilePasswordEditText.text.toString()
            newUser.userLocation = binding.editProfileLocationEditText.text.toString()
            newUser.userPhone = binding.editProfilePhoneEditText.text.toString()
            newUser.userEmail = binding.editProfileEmailEditText.text.toString()
            newUser.userSubject = subjectSelected
            newUser.isStudent = binding.editProfileStudentRadioButton.isChecked

            db.tudorUserDatabaseDao.update(newUser)

            runOnUiThread {
                run {
                    Toast.makeText(
                        this,
                        "Profile Updated. You are on the main screen.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val intent = Intent(applicationContext, MainScreenActivity::class.java)
            intent.putExtra("primaryKey", usrID)
            startActivity(intent)
        }
        thread.start()
    }

    private fun deleteProfile() {
        val thread = Thread {

            db.tudorUserDatabaseDao.delete(usrID)

            runOnUiThread {
                run {
                    Toast.makeText(this, "Profile Deleted. Please sign in now.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        thread.start()
    }
}
