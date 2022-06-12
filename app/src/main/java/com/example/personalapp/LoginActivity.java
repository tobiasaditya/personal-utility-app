package com.example.personalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
//    String baseUrl = "https://obider-transaction-service.herokuapp.com/auth";
    String baseUrl = "https://obider-transaction-service-v2.herokuapp.com/api/v2/user";
    EditText username;
    EditText password;
    ProgressBar loginProgress;

    String accessToken = null;
    String messageResponse = null;

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.userNameInput);
        password = findViewById(R.id.passwordInput);
        loginProgress = findViewById(R.id.loginProgress);
        sp = getSharedPreferences("appPreference",MODE_PRIVATE);

        if(!sp.getString("accessToken", "").equals("")){
            accessToken = sp.getString("accessToken","DEFAULT");
            startTransactiontActivity();
        }
    }
    public void login(View view) throws Exception {
        loginProgress.setVisibility(View.VISIBLE);
        String body = "{\"username\":\"%s\",\"password\":\"%s\"}";
        String finalBody = String.format(body, username.getText().toString(),password.getText().toString());
        loginRequest(baseUrl+"/login",finalBody);
    }
    void loginRequest(String postUrl, String body) throws IOException {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(body,mediaType);


        Request request = new Request.Builder()
                .url(postUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonStr = response.body().string();
                Log.d("RC", String.valueOf(response.code()));
                Log.d("BODY",jsonStr);

                if (response.code()!=200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Username/password invalid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Set session to false
                            sp.edit().putBoolean("isSessionActive",true).apply();
                            loginProgress.setVisibility(View.GONE);
                            getAccessToken(jsonStr);
                            Toast.makeText(getApplicationContext(), messageResponse, Toast.LENGTH_SHORT).show();
                            startTransactiontActivity();
                        }
                    });
                }

            }
        });
    }

    public void getAccessToken(String datas){
        JSONObject obj = null;
        try {
            obj = new JSONObject(datas);
            JSONObject contentJson = null;
            String content = obj.getString("content");
            messageResponse = obj.getString("message");
            contentJson = new JSONObject(content);
            accessToken = contentJson.getString("access_token");

            //Save access_token for next time open the app
            sp.edit().putString("accessToken",accessToken).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startTransactiontActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("accessToken",accessToken);
        Log.d("Intent Token",accessToken);
        startActivity(intent);
    }
}