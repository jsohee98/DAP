package com.example.myapplication.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

public class TestDeleteDialog extends Dialog {
    private Button pButton,nButton;
    public TestDeleteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.delete_alert_dialog);
        pButton = findViewById(R.id.positiveBtn);
        nButton = findViewById(R.id.negativeBtn);

        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "정상적으로 삭제 되었습니다.", Toast.LENGTH_LONG);
                TestDeleteDialog.this.dismiss();
            }
        });
        nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestDeleteDialog.this.dismiss();
            }
        });

    }
}
