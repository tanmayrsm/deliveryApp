package com.pragathiOrganic.deliveryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pragathiOrganic.deliveryapp.Models.Prevalent;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.paperdb.Paper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private Button EditDetails;
    private Button logout;
    private Button Submit;
    private LinearLayout linearLayout;
    private static JSONObject J;
    private EditText NAME, ADDRESS, PHONE, VEHICLE;
    private TextView naame, phonee;
    ProgressDialog pop;
    RelativeLayout rr;
    ImageView pic, prof_picc;
    String url, urlo, urlm;
    JSONObject jsonParam;
    private static final int pic_id = 3333;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
        Log.e("JJJJ",J.toString());

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        final FragmentActivity c = getActivity();

        jsonParam = new JSONObject();

        try {
            AllowAccess(J.getString("mobile_no"), J.getString("password"));
            if(urlo!=null){
                Picasso.get().load(urlo).into(prof_picc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EditDetails = view.findViewById(R.id.Edit);
        linearLayout = view.findViewById(R.id.l1);
        logout = view.findViewById(R.id.Logout);
        NAME = view.findViewById(R.id.new_name);
        ADDRESS = view.findViewById(R.id.address);
        PHONE = view.findViewById(R.id.new_ph_no);
        VEHICLE = view.findViewById(R.id.vehicle_no);
        Submit = view.findViewById(R.id.submit);
        rr = view.findViewById(R.id.rela);
        pic = view.findViewById(R.id.picc);
        prof_picc = view.findViewById(R.id.prof_pic);

        rr.setEnabled(false);
        pic.setVisibility(View.GONE);


        naame = view.findViewById(R.id.Namee);
        phonee = view.findViewById(R.id.Phonee);
        try {
            naame.setText(J.getString("username"));
            phonee.setText(J.getString("mobile_number"));
            Log.e("imp",J.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Paper.book().destroy();
                startActivity(i);
            }
        });
        rr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(getActivity());
            }
        });
        
        EditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout.getVisibility() == View.GONE)
                {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                else if (linearLayout.getVisibility() == View.VISIBLE)
                {
                    linearLayout.setVisibility(View.GONE);
                }

                if(EditDetails.getText().equals("Edit Profile"))
                {
                    EditDetails.setText("Back");
                }
                else if(EditDetails.getText().equals("Back"))
                {
                    EditDetails.setText("Edit Profile");
                }
                if(pic.getVisibility()==View.GONE){
                    rr.setEnabled(true);
                    pic.setVisibility(View.VISIBLE);
                }else if(pic.getVisibility() == View.VISIBLE){
                    rr.setEnabled(false);
                    pic.setVisibility(View.GONE);
                }
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(c, "sending post", Toast.LENGTH_SHORT).show();
                pop = new ProgressDialog(getActivity());
                pop.setMessage("Updating");
                pop.show();
                try {
                    sendPost2(J.getString("id"));            //update image
                    AllowAccess(J.getString("mobile_number"), J.getString("password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendPost();

            }
        });

        try{
            NAME.setText(J.getString("username"));
            ADDRESS.setText(J.getString("address"));
            PHONE.setText(J.getString("mobile_number"));
            VEHICLE.setText(J.getString("vehicle_no"));
            naame.setText(J.getString("username"));
            phonee.setText(J.getString("mobile_number"));

            if(J.getString("profile_pic")!=null){
                url = J.getString("profile_pic");
                url = url.replace("\\","");
                url = "https://pragathiorganic.in/"+url;
                //Log.e("Imageurl",url);
                Picasso.get().load(url).into(prof_picc);
            }


        }catch (Exception e){
            //Toast.makeText(c, "Cant fetch your data", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void AllowAccess(final String phoneUser, final String passo) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pragathiorganic.in/api/AuthdeliveryBoy");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam1 = new JSONObject();
                    jsonParam1.put("number", phoneUser);
                    jsonParam1.put("password", passo);


                    Log.e("JSON GOT", jsonParam1.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam1.toString());

                    os.flush();
                    os.close();

                    Log.e("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.e("MSG", conn.getResponseMessage());

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            //Log.e("dekho",strCurrentLine);
                            try {
                                JSONObject obj = new JSONObject(strCurrentLine);
                                String realStatus = obj.getString("status");
                                Log.e("real",realStatus);
                                if (realStatus.equals("200")) {

                                    JSONArray jsonArray = obj.optJSONArray("data");

                                    JSONObject Data = jsonArray.getJSONObject(0);       // get first array only
                                    Log.e("data", Data.toString());

                                    jsonParam.put("profile_pic", Data.getString("profile_pic"));

                                    urlo = Data.getString("profile_pic");
                                    urlm = Data.getString("profile_pic");
                                    urlo = urlo.replace("\\","");
                                    urlo = "https://pragathiorganic.in/"+urlo;
                                    Log.e("urlo",urlo);
                                    Picasso.get().load(urlo).into(prof_picc);


                                } else {
                                    //toast.show();
                                }
                            } catch (Exception e) {
                                //Toast.makeText(Login.this, "Eception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            Log.e("dekho err", strCurrentLine);
                        }
                    }
                    pop.dismiss();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    pop.dismiss();
                    //toast.show();
                    Log.e("EX","ex");
                }
            }
        });

        thread.start();
    }

    private void sendPost2(String idd) {
        BitmapDrawable drawable = (BitmapDrawable) prof_picc.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        if (addJpgSignatureToGallery(bitmap,idd)) {
            //Toast.makeText(CaptureImage.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "New Picture");
//                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
//                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(takePicture, IMAGE_CAPTURE_CODE);
                    Intent camera_intent
                            = new Intent(MediaStore
                            .ACTION_IMAGE_CAPTURE);

                    // Start the activity with camera_intent,
                    // and request pic id
                    startActivityForResult(camera_intent, pic_id);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //    @Override
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                //h=0;
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        File photo = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//                        //pic = photo;
//                        break;
//                    }
//
//                }
//
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//
//                            bitmapOptions);
//
//                    pic.setImageBitmap(bitmap);
//                    String path = android.os.Environment
//                            .getExternalStorageDirectory()
//                            + File.separator
//                            + "Phoenix" + File.separator + "default";
//                    //p = path;
//                    f.delete();
//                    OutputStream outFile = null;
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//
//                    try {
//                        outFile = new FileOutputStream(file);
//
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        //pic=file;
//                        outFile.flush();
//
//                        outFile.close();
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (requestCode == 2) {
//                Uri selectedImage = data.getData();
//                // h=1;
//                //imgui = selectedImage;
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                Log.w("path of im", picturePath + "");
//                pic.setImageBitmap(thumbnail);
//
//            }
//
//        }
//    }
    public boolean addJpgSignatureToGallery(Bitmap signature,String idd) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("ProfilePad"), String.format("Profile_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;

            sendiPost(photo,idd);

        } catch (Exception e) {
            e.printStackTrace();
            //pop.dismiss();
        }
        return result;
    }

    private void sendiPost(final File photo,final String idd) {
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
                    String Urll = "https://pragathiorganic.in/api/DeliveryBoyProfilePic";

                    Log.e("add",idd);
                    Log.e("nav",photo.getName());

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("deliveryboyid",idd)
                            .addFormDataPart("image",photo.getName(), RequestBody.create(MediaType.parse("image/jpeg"),photo))
                            .build();

                    Log.e("filename:",body.toString());
                    Request request = new Request.Builder()
                            .url(Urll)
                            .post(body)
                            .build();


                    try{
                        Response response = client.newCall(request).execute();
                        Log.e("Response",response.message());


                    }catch (Exception e){
                        Log.e("Exxxxx",e.getMessage());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("No tryy",e.getMessage());
                }
            }
        });

        thread.start();
        //pop.dismiss();
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("ProfilePad", "Directory not created");
        }
        return file;
    }
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }
    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("Uri",imageUri.toString());
        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which stor the image in memory
            try{
                if(data.getExtras().get("data")!=null) {
                    Bitmap photo = (Bitmap)data.getExtras().get("data");
                    prof_picc.setImageBitmap(photo);
                }
            }catch (Exception e){

            }

        }

        else if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                prof_picc.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
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

                    Log.e("Inside",J.getString("username"));

                    AllowAccess(J.getString("mobile_number"), J.getString("password"));
                    jsonParam.put("id", J.getString("id"));
                    jsonParam.put("name", "Name");
                    jsonParam.put("username", NAME.getText().toString());
                    jsonParam.put("password", J.getString("password"));
                    jsonParam.put("mobile_number", PHONE.getText().toString());
                    jsonParam.put("address", ADDRESS.getText().toString());
                    jsonParam.put("token", J.getString("token"));
                    jsonParam.put("vehicle_no", VEHICLE.getText().toString());
                    ;
                    if(urlo!=null)
                        jsonParam.put("profile_pic",urlm);


                    // setting local variable userProfile
                    Bundle args = new Bundle();
                    String userProfileString = jsonParam.toString();
                    args.putString("userProfile", userProfileString);

                    Log.e("usrProfile ",userProfileString);

                    HomeFragment.setArguements(args);
                    ProfileFragment.setArguements(args);
                    PendingOrders.setArguements(args);
                    ProcessingOrders.setArguements(args);
                    CompletedOrders.setArguements(args);
                    CaptureImage.setArguements(args);
                    DigitalSignature.setArguements(args);

                    // also set updated usrname and password in Paper
                    Paper.book().write(Prevalent.UserPhoneNo_ , PHONE.getText().toString());
                    //Paper.book().write(Prevalent.UserPasswordKey__ , "abcd123");

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
                    pop.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("No tryy","");
                    pop.dismiss();
                }
            }
        });

        thread.start();
    }
}
