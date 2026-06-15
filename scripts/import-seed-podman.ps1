$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$seed = Join-Path $root "database\seed_demo_orders.sql"
if (-not (Test-Path $seed)) {
    throw "Khong tim thay $seed"
}

# Khong pipe qua PowerShell — copy file vao container roi mysql < file (tranh loi UTF-8)
podman cp $seed drink-store-mysql:/tmp/seed_demo_orders.sql
if ($LASTEXITCODE -ne 0) {
    throw "podman cp seed that bai."
}

podman exec drink-store-mysql sh -c "mysql -uroot --default-character-set=utf8mb4 brewpoint_pos < /tmp/seed_demo_orders.sql"
if ($LASTEXITCODE -ne 0) {
    throw "Import seed_demo_orders.sql that bai."
}

Write-Host "Da import seed_demo_orders.sql (podman cp, utf8mb4)."
& (Join-Path $PSScriptRoot "verify-db-utf8.ps1")
