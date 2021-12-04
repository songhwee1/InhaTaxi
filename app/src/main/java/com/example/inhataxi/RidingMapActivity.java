package com.example.inhataxi;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class RidingMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    //지자기, 가속도 센서를 활용해 위치를 반환하는 구현체
    // 구글 플레이서비스의 FusedLocationProviderClient도 사용한다.
    //FusedLocationProviderClient란 통합 위치 제공자와 상호 작용하기 위한 기본 진입점.
    //요약하면 센서들을 활용해서 위치를 반환하는 클래스다
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private Geocoder geocoder;
    EditText editText;
    Button Button;
    Button CallTaxi;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridingmap);

        editText = findViewById(R.id.editText);
        Button = findViewById(R.id.button);
        CallTaxi = findViewById(R.id.callTaxi);

        //지도 사용권한을 받아 온다.
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment= (MapFragment) fragmentManager.findFragmentById(R.id.ridingmap);

        if(mapFragment==null){
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.ridingmap, mapFragment).commit();
        }
        //getMapAsync를 호출하여 비동기로 onMapReady콜백 메서드 호출
        //onMapReady에서 NaverMap객체를 받음
        mapFragment.getMapAsync(this);
        MapSearchTask mapSearchTask = new MapSearchTask();
        mapSearchTask.execute();
    }
    private void rideStatusChange() {
        String phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0, 11);
        Log.i("NOW PHONE : ", phone);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d("rsc","start log");
        Query query = reference.child("res").orderByChild("phone").equalTo(phone);
        String finalPhone = phone;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {

                    Log.i("keyssssss", issue.child("status").getValue().toString());
                    if(issue.child("status").getValue().toString().equals("out")){
                        Intent intent = new Intent(RidingMapActivity.this, ReviewActivity.class);
                        startActivity(intent);
                        finish();
                    }
//                    Query query_status = issue.getRef().child("status").equalTo("wait");
//                    query_status.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot issue : snapshot.getChildren()) {
//                                Log.d("rsc","hhhhhhhhh");
//                                Intent intent = new Intent(MyPageActivity.this, MyPageRideActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("여기는 스타트");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨


            }
            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        geocoder = new Geocoder(this);
        //네이버 맵에 locationSource를 셋하면 위치 추적 기능을 사용 할 수 있다
        naverMap.setLocationSource(locationSource);
        //위치 추적 모드 지정 가능 내 위치로 이동
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//        //현재위치 버튼 사용가능
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        LatLng initialPosition = new LatLng(37.448167086033614, 126.65800409772275);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
        //


        naverMap.moveCamera(cameraUpdate);
//        얘가 문제다 이 코드때문에 계속 화면이 갱신되고 있다.
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1000);


