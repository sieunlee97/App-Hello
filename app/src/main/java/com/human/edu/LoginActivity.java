package com.human.edu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import core.AsyncResponse;
import core.PostResponseAsyncTask;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText editTextID, editTextPassword;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(LoginActivity.this, "onDestroy상태6", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(LoginActivity.this, "onStop상태5", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(LoginActivity.this, "onPause상태4", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(LoginActivity.this, "onResume상태3", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(LoginActivity.this, "onStart상태2", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 스프링으로 보낼 데이터를 해시맵 타입으로 저장
                HashMap postDataParams = new HashMap();
                postDataParams.put("txtUsername", editTextID.getText().toString());
                postDataParams.put("textPassword", editTextPassword.getText().toString());
                // 스프링 앱 주소 지정
                String requestUrl = "http://192.168.56.1:8080/android/login";
                //jsp의 Ajax와 같은 역할의 AsyncTask 클래스 사용
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(LoginActivity.this, postDataParams, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) { // output = 스프링앱에서 반환받은 로그인 사용자 정보
                        Toast.makeText(LoginActivity.this, output+"디버그", Toast.LENGTH_SHORT).show();
                        String jsonString = output.substring(output.indexOf('{'), output.indexOf('}'));
                        if(!jsonString.equals("{}")){ // 로그인 사용자 정보가 있으면
                            Log.i("디버그", jsonString);
                            //로그인 이후 액티비티를 여기서 띄우기
                            Intent intent = new Intent(LoginActivity.this, SubActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                readTask.execute(requestUrl); // 1번 작업 (백그라운드 호출)
                
                // Intent는 안드로이드앱에서 액티비티 간 데이터를 전송하는 클래스
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra("editTextID", editTextID.getText().toString()); //아이디
                mainIntent.putExtra("editTextPassword", editTextPassword.getText().toString()); //암호
                startActivity(mainIntent);
                Toast.makeText(LoginActivity.this, "onCreate상태1", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goToMain(View view) {
        //위 매개변수 view는 버튼뷰를 가리킨다.
        EditText editTextID, editTextPassword;
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        Toast.makeText(LoginActivity.this, editTextID.getText(), Toast.LENGTH_LONG).show();

        //로그인 버튼을 onClick 했을 때
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

        //데이터를 입력해서 MainActivity 화면 열기(아래)
        mainIntent.putExtra("editTextID", editTextID.getText()); //아이디
        mainIntent.putExtra("editTextPassword", editTextPassword.getText()); //암호

        startActivity(mainIntent); //편지봉투 Intent 개봉 = 화면불러오기 실행
        finish(); //LoginActivity 화면 종료 (프로그램종료X)
    }
}