package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.util.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.widget.Toast;

public class WordsActivity extends Activity {

    private static final String TAG = WordsActivity.class.getSimpleName();
    private static final String CLIENT_ID = "kflfm7ez46";
    public static final String RECOMMAND_SETTING = "recommandSetting";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    public static final String NAMED_SP = "loginSetting";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    NetworkService networkService = APIClient.getNetworkService();

    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    private String convertedResult;

    private boolean isSuccess = false;
    SharedPreferences sp,rs;
    private AudioWriterPCM writer;
    public boolean feedbackDialogOn=false;
    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                //텍스트 받아오고 변환하기
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();

                /*StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }*/
                for(String result : results){
                    if((result.contains("나") || result.contains("내")) && result.contains("거북이") && result.contains("열쇠") && result.contains("다리")){
                        mResult  = result;
                        isSuccess = true;
                        convertedResult = convertText(mResult);
                        break;
                    }
                }
                if(!isSuccess){
                    convertedResult = "다시 녹음해 주세요";
                    btnStart.setText(R.string.str_start);
                } else {

                    SharedPreferences spid = getSharedPreferences(NAMED_SP,MODE_PRIVATE);
                    String userId = spid.getString("id","");
                    SimpleDateFormat smd = new SimpleDateFormat("yyyy-MM-dd");
                    final String nowDate = smd.format(new Date());
                    Log.e("now Date", nowDate);
                    Random rand = new Random();
                    int rn = rand.nextInt(10000000)+1;

                    Call<TestList> response = networkService.saveWordTest("WT"+rn, userId, "WT", nowDate, mResult ,convertedResult);
                    response.enqueue(new Callback<TestList>() {
                        @Override
                        public void onResponse(Call<TestList> call, Response<TestList> response) {
                            if(response.isSuccessful()){
                                if(rs.getBoolean("recommandState",true)&&rs.getBoolean("recommandTest",false)){
                                    feedbackDialogOn = true;
                                }else{
                                    feedbackDialogOn = false;
                                }
                                Intent intent = new Intent(getApplicationContext(),TestResultActivity.class);
                                intent.putExtra("testType", "WT");
                                intent.putExtra("userSpoke", mResult);
                                intent.putExtra("converted", convertedResult);
                                intent.putExtra("date", nowDate+"");
                                intent.putExtra("feedbackDialogOn",feedbackDialogOn);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.e("response is success", "fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<TestList> call, Throwable t) {
                            Log.e("on fail", t.getMessage());
                        }
                    });

                }


                txtResult.setText(convertedResult);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words);

        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        rs = getSharedPreferences(RECOMMAND_SETTING, MODE_PRIVATE);

        permissionCheck();

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });
    }

    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this, "음성 녹음을 위한 권한 필요", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]
                                {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == 0) {
            if (grantResult[0] == 0) {
                //해당 권한이 승낙된 경우.
            } else {
                //해당 권한이 거절된 경우.
                permissionCheck();
            }
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<WordsActivity> mActivity;

        RecognitionHandler(WordsActivity activity) {
            mActivity = new WeakReference<WordsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WordsActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
    public String convertText(String text){
        String tempText = text.replaceAll("열쇠를", "재산을");
        tempText = tempText.replaceAll("열쇠", "재산");
        tempText = tempText.replaceAll("거북이", "배우자");
        tempText = tempText.replaceAll("다리를", "인생을");
        tempText = tempText.replaceAll("다리", "인생");

        return tempText;

    }
}
