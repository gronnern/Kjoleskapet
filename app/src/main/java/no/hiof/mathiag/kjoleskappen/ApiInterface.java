package no.hiof.mathiag.kjoleskappen;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mathi on 15.10.2017.
 */

//denne håndterer de nødvendige parameterne som api-et må ha.
public interface ApiInterface {

    @Headers({"Accept:application/json", "Content-Type:application/json", "User-Agent:USERAGENT", "X-Client-Token:TOKEN"})

    @GET("v1/search")
    Call<VareResponse> getVare(@Query("q") String strekkode);

    @GET("v1/{id}")
    Call<VareResponse> getVareId(@Path("id") int id, @Query("q") String apiKey);
}
