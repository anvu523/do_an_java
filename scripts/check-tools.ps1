$ErrorActionPreference = "Continue"

Write-Host "== PATH tools =="
foreach ($tool in @("java", "javac", "mvn", "mysql")) {
    $found = Get-Command $tool -ErrorAction SilentlyContinue
    if ($found) {
        Write-Host "$tool => $($found.Source)"
    } else {
        Write-Host "$tool => NOT FOUND"
    }
}

Write-Host "`n== Services =="
Get-Service | Where-Object {
    $_.Name -match "mysql|maria" -or $_.DisplayName -match "mysql|maria"
} | Select-Object Name, DisplayName, Status, StartType | Format-Table -AutoSize

Write-Host "`n== Common install folders =="
$paths = @(
    "C:\Program Files\MySQL",
    "C:\Program Files (x86)\MySQL",
    "C:\xampp",
    "C:\laragon",
    "C:\wamp64",
    "C:\ProgramData\chocolatey",
    "C:\tools",
    "C:\apache-maven",
    "C:\maven",
    "$env:USERPROFILE\scoop"
)
foreach ($path in $paths) {
    if (Test-Path $path) {
        Write-Host "FOUND $path"
    }
}

Write-Host "`n== MySQL binaries =="
Get-ChildItem -Path "C:\Program Files\MySQL", "C:\Program Files (x86)\MySQL" -Recurse -Include mysql.exe, mysqld.exe, mysqladmin.exe -File -ErrorAction SilentlyContinue |
    Select-Object FullName, Length |
    Format-Table -AutoSize
