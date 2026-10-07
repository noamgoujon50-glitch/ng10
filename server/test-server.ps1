$ErrorActionPreference = "Stop"

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
Write-Host "Node: $node"
& $node "$PSScriptRoot\test-server.mjs"
