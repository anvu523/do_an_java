$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

mvn clean package
java -jar "target/drink-store-manager-1.0.0.jar"
