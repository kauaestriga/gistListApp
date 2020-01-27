package com.example.gistlistapp.Retrofit;

import com.example.gistlistapp.Objects.Gist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("gists/public")
    Call<List<Gist>> loadGists(@Query("page") String page);
}
