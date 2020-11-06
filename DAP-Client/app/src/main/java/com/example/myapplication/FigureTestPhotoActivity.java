package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FigureTestPhotoActivity extends Activity {
    public static final String NAMED_SP = "loginSetting";
    public static final String RECOMMAND_SETTING = "recommandSetting";
    public boolean feedbackDialogOn=false;
    Button button,selectBtn,uploadBtn;
    ImageView imageView;
    String imagePath, nowDate;
    SharedPreferences sp,rs;
    NetworkService networkService = APIClient.getNetworkService();
    final static int REQUEST_CODE = 201;
    private Cursor cursor;
    private File file = null;
    @Override
    protected void onCreate(Bundle saveIntSate){
        super.onCreate(saveIntSate);
        setContentView(R.layout.activity_figure_test_photo);
        button=findViewById(R.id.button);
        selectBtn = findViewById(R.id.selectBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        imageView=findViewById(R.id.imageView);
        sp = getSharedPreferences(NAMED_SP, MODE_PRIVATE);
        rs = getSharedPreferences(RECOMMAND_SETTING, MODE_PRIVATE);

        permissionCheck();
        button.setOnClickListener(v -> {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,1);
        });

        selectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, REQUEST_CODE);
        });

        uploadBtn.setOnClickListener(v -> {
            Random rand = new Random();
            SimpleDateFormat smd = new SimpleDateFormat("yyyy-MM-dd");
            nowDate = smd.format(new Date());
            String tId = "FT"+nowDate+(rand.nextInt(10000000)+1);
            if(file != null){
                Log.e("file ", file.toString());
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                RequestBody testId = RequestBody.create(MediaType.parse("multipart/form-data"), tId);

                RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), sp.getString("id",""));

                RequestBody testCode = RequestBody.create(MediaType.parse("multipart/form-data"), "FT");

                RequestBody testDate = RequestBody.create(MediaType.parse("multipart/form-data"), nowDate);

                Call<FigureTest> call = networkService.saveFigureTest(body, testId, userId, testCode, testDate);
                call.enqueue(new Callback<FigureTest>() {
                    @Override
                    public void onResponse(Call<FigureTest> call, Response<FigureTest> response) {
                        if(response.isSuccessful()){
                            if(rs.getBoolean("recommandState",true)&&rs.getBoolean("recommandTest",false)){
                                feedbackDialogOn = true;
                            }else{
                                feedbackDialogOn = false;
                            }
                            Log.e("image upload", "success");
                            FigureTest ft = response.body();
                            String testResult = ft.getTestResult();
                            Intent resultIntent = new Intent(getApplicationContext(), TestResultActivity.class);
                            resultIntent.putExtra("feedbackDialogOn",feedbackDialogOn);
                            resultIntent.putExtra("testType","FT");
                            resultIntent.putExtra("date", nowDate+"");
                            resultIntent.putExtra("filePath", file.getPath()+"");
                            resultIntent.putExtra("result", testResult+"");
                            Log.e("figure test result", testResult+"");
                            startActivity(resultIntent);
                            finish();
                        }
                        else{
                            Log.e("call result", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<FigureTest> call, Throwable t) {
                        Log.e("onFailure", t.getMessage());
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(),"사진을 선택해 주세요.", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void permissionCheck(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||  checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if (requestCode == 0) {
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if (grantResult[0] == 0) {
                //해당 권한이 승낙된 경우.
            } else {
                //해당 권한이 거절된 경우.
                permissionCheck();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {imageView.setImageURI(data.getData());}
        if (requestCode == REQUEST_CODE){
            try{
                Uri photoUri = data.getData();
                try{
                    String[] proj = { MediaStore.Images.Media.DATA };

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    file = new File(cursor.getString(column_index));

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                setImage();
            }catch(Exception e)
            {
                Log.e("exception ", e.getMessage());
            }
        }

    }

    public void setImage(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        imageView.setImageBitmap(originalBm);
    }
}
