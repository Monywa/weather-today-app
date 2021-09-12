package com.example.usingtestingapiandother

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenWeatherMapApi{
    @GET("weather")
    fun geoCordinate(
        @Query("lat")latitude:String,
        @Query("lon") longitude:String,
        @Query("appid")appidkey:String,
        @Query("units") unit:String
    ):Call<OpenWeatherMapResponse>

    @GET("weather")
    fun geoCordinate(
        @Query("q")cityName:String,
        @Query("appid")appidkey:String,
        @Query("units") unit:String
    ):Call<OpenWeatherMapResponse>

}