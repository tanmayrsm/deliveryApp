package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pragathiOrganic.deliveryapp.Models.Prevalent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    private EditText Mob, password;
    private TextView signUp;
    private Button Logi;
    private TextView Forgot;
    ProgressDialog pop;
    Toast toast;
    String realStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Mob = findViewById(R.id.phone_no);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.reg);
        Logi = findViewById(R.id.submit);
        Forgot = findViewById(R.id.forgot_password);
        toast = Toast.makeText(this, "This is a toast from Thread", Toast.LENGTH_SHORT);


        Paper.init(this);
        String phoneUser = Paper.book().read(Prevalent.UserPhoneNo_);
        String passo = Paper.book().read(Prevalent.UserPasswordKey__);

        if (phoneUser != null && passo != null) {
            pop = new ProgressDialog(Login.this);
            pop.setMessage("Please wait Logging you in");
            pop.show();
            AllowAccess(phoneUser, passo);
        }
        if(!realStatus.equals(""))
            Log.e("status",realStatus);
            if(realStatus.equals("500")){
                Toast.makeText(this, "Incorrect login credentials", Toast.LENGTH_SHORT).show();
            }
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Logi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop = new ProgressDialog(Login.this);
                pop.setMessage("Please wait Logging you in");
                pop.show();
                sendPost();

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
                                Log.e("real",realStatus);
                                if (realStatus.equals("200")) {

                                    JSONArray jsonArray = obj.optJSONArray("data");

                                    JSONObject Data = jsonArray.getJSONObject(0);       // get first array only
                                    Log.e("data", Data.toString());

                                    pop.dismiss();
                                    Intent i = new Intent(Login.this, MainActivity.class);
                                    i.putExtra("obj", Data.toString());
                                    startActivity(i);
                                } else {
                                    //toast.show();
                                }
                            } catch (Exception e) {
                                //Toast.makeText(Login.this, "Eception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err", strCurrentLine);
                        }
                    }
                    pop.dismiss();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    pop.dismiss();
                    //toast.show();
                    Log.e("EX","ex");
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

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("number", Mob.getText().toString());
                    jsonParam.put("password", password.getText().toString());


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
                                realStatus = obj.getString("status");
                                Log.e("real", realStatus);
                                if(realStatus.equals("200")){
                                        // write on paper
                                    Paper.book().write(Prevalent.UserPhoneNo_ , Mob.getText().toString());
                                    Paper.book().write(Prevalent.UserPasswordKey__ , password.getText().toString());

                                    JSONArray jsonArray = obj.optJSONArray("data");
                                        JSONObject Data = jsonArray.getJSONObject(0);       // get first array only
                                        Log.e("data",Data.toString());

                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        i.putExtra("obj", Data.toString());
                                    pop.dismiss();
                                        startActivity(i);
                                }else{
                                    //Toast.makeText(Login.this, "staus not200", Toast.LENGTH_SHORT).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                            builder.setTitle("Wrong credentials");
                                            builder.setCancelable(false);
                                            builder.setMessage("Please enter correct details")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    });
                                            // Create the AlertDialog object and return it
                                            builder.show();
                                        }
                                    });
                                    pop.dismiss();

                                }
                            }catch (Exception e){
                                //Toast.makeText(Login.this, "Eception", Toast.LENGTH_SHORT).show();
                                pop.dismiss();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            //Log.e("dekho err",strCurrentLine);
                        }
                        pop.dismiss();
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
