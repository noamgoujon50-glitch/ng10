# Outils VALHALLA RAGE

## Construire le dernier APK de test

```powershell
.\tools\build-valhalla-debug-apk.ps1
```

Le script lit `versionCode` et `versionName` dans `app\build.gradle.kts`, puis cree :

```text
outputs\VALHALLA-RAGE-vXX-clean.apk
```

## Envoyer le dernier APK au Pixel

```powershell
.\tools\send-latest-apk-to-pixel.ps1
```

Le Pixel doit etre branche en USB, deverrouille, et le mode transfert de fichiers doit etre actif.

## Verifier la preparation Play Store

```powershell
.\tools\check-play-store-readiness.ps1
```

Ce controle affiche la version actuelle, le dernier APK, l'identifiant Android et ce qu'il manque pour generer le fichier `.aab`.

## Envoyer le projet vers GitHub

```powershell
.\tools\PUSH-TO-GITHUB.cmd
```

Le script demande l'adresse HTTPS d'un depot GitHub vide, puis envoie le projet dessus pour que Render puisse le deployer.
