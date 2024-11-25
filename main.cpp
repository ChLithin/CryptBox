#include <iostream>
#include <sys/stat.h>  // For mkdir
#include <dirent.h>    // For opendir, readdir
#include <unistd.h>    // For rmdir, unlink
#include <cstring>     // For strerror
#include "CustomFile.h"
#include "CustomFolder.h"

// Helper function to create a directory if it doesn't exist
void createTestEnvironment() {
    const char* testFolderPath = "./testFolder";
    struct stat info;

    if (stat(testFolderPath, &info) != 0) {
        if (mkdir(testFolderPath) == 0) {
            std::cout << "Created test folder: " << testFolderPath << std::endl;
        } else {
            std::cerr << "Error creating test folder: " << strerror(errno) << std::endl;
        }
    } else if (!(info.st_mode & S_IFDIR)) {
        std::cerr << "Error: " << testFolderPath << " exists but is not a directory." << std::endl;
    }
}

int main() {
    try {
        createTestEnvironment(); // Ensure test directory exists

        // Testing CustomFile
        std::cout << "Testing CustomFile..." << std::endl;
        CustomFile file("testFile.txt", "./testFolder");
        
        // Touch file
        file.command_touch();
        std::cout << "File type: " << file.getFileType() << std::endl;

        // Echo to file
        file.command_echo("Hello, World!", "overwrite");
        file.command_cat();  // Read content of file

        file.command_echo(" Appended text.", "append");
        file.command_cat();  // Verify append

        // Copy and move operations
        file.command_cp("./testFolder/copy_testFile.txt");
        file.command_mv("./testFolder/moved_testFile.txt");

        // Remove file
        file.command_rm();

        std::cout << "CustomFile tests passed." << std::endl;

        // Testing CustomFolder
        std::cout << "Testing CustomFolder..." << std::endl;
        CustomFolder folder("testFolder", "./");

        // List directory contents
        folder.command_ls();
        folder.command_ls("detailed");

        // Create subfolder and navigate into it
        folder.command_cd("./testFolder");
        folder.command_touch("newFile.txt");

        // Search and filter
        folder.command_search("testFile");
        folder.command_filter();
        folder.command_filter(".txt");

        // Copy, move, and share folder
        folder.command_cp("./copiedFolder");
        folder.command_mv("./movedFolder");
        folder.command_share("exampleUser");

        // Remove folder
        folder.command_rm();

        std::cout << "CustomFolder tests passed." << std::endl;
    } catch (const std::exception& e) {
        std::cerr << "Error during testing: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
