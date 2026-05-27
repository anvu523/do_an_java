$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$files = Get-ChildItem -Recurse -Path "src/main/java" -Filter "*.java" | ForEach-Object { $_.FullName }
if (-not $files) {
    throw "No Java files found."
}

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}
New-Item -ItemType Directory -Force "out" | Out-Null

javac -encoding UTF-8 -d "out" $files
Copy-Item -Recurse -Force "src/main/resources/*" "out"

Write-Host "Compiled to $root\out"
