package com.example.calltaxiproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class TaxiMap extends AppCompatActivity implements OnMapReadyCallback {
    Marker myMarker;
    Circle myCircle;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // 권한 요청 식별에 사용되는 값
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2; // 백그라운드 위치 권한 요청 코드
    private OkHttpClient client;
    private WebSocket webSocket;
    private GoogleMap myMap;
    private boolean cameraFirst = false;
    int carNum;
    UUID uuid;
    LocationListener locationListener;
    private boolean isMapReady = false; // 맵이 준비되었는지 여부를 확인하기 위한 변수

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxidriver2);

        Intent getIntent = getIntent();
        carNum = getIntent.getIntExtra("차량번호", 0);

        uuid = UUID.randomUUID(); // 클라이언트 고유 식별자 생성

        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://49.50.172.178:8080")
                .build();

        WebSocketListener webSocketListener = new TaxiMap.TaxiWebSocketListener();
        webSocket = client.newWebSocket(request, webSocketListener);

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
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) { // 위경도 값이 바뀔때마다 호출되는 콜백 함수
//                            Toast.makeText(getApplicationContext(), "위치를 찾습니다.", Toast.LENGTH_SHORT).show();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latlng = new LatLng(latitude, longitude);

                    // 기존 마커 제거
                    if (myMarker != null) {
                        myMarker.remove();
                    }
                    myMarker = myMap.addMarker(new MarkerOptions().position(latlng).title("내 위치"));
                    if(cameraFirst==false) {
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
                        cameraFirst = true;
                    }
                    if(webSocket!=null){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("택시식별자",uuid);
                            jsonObject.put("택시번호",carNum);
                            jsonObject.put("택시위도",latitude);
                            jsonObject.put("택시경도",longitude);
                        }catch(JSONException e){
                            e.printStackTrace();
                            System.out.println("JSON 에러");
                        }
                        webSocket.send(jsonObject.toString());
                    }
                    else{ //웹소켓 null부분
                        Toast.makeText(getApplicationContext(), "서버 생성 안됨", Toast.LENGTH_SHORT).show();
                    }

                    System.out.println(latlng);
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
            ActivityCompat.requestPermissions(TaxiMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(getApplicationContext(), "에러입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private class TaxiWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            // WebSocket 연결이 수립되었을 때 실행되는 부분
            System.out.println("서버와 최초 연결됨");
        }

        // 다른 WebSocketListener의 메서드들을 필요에 따라 오버라이드하여 구현
        // ...

    }
}
