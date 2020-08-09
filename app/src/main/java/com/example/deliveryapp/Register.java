package com.example.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppCompatActivity {
    private EditText Name, Mob, Pass, RePass, Vehicle;
    private TextView Logg;
    private Button Reg;
    private CheckBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Name = findViewById(R.id.name);
        Mob = findViewById(R.id.mobile);
        Vehicle = findViewById(R.id.vehicle_no);
        Pass = findViewById(R.id.password);
        RePass = findViewById(R.id.reenter_pass);
        Logg = findViewById(R.id.log);
        box = findViewById(R.id.checkBox);
        Reg = findViewById(R.id.submit);

        Logg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this ,Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
