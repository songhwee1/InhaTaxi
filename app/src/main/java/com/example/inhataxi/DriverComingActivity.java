package com.example.inhataxi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class DriverComingActivity extends AppCompatActivity {

    String phone;
    FirebaseAuth mAuth;
    TextView carNo;
    Dialog cancelDialog;
    Button doCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivercoming);
        mAuth = FirebaseAuth.getInstance();

        doCancel = (Button) findViewById(R.id.btnCancelResv);
        carNo = (TextView) findViewById(R.id.txtCarNo);
        getCarNo();
        rideStatusChange();

        cancelDialog = new Dialog(DriverComingActivity.this);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.activity_dialog_cancel);

        // 예약 취소 버튼 누를 시
        doCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    //dialog를 띄어주는 함수
    public void showDialog() {
        cancelDialog.show();

        // 아니오 버튼
        Button noBtn = cancelDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 닫기
                cancelDialog.dismiss();
            }
        });
        // 예 버튼
        cancelDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelReservation();
                cancelDialog.dismiss();
                Intent intent = new Intent(DriverComingActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cancelReservation() {
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0, 11);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("res").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void getCarNo() {
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0, 11);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("res").orderByChild("phone").equalTo(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    carNo.setText(appleSnapshot.child("carNo").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void rideStatusChange() {
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0, 11);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("res").orderByChild("phone").equalTo(phone);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {
                    if(issue.child("status").getValue().toString().equals("ride")){
                        Intent intent = new Intent(DriverComingActivity.this, RidingMapActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    }