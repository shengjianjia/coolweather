package com.shengjianjia.coolweather.util;

public interface HttpCallbackListener {
	
	void onFinish(String response);
	
	void onError(Exception e);
}
