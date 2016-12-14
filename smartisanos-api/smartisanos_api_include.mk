SMARTISANOS_API_REAL := false
MY_DIR := $(call my-dir)
AbSOLUTE_MY_DIR := $(subst $(LOCAL_PATH)/,, $(MY_DIR))

ifeq ($(LOCAL_PATH), frameworks/base)
    LOCAL_SRC_FILES += $(call all-java-files-under, $(AbSOLUTE_MY_DIR)/java)
    ifeq (true,$(SMARTISANOS_API_REAL))
        LOCAL_SRC_FILES += $(call all-java-files-under, $(AbSOLUTE_MY_DIR)/real)
    else
        LOCAL_SRC_FILES += $(call all-java-files-under, $(AbSOLUTE_MY_DIR)/fake)
    endif
else
    ifneq (true,$(SMARTISANOS_API_REAL))
        LOCAL_SRC_FILES += \
                $(call all-java-files-under, $(AbSOLUTE_MY_DIR)/java) \
                $(call all-java-files-under, $(AbSOLUTE_MY_DIR)/fake)
    endif
endif
