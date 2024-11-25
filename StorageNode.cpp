#include "StorageNode.h"

StorageNode::StorageNode(const std::string& name, const std::string& path)
    : name(name), path(path) {}

std::string StorageNode::getName() const {
    return name;
}

std::string StorageNode::getPath() const {
    return path;
}
