package com.example.refreshdemo;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequst(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
