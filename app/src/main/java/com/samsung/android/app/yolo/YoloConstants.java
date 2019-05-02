package com.samsung.android.app.yolo;

public class YoloConstants {
    public static final int IMAGE_WIDTH = 608;
    public static final int IMAGE_HEIGHT = 608;

    public static final int DIM_PIXEL_SIZE = 3;

    public static final double BOX_THRESHOLD = 0.7;

    public static final float[][] ANCHORS = new float[][]{
            {0.57273f, 0.677385f},
            {1.87446f, 2.06253f},
            {3.33843f, 5.47434f},
            {7.88282f, 3.52778f},
            {9.77052f, 9.16828f}};

    public static String[] OBJECT_TEXT = {
            "person",
            "bicycle",
            "car",
            "motorbike",
            "aeroplane",
            "bus",
            "train",
            "truck",
            "boat",
            "traffic_light",
            "fire_hydrant",
            "stop_sign",
            "parking_meter",
            "bench",
            "bird",
            "cat",
            "dog",
            "horse",
            "sheep",
            "cow",
            "elephant",
            "bear",
            "zebra",
            "giraffe",
            "backpack",
            "umbrella",
            "handbag",
            "tie",
            "suitcase",
            "frisbee",
            "skis",
            "snowboard",
            "sports_ball",
            "kite",
            "baseball_bat",
            "baseball_glove",
            "skateboard",
            "surfboard",
            "tennis_racket",
            "bottle",
            "wine_glass",
            "cup",
            "fork",
            "knife",
            "spoon",
            "bowl",
            "banana",
            "apple",
            "sandwich",
            "orange",
            "broccoli",
            "carrot",
            "hot_dog",
            "pizza",
            "donut",
            "cake",
            "chair",
            "sofa",
            "pottedplant",
            "bed",
            "diningtable",
            "toilet",
            "tvmonitor",
            "laptop",
            "mouse",
            "remote",
            "keyboard",
            "cell_phone",
            "microwave",
            "oven",
            "toaster",
            "sink",
            "refrigerator",
            "book",
            "clock",
            "vase",
            "scissors",
            "teddy_bear",
            "hair_drier",
            "toothbrush",
    };

    private YoloConstants() {

    }
}
