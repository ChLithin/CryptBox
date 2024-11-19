#include "CustomFile.h"
#include <fstream>
#include <iostream>
#include <cstdio> // for std::remove and std::rename

CustomFile::CustomFile(const std::string& name, const std::string& path)
    : StorageNode(name, path) {}

void CustomFile::command_touch() {
    std::ofstream file(path + "/" + name);
    file.close();
}

std::string CustomFile::getFileType() const {
    size_t dotPos = name.find_last_of('.');
    return (dotPos != std::string::npos) ? name.substr(dotPos) : "";
}

void CustomFile::command_echo(const std::string& text, const std::string& option) {
    std::ofstream file;
    if (option == "append") {
        file.open(path + "/" + name, std::ios::app);
    } else {
        file.open(path + "/" + name, std::ios::trunc);
    }
    file << text << std::endl;
    file.close();
}

void CustomFile::command_cat() const {
    std::ifstream file(path + "/" + name);
    if (!file) {
        std::cerr << "File not found: " << path + "/" + name << std::endl;
        return;
    }
    std::string line;
    while (std::getline(file, line)) {
        std::cout << line << std::endl;
    }
    file.close();
}

void CustomFile::command_cp(const std::string& destPath) {
    std::ifstream src(path + "/" + name, std::ios::binary);
    std::ofstream dst(destPath, std::ios::binary);
    if (src && dst) {
        dst << src.rdbuf();
    }
    src.close();
    dst.close();
}

void CustomFile::command_mv(const std::string& destPath) {
    std::string oldPath = path + "/" + name;
    std::rename(oldPath.c_str(), destPath.c_str());
    path = destPath.substr(0, destPath.find_last_of('/'));
    name = destPath.substr(destPath.find_last_of('/') + 1);
}

void CustomFile::command_rm() {
    std::remove((path + "/" + name).c_str());
}

void CustomFile::command_rm(const std::string& option) {
    if (option == "force") {
        command_rm();
    } else {
        std::cerr << "Invalid option for command_rm: " << option << std::endl;
    }
}

