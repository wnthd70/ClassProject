package com.example.calltaxiproject;

import android.Manifest.permission;
import android.annotation.SuppressLint;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Passenger extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // 권한 요청 식별에 사용되는 값
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2; // 백그라운드 위치 권한 요청 코드

    private boolean permissionDenied = false;
    private GoogleMap mMap;
    Button backBtn, call;
    LocationListener locationListener;
    Double latitude;
    Double longitude;
    Marker myMarker;
    Circle myCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        backBtn = (Button) findViewById(R.id.backBtn);
        call = (Button) findViewById(R.id.call);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        call.setOnClickListener(new View.OnClickListener() { // 임시로 내 위치 GPS
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Passenger.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Passenger.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Toast.makeText(getApplicationContext(), "택시를 호출중입니다.", Toast.LENGTH_SHORT).show();
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) { // 위경도 값이 바뀔때마다 호출되는 콜백 함수
//                            Toast.makeText(getApplicationContext(), "위치를 찾습니다.", Toast.LENGTH_SHORT).show();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latlng = new LatLng(latitude, longitude);
                            System.out.println(latlng);

                            // 기존 마커 제거
                            if (myMarker != null) {
                                myMarker.remove();
                            }
                            if (myCircle != null){
                                myCircle.remove();
                            }
                            myMarker = mMap.addMarker(new MarkerOptions().position(latlng).title("내 위치"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
                            myCircle = mMap.addCircle(new CircleOptions()
                                    .center(latlng)
                                    .radius(100) // 반경 100미터 내
                                    .strokeWidth(5)
                                    .strokeColor(Color.BLACK)
                                    .clickable(true));

//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));

//                            if (circle != null){
//                                circle.remove();
//                            }
//                            circle = mMap.addCircle(new CircleOptions()
//                                    .center(latlng)
//                                    .radius(100)
//                                    .strokeWidth(5)
//                                    .strokeColor(Color.BLACK)
//                                    .clickable(true));

                            System.out.println("위도 : "+ latitude);
                            System.out.println("경도 : "+ longitude);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    
//                                }
//                            });
                        }
                        @Override
                        public void onProviderDisabled(String provider) { // 사용자가 GPS를 끄는 등의 행동을 해서 위치값에 접근할 수 없을 때 호출된다.
                            System.out.println("위치값 접근 에러");
                        }
                        @Override
                        public void onProviderEnabled(String provider) {
                            //사용자가 GPS를 on하는 등의 행동을 해서 위치값에 접근할 수 있게 되었을 때 호출된다.
                            //그냥 실내에 들어가서 GPS값이 안받아지기 시작하면 호출되는 함수가 아니다.
                            System.out.println("GPS 켜짐");
                        }
                    };

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                }else{
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(Passenger.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Toast.makeText(getApplicationContext(), "에러입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });






    }


//    @Override
//    protected void onStop() { // 앱이 종료될때 업데이트도 중지
//        super.onStop();
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.removeUpdates(locationListener);
//    }


    //     NULL이 아닌 GoogleMap 객체를 파라미터로 제공해 줄 수 있을 때 호출
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.556, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);


                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("내 위치"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                myCircle = mMap.addCircle(new CircleOptions()
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "위치 권한을 허용합니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "위치 권한을 거부합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
} // 클래스 괄호





