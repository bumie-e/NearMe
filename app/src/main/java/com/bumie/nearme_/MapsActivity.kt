package com.bumie.nearme_


import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.lang.StringBuilder
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.bumie.nearme_.databinding.ActivityMapsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val spatialUrl = "https://test.api.amadeus.com/v1/reference-data/locations/pois?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //Attach Click Listener to search Button
        binding.searchButton.setOnClickListener {
            //Getting the latitude longitude coordinates of the center point of screen
            val center = mMap.cameraPosition.target
            val latitude = center.latitude
            val longitude = center.longitude
            val query = StringBuilder(spatialUrl)
            query.append("latitude=")
            query.append(41.397158)
            query.append("&longitude=")
            query.append(2.160873)
            query.append("&radius=2")
            //Perform network call on background thread
            CoroutineScope(Dispatchers.IO).launch {
                fetchPlaces()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(41.397158, 2.160873)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Barcelona"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }


    private suspend fun fetchPlaces(){
        val client = OkHttpClient().newBuilder().build()
        val places = FetchPlaces.getPlaces_(client)
        updateMap(places)
    }

    private suspend fun updateMap(places:ArrayList<Places>){
      for (i in places.indices) {
             val currentPlace = places[i]
             Log.d("debugger: ", Gson().toJson(currentPlace))
             //Fetch information of a place
             val title = currentPlace.name
             val city = currentPlace.category
             val latitude = currentPlace.latitude
             val longitude = currentPlace.longitude
             //Place Marker on a Place
             val coordinate = LatLng(latitude!!, longitude!!)
             CoroutineScope(Dispatchers.Main)
                     mMap.addMarker(MarkerOptions().position(coordinate).title(title).snippet(city))
             }

         }
    }

