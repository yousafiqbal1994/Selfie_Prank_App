package com.photoprank.photoprank;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

@SuppressWarnings("ALL")
public class SelfieService extends Service
{
    private static final String TAG = "SelfieService";
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntent = new Intent("selfie");
    final Handler handler = new Handler();
    Timer t = new Timer();
    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Parameters parameters;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        broadcaster = LocalBroadcastManager.getInstance(this);
        try{
            mCamera = Camera.open(1); // Open front camera
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Oops Sorry...You can Try again or may be this app is not supported by your device",Toast.LENGTH_LONG);
            System.exit(0);
        }

        SurfaceView sv = new SurfaceView(getApplicationContext());
        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            t.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                            mCamera.takePicture(null, null, mCall);
                        }
                    });
                }
            }, 300);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sHolder = sv.getHolder();
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Service Destroyed");
        mCamera.release();
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(byte[] data, Camera camera)
        {
            Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.e(TAG,"Selfie Captured");
            broadcastIntent.putExtra("success", true);
            broadcaster.sendBroadcast(broadcastIntent);
            createDirectoryAndSaveFile(bitmap,getImageName());
        }
    };

    public String getImageName(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int n = 10000;
        n = random.nextInt(n);
        for (int i = 0; i < 7; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String name = "A"+sb.toString()+n+".JPEG";
        return name;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/PhotoPrank");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/PhotoPrank/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory().getPath()+"/PhotoPrank/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, this.getString(R.string.app_name));
            values.put(MediaStore.Images.Media.DESCRIPTION, this.getString(R.string.app_name));
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.e(TAG,"Image Saved");
            Log.e(TAG,"Selfie Captured");
            broadcastIntent.putExtra("success", true);
            broadcaster.sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}