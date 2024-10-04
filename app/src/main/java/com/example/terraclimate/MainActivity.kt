package com.example.terraclimate

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.terraclimate.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val apiKey = "d6c9df6cfbe600ef00ba1378cb4c4267"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("Delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val res = retrofit.getWeatherData(cityName, apiKey, "metric")

        res.enqueue(object : Callback<WeatherData> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherData>, res: Response<WeatherData>) {
                val resBody = res.body()

                if (res.isSuccessful && resBody != null) {
                    val temp = resBody.main.temp.toString()
                    val humidity = resBody.main.humidity
                    val windSpeed = resBody.wind.speed
                    val sunRise = resBody.sys.sunrise.toLong()
                    val sunSet = resBody.sys.sunset.toLong()
                    val seaLevel = resBody.main.pressure
                    val condition = resBody.weather.firstOrNull()?.main ?: "Unknown"
                    val maxTemp = resBody.main.temp_max.toString()
                    val minTemp = resBody.main.temp_min.toString()

                    binding.temp.text = "$temp °C"
                    binding.conditionType.text = condition
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Mim Temp : $minTemp °C"
                    binding.humidityValue.text = "$humidity %"
                    binding.windValue.text = "$windSpeed m/s"
                    binding.rainValue.text = condition
                    binding.sunriseValue.text = "${time(sunRise)}"
                    binding.sunsetValue.text = "${time(sunSet)}"
                    binding.seaValue.text = "$seaLevel hPa"

                    binding.dayTxt.text = dayName(System.currentTimeMillis())
                    binding.dateTxt.text = date()
                    binding.cityName.text = "$cityName"


                    changeBackgroundByCondition(condition)


                }
            }

            override fun onFailure(p0: Call<WeatherData>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeBackgroundByCondition(conditions: String) {
        when (conditions) {

            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }


            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }


            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }


            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
            binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timeStamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }


    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}