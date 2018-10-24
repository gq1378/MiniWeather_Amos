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
    private EditText city_search;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initView_city();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city_chosen=citylist.get(position).getNumber();
                title="当前城市："+citylist.get(position).getCity();
                title_name.setText(title);
                Toast.makeText(SelectCity.this,"你选择了"+cityData.get(position),Toast.LENGTH_SHORT).show();

            }
        });

        city_search=findViewById(R.id.city_search);
        city_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String str=s.toString();
                filterData(s.toString());
//                listView.setAdapter(adapter);
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

        MyApplication myApplication=(MyApplication) getApplication();
        citylist=myApplication.getCityList();
        cityData=new ArrayList<>();
        for(City city:citylist){
            String item=city.getCity()+" "+city.getNumber();
            cityData.add(item);
        }

        filterCityData=new ArrayList<>();
        filterCityData.addAll(cityData);
//        for(String data:cityData){
//                filterCityData.add(data);
//            }

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,filterCityData);
        listView=findViewById(R.id.city_list);
        listView.setAdapter(adapter);

    }

    private void filterData(String s){
//        List<String> newCityData=new ArrayList<>();
        filterCityData.clear();
        if(isEmpty(s)){
            filterCityData.addAll(cityData);
//            for(String data:cityData){
//                newCityData.add(data);
//            }
//            adapter.notifyDataSetChanged();
        }else {
            for(String str:cityData){
                if(str.contains(s)) {
                    filterCityData.add(str);
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
                i.putExtra("cityCode",city_chosen);
                setResult(RESULT_OK,i);
                finish();
                break;
                default:break;
        }
    }
}
