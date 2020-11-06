package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.APIClient;
import com.example.myapplication.DepressionTest;
import com.example.myapplication.ListViewAdapter;
import com.example.myapplication.NetworkService;
import com.example.myapplication.R;
import com.example.myapplication.TestList;
import com.example.myapplication.TestListItem;
import com.example.myapplication.TestResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {
    public ListView listView;
    public ImageView bg;
    public String ACTIVITY_NAME = "notice";
    public static final String NAMED_SP = "loginSetting";
    NetworkService networkService = APIClient.getNetworkService();
    public static NotificationsFragment newInstance(int index){
        NotificationsFragment hf = new NotificationsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index",index);
        hf.setArguments(bundle);
        return hf;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sf = this.getActivity().getSharedPreferences(NAMED_SP,MODE_PRIVATE);
        listView = getView().findViewById(R.id.list_item_view);
        Log.e("userid", sf.getString("id",""));
        bg = getView().findViewById(R.id.nullList);
        if(sf.getString("id","") != ""){
            Call<List<TestList>> test_list = networkService.callTestList(sf.getString("id",""));
            test_list.enqueue(new Callback<List<TestList>>() {

                @Override
                public void onResponse(Call<List<TestList>> call, Response<List<TestList>> response) {
                    List<TestList> testList = response.body();
                    final ListView listView;
                    ListViewAdapter adapter;
                    if(testList.size() == 0){
                        bg.setVisibility(View.VISIBLE);
                    }else{
                        //Adapter 생성
                        adapter = new ListViewAdapter(ACTIVITY_NAME);
                        for(TestList test : testList){
                            adapter.addItem(test.testId, test.testDate, test.testCode);
                        }
                        //리스트뷰 참조 및 Adapter 달기
                        listView = getView().findViewById(R.id.list_item_view);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view1, position, id) -> {
                            TestListItem item = (TestListItem)parent.getItemAtPosition(position);
                            String testId = item.getTestId();
                            final String testCode = item.getTestCode();
                            final String testDate = item.getTestDate();
                            switch (testCode){
                                case "DT":
                                    Call<DepressionTest> response1 = networkService.getDtPoint(testId);
                                    response1.enqueue(new Callback<DepressionTest>() {
                                        @Override
                                        public void onResponse(Call<DepressionTest> call1, Response<DepressionTest> response1) {
                                            if(response1.isSuccessful()){
                                                DepressionTest dt = response1.body();
                                                int point = dt.testPoint;
                                                Log.e("test code", testCode);
                                                Log.e("test date", testDate);
                                                Intent intent = new Intent(getActivity(), TestResultActivity.class);
                                                intent.putExtra("testType",testCode);
                                                intent.putExtra("date",testDate);
                                                intent.putExtra("total_point",point);
                                                startActivity(intent);
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<DepressionTest> call1, Throwable t) {

                                        }
                                    });
                                    break;
                                case "WT":
                                    break;
                                case "FT":
                                    break;
                            }

                        });
                    }
                }

                @Override
                public void onFailure(Call<List<TestList>> call, Throwable t) {

                }
            });
        }
    }
}