package com.bumie.nearme_

import android.R.id
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import com.bumie.nearme_.databinding.ActivityPlacesBinding
import okhttp3.OkHttpClient
import android.R.id.text2
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import java.lang.StringBuilder


class PlacesActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // using toolbar as ActionBar
        setSupportActionBar(binding.toolbar)
        //Disabling toolbar title
        supportActionBar?.setDisplayShowTitleEnabled(false);
        // "on click" operations to be performed
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

        val client = OkHttpClient().newBuilder().build()
        val addedplaces: ArrayList<Datum> = ArrayList()
        coroutineScope{
            val fetchPlaces = async(Dispatchers.Main + SupervisorJob()) {
                val places = FetchPlaces.getPlaces(client)
                for (i in 0 until places.size){
                    addedplaces.add(places.get(i))
                }
            }
            try {
                fetchPlaces.await()
            }catch (e: Exception){
                Log.d("debugger: ", e.message!!)
            }

            }
        getData(addedplaces)
    }


    suspend fun getData(places:ArrayList<Datum>){

        coroutineScope {
            val fetchPlaces = async(Dispatchers.Main + SupervisorJob()) {
                updateMap(places)
            }
            try {
                fetchPlaces.await()
            }catch (e: Exception){
                Log.d("debugger: ", e.message!!)
            }
        }


    }
    private fun updateMap(places:ArrayList<Datum>){
        val viewPlacesAdapter = ViewPlacesAdapter(this, places!!)
        binding.recyclerView.adapter = viewPlacesAdapter
    }

}