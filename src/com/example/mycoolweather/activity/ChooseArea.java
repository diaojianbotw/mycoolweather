package com.example.mycoolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mycoolweather.R;
import com.example.mycoolweather.db.CoolWeatherDB;
import com.example.mycoolweather.model.City;
import com.example.mycoolweather.model.County;
import com.example.mycoolweather.model.Province;

public class ChooseArea extends Activity{

	private ListView listview;
	
	//等级标识
	private int current_leavel;
	private int provinceLeavel=0;
	private int cityLeavel=1;
	private int countyLeavel=2;
	//省市县集合
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.id.list_view);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listview = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(ChooseArea.this, android.R.layout.simple_list_item_1, dataList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(current_leavel == provinceLeavel)
				{
					queryCity();
				} else if(current_leavel == cityLeavel)
				{
					queryCounty();
				}
				
			}
		});
		queryProvince();
		
	}

	public void queryProvince()
	{
		provinceList = coolWeatherDB.getProvinceList();
		if(provinceList != null && provinceList.size() > 0)
		{
			dataList.clear();
			for(Province p:provinceList)
			{
				dataList.add(p.getProvinceName());
				adapter.notifyDataSetChanged();
			}
		}
		current_leavel = provinceLeavel;	
	}
	
	public void queryCity()
	{
		cityList = coolWeatherDB.getCityList();
		if(cityList != null && cityList.size() > 0)
		{
			dataList.clear();
			for(City city:cityList)
			{
				dataList.add(city.getCityName());
				adapter.notifyDataSetChanged();
			}
		}
		current_leavel = cityLeavel;
	}
	
	public void queryCounty()
	{
		countyList = coolWeatherDB.getCountyList();
		if(countyList != null && countyList.size() > 0)
		{
			dataList.clear();
			for(County county:countyList)
			{
				dataList.add(county.getCountyName());
				adapter.notifyDataSetChanged();
			}
		}
		current_leavel = countyLeavel;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	
}
