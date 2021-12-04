package com.example.inhataxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ReviewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // initialize DB
    DatabaseReference mDatabase;
    Toast toast;
    String phone;
    String comment;
    float score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //sm
        //폰번호랑 별점이랑 리뷰 저장

        //현재 사용자 정보 가져오기
        mAuth = FirebaseAuth.getInstance();
        phone = mAuth.getCurrentUser().getEmail();
        phone = phone.substring(0,11);

        //activity_write_review 컴포넌트
        Button btnReview = (Button)findViewById(R.id.btnReview);
        TextView txtComment = (TextView)findViewById(R.id.txtComment);
        RatingBar reviewScore = findViewById(R.id.reviewScore);

        //js
        //레이팅바의 변화
        reviewScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar reviewScore, float rating, boolean fromUser) {
                score = rating;
            }
        });

        //sm
        //review 버튼 클릭시
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //comment 가져오기
                comment = txtComment.getText().toString().trim();

                //데이터베이스에 저장할 데이터
                HashMap result = new HashMap<>();
                result.put("phone", phone);
                result.put("comment", comment);
                result.put("score", score);

                // firebase 정의
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("review").push().setValue(result);

                //리뷰가 감사하다는 토스트 메세지 출력
                Toast.makeText(ReviewActivity.this, "Thank you for writing the review.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ReviewActivity.this, MapActivity.class);
                startActivity(intent);
                finish();

            }

        });

    }

}