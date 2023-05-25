package com.example.calltaxiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
//implements OnMapReadyCallback
//    private GoogleMap mMap;
    Button passengerBtn, taxiDriverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passengerBtn = (Button) findViewById(R.id.passenger);
        taxiDriverBtn = (Button) findViewById(R.id.taxiDriver);

        passengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Passenger.class);
                startActivity(intent);
            }
        });

        taxiDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TaxiDriver.class);
                startActivity(intent);
            }
        });




//        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    // NULL이 아닌 GoogleMap 객체를 파라미터로 제공해 줄 수 있을 때 호출
//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng SEOUL = new LatLng(37.556, 126.97);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국 수도");
//
//        mMap.addMarker(markerOptions);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
//    }
}