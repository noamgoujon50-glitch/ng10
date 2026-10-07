$ErrorActionPreference = "Stop"

if (-not $env:VALHALLA_SERVER_PORT) {
    $env:VALHALLA_SERVER_PORT = "8787"
}

$base = "http://127.0.0.1:$env:VALHALLA_SERVER_PORT"

Write-Host ""
Write-Host "Test IA VALHALLA RAGE"
Write-Host "Serveur : $base"
Write-Host ""

try {
    $health = Invoke-RestMethod -Method Get -Uri "$base/health" -TimeoutSec 5
} catch {
    Write-Host "Serveur non joignable."
    Write-Host "Lance d'abord : .\restart-valhalla-server.ps1"
    throw
}

Write-Host "Serveur : OK"
Write-Host "Modele : $($health.model)"
Write-Host "Cle IA active : $($health.keyConfigured)"

if (-not $health.keyConfigured) {
    Write-Host ""
    Write-Host "La cle OpenAI n'est pas encore visible par le serveur."
    Write-Host "Lance : .\set-openai-key.ps1 -Restart"
    exit 1
}

$payload = @{
    message = "Reponds en une phrase courte pour confirmer que la vraie IA est active."
    profile = @{
        userName = "Test"
        goal = "Prendre du muscle"
        level = "Debutant"
        frequency = "3 fois par semaine"
        place = "A la maison"
        equipment = "Poids du corps"
        sessionTime = "35 minutes"
        exercisePerformanceProfile = "Pompes|20||||"
    }
} | ConvertTo-Json -Depth 8

$coach = Invoke-RestMethod -Method Post -Uri "$base/coach" -ContentType "application/json" -Body $payload -TimeoutSec 45

Write-Host ""
Write-Host "Source coach : $($coach.source)"
Write-Host "Action : $($coach.action)"
Write-Host "Reponse : $($coach.reply)"

if ($coach.source -ne "openai") {
    Write-Host ""
    Write-Host "Attention : le serveur a repondu avec un secours local."
    if ($coach.serverNote) {
        Write-Host "Note serveur : $($coach.serverNote)"
    }
    exit 1
}

Write-Host ""
Write-Host "IA_REELLE_OK"
