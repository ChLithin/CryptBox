#include "CustomFolder.h"
#include <sys/stat.h>  // for mkdir
#include <unistd.h>    // for rmdir
#include <iostream>
#include <dirent.h>
#include <fstream>   
#include <cerrno>
#include <sys/types.h>
#include <cstring>     // for strerror

CustomFolder::CustomFolder(const std::string& name, const std::string& path)
    : StorageNode(name, path) {}

void CustomFolder::command_mkdir() {
    std::string folderPath = path + "/" + name;
    if (mkdir(folderPath.c_str()) == -1) {
        std::cerr << "Error creating directory: " << strerror(errno) << std::endl;
    }
}

void CustomFolder::command_rmdir() {
    std::string folderPath = path + "/" + name;
    if (rmdir(folderPath.c_str()) == -1) {
        std::cerr << "Error removing directory: " << strerror(errno) << std::endl;
    }
}


void CustomFolder::command_ls() const {
    DIR* dir = opendir((path + "/" + name).c_str());
    if (!dir) {
        std::cerr << "Error opening directory: " << strerror(errno) << std::endl;
        return;
    }
    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        std::cout << entry->d_name << std::endl;
    }
    closedir(dir);
}

void CustomFolder::command_ls(const std::string& option) const {
    if (option == "-a") {
        command_ls();
    } else {
        std::cerr << "Unsupported option: " << option << std::endl;
    }
}

void CustomFolder::command_cd(const std::string& folderName) {
    std::string newPath = path + "/" + folderName;
    DIR* dir = opendir(newPath.c_str());
    if (dir) {
        path = newPath;
        closedir(dir);
    } else {
        std::cerr << "Directory not found: " << newPath << std::endl;
    }
}

void CustomFolder::command_search(const std::string& fileName) const {
    DIR* dir = opendir((path + "/" + name).c_str());
    if (!dir) {
        std::cerr << "Error opening directory: " << strerror(errno) << std::endl;
        return;
    }
    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        if (fileName == entry->d_name) {
            std::cout << "File found: " << fileName << std::endl;
            closedir(dir);
            return;
        }
    }
    std::cout << "File not found: " << fileName << std::endl;
    closedir(dir);
}

void CustomFolder::command_filter(const std::string& extension) const {
    DIR* dir = opendir((path + "/" + name).c_str());
    if (!dir) {
        std::cerr << "Error opening directory: " << strerror(errno) << std::endl;
        return;
    }
    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        std::string fileName = entry->d_name;
        if (fileName.find(extension) != std::string::npos) {
            std::cout << fileName << std::endl;
        }
    }
    closedir(dir);
}

void CustomFolder::command_cp(const std::string& destPath) {
    std::cerr << "Copying directories is not implemented yet." << std::endl;
}

void CustomFolder::command_mv(const std::string& destPath) {
    std::string oldPath = path + "/" + name;
    std::rename(oldPath.c_str(), destPath.c_str());
}

void CustomFolder::command_rm() {
    command_rmdir();
}

void CustomFolder::command_share(const std::string& username) {
    std::cout << "Sharing folder with " << username << std::endl;
}

void CustomFolder::command_touch(const std::string& fileName) {
    // Combine the folder's path with the file name to create the full file path
    std::string filePath = this->path + "/" + fileName;

    // Create or update the file
    std::ofstream file(filePath, std::ios::app); // Open file in append mode, creates if it doesn't exist
    if (!file) {
        std::cerr << "Error: Could not create or update file: " << filePath << std::endl;
    } else {
        std::cout << "File created or updated: " << filePath << std::endl;
    }
    file.close();
}


