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
    // Hwi
    // initialize Authentication
    private FirebaseAuth mAuth;

    EditText Phone;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Hwi
        // Get Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Hwi
        // Button components
        Button DoLogin = (Button) findViewById(R.id.btnDoLogin);
        Button GoRegister = (Button) findViewById(R.id.btnGoRegister);


        // Hwi
        // EditText components
        Phone = (EditText) findViewById(R.id.edtUserPhone) ;


        // Hwi
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
    }





    // Hwi
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
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Success! - 승객",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MapActivity.class));
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
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Success! - 기사",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "You are not registered.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(Phone.getText().toString())) {
            Toast.makeText(LoginActivity.this, "Please enter your phone number.",
                    Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

}