@echo off
REM ========================================
REM Payroll Management System Build Script
REM Compiles all Java files to bin directory
REM ========================================

echo.
echo ============================================
echo   PAYROLL MANAGEMENT SYSTEM - BUILD
echo ============================================
echo.

REM Check if Java compiler is available
javac -version >nul 2>&1
if errorlevel 1 (
    echo Error: javac is not installed or not in PATH
    echo Please install JDK (Java Development Kit) 8 or later
    pause
    exit /b 1
)

REM Create bin directory if it doesn't exist
if not exist bin (
    echo Creating bin directory...
    mkdir bin
    echo.
)

REM Compile all Java files from src directory
echo Compiling source files...
echo.

javac -encoding UTF-8 -cp "src;lib\mysql-connector-j-9.6.0.jar" -d bin src\*.java

if errorlevel 1 (
    echo.
    echo ============================================
    echo Compilation FAILED!
    echo ============================================
    pause
    exit /b 1
)

echo.
echo ============================================
echo Compilation SUCCESSFUL!
echo ============================================
echo.
echo Files compiled to bin/ directory
echo Total classes: %number_of_files%
echo.
echo Next steps:
echo   1. Run "run.bat" to execute the application
echo   2. Or use: java -cp bin;lib\mysql-connector-j-9.6.0.jar src.main
echo.
pause
