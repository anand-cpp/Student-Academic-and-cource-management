@echo off
setlocal enabledelayedexpansion
cd /d "C:\Users\anand\Student-Academic-and-cource-management"

set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
set MYSQLD_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
set MYSQLADMIN_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqladmin.exe"
set DATA_DIR="C:\ProgramData\MySQL\MySQL Server 8.4\Data"

echo ========================================
echo Step 1: Starting MySQL with skip-grant-tables
echo ========================================
start "MySQLTemp" /b %MYSQLD_BIN% --datadir=%DATA_DIR% --port=3306 --skip-grant-tables --shared-memory --console

echo Waiting for MySQL...
timeout /t 8 /nobreak >nul

echo Testing connection...
for /L %%i in (1,1,15) do (
    %MYSQL_BIN% -u root -e "SELECT 1;" 2>nul
    if !errorlevel! equ 0 (
        echo MySQL is ready!
        goto Step2
    )
    echo   Attempt %%i/15...
    timeout /t 2 /nobreak >nul
)
echo ERROR: MySQL failed to start
goto End

:Step2
echo Setting root password...
%MYSQL_BIN% -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;"

echo Checking database...
%MYSQL_BIN% -u root -proot -e "USE student_academic_management;" 2>nul
if !errorlevel! neq 0 (
    echo Creating database...
    %MYSQL_BIN% -u root -proot -e "CREATE DATABASE student_academic_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
) else (
    echo Database already exists.
)

echo Running schema.sql...
%MYSQL_BIN% -u root -proot student_academic_management < sql\schema.sql
if !errorlevel! neq 0 (
    echo WARNING: schema.sql errors (may already be applied)
) else (
    echo Schema applied successfully.
)

echo Stopping temporary MySQL...
%MYSQLADMIN_BIN% -u root -proot shutdown 2>nul
timeout /t 3 /nobreak >nul

echo ========================================
echo Step 2: Restarting MySQL normally
echo ========================================
start "MySQLNormal" /b %MYSQLD_BIN% --datadir=%DATA_DIR% --port=3306 --shared-memory --console

echo Waiting for MySQL to restart...
for /L %%i in (1,1,15) do (
    %MYSQL_BIN% -u root -proot -e "SELECT 1;" 2>nul
    if !errorlevel! equ 0 (
        echo MySQL is running normally!
        goto Step3
    )
    echo   Attempt %%i/15...
    timeout /t 2 /nobreak >nul
)
echo ERROR: MySQL failed to restart
goto End

:Step3
echo Verifying tables...
%MYSQL_BIN% -u root -proot -e "SHOW TABLES FROM student_academic_management;" 2>&1

echo ========================================
echo Step 3: Compiling Java application
echo ========================================
if not exist build mkdir build
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\util\*.java
if !errorlevel! neq 0 (
    echo ERROR: Compilation failed
    goto End
)
echo Compilation successful.

echo Configuring db.properties...
if not exist config mkdir config
echo db.url=jdbc:mysql://localhost:3306/student_academic_management?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC > config\db.properties
echo db.user=root >> config\db.properties
echo db.password=root >> config\db.properties

echo ========================================
echo Step 4: Launching application
echo ========================================
start "MyApp" java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main

:End
echo Done.
pause
