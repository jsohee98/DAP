package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<TestListItem> listViewItemList = new ArrayList<TestListItem>() ;
    private String activityName;
    NetworkService networkService = APIClient.getNetworkService();
    public ListViewAdapter(String activityName){
        Log.e("ListView adapter", "check2");
        this.activityName = activityName;
    }


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (activityName){
                case "notice":convertView = layoutInflater.inflate(R.layout.result_list_box, parent,false); break;
                case "setting":convertView = layoutInflater.inflate(R.layout.test_list_box_setting_page, parent,false);break;
            }

        }

        TextView testDate = convertView.findViewById(R.id.test_date);
        TextView testSub = convertView.findViewById(R.id.testSub);
        TextView testTitle = convertView.findViewById(R.id.titleMain);
        TestListItem testListItem = listViewItemList.get(position);

        Log.e("list view adapter", "position :"+position+" , "+testListItem.toString());
        Log.e("list view adapter", testListItem.getTestDate());
        String date = testListItem.getTestDate();
        String sub = testListItem.getSubTitle();
        String main = testListItem.getMainTitle();
        testDate.setText("검사 일자: "+date);
        testSub.setText(sub);
        testTitle.setText(main);
        if(activityName == "setting"){
            final String testId = testListItem.getTestId();
            testDate.setText(date);
            ImageButton delBTN = convertView.findViewById(R.id.deleteButton);
            delBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("test id", testId);
                    Call<List<TestList>> response = networkService.deleteTestResult(testId);
                    response.enqueue(new Callback<List<TestList>>() {
                        @Override
                        public void onResponse(Call<List<TestList>> call, Response<List<TestList>> response) {
                            if(response.isSuccessful()){
                                // 아이템 삭제
                                listViewItemList.remove(position);
                                ListViewAdapter.this.notifyDataSetChanged();
                                Toast.makeText(context, "정상적으로 삭제 되었습니다.", Toast.LENGTH_LONG);
                            }else{
                                Log.e("onResponse", "fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<TestList>> call, Throwable t) {
                            Log.e("onFailure", t.getMessage().toString());
                        }
                    });
                }
            });
        }
        return convertView;
    }

    public void addItem(String testId, String testDate, String testCode){
        TestListItem listItem = new TestListItem();
        listItem.setTestId(testId);
        listItem.setTestDate(testDate);
        listItem.setTestCode(testCode);
        switch (testCode){
            case "DT":
                listItem.setSubTitle("직접 체크하여 알아보는");
                listItem.setMainTitle("나의 우울도 검사하기");
                break;
            case "FT":
                listItem.setSubTitle("도형 그리기를 통해");
                listItem.setMainTitle("나의 기질 알아보기");
                break;
            case "WT":
                listItem.setSubTitle("음성 인식을 통한");
                listItem.setMainTitle("단어 조합 심리테스트");
                break;
        }
        listViewItemList.add(listItem);
    }
}
