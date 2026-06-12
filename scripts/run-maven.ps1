$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

mvn clean package
java -jar "target/brewpoint-pos-1.0.0.jar"
