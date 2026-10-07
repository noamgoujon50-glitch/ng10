$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$root = $root.Path
$sdk = Resolve-Path (Join-Path $root "..\..\android-sdk")
$buildGradle = Join-Path $root "app\build.gradle.kts"
$gradleText = Get-Content $buildGradle -Raw

$versionCode = [regex]::Match($gradleText, "versionCode\s*=\s*(\d+)").Groups[1].Value
$versionName = [regex]::Match($gradleText, "versionName\s*=\s*`"([^`"]+)`"").Groups[1].Value
if (-not $versionCode -or -not $versionName) {
    throw "Impossible de lire versionCode/versionName dans app\build.gradle.kts"
}

$buildTools = Join-Path $sdk "build-tools\36.0.0"
$androidJar = Join-Path $sdk "platforms\android-36\android.jar"
$jbrRoot = "C:\Program Files\Android\Android Studio\jbr"
$jbrBin = Join-Path $jbrRoot "bin"
$env:JAVA_HOME = $jbrRoot
$env:Path = "$jbrBin;$env:Path"

$java = Join-Path $jbrBin "java.exe"
$javac = Join-Path $jbrBin "javac.exe"
$jar = Join-Path $jbrBin "jar.exe"
$r8 = Join-Path $sdk "cmdline-tools\latest\lib\r8.jar"

$stamp = Get-Date -Format "yyyyMMddHHmmss"
$build = Join-Path $root "build\manual-debug-v$versionCode-$stamp"
$outDir = Join-Path $root "outputs"
New-Item -ItemType Directory -Force -Path $build, $outDir | Out-Null

$generated = Join-Path $build "generated"
$classes = Join-Path $build "classes"
$dex = Join-Path $build "dex"
New-Item -ItemType Directory -Force -Path $generated, $classes, $dex | Out-Null

$resZip = Join-Path $build "resources.zip"
$linked = Join-Path $build "linked.apk"
$unsigned = Join-Path $build "VALHALLA-RAGE-v$versionCode-unsigned.apk"
$aligned = Join-Path $build "VALHALLA-RAGE-v$versionCode-aligned.apk"
$final = Join-Path $outDir "VALHALLA-RAGE-v$versionCode-clean.apk"

& (Join-Path $buildTools "aapt2.exe") compile --dir (Join-Path $root "app\src\main\res") -o $resZip
& (Join-Path $buildTools "aapt2.exe") link -o $linked -I $androidJar --manifest (Join-Path $root "app\src\main\AndroidManifest.xml") --java $generated --auto-add-overlay --rename-manifest-package com.valhallarage.app --version-code $versionCode --version-name $versionName $resZip
& $javac -encoding UTF-8 -source 17 -target 17 -classpath $androidJar -d $classes (Join-Path $generated "com\fitai\app\R.java") (Join-Path $root "app\src\main\java\com\fitai\app\MainActivity.java")
& $jar cf (Join-Path $build "classes.jar") -C $classes .
& $java -cp $r8 com.android.tools.r8.D8 --release --min-api 26 --lib $androidJar --output $dex (Join-Path $build "classes.jar")

Copy-Item -LiteralPath $linked -Destination $unsigned -Force
Push-Location $dex
& (Join-Path $buildTools "aapt.exe") add $unsigned classes.dex | Out-Host
Pop-Location

& (Join-Path $buildTools "zipalign.exe") -f -p 4 $unsigned $aligned
& (Join-Path $buildTools "apksigner.bat") sign --ks "$env:USERPROFILE\.android\debug.keystore" --ks-pass pass:android --key-pass pass:android --out $final $aligned
& (Join-Path $buildTools "apksigner.bat") verify --print-certs $final | Out-Host

Write-Output "APK=$final"
