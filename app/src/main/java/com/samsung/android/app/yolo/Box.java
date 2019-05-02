package com.samsung.android.app.yolo;

import android.graphics.RectF;

class Box {
    float score;
    float bx;
    float by;
    float bh;
    float bw;
    int box_class;

    RectF getRectF() {
        RectF rectF = new RectF();
        rectF.left = bx - (bw / 2f);
        rectF.right = bx + (bw / 2f);
        rectF.top = by - (bh / 2f);
        rectF.bottom = by + (bh / 2f);
        return rectF;
    }
}
