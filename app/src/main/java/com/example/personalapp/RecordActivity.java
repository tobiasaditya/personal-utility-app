package com.example.personalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {
    String baseUrl = "https://obider-transaction-service.herokuapp.com/transaction";
    //    RadioGroup trxTypeRadio;
    EditText trxAmount;
    EditText trxDesc;
    Spinner trxMethodSpinner;
    Spinner trxTypeSpinner;

    ProgressBar submitProgress;


    String trxType = null;
    String amount = null;
    String desc = null;
    String trxMethod = null;
    String jsonStr = null;

    String accessToken = null;

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        trxAmount = findViewById(R.id.trxAmount);
        trxDesc = findViewById(R.id.trxDesc);
        trxMethodSpinner = findViewById(R.id.trxMethodSpinner);
        trxTypeSpinner = findViewById(R.id.trxTypeSubmit);
        sp = getSharedPreferences("appPreference",MODE_PRIVATE);
        accessToken = sp.getString("accessToken","");

        submitProgress = findViewById(R.id.progressBarSubmit);

        submitProgress.setVisibility(View.GONE);
    }
    public void submitTrx(View view) throws IOException {
        trxType = trxTypeSpinner.getSelectedItem().toString();
        amount = trxAmount.getText().toString();
        desc = trxDesc.getText().toString();
        trxMethod = trxMethodSpinner.getSelectedItem().toString();

        Log.i("Input",trxType+""+amount+""+desc+""+trxMethod);
        String body = "{\"trxType\":\"%s\",\"amount\":\"%s\",\"desc\":\"%s\",\"trxMethod\":\"%s\"}";
        String finalBody = String.format(body, trxType,amount,desc,trxMethod);
        Log.d("Payload",finalBody);
        submitProgress.setVisibility(View.VISIBLE);

        submitRecord(baseUrl+"/add",finalBody);

        trxAmount.setText(null);
        trxDesc.setText(null);
    }
    void submitRecord(String postUrl, String body) throws IOException {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(body,mediaType);


        Request request = new Request.Builder()
                .url(postUrl)
                .addHeader("Authorization","Bearer " + accessToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Response Code", String.valueOf(response.code()));
                Log.d("Response Body",response.body().string());
                if(response.code() == 401){
                    //Set session to false
                    sp.edit().putBoolean("isSessionActive",false).apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Session expired", Toast.LENGTH_SHORT).show();
                            submitProgress.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Data is saved!", Toast.LENGTH_SHORT).show();
                            submitProgress.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}