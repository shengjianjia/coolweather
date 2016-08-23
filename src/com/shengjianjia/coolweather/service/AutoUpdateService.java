package com.shengjianjia.coolweather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.shengjianjia.coolweather.receiver.AutoUpdateReceiver;
import com.shengjianjia.coolweather.util.HttpCallbackListener;
import com.shengjianjia.coolweather.util.HttpUtil;
import com.shengjianjia.coolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;


public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					updateWeather();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 更新天气
	 * @throws UnsupportedEncodingException
	 */
	private void updateWeather() throws UnsupportedEncodingException{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String cityName = prefs.getString("city_name", "");
		String address = "http://api.map.baidu.com/telematics/v3/weather?location=" + URLEncoder.encode(cityName,"utf-8") + "&output=json&ak=FK9mkfdQsloEngodbFl4FeY3";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response, cityName);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
