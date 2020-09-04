package com.pragathiOrganic.deliveryapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pragathiOrganic.deliveryapp.OrderDetails;
import com.pragathiOrganic.deliveryapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.AllOrdersViewHolder> {
    Activity context;
    ArrayList<JSONObject> orderArrayList;

    public OrderAdapter(Activity context, ArrayList<JSONObject> orderArrayList){
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public OrderAdapter.AllOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
        AllOrdersViewHolder allOrdersAdapter = new AllOrdersViewHolder(view);
        return allOrdersAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.AllOrdersViewHolder holder, int position) {
        JSONObject jam = orderArrayList.get(position);
        //Log.e("Jammm", jam.toString());
        try {
            String gem;
            if(jam.getString("payment_status").equals("0")) gem = "ONLINE";
            else    gem = "OFFLINE";
            holder.orderCost.setText("Rs "+jam.getString("total_amount") + " | "+gem);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            Log.e("Ye dekh",jam.getJSONArray("orderitems").getJSONObject(0).toString());
            holder.orderid.setText(jam.getString("bill_no"));
            holder.orderDate.setText(jam.getJSONArray("orderitems").getJSONObject(0).getString("order_date"));
//            holder.orderCost.setText(jam.getString("total_amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        Log.e("sige", String.valueOf(orderArrayList.size()));
        return orderArrayList.size();
    }

    public class AllOrdersViewHolder extends RecyclerView.ViewHolder{
        TextView orderDate, orderid, orderCost;
        RelativeLayout rela;

        public AllOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.date);
            orderCost = itemView.findViewById(R.id.cost);
            orderid = itemView.findViewById(R.id.orderId);
            rela = itemView.findViewById(R.id.rela);

            rela.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject jem = orderArrayList.get(getAdapterPosition());
                    Intent i = new Intent(context, OrderDetails.class);
                    i.putExtra("a",jem.toString());
                    context.startActivity(i);

                }
            });


        }
    }
}
