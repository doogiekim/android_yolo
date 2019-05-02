package com.samsung.android.app.yolo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

class TfModel {
    private static final String TAG = "TfModel";

    private MappedByteBuffer mTfLiteModel;
    private Interpreter mTfLite;
    private final Interpreter.Options mTfLiteOptions = new Interpreter.Options();

    private static final int NUM_THREAD = 4;
    private static final boolean USE_GPU_DELEGATE = false;

    public TfModel(Activity activity) {
        try {
            mTfLiteModel = load(activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void init() {
        mTfLiteOptions.setUseNNAPI(false);
        mTfLiteOptions.setNumThreads(NUM_THREAD);
        if (USE_GPU_DELEGATE) {
            mTfLiteOptions.addDelegate(new GpuDelegate());
        }

        mTfLite = new Interpreter(mTfLiteModel, mTfLiteOptions);
    }

    void release() {
        mTfLite.close();
        mTfLite = null;
    }

    private String getModelPath() {
        return "yolo.tflite";
    }

    MappedByteBuffer load(Activity activity) throws IOException {
        Log.v(TAG, "load");
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

        Log.v(TAG, "Successfully loaded");
        return map;
    }

    public void run(ByteBuffer byteBuffer, float[][][][] out) {
        if (mTfLite != null) {
            mTfLite.run(byteBuffer, out);
        }
    }
}
