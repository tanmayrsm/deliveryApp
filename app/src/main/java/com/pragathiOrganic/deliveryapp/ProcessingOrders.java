package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pragathiOrganic.deliveryapp.Adapters.OrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ProcessingOrders extends AppCompatActivity {
    private static JSONObject J;
    RecyclerView rc;
    ArrayList<JSONObject> orderArrayList;
    SwipeRefreshLayout pullToRefresh;
    ProgressDialog pop;
    TextView scroll_down;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        Log.e("yes",userProfileString);
        J = new JSONObject(userProfileString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_orders);
        pullToRefresh = findViewById(R.id.pullRefresh);
        scroll_down = findViewById(R.id.scroll_down);

        pop = new ProgressDialog(ProcessingOrders.this);
        pop.setMessage("Please wait");
        pop.show();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                scroll_down.setVisibility(View.GONE);
                if(orderArrayList.size()==0)
                    Toast.makeText(ProcessingOrders.this, "No orders available", Toast.LENGTH_SHORT).show();

            }
        });

        rc = findViewById(R.id.recyclerView);
        orderArrayList = new ArrayList<>();
        //orderArrayList = (ArrayList<JSONObject>) getIntent().getSerializableExtra("aaa");
        rc.setLayoutManager(new LinearLayoutManager(ProcessingOrders.this));

        sendPost();

        OrderAdapter adapter = new OrderAdapter(ProcessingOrders.this, orderArrayList);
        rc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rc.setHasFixedSize(true);
    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/GetOrders");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", J.getString("id"));
                    //Log.e("JSON GOT", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

//                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
//                    Log.e("MSG" , conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            try{
                                JSONObject obj = new JSONObject(strCurrentLine);
                                String realStatus = obj.getString("status");
                                if(realStatus.equals("200")){

                                    orderArrayList.clear();

                                    JSONArray jsonArray = obj.optJSONArray("orders");
                                    JSONArray jsonOrderDet = obj.optJSONArray("orderitems");
                                    JSONArray pname = obj.optJSONArray("pname");
                                    //Log.e("Haaa1a", String.valueOf(jsonArray.length()));

                                    for (int i = 0; i < jsonArray.length(); i++){
                                        JSONObject Data = jsonArray.getJSONObject(i);
                                        ArrayList<Object> arr = new ArrayList<>();
                                        //Log.e("Haaa",Data.getString("order_status"));

                                        if(Data.getString("order_status").equals("2")){
                                            for(int j = 0; j < jsonOrderDet.length(); j++) {
                                                JSONArray DataOrder = jsonOrderDet.getJSONArray(j);
                                                Log.e("GOTT", DataOrder.toString());


                                                for(int k = 0; k < DataOrder.length(); k++){
                                                    JSONObject jesa = DataOrder.getJSONObject(k);
                                                    //Log.e("Haaaa", jesa.toString());

                                                    if(jesa.getString("bill_no").equals(Data.getString("bill_no"))){
                                                        // now get the pname related to product id in jesa

                                                        for(int l = 0 ; l < pname.length(); l++){
                                                            JSONArray tempo = pname.getJSONArray(l);
                                                            //Log.e("l ke:",tempo.toString());

                                                            for(int m = 0; m < tempo.length(); m++){
                                                                //Log.e("happpp",tempo.getJSONArray(m).toString());
                                                                JSONObject jarr = tempo.getJSONArray(m).getJSONObject(0);

                                                                if(jesa.getString("product_id").equals(jarr.getString("id"))){
                                                                    jesa.put("prod_details", jarr);
                                                                }
                                                            }
                                                        }
                                                        arr.add(jesa);
                                                    }
                                                }
//                                            if (DataOrder.getString("bill_no").equals(Data.getString("bill_no"))){
//                                                Data.put("orderitems",DataOrder);
//                                                orderArrayList.add(Data);
////                                                OrderAdapter adapter = new OrderAdapter(PendingOrders.this, orderArrayList);
////                                                rc.setAdapter(adapter);
////                                                adapter.notifyDataSetChanged();
//                                                //Log.e("$$data for orderssss",Data.toString());
//                                                //rc.scrollBy(10,11);
//                                            }
//                                        }
                                            }
                                            if(arr.size()>0)
                                                Data.put("orderitems", new JSONArray(arr));
                                            if(Data.length() > 0)
                                                orderArrayList.add(Data);
                                        }

                                    }
                                    if(orderArrayList.size()==0)
                                        Toast.makeText(ProcessingOrders.this, "No orders available", Toast.LENGTH_SHORT).show();

                                    Collections.reverse(orderArrayList);
                                    OrderAdapter adapter = new OrderAdapter(ProcessingOrders.this, orderArrayList);
                                    rc.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    rc.setHasFixedSize(true);

                                }else{
                                    //Toast.makeText(PendingOrders.this, "staus not200", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                //Toast.makeText(PendingOrders.this, "Eception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err",strCurrentLine);
                        }
                    }

                    conn.disconnect();
                    pop.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ex",e.getMessage());
                    pop.dismiss();
                }
            }
        });

        thread.start();
    }
}