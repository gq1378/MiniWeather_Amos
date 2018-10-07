package com.amos.myweather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
*created by amos on 10.1.18
*/

public class MainActivity extends Activity implements View.OnClickListener{
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button1:Toast.makeText(MainActivity.this,"Hello!",Toast.LENGTH_SHORT).show();
        }
    }
}
