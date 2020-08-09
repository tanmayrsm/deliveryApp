package com.example.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NewPassword extends AppCompatActivity {

    EditText e1, e2;
    Button done;
    String no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        no = getIntent().getStringExtra("No");


    }
}
