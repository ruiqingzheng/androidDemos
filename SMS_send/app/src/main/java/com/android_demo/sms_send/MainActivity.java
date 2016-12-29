package com.android_demo.sms_send;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


//这个例子详细见<<第一行代码>>
public class MainActivity extends AppCompatActivity {

    //初始化短信发送的广播接收器
    private SendReceiver sendReceiver = new SendReceiver();
    //初始化短信到达的广播接收器
    private DeliverReceiver deliverReceiver = new DeliverReceiver();

    private EditText address;
    private EditText body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (EditText) findViewById(R.id.address);
        body = (EditText) findViewById(R.id.body);

        //注册自定义的广播接收器 , 这两个消息是系统广播

        registerReceiver(sendReceiver, new IntentFilter("SENT_SMS_ACTION"));
        registerReceiver(deliverReceiver, new IntentFilter("DELIVERED_SMS_ACTION"));

    }


    // 发送短信按钮的点击事件
    public void sendSMS(View view) {
        String address = this.address.getText().toString();
        String body = this.body.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SENT_SMS_ACTION"), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, new Intent("DELIVERED_SMS_ACTION"), 0);

        if (body.length() > 70) {
            List<String> msgs = smsManager.divideMessage(body);
            for (String msg : msgs) {
                smsManager.sendTextMessage(address, null, msg, sentIntent, deliveryIntent);
            }
        } else {
            smsManager.sendTextMessage(address, null, body, sentIntent, deliveryIntent);
        }

    //    	ContentValues values = new ContentValues();
    //    	values.put("address", address);
    //    	values.put("body", body);
    //    	values.put("date", System.currentTimeMillis());
    //    	values.put("read", 0);
    //    	values.put("type", 2);
    //    	// 将发送的短信写入到sent表
    //    	getContentResolver().insert(Uri.parse("content://sms/sent"), values);

    }


    //当发送短信出去
    private class SendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Sent Successfully", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "Failed to Send.", Toast.LENGTH_LONG).show();
            }
        }

    }

    //当短信发送到达
    private class DeliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Delivered Successfully.", Toast.LENGTH_LONG).show();
        }
    }
}
