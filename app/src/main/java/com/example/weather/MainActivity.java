package com.example.weather;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.example.weather.databinding.ActivityMainBinding;
import com.airbnb.lottie.LottieAnimationView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchWeatherData("kolhapur");
        setupSearchView();
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchWeatherData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void fetchWeatherData(String cityname) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();

        APIinterface apiInterface = retrofit.create(APIinterface.class);

        Call<weatherapp> call = apiInterface.getWeatherData(cityname, "5fb95870250c1b493cd9376d0b4ad20d", "metric");

        call.enqueue(new Callback<weatherapp>() {
            @Override
            public void onResponse(Call<weatherapp> call, Response<weatherapp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherapp weatherApp = response.body();

                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    String day = dayFormat.format(calendar.getTime());
                    String date = dateFormat.format(calendar.getTime());

                    String temperature = String.valueOf(weatherApp.getMain().getTemp());
                    String humidity = String.valueOf(weatherApp.getMain().getHumidity());
                    String windspeed = String.valueOf(weatherApp.getWind().getSpeed());
                    String sunrise = String.valueOf(weatherApp.getSys().getSunrise());
                    String sunset = String.valueOf(weatherApp.getSys().getSunset());
                    String sealevel = String.valueOf(weatherApp.getMain().getSea_level());
                    String maxtemp = String.valueOf(weatherApp.getMain().getTemp_max());
                    String mintemp = String.valueOf(weatherApp.getMain().getTemp_min());
                    String condition = weatherApp.getWeather().isEmpty() ? "unknown" : weatherApp.getWeather().get(0).getMain();

                    binding.tempreture.setText(temperature + "°C");
                    binding.humidity.setText(humidity + "%");
                    binding.wind.setText(windspeed + "m/s");
                    binding.sunrise.setText(time(Long.parseLong(sunrise)));
                    binding.sunset.setText(time(Long.parseLong(sunset)));
                    binding.sea.setText(sealevel + "hPa");
                    binding.max.setText("Max: " + maxtemp + "°C");
                    binding.min.setText("Min: " + mintemp + "°C");
                    binding.weather.setText(condition);
                    binding.day.setText(day);
                    binding.date.setText(date);
                    binding.city.setText(cityname);
                    binding.sunny.setText(condition);
                    changeImage(condition);
                } else {
                    Log.e("TAG", "Response unsuccessful or body is null");
                }
            }

            private void changeImage(String condition) {
                LottieAnimationView animationView = binding.lottieAnimationView;
                Log.d("MainActivity", "Changing image for condition: " + condition);
                switch (condition) {
                    case "Clear Sky":
                    case "Sunny":
                    case "Clear":
                        binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                        animationView.setAnimation(R.raw.sun);
                        break;

                    case "Partly Clouds":
                    case "Clouds":
                    case "Overcast":
                    case "Mist":
                    case "Foggy":
                        binding.getRoot().setBackgroundResource(R.drawable.cloud_background);
                        animationView.setAnimation(R.raw.cloud);
                        break;

                    case "Light Rain":
                    case "Drizzle":
                    case "Moderate Rain":
                    case "Showers":
                    case "Rain":
                    case "Heavy Rain":
                        binding.getRoot().setBackgroundResource(R.drawable.rain_background);
                        animationView.setAnimation(R.raw.rain);
                        break;

                    case "Light Snow":
                    case "Moderate Snow":
                    case "Heavy Snow":
                    case "Blizzard":
                        binding.getRoot().setBackgroundResource(R.drawable.snow_background);
                        animationView.setAnimation(R.raw.snow);
                        break;

                    default:
                        binding.getRoot().setBackgroundResource(R.drawable.clear_sky);
                        animationView.setAnimation(R.raw.sun);
                        break;
                }
                animationView.playAnimation();
            }

            private String time(long timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return sdf.format(new Date(timestamp * 1000));
            }

            @Override
            public void onFailure(Call<weatherapp> call, Throwable t) {
                Log.e("TAG", "Failed to fetch weather data", t);
                Toast.makeText(MainActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
