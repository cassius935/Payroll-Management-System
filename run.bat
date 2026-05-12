@echo off
setlocal enabledelayedexpansion

echo.
echo ============================================
echo   PAYROLL MANAGEMENT SYSTEM v2.1
echo ============================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java Development Kit (JDK) 8 or later
    pause
    exit /b 1
)

echo Compiling Java source files...
echo.

REM Compile with proper classpath and encoding
javac -encoding UTF-8 -cp "src;lib\mysql-connector-j-9.6.0.jar" src\*.java 2>&1

if errorlevel 1 (
    echo.
    echo ============================================
    echo Compilation FAILED. Please fix errors above.
    echo ============================================
    pause
    exit /b 1
)

echo.
echo ============================================
echo Compilation SUCCESSFUL!
echo ============================================
echo.
echo Starting Payroll Management System...
echo.
echo Demo Credentials:
echo   Admin:  admin / Admin@2024!
echo   Worker: worker1 / Worker@123
echo.
echo ============================================
echo.

REM Run the application
java -cp "src;lib\mysql-connector-j-9.6.0.jar" src.main

endlocal
pause
