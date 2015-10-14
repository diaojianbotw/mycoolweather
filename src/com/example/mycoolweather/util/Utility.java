package com.example.mycoolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.mycoolweather.db.CoolWeatherDB;
import com.example.mycoolweather.model.City;
import com.example.mycoolweather.model.County;
import com.example.mycoolweather.model.Province;

public class Utility {
	
	public synchronized static boolean handProvinceResponse(String respnse,CoolWeatherDB coolWeatherDB)
	{
		if(!TextUtils.isEmpty(respnse)){
			String [] allProvince = respnse.split(",");
			if(allProvince!=null && allProvince.length>0)
			{
				for(String p:allProvince)
				{
					String [] pro = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(pro[0]);
					province.setProvinceName(pro[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handCityResponse(String response,CoolWeatherDB coolWeatherDB,int provinceId)
	{
		if(!TextUtils.isEmpty(response)){
			String [] allCity = response.split(",");
			if(allCity!=null && allCity.length>0)
			{
				for(String p:allCity)
				{
					String [] cit = p.split("\\|");
					City city = new City();
					city.setCityCode(cit[0]);
					city.setCityName(cit[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handCountyResponse(String response,CoolWeatherDB coolWeatherDB,int cityId )
	{
		if(TextUtils.isEmpty(response))
		{
			String [] allCounty = response.split(",");
			if(allCounty!=null && allCounty.length>0)
			{
				for(String p:allCounty)
				{
					String [] coun = p.split("\\|");
					County county = new County();
					county.setCountyCode(coun[0]);
					county.setCountyName(coun[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static void handleWeatherInfo(Context context,String response,String countyCode)
	{
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo =  jsonObject.getJSONObject("weatherinfo");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyƒÍM‘¬d»’",Locale.CHINA);
			weatherInfo.put("currentData", sdf.format(new Date()));
			saveWeatherInfo(context,weatherInfo,countyCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void saveWeatherInfo(Context context,JSONObject weatherInfo,String countyCode)
	{
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(countyCode, weatherInfo.toString());
		editor.commit();
	}
}
