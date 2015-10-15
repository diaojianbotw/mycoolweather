package com.example.mycoolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mycoolweather.R;
import com.example.mycoolweather.db.CoolWeatherDB;
import com.example.mycoolweather.model.City;
import com.example.mycoolweather.model.County;
import com.example.mycoolweather.model.Province;
import com.example.mycoolweather.util.HttpCallBack;
import com.example.mycoolweather.util.HttpUtil;
import com.example.mycoolweather.util.Utility;

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
	//参数
	private String countyCode;
	private String address;
	private Province selectProvince;
	private City selectCity;
	private County countySelect;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
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
					selectProvince = provinceList.get(position);
					queryCity();
				} else if(current_leavel == cityLeavel)
				{
					selectCity = cityList.get(position);
					queryCounty();
				} else if(current_leavel == countyLeavel)
				{
					countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseArea.this,WeatherActivity.class);
					intent.putExtra("countyCode", countyCode);
					startActivity(intent);
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
			}
			adapter.notifyDataSetChanged();
			current_leavel = provinceLeavel;
		}
		else
		{
			queryFromServer(null,"province");
		}
			
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
			}
			adapter.notifyDataSetChanged();
			current_leavel = cityLeavel;
		}
		else
		{
			queryFromServer(selectProvince.getProvinceCode(),"city");
		}
		
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
			}
			adapter.notifyDataSetChanged();
			current_leavel = countyLeavel;
		}
		else
		{
			queryFromServer(selectCity.getCityCode(),"county");
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(current_leavel == countyLeavel)
		{
			queryCity();
		} else if(current_leavel == cityLeavel){
			queryProvince();
		} else{
			finish();
		}
		
	}

	public void queryFromServer(String code,final String type)
	{
		showDialog();
		if(!TextUtils.isEmpty(code))
		{
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		else
		{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		HttpUtil.sendRequest(address, new HttpCallBack() {
			
			@Override
			public void onerror(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeDialog();
						Toast.makeText(ChooseArea.this, "加载失败", Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
			
			@Override
			public void finish(String respnse) {
				// TODO Auto-generated method stub
				boolean result = false;
				if("province".equals(type))
				{
					result = Utility.handProvinceResponse(respnse, coolWeatherDB);
				}else if("city".equals(type))
				{
					result = Utility.handCityResponse(respnse, coolWeatherDB, selectProvince.getId());
				} else if("county".equals(type)){
					result = Utility.handCountyResponse(respnse, coolWeatherDB, selectCity.getId());
				}
				if(result)
				{
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeDialog();
							if("province".equals(type))
							{
								queryProvince();
							}else if("city".equals(type))
							{
								queryCity();
							} else if("county".equals(type)){
								queryCounty();
							}
							
						}
					});
				}
			}
		});
	}
	
	public void showDialog()
	{
		if(dialog==null)
		{
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在加载请稍候...");
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();
	}
	
	public void closeDialog()
	{
		if(dialog!=null)
		{
			dialog.dismiss();
		}
	}
}
