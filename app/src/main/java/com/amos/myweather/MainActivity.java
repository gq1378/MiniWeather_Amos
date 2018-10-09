package com.amos.myweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amos.util.NetUtil;


/**
*created by amos on 10.1.18
*/

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("MyWeather","网络OK");
            Toast.makeText(MainActivity.this, "网络OK", Toast.LENGTH_LONG).show();
        }else{
            Log.d("MyWeather","网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }

    }
}
