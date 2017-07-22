package com.photoprank.photoprank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YouCaf Iqbal on 11/12/2016.
 */

public class StartActivity extends AppCompatActivity {
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    ImageButton start;
    final Handler handler = new Handler();
    Timer t = new Timer();
    public ProgressDialog pDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.start);
        pDialog = new ProgressDialog(StartActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        ////////////////////////////////////////// Showing Add
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(StartActivity.this);
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                super.onAdLoaded();
                pDialog.dismiss();
                t.schedule(new TimerTask() {
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                displayInterstitial();
                            }
                        });
                    }
                }, 500);

            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                pDialog.dismiss();
            }
        });
        //////////////////////////////////////////
        start = (ImageButton) findViewById(R.id.imageButton2);
        start.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent k = new Intent(StartActivity.this, MainActivity.class);
                startActivity(k);
                finish();
            }
        });
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
