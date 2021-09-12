package com.example.usingtestingapiandother

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient())
            .build()

//  val progessBar by lazy { findViewById<ProgressBar>(R.id.progress_Bar) }
//    val temperature by lazy { findViewById<TextView>(R.id.temperature) }
//     val temperatureIcon by lazy { findViewById<ImageView>(R.id.temperature_Image) }
//     val cityName by lazy { findViewById<TextView>(R.id.city_name_TextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object:PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                    getLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.d("MainActivity","Permission Shown")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.d("MainActivity","Permission Denied")
                }

            })
            .check()
        setContentView(R.layout.activity_main)

        search_btn.setOnClickListener {
            val citynameedit=city_name_Edit_Text.text.toString()
            executeNetworkCall(citynameedit)
        }





//        val jsonString="{\"name\": \"Aung Aung\",\"phno\": 1234567}"
//        val jsonObject=JSONObject(jsonString)
//
//        val name=jsonObject.getString("name")
//        Log.d("JsonMessage",name)
//
//        val phno=jsonObject.getString("phno")
//        Log.d("JsonMessage",phno)

//
//        val responseString="{\"coord\":{\"lon\":96.17,\"lat\":16.84},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"base\":\"stations\",\"main\":{\"temp\":304.15,\"feels_like\":308.67,\"temp_min\":304.15,\"temp_max\":304.15,\"pressure\":1006,\"humidity\":70},\"visibility\":8000,\"wind\":{\"speed\":2.6,\"deg\":140},\"clouds\":{\"all\":40},\"dt\":1594798013,\"sys\":{\"type\":1,\"id\":9322,\"country\":\"MM\",\"sunrise\":1594768196,\"sunset\":1594815133},\"timezone\":23400,\"id\":1298824,\"name\":\"Yangon\",\"cod\":200}"
//
//
//        val moshi= Moshi.Builder()
//            .build()
//
//        try{
//            val adapter=moshi.adapter(OpenWeatherMapResponse::class.java)
//
//
//        val response=adapter.fromJson(responseString)
//
//        Log.d("JsonMessage",response.toString())
//        }catch(E:Exception){
//            Log.d("JsonMessage",E.toString())
//        }


    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        val locationManger =
            this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val location = locationManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        Log.d("MainActivity", location?.latitude.toString())
        executeNetworkCall(location?.latitude.toString(), location?.longitude.toString())
    }

    private fun showLoading() {
        if(progress_Bar != null && city_name_Edit_Text != null && temperature != null && temperature_Image!=null) {
            progress_Bar.visibility= View.VISIBLE
            city_name_Edit_Text.visibility=View.VISIBLE
            temperature.visibility=View.GONE
            temperature_Image.visibility=View.GONE
        }

    }

    private fun showData(cityNametv:String,temperaturetv:String,temperatureIcontv:String){
        if(progress_Bar != null && city_name_Edit_Text != null && temperature != null && temperature_Image!=null){
        progress_Bar.visibility= View.GONE
        city_name_Edit_Text.visibility=View.VISIBLE
        temperature.visibility=View.VISIBLE
        temperature_Image.visibility=View.VISIBLE
            search_btn.visibility=View.VISIBLE

            Glide.with(this).load(temperatureIcontv).into(temperature_Image)

//            city_name_Edit_Text.text=cityNametv
            val temp=temperaturetv.toFloat().toInt()
            temperature.text="$temp°C"
        }


    }

    //
    private fun executeNetworkCall(latitude:String,longtitude:String) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.openweathermap.org/data/2.5/")
//            .addConverterFactory(MoshiConverterFactory.create())
//            .client(OkHttpClient())
//            .build()
        showLoading()
        val weatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        weatherMapApi.geoCordinate(
            latitude=latitude,
            longitude = longtitude,
            appidkey = "2d988ccef53d1c967647c84caecdd42d",
        unit = "metric")
            .enqueue(object :retrofit2.Callback<OpenWeatherMapResponse>{
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d("Response",t.toString())
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if(response.isSuccessful){
                        response.body()
                            .let {
                            Log.d("Response",it.toString())

                                val iconUrl= it?.weather?.getOrNull(0)?.icon?:""
                                val fulUrl="https://openweathermap.org/img/wn/$iconUrl@2x.png"


                                showData(
                                    cityNametv= it!!.cityName,
                                    temperaturetv = it.main.temp,
                                temperatureIcontv =fulUrl)
                        }
                    }
                }

            })
    }

    private fun executeNetworkCall(cityName:String){
        showLoading()
        val weatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        weatherMapApi.geoCordinate(

            cityName = cityName,
            appidkey = "2d988ccef53d1c967647c84caecdd42d",
            unit = "metric"
        )
            .enqueue(object :retrofit2.Callback<OpenWeatherMapResponse>{
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d("Response",t.toString())
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                    ) {
                    if(response.isSuccessful){
                        response.body()
                            .let {
                                Log.d("Response",it.toString())

                                val iconUrl= it?.weather?.getOrNull(0)?.icon?:""
                                val fulUrl="https://openweathermap.org/img/wn/$iconUrl@2x.png"


                                showData(
                                    cityNametv= it!!.cityName,
                                    temperaturetv = it.main.temp,
                                    temperatureIcontv =fulUrl)
                            }
                    }
                }

            })

    }
}