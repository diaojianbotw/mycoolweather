package com.example.mycoolweather.service;

import com.example.mycoolweather.reservice.AutoUpdateReService;
import com.example.mycoolweather.util.HttpCallBack;
import com.example.mycoolweather.util.HttpUtil;
import com.example.mycoolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service{

	String weatherCode;
	String countyCode;
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		weatherCode = intent.getStringExtra("weatherCode");
		countyCode = intent.getStringExtra("countyCode");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
				
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int min = 1000*6;
		long time = SystemClock.elapsedRealtime()+min;
		Intent i = new Intent(AutoUpdateService.this,AutoUpdateReService.class);
		PendingIntent pi =  PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void updateWeather()
	{
		String address =  "http://www.weather.com.cn/data/cityinfo/" +weatherCode + ".html";
		HttpUtil.sendRequest(address, new HttpCallBack() {
			
			@Override
			public void onerror(Exception e) {
				e.printStackTrace();
			}
			
			@Override
			public void finish(String respnse) {
				// TODO Auto-generated method stub
				Utility.handleWeatherInfo(AutoUpdateService.this, respnse, countyCode);
			}
		});
	}
}
