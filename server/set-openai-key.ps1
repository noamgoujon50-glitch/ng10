param(
    [switch]$Restart
)

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "Configuration de la vraie IA VALHALLA RAGE"
Write-Host ""
Write-Host "La cle sera enregistree dans les variables utilisateur Windows."
Write-Host "Elle ne sera pas mise dans l'APK Android."
Write-Host ""

$secureKey = Read-Host "Colle ta cle OpenAI API puis appuie sur Entree" -AsSecureString
$plainKey = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureKey)
)

if (-not $plainKey -or $plainKey.Trim().Length -lt 20) {
    throw "Cle OpenAI trop courte ou vide."
}

[Environment]::SetEnvironmentVariable("OPENAI_API_KEY", $plainKey.Trim(), "User")
[Environment]::SetEnvironmentVariable("OPENAI_MODEL", "gpt-5", "User")
[Environment]::SetEnvironmentVariable("OPENAI_MODERATION_MODEL", "omni-moderation-latest", "User")

$env:OPENAI_API_KEY = $plainKey.Trim()
$env:OPENAI_MODEL = "gpt-5"
$env:OPENAI_MODERATION_MODEL = "omni-moderation-latest"

Write-Host ""
Write-Host "Cle OpenAI enregistree."
Write-Host "Modele coach : $env:OPENAI_MODEL"
Write-Host "Modele moderation : $env:OPENAI_MODERATION_MODEL"
Write-Host ""

if ($Restart) {
    Write-Host "Redemarrage du serveur..."
    & "$PSScriptRoot\restart-valhalla-server.ps1"
} else {
    Write-Host "Prochaine etape :"
    Write-Host ".\restart-valhalla-server.ps1"
    Write-Host ".\test-openai-ia.ps1"
}
