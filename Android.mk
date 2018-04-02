LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	D:\android_sample\MoblieApp\app\src\main\jniLibs\arm64-v8a\libjpgt.so \
	D:\android_sample\MoblieApp\app\src\main\jniLibs\arm64-v8a\liblept.so \
	D:\android_sample\MoblieApp\app\src\main\jniLibs\arm64-v8a\libopencv_java3.so \
	D:\android_sample\MoblieApp\app\src\main\jniLibs\arm64-v8a\libpngt.so \
	D:\android_sample\MoblieApp\app\src\main\jniLibs\arm64-v8a\libtess.so \

LOCAL_C_INCLUDES += D:\android_sample\MoblieApp\app\src\debug\jni
LOCAL_C_INCLUDES += D:\android_sample\MoblieApp\app\src\main\jniLibs
LOCAL_C_INCLUDES += D:\android_sample\MoblieApp\app\src\main\jni

include $(BUILD_SHARED_LIBRARY)