//        PolylineOverlay polylineOverlay = new PolylineOverlay();
//
//        ArrayList<LatLng> polyarray = new ArrayList<LatLng>();
//        LatLng startPosition = new LatLng(37.4847422, 126.968773);
//        LatLng endPosition = new LatLng(37.4857433, 126.9723668);
//        polyarray.add(startPosition);
//        polyarray.add(endPosition);
//        polylineOverlay.setCoords(polyarray);
//        polylineOverlay.setMap(naverMap);

        // 카메라 이동 되면 호출 되는 이벤트
        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {


            }
        });

    }



    // 현재 카메라가 보고있는 위치
    public LatLng getCurrentPosition(NaverMap naverMap) {
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }




    //TMap자동차 경로를 검색해주는 메서드
    //출발좌표와 도착 좌표를 입력하여 자동차길찾기가 가능하다
    public String TMapWalkerTrackerURL(LatLng startPoint, LatLng endPoint){

        String url = null;


        try {
            String appKey = "l7xx17fd8ae850af4178a145f962daefe798";

            Intent intent = getIntent();

            String startX = ((MapActivity)MapActivity.mapActivity).startX;
            String startY = ((MapActivity)MapActivity.mapActivity).startY;
            String endX = ((MapActivity)MapActivity.mapActivity).endX;
            String endY = ((MapActivity)MapActivity.mapActivity).endY;

            String startName = URLEncoder.encode("출발지", "UTF-8");

            String endName = URLEncoder.encode("도착지", "UTF-8");
            url = "https://apis.openapi.sk.com/tmap/routes?version=1&callback=result&appKey=" + appKey
                    + "&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY
                    + "&startName=" + startName + "&endName=" + endName;



        } catch ( UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return url;
    }



    //맵검색을 비동기 식으로 처리한다.
    public class MapSearchTask extends AsyncTask<Void, Void, String>{
        String str="인하대역";
        List<Address> addressList = null;


        @Override
        protected String doInBackground(Void... voids) {

            String result = "37.44831032092448"+","+"126.65004667134748";

            return result;
        }


            @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            String[] latlong = result.split(",");
//            System.out.println("위도 텍스트로 바꾸면 어떻게 가지고 오냐 "+latlong[0]);
            double lat = Double.parseDouble(((MapActivity)MapActivity.mapActivity).endX);
            double lon = Double.parseDouble(((MapActivity)MapActivity.mapActivity).endY);
//            System.out.println("위도 텍스트로 바꾸면 어떻게 가지고 오냐 "+lat);
//            System.out.println("경도 텍스트로 바꾸면 어떻게 가지고 오냐 "+lon);


            //검색한 좌표를 만들어준다
            LatLng endPoint = new LatLng(lat, lon);


            // 마커 생성
            Marker marker = new Marker();
            marker.setPosition(endPoint);
            // 마커 추가
            marker.setMap(naverMap);



            //현재위치를 가지고 온다
            GpsTracker gpsTracker = new GpsTracker(RidingMapActivity.this);
            double currentLatitude = gpsTracker.getLatitude();
            double currentLongitude = gpsTracker.getLongitude();

            LatLng startPoint = new LatLng(currentLatitude,currentLongitude);

            //현재 보고있는 화면의 중심을 기준으로 좌표를 만들어주는 메서드
            //LatLng startPoint=getCurrentPosition(naverMap);


            //검색한 좌표와 현재 위치를 넣어서 url을 가지고 온다.
            String url=TMapWalkerTrackerURL(startPoint, endPoint);

            //검색한 url을 가지고 데이터를 파싱한다
            NetworkTask networkTask = new NetworkTask(url, null);
            networkTask.execute();

            //검색한 좌표로 카메라 이동
            //CameraUpdate cameraUpdate = CameraUpdate.scrollTo(endPoint);
            //naverMap.moveCamera(cameraUpdate);
        }
    }


    //URL을 가지고 검색하는 스레드
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        ArrayList<LatLng> latLngArrayList=new ArrayList<LatLng>();
        Marker marker = new Marker();
        TextView totalDistanceText = findViewById(R.id.totalDistance);
        TextView totalTimeText = findViewById(R.id.totalTime);
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            try {
                //전체 데이터를 제이슨 객체로 변환
                JSONObject root = new JSONObject(s);
                System.out.println("제일 상위 "+root);


                //총 경로 횟수 featuresArray에 저장
                JSONArray featuresArray = root.getJSONArray("features");

                for (int i = 0; i < featuresArray.length(); i++){
                    JSONObject featuresIndex = (JSONObject) featuresArray.get(i);
//                    System.out.println("뭐가 저장 됨?"+featuresIndex);
                    JSONObject geometry =  featuresIndex.getJSONObject("geometry");

                    String type =  geometry.getString("type");

                    //type이 LineString일 경우 좌표값이 하나가 아니라 여러개로 책정이 된다.
                    //전부 뽑아서 전체경로에 추가해준다.
                    //type이 Point일 경우에는 출발점, 경유지, 도착지점 이 세경우 뿐인데
                    //세가지는 구분하는 기준은 properties의 pointType으로 구분 가능하다.

                    if(type.equals("LineString")){


                        JSONArray coordinatesArray = geometry.getJSONArray("coordinates");

//                        System.out.println("라인이 여러개다"+coordinatesArray);

                        for(int j=0; j<coordinatesArray.length(); j++){

//                            System.out.println(coordinatesArray.get(j).getClass().getName());

                            JSONArray pointArray = (JSONArray) coordinatesArray.get(j);
                            double longitude =Double.parseDouble(pointArray.get(0).toString());
                            double latitude =Double.parseDouble(pointArray.get(1).toString());

                            latLngArrayList.add(new LatLng(latitude, longitude));
                            System.out.println("LineString를 저장 ");
//                            System.out.println("만들어진 어레이는  "+latLngArrayList);
//                            System.out.println("총저장된 경로의 갯수는"+latLngArrayList.size());
                        }
                    }

                    if(type.equals("Point")){
                        JSONObject properties =  featuresIndex.getJSONObject("properties");
                        try{
                            double totalDistance = Integer.parseInt(properties.getString("totalDistance"));


                            totalDistanceText.setText("총 거리 :"+totalDistance/1000+" km");

                            int totalTime = Integer.parseInt(properties.getString("totalTime"));
                            totalTimeText.setText("총 거리 :"+ totalTime/60+"분");


                        }catch (Exception e){

                        }

                        String pointType = properties.getString("pointType");


                        double longitude =  Double.parseDouble(geometry.getJSONArray("coordinates").get(0).toString());
                        double latitude =  Double.parseDouble(geometry.getJSONArray("coordinates").get(1).toString());
//                        System.out.println("Point를 저장 ");
//                        latLngArrayList.add(new LatLng(latitude, longitude));

                        if(pointType.equals("SP")){
                            System.out.println("시작지점이다");

                        }
                        else if(pointType.equals("GP")){

                            System.out.println("중간지점이다");
                        }
                        else if(pointType.equals("EP")){

                            System.out.println("끝지점이다");

                        }
//                        marker.setPosition(new LatLng(latitude, longitude));
//                        System.out.println(latitude+","+longitude);
//                        marker.setMap(naverMap);
                    }

                    System.out.println("총저장된 경로의 갯수는"+latLngArrayList.size());


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PolylineOverlay polylineOverlay = new PolylineOverlay();
            polylineOverlay.setCoords(latLngArrayList);
            polylineOverlay.setWidth(10);
            polylineOverlay.setPattern(10, 5);
            polylineOverlay.setCapType(PolylineOverlay.LineCap.Round);
            polylineOverlay.setJoinType(PolylineOverlay.LineJoin.Round);

            polylineOverlay.setMap(naverMap);



        }
    }
}
