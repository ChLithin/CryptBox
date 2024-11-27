# CryptBox: Secure File Management System

CryptBox is a command-line interface (CLI)-based file management system designed to provide users with secure, locally-managed file storage solutions. The application offers enhanced data protection and organizational capabilities through comprehensive encryption and file management features.

## Project Structure
```
CryptBox/
├── src/
│   ├── java/
│   │   ├── CustomFile.java
│   │   ├── CustomFolder.java
│   │   ├── StorageNode.java
│   │   ├── User.java
│   │   ├── CLI.java
│   │   └── MainCLI.java
│   └── cpp/
│       ├── CustomFile.cpp
│       └── CustomFolder.cpp
├── docs/
│   ├── commands.txt
│   └── SRS.pdf
├── bin/
│   └── (.class files generated)
├── lib/
│   └── (library generated)
├── README.md
├── build.sh
└── build.bat
```


## Features

- **Multi-user file system with isolated storage**  
  Allows multiple users to store and manage their files securely with isolated storage for each user.

- **Granular file and folder encryption/decryption**  
  Supports encryption and decryption of individual files and entire folders to protect sensitive data.

- **Dynamic file and folder labeling**  
  Users can add custom labels to files and folders for better organization and searchability.

- **Search based on labels and filter by encryption status and file extension**  
  Powerful search functionality to find files by labels and filter them by their encryption status or file type.

- **File and folder sharing between users**  
  Enables secure sharing of files and folders between different users, maintaining encryption.

- **Secure and native implementation**  
  All encryption and file management operations are implemented natively for maximum performance and data privacy.

## Requirements

- **Java Development Kit (JDK)**: Version 8 or above
- **C++ Compiler**: g++ (with support for C++17 or later)
- **Operating System**: macOS, Windows (with appropriate changes in build scripts)

## Setup Instructions

### 1. Clone the repository

To clone the repository, open your terminal or command prompt and run:

```bash
git clone https://github.com/yourusername/cryptbox.git
cd CryptBox
```
To compile and run the project, follow the steps below:

- For **macOS**, use the following command:
  ```bash
  ./build.sh
  ```
- For **Windows**, use the following command:
  ```bash
  build.bat
  ```
## Supported Commands

A comprehensive list of all supported commands can be found in the [`commands.txt`](docs/commands.txt) file located in the `docs` directory.  

##Software Requirements Specification (SRS)

For detailed technical requirements and system specifications, refer to the [`SRS.pdf`](docs/SRS.pdf) file located in the `docs` directory.


