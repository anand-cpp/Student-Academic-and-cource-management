@echo off
setlocal enabledelayedexpansion
cd /d "C:\Users\anand\Student-Academic-and-cource-management"

set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
set MYSQLD_BIN="C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
set DATA_DIR="C:\ProgramData\MySQL\MySQL Server 8.4\Data"

echo ========================================
echo Starting MySQL (skip-grant-tables)
echo ========================================
start "MySQL" /b %MYSQLD_BIN% --datadir=%DATA_DIR% --port=3306 --skip-grant-tables --shared-memory --console

echo Waiting for MySQL...
timeout /t 8 /nobreak >nul

echo Testing connection...
for /L %%i in (1,1,15) do (
    %MYSQL_BIN% -u root -e "SELECT 1;" 2>nul
    if !errorlevel! equ 0 (
        echo MySQL is ready!
        goto Ready
    )
    echo   Attempt %%i/15...
    timeout /t 2 /nobreak >nul
)
echo ERROR: MySQL failed to start
goto End

:Ready
echo Running schema.sql...
%MYSQL_BIN% -u root student_academic_management < sql\schema.sql
if !errorlevel! neq 0 (
    echo WARNING: schema.sql errors (may already be applied)
) else (
    echo Schema applied successfully.
)

echo Verifying tables...
%MYSQL_BIN% -u root -e "SHOW TABLES FROM student_academic_management;" 2>&1

echo ========================================
echo Compiling Java application
echo ========================================
if not exist build mkdir build
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\util\*.java
if !errorlevel! neq 0 (
    echo ERROR: Compilation failed
    goto End
)
echo Compilation successful.

echo Configuring db.properties (no password - skip-grant-tables)...
if not exist config mkdir config
echo db.url=jdbc:mysql://localhost:3306/student_academic_management?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC > config\db.properties
echo db.user=root >> config\db.properties
echo db.password= >> config\db.properties

echo ========================================
echo Launching application
echo ========================================
start "MyApp" /b java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main

echo Application launched!
echo MySQL is running in the background with skip-grant-tables.
echo Close this window to stop everything.

timeout /t 5 >nul

:End
pause
