package com.example.yhdj.ad0313baidumap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GuideActivity extends AppCompatActivity {
private EditText mEdt_staring;
    private EditText mEdt_end;
    private Button mBtn_guide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
    }

    private void initViews() {
        mEdt_staring = (EditText) findViewById(R.id.edt_staring);
        mEdt_end = (EditText) findViewById(R.id.edt_end);
        mBtn_guide = (Button) findViewById(R.id.btn_guide);
        mBtn_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
