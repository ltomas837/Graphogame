LOCAL_PATH := $(call my-dir)





include $(CLEAR_VARS)

LOCAL_MODULE    := Game1

LOCAL_C_INCLUDES := $(LOCAL_PATH)/Game1/Include/
LOCAL_SRC_FILES :=  \
                    Game1/Source/LaunchGame.cpp  \
                    Game1/Source/Rules.cpp  \
                    Game1/Source/Menu.cpp  \
                    Game1/Source/Background.cpp  \
                    Game1/Source/Character.cpp  \
                    Game1/Source/Fence.cpp  \
                    Game1/Source/Game.cpp  \
                    Game1/Source/GameSound.cpp  \
                    Game1/Source/Input.cpp  \

LOCAL_SHARED_LIBRARIES := sfml-system-d
LOCAL_SHARED_LIBRARIES += sfml-window-d
LOCAL_SHARED_LIBRARIES += sfml-graphics-d
LOCAL_SHARED_LIBRARIES += sfml-audio-d
LOCAL_SHARED_LIBRARIES += sfml-network-d
LOCAL_SHARED_LIBRARIES += sfml-activity-d
LOCAL_SHARED_LIBRARIES += openal
LOCAL_WHOLE_STATIC_LIBRARIES := sfml-main-d

include $(BUILD_SHARED_LIBRARY)






include $(CLEAR_VARS)

LOCAL_MODULE    := Game2

LOCAL_C_INCLUDES := $(LOCAL_PATH)/Game2/Include/
LOCAL_SRC_FILES :=  \
                    Game2/Source/LaunchGame.cpp  \
                    Game2/Source/Background.cpp  \
                    Game2/Source/Character.cpp  \
                    Game2/Source/Fence.cpp  \
                    Game2/Source/Game.cpp  \
                    Game2/Source/GameSound.cpp  \
                    Game2/Source/Input.cpp  \

LOCAL_SHARED_LIBRARIES := sfml-system-d
LOCAL_SHARED_LIBRARIES += sfml-window-d
LOCAL_SHARED_LIBRARIES += sfml-graphics-d
LOCAL_SHARED_LIBRARIES += sfml-audio-d
LOCAL_SHARED_LIBRARIES += sfml-network-d
LOCAL_SHARED_LIBRARIES += sfml-activity-d
LOCAL_SHARED_LIBRARIES += openal
LOCAL_WHOLE_STATIC_LIBRARIES := sfml-main-d

include $(BUILD_SHARED_LIBRARY)







$(call import-module,third_party/sfml)
