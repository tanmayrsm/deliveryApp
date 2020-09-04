package com.pragathiOrganic.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CaptureImage extends AppCompatActivity {
    RelativeLayout relativeLayout;
    ImageView pic, Rem;
    Button done;
    TextView main;
    Uri imageUri;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int pic_id = 3333;
    private  static  JSONObject J;
    String ID;
    String BillNO, No;
    ProgressDialog pop;

    public static void setArguements(Bundle args) throws JSONException {
        String userProfileString = args.getString("userProfile");
        J = new JSONObject(userProfileString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        relativeLayout = findViewById(R.id.relative);
        pic = findViewById(R.id.image);
        done = findViewById(R.id.Done);
        main = findViewById(R.id.main_text);
        main.setText("Take photo");
        Rem = findViewById(R.id.rem);


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(CaptureImage.this);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop = new ProgressDialog(CaptureImage.this);
                pop.setMessage("Please wait");
                pop.show();
                BitmapDrawable drawable = (BitmapDrawable) pic.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                if (addJpgSignatureToGallery(bitmap)) {
                    Toast.makeText(CaptureImage.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        BillNO =getIntent().getStringExtra("bill");
        No = getIntent().getStringExtra("no");
        if(BillNO!=null)
            Log.e("billo",BillNO);
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 0, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
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

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;

            ID = J.getString("id");
            sendPost(photo);

            pop.dismiss();
            Intent ii = new Intent(CaptureImage.this, DigitalSignature.class);
            ii.putExtra("id",ID);
            ii.putExtra("bill",BillNO);
            ii.putExtra("no",No);
            ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ii);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            pop.dismiss();
        }
        return result;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("PicPad", "Directory not created");
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
        CaptureImage.this.sendBroadcast(mediaScanIntent);
    }

    public void sendPost(final File photo) {
        //Log.e("J",J.toString());
        //Log.e("Id",ID);
        //Log.e("biill",BillNO);
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
                            .addFormDataPart("id","product")
                            .addFormDataPart("bill_no",BillNO)
                            .addFormDataPart("image",photo.getName(), RequestBody.create(MediaType.parse("image/jpeg"),photo))
                            .addFormDataPart("deliveryboyid",ID)
                            .build();

                    Log.e("filename:",photo.getAbsolutePath());
                    Request request = new Request.Builder()
                            .url(Urll)
                            .post(body)
                            .build();

                    try{
                        Response response = client.newCall(request).execute();
                        Log.e("Response",response.toString());

                        //take his signature


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("Uri",imageUri.toString());
        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which stor the image in memory
            try{
                Bitmap photo = (Bitmap)data.getExtras()
                        .get("data");

                // Set the image in imageview for display
                pic.setImageBitmap(photo);
            }
            catch (Exception e){
            }
        }

        else if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
}
