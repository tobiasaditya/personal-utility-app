package com.example.personalapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    ArrayList<String> listDeskripsi = new ArrayList<String>();
    ArrayList<String> listAmount = new ArrayList<String>();
    ArrayList<String> listType = new ArrayList<String>();
    ArrayList<String> listDate = new ArrayList<String>();
    ArrayList<String> listMethod = new ArrayList<String>();
    ArrayList<String> listTrxId = new ArrayList<String>();
    ArrayList<String> listId = new ArrayList<String>();

    Context context;


    public MyAdapter(Context ct,
                     ArrayList<String> desc,
                     ArrayList<String> amount,
                     ArrayList<String> type,
                     ArrayList<String> date,
                     ArrayList<String> method,
                     ArrayList<String> trxId,
                     ArrayList<String> id){
        context = ct;
        listDeskripsi = desc;
        listAmount = amount;
        listType = type;
        listDate = date;
        listMethod = method;
        listTrxId = trxId;
        listId = id;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_transaction,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
//        holder.text1.setText(listType.get(position));
        holder.text2.setText(listDeskripsi.get(position));

        //Untuk amount, format pake separator dan Rupiah
        int intAmount = Integer.parseInt(listAmount.get(position));
        holder.text3.setText(String.format("Rp %,d", intAmount));
        if (listType.get(position).equals("PURCHASE")) {
            holder.text3.setTextColor(context.getResources().getColor(R.color.darkPink));
            holder.text3.setBackgroundResource(R.color.softPink);
        }
        else if (listType.get(position).equals("INCOME")) {
            holder.text3.setTextColor(context.getResources().getColor(R.color.darkGreen));
            holder.text3.setBackgroundResource(R.color.softGreen);
        }
        else if (listType.get(position).equals("INVESTMENT")) {
            holder.text3.setTextColor(context.getResources().getColor(R.color.darkYellow));
            holder.text3.setBackgroundResource(R.color.softYellow);
        }
        holder.text4.setText(listDate.get(position).substring(0 , listDate.get(position).indexOf(".")));

        holder.text5.setText(listMethod.get(position));

    }

    @Override
    public int getItemCount() {
        return listType.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{ //implements View.OnClickListener {
//        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
        TextView text5;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            text1 = itemView.findViewById(R.id.trxTypeText);
            text2 = itemView.findViewById(R.id.trxDescText);
            text3 = itemView.findViewById(R.id.trxAmoutText);
            text4 = itemView.findViewById(R.id.trxDateText);
            text5 = itemView.findViewById(R.id.trxMethodText);


//            itemView.setOnClickListener(this);


        }

//        @Override
//        public void onClick(View view) {
//            int posClick = getAdapterPosition();
//            Log.d("click", String.valueOf(posClick));
//            Intent intent = new Intent(view.getContext(),DetailTransaction.class);
//            intent.putExtra("tanggalText",listDate.get(posClick).substring(0 , listDate.get(posClick).indexOf(" ")));
//            intent.putExtra("waktuText",listDate.get(posClick).substring(
//                    (listDate.get(posClick).indexOf(" ")+1),
//                    (listDate.get(posClick).indexOf("."))
//            ));
//            intent.putExtra("typeText",listType.get(posClick));
//            intent.putExtra("nominalText",listAmount.get(posClick));
//            intent.putExtra("descText",listDeskripsi.get(posClick));
//            intent.putExtra("methodText",listMethod.get(posClick));
//            intent.putExtra("trxIdText",listTrxId.get(posClick));
//            intent.putExtra("idText",listId.get(posClick));
//
//
//            context = view.getContext();
//            context.startActivity(intent);
//
//        }
    }
}
