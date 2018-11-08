package com.amos.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amos.app.MyApplication;
import com.amos.bean.City;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class SelectCity extends Activity implements View.OnClickListener{
    private String city_chosen="101010100";
    private String title;
    private ListView listView;
    private TextView title_name;
    private List<City> citylist;
    private List<String> cityData;
    private List<String> filterCityData;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initView_city();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city_chosen=filterCityData.get(position);
                title="当前城市："+filterCityData.get(position);
                title_name.setText(title);
                Toast.makeText(SelectCity.this,"你选择了"+filterCityData.get(position),Toast.LENGTH_SHORT).show();

            }
        });

        EditText city_search=findViewById(R.id.city_search);
        city_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(after>10){
                    Toast.makeText(SelectCity.this,"字太多了！",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count<11) filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView mBackBtn=findViewById(R.id.titel_back);
        mBackBtn.setOnClickListener(this);

    }

    private void initView_city(){
        title_name=findViewById(R.id.title_name);

        MyApplication myApplication=MyApplication.getInstance();
        citylist=myApplication.getCityList();
        cityData=new ArrayList<>();
        for(City city:citylist){
            String item=city.getCity();
            cityData.add(item);
        }

        filterCityData=new ArrayList<>();
        filterCityData.addAll(cityData);

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,filterCityData);
        listView=findViewById(R.id.city_list);
        listView.setAdapter(adapter);

    }

    private void filterData(String s){
        filterCityData.clear();
        if(isEmpty(s)){
            filterCityData.addAll(cityData);
        }else {
            for(City city:citylist){
                if(city.toString().contains(s)) {
                    String item=city.getCity();
                    filterCityData.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.titel_back:
                Intent i=new Intent();
                i.putExtra("city",city_chosen);
                setResult(RESULT_OK,i);
                finish();
                break;
                default:break;
        }
    }
}
