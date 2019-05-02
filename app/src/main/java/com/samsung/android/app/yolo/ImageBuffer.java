package com.samsung.android.app.yolo;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class ImageBuffer {

    private static final String TAG = "ImageBuffer";

    private ByteBuffer mByteBuffer;

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
        
        int[] pixels = new int[YoloConstants.IMAGE_WIDTH * YoloConstants.IMAGE_HEIGHT];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int n = 0;
        for (int i = 0; i < YoloConstants.IMAGE_WIDTH; ++i) {
            for (int j = 0; j < YoloConstants.IMAGE_HEIGHT; ++j) {
                final int pixel = pixels[n++];
                mByteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.f);
                mByteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.f);
                mByteBuffer.putFloat((pixel & 0xFF) / 255.f);
            }
        }
    }

}
