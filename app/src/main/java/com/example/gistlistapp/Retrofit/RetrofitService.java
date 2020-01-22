package com.example.gistlistapp.Retrofit;

import com.example.gistlistapp.Objects.Gist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService {

    @GET("gists/public?page=0")
    Call<List<Gist>> loadGists();
}
