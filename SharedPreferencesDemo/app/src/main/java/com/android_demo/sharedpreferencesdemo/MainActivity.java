package com.android_demo.sharedpreferencesdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username_edt, userpassword_edt;
    CheckBox saveName_chk;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        testSharedPreferences();

        //初始化控件
        username_edt = (EditText) findViewById(R.id.id_username_edt);
        userpassword_edt = (EditText) findViewById(R.id.id_password_edt);
        saveName_chk = (CheckBox) findViewById(R.id.id_checkBox);
        //pref 可以生成多个, 这样就会有多个对应的xml文件
        pref = getSharedPreferences("prefDemo_userInfo", MODE_PRIVATE);
        editor = pref.edit();

        //如果有设置保存用户名, 那么显示上次保存的用户名
        String nameInPref= pref.getString("username", "");
        //如果用户名为空, 那么默认checkbox不被勾选, 留给用户更改是否勾选
        if("".equals(nameInPref)||null == nameInPref) {
            saveName_chk.setChecked(false);
        }else {
            saveName_chk.setChecked(true);
            username_edt.setText(nameInPref);
        }



    }

    //按钮的点击事件 , 两个按钮的事件都放在这个方法里
   public void doClick(View v) {

       switch (v.getId()) {
           case R.id.id_login_btn :
               String username = username_edt.getText().toString().trim();
               String password = userpassword_edt.getText().toString().trim();
               //如果登陆成功 , admin admin
               if("admin".equals(username) && "admin".equals(password)) {
                   //登陆成功, 且勾选了保存用户名, 那么就设置pref 用editor 写入用户名到xml
                   if(saveName_chk.isChecked()) {
                       editor.putString("username", username);
                       editor.commit();
                   }else{
                       editor.remove("username");
                       editor.commit();
                   }
                   Toast.makeText(MainActivity.this, username + " 登陆成功",Toast.LENGTH_LONG).show();
               } else {
                   Toast.makeText(MainActivity.this, username + " 登陆失败...",Toast.LENGTH_LONG).show();
               }
               break;
           case R.id.id_cancel_btn:
               this.finish();
               break;
           default:
       }

    }

    public void testSharedPreferences(){

        //测试直接使用SharedPreferences类来存键值对
        //1. PreferenceManager 获取默认的SharedPreferences对象
//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //只允许本APP读取的SharedPreferences
        //当定义了SharedPreferences 对象, 就会立即生成对应的xml 文件
        SharedPreferences pre = getSharedPreferences("myPreperence", MODE_PRIVATE);


        //2. 获取Editor对象, 写入键值对
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("name", "张三");
        editor.putInt("age", 30);
        editor.putLong("time", System.currentTimeMillis());
        editor.putBoolean("default", true);


        //3. 提交
        editor.commit();

        //4. 删除
        editor.remove("default");
        editor.commit();

        //5. 读取

        Map<String, ?> map = pre.getAll();
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }

        //查看ddms虚拟机的数据找到对应的xml文件

        //studio 中打开ddms的方法:
        //Tools -> Android ->  android device monitor

        //权限问题, 不能查看data目录, 也就找到xml文件
        //http://www.cnblogs.com/shoneworn/p/4153256.html  需要root
    }
}
