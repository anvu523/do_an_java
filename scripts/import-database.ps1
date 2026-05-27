$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

& (Join-Path $PSScriptRoot "start-mysql.ps1")

$mysqlCandidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
)
$mysql = $mysqlCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $mysql) {
    throw "mysql.exe not found."
}

$sql = Join-Path $root "database\drink_store.sql"
$cmd = '"' + $mysql + '" --protocol=tcp --host=127.0.0.1 --port=3306 --user=root --default-character-set=utf8mb4 < "' + $sql + '"'
cmd.exe /c $cmd
if ($LASTEXITCODE -ne 0) {
    throw "SQL import failed."
}

& $mysql --protocol=tcp --host=127.0.0.1 --port=3306 --user=root --default-character-set=utf8mb4 --database=drink_store --execute="SHOW TABLES; SELECT 'users' AS table_name, COUNT(*) AS rows_count FROM users UNION ALL SELECT 'employees', COUNT(*) FROM employees UNION ALL SELECT 'categories', COUNT(*) FROM categories UNION ALL SELECT 'products', COUNT(*) FROM products UNION ALL SELECT 'orders', COUNT(*) FROM orders UNION ALL SELECT 'order_details', COUNT(*) FROM order_details;"
