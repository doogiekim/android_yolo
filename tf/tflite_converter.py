import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_keras_model_file("model_data/yolo.h5")
tflite_model = converter.convert()
open("model_data/yolo.tflite", "wb").write(tflite_model)