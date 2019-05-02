package com.samsung.android.app.yolo;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class ImageBuffer {

    private static final String TAG = "ImageBuffer";

    private ByteBuffer mByteBuffer;
    private int[] intValues = new int[YoloConstants.IMAGE_WIDTH * YoloConstants.IMAGE_HEIGHT];

    public ImageBuffer() {
        initBuffer();
    }

    private void initBuffer() {
        mByteBuffer =
                ByteBuffer.allocateDirect(YoloConstants.IMAGE_WIDTH * YoloConstants.IMAGE_HEIGHT * YoloConstants.DIM_PIXEL_SIZE * 4);
        mByteBuffer.order(ByteOrder.nativeOrder());
    }

    public ByteBuffer getByteBuffer() {
        return mByteBuffer;
    }

    public void setBitmap(Bitmap bitmap) {
        if (mByteBuffer == null) {
            return;
        }
        mByteBuffer.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < YoloConstants.IMAGE_WIDTH; ++i) {
            for (int j = 0; j < YoloConstants.IMAGE_HEIGHT; ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + (endTime - startTime));
    }

    private void addPixelValue(int pixelValue) {
        mByteBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.f);
        mByteBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.f);
        mByteBuffer.putFloat((pixelValue & 0xFF) / 255.f);
    }
}
