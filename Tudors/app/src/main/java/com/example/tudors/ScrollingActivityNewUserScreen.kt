package com.example.tudors

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tudors.databinding.ContentScrollingActivityNewUserScreenBinding

class ScrollingActivityNewUserScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ContentScrollingActivityNewUserScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scrolling_new_user_screen)

        val spinner: Spinner = binding.newUserSubjectSpinner
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.subjects_array,
            android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback
        // TO DO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item was selected. Save it.
        var subjectSelected : String = parent!!.getItemAtPosition(position).toString()
    }
}
