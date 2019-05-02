package com.samsung.android.app.yolo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

class CameraInput implements ImageSource {

    private static final String TAG = "CameraInput";

    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private String[] mCameraIdList;
    private String mCurrentCameraId;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private Handler mHandler;
    private Activity mActivity;
    private HandlerThread mBackgroundThread;

    private int mWidth;
    private int mHeight;
    private TextureView mTextureView;
    private CameraCharacteristics mCharacteristics;

    CameraInput(Activity activity) {
        mActivity = activity;
    }

    void start() {
        startThread();
        initSurfaceAndStartCamera();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void startThread() {
        mBackgroundThread = new HandlerThread("CameraInputThread");
        mBackgroundThread.start();
        mHandler = new Handler(mBackgroundThread.getLooper());
    }

    void stop() {
        if (mSession != null) {
            try {
                mSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mSession.close();
            mSession = null;
        }
        mBackgroundThread.quit();
        mBackgroundThread = null;
        mHandler = null;
    }

    private void initSurfaceAndStartCamera() {
        mTextureView = mActivity.findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mWidth = width;
                mHeight = height;
                mSurfaceTexture = surface;

                try {
                    init(mActivity);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Error accessing camera");
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void init(Activity activity) throws CameraAccessException {
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        mCameraIdList = mCameraManager.getCameraIdList();
        if (mCameraIdList == null || mCameraIdList.length == 0) {
            return;
        }

        mCurrentCameraId = mCameraIdList[0];
        mCharacteristics = mCameraManager.getCameraCharacteristics(mCurrentCameraId);

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Camera Permission is missing");
            return;
        }

        Log.v(TAG, "Openning camera device");

        mCameraManager.openCamera(mCurrentCameraId, mDeviceStateCallback, new Handler());
    }

    private CameraCaptureSession mSession;
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.v(TAG, "Session configured successfully");

            CaptureRequest.Builder builder = null;
            try {
                builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(mSurface);
                builder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                CaptureRequest previewRequest = builder.build();

                Log.v(TAG, "Starting preview");
                mSession = session;
                OutputConfiguration outputConfiguration = new OutputConfiguration(mSurface);
                mSession.updateOutputConfiguration(outputConfiguration);
                mSession.setRepeatingRequest(previewRequest, new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                    }
                }, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.v(TAG, "Camera device is opened");

            mCameraDevice = camera;

            mSurfaceTexture.setDefaultBufferSize(mWidth, mHeight);
            mSurface = new Surface(mSurfaceTexture);

            try {
                Log.v(TAG, "Starting session");
                mCameraDevice.createCaptureSession(Arrays.asList(mSurface), mSessionStateCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            mCameraDevice = null;
        }
    };

    @Override
    public Bitmap getBitmap(int width, int height) {
        return mTextureView.getBitmap(width, height);
    }
}
