package com.mickale.pindropdistance;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MapFragment extends Fragment implements OnMapReadyCallback, FloatingActionButton.OnClickListener {

    private String TAG = "MAP";

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

    private DisplayFragment photoViewer;

    private boolean requestingLocationUpdates = false;

    private int imageID = 0;
    private Bitmap[] photos = new Bitmap[100];
    private FragmentManager fragmentManager;

    private GoogleMap map;
    private FloatingActionButton fab;
    private View myView;

    private FusedLocationProviderClient myFusedLocationClient;
    //LocationRequest myLocationRequest;
    private Location myLastLocation;
    //private LocationCallback locationCallback;
    //private LocationSettingsRequest.Builder builder;
    //private SettingsClient client;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        requestPermissions();

        Log.v(TAG, "starting");
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
        fragmentManager = getActivity().getSupportFragmentManager();
        fab = myView.findViewById(R.id.cameraFAB);
        fab.setOnClickListener(this);
        requestingLocationUpdates = true;
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
                photoViewer = DisplayFragment.newInstance();
                photoViewer.setPicture(photos[Integer.parseInt(marker.getTitle())]);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //May need to check here
                fragmentTransaction.replace(R.id.LinearLayout1, photoViewer);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Onclicked");
        if (v == fab){
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
            //Should check for location and pass it to the marker
            getLastLocation();
            makeMarker(bp);
        } else {
            //No results
            Log.d(TAG, "No message");

        }
    }

    private void makeMarker(Bitmap bm){
        MarkerOptions markerOptions = new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromBitmap(bm))
                .position(new LatLng(myLastLocation.getLatitude(),myLastLocation.getLongitude()))
                .title(Integer.toString(imageID));
        map.addMarker(markerOptions);
        photos[imageID] = bm;
        imageID++;
    }

    private void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

        }
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        myFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            myLastLocation = location;
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            myFusedLocationClient.getLastLocation()
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() != null){
                                myLastLocation = task.getResult();
                            }
                        }
                    });
        }
    }
}
