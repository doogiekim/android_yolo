package com.samsung.android.app.yolo;

import java.util.List;

public interface OnObjectDetectedListener {
    void OnObjectDetected(List<Box> boxes);
}
