package com.example.inhataxi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DriverActivity extends AppCompatActivity {

    ListView mListView_res;
    ListView mListView_on;
    ListView mListView_come;
    ArrayList<SampleData> list_1;
    ArrayList<SampleData> list_2;
    ArrayList<SampleData> list_3;
    MyAdapter myAdapter_1;
    MyAdapter myAdapter_2;
    MyAdapter myAdapter_3;
    String phone, start, end;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String carNo;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_status);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.waitList();
        this.comeList();
        this.rideList();

        mListView_res = (ListView)findViewById(R.id.listView1);
        mListView_come = (ListView)findViewById(R.id.listView2);
        mListView_on  = (ListView)findViewById(R.id.listView3);

        myAdapter_1 = new MyAdapter(this, list_1);
        myAdapter_2 = new MyAdapter(this, list_2);
        myAdapter_3 = new MyAdapter(this, list_3);
        mListView_res.setAdapter(myAdapter_1);
        mListView_come.setAdapter(myAdapter_2);
        mListView_on.setAdapter(myAdapter_3);
        // Hwi
        // 오는중
        SwipeDismissListViewTouchListener touchListener_1 =
                new SwipeDismissListViewTouchListener(mListView_res,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //insertStatus(myAdapter_1.getItem(position),"ride");
                                    getCarNo();
                                    changeStatus(myAdapter_1.getItem(position),"come");

                                }
                            }
                        });
        // 승차처리
        SwipeDismissListViewTouchListener touchListener_2 =
                new SwipeDismissListViewTouchListener(mListView_come,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //insertStatus(myAdapter_1.getItem(position),"ride");
                                    changeStatus(myAdapter_2.getItem(position),"ride");

                                }
                            }
                        });
        // Hwi
        // 하차처리
        SwipeDismissListViewTouchListener touchListener_3=
                new SwipeDismissListViewTouchListener(mListView_on,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    deleteStatus(myAdapter_3.getItem(position));
                                }
                            }
                        });

        mListView_res.setOnTouchListener(touchListener_1);
        mListView_res.setOnScrollListener(touchListener_1.makeScrollListener());
        mListView_come.setOnTouchListener(touchListener_2);
        mListView_come.setOnScrollListener(touchListener_2.makeScrollListener());
        mListView_on.setOnTouchListener(touchListener_3);
        mListView_on.setOnScrollListener(touchListener_3.makeScrollListener());
    }

/*
    // Hwi
    // 상태 변경
    private void insertStatus(SampleData data, String status){
        phone = data.getPhone();
        start = data.getStart();
        end = data.getEnd();
        HashMap result = new HashMap<>();
        result.put("end", end);
        result.put("phone", phone);
        result.put("start", start);
        // firebase 정의
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(status).push().setValue(result);
    }
 */
    private void getCarNo(){
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0,11);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child("user").orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    carNo = appleSnapshot.child("carNo").getValue().toString();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
    private void changeStatus(SampleData data, String status){
        phone = data.getPhone();
        start = data.getStart();
        end = data.getEnd();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("res").orderByChild("phone").equalTo(phone);
        getCarNo();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().child("status").setValue(status);
                    appleSnapshot.getRef().child("carNo").setValue(carNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void deleteStatus(SampleData data){
        phone = data.getPhone();
        start = data.getStart();
        end = data.getEnd();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("res").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    // Wait List
    public void waitList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("res").orderByChild("status").equalTo("wait");
        list_1 = new ArrayList<SampleData>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_1.clear();
                if(snapshot.exists()){
                    for(DataSnapshot issue : snapshot.getChildren()){

                        String start = issue.child("start").getValue().toString();
                        String end = issue.child("end").getValue().toString();
                        String phone = issue.child("phone").getValue().toString();

                        Log.i("RETURN VALUE NAME : ", start);
                        Log.i("RETURN VALUE NAME : ", end);
                        Log.i("RETURN VALUE NAME : ", phone);

                        list_1.add(new SampleData(start,end,phone));

                    }
                }myAdapter_1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Come List
    public void comeList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("res").orderByChild("status").equalTo("come");
        list_2 = new ArrayList<SampleData>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_2.clear();
                if(snapshot.exists()){
                    for(DataSnapshot issue : snapshot.getChildren()){

                        String start = issue.child("start").getValue().toString();
                        String end = issue.child("end").getValue().toString();
                        String phone = issue.child("phone").getValue().toString();

                        Log.i("RETURN VALUE NAME : ", start);
                        Log.i("RETURN VALUE NAME : ", end);
                        Log.i("RETURN VALUE NAME : ", phone);

                        list_2.add(new SampleData(start,end,phone));

                    }
                }myAdapter_2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Ride List
    public void rideList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("res").orderByChild("status").equalTo("ride");
        list_3 = new ArrayList<SampleData>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_3.clear();
                if(snapshot.exists()){
                    for(DataSnapshot issue : snapshot.getChildren()){

                        String start = issue.child("start").getValue().toString();
                        String end = issue.child("end").getValue().toString();
                        String phone = issue.child("phone").getValue().toString();

                        Log.i("RETURN VALUE NAME : ", start);
                        Log.i("RETURN VALUE NAME : ", end);
                        Log.i("RETURN VALUE NAME : ", phone);

                        list_3.add(new SampleData(start,end,phone));

                    }
                }myAdapter_3.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}