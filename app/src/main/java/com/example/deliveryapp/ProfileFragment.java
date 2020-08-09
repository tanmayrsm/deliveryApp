package com.example.deliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.deliveryapp.Models.Prevalent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.paperdb.Paper;

public class ProfileFragment extends Fragment {
    private Button EditDetails;
    private Button logout;
    private Button Submit;
    private LinearLayout linearLayout;
    private static JSONObject J;
    private EditText NAME, ADDRESS, PHONE, VEHICLE;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        final FragmentActivity c = getActivity();

        EditDetails = view.findViewById(R.id.Edit);
        linearLayout = view.findViewById(R.id.l1);
        logout = view.findViewById(R.id.Logout);
        NAME = view.findViewById(R.id.new_name);
        ADDRESS = view.findViewById(R.id.address);
        PHONE = view.findViewById(R.id.new_ph_no);
        VEHICLE = view.findViewById(R.id.vehicle_no);
        Submit = view.findViewById(R.id.submit);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Paper.book().destroy();
                startActivity(i);
            }
        });

        EditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout.getVisibility() == View.GONE)   linearLayout.setVisibility(View.VISIBLE);
                else if (linearLayout.getVisibility() == View.VISIBLE)   linearLayout.setVisibility(View.GONE);

                if(EditDetails.getText().equals("Edit Profile"))    EditDetails.setText("Back");
                else if(EditDetails.getText().equals("Back"))    EditDetails.setText("Edit Profile");
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "sending post", Toast.LENGTH_SHORT).show();
                sendPost();
            }
        });

        try{
            NAME.setText(J.getString("username"));
            ADDRESS.setText(J.getString("address"));
            PHONE.setText(J.getString("mobile_number"));
            VEHICLE.setText(J.getString("vehicle_no"));
        }catch (Exception e){
            Toast.makeText(c, "Cant fetch your data", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void sendPost() {
        Log.e("J",J.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/UpdateDeliveryBoy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    //conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    Log.e("Inside",J.getString("username"));
                    /*{
                        "id":"1",
                        "name":"Name",
                        "username" : "testing",
                        "password":"abcd123",
                        "mobile_number":"8160759613",
                        "address":"test",
                        "token":"mS6zovYd7CrjEPtKG2aH"
                    }*/

                    jsonParam.put("id", J.getString("id"));
                    jsonParam.put("name", "Name");
                    jsonParam.put("username", NAME.getText().toString());
                    jsonParam.put("password", J.getString("password"));
                    jsonParam.put("mobile_number", PHONE.getText().toString());
                    jsonParam.put("address", ADDRESS.getText().toString());
                    jsonParam.put("token", J.getString("token"));


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

                            }catch (Exception e){
                                Toast.makeText(getActivity(), "Eception", Toast.LENGTH_SHORT).show();
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
                    Log.e("No tryy","");
                }
            }
        });

        thread.start();
    }
}
