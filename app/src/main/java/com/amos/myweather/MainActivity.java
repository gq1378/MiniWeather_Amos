package com.amos.myweather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amos.app.MyApplication;
import com.amos.bean.TodayWeather;
import com.amos.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER=1,PERMISSION_REQUESTCODE=0;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,
            windTv,city_name_Tv,wenduTv;
    private ImageView mLocBtn,mUpdateBtn,weatherImg,pmImag;
    private ProgressBar mUpdateProgress;
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids={R.id.iv1,R.id.iv2};
    public AMapLocationClient mLocationClient;
    public AMapLocationClientOption mLocationOption;
    public AMapLocationListener mLocationListener;
//    public AMapLocation mLocation;
    protected String[] needPermissions={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
         };

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    mUpdateProgress.setVisibility(View.INVISIBLE);
                    mUpdateBtn.setVisibility(View.VISIBLE);
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                    default: break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        checkPermissions(needPermissions);

        mLocBtn=findViewById(R.id.title_location);
        mLocBtn.setOnClickListener(this);
        mUpdateBtn = findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("MyWeather","网络OK");
            Toast.makeText(this, "网络OK", Toast.LENGTH_LONG).show();
        }else{
            Log.d("MyWeather","网络挂了");
            Toast.makeText(this, "网络挂了", Toast.LENGTH_LONG).show();
        }

        ImageView mCitySelect=findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }

        if(view.getId()==R.id.title_location){
            pingCityCode();
        }

        if(view.getId()==R.id.title_update_btn){
            queryWeatherCode();
        }
    }

    private void pingCityCode(){
        mUpdateBtn.setVisibility(View.INVISIBLE);
        mUpdateProgress=findViewById(R.id.title_update_progress);
        mUpdateProgress.setVisibility(View.VISIBLE);

        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation!=null){
                    if(aMapLocation.getErrorCode()==0){
                        Log.d("MyWeather","OK"+aMapLocation.getCity()+aMapLocation.getCityCode());
                        SharedPreferences sharePreferences=getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharePreferences.edit();
                        String city=aMapLocation.getCity().replace("市","");
                        editor.putString("locCitycode",city);
                        editor.apply();
                        queryWeatherCode();
                        mLocationClient.stopLocation();
                        mLocationClient.onDestroy();
                    }else {
                        Log.e("MyWeather","location Error, ErrCode:"
                                +aMapLocation.getErrorCode()+",errInfo:"
                                +aMapLocation.getErrorInfo());
                    }
                }
            }
        };

        mLocationClient=new AMapLocationClient(this);
        mLocationOption=new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setHttpTimeOut(5000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();

//        mLocation=new AMapLocation("");
//        mLocation.setCity("北京市");
////        mLocation.setLatitude(39.9118777862D);
////        mLocation.setLongitude(116.3946955852D);
////        mLocation=mLocationClient.getLastKnownLocation();
//
//        Log.d("MyWeather", "OK9" + mLocation.getCity() + mLocation.getCityCode());
//        SharedPreferences sharePreferences = getSharedPreferences("config", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharePreferences.edit();
//        String city = mLocation.getCity().replace("市", "");
//        editor.putString("locCitycode", city);
//        editor.apply();
//        queryWeatherCode();
//        Log.d("MyWeather","OK7");
//        Log.d("MyWeather",mLocation.getCity());
//        Log.d("MyWeather","OK8");

//        mLocationClient.stopLocation();
//        mLocationClient.onDestroy();

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCity=data.getStringExtra("city");

            SharedPreferences sharePreferences=getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharePreferences.edit();
            editor.putString("locCitycode",newCity);
            editor.apply();

            Log.d("MyWeather","选择的城市为"+newCity);
            queryWeatherCode();
        }
    }


