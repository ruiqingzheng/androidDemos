# SharedPreferences

1. 轻型数据存储方式
2. 本质是key-value键值对的XML文件 , 默认在app安装目录下 也就是data目录下
3. 通常用来存储一些简单的配置信息


SharedPreferences对象本身只能读取数据, 不能存储和修改
存储和修改是通过Editor对象


# 存储步骤

1. 获取sharedPreferences对象
2. 获取sharedPreferences.Editor对象
3. 通过Editor接口的putXxx方法来保存key-value, 其中Xxx表示不同的数据类型
4. 通过Editor的接口commit方法来保存key-value对



```java

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
```


studio 中打开ddms的方法:

Tools -> Android ->  android device monitor

但data目录 原本是app安装目录 , 可以查看很多信息, 比如文件是否写入成功等...

权限问题, android 5.1
不能查看data目录, 也就找到xml文件

http://www.cnblogs.com/shoneworn/p/4153256.html  需要root