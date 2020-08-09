package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.deliveryapp.Models.Prevalent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    private EditText Mob, password;
    private TextView signUp;
    private Button Logi;
    private TextView Forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Mob = findViewById(R.id.phone_no);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.reg);
        Logi = findViewById(R.id.submit);
        Forgot = findViewById(R.id.forgot_password);

        Paper.init(this);
        String phoneUser = Paper.book().read(Prevalent.UserPhoneNo_);
        String passo = Paper.book().read(Prevalent.UserPasswordKey__);

        if (phoneUser != null && passo != null) {
            Log.e("haaa", phoneUser + " " + passo);
            AllowAccess(phoneUser, passo);
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Logi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost();
//                try{
//                    Toast.makeText(Login.this, "Ha", Toast.LENGTH_SHORT).show();
//                    RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
//                    String URL = "https://pragathiorganic.in/api/AuthdeliveryBoy";
//                    JSONObject jsonBody = new JSONObject();
//                    jsonBody.put("number", "8160759613");
//                    jsonBody.put("password", "abcd123");
//                    final String requestBody = jsonBody.toString();
//
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.e("VOLLEY  response - ", response);
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e("VOLLEY err", error.toString());
//                        }
//                    }) {
//                        @Override
//                        public String getBodyContentType() {
//                            return "application/json; charset=utf-8";
//                        }
//
//                        @Override
//                        public byte[] getBody() throws AuthFailureError {
//                            try {
//                                return requestBody == null ? null : requestBody.getBytes("utf-8");
//                            } catch (UnsupportedEncodingException uee) {
//                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                                return null;
//                            }
//                        }
//
//                        @Override
//                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                            String responseString = "";
//                            if (response != null) {
//                                responseString = String.valueOf(response.statusCode);
//                                // can get more details such as response.headers
//                            }
//                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                        }
//                    };
//                    requestQueue.add(stringRequest);
//                }
//                catch (JSONException e) {
//                    Log.e("Try FAILED","");
//                    e.printStackTrace();
//                }
            }
        });

        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

    }

    private void AllowAccess(final String phoneUser, final String passo) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/AuthdeliveryBoy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    //conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("number", phoneUser);
                    jsonParam.put("password", passo);


                    Log.e("JSON GOT", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.e("MSG", conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            //Log.e("dekho",strCurrentLine);
                            try {
                                JSONObject obj = new JSONObject(strCurrentLine);
                                String realStatus = obj.getString("status");
                                if (realStatus.equals("200")) {

                                    JSONArray jsonArray = obj.optJSONArray("data");


                                    JSONObject Data = jsonArray.getJSONObject(0);       // get first array only
                                    Log.e("data", Data.toString());

                                    Intent i = new Intent(Login.this, MainActivity.class);
                                    i.putExtra("obj", Data.toString());
                                    startActivity(i);
                                } else {
                                    Toast.makeText(Login.this, "staus not200", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(Login.this, "Eception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err", strCurrentLine);
                        }
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/AuthdeliveryBoy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    //conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("number", "8160759613");
                    jsonParam.put("password", "abcd123");


                    Log.e("JSON GOT", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.e("MSG" , conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            //Log.e("dekho",strCurrentLine);
                            try{
                                JSONObject obj = new JSONObject(strCurrentLine);
                                String realStatus = obj.getString("status");
                                if(realStatus.equals("200")){
                                        // write on paper
                                    Paper.book().write(Prevalent.UserPhoneNo_ , "8160759613");
                                    Paper.book().write(Prevalent.UserPasswordKey__ , "abcd123");

                                    JSONArray jsonArray = obj.optJSONArray("data");
                                        JSONObject Data = jsonArray.getJSONObject(0);       // get first array only
                                        Log.e("data",Data.toString());

                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        i.putExtra("obj", Data.toString());
                                        startActivity(i);
                                }else{
                                    Toast.makeText(Login.this, "staus not200", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(Login.this, "Eception", Toast.LENGTH_SHORT).show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
