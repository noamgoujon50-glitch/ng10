$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$safePath = ($projectRoot.Path -replace "\\", "/")

Write-Host ""
Write-Host "VALHALLA RAGE - Envoi vers GitHub"
Write-Host "----------------------------------"
Write-Host "Avant de continuer, cree un depot GitHub vide, puis copie son adresse HTTPS."
Write-Host "Exemple : https://github.com/ton-compte/valhalla-rage.git"
Write-Host ""

$repoUrl = Read-Host "Colle ici l'adresse HTTPS du depot GitHub"
$repoUrl = $repoUrl.Trim()

if (-not $repoUrl) {
    throw "Adresse GitHub manquante."
}

if ($repoUrl -notmatch "^https://github\.com/.+/.+\.git$") {
    Write-Host "Attention : l'adresse ne ressemble pas a une URL GitHub HTTPS classique."
}

function Invoke-Git {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    & git -c "safe.directory=$safePath" -C $projectRoot.Path @Args
}

$currentBranch = (Invoke-Git branch --show-current).Trim()
if ($currentBranch -ne "main") {
    Invoke-Git branch -M main
}

$origin = ""
try {
    $origin = (Invoke-Git remote get-url origin).Trim()
} catch {
    $origin = ""
}

if ($origin) {
    Invoke-Git remote set-url origin $repoUrl
} else {
    Invoke-Git remote add origin $repoUrl
}

Write-Host ""
Write-Host "Envoi du projet vers GitHub..."
Invoke-Git push -u origin main

Write-Host ""
Write-Host "OK : le projet a ete envoye sur GitHub."
Write-Host "Tu peux maintenant connecter ce depot dans Render avec le fichier render.yaml."

