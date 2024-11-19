#ifndef CUSTOM_FILE_H
#define CUSTOM_FILE_H

#include "StorageNode.h"
#include <string>

class CustomFile : public StorageNode {
public:
    CustomFile(const std::string& name, const std::string& path);
    void command_touch();
    std::string getFileType() const;
    void command_echo(const std::string& text, const std::string& option);
    void command_cat() const;
    void command_cp(const std::string& destPath);
    void command_mv(const std::string& destPath);
    void command_rm();
    void command_rm(const std::string& option);
};

#endif
