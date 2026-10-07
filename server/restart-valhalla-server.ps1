$ErrorActionPreference = "Stop"

if (-not $env:VALHALLA_SERVER_PORT) {
    $env:VALHALLA_SERVER_PORT = "8787"
}

Write-Host ""
Write-Host "Redemarrage du serveur VALHALLA RAGE"
Write-Host "Port: $env:VALHALLA_SERVER_PORT"
Write-Host ""

$connections = Get-NetTCPConnection -LocalPort $env:VALHALLA_SERVER_PORT -State Listen -ErrorAction SilentlyContinue
$pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique

foreach ($processId in $pids) {
    if ($processId -and $processId -gt 0) {
        Write-Host "Arret de l'ancien serveur PID $processId"
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    }
}

Start-Sleep -Milliseconds 500
& "$PSScriptRoot\start-valhalla-server.ps1"
