@echo off
cd /d "C:\Users\anand\Student-Academic-and-cource-management"

echo "========================================"
echo "Starting MySQL with skip-grant-tables..."
echo "========================================"
start "MySQLSetup" /b "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --datadir="C:/ProgramData/MySQL/MySQL Server 8.4/Data" --port=3306 --skip-grant-tables --console

echo "Waiting for MySQL to start..."
timeout /t 8 /nobreak >nul

echo "Checking MySQL connectivity..."
for /L %%i in (1,1,10) do (
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -e "SELECT 1;" 2>nul && goto mysql_ready
    echo "  Attempt %%i/10 - waiting..."
    timeout /t 2 /nobreak >nul
)
echo "ERROR: MySQL did not become ready"
goto end

:mysql_ready
echo "MySQL is ready!"

echo "Setting root password..."
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;"

echo "Checking if student_academic_management database exists..."
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -proot -e "USE student_academic_management;" 2>nul
if %errorlevel% neq 0 (
    echo "Creating database student_academic_management..."
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -proot -e "CREATE DATABASE student_academic_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
) else (
    echo "Database already exists."
)

echo "Running schema.sql..."
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -proot student_academic_management < sql/schema.sql
if %errorlevel% neq 0 (
    echo "WARNING: schema.sql had errors (may already be applied)"
) else (
    echo "Schema applied successfully."
)

echo "Stopping temporary MySQL instance..."
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqladmin.exe" -u root -proot shutdown 2>nul
timeout /t 3 /nobreak >nul

echo "========================================"
echo "Restarting MySQL normally (with password)..."
echo "========================================"
start "MySQLNormal" /b "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --datadir="C:/ProgramData/MySQL/MySQL Server 8.4/Data" --port=3306 --console

echo "Waiting for MySQL to restart..."
timeout /t 8 /nobreak >nul

echo "Verifying MySQL is running..."
for /L %%i in (1,1,10) do (
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -proot -e "SELECT 1;" 2>nul && goto mysql_ready2
    echo "  Attempt %%i/10 - waiting..."
    timeout /t 2 /nobreak >nul
)
echo "ERROR: MySQL did not restart properly"
goto end

:mysql_ready2
echo "MySQL is running normally!"

echo "========================================"
echo "Compiling Java application..."
echo "========================================"
if not exist build mkdir build
javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" src\app\*.java src\model\*.java src\dao\*.java src\service\*.java src\db\*.java src\ui\*.java src\util\*.java
if %errorlevel% neq 0 (
    echo "ERROR: Compilation failed"
    goto end
)
echo "Compilation successful."

echo "========================================"
echo "Running Student Academic & Course Management System..."
echo "========================================"
java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main

:end
echo "Done."
pause
