package com.pragathiOrganic.deliveryapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
      PendingOrders.setArguements(args);
      ProcessingOrders.setArguements(args);
      CompletedOrders.setArguements(args);
      CaptureImage.setArguements(args);
      DigitalSignature.setArguements(args);


    } catch (JSONException e) {
      e.printStackTrace();
    }


    bottomNavigationView = findViewById(R.id.navigation_menu);
    bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

    }else {
      requestStoragePermission();
    } if(ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

    }else {
      requestCamera();
    } if(ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
    } else {
      requestPhone();
    }
  }
  private void requestStoragePermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      new AlertDialog.Builder(this)
              .setTitle("Write external storage")
              .setMessage("This permission is needed because of this and that")
              .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  ActivityCompat.requestPermissions(MainActivity.this,
                          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
                }
              })
              .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create().show();
    }
    else {
      ActivityCompat.requestPermissions(this,
              new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
      }
  }
    private  void requestCamera(){
    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CAMERA)){
      new AlertDialog.Builder(this)
              .setTitle("Camera needed")
              .setMessage("This permission is needed because of this and that")
              .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  ActivityCompat.requestPermissions(MainActivity.this,
                          new String[] {Manifest.permission.CAMERA}, 13);
                }
              })
              .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create().show();
    }
    else {
      ActivityCompat.requestPermissions(this,
              new String[] {Manifest.permission.CAMERA}, 13);
    }
  }

    private void requestPhone(){
    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CALL_PHONE)){
      new AlertDialog.Builder(this)
              .setTitle("Phone needed")
              .setMessage("This permission is needed because of this and that")
              .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  ActivityCompat.requestPermissions(MainActivity.this,
                          new String[] {Manifest.permission.CALL_PHONE}, 14);
                }
              })
              .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create().show();
    }
    else {
      ActivityCompat.requestPermissions(this,
              new String[] {Manifest.permission.CALL_PHONE}, 14);
    }

  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 12)  {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
      }
    }

    if (requestCode == 13)  {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
      }
    }

    if (requestCode == 14)  {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
      }
    }
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
                default:
                  selectedFragment = new HomeFragment();
              }
              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                      selectedFragment).commit();
              return true;
            }
          };
}