//  prama citycode
    private void queryWeatherCode(){
        mUpdateBtn.setVisibility(View.INVISIBLE);
        mUpdateProgress=findViewById(R.id.title_update_progress);
        mUpdateProgress.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        String city=sharedPreferences.getString("locCitycode","郑州");
        Log.d("MyWeather",city);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("MyWeather","网络OK");
        }else {
            mUpdateProgress.setVisibility(View.INVISIBLE);
            mUpdateBtn.setVisibility(View.VISIBLE);
            Log.d("MyWeather","网络挂了");
            Toast.makeText(this,"网络挂了！",Toast.LENGTH_LONG).show();
            return;
        }

        final String address="http://wthrcdn.etouch.cn/WeatherApi?citykey="+MyApplication.getInstance().cityToNum(city);
        Log.d("MyWeather",address);
        new Thread(new Runnable(){
            @Override
            public void run(){
                HttpURLConnection con=null;
//                TodayWeather todayWeather;
                try{
                    URL url=new URL(address);
                    con=(HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("MyWeather",str);
                    }
                    String responseStr=response.toString();
                    Log.d("MyWeather",responseStr);

                    TodayWeather todayWeather=parseXML(responseStr);
                    if(todayWeather!=null)
                        Log.d("MyWeather",todayWeather.toString());
//
                    Message msg=new Message();
                    msg.what=UPDATE_TODAY_WEATHER;
                    msg.obj=todayWeather;
                    mHandler.sendMessage(msg);

                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(con!=null) con.disconnect();
                }
            }
        }).start();
    }

    void initView(){
        city_name_Tv=findViewById(R.id.title_city_name);
        cityTv=findViewById(R.id.city);
        timeTv=findViewById(R.id.time);
        humidityTv=findViewById(R.id.humidity);
        weekTv=findViewById(R.id.week);
        pmDataTv=findViewById(R.id.pm_data);
        pmQualityTv=findViewById(R.id.pm_quality);
        temperatureTv=findViewById(R.id.temperature);
        climateTv=findViewById(R.id.climate);
        windTv=findViewById(R.id.wind);
        wenduTv=findViewById(R.id.temperature_today);
        weatherImg=findViewById(R.id.weather_img);
        pmImag=findViewById(R.id.pm2_5_img);

        cityTv.setText("N/A");
        city_name_Tv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        wenduTv.setText("N/A");

        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<>();
        views.add(inflater.inflate(R.layout.next123day, null));
        views.add(inflater.inflate(R.layout.next456day, null));
        vpAdapter = new ViewPagerAdapter(views, this);
        vp = findViewById(R.id.vp);
        vp.setAdapter(vpAdapter);
        vp.addOnPageChangeListener(this);

        dots = new ImageView[views.size()];
        for (int i = 0; i < ids.length; ++i) {
            dots[i] = findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<ids.length;++i){
            if(i==position){
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("MyWeather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"))
                            todayWeather=new TodayWeather();
//                        if(todayWeather!=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("fengxiang") && (fengxiangCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("fengli") && (fengliCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("date") && (dateCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("high") && (highCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            }else if (xmlPullParser.getName().equals("low") && (lowCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            }else if (xmlPullParser.getName().equals("type") && (typeCount++)==0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                            }
//                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        }catch (XmlPullParserException|IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCityName());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime());
        humidityTv.setText(todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHL());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengli());
        wenduTv.setText(todayWeather.getWendu());

        if(todayWeather.getPmImag()<51)
            pmImag.setImageResource(R.drawable.biz_plugin_weather_0_50);
        else if(todayWeather.getPmImag()<101)
            pmImag.setImageResource(R.drawable.biz_plugin_weather_51_100);
        else if(todayWeather.getPmImag()<151)
            pmImag.setImageResource(R.drawable.biz_plugin_weather_101_150);
        else if(todayWeather.getPmImag()<201)
            pmImag.setImageResource(R.drawable.biz_plugin_weather_151_200);
        else pmImag.setImageResource(R.drawable.biz_plugin_weather_201_300);

        switch (todayWeather.getType()){
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "晴": default:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
        }

        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
    }



    /**
     * 检查权限
     *
     */
    private void checkPermissions(String[] permissions) {
        //获取权限列表
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(new String[0]),
                    PERMISSION_REQUESTCODE);
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }
}
