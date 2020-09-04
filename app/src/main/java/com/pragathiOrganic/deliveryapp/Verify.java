package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Verify extends AppCompatActivity {
    Button veri;
    EditText Code;
    String realOtp, Bill, No;
    ProgressDialog pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        veri = findViewById(R.id.verify);
        Code = findViewById(R.id.codeText);
        realOtp = getIntent().getStringExtra("otp");
        Bill = getIntent().getStringExtra("bill");
        No = getIntent().getStringExtra("no");

        veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop = new ProgressDialog(Verify.this);
                pop.setMessage("Please wait");
                pop.show();
                if(Code.getText().toString().equals(realOtp)){
                    // go to product image lo
                    pop.dismiss();
                    Intent i = new Intent(Verify.this, CaptureImage.class);
                    i.putExtra("bill", Bill);
                    i.putExtra("no",No);
                    Log.e("sendin",Bill);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Verify.this, "Wrong OTP entered", Toast.LENGTH_SHORT).show();
                    pop.dismiss();
                }
            }
        });
    }
}
