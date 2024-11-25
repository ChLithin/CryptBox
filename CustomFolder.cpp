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


// Implementation of command_cp()
JNIEXPORT void JNICALL Java_CustomFolder_command_1cp(JNIEnv* env, jobject obj, jstring jdest) {
    // Get the class and field IDs
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    // Retrieve source directory (current directory) and destination directory from Java fields
    const char* src = env->GetStringUTFChars(jpath, nullptr);
    const char* dest = env->GetStringUTFChars(jdest, nullptr);

    try {
        fs::path srcPath(src);   // Current directory is the source
        fs::path destPath(dest); // User-specified destination

        // Normalize paths
        srcPath = fs::canonical(srcPath);        // Resolve the source to an absolute path
        destPath = fs::weakly_canonical(destPath); // Normalize destination path

        // Verify that the source exists and is a directory
        if (!fs::exists(srcPath)) {
            std::cerr << "Source directory does not exist: " << srcPath << "\n";
            return;
        }
        if (!fs::is_directory(srcPath)) {
            std::cerr << "Source is not a directory: " << srcPath << "\n";
            return;
        }

        // If destination directory exists, copy source directory into it
        if (fs::exists(destPath) && fs::is_directory(destPath)) {
            destPath /= srcPath.filename(); // Append the source directory's name to the destination
        }

        // Perform the recursive copy with overwrite
        fs::copy(srcPath, destPath, fs::copy_options::recursive | fs::copy_options::overwrite_existing);
        std::cout << "Directory copied from " << srcPath << " to " << destPath << "\n";
    } catch (const fs::filesystem_error& e) {
        // Handle specific filesystem errors
        if (e.code() == std::errc::file_exists) {
            std::cerr << "Error: Destination directory already exists.\n";
        } else if (e.code() == std::errc::no_such_file_or_directory) {
            std::cerr << "Error: Destination directory not found.\n";
        } else if (e.code() == std::errc::permission_denied) {
            std::cerr << "Error: Permission denied.\n";
        } else {
            std::cerr << "Filesystem error: " << e.what() << "\n";
        }
    } catch (const std::exception& e) {
        std::cerr << "General error: " << e.what() << "\n";
    }

    // Release the UTF strings
    env->ReleaseStringUTFChars(jpath, src);
    env->ReleaseStringUTFChars(jdest, dest);
}


// Implementation of command_mv()
JNIEXPORT void JNICALL Java_CustomFolder_command_1mv(JNIEnv* env, jobject obj, jstring jdest) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fidPath = env->GetFieldID(cls, "actualPath", "Ljava/lang/String;");
    jstring jpath = (jstring)env->GetObjectField(obj, fidPath);

    const char* src = env->GetStringUTFChars(jpath, nullptr);
    const char* dest = env->GetStringUTFChars(jdest, nullptr);

    try {
        fs::rename(src, dest);
        std::cout << "Directory moved from " << src << " to " << dest << "\n";
    } catch (const std::exception& e) {
        std::cerr << "Move failed: " << e.what() << "\n";
    }

    env->ReleaseStringUTFChars(jpath, src);
    env->ReleaseStringUTFChars(jdest, dest);
}

}
