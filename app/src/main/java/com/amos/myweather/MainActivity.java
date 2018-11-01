package com.amos.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER=1;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,
            windTv,city_name_Tv,wenduTv;
    private ImageView mUpdateBtn,weatherImg,pmImag;
    private ProgressBar mUpdateProgress;

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

        if(view.getId()==R.id.title_update_btn){

            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String citycode=sharedPreferences.getString("main_city_code","101010100");
            Log.d("MyWeather",citycode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("MyWeather","网络OK");
                mUpdateBtn.setVisibility(View.INVISIBLE);
                mUpdateProgress=findViewById(R.id.title_update_progress);
                mUpdateProgress.setVisibility(View.VISIBLE);
                queryWeatherCode(citycode);
            }else {
                Log.d("MyWeather","网络挂了");
                Toast.makeText(this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("MyWeather","选择的城市代码为"+newCityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("MyWeather","网络OK");
                mUpdateBtn.setVisibility(View.INVISIBLE);
                mUpdateProgress=findViewById(R.id.title_update_progress);
                mUpdateProgress.setVisibility(View.VISIBLE);
                queryWeatherCode(newCityCode);
            }else {
                Log.d("MyWeather","网络挂了");
                Toast.makeText(this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }


//  prama citycode
    private void queryWeatherCode(String cityCode){
        final String address="http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
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
}
