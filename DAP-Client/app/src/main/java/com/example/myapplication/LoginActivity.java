package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_JOIN = 101;
    public static final String NAMED_SP = "loginSetting";
    Button loginButton ;
    EditText id, pwd;
    TextView signUp;
    boolean idCheck = false, pwdCheck=false, autologin=false;
    CheckBox checkbox;
    AlertDialog.Builder builder ;
    protected void onCreate(Bundle savedInst) {
        super.onCreate(savedInst);
        setContentView(R.layout.activity_login);

        final NetworkService networkService = APIClient.getNetworkService();
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUpBtn);
        id = findViewById(R.id.ed_id);
        pwd = findViewById(R.id.ed_pwd);
        checkbox = findViewById(R.id.autoLogincheck);
        builder = new AlertDialog.Builder(LoginActivity.this);

        signUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        checkbox.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(checkbox.isChecked()){
                    autologin = true;
                }else{
                    autologin = false;
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                builder.setTitle("로그인 안내");
                if(id.getText().toString().equals("") || pwd.getText().toString().equals("")){
                    Log.e("Login button", "ok");
                    builder.setMessage("아이디 혹은 비밀번호를 입력해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(id.getText().toString().equals("")) idCheck = false;
                            if(pwd.getText().toString().equals("")) pwdCheck=false;
                        }
                    });
                    AlertDialog altdlg = builder.create();
                    altdlg.show();
                }else {
//                    서버와의 호출 후 response가 success인 경우만 아래와같이 정보 저장함.
                    Call<Login>  login = networkService.loginApi(id.getText().toString(), pwd.getText().toString());
                    login.enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if(response.isSuccessful()){
                                SharedPreferences loginInformation = getSharedPreferences(NAMED_SP,0);
                                SharedPreferences.Editor editor = loginInformation.edit();
                                editor.putString("id", id.getText().toString());
                                editor.putString("pwd", pwd.getText().toString());
                                editor.putBoolean("autoLogin", autologin);
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Log.e("error", t.getMessage());
                        }
                    });
                }
            }
        });
    }
}
