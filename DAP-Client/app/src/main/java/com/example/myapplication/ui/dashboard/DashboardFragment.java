package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DepressionIntroduceActivity;
import com.example.myapplication.FigureTestActivity;
import com.example.myapplication.R;
import com.example.myapplication.WordsActivity;

public class DashboardFragment extends Fragment {
    LinearLayout test1, test2, test3;
    public static DashboardFragment newInstance(int index){
        DashboardFragment hf = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index",index);
        hf.setArguments(bundle);
        return hf;
    }

    public int getShowInt(){
        return getArguments().getInt("index", 0 );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        test1 = getView().findViewById(R.id.test_one);
        test2 = getView().findViewById(R.id.test_two);
        test3 = getView().findViewById(R.id.test_three);

        test1.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(getActivity(), FigureTestActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        test2.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(getActivity(), WordsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        test3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(getActivity(), DepressionIntroduceActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}