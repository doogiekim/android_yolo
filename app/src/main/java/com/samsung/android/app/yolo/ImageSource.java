package com.samsung.android.app.yolo;

import android.graphics.Bitmap;

public interface ImageSource {
    Bitmap getBitmap(int width, int height);
}
