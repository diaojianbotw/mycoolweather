package com.example.mycoolweather.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mycoolweather.R;
import com.example.mycoolweather.util.HttpCallBack;
import com.example.mycoolweather.util.HttpUtil;
import com.example.mycoolweather.util.Utility;

public class WeatherActivity extends Activity{

	private Button switchCity;
	private Button refresh;
	private TextView titleCity;
	private TextView publishTime;
	private TextView currentTime;
	private TextView weatherInfo;
	private TextView temp1;
	private TextView temp2;
	private LinearLayout liner;
	private String countyCode;
	private String address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_info);
		switchCity = (Button) findViewById(R.id.switch_city);
		refresh = (Button) findViewById(R.id.refresh);
		titleCity = (TextView) findViewById(R.id.title_county);
		publishTime = (TextView) findViewById(R.id.publish_time);
		currentTime = (TextView) findViewById(R.id.current_time);
		weatherInfo = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		liner = (LinearLayout) findViewById(R.id.weather_info_layout);
		countyCode = getIntent().getStringExtra("countyCode");
		if(!TextUtils.isEmpty(countyCode))
		{
			publishTime.setText("同步中");
			show();
		}
		titleCity.setVisibility(View.INVISIBLE);
		liner.setVisibility(View.INVISIBLE);
		
	}

	public void queryWeather(){
		address = "http://www.weather.com.cn/data/list3/city" +countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}
	
	public void queryWeatherInfo(String weatherCode)
	{
		address = "http://www.weather.com.cn/data/cityinfo/" +weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void queryFromServer(String address,final String type)
	{
		HttpUtil.sendRequest(address, new HttpCallBack() {
			
			@Override
			public void onerror(Exception e) {
				
				publishTime.setText("同步失败");
			}
			
			@Override
			public void finish(String respnse) {
				if("countyCode".equals(type))
				{
					String [] arr = respnse.split(",");
					String weatherCode = arr[1];
				} else if("weatherCode".equals(type))
				{
					Utility.handleWeatherInfo(WeatherActivity.this, respnse, countyCode);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							show();
							
						}
					});
				}
						
			}
		});
	}
	
	public void show()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
			JSONObject json = new JSONObject(pref.getString(countyCode, ""));
			titleCity.setText(json.getString("city"));
			publishTime.setText("今天 "+json.getString("ptime")+" 发布");
			currentTime.setText(sdf.format(new Date()));
			weatherInfo.setText(json.getString("weather"));
			temp1.setText(json.getString("temp1"));
			temp2.setText(json.getString("temp2"));
			titleCity.setVisibility(View.VISIBLE);
			liner.setVisibility(View.VISIBLE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
