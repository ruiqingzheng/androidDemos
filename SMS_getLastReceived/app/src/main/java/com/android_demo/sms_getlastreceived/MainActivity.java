package com.android_demo.sms_getlastreceived;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "SMS_LAST";
    //获取到短信 进行过滤
    private static final String ADDRESS_FILTER = "654321";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mObserver);
        Log.i(DEBUG_TAG,"Registering smsReceiver......");
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED") ;

        Log.i(DEBUG_TAG,"Default priority: " + filter.getPriority());
        filter.setPriority(1000);
        Log.i(DEBUG_TAG,"seted priority: " + filter.getPriority());
        registerReceiver(mSmsReceiver, filter);
    }


    private final ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "body"}, null, null,
                    "_id desc");
            long id = -1;

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex("_id"));
                String address = cursor.getString(cursor
                        .getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                Toast.makeText(getApplicationContext(),
                        String.format("id: %d\n address:%s\n body: %s", id, address, body),
                        Toast.LENGTH_LONG).show();

            }

            cursor.close();
            if (id != -1) {
                int count = resolver.delete(Uri.parse("content://sms"), "_id=" + id, null);
                Toast.makeText(getApplicationContext(), count == 1 ? "删除成功" : "删除失败", Toast.LENGTH_LONG).show();
            }

        }

    };
    private final BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(DEBUG_TAG, ">>>>>>>>onReceive start");
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            String address = "";
            String fullMessage = "";

            address = messages[0].getOriginatingAddress();
            for (SmsMessage message : messages) {
                fullMessage += message.getMessageBody();
            }
            Log.i(DEBUG_TAG, address + ":" + fullMessage);
            Toast.makeText(getApplicationContext(),
                    address + ":" + fullMessage, Toast.LENGTH_LONG).show();

            if (address.contains("+86")) {
                address = address.substring(3);
            }

            boolean flag_filter = false;
            if (address.equals(ADDRESS_FILTER)) {
                flag_filter = true;
                Log.i(DEBUG_TAG, "sender address match ADDRESS_FILTER");
            }

            if (flag_filter) {
                Toast.makeText(getApplicationContext(),
                        "sender address match ADDRESS_FILTER",
                        Toast.LENGTH_LONG).show();
                this.abortBroadcast();
            }

            Log.i(DEBUG_TAG, ">>>>>>>>onReceive end");
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mSmsReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
    }

}
