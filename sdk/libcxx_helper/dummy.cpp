#include <jni.h>

// empty

extern "C"
JNIEXPORT void JNICALL
Java_learn_kotlin_com_androidopencv_CameraActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                                    jlong mat_addr_input,
                                                                    jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoGray()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_cameraActivity_ConvertRGBtoGray__JJ(jobject thiz,
                                                                   jobject mat_addr_result) {
    // TODO: implement ConvertRGBtoGray()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_ImageActivity_detectEdgeJNI(JNIEnv *env, jobject thiz,
                                                           jlong input_image, jlong output_image,
                                                           jint th1, jint th2) {
    // TODO: implement detectEdgeJNI()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_CameraActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                               jlong mat_addr_input,
                                                               jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoGray()
}