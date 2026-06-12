$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $false

$mysqlBaseCandidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.4",
    "C:\Program Files\MySQL\MySQL Server 8.0",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.4",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0"
)

$mysqlBase = $mysqlBaseCandidates | Where-Object { Test-Path (Join-Path $_ "bin\mysqld.exe") } | Select-Object -First 1
if (-not $mysqlBase) {
    throw "MySQL Server not found. Install with: winget install --id Oracle.MySQL -e --source winget --accept-package-agreements --accept-source-agreements"
}

$bin = Join-Path $mysqlBase "bin"
$mysqld = Join-Path $bin "mysqld.exe"
$mysqladmin = Join-Path $bin "mysqladmin.exe"
$data = Join-Path $env:USERPROFILE "mysql-8.4-data"
$ini = Join-Path $env:USERPROFILE "mysql-8.4-my.ini"

if (-not (Test-Path $data)) {
    New-Item -ItemType Directory -Force $data | Out-Null
}

if (-not (Get-ChildItem -LiteralPath $data -Force -ErrorAction SilentlyContinue)) {
    & $mysqld --initialize-insecure "--basedir=$mysqlBase" "--datadir=$data" --console
}

$baseUnix = $mysqlBase.Replace("\", "/")
$dataUnix = $data.Replace("\", "/")
@"
[mysqld]
basedir=$baseUnix
datadir=$dataUnix
port=3306
bind-address=127.0.0.1
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

[client]
port=3306
default-character-set=utf8mb4
"@ | Set-Content -LiteralPath $ini -Encoding ASCII

$listening = netstat -ano | Select-String "127.0.0.1:3306"
if (-not $listening) {
    $process = Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=$ini" -WindowStyle Hidden -PassThru
    Write-Host "Started mysqld PID=$($process.Id)"
}

$alive = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 1
    & $mysqladmin --protocol=tcp --host=127.0.0.1 --port=3306 --user=root ping 2>$null
    if ($LASTEXITCODE -eq 0) {
        $alive = $true
        break
    }
}

if (-not $alive) {
    throw "MySQL did not respond at 127.0.0.1:3306."
}

& $mysqladmin --protocol=tcp --host=127.0.0.1 --port=3306 --user=root version
