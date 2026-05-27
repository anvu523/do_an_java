$ErrorActionPreference = "Stop"

$mysqladminCandidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqladmin.exe",
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqladmin.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.4\bin\mysqladmin.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysqladmin.exe"
)
$mysqladmin = $mysqladminCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $mysqladmin) {
    throw "mysqladmin.exe not found."
}

& $mysqladmin --protocol=tcp --host=127.0.0.1 --port=3306 --user=root shutdown
Write-Host "MySQL shutdown requested."
