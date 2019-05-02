package com.samsung.android.app.yolo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

class YoloModelRunner {

    private HandlerThread mBackgroundThread;
    private Handler mHandler;
    private ImageSource mImageSource;
    private ImageBuffer mImageBuffer;
    private TfModel mTfModel;
    private YoloPostProcessor mYoloPostProcessor;
    private OnObjectDetectedListener mOnObjectDetectedListener = null;

    YoloModelRunner(Activity activity) {
        mImageBuffer = new ImageBuffer();
        mTfModel = new TfModel(activity);
        mYoloPostProcessor = new YoloPostProcessor();
    }

    void start() {
        mBackgroundThread = new HandlerThread("tflite-thread");
        mBackgroundThread.start();
        mHandler = new Handler(mBackgroundThread.getLooper());
        mHandler.post(mTfModel::init);
        mHandler.post(mDetectRunnable);
    }

    void stop() {
        mImageSource = null;
        mHandler.removeCallbacks(mDetectRunnable);
        mBackgroundThread.quit();
        mBackgroundThread = null;
        mHandler = null;
        mTfModel.release();
    }

    void setImageSource(ImageSource imageSource) {
        mImageSource = imageSource;
    }

    private Runnable mDetectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mImageSource != null) {
                Bitmap b = mImageSource.getBitmap(YoloConstants.IMAGE_WIDTH, YoloConstants.IMAGE_HEIGHT);
                if (b != null) {
                    mImageBuffer.setBitmap(b);
                    float[][][][] out = new float[1][19][19][425];
                    mTfModel.run(mImageBuffer.getByteBuffer(), out);

                    new Thread(() -> handleOutput(out[0])).run();
                }
            }
            if (mHandler != null) {
                mHandler.post(mDetectRunnable);
            }
        }
    };

    private void handleOutput(float[][][] output) {
        List<Box> boxes = mYoloPostProcessor.performPostProcess(output);

        if (mOnObjectDetectedListener != null) {
            mOnObjectDetectedListener.OnObjectDetected(boxes);
        }
    }

    public void setOnObjectDetectedListener(OnObjectDetectedListener onObjectDetectedListener) {
        mOnObjectDetectedListener = onObjectDetectedListener;
    }
}
