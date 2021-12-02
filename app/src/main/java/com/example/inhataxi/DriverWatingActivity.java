package com.example.inhataxi;

import androidx.appcompat.app.AppCompatActivity;
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
public class DriverWatingActivity extends AppCompatActivity {

    Button doCancel;

    Dialog cancelDialog;

    private FirebaseAuth mAuth;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverwating);
        mAuth = FirebaseAuth.getInstance();


        doCancel = (Button) findViewById(R.id.btnCancelResv);

        Intent intent = getIntent();

        //jh
        TextView txtBus = (TextView) findViewById(R.id.txtBus);
        TextView txtDepart = (TextView) findViewById(R.id.txtDepart);
        TextView txtArrive = (TextView) findViewById(R.id.txtArrive);

        //ReservationActivity.java에서 보낸 변수로 바꿔주기
        String bus = intent.getStringExtra("bus");
        String depart = intent.getStringExtra("depart");
        String arrive = intent.getStringExtra("arrive");

        txtBus.setText(bus);
        txtDepart.setText(depart);
        txtArrive.setText(arrive);

        rideStatusChange(bus , depart, arrive);

        // Minjae
        // dialog

        cancelDialog = new Dialog(DriverWatingActivity.this);
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

    //sm
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
        // 네 버튼
        cancelDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelReservation();
                cancelDialog.dismiss();
                finish();
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

    private void rideStatusChange(String bus, String depart, String arrive) {
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0, 11);
        Log.i("NOW PHONE : ", phone);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d("rsc","start log");
        Query query = reference.child("res").orderByChild("phone").equalTo(phone);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {
                    Log.i("IN THE NOW PHONE : ", phone);
                    Log.d("rsc","hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh1323513");

                    Log.i("keyssssss", issue.child("status").getValue().toString());
                    if(issue.child("status").getValue().toString().equals("ride")){
                        Intent intent = new Intent(DriverWatingActivity.this, RidingMapActivity.class);
                        intent.putExtra("busnum", bus);
                        intent.putExtra("depart", depart);
                        intent.putExtra("arrive", arrive);
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
}

