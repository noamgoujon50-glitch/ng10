# VALHALLA RAGE - Checklist Play Store

Derniere mise a jour : 2026-10-06

## Etat actuel

- Nom public : VALHALLA RAGE
- Package Android de test propre : `com.valhallarage.app`
- Version de test la plus recente envoyee au Pixel : `0.47.0`
- Serveur local de test : `server/server.mjs`
- APK de test : `outputs/VALHALLA-RAGE-v47-clean.apk`
- Vraie IA : scripts prets, activation a verifier avec `server/test-openai-ia.ps1`
- Hebergement serveur : Dockerfile, `.env.example`, `render.yaml` et guide Render prepares, serveur compatible avec `PORT`

## Avant publication

1. Creer le compte Google Play Console.
2. Choisir le package final, idealement `com.valhallarage.app`, et ne plus le changer.
3. Creer une version release signee en Android App Bundle `.aab`, pas seulement un APK de test.
4. Activer Play App Signing.
5. Remplacer le serveur local PC par un serveur heberge en ligne avec HTTPS.
6. Ajouter une vraie base de donnees serveur.
7. Verifier la vraie IA cote serveur avec `server/set-openai-key.ps1 -Restart`, jamais dans l'application Android.
8. Brancher Google Play Billing pour les pieces payantes.
9. Brancher une vraie verification/moderation video et photo cote serveur.
10. Publier une politique de confidentialite accessible par URL publique.
11. Remplir la section Data safety dans Play Console.
12. Faire le questionnaire de classification du contenu.
13. Faire un test ferme si Google le demande.

## Points Play Store importants

- Pour les nouveaux comptes personnels crees apres le 13 novembre 2023, Google indique qu'un test ferme avec au moins 12 testeurs inscrits pendant 14 jours consecutifs peut etre requis avant l'acces production.
- La section Data safety doit etre completee dans Play Console.
- Une politique de confidentialite doit etre coherente avec les donnees declarees.
- Les achats de pieces dans l'application devront utiliser Google Play Billing.

Sources utiles :

- Tests fermes Google Play : https://support.google.com/googleplay/android-developer/answer/14151465
- Configuration des tests : https://support.google.com/googleplay/android-developer/answer/9845334
- Data safety : https://support.google.com/googleplay/android-developer/answer/10787469
