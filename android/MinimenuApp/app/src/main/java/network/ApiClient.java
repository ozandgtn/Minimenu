package com.ozandgtn.minimenuapp.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Tek Retrofit istemcisi.
 * BASE_URL'i public yaptık ki UI tarafında (Glide ile resim yüklerken) erişebilelim.
 */
public final class ApiClient {

    // Emülatörde bilgisayara erişim: 10.0.2.2
    // Sonda / bırakmak, Retrofit baseUrl için önemlidir.
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private ApiClient() { /* no-op */ }
}
