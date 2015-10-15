package com.example.mycoolweather.reservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mycoolweather.service.AutoUpdateService;

public class AutoUpdateReService extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpdateService.class);
		context.startService(i);
		
	}

}
