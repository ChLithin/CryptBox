@echo off

REM Step 1: Compile all Java files
echo Compiling Java files...
javac -d bin src/java/*.java
if errorlevel 1 (
    echo Error: Java compilation failed.
    exit /b 1
)

REM Step 2: Generate JNI header files
echo Generating JNI header files...
javac -h src/cpp src/java/StorageNode.java src/java/CustomFolder.java src/java/CustomFile.java
if errorlevel 1 (
    echo Error: JNI header generation failed.
    exit /b 1
)

REM Step 3: Compile C++ files into a shared library (DLL)
echo Compiling C++ files into DLL...
g++ -shared -o lib/cryptbox.dll -I"C:\\Program Files\\Java\\jdk-23\\include" -I"C:\\Program Files\\Java\\jdk-23\\include\\win32" src/cpp/CustomFile.cpp src/cpp/CustomFolder.cpp -std=c++17

if errorlevel 1 (
    echo Error: C++ compilation failed.
    exit /b 1
)

REM Step 4: Run the Java program
echo Running the Java program...
java -Djava.library.path=lib -cp bin MainCLI
if errorlevel 1 (
    echo Error: Failed to execute the Java program.
    exit /b 1
)

echo Build and execution completed successfully.
pause
