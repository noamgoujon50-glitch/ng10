$ErrorActionPreference = "Stop"

if (-not $env:VALHALLA_SERVER_PORT) {
    $env:VALHALLA_SERVER_PORT = "8787"
}

if (-not $env:OPENAI_API_KEY) {
    $userOpenAiKey = [Environment]::GetEnvironmentVariable("OPENAI_API_KEY", "User")
    if ($userOpenAiKey) {
        $env:OPENAI_API_KEY = $userOpenAiKey
    }
}

if (-not $env:OPENAI_MODEL) {
    $userOpenAiModel = [Environment]::GetEnvironmentVariable("OPENAI_MODEL", "User")
    if ($userOpenAiModel) {
        $env:OPENAI_MODEL = $userOpenAiModel
    }
}

if (-not $env:OPENAI_MODERATION_MODEL) {
    $userModerationModel = [Environment]::GetEnvironmentVariable("OPENAI_MODERATION_MODEL", "User")
    if ($userModerationModel) {
        $env:OPENAI_MODERATION_MODEL = $userModerationModel
    }
}

function Resolve-ValhallaNode {
    $command = Get-Command node -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    $candidates = @(
        "$env:USERPROFILE\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe",
        "$env:ProgramFiles\nodejs\node.exe",
        "${env:ProgramFiles(x86)}\nodejs\node.exe"
    )

    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path -LiteralPath $candidate)) {
            return $candidate
        }
    }

    throw "Node.js est introuvable. Installe Node.js ou relance depuis Codex."
}

$node = Resolve-ValhallaNode

Write-Host ""
Write-Host "VALHALLA RAGE Server"
Write-Host "Port: $env:VALHALLA_SERVER_PORT"
Write-Host "Node: $node"
Write-Host ""

$existing = Get-NetTCPConnection -LocalPort $env:VALHALLA_SERVER_PORT -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($existing) {
    Write-Host "Un serveur utilise deja ce port."
    Write-Host "PID: $($existing.OwningProcess)"
    Write-Host "Adresse locale a tester dans l'application : http://127.0.0.1:$env:VALHALLA_SERVER_PORT"
    Write-Host "Depuis le Pixel, utilise l'adresse Wi-Fi du PC affichee plus bas si elle est disponible."
    Write-Host ""
    Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
        Where-Object { $_.IPAddress -notlike "127.*" -and $_.PrefixOrigin -ne "WellKnown" } |
        ForEach-Object { Write-Host "Adresse possible Pixel: http://$($_.IPAddress):$env:VALHALLA_SERVER_PORT" }
    Write-Host ""
    Write-Host "Pour relancer proprement avec la nouvelle version :"
    Write-Host ".\restart-valhalla-server.ps1"
    return
}

Write-Host "Adresses a utiliser :"
Write-Host "PC local: http://127.0.0.1:$env:VALHALLA_SERVER_PORT"
Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notlike "127.*" -and $_.PrefixOrigin -ne "WellKnown" } |
    ForEach-Object { Write-Host "Pixel Wi-Fi: http://$($_.IPAddress):$env:VALHALLA_SERVER_PORT" }
Write-Host ""

Write-Host "Si tu veux activer la vraie IA, lance avant :"
Write-Host '$env:OPENAI_API_KEY="ta_cle_api_openai"'
Write-Host ""

& $node "$PSScriptRoot\server.mjs"
