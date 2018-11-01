package com.amos.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.amos.bean.City;
import com.amos.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG="MyAPP";
    private static MyApplication mApplication;
    private CityDB mCityDB;

    private List<City> mCityList;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        mApplication=this;
//        mCityDB是一个CityDB类实例，有db数据库，路径是city,即他就是cityDB数据库路径
        mCityDB=openCityDB();
        initCityList();
    }
    public static MyApplication getInstance(){
        return mApplication;
    }

    private void initCityList(){
        mCityList=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private void prepareCityList(){
//        读取数据库路径代表的数据的
        mCityList=mCityDB.getAllCity();
    }

//    返回城市数据链表
    public List<City> getCityList(){
        return mCityList;
    }

    private CityDB openCityDB(){
        String path="/data"
                + Environment.getDataDirectory().getAbsolutePath()
                +File.separator+getPackageName()
                +File.separator+"databases1"
                +File.separator+CityDB.CITY_DB_NAME;
        File db=new File(path);
        Log.d(TAG,path);
        if(!db.exists()){
            String pathfolder="/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    +File.separator+getPackageName()
                    +File.separator+"databases1"
                    +File.separator;
            File dirFirstFolder=new File(pathfolder);
            if (!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db does not exists");

            try {
                InputStream is = getAssets().open("city.db");
//                db.createNewFile();
                FileOutputStream fos = new FileOutputStream(db);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this,path);
    }
}
