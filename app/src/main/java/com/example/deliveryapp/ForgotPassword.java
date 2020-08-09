package com.example.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

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

public class ForgotPassword extends AppCompatActivity {
    private Button Done;
    private EditText ed1, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ed1 = findViewById(R.id.phone_no);
        Done = findViewById(R.id.done);
        otp = findViewById(R.id.otp);

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 http://message.rajeshwersoftsolution.com/rest/services/sendSMS/
//                 sendGroupSms?AUTH_KEY=bdf5c4d0f53a946e2cbc4a9491cfeab3&message=hiiiii
//                 &senderId=DEMOOS&routeId=8&mobileNos=8369608858&smsContentType=english

                //final TextView textView = (TextView) findViewById(R.id.text);
                final String OTP = "1234";
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
                                Toast.makeText(ForgotPassword.this, "OTP sent", Toast.LENGTH_SHORT).show();


                                Done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(otp.getText().toString().equals(OTP))
                                        {
                                            Intent i = new Intent(ForgotPassword.this, NewPassword.class);
                                            i.putExtra("No", ed1.getText().toString());
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                    }
                                });


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPassword.this, "Bhag sala", Toast.LENGTH_SHORT).show();
                        Log.e("Damsn",error.toString());
                        Log.e("The url:",url);
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }
}
