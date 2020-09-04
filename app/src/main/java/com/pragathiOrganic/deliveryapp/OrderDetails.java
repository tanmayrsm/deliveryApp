package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pragathiOrganic.deliveryapp.Adapters.AllProductAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OrderDetails extends AppCompatActivity {
    JSONObject J;
    TextView idd, name, addr, total_amt, deli_amt, openMaps;
    Button orderPicked, deliveryDOne;
    TextView callBtn;
    ImageView back;
    RecyclerView rc;
    ArrayList<JSONObject> productArrayList;
    JSONArray arr;
    ProgressDialog pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        idd = findViewById(R.id.orderid);
        name = findViewById(R.id.personName);
        addr = findViewById(R.id.personAddress);
        total_amt = findViewById(R.id.totalAmt);
        deli_amt = findViewById(R.id.deliveryAmt);
        orderPicked = findViewById(R.id.orderpicked);
        deliveryDOne = findViewById(R.id.orderDone);
        callBtn = findViewById(R.id.callNo);
        openMaps = findViewById(R.id.maps);
        back = findViewById(R.id.text_toolbar);
        rc = findViewById(R.id.rec);
        productArrayList = new ArrayList<>();

        rc.setLayoutManager(new LinearLayoutManager(OrderDetails.this));
        back.setVisibility(View.GONE);

        try {
            J = new JSONObject(getIntent().getStringExtra("a"));
            idd.setText(J.getString("bill_no"));
            name.setText(J.getString("number"));
            addr.setText(J.getString("address"));

            total_amt.setText(J.getString("total_amount"));
            deli_amt.setText(J.getString("total_deliveryamt"));

            if(J.getString("order_status").equals("2"))
            {
                orderPicked.setVisibility(View.GONE);
                deliveryDOne.setVisibility(View.VISIBLE);
                deliveryDOne.setEnabled(true);
            }

            else if(J.getString("order_status").equals("1"))
            {
                orderPicked.setVisibility(View.VISIBLE);
                deliveryDOne.setVisibility(View.GONE);
                orderPicked.setEnabled(true);
            }
            else{
                orderPicked.setVisibility(View.GONE);
                deliveryDOne.setEnabled(false);
                deliveryDOne.setVisibility(View.GONE);
            }


            productArrayList.clear();
            arr = J.getJSONArray("orderitems");

            for(int i = 0; i< arr.length(); i++){
                JSONObject Data = arr.getJSONObject(i);
                productArrayList.add(Data);
            }

            AllProductAdapter adapter = new AllProductAdapter(OrderDetails.this, productArrayList);
            rc.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            rc.setHasFixedSize(true);

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+name.getText().toString()));
                    if (ActivityCompat.checkSelfPermission(OrderDetails.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            });
            openMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+addr.getText().toString());
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Attempt to start an activity that can handle the Intent
                    startActivity(mapIntent);
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(OrderDetails.this, PendingOrders.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                    startActivity(i);
//                    finish();
                }
            });
            orderPicked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        pop = new ProgressDialog(OrderDetails.this);
                        pop.setMessage("Please wait");
                        pop.show();
                        sendPost(J.getString("bill_no"));
                        orderPicked.setEnabled(false);
                        Toast.makeText(OrderDetails.this, "Done, please check processing order for same order", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            deliveryDOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send otp to his mobile
                    //                 http://message.rajeshwersoftsolution.com/rest/services/sendSMS/
                    //                 sendGroupSms?AUTH_KEY=bdf5c4d0f53a946e2cbc4a9491cfeab3&message=hiiiii
                    //                 &senderId=DEMOOS&routeId=8&mobileNos=8369608858&smsContentType=english

                    //final TextView textView = (TextView) findViewById(R.id.text);
                    pop = new ProgressDialog(OrderDetails.this);
                    pop.setMessage("Please wait");
                    pop.show();

                    try {
                        final String OTP;
                        String mobilere;

                        OTP = J.getString("delivery_otp");
                        mobilere = J.getString("number").replace("+91", "");
                        //Log.e("Mobu",mobilere);

                        String apikey = "bdf5c4d0f53a946e2cbc4a9491cfeab3";
                        String senderid = "DEMOOS";
                        String routeId = "8";
                        String mobileNo = mobilere;
                        String smsContentType = "english";

                        final String url = "http://message.rajeshwersoftsolution.com/rest/services/sendSMS/" +
                                "sendGroupSms?AUTH_KEY=" + apikey + "&message=" + OTP +
                                "&senderId=" + senderid + "&routeId=" + routeId + "&mobileNos=" + mobileNo + "&smsContentType=" + smsContentType;

                        Log.e("Url",url);

                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(OrderDetails.this);

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        // textView.setText("Response is: "+ response.substring(0,500));
                                        Toast.makeText(OrderDetails.this, "OTP sent", Toast.LENGTH_SHORT).show();
                                        deliveryDOne.setEnabled(false);
                                        Intent i = new Intent(OrderDetails.this, Verify.class);
                                        pop.dismiss();
                                        i.putExtra("otp", OTP);

                                        try {
                                            i.putExtra("bill",J.getString("bill_no"));
                                            i.putExtra("no",J.getString("number"));
                                            startActivity(i);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(OrderDetails.this, "Exception in sending bill no", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(OrderDetails.this, "Can't send OTP", Toast.LENGTH_SHORT).show();
                                pop.dismiss();
                            }
                        });

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    } catch (JSONException e) {
                        pop.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendPost(final String bill_no) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/OrderPickedStatus");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("bill_no", bill_no);

                    Log.e("JSON GOT", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.e("MSG" , conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        //Toast.makeText(OrderDetails.this, "Order added in processed order", Toast.LENGTH_SHORT).show();
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            //Log.e("dekho",strCurrentLine);
                            try{
                                JSONObject obj = new JSONObject(strCurrentLine);
                                String realStatus = obj.getString("status");
                                if(realStatus.equals("200")){
                                    // write on paper
                                    Toast.makeText(OrderDetails.this, "Done", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(OrderDetails.this, "staus not200", Toast.LENGTH_SHORT).show();
                                }
                                pop.dismiss();
                            }catch (Exception e){
                                Toast.makeText(OrderDetails.this, "Eception", Toast.LENGTH_SHORT).show();
                                pop.dismiss();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err",strCurrentLine);
                        }
                        pop.dismiss();
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    pop.dismiss();
                }
            }
        });

        thread.start();
    }
}
