package com.example.personalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorManageActivity extends AppCompatActivity{
    String auth_token = "EmXo_WkxwZkqMBoNbAyxKXavm79vWkYv";
    boolean statusLed = false;
    SwitchCompat switchLed;
    TextView textHistoryStatus;
    TextView textResultMeasurement;
    TextView textStatus;
    ImageView imageSound;
    BottomNavigationView bottomNavigationView;

    SharedPreferences sp;
    //From esp32
    float v5 = 0;
    String v6 = "";
    int v1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_manage);
        switchLed = findViewById(R.id.switchLed);
        textHistoryStatus = findViewById(R.id.textHistoryStatus);
        textResultMeasurement = findViewById(R.id.textResultMeasurement);
        textStatus = findViewById(R.id.textStatus);
        imageSound = findViewById(R.id.imageSound);
        sp = getSharedPreferences("appPreference",MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.d("Menu", String.valueOf(item.getItemId()));
            switch (item.getItemId()) {
                case R.id.menuHome:
                    Log.d("Menu", "Home selected");
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    return true;

                case R.id.menuSensor:
                    Log.d("Menu", "Sensor selected");
                    Intent houseIntent = new Intent(getApplicationContext(), HouseActivity.class);
                    startActivity(houseIntent);
                    finish();
                    return true;

                case R.id.menuFinance:
                    Log.d("Menu", "Finance selected");
                    Intent financeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(financeIntent);
                    finish();
                    return true;


                case R.id.menuAccount:
                    Log.d("Menu", "Account selected");
                    Intent accountIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(accountIntent);
                    finish();
                    return true;

                default:
                    Log.d("Menu", "Default selected");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
        });

        textHistoryStatus.setText(sp.getString("lastConnection","Unknown"));

        //Initialize status device
        try {
            getDeviceStatus();
            getDeviceData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("Toggle","ON!");
                    try {
                        statusLed = true;
                        sp.edit().putBoolean("statusLed",statusLed).apply();
                        operateSensor("https://blynk.cloud/external/api/update?token="+auth_token+"&v0=1");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Toggle","Enabled!");
                } else {
                    try {
                        statusLed = false;
                        sp.edit().putBoolean("statusLed",statusLed).apply();
                        operateSensor("https://blynk.cloud/external/api/update?token="+auth_token+"&v0=0");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Toggle","Disabled!");
                    // The toggle is disabled
                }
            }
        });


    }

    public void checkDevice(View view) throws IOException {
        getDeviceStatus();
    }

    public void operateSensor(String getUrl) throws IOException {
        Log.d("Request","Operate Sensor D2");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getUrl)
                .build();

         client.newCall(request).enqueue(new Callback() {
             @Override
             public void onFailure(@NonNull Call call, @NonNull IOException e) {
                 call.cancel();
             }

             @Override
             public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                 SystemClock.sleep(1500);
                 getDeviceData();
             }
         });
    }

    public void getDeviceData() throws IOException {
        Log.d("Request","Get Device Data");
        OkHttpClient client = new OkHttpClient();
        String getUrl = "https://blynk.cloud/external/api/get?token="+auth_token+"&v5&v6&v1";
        Request request = new Request.Builder()
                .url(getUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Response Code", String.valueOf(response.code()));
                String jsonStr =  Objects.requireNonNull(response.body()).string();
                Log.d("Response Body",jsonStr);
                if (response.code() == 200){
                    getDeviceDataJson(jsonStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        textHistoryStatus.setText(v6);
                        sp.edit().putString("lastConnection",v6).apply();
                        sp.edit().putFloat("lastMeasure", v5).apply();
                        sp.edit().putInt("lastLed",v1).apply();
                        String measUltraSound = String.format("%.2f cm",v5);
                        textResultMeasurement.setText(measUltraSound);
                        if (v1==1){
                            imageSound.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        }
                        else{
                            imageSound.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                        }
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SensorManageActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    public void getDeviceDataJson(String datas){
        JSONObject obj = null;
        try {
            obj = new JSONObject(datas);
            JSONObject contentJson = null;
            v5 = (float) obj.getDouble("v5");
            v6 = obj.getString("v6");
            v1 = obj.getInt("v1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getDeviceStatus() throws IOException {
        String URL_DEVICE_STATUS = "https://blynk.cloud/external/api/isHardwareConnected?token="+auth_token;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_DEVICE_STATUS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonStr =  Objects.requireNonNull(response.body()).string();
                Log.d("Response Code", String.valueOf(response.code()));
                Log.d("Response Body", jsonStr);

                if (response.code() == 200){
                    if (jsonStr.equals("true")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                textStatus.setText("ONLINE");
                                textStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        });

                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textStatus.setText("OFFLINE");
                                textStatus.setTextColor(getResources().getColor(R.color.red));
                            }
                        });
                    }

                }

            }
        });
    }



}