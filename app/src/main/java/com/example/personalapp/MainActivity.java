package com.example.personalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


    }

    public void startHouseActivity(View view){
        Intent houseIntent = new Intent(getApplicationContext(),HouseActivity.class);
        startActivity(houseIntent);
    }

    public void startRecordActivity(View view){
        Intent recordIntent = new Intent(getApplicationContext(),RecordActivity.class);
        startActivity(recordIntent);
    }

    public void startFinanceActivity(View view){
        Intent financeIntent = new Intent(getApplicationContext(),FinanceActivity.class);
        startActivity(financeIntent);
    }


}