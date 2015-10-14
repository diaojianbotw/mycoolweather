package com.example.mycoolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class HttpUtil {
	
	

	public static void sendRequest(final String address,final HttpCallBack httpcallback){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader read = new BufferedReader(new InputStreamReader(in));
					StringBuilder respnse = null;
					String line;
					while(( line = read.readLine())!=null)
					{
						respnse.append(line);
						if(httpcallback!=null)
						{
							httpcallback.finish(respnse.toString());
						}	
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(httpcallback!=null)
					{
						httpcallback.onerror(e);
					}
				}
				finally{
					if(connection != null)
					{
						connection.disconnect();
					}
				}
			}
		});
	}
	
}
