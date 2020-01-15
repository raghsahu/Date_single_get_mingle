package com.dating.datesinglegetmingle.Bean;

import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Interface.UserInterface;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {

    private static Retrofit retrofit = null;
    private static UserInterface loadInterface = null;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

    private static Retrofit getClient() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder().baseUrl(Config.Base_Url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserInterface loadInterface() {
        if (loadInterface == null) {
            loadInterface = AppConfig.getClient().create(UserInterface.class);
        }
        return loadInterface;
    }
}
