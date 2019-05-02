package com.samsung.android.app.yolo;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class YoloPostProcessor {

    public YoloPostProcessor() {
    }

    public List<Box> performPostProcess(float[][][] output) {
        List<Box> boxes = convertToBox(output);
        boxes = preformNonMaxSuppression(boxes);
        return boxes;
    }

    private List<Box> preformNonMaxSuppression(List<Box> boxes) {
        Object[] boxArray = boxes.toArray();
        int n = boxArray.length;

        float[][] iouScore = new float[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Box box1 = (Box) boxArray[i];
                Box box2 = (Box) boxArray[j];

                float iou = iou(box1.getRectF(), box2.getRectF());

                iouScore[i][j] = iou;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (iouScore[i][j] > 0.6) {
                    if (((Box)boxArray[i]).score > ((Box)boxArray[j]).score) {
                        ((Box)boxArray[j]).score = 0;
                    } else {
                        ((Box)boxArray[i]).score = 0;
                    }
                }
            }
        }

        List<Box> maxBoxes = new ArrayList<>();
        for (Object box : boxArray) {
            if (((Box) box).score > 0) {
                maxBoxes.add((Box) box);
            }
        }

        return maxBoxes;
    }

    private List<Box> convertToBox(float[][][] output) {
        List<Box> boxes = new ArrayList<>();

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                for (int k = 0; k < 5; k++) {
                    Box box = new Box();
                    float confidence = Nn.sigmoid(output[i][j][85 * k + 4]);
                    if (confidence < 0.5) continue;
                    box.score = 0;
                    box.bx = (Nn.sigmoid(output[i][j][85 * k + 0]) + j) / 19;
                    box.by = (Nn.sigmoid(output[i][j][85 * k + 1]) + i) / 19;
                    box.bw = (float) Math.exp(output[i][j][85 * k + 2]) * YoloConstants.ANCHORS[k][0] / 19;
                    box.bh = (float) Math.exp(output[i][j][85 * k + 3]) * YoloConstants.ANCHORS[k][1] / 19;

                    float[] box_cls = new float[80];
                    for (int l = 0; l < 80; l++) {
                        box_cls[l] = output[i][j][85 * k + 5 + l];
                    }

                    Nn.softmax(box_cls);

                    float max_p_c = 0;
                    int max_p_c_index = 0;

                    for (int l = 0; l < 80; l++) {
                        float p_c = confidence * box_cls[l];
                        if (max_p_c < p_c) {
                            max_p_c = p_c;
                            max_p_c_index = l;
                        }
                    }

                    box.score = max_p_c;
                    box.box_class = max_p_c_index;

                    if (box.score > YoloConstants.BOX_THRESHOLD) {
                        boxes.add(box);
                    }
                }
            }
        }

        return boxes;
    }

    float iou(RectF box1, RectF box2) {
        float xi1 = Math.max(box1.left, box2.left);
        float yi1 = Math.max(box1.top, box2.top);
        float xi2 = Math.min(box1.right, box2.right);
        float yi2 = Math.min(box1.bottom, box2.bottom);

        float inter_area = Math.max(0, (xi2 - xi1)) * Math.max(0, (yi2 - yi1));
        float box1_area = (box1.right - box1.left) * (box1.bottom - box1.top);
        float box2_area = (box2.right - box2.left) * (box2.bottom - box2.top);
        float union_area = box1_area + box2_area - inter_area;

        return inter_area / union_area;
    }
}
