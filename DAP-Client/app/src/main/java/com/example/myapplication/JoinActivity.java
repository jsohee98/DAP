package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    EditText et_id, et_pw, et_pwChk, et_mail, et_birth, et_gender;
    TextView pwdCheckResultView, emailValidationView;
    String stringId, stringPw, stringPwCheck,user_birth, user_email ;
    int user_gender;
    Button idDCheckBtn ;
    Button joinButton ;
    boolean idcheck = false, pwdCheck = false, emailCheck = false, birthCheck = false, genderCheck = false;
    NetworkService networkService = APIClient.getNetworkService();
    AlertDialog.Builder builder ;
    String regExpn =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_join);
        et_id = (EditText) findViewById(R.id.inputId);
        et_pw = (EditText) findViewById(R.id.inputPwd);
        et_pwChk = (EditText) findViewById(R.id.inputConfirmPwd);
        et_mail = (EditText) findViewById(R.id.inputEmail);
        et_birth = (EditText) findViewById(R.id.inputBirth);
        et_gender = (EditText) findViewById(R.id.inputGender);
        pwdCheckResultView = (TextView)findViewById(R.id.pwdCheckResultTextView);
        idDCheckBtn = (Button)findViewById(R.id.idDCheckBtn);
        joinButton = findViewById(R.id.button2);
        joinButton.setEnabled(false);
        emailValidationView = findViewById(R.id.emailValidation);
        builder = new AlertDialog.Builder(JoinActivity.this);
//                서버로 아이디 값을 보내면 중복값 확인 후 결과값을 팝업창으로 띄움, 여기서 결과값은 => (사용가능한 아이디입니다., 존재하는 아이디입니다.)
        idDCheckBtn.setOnClickListener(v -> {
            Log.e("id check button click", "ok");
            builder.setTitle("아이디 중복확인");
            String id = et_id.getText().toString();
            Log.e("id check", "ok1");
            if(!(id.equals(""))) {
                Log.e("id check", id + "");
                Call<Register> idCheck = networkService.idcheck(id);
                idCheck.enqueue(new Callback<Register>() {
                    @Override
                    public void onResponse(Call<Register> call, Response<Register> response) {
                        if (response.isSuccessful()) {
                            builder.setMessage("사용가능한 아이디입니다. \n 아이디를 사용하시겠습니까?");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    idcheck = true;
                                }
                            });
                            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    et_id.requestFocus();
                                    idcheck = false;
                                }
                            });

                            Log.e("id check", "ok");
                            AlertDialog altdlg = builder.create();
                            altdlg.show();
                        } else {
                            Log.e("id double check", "ok");
                            builder.setMessage("사용할 수 없는 아이디입니다.\n 다시 입력해주세요.");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    et_id.requestFocus();
                                }
                            });
                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    et_id.requestFocus();
                                }
                            });
                            AlertDialog altdlg = builder.create();
                            altdlg.show();
                        }
                        loginChecking();
                    }

                    @Override
                    public void onFailure(Call<Register> call, Throwable t) {

                    }
                });
            }
        });
        et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(et_pwChk.getText().toString().equals(""))){
                    if(!(et_pw.getText().toString().equals(et_pwChk.getText().toString()))){
                        pwdCheckResultView.setText("비밀번호가 일치하지 않습니다.");
                        loginChecking();
                    }else{
                        pwdCheckResultView.setText("");
                        pwdCheck=true;
                        loginChecking();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_pwChk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pw.getText().toString() != "" ) {
                    if (!(et_pw.getText().toString().equals(et_pwChk.getText().toString()))) {
                        pwdCheckResultView.setText("비밀번호가 일치하지 않습니다.");
                        loginChecking();
                    } else {
                        pwdCheckResultView.setText("");
                        pwdCheck=true;
                        loginChecking();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence inputStr = et_mail.getText().toString();

                Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);

                Log.e("email check=========", "ok");
                if (matcher.matches()){
                    emailValidationView.setText("");
                    emailCheck = true;
                    loginChecking();
                }else{
                    emailValidationView.setText("이메일 형식으로 작성해주세요.");
                    emailCheck = false;
                    loginChecking();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_birth.getText().toString().length() == 8){
                    birthCheck = validationDate(et_birth.getText().toString());
                    Log.e("birth check", birthCheck+"");
                    loginChecking();
                }else{
                    birthCheck = false;
                    loginChecking();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Integer.parseInt(et_gender.getText().toString()) > 0 && Integer.parseInt(et_gender.getText().toString()) < 5 ){
                    genderCheck = true;
                    loginChecking();
                }else{
                    genderCheck = false;
                    Toast.makeText(getApplicationContext(),"숫자만 입력해주세요.",Toast.LENGTH_LONG).show();
                    loginChecking();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // onClick 메서드
    public void btn_join(View view) throws ParseException {
        stringPw = et_pw.getText().toString();
        stringPwCheck = et_pwChk.getText().toString();
        if(!stringPw.equals(stringPwCheck)){
            Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG);
        }else{
            stringId = et_id.getText().toString();
            user_email = et_mail.getText().toString();
            user_birth = et_birth.getText().toString();
            user_gender = Integer.parseInt(et_gender.getText().toString());
            Call<Register> response = networkService.createRegister(stringId,stringPw,user_email,user_gender,user_birth);
            response.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Call<Register> call, Response<Register> response) {
                    Log.e("response result",response.isSuccessful()+"");
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "서버 통신 완료,", Toast.LENGTH_LONG);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else {Toast.makeText(getApplicationContext(), "서버 통신 실패", Toast.LENGTH_LONG).show();}
                }

                @Override
                public void onFailure(Call<Register> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());
                }
            });
        }
    }

    public void loginChecking(){
        if(idcheck && pwdCheck && emailCheck && birthCheck && genderCheck){
            Log.e("login Check", "login true");
            joinButton.setEnabled(true);
        }else{
            joinButton.setEnabled(false);
        }
    }

    public  boolean  validationDate(String checkDate){

        try{
            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMdd");

            dateFormat.setLenient(false);
            dateFormat.parse(checkDate);
            return  true;

        }catch (ParseException e){
            return  false;
        }

    }
}