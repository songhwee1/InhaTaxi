//로그인메인메이지
package com.example.inhataxi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    // initialize Authentication
    private FirebaseAuth mAuth;
    EditText Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Button components
        Button DoLogin = (Button) findViewById(R.id.btnDoLogin);
        Button GoRegister = (Button) findViewById(R.id.btnGoRegister);
        Button GoRegister2 = (Button) findViewById(R.id.btnGoRegister2);

        // EditText components
        Phone = (EditText) findViewById(R.id.edtUserPhone) ;

        // 관리자 승인시 로그인 성공
        DoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, JoinActivity.class));
                finish();
            }
        });

        GoRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, DriverJoinActivity.class));
                finish();
            }
        });
    }

    // Login Method
    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        String phone1 = Phone.getText().toString() + "@user.com";

        String password = "test1234";

        mAuth.signInWithEmailAndPassword(phone1, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "탑승자 로그인 성공!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            intent.putExtra("phone",phone1);
                            startActivity(intent);

                            finish();
                        } else {
                            driverSignin();
                        }
                    }
                });
    }
    private void driverSignin(){
        String phone2 = Phone.getText().toString() + "@admin.com";
        String password = "test1234";
        mAuth.signInWithEmailAndPassword(phone2, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "기사 로그인 성공!",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "가입되지 않은 사용자 입니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(Phone.getText().toString())) {
            Toast.makeText(LoginActivity.this, "휴대폰 번호를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

}