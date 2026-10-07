$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$gradleFile = Join-Path $root "app\build.gradle.kts"
$gradleText = Get-Content $gradleFile -Raw
$versionCode = [regex]::Match($gradleText, "versionCode\s*=\s*(\d+)").Groups[1].Value
$versionName = [regex]::Match($gradleText, "versionName\s*=\s*`"([^`"]+)`"").Groups[1].Value
$applicationId = [regex]::Match($gradleText, "applicationId\s*=\s*`"([^`"]+)`"").Groups[1].Value
$latestApk = Get-ChildItem (Join-Path $root "outputs") -Filter "VALHALLA-RAGE-v*-clean.apk" -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1
$gradle = Get-Command gradle -ErrorAction SilentlyContinue
$wrapper = Test-Path (Join-Path $root "gradlew.bat")

Write-Host "VALHALLA RAGE - Controle publication"
Write-Host ""
Write-Host "Package Android : $applicationId"
Write-Host "Version : $versionName ($versionCode)"
if ($latestApk) {
    Write-Host "Dernier APK : $($latestApk.FullName)"
} else {
    Write-Host "Dernier APK : aucun APK trouve"
}
Write-Host "Gradle installe : $([bool]$gradle)"
Write-Host "Gradle wrapper : $wrapper"
Write-Host "Cle OpenAI serveur : $([bool]$env:OPENAI_API_KEY)"
Write-Host ""
if (-not $gradle -and -not $wrapper) {
    Write-Host "Action requise : ouvrir Android Studio pour generer l'AAB ou installer Gradle/creer un wrapper."
}
Write-Host "Guide : docs\play-store\BUILD_AAB_GUIDE.md"
