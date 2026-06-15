$ErrorActionPreference = "Stop"

$names = podman exec drink-store-mysql mysql -uroot --default-character-set=utf8mb4 -N -e `
    "SELECT name FROM brewpoint_pos.products ORDER BY product_id LIMIT 5;"
$snapshot = podman exec drink-store-mysql mysql -uroot --default-character-set=utf8mb4 -N -e `
    "SELECT product_name_snapshot FROM brewpoint_pos.order_items LIMIT 3;"
$orderCount = podman exec drink-store-mysql mysql -uroot --default-character-set=utf8mb4 -N -e `
    "SELECT COUNT(*) FROM brewpoint_pos.orders;"

Write-Host "products.name:"
Write-Host $names
Write-Host ""
Write-Host "order_items.product_name_snapshot:"
Write-Host $snapshot
Write-Host ""
Write-Host "orders count: $orderCount"

$combined = ($names + " " + $snapshot)
if ($combined.Contains("?")) {
    Write-Host ""
    Write-Host "LOI: Du lieu co dau ? - UTF-8 bi hong. Chay lai .\scripts\reset-db-podman.ps1" -ForegroundColor Red
    exit 1
}

if ([int]$orderCount -lt 100) {
    Write-Host ""
    Write-Host "CANH BAO: It hon 100 don - seed chua import. Chay lai .\scripts\reset-db-podman.ps1" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "UTF-8 OK - tieng Viet trong DB dung." -ForegroundColor Green
