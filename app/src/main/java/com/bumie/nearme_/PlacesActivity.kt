package com.bumie.nearme_

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumie.nearme_.databinding.ActivityPlacesBinding
import okhttp3.OkHttpClient
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.sql.Time
import java.util.concurrent.TimeUnit

class PlacesActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlacesBinding
    private val spatialUrl = "https://test.api.amadeus.com/v1/reference-data/locations/pois?"
    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // using toolbar as ActionBar
        setSupportActionBar(binding.toolbar)
        //Disabling toolbar title
        supportActionBar?.setDisplayShowTitleEnabled(false);
        // "on click" operations to be performed
        // initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.textView2.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            //Perform network call on background thread
            CoroutineScope(Dispatchers.IO).launch {
                fetchPlaces()
            }
        }



    }
    private suspend fun fetchPlaces(){
        val client = OkHttpClient().newBuilder()
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
       // getCurrentLocation()
        val query = StringBuilder(spatialUrl)
        query.append("latitude=")
        query.append(41.397158)
        query.append("&longitude=")
        query.append(2.160873)
        query.append("&radius=2")
        var places = FetchPlaces.getPlaces_(client, query.toString())
        updateMap(places)

    }


    private suspend fun updateMap(places:ArrayList<Places>) {

        val viewPlacesAdapter = ViewPlacesAdapter(this, places)
        coroutineScope {
            val fetchPlaces = async(Dispatchers.Main + SupervisorJob()) {
                binding.recyclerView.adapter = viewPlacesAdapter
            }
            try {
                fetchPlaces.await()
            }catch (e: Exception){
                Log.d("debugger: ", e.message!!)
            }
        }
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                filter(s.toString(), places, viewPlacesAdapter)
            }
        })
    }

    private fun getCurrentLocation(){

        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
            return
        }
        try{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // getting the last known or current location
                    latitude = location.latitude
                    longitude = location.longitude
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed on getting current location",
                        Toast.LENGTH_SHORT).show()
                }
        }catch (e: Exception){
            Toast.makeText(this, "Please turn on your location",
                Toast.LENGTH_SHORT).show()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun filter(text: String?, places: ArrayList<Places>, adapter: ViewPlacesAdapter) {
        val temp: ArrayList<Places> = ArrayList()
        for (d in places) {
            if (text?.let { d.category.toString().contains(it) } == true) {
                temp.add(d)
            }
        }
        //update recyclerview
        adapter.updateList(temp)
    }
}