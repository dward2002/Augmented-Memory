package uk.ac.wlv.augmentedmemory;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.location.Geocoder;
import android.location.Address;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, LocationListener {

    private static final String EXTRA_REMINDER_ID = "uk.ac.wlv.augmentedmemory.reminder_id";
    private static final String EXTRA_REMINDER_LIST = "uk.ac.wlv.augmentedmemory.reminder_list";
    public static final String USERS_CHILD = "users";
    public static final String MESSAGES_CHILD = "messages";

    private LatLng DUDLEY = new LatLng(0, 0);
    private Marker markerDudley;
    private Marker markerExample;
    private double CurrentLong = 0;
    private double CurrentLat = 0;
    private GoogleMap map;
    private PlacesClient placesClient;
    private List<Reminder> mReminders;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String emailId;


    public static Intent newIntent(Context packageContext, List<Reminder> mReminders){
        Intent intent = new Intent(packageContext, MapsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_REMINDER_LIST, (Serializable) mReminders);
        intent.putExtra(EXTRA_REMINDER_ID,args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), "PUT API KEY HERE");
        Bundle args = getIntent().getBundleExtra(EXTRA_REMINDER_ID);
        mReminders = (ArrayList<Reminder>) args.getSerializable(EXTRA_REMINDER_LIST);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String email = mFirebaseUser.getEmail();
        int dotIndex = email.indexOf(".");
        emailId = email.substring(0,dotIndex);

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        getLocation();
        markerDudley = map.addMarker(new MarkerOptions()
                .position(DUDLEY)
                .title("Current Location")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerDudley.setTag(0);


        //searchLocation();
        //getLocationFromAddress();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    private void getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(GPS_PROVIDER, 600000000, 5, MapsActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        CurrentLat = location.getLatitude();
        CurrentLong = location.getLongitude();
        DUDLEY = new LatLng(CurrentLat, CurrentLong);
        markerDudley.setPosition(DUDLEY);
        markerDudley.setVisible(true);
        getAddressFromLocation(CurrentLat, CurrentLong);
        //autocompleteTest("University Dudley");
        //autocompleteTest("Woodside");
        //autocompleteTest("zoo");
        //autocompleteTest("Mcdonald's");
        for(Reminder rem : mReminders){
            //if already has long and lat
            if(rem.getLongitude() != null && rem.getLatitude() != null){
                double lon = Double.parseDouble(rem.getLongitude());
                double lat = Double.parseDouble(rem.getLatitude());
                markReminder(lon,lat,rem);
            }
            //if it doesnt then get the long and lat
            else if (rem.getLocation() != null){
                autocompleteTest(rem.getLocation(), rem);
            }
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DUDLEY, 15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void autocompleteTest(String query, Reminder rem){

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();




        RectangularBounds bounds = RectangularBounds.newInstance(
                getCoordinate(this.CurrentLat, this.CurrentLong, -1000, 1000),
                getCoordinate(this.CurrentLat, this.CurrentLong, 1000, -1000));

        Log.d("www", "bounds = "+bounds);

        Log.d("www", "new "+getCoordinate(this.CurrentLat, this.CurrentLong, -1000, -1000));
        Log.d("www", "new "+getCoordinate(this.CurrentLat, this.CurrentLong, 1000, 1000));


       /* RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));*/


        /*RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(52.521810, -2.109680),
                new LatLng(52.529869, -2.066760));*/

       /*RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(52.476370,-2.038309),
                new LatLng(52.566201,-2.180295));*/


        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(new LatLng(this.CurrentLat,this.CurrentLong))
                //.setCountries("UK")
                //.setLocationRestriction()
                //.setTypesFilter(Arrays.asList(TypeFilter.ADDRESS.toString()))
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
            Log.i("www", prediction.getPlaceId());
            Log.i("www", prediction.getFullText(null).toString());
            String strAddress = prediction.getFullText(null).toString();
            getLocationFromAddress(strAddress, rem);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("www", "Place not found: " + apiException.getStatusCode());
            }
        });

    }


    public void getLocationFromAddress(String strAddress, Reminder rem) {
        Log.d("www","this is the fulltext "+strAddress);
        Geocoder coder = new Geocoder(MapsActivity.this);
        List<Address> address;
        //LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);

            Address location = address.get(0);
            Log.d("www", String.valueOf(location.getLongitude()));
            Log.d("www", String.valueOf(location.getLatitude()));

            LatLng ReminderLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Marker markerReminder = map.addMarker(new MarkerOptions()
                    .position(ReminderLocation)
                    .title(rem.getmTitle())
                    .snippet(rem.getLocation()));
            markerReminder.setTag(0);
            rem.setLongitude(String.valueOf(location.getLongitude()));
            rem.setLatitude(String.valueOf(location.getLatitude()));

            DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(MESSAGES_CHILD).child(emailId).child(rem.getId());

            mFirebaseReference.setValue(rem);



        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    //used if reminder already has long and lat coordinates
    public void markReminder(double lon, double lat, Reminder rem){
        LatLng ReminderLocation = new LatLng(lat, lon);
        Marker markerReminder = map.addMarker(new MarkerOptions()
                .position(ReminderLocation)
                .title(rem.getmTitle())
                .snippet(rem.getLocation()));
        markerReminder.setTag(0);
    }

    public void getAddressFromLocation(double lat, double lng){
        Geocoder coder = new Geocoder(MapsActivity.this);
        List<Address> address;
        String state = "";

        try {
            address = coder.getFromLocation(lat, lng, 1);
            state = address.get(0).getAddressLine(0);
            Log.d("www", "state " + state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseReference mFirebaseUserReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS_CHILD).child(emailId).child("lastLocation");
        mFirebaseUserReference.setValue(state);

        DatabaseReference mFirebaseUserReference1 = FirebaseDatabase.getInstance().getReference()
                .child(USERS_CHILD).child(emailId).child("lastDate");
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy, HH:mm");
        String myString = fm.format(date);
        Log.d("www",myString);
        mFirebaseUserReference1.setValue(myString);
    }

    public static LatLng getCoordinate(double lat0, double lng0, long dy, long dx) {

        //Log.d("www", "original "+lat0+ " "+lng0);

        double val1 = (double) dy / 6378137;
        double val2 = (double) dx / 6378137;
        double val3 = (double) 180 / Math.PI;
        double val4 = (double) Math.cos(lat0);

        //double lat = lat0 + (val3 * val1);
        //double lng = lng0 + (val3 * (val2 / val4));

        double lat = Math.round((lat0 + (val3 * val1)) * 1000000.0) / 1000000.0;
        double lng = Math.round((lng0 + (val3 * (val2 / val4))) * 1000000.0) / 1000000.0;
        //Log.d("www", "in "+lat+ " "+lng);

        return new LatLng(lat, lng);
    }


}
