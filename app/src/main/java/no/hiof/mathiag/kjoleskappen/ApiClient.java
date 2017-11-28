package no.hiof.mathiag.kjoleskappen;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mathi on 15.10.2017.
 */

//tar for seg urlen til api-et.
public class ApiClient {
    public static final String BASE_URL = "https://kolonial.no/api/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
