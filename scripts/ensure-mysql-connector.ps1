$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$lib = Join-Path $root "lib"
$jar = Join-Path $lib "mysql-connector-j-9.2.0.jar"
$url = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar"

if (-not (Test-Path $lib)) {
    New-Item -ItemType Directory -Force $lib | Out-Null
}

if (-not (Test-Path $jar)) {
    Write-Host "Downloading MySQL Connector/J..."
    Invoke-WebRequest -Uri $url -OutFile $jar -UseBasicParsing
}

Write-Host $jar
