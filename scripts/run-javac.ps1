$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

& (Join-Path $PSScriptRoot "start-mysql.ps1")
& (Join-Path $PSScriptRoot "ensure-mysql-connector.ps1")
& (Join-Path $PSScriptRoot "compile-javac.ps1")

$javaCandidates = @()
if ($env:JAVA_HOME) {
    $javaCandidates += (Join-Path $env:JAVA_HOME "bin\javaw.exe")
    $javaCandidates += (Join-Path $env:JAVA_HOME "bin\java.exe")
}
$javaCandidates += @(
    "javaw.exe",
    "java.exe",
    "C:\Users\anvu5\.antigravity\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64\bin\javaw.exe",
    "C:\Users\anvu5\.antigravity\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64\bin\java.exe",
    "C:\Users\anvu5\Downloads\jdk-26_windows-x64_bin\jdk-26\bin\javaw.exe",
    "C:\Users\anvu5\Downloads\jdk-26_windows-x64_bin\jdk-26\bin\java.exe"
)

$java = $javaCandidates | Where-Object {
    if ($_ -eq "javaw.exe" -or $_ -eq "java.exe") {
        $null -ne (Get-Command $_ -ErrorAction SilentlyContinue)
    } else {
        Test-Path $_
    }
} | Select-Object -First 1

if (-not $java) {
    throw "java.exe/javaw.exe not found. Install JDK 17+ or set JAVA_HOME."
}

$classpath = "out;lib\mysql-connector-j-9.2.0.jar"
Start-Process -FilePath $java -ArgumentList @("-cp", $classpath, "com.brewpoint.pos.main.App") -WorkingDirectory $root
Write-Host "Started BrewPoint POS."
