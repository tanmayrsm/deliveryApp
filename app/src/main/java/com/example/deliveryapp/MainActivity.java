package com.example.deliveryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView txt;
    private JSONObject J;
    String objo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objo = getIntent().getStringExtra("obj");
        txt = findViewById(R.id.text);


        try {
            J = new JSONObject(objo);
            txt.setText(J.getString("username"));

            Bundle args = new Bundle();
            String userProfileString = J.toString();
            args.putString("userProfile", userProfileString);
            HomeFragment.setArguements(args);
            ProfileFragment.setArguements(args);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        bottomNavigationView = findViewById(R.id.navigation_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.action_home :
                            selectedFragment = new HomeFragment();
                            break;
                        case  R.id.action_profile :
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}
