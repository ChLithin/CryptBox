#ifndef STORAGE_NODE_H
#define STORAGE_NODE_H

#include <string>

class StorageNode {
protected:
    std::string name;
    std::string path;

public:
    StorageNode(const std::string& name, const std::string& path);
    std::string getName() const;
    std::string getPath() const;
};

#endif
