package com.bumie.nearme_

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


object FetchPlaces {

    suspend fun getToken(client: OkHttpClient):String{
        var token = ""
        val formBody: RequestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", "your_client_id")
            .add("client_secret", "your_api_secret")
            .build()
        withContext(Dispatchers.IO) {
            val request: Request = Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url("https://test.api.amadeus.com/v1/security/oauth2/token")
                .post(formBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    } else {
                        val text = response.body!!.string()
                        try {
                            token = JSONObject(text).getString("access_token")

                        } catch (e: JSONException) {
                            Log.e("Fetch Places", "Problem getting access code", e)
                        }
                    }
                }
            })
            delay(2080L)
        }
        return token
    }

    suspend fun getPlaces_(client: OkHttpClient, query: String):ArrayList<Places>{
        // Create an empty ArrayList that we can start adding places to
        val places: ArrayList<Places> = ArrayList()
        coroutineScope{
            val fetchPlaces = async(Dispatchers.IO + Job()) {
                val token = getToken(client)
                val request: Request = Request.Builder()
                    .header("Authorization", "Bearer "+token)
                    .url(query)
                    .method("GET", null)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        } else {
                            val text = response.body!!.string()
                            Log.i("Main Activity: ", text)
                            // If the JSON string is empty or null, then return early.

                            // Try to parse the JSON response string. If there's a problem with the way the JSON
                            // is formatted, a JSONException exception object will be thrown.
                            // Catch the exception so the app doesn't crash, and print the error message to the logs.
                            try {
                                val baseJsonResponse = JSONObject(text)
                                // Extract the JSONArray associated with the key called "data",
                                val placesArray = baseJsonResponse.getJSONArray("data")

                                for (i in 0 until placesArray.length()) {

                                    val currentPlace = placesArray.getJSONObject(i)
                                    val name = currentPlace.getString("name")
                                    val geoCode = currentPlace.getJSONObject("geoCode")
                                    val latitude = geoCode.getDouble("latitude")
                                    val longitude = geoCode.getDouble("longitude")
                                    val category = currentPlace.getString("category")
                                    // Create a new {@link Places} object with the name, category, latitude, longitude
                                    val place = Places(latitude, longitude, name, category)
                                    places.add(place)
                                }
                            } catch (e: JSONException) {
                                Log.e("FetchPlaces", "Problem parsing JSON results", e)
                            }
                        }
                    }
                })
            }
            fetchPlaces.await()
            delay(2080L)
        }
        return places
    }
}