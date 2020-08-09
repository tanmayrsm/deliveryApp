package com.example.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CaptureImage extends AppCompatActivity {
    RelativeLayout relativeLayout;
    ImageView pic;
    Button done;
    TextView main;
    Uri imageUri;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        relativeLayout = findViewById(R.id.relative);
        pic = findViewById(R.id.image);
        done = findViewById(R.id.Done);
        main = findViewById(R.id.main_text);
        main.setText("Take photo");


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(CaptureImage.this);
            }
        });
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

//    private void selectImage() {
//        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImage.this);
//
//        builder.setTitle("Add Photo!");
//
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//
//                if (options[item].equals("Take Photo"))
//                {
//                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    //pic = f;
//                    startActivityForResult(intent, 1);
//                }
//                else if (options[item].equals("Choose from Gallery"))
//                {
//                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, 2);
//                }
//
//                else if (options[item].equals("Cancel")) {
//
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//
//    }
private void selectImage(Context context) {
    final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle("Choose your profile picture");

    builder.setItems(options, new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int item) {

            if (options[item].equals("Take Photo")) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePicture, IMAGE_CAPTURE_CODE);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Uri",imageUri.toString());
        if(requestCode == IMAGE_CAPTURE_CODE){
            pic.setImageURI(imageUri);
        }
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
//                case 0:
//                    if (resultCode == RESULT_OK && data != null) {
//                        pic.setImageURI(imageUri);
//                    }
//
//                    break;
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
