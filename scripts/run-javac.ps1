$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

& (Join-Path $PSScriptRoot "start-mysql.ps1")
& (Join-Path $PSScriptRoot "ensure-mysql-connector.ps1")
& (Join-Path $PSScriptRoot "compile-javac.ps1")

$javaw = Join-Path $env:JAVA_HOME "bin\javaw.exe"
if (-not (Test-Path $javaw)) {
    $javaw = "javaw.exe"
}

$classpath = "out;lib\mysql-connector-j-9.2.0.jar"
Start-Process -FilePath $javaw -ArgumentList @("-cp", $classpath, "com.drinkstore.main.App") -WorkingDirectory $root
Write-Host "Started Drink Store Manager."
