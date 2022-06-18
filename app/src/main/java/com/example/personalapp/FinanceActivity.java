package com.example.personalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinanceActivity extends AppCompatActivity {
//    String baseUrl = "https://obider-transaction-service.herokuapp.com/transaction";
    String baseUrl = "https://obider-transaction-service-v2.herokuapp.com/api/v2";

    RecyclerView recyclerView;
    SharedPreferences sp;
    ProgressBar historyProgress;
    String accessToken;
    TextView totalPurchase;
    TextView totalIncome;
    TextView totalNet;
    TextView titleFinance;


    //For request response finance history
    String trxType = null;
    String amount = null;
    String desc = null;
    String trxMethod = null;
    String stringDate = "";
    String trxId = null;
    String id = null;
    int sumPuchase = 0;
    int sumIncome = 0;
    int nett = 0;


    ArrayList<String> listDesc = new ArrayList<String>();
    ArrayList<String> listAmount = new ArrayList<String>();
    ArrayList<String> listType = new ArrayList<String>();
    ArrayList<String> listDate = new ArrayList<String>();
    ArrayList<String> listMethod = new ArrayList<String>();
    ArrayList<String> listTrxId = new ArrayList<String>();
    ArrayList<String> listId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        recyclerView = findViewById(R.id.recyclerView);
        historyProgress = findViewById(R.id.historyProgress);
        totalPurchase = findViewById(R.id.textTotalPurchase);
        totalIncome = findViewById(R.id.textTotalIncome);
        totalNet = findViewById(R.id.textNett);

        titleFinance = findViewById(R.id.textFinanceActivity);

        DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        Date date = new Date();
//        Log.d("Month",dateFormat.format(date));
        titleFinance.setText(dateFormat.format(date));

        MyAdapter myAdapter = new MyAdapter(this,listDesc,listAmount,listType,listDate,listMethod,listTrxId,listId);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sp = getSharedPreferences("appPreference",MODE_PRIVATE);
        Log.d("sessionActive", String.valueOf(sp.getBoolean("isSessionActive", false)));
        if(!sp.getBoolean("isSessionActive", false)) {
            finish();
        }
        accessToken = sp.getString("accessToken","");

        //Initialize request finance history
        try {
            historyProgress.setVisibility(View.VISIBLE);
            getFinanceHistory(baseUrl+"/transaction");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void historyTrx(View view){
        try {
            historyProgress.setVisibility(View.VISIBLE);
            getFinanceHistory(baseUrl+"/finance");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFinanceHistory(String getUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getUrl)
                .addHeader("Authorization","Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonStr =  response.body().string();
                Log.d("Response Code", String.valueOf(response.code()));
                Log.d("Response Body",jsonStr);
                if(response.code() == 401){
                    //Set session to false
                    sp.edit().putBoolean("isSessionActive",false).apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Session expired", Toast.LENGTH_SHORT).show();
                            historyProgress.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }
                else if(response.code()==200){
                    getHistoryTrx(jsonStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            historyProgress.setVisibility(View.GONE);
                            //Update view
                            MyAdapter myAdapter = new MyAdapter(recyclerView.getContext(),
                                    listDesc,
                                    listAmount,
                                    listType,
                                    listDate,
                                    listMethod,
                                    listTrxId,
                                    listId);
                            recyclerView.setAdapter(myAdapter);

                            totalPurchase.setText(String.format("Rp %,d", sumPuchase));
                            totalIncome.setText(String.format("Rp %,d", sumIncome));
                            totalNet.setText(String.format("Rp %,d", nett));

                            if (nett<=0){
                                totalNet.setTextColor(getResources().getColor(R.color.darkPink));
                                totalNet.setBackgroundResource(R.color.softPink);
                            }
                            else{
                                totalNet.setTextColor(getResources().getColor(R.color.darkGreen));
                                totalNet.setBackgroundResource(R.color.softGreen);
                            }



                        }
                    });



                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            historyProgress.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    public void getHistoryTrx(String datas){
        listDesc = new ArrayList<String>();
        listAmount = new ArrayList<String>();
        listType = new ArrayList<String>();
        listDate = new ArrayList<String>();
        listMethod = new ArrayList<String>();
        listTrxId = new ArrayList<String>();
        listId = new ArrayList<String>();

        JSONObject obj = null;
        ArrayList<String> historyTrx = new ArrayList<String>();
        try {
            obj = new JSONObject(datas);
            JSONObject contentJson = null;
            String content = obj.getString("content");
            contentJson = new JSONObject(content);

            int n_trx = Integer.parseInt(contentJson.getString("n_data"));
            sumPuchase = Integer.parseInt(contentJson.getString("total_purchase"));
            sumIncome = Integer.parseInt(contentJson.getString("total_income"));
            nett = Integer.parseInt(contentJson.getString("total_net"));

            JSONArray ja_data = contentJson.getJSONArray("content");
            for(int i=0; i<n_trx; i++) {
                JSONObject jsonObj = ja_data.getJSONObject(i);
//                Log.d("TRX-TYPE", jsonObj.getString("trxType").toString());
                stringDate = jsonObj.getString("requestTime");
                trxType = jsonObj.getString("trxType");
                amount = jsonObj.getString("amount");
                desc = jsonObj.getString("desc");
                trxMethod = jsonObj.getString("trxMethod");
                trxId = jsonObj.getString("trxId");
                id = jsonObj.getString("id");

                listType.add(trxType);
                listDesc.add(desc);
                listAmount.add(amount);
                listDate.add(stringDate);
                listMethod.add(trxMethod);
                listTrxId.add(trxId);
                listId.add(id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}