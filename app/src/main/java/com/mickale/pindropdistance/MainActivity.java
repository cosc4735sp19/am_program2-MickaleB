package com.mickale.pindropdistance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    FragmentManager fragmentManager;
    MapFragment myMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            myMapFragment = new MapFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.container, myMapFragment).commit();
        }

    }
}
