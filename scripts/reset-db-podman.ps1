$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host "1/3 Dung container va xoa volume MySQL..."
podman compose down -v
if ($LASTEXITCODE -ne 0) {
    throw "podman compose down -v that bai."
}

Write-Host "2/3 Khoi dong MySQL (tu import drink_store + seed, doi 1-3 phut)..."
podman compose up -d
if ($LASTEXITCODE -ne 0) {
    throw "podman compose up -d that bai."
}

Write-Host "Cho MySQL healthy..."
$healthy = $false
for ($i = 0; $i -lt 40; $i++) {
    $status = podman inspect drink-store-mysql --format "{{.State.Health.Status}}" 2>$null
    if ($status -eq "healthy") {
        $healthy = $true
        break
    }
    Start-Sleep -Seconds 3
}
if (-not $healthy) {
    throw "MySQL chua healthy sau 2 phut. Xem: podman logs drink-store-mysql"
}

Write-Host "3/3 Kiem tra UTF-8..."
& (Join-Path $PSScriptRoot "verify-db-utf8.ps1")
