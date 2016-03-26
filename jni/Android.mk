LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE     := IncrementUpdate
LOCAL_SRC_FILES  := cn_coolspan_open_IncrementUpdateLibs_IncrementUpdateUtil.c

LOCAL_LDLIBS     := -lz -llog

include $(BUILD_SHARED_LIBRARY)