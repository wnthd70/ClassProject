package com.example.calltaxiproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaxiDriver extends AppCompatActivity implements OnMapReadyCallback {
    Button backBtn;
    Marker myMarker;
    Circle myCircle;
    private GoogleMap myMap;
    Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_driver_main);

        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn1 = (Button) findViewById(R.id.btn1);
        // 위치 권한 요청 //
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //포그라운드 위치 권한 확인
            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if(permissionCheck2 == PackageManager.PERMISSION_DENIED){ //백그라운드 위치 권한 확인
            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
        }
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view) {
                setContentView(R.layout.taxidriver2);
                Toast.makeText(getApplicationContext(), "내 위치를 찾습니다.", Toast.LENGTH_SHORT).show();
                LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(TaxiDriver.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null){
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng =new LatLng(latitude, longitude);

                        myMarker = myMap.addMarker(new MarkerOptions().position(latLng).title("내 위치"));
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }

        });
    }
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                myMap = googleMap;

                LatLng SEOUL = new LatLng(37.556, 126.97);
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);


                        myMarker = myMap.addMarker(new MarkerOptions().position(latLng).title("내 위치"));
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        myCircle = myMap.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(100)
                                .strokeWidth(5)
                                .strokeColor(Color.BLACK)
                                .clickable(true));
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
//
//                    @Override
//                    public void onMapClick(@NonNull LatLng latLng) {
//                        public void onMapClick(LatLng point){
//                            MarkerOptions mOptions = new MarkerOptions();
//                        }
//                    }
//                });

//                LatLng SEOUL = new LatLng(37.556, 126.97);
//
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(SEOUL);
//                markerOptions.title("서울");
//                markerOptions.snippet("한국 수도");
//
//                mMap.addMarker(markerOptions);
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
                    }
                }
            }
}




