package com.photoprank.photoprank;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSet extends Activity {
    private static final String TAG = "ImageSet";
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    ImageView imageView;
    private BroadcastReceiver receiver = null;
    final Handler handler = new Handler();
    Timer t = new Timer();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //////////////////////////////////////// Showing Add
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(ImageSet.this);
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });
        //////////////////////////////////////////
        setContentView(R.layout.setimage);
        try {
            t.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            Intent serviceIntent = new Intent(getApplicationContext(), SelfieService.class);
                            startService(serviceIntent);
                        }
                    });
                }
            }, 200);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        imageView = (ImageView) ((FrameLayout) findViewById(R.id.preview)).findViewById(R.id.imageView);
        if(MainActivity.bitmap!=null){
            imageView.setImageBitmap(MainActivity.bitmap);
            MainActivity.bitmap=null;
        }
        showSpinner();
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView.setImageBitmap(null);
    }

    private void showSpinner() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                if (!success) {
                    stopService(new Intent(getApplicationContext(), SelfieService.class));
                }else{
                    stopService(new Intent(getApplicationContext(), SelfieService.class));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("selfie"));
    }

}
