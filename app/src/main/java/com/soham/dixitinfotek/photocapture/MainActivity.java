package com.soham.dixitinfotek.photocapture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button capture, save;
    private ImageView imageHolder;
    private File photofile, file;
    private int TAKENPHOTO = 20;
    Bitmap photo;
    EditText tv;
    private static final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }

        capture = findViewById(R.id.capture);
        save = findViewById(R.id.save);
        imageHolder = findViewById(R.id.iv_image);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        tv = findViewById(R.id.tv);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File photostorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                photofile = new File(photostorage, (System.currentTimeMillis()) + ".jpg");

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent to start camera
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
                startActivityForResult(i, TAKENPHOTO);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tv.getText().toString().equals(""))
                {
                    tv.setError("Enter commodity code");
                    tv.requestFocus();
                  //  Toast.makeText(MainActivity.this,String.valueOf("yo"),Toast.LENGTH_SHORT).show();
                }

                else
                {
                    imageHolder.setDrawingCacheEnabled(true);
                    Bitmap bitmap = imageHolder.getDrawingCache();

                    String root = Environment.getExternalStorageDirectory().toString();
                    File newDir = new File(root + "/cpi_images");
                    newDir.mkdirs();
                    //Toast.makeText(MainActivity.this,String.valueOf(root),Toast.LENGTH_SHORT).show();
                    Random gen = new Random();
                    int n = 10000;
                    n = gen.nextInt(n);
                    String fname = tv.getText().toString();

                    String fotoname = fname+".jpg";
                    File file = new File(newDir, fotoname);
                    if (file.exists()) file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                        Toast.makeText(getApplicationContext(), "saved to your defined folder", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);

                    } catch (Exception e) {

                    }
                }



            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Please allow permissions in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(this.TAKENPHOTO == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageHolder.setImageBitmap(bitmap);
        }*/

        if (requestCode == TAKENPHOTO) {

            try {
                photo = (Bitmap) data.getExtras().get("data");
            } catch (NullPointerException ex) {
                photo = BitmapFactory.decodeFile(photofile.getAbsolutePath());
            }

            if (photo != null) {
                imageHolder.setImageBitmap(photo);
            } else {

                Toast.makeText(this, "Can't get the photo, try again", Toast.LENGTH_LONG).show();
            }
        }

    }
}
