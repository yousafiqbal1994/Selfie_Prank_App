package com.photoprank.photoprank;
import java.io.IOException;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("ALL")
class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";
    SurfaceHolder mHolder;
    public Camera camera;
    int width,height;
    List<Camera.Size> mSupportedPreviewSizes;
    Camera.Size mPreviewSize;
    Context c;
    Preview(Context context) {
        super(context);
        Log.e(TAG,"Preview");
        mHolder = getHolder();
        this.c=context;
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG,"surfaceCreated");
        try {
            camera = Camera.open(0); // back camera opened
        } catch (Exception e) {
            System.exit(0);
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG,"surfaceDestroyed");
        camera.stopPreview();
        camera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG,"surfaceChanged");
//        mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
//        setMeasuredDimension(width, height);
//        if (mSupportedPreviewSizes != null) {
//            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//        }
        Camera.Parameters parameters = camera.getParameters();
//        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void draw(Canvas canvas) {
            Log.e(TAG,"draw");
            super.draw(canvas);
            Paint p= new Paint(Color.RED);
            Log.e(TAG,"draw");
            canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        Log.e(TAG,"getOptimalPreviewSize");
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        Log.e(TAG,"onMeasure");
//
//    }
}
