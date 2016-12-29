package com.android_demo.sms_read_send;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button readBtn;
    SMSTest st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SMSTest 是一个用contentResolver 来读取短信的类 , 初始化需要一个上下文
        st = new SMSTest(MainActivity.this);

        readBtn = (Button) findViewById(R.id.btnRead);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st.testReadSMS();
            }
        });
    }
}
