package com.example.deliveryapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    private static JSONObject J;
    Button b;
    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
        //Log.e("in hF",J.toString());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);
        final FragmentActivity c = getActivity();
        b = view.findViewById(R.id.button1);
        try {
            String s = J.getString("username");
            b.setText(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }


}
