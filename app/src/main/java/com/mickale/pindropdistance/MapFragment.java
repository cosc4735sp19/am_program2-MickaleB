package com.mickale.pindropdistance;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment implements OnMapReadyCallback, FloatingActionButton.OnClickListener {

    private String TAG = "MAP";

    public static final int REQUEST_ACCESS_startLocationUpdates = 0;
    public static final int REQUEST_ACCESS_onConnected = 1;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GoogleMap map;
    FloatingActionButton fab;
    View myView;

    private FusedLocationProviderClient myFusedLocationClient;
    LocationRequest myLocationRequest;
    Location myLastLocation;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        Log.v(TAG, "starting");
        getLastLocation();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (myView == null) {
            myView = inflater.inflate(R.layout.fragment_map, container, false);
        } else {
            ((ViewGroup) container.getParent()).removeView(myView);
            return myView;
        }

        fab = myView.findViewById(R.id.cameraFAB);
        fab.setOnClickListener(this);

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //In here we should show the picture

                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "Onclicked");
        if (v == fab) {
            //Ask for location permission and camera permission

            //Launch camera intent
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //the picture is stored in the intent in the data key.
        //get the picture and show it in an the imagview.
        //Note the picture is not stored on the filesystem, so this is the only "copy" of the picture.
        Bundle extras = data.getExtras();
        if (extras != null) {
            //if you know for a fact there will be a bundle, you can use  data.getExtras().get("Data");  but we don't know.
            Bitmap bp = (Bitmap) extras.get("data");
        } else {
            //No results

        }
    }

    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this.getContext(), FetchAddressIntentService.class);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, myLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        ////startService(intent);
    }

    public void getLastLocation() {
        //first check to see if I have permissions (marshmallow) if I don't then ask, otherwise start up the demo.
        if ((ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            //I'm on not explaining why, just asking for permission.
            Log.v(TAG, "asking for permissions");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MapFragment.REQUEST_ACCESS_onConnected);
            return;
        }
        myFusedLocationClient.getLastLocation()
                .addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            //logger.append("Last location: None");
                            return;
                        }
                        myLastLocation = location;

                        Log.v(TAG, "getLastLocation");
                        if (myLastLocation != null) {

                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener((Executor) this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                        Toast.makeText(getActivity(), "GPS access NOT granted", Toast.LENGTH_SHORT).show();
                        //logger.append("Last location: Fail");
                    }
                });

    }

    protected void makeMarker(Location pinLocation, Bitmap bm){
        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bm)).position(new LatLng(0,0));
        map.addMarker(markerOptions);
    }

}
