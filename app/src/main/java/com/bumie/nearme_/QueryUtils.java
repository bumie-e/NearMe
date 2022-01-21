package com.bumie.nearme_;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QueryUtils {

    private static final String defaultCollegeImageUrl = "";

    //Private Constructor so that this class cannot be sub-classed
    private QueryUtils(){
    }

    //Create URL object from the string
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Problem", "Problem building the URL ", e);
        }
        return url;
    }

    //Perform a network call and obtain the response
    private static String makeHttpRequest(URL url) throws IOException {
        String response = "";

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Log.e("Problem", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Problem", "Problem retrieving the college JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return response;
    }

    //Convert receicved stream from API to String
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Extract required information of each college from JSON and store in List
    public static List<Datum> extractFeatureFromJson(String placesJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(placesJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding colleges to
        List<Datum> places = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(placesJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or colleges).
            JSONArray collegeArray = baseJsonResponse.getJSONArray("data");

            // For each college in the collegeArray, create an {@link College} object
            for (int i = 0; i < collegeArray.length(); i++) {

                // Get a single earthquake at position i within the list of college
                JSONObject currentCollege = collegeArray.getJSONObject(i);

                // Extract the value for the key called "title"
                String name = currentCollege.getString("name");

                // Extract the value for the key called "city"
                String category = currentCollege.getString("category");

                /*JSONArray geocodeArray = baseJsonResponse.getJSONArray("geoCode");

                for (int a = 0; a < geocodeArray.length(); a++) {
                    // Get a single earthquake at position i within the list of college
                    JSONObject currentGeocode = geocodeArray.getJSONObject(i);

                    // Extract the value for the key called "latitude"
                    String latitude = currentGeocode.getString("latitude");

                    // Extract the value for the key called "longitude"
                    String longitude = currentGeocode.getString("longitude");
                }*/

                // Create a new {@link College} object with the city, latitude, longitude
                // from the JSON response.
                Datum college = new Datum(name, category);

                // Add the new {@link College} to the list of colleges.
                places.add(college);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the college JSON results", e);
        }

        // Return the list of colleges
        return places;
    }


    //Driver Method for all the other methods
    public static List<Datum> fetchTutorialData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Problem", "Problem making the HTTP request.", e);
        }
        List<Datum> colleges = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Colleges}s
        return colleges;
    }


}
