package com.bumie.nearme_;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bumie.nearme_.databinding.ActivityMapsBinding;

import java.util.List;

import okhttp3.OkHttpClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Datum> mCollegeList;
    private Button searchButton;
    private String spatialUrl = "https://test.api.amadeus.com/v1/reference-data/locations/pois?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Attach Click Listener to search Button
        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Getting the latitude longitude coordinates of the center point of screen
                LatLng center = mMap.getCameraPosition().target;
                double latitude = center.latitude;
                double longitude = center.longitude;

                StringBuilder query = new StringBuilder(spatialUrl);
                query.append("latitude=");
                query.append(41.397158);
                query.append("&longitude=");
                query.append(2.160873);
                query.append("&radius=2");

                //Perform network call on background thread
                final PlacesAsyncTask queryTask = new PlacesAsyncTask();
                queryTask.execute(query.toString());

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    private class PlacesAsyncTask extends AsyncTask<String, Void, List<Datum>> {

        @Override
        protected List<Datum> doInBackground(String... url) {
            //Using QueryUtils class to perform the network call
            // and store the information

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            List<Datum> result = QueryUtils.getPlaces(url[0], client);
            return result;
        }
        @Override
        protected void onPostExecute(List<Datum> result) {
            mCollegeList = result;

            //Iterate over each college in list
            for  (int i = 0; i < mCollegeList.size(); i++) {
                Datum currentCollege = mCollegeList.get(i);

                //Fetch information of college
                Double latitude = currentCollege.getGeoCode().getLatitude();
                Double longitude = currentCollege.getGeoCode().getLongitude();
                String title = currentCollege.getName();
                String city = currentCollege.getCategory();

                //Place Marker on College
                LatLng coordinate = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(coordinate).title(city).snippet(title)).setTag(currentCollege);
            }
        }
    }
}