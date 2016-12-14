LOCAL_PATH := $(call my-dir)


# A helper sub-library that contains the R class only. Used to compiled the final library
# without being included in it.
#include $(CLEAR_VARS)
#LOCAL_MODULE := smartisanos-r
#
#smartisanos_source_path := APPS/smartisanos_intermediates/src
#LOCAL_INTERMEDIATE_SOURCES := \
                       $(smartisanos_source_path)/smartisanos/R.java

#include $(BUILD_STATIC_JAVA_LIBRARY)

#############################################################################################
include $(CLEAR_VARS)
LOCAL_MODULE := smartisanos
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DX_FLAGS := --core-library

LOCAL_AIDL_INCLUDES := $(LOCAL_PATH)/core/java

LOCAL_SRC_FILES := $(call all-java-files-under,core) \
            $(call all-Iaidl-files-under, core/java)
            
include $(LOCAL_PATH)/smartisanos-api/smartisanos_api_include.mk


#LOCAL_JAVA_LIBRARIES += \
#                        smartisanos-r

smartisanos_source_path := APPS/framework-smartisanos-res_intermediates/src
LOCAL_INTERMEDIATE_SOURCES := \
            $(smartisanos_source_path)/smartisanos/R.java \
            $(smartisanos_source_path)/com/smartisanos/internal/R.java

include $(BUILD_JAVA_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
include $(BUILD_MULTI_PREBUILT)

# Make sure that R.java and Manifest.java are built before we build
# the source for this library.
framework_smartisanos_res_R_stamp := \
    $(call intermediates-dir-for,APPS,framework-smartisanos-res,,COMMON)/src/R.stamp
$(full_classes_compiled_jar): $(framework_smartisanos_res_R_stamp)

# Make sure that framework-res is installed when framework is.
$(LOCAL_INSTALLED_MODULE): | $(dir $(LOCAL_INSTALLED_MODULE))framework-smartisanos-res.apk

# Include this library in the build server's output directory
#$(call dist-for-goals, droidcore sdk, $(LOCAL_BUILT_MODULE):smartisanos.jar)

ifeq (,$(ONE_SHOT_MAKEFILE))
include $(call first-makefiles-under,$(LOCAL_PATH))
endif
