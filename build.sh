#!/bin/bash

# Stop the script if any command fails
set -e

# Step 1: Compile all Java files
echo "Compiling Java files..."
javac -d bin src/java/*.java

# Step 2: Generate header files for JNI
echo "Generating JNI headers..."
javac -h src/cpp src/java/StorageNode.java src/java/CustomFolder.java src/java/CustomFile.java

# Step 3: Compile and create the shared library for C++ code
#change the jni path accordingly
echo "Compiling C++ files and creating shared library..."
g++ -shared -o lib/libcryptbox.dylib -fPIC \
    -I/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/include \
    -I/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/include/darwin \
    src/cpp/CustomFile.cpp src/cpp/CustomFolder.cpp -std=c++17

# Step 4: Print a message to indicate success
echo "Build successful! Run the program using:"
echo "Running"
java -Djava.library.path=lib -cp bin MainCLI
