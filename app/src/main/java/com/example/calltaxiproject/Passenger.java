package com.example.calltaxiproject;

import android.Manifest.permission;
import android.annotation.SuppressLint;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Passenger extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // 권한 요청 식별에 사용되는 값
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2; // 백그라운드 위치 권한 요청 코드

    private boolean permissionDenied = false;
    private GoogleMap mMap;
    Button backBtn, call;
    LocationListener locationListener;
    Double latitude;
    Double longitude;
    Marker myMarker, callMarker, taxiMarker;
    Circle myCircle;
    double callLat;
    double callLon;
    private OkHttpClient client;
    private WebSocket webSocket;
    private boolean cameraFirst = false;
    private int user_id;
    private String taxiNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        UUID uuid = UUID.randomUUID(); // 클라이언트 고유 식별자 생성

        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://49.50.172.178:8080")
                .build();

        WebSocketListener webSocketListener = new MyWebSocketListener();
        webSocket = client.newWebSocket(request, webSocketListener);


        System.out.println("웹소켓 정보 : " + webSocket);

        backBtn = (Button) findViewById(R.id.backBtn);
        call = (Button) findViewById(R.id.call);

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
        mapFragment.getMapAsync(this);



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        call.setOnClickListener(new View.OnClickListener() { // 택시 호출
            @Override
            public void onClick(View view) {
                if(callMarker != null){
                    if(webSocket!=null){
                        LatLng callLatLng = new LatLng(callLat, callLon);
                        UUID callId = UUID.randomUUID();
                        JSONObject jsonObject = new JSONObject();
                        JSONObject call = new JSONObject();
                        try {
                            jsonObject.put("식별자",user_id);
                            jsonObject.put("호출번호",callId);
                            jsonObject.put("호출위도",callLat);
                            jsonObject.put("호출경도",callLon);
                        }catch(JSONException e){
                            e.printStackTrace();
                            System.out.println("JSON 에러");
                        }
                        webSocket.send(jsonObject.toString());
                    }
                }
                    else{ //웹소켓 null부분
                        Toast.makeText(getApplicationContext(), "서버 생성 안됨", Toast.LENGTH_SHORT).show();
                    }
            }
        });






    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(webSocket!=null){
//            webSocket.close(1000,"앱 종료됨");
//            webSocket = null;
//        }
//    }

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
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.call_marker);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        LatLng SEOUL = new LatLng(37.556, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    if(cameraFirst==false) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
                        cameraFirst = true;
                    }
                    System.out.println("카메라 움직임: "+cameraFirst);

                    if(webSocket!=null){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("식별자",user_id);
                            jsonObject.put("승객위도",latitude);
                            jsonObject.put("승객경도",longitude);
                        }catch(JSONException e){
                            e.printStackTrace();
                            System.out.println("JSON 에러");
                        }
                        webSocket.send(jsonObject.toString());
                    }
                    else{ //웹소켓 null부분
                        Toast.makeText(getApplicationContext(), "서버 생성 안됨", Toast.LENGTH_SHORT).show();
                    }
//                            myCircle = mMap.addCircle(new CircleOptions()
//                                    .center(latlng)
//                                    .radius(100) // 반경 100미터 내
//                                    .strokeWidth(5)
//                                    .strokeColor(Color.BLACK)
//                                    .clickable(true));
//                            System.out.println("위도 : "+ latitude);
//                            System.out.println("경도 : "+ longitude);
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
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location != null) {
//
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                LatLng latLng = new LatLng(latitude, longitude);
//
//
//                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("내 위치"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

//                myCircle = mMap.addCircle(new CircleOptions()
//                        .center(latLng)
//                        .radius(100)
//                        .strokeWidth(5)
//                        .strokeColor(Color.BLACK)
//                        .clickable(true));

                // 맵 터치 이벤트 구현 //
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(LatLng point) {
                        MarkerOptions mOptions = new MarkerOptions();
                        // 마커 타이틀
                        mOptions.title("마커 좌표");
                        mOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        callLat = point.latitude; // 위도
                        callLon = point.longitude; // 경도
                        LatLng callLatLng = new LatLng(callLat, callLon);
                        // 마커의 스니펫(간단한 텍스트) 설정
                        mOptions.snippet("호출 지점");
                        // LatLng: 위도 경도 쌍을 나타냄
                        mOptions.position(callLatLng);
                        Toast.makeText(getApplicationContext(), "호출 좌표 : "+callLatLng, Toast.LENGTH_SHORT).show();
                        // 마커(핀) 추가
                        if(callMarker!=null){
                            callMarker.remove();
                        }
                        callMarker = mMap.addMarker(mOptions);
                    }
                });
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
    private class MyWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            // WebSocket 연결이 수립되었을 때 실행되는 부분
            System.out.println("서버와 최초 연결됨");
        }
        @Override
        public void onMessage(WebSocket webSocket, String message) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if(jsonObject.has("connection")) {
                try {
                    user_id = jsonObject.getInt("user_id");
                    String text = jsonObject.getString("msg");
                    System.out.println(text);
                    System.out.println("당신의 식별자 : " + user_id);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else if (jsonObject.has("taxiNum")) { // 택시가 호출을 수락했을때
                try{
                    taxiNum = jsonObject.getString("taxiNum");
                    taxiNumInfo();
                    System.out.println("택시 번호 받음 : "+taxiNum);
                } catch (JSONException e){
                    throw new RuntimeException(e);
                }
            }
            System.out.println("서버로부터 메시지 수신: " + message);
        }
        // 다른 WebSocketListener의 메서드들을 필요에 따라 오버라이드하여 구현
    }

    public void taxiNumInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "호출한 택시 번호 : "+taxiNum, Toast.LENGTH_SHORT).show();
            }
        });
    }
} // 클래스 괄호





