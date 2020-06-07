package com.example.tudors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.example.tudors.database.TudorUser
import com.example.tudors.database.TudorUserDatabase
import com.google.android.gms.location.*

class MainScreenActivity : AppCompatActivity() {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var usingGPS = false

    // Variables to store the location
    private var storedLocation = ""
    var gpsLocation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Bring over key from MainActivity
        val primaryKey = intent.getLongExtra("primaryKey", 0L)

        // Populate the subject and location textViews
        popFields(primaryKey)

        val toggle: ToggleButton = findViewById(R.id.btnToggleGPS)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled

                // Store the initial location to be able to switch back to it
                storedLocation = findViewById<TextView>(R.id.textViewLocationResult).text.toString()

                // Get last location from GPS and fill the location textView with it
                getLastLocation()
                usingGPS = true
            } else {
                // The toggle is disabled

                // Switch back to the stored location from GPS
                findViewById<TextView>(R.id.textViewLocationResult).text = storedLocation
                usingGPS = false
            }
        }

        // Touch the Find Matches button
        findViewById<Button>(R.id.buttonFindMatches).setOnClickListener {
            findMatch(primaryKey)
        }

        // Go to the edit profile activity (added by Ken)
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            goToEditProfile(primaryKey)
        }
    }

    private fun goToEditProfile(uid: Long) {
        Toast.makeText(this, "You are on the edit profile screen", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, EditProfile::class.java)
        intent.putExtra("primaryKey", uid)
        startActivity(intent)
    }

    private fun popFields(uid: Long) {
        // Get instance of database
        val db = TudorUserDatabase.getInstance(this)

        val thread = Thread {
            // Fetch User
            val tudorUser = db.tudorUserDatabaseDao.get(uid)

            // Display the data
            if (tudorUser != null) {
                findViewById<TextView>(R.id.textViewSubjectResult).text = tudorUser.userSubject
                findViewById<TextView>(R.id.textViewLocationResult).text = tudorUser.userLocation
                storedLocation = tudorUser.userLocation
            }
        }
        thread.start()
    }

    private fun findMatch(uid: Long) {
        val db = TudorUserDatabase.getInstance(this)
        var matches : LiveData<List<TudorUser>>

        val thread = Thread {
            val tudorUser = db.tudorUserDatabaseDao.get(uid)

            if (tudorUser != null) {
                var location = ""

                // Use either GPS or stored location
                if(usingGPS) {
                    tudorUser.userLocation = gpsLocation
                } else {
                    tudorUser.userLocation = storedLocation
                }

                matches = db.tudorUserDatabaseDao.match(tudorUser.userLocation, tudorUser.userSubject, !tudorUser.isStudent)

                // Insert start activity/setExtra code here when review page is ready
            }
        }
        thread.start()
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        val location = list[0].getAddressLine(0)
        gpsLocation = location
        return location
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
