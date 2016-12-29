package com.android_demo.sms_read_send;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;

/**
 * 使用ContentResolver 来读取短信
 */


public class SMSTest {
    private static final String TAG = "SMSTest";

    private static final String CONVERSATIONS = "content://sms/conversations/";
    private static final String CONTACTS_LOOKUP = "content://com.android.contacts/phone_lookup/";
    private static final String SMS_ALL = "content://sms/";
    //发送箱
//	private static final String SMS_SENT = "content://sms/sent";
    //收件箱
//	private static final String SMS_INBOX = "content://sms/inbox";
    //草稿箱
//	private static final String SMS_DRAFT = "content://sms/DRAFT";

    private SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context context;


    public SMSTest(Context con) {
        context = con;
    }


    public void testReadConversation() {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse(CONVERSATIONS);
        Log.i(TAG, "parsed uri: " + uri);

        //如果我们查询时的projection为null的话，sConversationProjectionMap就将转换为默认的projection，
        //最后查询结果中仅包含这三个最基本的字段：snippet、thread_id、msg_count，可以代表一个会话的最简明的信息
        String[] projections = new String[]{"groups.group_thread_id AS group_id",
                "groups.msg_count AS msg_count",
                "groups.group_date AS last_date",
                "sms.body AS last_msg",
                "sms.address AS address "};

        Cursor thinc = resolver.query(uri, projections, null, null, "groups.group_date DESC");
        Cursor richc = new CursorWrapper(thinc) {
            //处理返回结果的sms.address AS address那一列
            //查询CONTACTS_LOOKUP表, 如果address有对应的联系人名,则返回联系人名称
            //否则返回address, 也就是原号码
            @Override
            public String getString(int columnIndex) {
                if (super.getColumnIndex("address") == columnIndex) {
                    String address = super.getString(columnIndex);

                    Uri uri = Uri.parse(CONTACTS_LOOKUP + address);
                    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                        return contactName;
                    }
                    return address;
                }
                return super.getString(columnIndex);
            }

        };

        while (richc.moveToNext()) {
            String groupId = "group Id:" + richc.getInt(richc.getColumnIndex("group_id"));
            String msgCount = "msg count:" + richc.getInt(richc.getColumnIndex("msg_count"));
            String lastMsg = "lastMsg:" + richc.getString(richc.getColumnIndex("last_msg"));
            String contact = "contact:" + richc.getString(richc.getColumnIndex("address"));
            String lastDate = "lastDate:" + dateFormate.format(richc.getLong(richc.getColumnIndex("last_date")));

            printLog(groupId, contact, msgCount, lastMsg, lastDate, "--------------------END---------");

        }
        richc.close();

    }


    public void testReadSMS() {
        // 使用到的URI，他们分别是收件箱、已发送和草稿箱，SMS_SENT = "content://sms/sent";
        // 这几个查询是“content://sms/”的子集，分别用了不同的选择条件对短信表进行查询
        // 比如只需要查询收件箱 其地址: SMS_INBOX = "content://sms/inbox"
        Uri uri = Uri.parse(SMS_ALL);
        ContentResolver resolver = context.getContentResolver();
        String[] projections = new String[]{"thread_id AS group_id", "address as contact", "body AS msg_content", "date", "type"};
        Cursor cursor = resolver.query(uri, projections, null, null, "date DESC");
        Log.i(TAG, "--------------------------------CHECKING MSG START------------------------------  ");
        while (cursor.moveToNext()) {
            String groupId = "groupId:" + cursor.getInt(cursor.getColumnIndex("group_id"));
            String contact = "contact:" + cursor.getString(cursor.getColumnIndex("contact"));
            String content = "msg body:" + cursor.getString(cursor.getColumnIndex("msg_content"));
//			String date = "date:" + DateFormat.format(dateFormate, cursor.getLong(cursor.getColumnIndex("date")));
            String date = "date:" + dateFormate.format(cursor.getLong(cursor.getColumnIndex("date")));
            String type = "type:" + getTypeById(cursor.getInt(cursor.getColumnIndex("type")));
            printLog(groupId, contact, content, date, type, "-------------------------END-------------------------");
        }
        cursor.close();
        Log.i(TAG, "--------------------------------CHECKING MSG END------------------------------ ");
    }

    private String getTypeById(int typeId) {
        switch (typeId) {
            case 1:
                return "received";
            case 2:
                return "send";
            case 3:
                return "draft";
            default:
                return "none";
        }
    }

    private void printLog(String... strings) {
        for (String s : strings) {
            Log.i(TAG, s == null ? "NULL" : s);
        }
    }
}
