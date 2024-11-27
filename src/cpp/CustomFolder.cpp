#include <jni.h>
#include <iostream>
#include <filesystem>
#include "CustomFolder.h"

namespace fs = std::filesystem;

extern "C" {

// Implementation of command_mkdir()
JNIEXPORT void JNICALL Java_CustomFolder_command_1mkdir(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    if (fs::create_directory(path)) {
        std::cout << "Directory created: " << path << "\n";
    } else {
        std::cerr << "Failed to create directory: " << path << "\n";
    }
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_rmdir()
JNIEXPORT void JNICALL Java_CustomFolder_command_1rmdir(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    if (fs::remove_all(path)) {
        std::cout << "Directory removed: " << path << "\n";
    } else {
        std::cerr << "Failed to remove directory: " << path << "\n";
    }
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_ls()
JNIEXPORT void JNICALL Java_CustomFolder_command_1ls(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    std::cout << "Contents of " << path << ":\n";
    for (const auto& entry : fs::directory_iterator(path)) {
        std::cout << "  " << entry.path().filename().string() << "\n";
    }
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_ls(option)
JNIEXPORT void JNICALL Java_CustomFolder_command_1lsWithOption(JNIEnv* env, jobject obj, jstring joption) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    const char* option = env->GetStringUTFChars(joption, nullptr);

    if (std::string(option) == "-l") {
        std::cout << "Detailed listing of " << path << ":\n";
        for (const auto& entry : fs::directory_iterator(path)) {
            auto ftime = fs::last_write_time(entry);
            auto sctp = std::chrono::time_point_cast<std::chrono::system_clock::duration>(
                ftime - fs::file_time_type::clock::now() + std::chrono::system_clock::now()
            );
            std::time_t cftime = std::chrono::system_clock::to_time_t(sctp);

            auto size = fs::is_directory(entry) ? 0 : fs::file_size(entry);
            std::cout << "  " << entry.path().filename().string()
                      << " | Size: " << size
                      << " bytes | Last modified: " << std::ctime(&cftime);
        }
    } else {
        std::cerr << "Unsupported option: " << option << "\n";
    }

    env->ReleaseStringUTFChars(jpath, path);
    env->ReleaseStringUTFChars(joption, option);
}
}
