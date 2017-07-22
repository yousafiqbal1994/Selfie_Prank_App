package com.photoprank.photoprank;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    public ProgressDialog pDialog;
    Camera camera;
    Preview preview;
    ImageButton buttonClick;
    ProgressBar progressBar;
    public static Bitmap bitmap;
    public boolean cameraGranted=false;
    public boolean storageGranted=false;
    public String apiLevel = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.e(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Adjusting Camera...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        ////////////////////////////////////////// Showing Add
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                super.onAdLoaded();
                pDialog.dismiss();
                displayInterstitial();
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                pDialog.dismiss();
                super.onAdFailedToLoad(errorCode);

            }
        });
        //////////////////////////////////////////
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        buttonClick = (ImageButton) findViewById(R.id.imageButton);
        progressBar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            apiLevel=">=23";
            int allowedPermissions = 0;
            if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.CAMERA" }, 999);
            }else{
                cameraGranted=true;
                allowedPermissions++;
            }
            if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.WRITE_EXTERNAL_STORAGE" }, 888);
            }else{
                storageGranted=true;
                allowedPermissions++;
            }
            if(allowedPermissions==2){ /// already allowed both
                moveOn();
            }
        }else{
            /////// <api23
            apiLevel="<23";
            moveOn();
        }
        buttonClick.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                preview.camera.takePicture(null, rawCallback, jpegCallback);
            }
        });


    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
    private void moveOn() {
        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(apiLevel.equals(">=23")){
            if(cameraGranted==false || storageGranted==false ){
                Toast.makeText(this,"All the permission must be granted",Toast.LENGTH_SHORT).show();
            }
        }
        apiLevel="";
        //finish();
    }

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e(TAG,"onPictureTaken in rawCallback ");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.e(TAG,"onPictureTaken in jpegCallback ");
            camera.stopPreview();
            //finish();
            Intent k = new Intent(MainActivity.this, ImageSet.class);
            startActivity(k);
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG,"onRequestPermissionsResult");
        if(requestCode == 999){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                cameraGranted = true;
                move();
            }
            else{
                cameraGranted = false;
                finish();
            }
        }

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                storageGranted = true;
                move();
            }
            else{
                storageGranted = false;
                finish();
            }
        }

    }
    private void move() {
        if(cameraGranted && storageGranted){
            Log.e(TAG,"move called");
            moveOn();
        }
    }
}