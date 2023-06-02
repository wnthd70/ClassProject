package com.example.calltaxiproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaxiMap extends AppCompatActivity implements OnMapReadyCallback {
    Marker myMarker;
    Circle myCircle;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // 권한 요청 식별에 사용되는 값
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2; // 백그라운드 위치 권한 요청 코드
    private GoogleMap myMap;
    private boolean isMapReady = false; // 맵이 준비되었는지 여부를 확인하기 위한 변수

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxidriver2);

        Intent getIntent = getIntent();
        int carNum = getIntent.getIntExtra("차량번호", 0);
        // 위치 권한 요청 //
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //포그라운드 위치 권한 확인
            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if(permissionCheck2 == PackageManager.PERMISSION_DENIED){ //백그라운드 위치 권한 확인
            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
        }
        // 위치 권한 요청 //

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // SupportMapFragment가 null인 경우 생성하여 추가
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map2, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);

        // 맵이 준비되지 않았을 경우, isMapReady 변수를 true로 설정
        if (myMap == null) {
            isMapReady = true;
        }

        // 나머지 코드는 onMapReady() 메서드로 이동



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        LatLng SEOUL = new LatLng(37.556, 126.97);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "위치 권한 수락", Toast.LENGTH_SHORT).show();
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latlng = new LatLng(latitude, longitude);
                if(latlng==null){
                    System.out.println("널입니다.");
                }else{
                    System.out.println("latlng : "+latlng);
                }

                if(myMarker!=null){
                    myMarker.remove();
                }
                myMarker = myMap.addMarker(new MarkerOptions().position(latlng).title("현재위치"));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                myCircle = myMap.addCircle(new CircleOptions().center(latlng).radius(3).strokeWidth(10));
            }
        }
    }
}