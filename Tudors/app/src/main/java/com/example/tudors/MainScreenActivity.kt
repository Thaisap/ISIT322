package com.example.tudors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.tudors.database.TudorUser
import com.example.tudors.database.TudorUserDatabase
import com.google.android.gms.location.*

class MainScreenActivity : AppCompatActivity() {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        /*
        val spinner: Spinner = findViewById(R.id.subject_spinner)
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
         */

        // Dylan (5/27/20): Not yet fully functional.
        // Add in https://developer.android.com/guide/topics/ui/controls/spinner#SelectListener

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val toggle: ToggleButton = findViewById(R.id.btnToggleGPS)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                getLastLocation()
            } else {
                // The toggle is disabled
                findViewById<TextView>(R.id.textViewLocationResult).text = ""
            }
        }

        // Get instance of database
        val db = TudorUserDatabase.getInstance(this)

        val thread = Thread {
            val user = TudorUser()
            user.userName = "User"
            user.userPassword = "Password"
            user.userLocation = "Location"
            user.userPhone = "0000000000"
            user.userEmail = "user@email.com"
            user.isStudent = true
            user.userSubject = "Math"

            // Uncomment this to populate the database with a dummy user
            // If left on it will create a new dummy user every run. Wipe data on emulator to clear
            //db.tudorUserDatabaseDao.insert(user)

            //fetch User
            val testUser = db.tudorUserDatabaseDao.get(1)

            // Problem, Dylan 6/4/20: Currently doesn't work on first run after wiping data
            if (testUser != null) {
                findViewById<TextView>(R.id.textViewSubjectResult).text = testUser.userSubject
                findViewById<TextView>(R.id.textViewLocationResult).text = testUser.userLocation
            }
        }
        thread.start()
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val address = getAddress(location.latitude, location.longitude)
                        findViewById<TextView>(R.id.textViewLocationResult).text = address
                        //findViewById<TextView>(R.id.textViewLocationResult).text =
                        //    location.latitude.toString() + location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

            val address = getAddress(mLastLocation.latitude, mLastLocation.longitude)
            findViewById<TextView>(R.id.textViewLocationResult).text = address
            //findViewById<TextView>(R.id.textViewLocationResult).text =
            //    mLastLocation.latitude.toString() + mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}
