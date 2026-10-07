# VALHALLA RAGE - Generer le fichier Play Store

Le Play Store demande normalement un fichier Android App Bundle :

```text
.aab
```

L'APK actuel sert aux tests sur Pixel. Il ne suffit pas pour une publication propre sur Google Play.

## Etat actuel

- Package final : `com.valhallarage.app`
- Version actuelle : `0.47.0`
- APK de test : `outputs/VALHALLA-RAGE-v47-clean.apk`
- Le projet n'a pas encore de `gradlew`.
- Gradle n'est pas disponible en ligne de commande sur ce PC.

## Methode simple avec Android Studio

1. Ouvrir le dossier du projet dans Android Studio.
2. Attendre la synchronisation.
3. Menu `Build`.
4. Choisir `Generate Signed Bundle / APK`.
5. Choisir `Android App Bundle`.
6. Creer une cle de signature release si elle n'existe pas encore.
7. Garder cette cle precieusement : elle servira pour toutes les futures mises a jour.
8. Generer le fichier `.aab`.

## Important

- Ne jamais perdre la cle de signature release.
- Ne jamais mettre la cle OpenAI dans l'application Android.
- Le serveur de production devra etre en HTTPS avant publication.
- Les achats de pieces devront passer par Google Play Billing avant une vraie publication.

## Ce que je peux encore preparer

- Ajouter un `gradle wrapper` quand Gradle sera disponible.
- Ajouter une configuration release signee quand la cle Play Store sera creee.
- Generer l'AAB quand l'environnement Gradle complet sera accessible.
