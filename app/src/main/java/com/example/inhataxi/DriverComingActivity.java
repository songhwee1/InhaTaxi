package com.example.inhataxi;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivercoming);
        mAuth = FirebaseAuth.getInstance();

        carNo = (TextView) findViewById(R.id.txtCarNo);
        getCarNo();
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
    }