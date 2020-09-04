package com.pragathiOrganic.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DigitalSignature extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private TextView main;
    String ID, bill, No;
    ProgressDialog pop;
    private  static  JSONObject J;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_signature);
        verifyStoragePermissions(this);

        ID = getIntent().getStringExtra("id");
        bill = getIntent().getStringExtra("bill");
        No = getIntent().getStringExtra("no");

        main = findViewById(R.id.main_text);
        main.setText("Digital Signature");

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(DigitalSignature.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignature.this);
                builder.setTitle("Submit Delivery ?");
                builder.setCancelable(false);
                builder.setMessage("Click Ok and the order will be completed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                                pop = new ProgressDialog(DigitalSignature.this);
                                pop.setMessage("Please wait");
                                pop.show();

                                if (addJpgSignatureToGallery(signatureBitmap)) {
                                    Toast.makeText(DigitalSignature.this, "Delivery done successfully", Toast.LENGTH_SHORT).show();
                                    pop.dismiss();

                                    //send the user message for completion of order

                                    Intent ppp = new Intent(DigitalSignature.this, MainActivity.class);
                                    ppp.putExtra("obj", J.toString());
                                    ppp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(ppp);
                                } else {
                                    Toast.makeText(DigitalSignature.this, "Unable to complete order, please make sure you are connected with good network", Toast.LENGTH_SHORT).show();
                                    pop.dismiss();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DigitalSignature.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
            if(ID!=null && bill!=null)
            {
                sendPost(photo);
                sendMessage();
            }

            else{
                Toast.makeText(this, "Error in receiving ur id and bill no", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        DigitalSignature.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void sendPost(final File photo) {
        //Log.e("J",J.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    URL url = new URL("https://pragathiorganic.in/api/UploaddeliveryPic");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    conn.setRequestProperty("Content-Type", "application/json");
                    //conn.setRequestProperty("Accept","application/json");
                    OkHttpClient client = new OkHttpClient();
                    String Urll = "https://pragathiorganic.in/api/UploaddeliveryPic";

                    RequestBody body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("id","sign")
                                        .addFormDataPart("bill_no",bill)
                                        .addFormDataPart("image",photo.getName(), RequestBody.create(MediaType.parse("image/jpeg"),photo))
                                        .addFormDataPart("deliveryboyid",ID)
                                        .build();

                    Log.e("req:",body.toString());
                    Request request = new Request.Builder()
                            .url(Urll)
                            .post(body)
                            .build();
                    try{
                        Response response = client.newCall(request).execute();
                        Log.e("Response", String.valueOf(response.code()));
                        if(response.code() == 200){
                            Toast.makeText(DigitalSignature.this, "Order placed suceefully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(DigitalSignature.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            pop.dismiss();
                            //startActivity(i);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("No tryy","");
                }
            }
        });

        thread.start();
    }

    public void sendMessage(){
        try {
            String num = No.replace("+91","");

            //Log.e("ACK",No);

            String message = "Delivery for your order is successfully done";
            //mobilere = J.getString("number").replace("+91", "");

            String apikey = "bdf5c4d0f53a946e2cbc4a9491cfeab3";
            String senderid = "DEMOOS";
            String routeId = "8";
            String mobileNo = num;
            String smsContentType = "english";

            final String url = "http://message.rajeshwersoftsolution.com/rest/services/sendSMS/" +
                    "sendGroupSms?AUTH_KEY=" + apikey + "&message=" + message +
                    "&senderId=" + senderid + "&routeId=" + routeId + "&mobileNos=" + mobileNo + "&smsContentType=" + smsContentType;

            //Log.e("Url",url);

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(DigitalSignature.this);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            // textView.setText("Response is: "+ response.substring(0,500));
                            Toast.makeText(DigitalSignature.this, "Acknowledge sent", Toast.LENGTH_SHORT).show();

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DigitalSignature.this, "Ack not successful", Toast.LENGTH_SHORT).show();
                    pop.dismiss();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {
            pop.dismiss();
            e.printStackTrace();
        }
    }
}
