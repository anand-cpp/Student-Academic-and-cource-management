@echo off
setlocal enabledelayedexpansion
cd /d "C:\Users\anand\Student-Academic-and-cource-management"

set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
set MYSQLD_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
set DATA_DIR="C:\ProgramData\MySQL\MySQL Server 8.4\Data"

echo ====================================================================
echo  STUDENT ACADEMIC AND COURSE MANAGEMENT SYSTEM - LOCAL RUN
echo ====================================================================

echo.
echo [1/5] Starting MySQL with skip-grant-tables...
start "" /b %MYSQLD_BIN% --datadir=%DATA_DIR% --port=3306 --skip-grant-tables --shared-memory --console

echo Waiting for MySQL to be ready...
set READY=0
for /L %%i in (1,1,30) do (
    %MYSQL_BIN% -u root -e "SELECT 1;" 2>nul
    if !errorlevel! equ 0 (
        set READY=1
        goto MySQLReady
    )
    echo   Attempt %%i/30...
    timeout /t 2 /nobreak >nul
)
echo ERROR: MySQL failed to start
goto End

:MySQLReady
echo MySQL is running on port 3306!

echo.
echo [2/5] Setting up database...
%MYSQL_BIN% -u root -e "CREATE DATABASE IF NOT EXISTS student_academic_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

echo Running schema.sql...
%MYSQL_BIN% -u root student_academic_management < sql\schema.sql
if !errorlevel! neq 0 (
    echo   (schema may already be applied - continuing)
) else (
    echo   Schema applied successfully.
)

echo Tables:
%MYSQL_BIN% -u root -e "SHOW TABLES FROM student_academic_management;" 2>&1 | findstr /V "^Warning"

echo.
echo [3/5] Compiling Java application (JDK 21 + JDBC, no build tool)...
if not exist build mkdir build
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\util\*.java 2>&1
if !errorlevel! neq 0 (
    echo ERROR: Compilation failed!
    goto End
)
echo   Compilation successful.

echo.
echo [4/5] Configuring database connection...
if not exist config mkdir config
echo db.url=jdbc:mysql://localhost:3306/student_academic_management?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC > config\db.properties
echo db.user=root >> config\db.properties
echo db.password= >> config\db.properties
type config\db.properties

echo.
echo [5/5] Launching Student Academic and Course Management System...
echo   The Swing desktop window should appear.
echo   MySQL is running in the background (skip-grant-tables mode).
echo   Close this window to stop everything.
echo.
start "" /b java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main

echo Application launched!
echo.
echo ====================================================================
echo  System is running. Keep this window open.
echo ====================================================================
timeout /t 5 >nul

:End
pause
