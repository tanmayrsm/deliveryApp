package com.pragathiOrganic.deliveryapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static JSONObject J;
    ArrayList<JSONObject> orderArrayList;
    Button b,proCess,completedd;
    Button B1, B2, B3;
    Button whatsapp;
    String date;
    String all_order_len;
    String todays_YYYY_MM_DD[];
    Button privacyPolicy, help;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        Button pend;
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        pend = view.findViewById(R.id.pending);
        proCess = view.findViewById(R.id.process);
        completedd = view.findViewById(R.id.completed);

        B1 = view.findViewById(R.id.button1);
        B2 = view.findViewById(R.id.button2);
        B3 = view.findViewById(R.id.button3);

        help = view.findViewById(R.id.help);
        privacyPolicy = view.findViewById(R.id.pp);

        whatsapp = view.findViewById(R.id.open_whatsapp);

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        todays_YYYY_MM_DD = date.split("-");


        final FragmentActivity c = getActivity();
        orderArrayList = new ArrayList<>();
        b = view.findViewById(R.id.button1);

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = "+91 9740876659"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicy.class));
            }
        });



        try {
            String s = J.getString("username");
            //b.setText(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ij = new Intent(getActivity(), PendingOrders.class);
                //ij.putExtra("aaa",orderArrayList.toString());
                startActivity(ij);
            }
        });
        proCess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProcessingOrders.class));
            }
        });
        completedd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CompletedOrders.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sendPost();
    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/GetOrders");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", J.getString("id"));
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

                                    orderArrayList.clear();

                                    JSONArray jsonArray = obj.optJSONArray("orders");
                                    JSONArray jsonOrderDet = obj.optJSONArray("orderitems");

                                    //set lifetime orders
                                    all_order_len = String.valueOf(jsonArray.length());
                                    B1.setText(all_order_len +"\nLifetime orders");
                                    int avg_daily = 0;
                                    int monthly_daily = 0;
                                    HashSet<String> total_days=new HashSet();
                                    HashSet<String> bill__ = new HashSet<>();

                                    for (int i = 0; i < jsonArray.length(); i++){
                                        JSONObject Data = jsonArray.getJSONObject(i);
                                        for(int j = 0; j < jsonOrderDet.length(); j++){
                                            JSONObject DataOrder = jsonOrderDet.getJSONArray(j).getJSONObject(0);
                                            Log.e("Ha",DataOrder.toString());

                                            if(DataOrder.getString("bill_no").equals(Data.getString("bill_no")))
                                            {
                                                String order_date = DataOrder.getString("date_time");
                                                String[] splitStr = order_date.split("\\s+");
                                                String sep = splitStr[0];
                                                String YYYY_MM_DD[] = sep.split("-");
                                                Log.e("Today",todays_YYYY_MM_DD[2]);
                                                Log.e("Orders",YYYY_MM_DD[2]);

                                                //calculate average daily orders
                                                total_days.add(sep);    //adding all dates he received orders
                                                // avg daily order = (total orders)/(total days he received those orders)

//                                            if (DataOrder.getString("bill_no").equals(Data.getString("bill_no"))){
//                                                Data.put("orderitems",DataOrder);
//                                                //orderArrayList.add(Data);
////                                                OrderAdapter adapter = new OrderAdapter(PendingOrders.this, orderArrayList);
////                                                rc.setAdapter(adapter);
////                                                adapter.notifyDataSetChanged();
//                                                //Log.e("$$data for orderssss",Data.toString());
//                                            }
                                                // if todays month matches with orders month...add it
                                                if(todays_YYYY_MM_DD[1].equals(todays_YYYY_MM_DD[1])){
                                                    monthly_daily++;
                                                }
                                                break;
                                            }

                                        }
                                        //Log.e("final", orderArrayList.toString());
                                        //orderArrayList.add(Data);
                                    }
                                    int avg_daily_orderss = Integer.parseInt(all_order_len) / total_days.size();
                                    B2.setText(avg_daily_orderss+"\nAverage daily\norders");
                                    B3.setText(monthly_daily + "\nMonthly orders");

//                                    OrderAdapter adapter = new OrderAdapter(PendingOrders.this, orderArrayList);
//                                    rc.setAdapter(adapter);
//                                    adapter.notifyDataSetChanged();
//                                    rc.setHasFixedSize(true);

                                }else{
                                    //Toast.makeText(PendingOrders.this, "staus not200", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                //Toast.makeText(PendingOrders.this, "Eception", Toast.LENGTH_SHORT).show();
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
                    //Log.e("ex",e.getMessage());
                }
            }
        });

        thread.start();
    }


}
