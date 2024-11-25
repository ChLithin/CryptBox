#include <jni.h>
#include <iostream>
#include <fstream>
#include <filesystem>
#include "CustomFile.h"

namespace fs = std::filesystem;

extern "C" {

// Implementation of command_touch()
JNIEXPORT void JNICALL Java_CustomFile_command_1touch(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    std::ofstream outfile(path);
    outfile.close();
    std::cout << "File created: " << path << "\n";
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_cat()
JNIEXPORT void JNICALL Java_CustomFile_command_1cat(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    std::ifstream infile(path);
    if (!infile) {
        std::cerr << "Failed to open file: " << path << "\n";
    } else {
        std::cout << "Contents of " << path << ":\n";
        std::cout << infile.rdbuf() << "\n";
    }
    infile.close();
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_echo()
JNIEXPORT void JNICALL Java_CustomFile_command_1echo(JNIEnv* env, jobject obj, jstring jtext, jstring joption) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* text = env->GetStringUTFChars(jtext, nullptr);
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    const char* option = env->GetStringUTFChars(joption, nullptr);

    bool append = (std::string(option) == ">>");
    std::ofstream outfile(path, append ? std::ios::app : std::ios::trunc);
    outfile << text << "\n";
    outfile.close();

    std::cout << "Text written to " << path << " with option " << option << "\n";
    env->ReleaseStringUTFChars(jtext, text);
    env->ReleaseStringUTFChars(jpath, path);
    env->ReleaseStringUTFChars(joption, option);
}

// Implementation of command_rm()
JNIEXPORT void JNICALL Java_CustomFile_command_1rm(JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    if (fs::remove(path)) {
        std::cout << "File removed: " << path << "\n";
    } else {
        std::cerr << "Failed to remove file: " << path << "\n";
    }
    env->ReleaseStringUTFChars(jpath, path);
}

// Implementation of command_rm(option)
JNIEXPORT void JNICALL Java_CustomFile_command_1rmWithOption(JNIEnv* env, jobject obj, jstring joption) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* path = env->GetStringUTFChars(jpath, nullptr);
    const char* option = env->GetStringUTFChars(joption, nullptr);

    if (std::string(option) == "-i") {
        std::cout << "Remove " << path << "? (y/n): ";
        char choice;
        std::cin >> choice;
        if (choice != 'y' && choice != 'Y') {
            std::cout << "File not removed.\n";
            env->ReleaseStringUTFChars(jpath, path);
            env->ReleaseStringUTFChars(joption, option);
            return;
        }
    }

    if (fs::remove(path)) {
        std::cout << "File removed: " << path << "\n";
    } else {
        std::cerr << "Failed to remove file: " << path << "\n";
    }

    env->ReleaseStringUTFChars(jpath, path);
    env->ReleaseStringUTFChars(joption, option);
}


// Implementation of command_cp()
JNIEXPORT void JNICALL Java_CustomFile_command_1cp(JNIEnv* env, jobject obj, jstring jdest) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* src = env->GetStringUTFChars(jpath, nullptr);
    const char* dest = env->GetStringUTFChars(jdest, nullptr);

    try {
        fs::path srcPath(src);
        fs::path destPath(dest);

        // Normalize paths
        srcPath = fs::canonical(srcPath);
        destPath = fs::weakly_canonical(destPath);

        // Verify source file
        if (!fs::exists(srcPath)) {
            std::cerr << "Source file does not exist: " << srcPath << "\n";
            return;
        }
        if (!fs::is_regular_file(srcPath)) {
            std::cerr << "Source is not a file: " << srcPath << "\n";
            return;
        }

        // Handle directory destination
        if (fs::is_directory(destPath)) {
            destPath /= srcPath.filename(); // Add source file name to destination
        }

        // Copy with overwrite
        fs::copy(srcPath, destPath, fs::copy_options::overwrite_existing);
        std::cout << "File copied from " << srcPath << " to " << destPath << "\n";
    } catch (const fs::filesystem_error& e) {
        if (e.code() == std::errc::file_exists) {
            std::cerr << "Error: Destination file already exists.\n";
        } else if (e.code() == std::errc::no_such_file_or_directory) {
            std::cerr << "Error: Source file or destination directory not found.\n";
        } else if (e.code() == std::errc::permission_denied) {
            std::cerr << "Error: Permission denied.\n";
        } else {
            std::cerr << "Filesystem error: " << e.what() << "\n";
        }
    } catch (const std::exception& e) {
        std::cerr << "General error: " << e.what() << "\n";
    }

    env->ReleaseStringUTFChars(jpath, src);
    env->ReleaseStringUTFChars(jdest, dest);
}


// Implementation of command_mv()
JNIEXPORT void JNICALL Java_CustomFile_command_1mv(JNIEnv* env, jobject obj, jstring jdest) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* src = env->GetStringUTFChars(jpath, nullptr);
    const char* dest = env->GetStringUTFChars(jdest, nullptr);

    try {
        fs::rename(src, dest);
        std::cout << "File moved from " << src << " to " << dest << "\n";
    } catch (const std::exception& e) {
        std::cerr << "Move failed: " << e.what() << "\n";
    }

    env->ReleaseStringUTFChars(jpath, src);
    env->ReleaseStringUTFChars(jdest, dest);
}

}
