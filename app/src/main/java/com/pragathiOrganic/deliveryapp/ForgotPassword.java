package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ForgotPassword extends AppCompatActivity {
    private Button Done;
    private EditText ed1, otp;
    TextView incc;
    ProgressDialog pop;
    String realStatus = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ed1 = findViewById(R.id.phone_no);
        Done = findViewById(R.id.done);
        otp = findViewById(R.id.otp);
        incc = findViewById(R.id.inc);

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 http://message.rajeshwersoftsolution.com/rest/services/sendSMS/
//                 sendGroupSms?AUTH_KEY=bdf5c4d0f53a946e2cbc4a9491cfeab3&message=hiiiii
//                 &senderId=DEMOOS&routeId=8&mobileNos=8369608858&smsContentType=english

                //final TextView textView = (TextView) findViewById(R.id.text);
                Random random = new Random();
                final String OTP = String.format("%04d", random.nextInt(10000));
                String apikey = "bdf5c4d0f53a946e2cbc4a9491cfeab3";
                String senderid = "DEMOOS";
                String routeId = "8";
                String mobileNo = ed1.getText().toString();
                String smsContentType = "english";

                final String url = "http://message.rajeshwersoftsolution.com/rest/services/sendSMS/" +
                        "sendGroupSms?AUTH_KEY="+apikey+"&message="+ OTP +
                        "&senderId="+ senderid +"&routeId="+ routeId +"&mobileNos="+ mobileNo +"&smsContentType="+smsContentType;


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ForgotPassword.this);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                // textView.setText("Response is: "+ response.substring(0,500));
                                otp.setVisibility(View.VISIBLE);
                                ed1.setVisibility(View.GONE);
                                incc.setVisibility(View.GONE);
                                Toast.makeText(ForgotPassword.this, "OTP sent", Toast.LENGTH_SHORT).show();

                                Done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(otp.getText().toString().equals(OTP))
                                        {
                                            pop = new ProgressDialog(ForgotPassword.this);
                                            pop.setMessage("Please wait");
                                            pop.show();
                                            // send request to get message
                                            sendPost(ed1.getText().toString());
                                            pop.dismiss();
                                        }
                                    }
                                });


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
        if(!realStatus.equals(""))
        if(realStatus.equals("500")){
            Toast.makeText(this, "Incorrect Mobile No", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPost(final String mobileNo) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/ResetPassword");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    //conn.setRequestProperty("Content-Type", "application/json");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", "check");
                    jsonParam.put("mobilenumber", mobileNo);


                    Log.e("JSON GOT", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.e("MSG" , conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        //Toast.makeText(ForgotPassword.this, "Hm", Toast.LENGTH_SHORT).show();
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {

                            try{
                                JSONObject obj = new JSONObject(strCurrentLine);
                                realStatus = obj.getString("status");
                                if(realStatus.equals("500")) {
                                    incc.setVisibility(View.VISIBLE);
                                }

                                if(realStatus.equals("200")){
                                    JSONArray arr = obj.getJSONArray("data");
                                    JSONObject jerr = arr.getJSONObject(0);
                                    String passwordd = jerr.getString("password");
                                    sendMessage(ed1.getText().toString(), passwordd);
                                    Intent ii = new Intent(ForgotPassword.this, Login.class);
                                    ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    pop.dismiss();
                                    startActivity(ii);

                                }else{

                                }
                                //pop.dismiss();
                            }catch (Exception e){
                                Log.e("ex","ex");
                                //pop.dismiss();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err",strCurrentLine);
                        }
                        //pop.dismiss();
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("EX",e.getMessage());
                    e.printStackTrace();
                    //pop.dismiss();
                }
            }
        });

        thread.start();
    }

    private void sendMessage(String mob, String passwordd) {
        Random random = new Random();
        final String OTP = "Your password is "+passwordd;
        String apikey = "bdf5c4d0f53a946e2cbc4a9491cfeab3";
        String senderid = "DEMOOS";
        String routeId = "8";
        String mobileNo = mob;
        String smsContentType = "english";

        final String url = "http://message.rajeshwersoftsolution.com/rest/services/sendSMS/" +
                "sendGroupSms?AUTH_KEY="+apikey+"&message="+ OTP +
                "&senderId="+ senderid +"&routeId="+ routeId +"&mobileNos="+ mobileNo +"&smsContentType="+smsContentType;


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ForgotPassword.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        // textView.setText("Response is: "+ response.substring(0,500));
                        otp.setVisibility(View.VISIBLE);
                        ed1.setVisibility(View.GONE);
                        Toast.makeText(ForgotPassword.this, "Password sms sent", Toast.LENGTH_SHORT).show();

                        Done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(otp.getText().toString().equals(OTP))
                                {
                                    // send request to get message
                                    sendPost(ed1.getText().toString());
                                }else{
                                    Toast.makeText(ForgotPassword.this, "Incorrect OTP entered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
