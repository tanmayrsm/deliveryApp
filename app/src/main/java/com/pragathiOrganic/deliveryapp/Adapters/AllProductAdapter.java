package com.pragathiOrganic.deliveryapp.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pragathiOrganic.deliveryapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.AllProductsViewHolder>{
    Activity context;
    ArrayList<JSONObject> productArrayList;
    String url;

    public AllProductAdapter(Activity context, ArrayList<JSONObject> productArrayList){
        this.context = context;
        this.productArrayList = productArrayList;
    }


    @NonNull
    @Override
    public AllProductAdapter.AllProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_product_order,parent,false);
        AllProductAdapter.AllProductsViewHolder allOrdersAdapter = new AllProductAdapter.AllProductsViewHolder(view);
        return allOrdersAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull AllProductAdapter.AllProductsViewHolder holder, int position) {
        JSONObject jam = productArrayList.get(position);

        try {
            holder.pQty.setText(jam.getString("qty"));
            holder.pName.setText(jam.getJSONObject("prod_details").getString("name"));
            url = jam.getJSONObject("prod_details").getString("image");
            url = url.replace("\\","");
            url = "https://pragathiorganic.in/"+url;
            Log.e("Imageurl",url);
            Picasso.get().load(url).into(holder.pic);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class AllProductsViewHolder extends RecyclerView.ViewHolder{
        TextView pName, pQty;
        ImageView pic;

        public AllProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            pName = itemView.findViewById(R.id.prodName);
            pQty = itemView.findViewById(R.id.prodQty);
            pic = itemView.findViewById(R.id.image);
        }
    }
}
