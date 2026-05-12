@echo off
REM ============================================================
REM Payroll Management System v2.0.0 - SMART BUILD & RUN SCRIPT
REM ============================================================
REM This script compiles and runs the Payroll Management System
REM with automatic error detection and database validation

setlocal enabledelayedexpansion

echo.
echo ============================================================
echo   PAYROLL MANAGEMENT SYSTEM v2.0.0
echo   Smart Build & Run Script
echo ============================================================
echo.

REM Check if Java is installed
echo [1/5] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 8 or higher from:
    echo https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| find "version"') do (
    set JAVA_VERSION=%%g
)
echo Java Version: %JAVA_VERSION%

REM Create bin directory if it doesn't exist
echo.
echo [2/5] Preparing build environment...
if not exist "bin" mkdir bin
if not exist "lib" mkdir lib

REM Compile Java files
echo.
echo [3/5] Compiling Java source files...
cd src
echo Compiling files in: %CD%

javac -encoding UTF-8 -d ..\bin *.java 2>compile_errors.txt
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Showing compilation errors:
    echo.
    type compile_errors.txt
    echo.
    echo ============================================================
    cd ..
    pause
    exit /b 1
)

echo Compilation successful!
cd ..

REM Check for MySQL JAR file
echo.
echo [4/5] Checking for required libraries...
if not exist "lib\mysql-connector-java*.jar" (
    echo WARNING: MySQL JDBC driver not found in lib/ directory
    echo The application will run in DEMO MODE
    echo To enable database features, download:
    echo https://dev.mysql.com/downloads/connector/j/
    echo And place the JAR file in the lib/ folder
    set CLASSPATH=.;bin
) else (
    echo MySQL JDBC driver found
    set CLASSPATH=.;bin;lib\*
)

REM Run the application
echo.
echo [5/5] Starting Payroll Management System...
echo.
echo ============================================================
echo.

cd bin
java -Dfile.encoding=UTF-8 -cp %CLASSPATH% src.main

if errorlevel 1 (
    echo.
    echo ============================================================
    echo ERROR: Application encountered an error
    echo Please check the console output above for details
    echo ============================================================
    pause
)

cd..

