LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    android-support-v7-cardview \
    dkcolorpicker

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
    external/dkcolorpicker/res \
    frameworks/support/v7/cardview/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.cardview \
    --extra-packages net.darkkatrom.dkcolorpicker

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_PACKAGE_NAME := DKSettings
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

include $(BUILD_PACKAGE)
