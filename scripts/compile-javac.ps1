$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$files = Get-ChildItem -Recurse -Path "src/main/java" -Filter "*.java" | ForEach-Object { $_.FullName }
if (-not $files) {
    throw "No Java files found."
}

$javacCandidates = @()
if ($env:JAVA_HOME) {
    $javacCandidates += (Join-Path $env:JAVA_HOME "bin\javac.exe")
}
$javacCandidates += @(
    "javac.exe",
    "C:\Users\anvu5\.antigravity\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64\bin\javac.exe",
    "C:\Users\anvu5\Downloads\jdk-26_windows-x64_bin\jdk-26\bin\javac.exe"
)

$javac = $javacCandidates | Where-Object {
    if ($_ -eq "javac.exe") {
        $null -ne (Get-Command javac.exe -ErrorAction SilentlyContinue)
    } else {
        Test-Path $_
    }
} | Select-Object -First 1

if (-not $javac) {
    throw "javac.exe not found. Install JDK 17+ or set JAVA_HOME."
}

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}
New-Item -ItemType Directory -Force "out" | Out-Null

& $javac --release 17 -encoding UTF-8 -d "out" $files
Copy-Item -Recurse -Force "src/main/resources/*" "out"

Write-Host "Compiled to $root\out"
