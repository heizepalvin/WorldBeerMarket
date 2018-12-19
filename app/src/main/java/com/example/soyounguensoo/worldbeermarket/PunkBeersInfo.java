package com.example.soyounguensoo.worldbeermarket;


import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PunkBeersInfo {
    @GET("v2/beers")
    Call<JsonArray> getBeersInfo();
    @GET("v2/beers/random")
    Call<JsonArray> getRandomBeersInfo();
    @GET("v2/beers")
    Call<JsonArray> getBeersInfoPaging(@Query("page") int page);
//    @GET("v2/beers")
//    Call<JsonArray> getBeersABVInfo(@Query("abv_lt") String abv_lt);
//    @GET("v2/beers")
//    Call<JsonArray> getBeersYeast(@Query("yeast") String yeast);
}
