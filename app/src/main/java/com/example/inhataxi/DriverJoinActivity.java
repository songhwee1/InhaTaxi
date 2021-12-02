package com.example.inhataxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class DriverJoinActivity extends AppCompatActivity {
    // Hwi
    // initialize DB
    DatabaseReference mDatabase;

    // Hwi
    // initialize for Upload Image
    private Uri filePath;
    private ImageView ivPreview;
    String get_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // Hwi
        // Button components
        Button DoRegister = (Button) findViewById(R.id.btnDoRegister);
        Button btnUpload = (Button) findViewById(R.id.btnUpload);
        // Hwi
        // EditText components
        final EditText name = (EditText) findViewById(R.id.edtUserName);
        final EditText phone = (EditText) findViewById(R.id.edtUserPhone);
        final EditText carNum = (EditText) findViewById(R.id.edtCarNum);

        // Hwi
        // Image components
        ivPreview = (ImageView) findViewById(R.id.iv_preview);

        // Hwi
        // 'Register' 버튼 클릭 시 로그인 화면으로 이동
        // "관리자 승인 후 알려드릴게요" 토스트 메세지 띄움
        DoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hwi
                // Convert EditText to String
                String get_name= name.getText().toString();
                get_phone= phone.getText().toString();
                String get_carNum = carNum.getText().toString();

                // Send UserData to DB
                HashMap result = new HashMap<>();
                result.put("name", get_name);
                result.put("phone", get_phone);
                result.put("carNum",get_carNum);


                // Hwi
                // firebase 정의
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("test").child("subtest").push().setValue(result);
                uploadFile();
                // Hwi
                // Make Toast
                Toast.makeText(DriverJoinActivity.this, "I'll let you know after the administrator approves it.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Hwi
        // Image Upload Button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Choose Image."), 0);
            }
        });
    }

    // Hwi
    // Image Upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Hwi
    // upload the file
    private void uploadFile() {
        // 업로드할 파일이 있으면 수행
        if (filePath != null) {
            // 업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Set Filename as YYYYMMHH
            SimpleDateFormat formatter = new SimpleDateFormat(get_phone+"yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            // storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://anywhere-6df10.appspot.com/").child("images/" + filename);
            // 올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            //Toast.makeText(getApplicationContext(), "Upload Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    // 실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    // 진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            // dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please choose file first.", Toast.LENGTH_SHORT).show();
        }
    }
}
