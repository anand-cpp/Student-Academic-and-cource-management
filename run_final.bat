@echo off
setlocal enabledelayedexpansion
cd /d "C:\Users\anand\Student-Academic-and-cource-management"

set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
set MYSQLD_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
set DATA_DIR="C:\ProgramData\MySQL\MySQL Server 8.4\Data"

echo ========================================
echo Starting MySQL with skip-grant-tables
echo ========================================
start "MySQL" /b %MYSQLD_BIN% --datadir=%DATA_DIR% --port=3306 --skip-grant-tables --shared-memory --console

echo Waiting for MySQL to be ready...
set READY=0
for /L %%i in (1,1,20) do (
    %MYSQL_BIN% -u root -e "SELECT 1;" 2>nul
    if !errorlevel! equ 0 (
        set READY=1
        goto MySQLReady
    )
    echo   Waiting... (%%i/20)
    timeout /t 2 /nobreak >nul
)
echo ERROR: MySQL failed to start
goto End

:MySQLReady
echo MySQL is running!

echo Checking/creating database...
%MYSQL_BIN% -u root -e "CREATE DATABASE IF NOT EXISTS student_academic_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

echo Running schema.sql...
%MYSQL_BIN% -u root student_academic_management < sql\schema.sql
if !errorlevel! neq 0 (
    echo WARNING: schema.sql may have had errors (tables may already exist)
) else (
    echo Schema applied successfully.
)

echo Tables in database:
%MYSQL_BIN% -u root -e "SHOW TABLES FROM student_academic_management;" 2>&1

echo ========================================
echo Compiling Java application
echo ========================================
if not exist build mkdir build
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\ui\components\*.java src\util\*.java
if !errorlevel! neq 0 (
    echo ERROR: Compilation failed
    goto End
)
echo Compilation successful.

echo Configuring db.properties...
if not exist config mkdir config
echo db.url=jdbc:mysql://localhost:3306/student_academic_management?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC > config\db.properties
echo db.user=root >> config\db.properties
echo db.password= >> config\db.properties

echo ========================================
echo Launching Student Academic ^& Course Management System
echo ========================================
start "MyApp" /b java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main

echo Application launched successfully!
echo MySQL is running in background with skip-grant-tables.
echo The app window should appear.
echo Close this window to stop everything.
timeout /t 5 >nul

:End
pause
