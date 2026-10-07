$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$root = $root.Path
$apk = Get-ChildItem (Join-Path $root "outputs") -Filter "VALHALLA-RAGE-v*-clean.apk" |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $apk) {
    throw "Aucun APK VALHALLA RAGE trouve dans outputs."
}

$shell = New-Object -ComObject Shell.Application
$computer = $shell.Namespace(17)
$phone = $computer.Items() | Where-Object { $_.Name -like "Pixel*" } | Select-Object -First 1
if (-not $phone) {
    throw "Pixel introuvable en USB/MTP."
}

$phoneFolder = $phone.GetFolder
$storage = $phoneFolder.Items() | Where-Object {
    $_.Name -like "*stockage*" -or $_.Name -like "*Storage*" -or $_.Name -like "*interne*"
} | Select-Object -First 1
if (-not $storage) {
    throw "Stockage interne du Pixel introuvable."
}

$storageFolder = $storage.GetFolder
$download = $storageFolder.Items() | Where-Object {
    $_.Name -eq "Download" -or $_.Name -eq "Telechargements" -or $_.Name -eq "Téléchargements"
} | Select-Object -First 1
if (-not $download) {
    throw "Dossier Download du Pixel introuvable."
}

$download.GetFolder.CopyHere($apk.FullName, 16)
Start-Sleep -Seconds 5
Write-Output "TRANSFER_OK=$($apk.Name) DEST=$($phone.Name)/$($download.Name)"
