package com.example.myapplication;

import android.content.Context;
import android.content.Intent;

import java.util.Dictionary;
import java.util.HashMap;

public class ActivityMovementClass {

    //단순 페이지 이동
    public void activityMovement(Context context, Class activityClass){
        Intent intent = new Intent(context.getApplicationContext(),activityClass);
        context.startActivity(intent);
    }

    //페이지 이동 및 데이터 이동
    public void activityMovementWithDate(Context context, Class activityClass, HashMap<String,String> dict){
        Intent intent = new Intent(context.getApplicationContext(), activityClass);
        for(String key : dict.keySet()){
            intent.putExtra(key, dict.get(key));
        }
        context.startActivity(intent);
    }

    //activityForResult 액티비티 이동
    public void activityMovementForResult(Context context, Class activityClass, int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),activityClass);
//        context.
    }
}
