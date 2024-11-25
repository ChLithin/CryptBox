#ifndef CUSTOM_FOLDER_H
#define CUSTOM_FOLDER_H

#include "StorageNode.h"
#include <string>

class CustomFolder : public StorageNode {
public:
    CustomFolder(const std::string& name, const std::string& path);
    void command_mkdir();
    void command_rmdir();
    void command_ls() const;
    void command_ls(const std::string& option) const;
    void command_cd(const std::string& folderName);
    void command_search(const std::string& fileName) const;
    void command_filter(const std::string& extension = "") const;
    void command_cp(const std::string& destPath);
    void command_mv(const std::string& destPath);
    void command_rm();
    void command_share(const std::string& username);
    void command_touch(const std::string& fileName);
};

#endif
