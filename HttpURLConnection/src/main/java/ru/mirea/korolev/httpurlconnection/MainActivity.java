package ru.mirea.korolev.httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.korolev.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = null;
                if (connectivityManager != null) {
                    networkinfo = connectivityManager.getActiveNetworkInfo();
                }
                if (networkinfo != null && networkinfo.isConnected()) {

                    new DownloadPageTask().execute("https://ipinfo.io/json"); // запуск нового потока

                } else {
                    Toast.makeText(MainActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = null;
                if (connectivityManager != null) {
                    networkinfo = connectivityManager.getActiveNetworkInfo();
                }
                if (networkinfo != null && networkinfo.isConnected()) {

                    new DownloadPageTaskWeather().execute("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true");

                } else {
                    Toast.makeText(MainActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.tvTest.setText("Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            //binding.tvIp.setText(result);
            Log.d(MainActivity.class.getSimpleName(), result);
            try {
                binding.etTimezone.setVisibility(View.VISIBLE);
                binding.tvTimezone.setVisibility(View.VISIBLE);
                binding.etReadme.setVisibility(View.VISIBLE);
                binding.tvReadme.setVisibility(View.VISIBLE);
                JSONObject responseJson = new JSONObject(result);
                Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
                String ip = responseJson.getString("ip");
                binding.etIp.setText(responseJson.getString("ip"));
                binding.etCity.setText(responseJson.getString("city"));
                binding.etRegion.setText(responseJson.getString("region"));
                binding.etCountry.setText(responseJson.getString("country"));
                binding.etLoc.setText(responseJson.getString("loc"));
                binding.etOrg.setText(responseJson.getString("org"));
                binding.etPostal.setText(responseJson.getString("postal"));
                binding.etTimezone.setText(responseJson.getString("timezone"));
                binding.etReadme.setText(responseJson.getString("readme"));
                //binding.textHost.setText("Hostname: " + responseJson.getString("hostname"));
                Log.d(MainActivity.class.getSimpleName(), "IP: " + ip);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
        private String downloadIpInfo(String address) throws IOException {
            InputStream inputStream = null;
            String data = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(100000);
                connection.setConnectTimeout(100000);
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read = 0;
                    while ((read = inputStream.read()) != -1) {
                        bos.write(read);
                    }
                    bos.close();
                    data = bos.toString();
                } else {
                    data = connection.getResponseMessage() + ". Error Code: " + responseCode;
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return data;
        }
    }
    private class DownloadPageTaskWeather extends AsyncTask<String, Void, String> {
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.tvTest.setText("Temperature: Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            //binding.tvTest.setText(result);
            Log.d(MainActivity.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
                JSONObject weather = responseJson.getJSONObject("current_weather");
                binding.etIp.setText(weather.getString("temperature"));
                binding.tvIp.setText("Temperature: ");
                binding.etCity.setText(weather.getString("windspeed"));
                binding.tvCity.setText("WindSpeed: ");
                binding.etRegion.setText(weather.getString("winddirection"));
                binding.tvRegion.setText("WindDirection: ");
                binding.etCountry.setText(weather.getString("time"));
                binding.tvCountry.setText("Time: ");
                binding.etLoc.setText(responseJson.getString("latitude"));
                binding.tvLoc.setText("Latitude: ");
                binding.etOrg.setText(responseJson.getString("longitude"));
                binding.tvOrg.setText("Longitude: ");
                binding.etPostal.setText(responseJson.getString("elevation"));
                binding.tvPostal.setText("Elevation: ");
                binding.etTimezone.setVisibility(View.GONE);
                binding.tvTimezone.setVisibility(View.GONE);
                binding.etReadme.setVisibility(View.GONE);
                binding.tvReadme.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read); }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage()+". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
