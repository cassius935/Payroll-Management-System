#!/bin/bash

#############################################################
# Payroll Management System v2.0.0 - Build & Run Script
# For Linux and macOS systems
#############################################################

set -e  # Exit on any error

echo ""
echo "============================================================"
echo "  PAYROLL MANAGEMENT SYSTEM v2.0.0"
echo "  Smart Build & Run Script"
echo "============================================================"
echo ""

# Step 1: Check Java installation
echo "[1/5] Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed"
    echo "Please install Java 8 or higher"
    echo "macOS: brew install openjdk@11"
    echo "Linux: sudo apt-get install default-jdk"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "Java Version: $JAVA_VERSION"

# Step 2: Create directories
echo ""
echo "[2/5] Preparing build environment..."
mkdir -p bin
mkdir -p lib

# Step 3: Compile Java files
echo ""
echo "[3/5] Compiling Java source files..."
echo "Compiling files..."

cd src
javac -encoding UTF-8 -d ../bin *.java 2> ../compile_errors.txt || {
    echo ""
    echo "ERROR: Compilation failed!"
    echo "Showing compilation errors:"
    echo ""
    cat ../compile_errors.txt
    cd ..
    exit 1
}

echo "Compilation successful!"
cd ..

# Step 4: Check for MySQL driver
echo ""
echo "[4/5] Checking for required libraries..."

if ls lib/mysql-connector-java*.jar &> /dev/null; then
    echo "MySQL JDBC driver found"
    CLASSPATH=".:bin:lib/*"
else
    echo "WARNING: MySQL JDBC driver not found in lib/ directory"
    echo "The application will run in DEMO MODE"
    echo "To enable database features, download from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    CLASSPATH=".:bin"
fi

# Step 5: Run the application
echo ""
echo "[5/5] Starting Payroll Management System..."
echo ""
echo "============================================================"
echo ""

cd bin
java -Dfile.encoding=UTF-8 -cp "$CLASSPATH" src.main

EXIT_CODE=$?

cd ..

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo "============================================================"
    echo "ERROR: Application terminated with error code: $EXIT_CODE"
    echo "Please check the console output above for details"
    echo "============================================================"
    exit $EXIT_CODE
fi

echo ""
echo "Application closed successfully"
